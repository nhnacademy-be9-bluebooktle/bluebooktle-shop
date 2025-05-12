package shop.bluebooktle.backend.book_order.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import feign.Param;
import shop.bluebooktle.backend.book_order.entity.OrderPackaging;

public interface OrderPackagingRepository extends JpaRepository<OrderPackaging, Long> {
	// 삭제되지 않은 포장 전체 조회
	@Query("SELECT op FROM OrderPackaging op WHERE op.deletedAt IS NULL")
	List<OrderPackaging> findAllAvailable();

	// 삭제되지 않은 개별 포장 조회
	@Query("SELECT op FROM OrderPackaging op WHERE op.id = :id AND op.deletedAt IS NULL")
	Optional<OrderPackaging> findByIdAndDeletedAtIsNull(@Param("id") Long id);
}
