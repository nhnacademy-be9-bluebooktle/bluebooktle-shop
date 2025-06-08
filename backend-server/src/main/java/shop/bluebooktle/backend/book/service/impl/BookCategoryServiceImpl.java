package shop.bluebooktle.backend.book.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.BookCategory;
import shop.bluebooktle.backend.book.entity.BookSaleInfo;
import shop.bluebooktle.backend.book.entity.Category;
import shop.bluebooktle.backend.book.repository.BookAuthorRepository;
import shop.bluebooktle.backend.book.repository.BookCategoryRepository;
import shop.bluebooktle.backend.book.repository.BookImgRepository;
import shop.bluebooktle.backend.book.repository.BookRepository;
import shop.bluebooktle.backend.book.repository.BookSaleInfoRepository;
import shop.bluebooktle.backend.book.repository.CategoryRepository;
import shop.bluebooktle.backend.book.service.BookCategoryService;
import shop.bluebooktle.backend.book.service.CategoryService;
import shop.bluebooktle.backend.elasticsearch.service.BookElasticSearchService;
import shop.bluebooktle.common.dto.book.BookSortType;
import shop.bluebooktle.common.dto.book.request.BookInfoRequest;
import shop.bluebooktle.common.dto.book.response.BookInfoResponse;
import shop.bluebooktle.common.dto.book.response.CategoryResponse;
import shop.bluebooktle.common.exception.book.BookCategoryAlreadyExistsException;
import shop.bluebooktle.common.exception.book.BookCategoryLimitExceededException;
import shop.bluebooktle.common.exception.book.BookCategoryNotFoundException;
import shop.bluebooktle.common.exception.book.BookCategoryRequiredException;
import shop.bluebooktle.common.exception.book.BookNotFoundException;
import shop.bluebooktle.common.exception.book.CategoryNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional
public class BookCategoryServiceImpl implements BookCategoryService {

	private final BookRepository bookRepository;
	private final CategoryRepository categoryRepository;
	private final BookCategoryRepository bookCategoryRepository;
	private final BookAuthorRepository bookAuthorRepository;
	private final BookSaleInfoRepository bookSaleInfoRepository;
	private final BookImgRepository bookImgRepository;

	private CategoryService categoryService;
	private BookElasticSearchService bookElasticSearchService;

	@Override
	public void registerBookCategory(Long bookId, Long categoryId) {
		Book book = requireBook(bookId);
		Category category = requireCategory(categoryId);

		// 도서에 이미 등록된 카테고리인 경우 예외 발생
		if (bookCategoryRepository.existsByBookAndCategory(book, category)) {
			throw new BookCategoryAlreadyExistsException(book.getId(), category.getId());
		}

		long count = bookCategoryRepository.countByBook(book);
		// 도서 최대 10개의 카테고리 지정
		if (count >= 10) {
			throw new BookCategoryLimitExceededException(book.getId());
		}
		BookCategory bookCategory = new BookCategory(book, category);
		bookCategoryRepository.save(bookCategory);
	}

	@Override
	public void registerBookCategory(Long bookId, List<Long> categoryIdList) {
		for (Long categoryId : categoryIdList) {
			registerBookCategory(bookId, categoryId);
		}
	}

	@Override
	public void deleteBookCategory(Long bookId, Long categoryId) {
		Book book = requireBook(bookId);
		Category category = requireCategory(categoryId);

		BookCategory bookCategory = bookCategoryRepository.findByBookAndCategory(book, category)
			.orElseThrow(() -> new BookCategoryNotFoundException(bookId, categoryId));

		long count = bookCategoryRepository.countByBook(book);
		if (count <= 1) {
			throw new BookCategoryRequiredException(book.getId());
		}

		bookCategoryRepository.delete(bookCategory);
	}

