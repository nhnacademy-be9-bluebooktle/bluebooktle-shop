package shop.bluebooktle.frontend.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.common.dto.book.response.BookCartOrderResponse;
import shop.bluebooktle.common.dto.book.response.BookDetailResponse;
import shop.bluebooktle.common.dto.book.response.BookInfoResponse;
import shop.bluebooktle.common.dto.book.response.CategoryResponse;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.frontend.repository.AdminBookRepository;
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

	@Override
	public Page<BookInfoResponse> getPagedBooks(int page, int size, String searchKeyword) {
		Pageable pageable = PageRequest.of(page, size);

		String keyword = null;
		if (searchKeyword != null && !searchKeyword.isBlank()) {
			keyword = searchKeyword;
		}

		PaginationData<BookInfoResponse> response = bookRepository.searchBooks(page, size, searchKeyword);
		List<BookInfoResponse> books = response.getContent();
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

	@Override
	public BookDetailResponse getBookDetail(Long bookId) {
		return bookRepository.getBookDetail(bookId);
	}

	@Override
	public void like(Long bookId) {
		bookRepository.likeBook(bookId);
	}

	@Override
	public void unlike(Long bookId) {
		bookRepository.unlikeBook(bookId);
	}

	@Override
	public boolean isLiked(Long bookId) {
		return bookRepository.isLiked(bookId).isLiked();
		// 뒤에 isLiked()는 BookLikesResponse(DTO)에 있는 boolean을 꺼내기 위함
	}

	@Override
	public int countLikes(Long bookId) {
		return bookRepository.countLikes(bookId).getCountLikes();
	}
}
