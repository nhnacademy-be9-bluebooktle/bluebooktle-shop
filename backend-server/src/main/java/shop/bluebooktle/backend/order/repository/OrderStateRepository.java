package shop.bluebooktle.backend.order.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.order.entity.OrderState;
import shop.bluebooktle.common.domain.order.OrderStatus;

public interface OrderStateRepository extends JpaRepository<OrderState, Long> {

	Optional<OrderState> findByState(OrderStatus state); // 상태 enum → 엔티티로 변환

}
