package shop.bluebooktle.backend.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.order.entity.Refund;

public interface RefundRepository extends JpaRepository<Refund, Long> {
}
