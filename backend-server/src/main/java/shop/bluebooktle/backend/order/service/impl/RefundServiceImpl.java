package shop.bluebooktle.backend.order.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import shop.bluebooktle.common.domain.order.OrderStatus;
import shop.bluebooktle.common.domain.point.PointSourceTypeEnum;
import shop.bluebooktle.common.domain.refund.RefundReason;
import shop.bluebooktle.common.domain.refund.RefundStatus;
import shop.bluebooktle.common.dto.refund.request.RefundCreateRequest;
import shop.bluebooktle.common.dto.refund.request.RefundSearchRequest;
import shop.bluebooktle.common.dto.refund.request.RefundUpdateRequest;
import shop.bluebooktle.common.dto.refund.response.AdminRefundDetailResponse;
import shop.bluebooktle.common.dto.refund.response.RefundListResponse;
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.entity.point.PointHistory;
import shop.bluebooktle.common.exception.order.OrderNotFoundException;
import shop.bluebooktle.common.exception.order.order_state.OrderStateNotFoundException;
import shop.bluebooktle.common.exception.refund.RefundAlreadyProcessedException;
import shop.bluebooktle.common.exception.refund.RefundNotFoundException;
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

		Order order = orderRepository.getOrderByOrderKey(request.orderKey()).orElseThrow(OrderNotFoundException::new);
		BigDecimal paidAmount = order.getPayment().getPaidAmount();

		if (!order.getUser().getId().equals(userId)) {
			throw new RefundNotPossibleException("접근 권한이 없습니다.");
		}

		LocalDateTime now = LocalDateTime.now();

		validPeriod(order, request, now);

		Refund refund = Refund.builder()
			.order(order)
			.status(RefundStatus.PENDING)
			.date(now)
			.price(paidAmount)
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
		Refund refund = refundRepository.findRefundDetailsById(request.refundId())
			.orElseThrow(RefundNotPossibleException::new);
		Order order = refund.getOrder();

		BigDecimal deliveryFee = order.getDeliveryRule().getDeliveryFee();

		if (refund.getStatus() == RefundStatus.COMPLETE) {
			throw new RefundAlreadyProcessedException();
		}

		if (request.status() == RefundStatus.COMPLETE) {
			BigDecimal finalAmount = order.getOriginalAmount()
				.subtract(order.getSaleDiscountAmount())
				.add(order.getDeliveryFee())
				.add(Optional.ofNullable(order.getCouponDiscountAmount()).orElse(BigDecimal.ZERO));

			if (refund.getReason() != RefundReason.DAMAGED && refund.getReason() != RefundReason.DEFECT) {
				finalAmount = finalAmount.subtract(deliveryFee);
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

	@Transactional(readOnly = true)
	@Override
	public Page<RefundListResponse> getRefundList(RefundSearchRequest request, Pageable pageable) {
		Page<Refund> refundPage = refundRepository.searchRefunds(request, pageable);
		return refundPage.map(this::mapToRefundListResponse);
	}

	private RefundListResponse mapToRefundListResponse(Refund refund) {
		Order order = refund.getOrder();
		return new RefundListResponse(
			refund.getId(),
			order.getId(),
			order.getOrderKey(),
			order.getOrdererName(),
			refund.getDate(),
			refund.getPrice(),
			refund.getReason(),
			refund.getStatus()
		);
	}

	private void validPeriod(Order order, RefundCreateRequest request, LocalDateTime now) {
		LocalDateTime shippedAt = order.getShippedAt();
		long allowedDays;

		if (request.reason() == RefundReason.DAMAGED || request.reason() == RefundReason.DEFECT) {
			allowedDays = 30;
		} else {
			allowedDays = 10;
		}

		LocalDateTime deadline = shippedAt.plusDays(allowedDays);

		if (now.isAfter(deadline)) {
			throw new RefundNotPossibleException("반품 가능 기간(" + allowedDays + "일)이 지났습니다.");
		}
	}

	@Override
	@Transactional(readOnly = true)
	public AdminRefundDetailResponse getAdminRefundDetail(Long refundId) {
		Refund refund = refundRepository.findRefundDetailsById(refundId)
			.orElseThrow(RefundNotFoundException::new);
		Order order = refund.getOrder();
		String userLoginId = Optional.ofNullable(order.getUser())
			.map(User::getLoginId)
			.orElse("비회원");
		return new AdminRefundDetailResponse(
			refund.getId(),
			refund.getDate(),
			refund.getStatus(),
			refund.getPrice(),
			refund.getReason(),
			refund.getReasonDetail(),
			order.getId(),
			order.getOrderKey(),
			order.getOrdererName(),
			userLoginId
		);
	}
}
