package shop.bluebooktle.backend.order.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.order.entity.Order;
import shop.bluebooktle.backend.order.entity.OrderState;
import shop.bluebooktle.backend.order.entity.Refund;
import shop.bluebooktle.backend.order.repository.OrderRepository;
import shop.bluebooktle.backend.order.repository.OrderStateRepository;
import shop.bluebooktle.backend.order.repository.RefundRepository;
import shop.bluebooktle.backend.order.service.RefundService;
import shop.bluebooktle.backend.point.repository.PointHistoryRepository;
import shop.bluebooktle.common.domain.RefundReason;
import shop.bluebooktle.common.domain.RefundStatus;
import shop.bluebooktle.common.domain.order.OrderStatus;
import shop.bluebooktle.common.domain.point.PointSourceTypeEnum;
import shop.bluebooktle.common.dto.refund.request.RefundCreateRequest;
import shop.bluebooktle.common.dto.refund.request.RefundUpdateRequest;
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.entity.point.PointHistory;
import shop.bluebooktle.common.exception.order.OrderNotFoundException;
import shop.bluebooktle.common.exception.order.order_state.OrderStateNotFoundException;
import shop.bluebooktle.common.exception.refund.RefundAlreadyProcessedException;
import shop.bluebooktle.common.exception.refund.RefundNotPossibleException;

@Service
@Transactional
@RequiredArgsConstructor
public class RefundServiceImpl implements RefundService {

	private final RefundRepository refundRepository;
	private final OrderRepository orderRepository;
	private final PointHistoryRepository pointHistoryRepository;
	private final OrderStateRepository orderStateRepository;

	@Override
	public void requestRefund(Long userId, RefundCreateRequest request) {

		Order order = orderRepository.findById(request.orderId()).orElseThrow(OrderNotFoundException::new);

		if (!order.getUser().getId().equals(userId)) {
			throw new RefundNotPossibleException("접근 권한이 없습니다.");
		}
		validPeriod(order, request);

		Refund refund = Refund.builder()
			.order(order)
			.status(RefundStatus.PENDING)
			.date(request.date())
			.price(request.refundAmount())
			.reason(request.reason())
			.reasonDetail(request.reasonDetail())
			.build();
		refundRepository.save(refund);

		OrderState orderState = orderStateRepository.findByState(OrderStatus.RETURNED_REQUEST)
			.orElseThrow(OrderStateNotFoundException::new);

		order.changeOrderState(orderState);
	}

	@Override
	public void updateRefund(RefundUpdateRequest request) {
		Order order = orderRepository.findByOrderIdForRefund(request.orderId())
			.orElseThrow(OrderNotFoundException::new);

		BigDecimal deliveryFee = order.getDeliveryRule().getDeliveryFee();

		Refund refund = order.getRefund();

		if (refund.getStatus() == RefundStatus.COMPLETE || refund.getStatus() == RefundStatus.CANCELED) {
			throw new RefundAlreadyProcessedException();
		}

		if (request.status() == RefundStatus.COMPLETE) {
			BigDecimal finalAmount;
			if (refund.getReason() != RefundReason.DAMAGED && refund.getReason() != RefundReason.DEFECT) {
				finalAmount = refund.getPrice().subtract(deliveryFee);
			} else {
				finalAmount = refund.getPrice();
			}
			User user = order.getUser();
			user.addPoint(finalAmount);

			PointHistory pointHistory = PointHistory.builder()
				.user(user)
				.sourceType(PointSourceTypeEnum.REFUND_EARN)
				.value(finalAmount)
				.build();
			pointHistoryRepository.save(pointHistory);

			OrderState orderState = orderStateRepository.findByState(OrderStatus.RETURNED).orElseThrow(
				OrderStateNotFoundException::new);

			order.changeOrderState(orderState);
		}
		refund.changeStatus(request.status());
	}

	private void validPeriod(Order order, RefundCreateRequest request) {
		LocalDateTime shippedAt = order.getShippedAt();
		LocalDateTime requestDate = request.date();
		long allowedDays;

		if (request.reason() == RefundReason.DAMAGED || request.reason() == RefundReason.DEFECT) {
			allowedDays = 30;
		} else {
			allowedDays = 10;
		}

		LocalDateTime deadline = shippedAt.plusDays(allowedDays);

		if (requestDate.isAfter(deadline)) {
			throw new RefundNotPossibleException("반품 가능 기간(" + allowedDays + "일)이 지났습니다.");
		}
	}

}
