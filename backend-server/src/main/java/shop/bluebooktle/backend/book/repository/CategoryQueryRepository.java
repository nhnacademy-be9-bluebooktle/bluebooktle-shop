package shop.bluebooktle.backend.book.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.bluebooktle.backend.book.entity.Category;

public interface CategoryQueryRepository {
	Page<Category> searchByNameContaining(String searchKeyword, Pageable pageable);
}
