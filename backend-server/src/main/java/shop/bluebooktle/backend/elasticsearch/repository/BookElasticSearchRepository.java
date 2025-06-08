package shop.bluebooktle.backend.elasticsearch.repository;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import shop.bluebooktle.backend.elasticsearch.document.BookDocument;

public interface BookElasticSearchRepository extends ElasticsearchRepository<BookDocument, Long> {
	List<BookDocument> findByIdIn(List<Long> ids);
}