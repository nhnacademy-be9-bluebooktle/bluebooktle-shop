package shop.bluebooktle.backend.elasticsearch.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.BookSaleInfo;
import shop.bluebooktle.backend.elasticsearch.document.BookDocument;
import shop.bluebooktle.backend.elasticsearch.repository.BookElasticSearchCustomRepository;
import shop.bluebooktle.backend.elasticsearch.repository.BookElasticSearchRepository;
import shop.bluebooktle.backend.elasticsearch.service.BookElasticSearchService;
import shop.bluebooktle.common.dto.book.BookSortType;
import shop.bluebooktle.common.dto.elasticsearch.BookElasticSearchRegisterRequest;
import shop.bluebooktle.common.dto.elasticsearch.BookElasticSearchUpdateRequest;
import shop.bluebooktle.common.exception.elasticsearch.ElasticsearchBookDocumentSaveException;
import shop.bluebooktle.common.exception.elasticsearch.ElasticsearchBookNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional
public class BookElasticSearchServiceImpl implements BookElasticSearchService {

	private final BookElasticSearchRepository bookElasticSearchRepository;
	private final BookElasticSearchCustomRepository bookElasticSearchCustomRepository;

	@Override
	public void registerBook(BookElasticSearchRegisterRequest request) {
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

	@Override
	public void updateSearchCount(List<Book> bookList) {
		List<BookDocument> bookDocumentList = bookElasticSearchRepository.findByIdIn(
			bookList.stream().map(Book::getId).toList()
		);
		for (BookDocument bookDocument : bookDocumentList) {
			bookDocument.setSearchCount(bookDocument.getSearchCount() + 1);
			bookElasticSearchRepository.save(bookDocument);
		}
	}

	@Override
	public void updateViewCount(Book book) {
		BookDocument bookDocument = bookElasticSearchRepository.findById(book.getId())
			.orElseThrow(ElasticsearchBookNotFoundException::new);
		bookDocument.setViewCount(bookDocument.getViewCount() + 1);
		bookElasticSearchRepository.save(bookDocument);
	}

	@Override
	public void updateReviewCountAndStar(BookSaleInfo bookSaleInfo) {
		BookDocument bookDocument = bookElasticSearchRepository.findById(bookSaleInfo.getBook().getId())
			.orElseThrow(ElasticsearchBookNotFoundException::new);
		bookDocument.setReviewCount(bookSaleInfo.getReviewCount());
		bookDocument.setStar(bookSaleInfo.getStar());
		bookElasticSearchRepository.save(bookDocument);
	}

	@Override
	public void updateBook(BookElasticSearchUpdateRequest request) {
		BookDocument bookDocument = bookElasticSearchRepository.findById(request.getBookId())
			.orElseThrow(ElasticsearchBookNotFoundException::new);

		bookDocument.setTitle(request.getTitle());
		bookDocument.setDescription(request.getDescription());
		bookDocument.setPublishDate(request.getPublishDate());
		bookDocument.setSalePrice(request.getSalePrice());
		bookDocument.setAuthorNames(request.getAuthorNames());
		bookDocument.setPublisherNames(request.getPublisherNames());
		bookDocument.setTagNames(request.getTagNames());
		bookDocument.setCategoryIds(request.getCategoryIds());

		bookElasticSearchRepository.save(bookDocument);

	}

	@Override
	public void updateTagName(List<Book> bookList, String updatedTagName, String currentTagName) {
		List<BookDocument> bookDocumentList = bookElasticSearchRepository.findByIdIn(
			bookList.stream().map(Book::getId).toList()
		);

		for (BookDocument bookDocument : bookDocumentList) {
			List<String> tagNames = bookDocument.getTagNames();

			if (tagNames.contains(currentTagName)) {
				tagNames = tagNames.stream()
					.map(tag -> tag.equals(currentTagName) ? updatedTagName : tag)
					.toList();

				bookDocument.setTagNames(tagNames);
				bookElasticSearchRepository.save(bookDocument);
			}
		}
	}

	@Override
	public void updateAuthorName(List<Book> bookList, String updatedAuthorName, String currentAuthorName) {
		List<BookDocument> bookDocumentList = bookElasticSearchRepository.findByIdIn(
			bookList.stream().map(Book::getId).toList()
		);

		for (BookDocument bookDocument : bookDocumentList) {
			List<String> authorNames = bookDocument.getAuthorNames();

			if (authorNames.contains(currentAuthorName)) {
				authorNames = authorNames.stream()
					.map(author -> author.equals(currentAuthorName) ? updatedAuthorName : author)
					.toList();

				bookDocument.setAuthorNames(authorNames);
				bookElasticSearchRepository.save(bookDocument);
			}
		}
	}

	@Override
	public void updatePublisherName(List<Book> bookList, String updatePublisherName, String currentPublisherName) {
		List<BookDocument> bookDocumentList = bookElasticSearchRepository.findByIdIn(
			bookList.stream().map(Book::getId).toList()
		);

		for (BookDocument bookDocument : bookDocumentList) {
			List<String> publisherNames = bookDocument.getPublisherNames();

			if (publisherNames.contains(currentPublisherName)) {
				publisherNames = publisherNames.stream()
					.map(author -> author.equals(currentPublisherName) ? updatePublisherName : author)
					.toList();

				bookDocument.setPublisherNames(publisherNames);
				bookElasticSearchRepository.save(bookDocument);
			}
		}
	}

	@Override
	public void deleteBook(Book book) {
		BookDocument bookDocument = bookElasticSearchRepository.findById(book.getId())
			.orElseThrow(ElasticsearchBookNotFoundException::new);
		bookElasticSearchRepository.delete(bookDocument);
	}

	@Transactional(readOnly = true)
	@Override
	public Page<Book> searchBooksByKeyword(String keyword, int page, int size) {
		return bookElasticSearchCustomRepository.searchBooksByKeyword(keyword, page, size);
	}

	@Transactional(readOnly = true)
	@Override
	public Page<Book> searchBooksByKeywordAndSort(String keyword, BookSortType bookSortType, int page, int size) {
		return bookElasticSearchCustomRepository.searchBooksByKeywordAndSort(keyword, bookSortType, page, size);
	}

	@Transactional(readOnly = true)
	@Override
	public Page<Book> searchBooksBySortOnly(BookSortType bookSortType, int page, int size) {
		return bookElasticSearchCustomRepository.searchBooksBySortOnly(bookSortType, page, size);
	}

	@Transactional(readOnly = true)
	@Override
	public Page<Book> searchBooksByCategoryAndSort(List<Long> categoryIds, BookSortType bookSortType, int page,
		int size) {
		return bookElasticSearchCustomRepository.searchBooksByCategoryAndSort(categoryIds, bookSortType, page, size);
	}

}
