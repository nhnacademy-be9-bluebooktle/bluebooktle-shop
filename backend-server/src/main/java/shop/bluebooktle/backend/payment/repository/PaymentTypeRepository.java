package shop.bluebooktle.backend.payment.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.payment.entity.PaymentType;

public interface PaymentTypeRepository extends JpaRepository<PaymentType, Long> {

	Optional<PaymentType> findByMethod(String method);

	boolean existsByMethod(String method);
}
