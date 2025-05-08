package shop.bluebooktle.backend.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.payment.entity.PaymentType;

public interface PaymentTypeRepository extends JpaRepository<PaymentType, Long> {
}
