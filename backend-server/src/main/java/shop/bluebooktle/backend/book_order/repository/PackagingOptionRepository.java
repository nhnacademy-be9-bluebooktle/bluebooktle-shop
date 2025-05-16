package shop.bluebooktle.backend.book_order.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import shop.bluebooktle.backend.book_order.entity.PackagingOption;

public interface PackagingOptionRepository extends JpaRepository<PackagingOption, Long> {
	// 삭제되지 않은 포장 전체 조회
	List<PackagingOption> findAllByDeletedAtIsNull();

	// 삭제되지 않은 개별 포장 조회
	Optional<PackagingOption> findByIdAndDeletedAtIsNull(@Param("id") Long id);
}
