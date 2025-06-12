package shop.bluebooktle.backend.order.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.order.entity.Order;
import shop.bluebooktle.common.domain.order.OrderStatus;
import shop.bluebooktle.common.entity.auth.User;

public interface OrderRepository extends JpaRepository<Order, Long>, OrderQueryRepository {

	Page<Order> findByUserAndCreatedAtBetween(User user, LocalDateTime createdAtAfter, LocalDateTime createdAtBefore,
		Pageable pageable);

	@EntityGraph(attributePaths = {
		"user",
		"orderState",
		"bookOrders.book.bookSaleInfo",
	})
	Optional<Order> findOrderForCancelById(Long id);

	// 사용자 + 상태(enum) 조회
	@EntityGraph(attributePaths = {
		"orderState",
		"bookOrders.book.bookImgs.img"
	})
	Page<Order> findByUserAndOrderState_StateOrderByCreatedAtDesc(User user, OrderStatus state, Pageable pageable);

	// 사용자 + 상태 + 기간 조회
	@EntityGraph(attributePaths = {"orderState"})
	Page<Order> findByUserAndOrderState_StateAndCreatedAtBetween(User user, OrderStatus state, LocalDateTime start,
		LocalDateTime end, Pageable pageable);

	// 사용자 전체 조회
	@EntityGraph(attributePaths = {
		"orderState",
		"bookOrders.book.bookImgs.img"
	})
	Page<Order> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

	@EntityGraph(attributePaths = {
		"orderState",
		"bookOrders.book.bookImgs.img",
		"user",
		"payment.paymentDetail",
		"payment.paymentPointHistory.pointHistory"
	})
	Optional<Order> findByOrderKey(String orderKey);

	@EntityGraph(attributePaths = {
		"orderState",
		"user",
		"payment.paymentDetail",
		"payment.paymentPointHistory.pointHistory",
		"refund"
	})
	Optional<Order> findOrderForRefund(Long orderId);

	@NotNull
	@EntityGraph(attributePaths = {
		"bookOrders.book.bookImgs.img",
		"bookOrders.orderPackagings.packagingOption",
		"userCouponBookOrders.userCoupon.coupon.couponType"
	})
	Optional<Order> findById(@NotNull Long orderId);

	@EntityGraph(attributePaths = {
		"payment"
	})
	Optional<Order> getOrderByOrderKey(String orderKey);
}
