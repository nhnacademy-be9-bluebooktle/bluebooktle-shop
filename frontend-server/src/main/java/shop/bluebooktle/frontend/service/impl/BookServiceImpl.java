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
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.frontend.repository.BookRepository;
import shop.bluebooktle.frontend.service.BookService;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

	private final AdminBookRepository adminBookRepository;
	private final BookRepository bookRepository;

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
}
