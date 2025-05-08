package shop.bluebooktle.backend.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.payment.entity.PaymentPointHistory;

public interface PaymentPointHistoryRepository extends JpaRepository<PaymentPointHistory, Long> {
}
