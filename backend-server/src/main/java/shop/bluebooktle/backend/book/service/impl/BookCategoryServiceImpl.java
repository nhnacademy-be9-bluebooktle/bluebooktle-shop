package shop.bluebooktle.backend.book.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.dto.request.BookCategoryRequest;
import shop.bluebooktle.backend.book.dto.request.BookInfoRequest;
import shop.bluebooktle.backend.book.dto.request.CategoryInfoRequest;
import shop.bluebooktle.backend.book.dto.response.BookInfoResponse;
import shop.bluebooktle.backend.book.dto.response.CategoryResponse;
import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.BookCategory;
import shop.bluebooktle.backend.book.entity.Category;
import shop.bluebooktle.backend.book.repository.BookCategoryRepository;
import shop.bluebooktle.backend.book.repository.BookRepository;
import shop.bluebooktle.backend.book.repository.CategoryRepository;
import shop.bluebooktle.backend.book.service.BookCategoryService;
import shop.bluebooktle.common.exception.book.BookCategoryAlreadyExistsException;
import shop.bluebooktle.common.exception.book.BookCategoryLimitExceededException;
import shop.bluebooktle.common.exception.book.BookCategoryNotFoundException;
import shop.bluebooktle.common.exception.book.BookCategoryRequiredException;
import shop.bluebooktle.common.exception.book.BookNotFoundException;
import shop.bluebooktle.common.exception.book.CategoryNotFoundException;

@Service
@RequiredArgsConstructor
public class BookCategoryServiceImpl implements BookCategoryService {

	private final BookRepository bookRepository;
	private final CategoryRepository categoryRepository;
	private final BookCategoryRepository bookCategoryRepository;

	@Override
	@Transactional
	public void registerBookCategory(BookCategoryRequest request) {
		Book book = requireBook(request.bookId());
		Category category = requireCategory(request.categoryId());

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
	@Transactional
	public void deleteBookCategory(BookCategoryRequest request) {
		Book book = requireBook(request.bookId());
		Category category = requireCategory(request.categoryId());

		if (!bookCategoryRepository.existsByBookAndCategory(book, category)) {
			throw new BookCategoryNotFoundException(book.getId(), category.getId());
		}

		long count = bookCategoryRepository.countByBook(book);
		if (count <= 1) {
			throw new BookCategoryRequiredException(book.getId());
		}

		BookCategory bookCategory = new BookCategory(book, category);
		bookCategoryRepository.delete(bookCategory);
	}

	@Override
	@Transactional(readOnly = true)
	public List<CategoryResponse> getCategoryByBookId(BookInfoRequest request) {
		Book book = requireBook(request.bookId());

		List<BookCategory> bookCategories = bookCategoryRepository.findByBook(book);
		List<CategoryResponse> result = new ArrayList<>();

		for (BookCategory bookCategory : bookCategories) {
			Category category = bookCategory.getCategory();
			result.add(new CategoryResponse(category.getId(), category.getName()));
		}

		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<BookInfoResponse> searchBooksByCategory(CategoryInfoRequest request) {
		Category category = requireCategory(request.categoryId());

		List<Long> bookIds = bookCategoryRepository.findBookIdByCategory_Id(category.getId());

		List<BookInfoResponse> result = new ArrayList<>();
		for (Long bookId : bookIds) {
			result.add(new BookInfoResponse(bookId));
		}
		return result;
	}

	private Book requireBook(Long id) {
		return bookRepository.findById(id)
			.orElseThrow(() -> new BookNotFoundException("Book not found with id: " + id));
	}

	private Category requireCategory(Long id) {
		return categoryRepository.findById(id)
			.orElseThrow(() -> new CategoryNotFoundException(id));
	}
}
