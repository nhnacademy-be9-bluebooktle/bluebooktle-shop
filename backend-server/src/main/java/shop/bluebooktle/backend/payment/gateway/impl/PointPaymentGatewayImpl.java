package shop.bluebooktle.backend.payment.gateway.impl;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.order.entity.Order;
import shop.bluebooktle.backend.order.repository.OrderRepository;
import shop.bluebooktle.backend.payment.dto.request.GenericPaymentCancelRequest;
import shop.bluebooktle.backend.payment.dto.response.GenericPaymentCancelResponse;
import shop.bluebooktle.backend.payment.dto.response.GenericPaymentConfirmResponse;
import shop.bluebooktle.backend.payment.dto.response.PaymentStatus;
import shop.bluebooktle.backend.payment.gateway.PaymentGateway;
import shop.bluebooktle.common.dto.payment.request.PaymentConfirmRequest;
import shop.bluebooktle.common.exception.order.OrderNotFoundException;

@Component("POINT")
@RequiredArgsConstructor
@Slf4j
public class PointPaymentGatewayImpl implements PaymentGateway {
	private final OrderRepository orderRepository;

	@Override
	public String getGatewayName() {
		return "POINT";
	}

	@Override
	public GenericPaymentConfirmResponse confirmPayment(PaymentConfirmRequest commonRequest) {

		try {
			return new GenericPaymentConfirmResponse(
				// Point 결제의 경우 paymentKey에 OrderId를 넣음
				commonRequest.orderId(),
				commonRequest.orderId(),
				BigDecimal.valueOf(commonRequest.amount()),
				PaymentStatus.SUCCESS,
				this.getGatewayName() + "_포인트 결제",
				null);

		} catch (Exception e) {
			log.error("PointPaymentGateway 실행 중 에러 발생: {}", commonRequest.orderId(), e);
			return new GenericPaymentConfirmResponse(
				null,
				commonRequest.orderId(),
				BigDecimal.valueOf(commonRequest.amount()),
				PaymentStatus.FAILURE,
				"UNKNOWN_ERROR",
				Map.of("errorMessage", e.getMessage())
			);
		}
	}

	@Override
	public GenericPaymentCancelResponse cancelPayment(GenericPaymentCancelRequest request) {
		try {
			// Point 결제 시 paymentKey = orderKey이므로 paymentKey로 Order 조회
			Order order = orderRepository.findByOrderKey(request.paymentKey()).orElseThrow(OrderNotFoundException::new);

			return new GenericPaymentCancelResponse(
				order.getOrderKey(),
				order.getOrderKey(),
				order.getPayment().getPaidAmount(),
				PaymentStatus.SUCCESS,
				this.getGatewayName() + "_포인트 결제 취소",
				null
			);

		} catch (Exception e) {
			log.error("POINT 결제 취소 중 알 수 없는 에러 발생 (paymentKey: {}): {}", request.paymentKey(), e.getMessage(), e);
			return new GenericPaymentCancelResponse(
				null,
				null,
				null,
				PaymentStatus.FAILURE,
				"UNKNOWN_ERROR",
				Map.of("errorMessage", e.getMessage())
			);
		}
	}

}