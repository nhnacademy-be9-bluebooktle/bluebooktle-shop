package shop.bluebooktle.backend.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.order.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
	// Custom query methods can be defined here if needed
}
