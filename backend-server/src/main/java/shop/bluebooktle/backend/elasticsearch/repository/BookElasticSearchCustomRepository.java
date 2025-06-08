package shop.bluebooktle.backend.elasticsearch.repository;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.repository.BookRepository;
import shop.bluebooktle.backend.elasticsearch.document.BookDocument;
import shop.bluebooktle.common.dto.book.BookSortType;

@Repository
@RequiredArgsConstructor
public class BookElasticSearchCustomRepository {

	private final ElasticsearchOperations operations;
	private final BookRepository bookRepository;

	// 키워드 검색 및 정렬을 위한 메소드
	public Page<Book> searchBooksByKeywordAndSort(String keyword, BookSortType bookSortType, int page, int size) {
		PageRequest pageable = PageRequest.of(page, size);
		CriteriaQuery query = searchWeightSetting(keyword);
		query.setPageable(pageable);
		applySorts(query, bookSortType);
		return executeSearchAndMapToBooks(query, pageable);
	}

	// 키워드 검색을 위한 메소드 (관리자 페이지)
	public Page<Book> searchBooksByKeyword(String keyword, int page, int size) {
		PageRequest pageable = PageRequest.of(page, size);
		CriteriaQuery query = searchWeightSetting(keyword);
		query.setPageable(pageable);
		return executeSearchAndMapToBooks(query, pageable);
	}

	// 정렬을 위한 메소드
	public Page<Book> searchBooksBySortOnly(BookSortType bookSortType, int page, int size) {
		PageRequest pageable = PageRequest.of(page, size);
		CriteriaQuery query = new CriteriaQuery(new Criteria());
		query.setPageable(pageable);
		applySorts(query, bookSortType);
		return executeSearchAndMapToBooks(query, pageable);
	}

	// 카테고리 내 정렬
	public Page<Book> searchBooksByCategoryAndSort(List<Long> categoryIds, BookSortType bookSortType, int page,
		int size) {
		PageRequest pageable = PageRequest.of(page, size);
		Criteria criteria = new Criteria("categoryIds").in(categoryIds);
		CriteriaQuery query = new CriteriaQuery(criteria);
		query.setPageable(pageable);
		applySorts(query, bookSortType);
		return executeSearchAndMapToBooks(query, pageable);
	}

	private CriteriaQuery searchWeightSetting(String keyword) {
		Criteria criteria = new Criteria()
			.or(new Criteria("title").matches(keyword).boost(10.0f))
			.or(new Criteria("description").matches(keyword).boost(3.0f))
			.or(new Criteria("publisherNames").matches(keyword).boost(2.0f))
			.or(new Criteria("authorNames").matches(keyword).boost(4.0f))
			.or(new Criteria("tagNames").matches(keyword).boost(5.0f));

		return new CriteriaQuery(criteria);
	}

	private Page<Book> executeSearchAndMapToBooks(CriteriaQuery query, PageRequest pageable) {
		SearchHits<BookDocument> hits = operations.search(query, BookDocument.class);

		List<Long> bookIds = hits.getSearchHits().stream()
			.map(SearchHit::getContent)
			.map(BookDocument::getId)
			.toList();

		List<Book> books = bookRepository.findAllById(bookIds);
		Map<Long, Book> bookMap = books.stream()
			.collect(Collectors.toMap(Book::getId, b -> b));

		List<Book> sortedBooks = bookIds.stream()
			.map(bookMap::get)
			.filter(Objects::nonNull)
			.toList();

		return new PageImpl<>(sortedBooks, pageable, hits.getTotalHits());
	}

	private void applySorts(CriteriaQuery query, BookSortType sortType) {
		switch (sortType) {
			case POPULARITY -> {
				query.addSort(Sort.by(Sort.Direction.DESC, "viewCount"));
				query.addSort(Sort.by(Sort.Direction.DESC, "searchCount"));
			}
			case NEWEST -> query.addSort(Sort.by(Sort.Direction.DESC, "publishDate"));
			case PRICE_ASC -> query.addSort(Sort.by(Sort.Direction.ASC, "salePrice"));
			case PRICE_DESC -> query.addSort(Sort.by(Sort.Direction.DESC, "salePrice"));
			case RATING -> query.addSort(Sort.by(Sort.Direction.DESC, "star"));
			case REVIEW_COUNT -> query.addSort(Sort.by(Sort.Direction.DESC, "reviewCount"));
			default -> query.addSort(Sort.by(Sort.Direction.DESC, "publishDate"));
		}
	}

}
