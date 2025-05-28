package shop.bluebooktle.backend.book_order.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.book_order.entity.PackagingOption;

public interface PackagingOptionRepository
	extends JpaRepository<PackagingOption, Long>, PackagingOptionQueryRepository {
	// 포장 옵션 이름, 가격으로 조회
	boolean existsByName(String name);
}
