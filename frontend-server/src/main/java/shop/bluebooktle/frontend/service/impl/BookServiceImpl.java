package shop.bluebooktle.frontend.service.impl;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.common.dto.book.response.BookCartOrderResponse;
import shop.bluebooktle.frontend.repository.AdminBookRepository;
import shop.bluebooktle.frontend.service.BookService;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

	private final AdminBookRepository adminBookRepository;

	@Override
	public BookCartOrderResponse getBookCartOrder(Long bookId, int quantity) {
		return adminBookRepository.getBookCartOrder(bookId, quantity);
	}
}
