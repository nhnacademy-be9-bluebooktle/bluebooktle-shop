package shop.bluebooktle.backend.elasticsearch.repository;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.elasticsearch.document.BookDocument;

@Repository
@RequiredArgsConstructor
public class BookElasticSearchCustomRepository {

	private final ElasticsearchOperations operations;

	// 키워드 검색을 위한 메소드, 반환값 : book_id
	public List<Long> searchByWeightedKeyword(String keyword, int page, int size) {
		Criteria criteria = new Criteria()
			.or(new Criteria("title").matches(keyword).boost(10.0f))
			.or(new Criteria("description").matches(keyword).boost(3.0f))
			.or(new Criteria("publisherNames").matches(keyword).boost(2.0f))
			.or(new Criteria("authorNames").matches(keyword).boost(4.0f))
			.or(new Criteria("tagNames").matches(keyword).boost(5.0f));

		CriteriaQuery query = new CriteriaQuery(criteria, PageRequest.of(page, size));
		return operations.search(query, BookDocument.class)
			.stream()
			.map(SearchHit::getContent)
			.map(BookDocument::getId)
			.toList();
	}
}
