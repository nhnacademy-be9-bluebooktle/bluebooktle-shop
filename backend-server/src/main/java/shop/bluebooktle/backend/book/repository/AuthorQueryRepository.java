package shop.bluebooktle.backend.book.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.bluebooktle.backend.book.entity.Author;

public interface AuthorQueryRepository {
	Page<Author> searchByNameContaining(String searchKeyword, Pageable pageable);
}
