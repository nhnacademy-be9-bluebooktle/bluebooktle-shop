package shop.bluebooktle.backend.book.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.bluebooktle.backend.book.entity.Tag;

public interface TagQueryRepository {
	Page<Tag> searchByNameContaining(String searchKeyword, Pageable pageable);
}
