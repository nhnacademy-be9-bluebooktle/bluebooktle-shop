package shop.bluebooktle.backend.book.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.bluebooktle.backend.book.entity.Book;

public interface BookCategoryQueryRepository {

	Page<Book> findBookUnderCategory(Long categoryId, Pageable pageable);
}
