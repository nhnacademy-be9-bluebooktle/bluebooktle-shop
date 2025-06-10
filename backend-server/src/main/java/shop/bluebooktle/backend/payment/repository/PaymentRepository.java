package shop.bluebooktle.backend.payment.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.order.entity.Order;
import shop.bluebooktle.backend.payment.entity.Payment;
import shop.bluebooktle.common.domain.order.OrderStatus;
import shop.bluebooktle.common.entity.auth.User;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

	Optional<Payment> findByOrder(Order order);

	@EntityGraph(attributePaths = {"order", "order.orderState"})
	Page<Payment> findByOrder_User(User user, Pageable pageable);

	@EntityGraph(attributePaths = {"order", "order.orderState"})
	Page<Payment> findByOrder_UserAndOrder_OrderState_State(
		User user,
		OrderStatus status,
		Pageable pageable
	);

}
