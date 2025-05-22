package shop.bluebooktle.backend.book.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.bluebooktle.backend.book.entity.Publisher;

public interface PublisherQueryRepository {
	Page<Publisher> searchByNameContaining(String searchKeyword, Pageable pageable);
}
