package shop.bluebooktle.backend.book_order.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.bluebooktle.backend.book_order.entity.PackagingOption;

public interface PackagingOptionQueryRepository {
	Page<PackagingOption> searchNameContaining(String name, Pageable pageable);
}
