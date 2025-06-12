package shop.bluebooktle.backend.order.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.bluebooktle.backend.order.entity.Refund;
import shop.bluebooktle.common.dto.refund.request.RefundSearchRequest;

public interface RefundQueryRepository {
	Page<Refund> searchRefunds(RefundSearchRequest request, Pageable pageable);

	Optional<Refund> findRefundDetailsById(Long refundId);
}
