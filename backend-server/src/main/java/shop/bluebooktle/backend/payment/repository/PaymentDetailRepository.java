package shop.bluebooktle.backend.payment.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.payment.entity.PaymentDetail;

public interface PaymentDetailRepository extends JpaRepository<PaymentDetail, Long> {
	Optional<PaymentDetail> findByPaymentKey(String s);
}
