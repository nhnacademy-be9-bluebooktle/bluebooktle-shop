package shop.bluebooktle.backend.elasticsearch.service.impl;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.elasticsearch.document.BookDocument;
import shop.bluebooktle.backend.elasticsearch.repository.BookElasticSearchRepository;
import shop.bluebooktle.backend.elasticsearch.service.BookElasticSearchService;
import shop.bluebooktle.common.dto.book.request.BookElasticSearchRegisterRequest;
import shop.bluebooktle.common.exception.elasticsearch.ElasticsearchBookDocumentSaveException;

@Service
@RequiredArgsConstructor
public class BookElasticSearchServiceImpl implements BookElasticSearchService {

	private final BookElasticSearchRepository bookElasticSearchRepository;

	@Override
	public void create(BookElasticSearchRegisterRequest request) {
		try {
			BookDocument bookDocument = BookDocument.builder()
				.id(request.getBookId())
				.title(request.getTitle())
				.description(request.getDescription())
				.publishDate(request.getPublishDate())
				.salePrice(request.getSalePrice())
				.star(request.getStar())
				.viewCount(request.getViewCount())
				.searchCount(request.getSearchCount())
				.reviewCount(request.getReviewCount())
				.authorNames(request.getAuthorNames())
				.publisherNames(request.getPublisherNames())
				.tagNames(request.getTagNames())
				.categoryIds(request.getCategoryIds())
				.build();

			bookElasticSearchRepository.save(bookDocument);
		} catch (Exception e) {
			throw new ElasticsearchBookDocumentSaveException(e.getMessage());
		}

	}
}
