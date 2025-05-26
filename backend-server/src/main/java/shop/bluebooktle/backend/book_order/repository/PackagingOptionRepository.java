package shop.bluebooktle.backend.book_order.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.book_order.entity.PackagingOption;

public interface PackagingOptionRepository
	extends JpaRepository<PackagingOption, Long>, PackagingOptionQueryRepository {
	// 포장 옵션 이름, 가격으로 조회
	boolean existsByName(String name);

	// 삭제되지 않은 포장 전체 조회
	//Page<PackagingOption> findAllByDeletedAtIsNull(Pageable pageable);

	// 삭제되지 않은 개별 포장 조회
	//Optional<PackagingOption> findByIdAndDeletedAtIsNull(@Param("id") Long id);
}
