package shop.bluebooktle.backend.book_order.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book_order.entity.BookOrder;
import shop.bluebooktle.backend.book_order.entity.UserCouponBookOrder;
import shop.bluebooktle.backend.book_order.jpa.BookOrderRepository;
import shop.bluebooktle.backend.book_order.jpa.UserCouponBookOrderRepository;
import shop.bluebooktle.backend.book_order.service.UserCouponBookOrderService;
import shop.bluebooktle.backend.coupon.entity.UserCoupon;
import shop.bluebooktle.backend.coupon.repository.UserCouponRepository;
import shop.bluebooktle.backend.order.entity.Order;
import shop.bluebooktle.backend.order.repository.OrderRepository;
import shop.bluebooktle.backend.user.repository.UserRepository;
import shop.bluebooktle.common.dto.book_order.request.UserCouponBookOrderRequest;
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.exception.auth.UserNotFoundException;
import shop.bluebooktle.common.exception.book_order.BookOrderNotFoundException;
import shop.bluebooktle.common.exception.coupon.UserCouponAlreadyUsedException;
import shop.bluebooktle.common.exception.coupon.UserCouponNotFoundException;
import shop.bluebooktle.common.exception.order.OrderNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional
public class UserCouponBookOrderServiceImpl implements UserCouponBookOrderService {

	private final UserCouponBookOrderRepository userCouponBookOrderRepository;
	private final UserCouponRepository userCouponRepository;
	private final BookOrderRepository bookOrderRepository;
	private final OrderRepository orderRepository;
	private final UserRepository userRepository;

	@Override
	public void saveCouponUsage(UserCouponBookOrderRequest request) {
		Order order = orderRepository.findById(request.getOrderId())
			.orElseThrow(OrderNotFoundException::new);

		User user = userRepository.findById(request.getUserId())
			.orElseThrow(UserNotFoundException::new);

		for (UserCouponBookOrderRequest.CouponUsageDto usageDto : request.getUsages()) {
			UserCoupon userCoupon = userCouponRepository.findById(usageDto.getUserCouponId())
				.orElseThrow(UserCouponNotFoundException::new);

			if (userCoupon.getUsedAt() != null) {
				throw new UserCouponAlreadyUsedException();
			}

			BookOrder bookOrder = null;
			if (usageDto.getBookOrderId() != null) {
				bookOrder = bookOrderRepository.findById(usageDto.getBookOrderId())
					.orElseThrow(BookOrderNotFoundException::new);
			}

			UserCouponBookOrder usage = UserCouponBookOrder.builder()
				.userCoupon(userCoupon)
				.bookOrder(bookOrder) // 주문 쿠폰이면 null
				.order(order)
				.user(user)
				.build();

			userCouponBookOrderRepository.save(usage);
			userCoupon.useCoupon();
			userCouponRepository.save(userCoupon);
		}
	}

	@Override
	public void deleteCouponUsage(Long orderId, List<Long> userCouponIds) {
		Order order = orderRepository.findById(orderId)
			.orElseThrow(OrderNotFoundException::new);

		List<UserCouponBookOrder> userCouponBookOrders = userCouponBookOrderRepository.findByOrder(order);

		List<UserCouponBookOrder> targetUsages = userCouponBookOrders.stream()
			.filter(usage -> userCouponIds.contains(usage.getUserCoupon().getId()))
			.toList();

		targetUsages.forEach(usage -> usage.getUserCoupon().cancelCoupon());

		userCouponRepository.saveAll(targetUsages.stream().map(UserCouponBookOrder::getUserCoupon).toList());

		userCouponBookOrderRepository.deleteAll(targetUsages);
	}
}
