package shop.bluebooktle.backend.elasticsearch.service;

import shop.bluebooktle.common.dto.book.request.BookElasticSearchRegisterRequest;

public interface BookElasticSearchService {

	void create(BookElasticSearchRegisterRequest request);

}
