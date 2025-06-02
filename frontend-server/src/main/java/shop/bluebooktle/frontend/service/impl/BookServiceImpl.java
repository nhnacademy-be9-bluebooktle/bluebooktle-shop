package shop.bluebooktle.frontend.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.common.dto.book.response.BookCartOrderResponse;
import shop.bluebooktle.frontend.repository.AdminBookRepository;
import shop.bluebooktle.common.dto.book.response.BookAllResponse;
import shop.bluebooktle.common.dto.book.response.BookInfoResponse;
import shop.bluebooktle.common.dto.book.response.CategoryResponse;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.frontend.repository.BookCategoryRepository;
import shop.bluebooktle.frontend.repository.BookRepository;
import shop.bluebooktle.frontend.repository.CategoryRepository;
import shop.bluebooktle.frontend.service.BookService;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

	private final AdminBookRepository adminBookRepository;
	private final BookRepository bookRepository;
	private final BookCategoryRepository bookCategoryRepository;
	private final CategoryRepository categoryRepository;

	@Override
	public BookCartOrderResponse getBookCartOrder(Long bookId, int quantity) {
		return adminBookRepository.getBookCartOrder(bookId, quantity);
	}

	public Page<BookAllResponse> getPagedBooks(int page, int size, String searchKeyword) {
		Pageable pageable = PageRequest.of(page, size);

		String keyword = null;
		if (searchKeyword != null && !searchKeyword.isBlank()) {
			keyword = searchKeyword;
		}

		PaginationData<BookAllResponse> response = bookRepository.searchBooks(page, size, searchKeyword);
		List<BookAllResponse> books = response.getContent();
		return new PageImpl<>(books, pageable, response.getTotalElements());
	}

	@Override
	public Page<BookInfoResponse> getPagedBooksByCategoryId(int page, int size, Long categoryId) {
		Pageable pageable = PageRequest.of(page, size);

		PaginationData<BookInfoResponse> response = bookCategoryRepository.getBooksByCategory(page, size, categoryId);
		List<BookInfoResponse> books = response.getContent();
		return new PageImpl<>(books, pageable, response.getTotalElements());
	}

	@Override
	public CategoryResponse getCategoryById(Long categoryId) {
		return categoryRepository.getCategory(categoryId);
	}

}
