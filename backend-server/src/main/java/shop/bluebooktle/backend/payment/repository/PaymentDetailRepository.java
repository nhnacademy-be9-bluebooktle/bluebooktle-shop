package shop.bluebooktle.backend.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.payment.entity.PaymentDetail;

public interface PaymentDetailRepository extends JpaRepository<PaymentDetail, Long> {
}
