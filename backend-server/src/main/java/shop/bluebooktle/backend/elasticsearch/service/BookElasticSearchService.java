package shop.bluebooktle.backend.elasticsearch.service;

import org.springframework.data.domain.Page;

import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.common.dto.book.request.BookElasticSearchRegisterRequest;

public interface BookElasticSearchService {

	void create(BookElasticSearchRegisterRequest request);

	Page<Book> searchBookByKeyword(String keyword, int page, int size);

}
