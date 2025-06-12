package shop.bluebooktle.backend.book_order.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.book_order.entity.PackagingOption;

public interface PackagingOptionRepository
	extends JpaRepository<PackagingOption, Long>, PackagingOptionQueryRepository {
	boolean existsByName(String name);
}