	@Override
	public void updateBookCategory(Long bookId, List<Long> categoryIdList) {
		// 도서에 등록 되었던 기존 카테고리 삭제
		List<BookCategory> bookCategoryList = bookCategoryRepository.findByBook_Id(bookId);
		bookCategoryRepository.deleteAll(bookCategoryList);
		// 다시 새롭게 등록
		registerBookCategory(bookId, categoryIdList);

	}

	// 해당 도서의 카테고리 수정
	@Override
	public void updateBookCategoryByBookCategoryId(Long updatedCategoryId, Long bookCategoryId) {
		BookCategory bookCategory = bookCategoryRepository.findById(bookCategoryId)
			.orElseThrow(BookCategoryNotFoundException::new);
		Category updatedCategory = requireCategory(updatedCategoryId);
		bookCategory.setCategory(updatedCategory);
	}

	@Override
	public void updateBookCategory(Long updatedCategoryId, Long categoryId, Long bookId) {
		Category category = requireCategory(categoryId);
		Book book = requireBook(bookId);
		if (!bookCategoryRepository.existsByBookAndCategory(book, category)) {
			throw new BookCategoryNotFoundException(book.getId(), category.getId());
		}
		Category updatedCategory = requireCategory(updatedCategoryId);
		BookCategory bookCategory = bookCategoryRepository.findByBookAndCategory(book, category)
			.orElseThrow(BookCategoryNotFoundException::new);
		bookCategory.setCategory(updatedCategory);
	}

	@Override
	@Transactional(readOnly = true)
	public List<CategoryResponse> getCategoryByBookId(BookInfoRequest request) {
		Book book = requireBook(request.bookId());

		List<BookCategory> bookCategories = bookCategoryRepository.findByBook(book);
		List<CategoryResponse> result = new ArrayList<>();

		for (BookCategory bookCategory : bookCategories) {
			Category category = bookCategory.getCategory();
			result.add(new CategoryResponse(
				category.getId(),
				category.getName(),
				category.getParentCategory().getName(),
				category.getCategoryPath()));
		}
		//TODO book_category_id 도 반환해야할 듯
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public Page<BookInfoResponse> searchBooksByCategory(Long categoryId, Pageable pageable, BookSortType bookSortType) {
		Category category = requireCategory(categoryId);
		List<Long> categoryIds = categoryService.getAllDescendantCategories(category).stream()
			.map(Category::getId)
			.collect(Collectors.toCollection(ArrayList::new));
		categoryIds.add(category.getId());

		// 엘라스틱에서 도서 찾기
		Page<Book> bookPage = bookElasticSearchService.searchBooksByCategoryAndSort(categoryIds, bookSortType,
			pageable.getPageNumber(), pageable.getPageSize());
		List<BookInfoResponse> responseList = bookPage.getContent().stream()
			.map(book -> {
				BookSaleInfo bookSaleInfo = bookSaleInfoRepository.findByBook(book)
					.orElseThrow(BookNotFoundException::new);
				List<String> authorNameList = bookAuthorRepository.findByBookId(book.getId()).stream()
					.map(bookAuthor -> bookAuthor.getAuthor().getName())
					.toList();

				String imgUrl = bookImgRepository.findByBook(book).getImg().getImgUrl();
				return new BookInfoResponse(
					book.getId(),
					book.getTitle(),
					authorNameList,
					bookSaleInfo.getSalePrice(),
					bookSaleInfo.getPrice(),
					imgUrl,
					book.getCreatedAt(),
					bookSaleInfo.getStar(),
					bookSaleInfo.getReviewCount(),
					bookSaleInfo.getViewCount()
				);
			})
			.toList();

		return new PageImpl<>(responseList, pageable, bookPage.getTotalElements());
	}

	@Transactional(readOnly = true)
	protected Book requireBook(Long id) {
		return bookRepository.findById(id)
			.orElseThrow(() -> new BookNotFoundException("Book not found with id: " + id));
	}

	@Transactional(readOnly = true)
	protected Category requireCategory(Long id) {
		return categoryRepository.findById(id)
			.orElseThrow(() -> new CategoryNotFoundException(id));
	}
}
