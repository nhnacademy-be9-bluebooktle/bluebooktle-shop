package shop.bluebooktle.backend.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.payment.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
