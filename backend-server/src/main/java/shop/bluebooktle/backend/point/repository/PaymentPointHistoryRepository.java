package shop.bluebooktle.backend.point.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.point.entity.PaymentPointHistory;

public interface PaymentPointHistoryRepository extends JpaRepository<PaymentPointHistory, Long> {
}
