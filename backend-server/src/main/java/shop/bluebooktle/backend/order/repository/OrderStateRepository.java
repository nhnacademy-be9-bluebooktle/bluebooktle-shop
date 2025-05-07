package shop.bluebooktle.backend.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.order.entity.OrderState;

public interface OrderStateRepository extends JpaRepository<OrderState, Long> {
}
