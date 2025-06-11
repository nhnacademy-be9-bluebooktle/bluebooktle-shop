package shop.bluebooktle.backend.book_order.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.book_order.entity.UserCouponBookOrder;
import shop.bluebooktle.backend.order.entity.Order;

public interface UserCouponBookOrderRepository extends JpaRepository<UserCouponBookOrder, Long> {
	List<UserCouponBookOrder> findByOrder(Order order);
}
