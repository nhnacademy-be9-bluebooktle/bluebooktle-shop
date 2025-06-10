package shop.bluebooktle.backend.payment.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.order.entity.Order;
import shop.bluebooktle.backend.order.entity.OrderState;
import shop.bluebooktle.backend.order.repository.OrderRepository;
import shop.bluebooktle.backend.order.repository.OrderStateRepository;
import shop.bluebooktle.backend.payment.dto.response.GenericPaymentConfirmResponse;
import shop.bluebooktle.backend.payment.dto.response.GenericPaymentConfirmResponse.PaymentStatus;
import shop.bluebooktle.backend.payment.entity.Payment;
import shop.bluebooktle.backend.payment.entity.PaymentDetail;
import shop.bluebooktle.backend.payment.entity.PaymentType;
import shop.bluebooktle.backend.payment.event.type.PaymentPointEarnEvent;
import shop.bluebooktle.backend.payment.gateway.PaymentGateway;
import shop.bluebooktle.backend.payment.repository.PaymentDetailRepository;
import shop.bluebooktle.backend.payment.repository.PaymentRepository;
import shop.bluebooktle.backend.payment.repository.PaymentTypeRepository;
import shop.bluebooktle.backend.payment.service.PaymentService;
import shop.bluebooktle.common.domain.order.OrderStatus;
import shop.bluebooktle.common.dto.payment.request.PaymentConfirmRequest;
import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;
import shop.bluebooktle.common.exception.order.OrderNotFoundException;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

	private final PaymentRepository paymentRepository;
	private final OrderRepository orderRepository;
	private final PaymentDetailRepository paymentDetailRepository;
	private final PaymentTypeRepository paymentTypeRepository;
	private final OrderStateRepository orderStateRepository;
	private final List<PaymentGateway> gatewayList;
	private final ApplicationEventPublisher eventPublisher;

	private Map<String, PaymentGateway> paymentGateways;

	@PostConstruct
	public void initializeGateways() {
		this.paymentGateways = gatewayList.stream()
			.collect(Collectors.toMap(pg -> pg.getGatewayName().toUpperCase(), Function.identity()));
	}

	@Override
	@Transactional
	public void confirmPayment(PaymentConfirmRequest request, String gatewayName) {
		Order order = orderRepository.findByOrderKey(request.orderId()).orElseThrow(OrderNotFoundException::new);

		if (!request.orderId().equals(order.getOrderKey())) {
			throw new ApplicationException(ErrorCode.ORDER_INVALID_ORDER_KEY, "요청된 주문 ID와 실제 주문 정보가 일치하지 않습니다.");
		}

		BigDecimal expectedAmount = getExpectedAmount(order);
		BigDecimal requestedAmount = BigDecimal.valueOf(request.amount());

		log.info("== Expected Amount {}", expectedAmount);
		log.info("== Requested Amount {}", requestedAmount);

		if (requestedAmount.compareTo(expectedAmount) != 0) {
			throw new ApplicationException(ErrorCode.PAYMENT_AMOUNT_MISMATCH);
		}

		if (paymentGateways == null || paymentGateways.isEmpty()) {
			throw new ApplicationException(ErrorCode.INTERNAL_SERVER_ERROR, "결제 서비스가 올바르게 초기화되지 않았습니다.");
		}

		PaymentGateway selectedGateway = paymentGateways.get(gatewayName.toUpperCase());
		if (selectedGateway == null) {
			throw new ApplicationException(ErrorCode.INVALID_INPUT_VALUE, "지원하지 않는 결제 서비스입니다: " + gatewayName);
		}

		OrderState pendingOrderState = orderStateRepository.findByState(OrderStatus.PENDING)
			.orElseThrow(() -> new ApplicationException(ErrorCode.INTERNAL_SERVER_ERROR, "PENDING 상태를 찾을 수 없습니다."));
		order.changeOrderState(pendingOrderState);

		GenericPaymentConfirmResponse gatewayResponse = selectedGateway.confirmPayment(request);

		if (gatewayResponse.status() == PaymentStatus.SUCCESS || gatewayResponse.status() == PaymentStatus.PENDING) {
			String paymentMethodName = gatewayName.toUpperCase() + "_" + gatewayResponse.paymentMethodDetail();
			PaymentType paymentType = paymentTypeRepository.findByMethod(paymentMethodName)
				.orElseGet(() -> paymentTypeRepository.save(PaymentType.builder().method(paymentMethodName).build()));

			PaymentDetail paymentDetail = PaymentDetail.builder()
				.paymentType(paymentType)
				.key(gatewayResponse.transactionId())
				.build();
			paymentDetailRepository.save(paymentDetail);

			Payment payment = Payment.builder()
				.order(order)
				.paymentDetail(paymentDetail)
				.paidAmount(gatewayResponse.confirmedAmount())
				.build();
			paymentRepository.save(payment);
			OrderState preparingOrderState = orderStateRepository.findByState(OrderStatus.PREPARING)
				.orElseThrow(
					() -> new ApplicationException(ErrorCode.INTERNAL_SERVER_ERROR, "PREPARING 상태를 찾을 수 없습니다."));
			order.changeOrderState(preparingOrderState);
		} else {
			String failReason = "결제 실패";
			if (gatewayResponse.additionalData() != null
				&& gatewayResponse.additionalData().get("errorMessage") != null) {
				failReason = gatewayResponse.additionalData().get("errorMessage").toString();
			} else if (gatewayResponse.additionalData() != null
				&& gatewayResponse.additionalData().get("tossApiErrorMessage") != null) {
				failReason = gatewayResponse.additionalData().get("tossApiErrorMessage").toString();
			}
			throw new ApplicationException(ErrorCode.PAYMENT_CONFIRMATION_FAILED, failReason);
		}
		if (order.getUser() != null) {
			eventPublisher.publishEvent(
				new PaymentPointEarnEvent(order.getUser().getId(), gatewayResponse.confirmedAmount()));
		}
		orderRepository.save(order);
	}

	@NotNull
	private BigDecimal getExpectedAmount(Order order) {
		BigDecimal totalPackagingPrice = orderRepository.findTotalPackagingPriceByOrderId(order.getId());
		BigDecimal originalAmount = Optional.ofNullable(order.getOriginalAmount()).orElse(BigDecimal.ZERO);
		BigDecimal deliveryFee = Optional.ofNullable(order.getDeliveryFee()).orElse(BigDecimal.ZERO);
		BigDecimal saleDiscount = Optional.ofNullable(order.getSaleDiscountAmount()).orElse(BigDecimal.ZERO);
		BigDecimal pointDiscount = Optional.ofNullable(order.getPointUseAmount()).orElse(BigDecimal.ZERO);
		BigDecimal couponDiscount = Optional.ofNullable(order.getCouponDiscountAmount()).orElse(BigDecimal.ZERO);

		return originalAmount
			.add(deliveryFee)
			.add(totalPackagingPrice)
			.subtract(saleDiscount
				.add(pointDiscount)
				.add(couponDiscount));
	}

}