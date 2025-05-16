package shop.bluebooktle.backend.book_order.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.jpa.BookRepository;
import shop.bluebooktle.backend.book_order.entity.BookOrder;
import shop.bluebooktle.backend.book_order.repository.BookOrderRepository;
import shop.bluebooktle.backend.book_order.service.BookOrderService;
import shop.bluebooktle.backend.order.entity.Order;
import shop.bluebooktle.backend.order.repository.OrderRepository;
import shop.bluebooktle.common.dto.book_order.request.BookOrderRegisterRequest;
import shop.bluebooktle.common.dto.book_order.request.BookOrderUpdateRequest;
import shop.bluebooktle.common.dto.book_order.response.BookOrderResponse;
import shop.bluebooktle.common.exception.book.BookNotFoundException;
import shop.bluebooktle.common.exception.book_order.BookOrderNotFoundException;
import shop.bluebooktle.common.exception.order.OrderNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional
public class BookOrderServiceImpl implements BookOrderService {
	private final BookOrderRepository bookOrderRepository;
	private final BookRepository bookRepository;
	private final OrderRepository orderRepository;

	/** 도서 주문 생성 */
	@Override
	public BookOrderResponse createBookOrder(BookOrderRegisterRequest request) {
		Order order = orderRepository.findById(request.getOrderId())
			.orElseThrow(OrderNotFoundException::new);
		Book book = bookRepository.findById(request.getBookId())
			.orElseThrow(BookNotFoundException::new);

		BookOrder bookOrder = BookOrder.builder()
			.order(order)
			.book(book)
			.quantity(request.getQuantity())
			.price(request.getPrice())
			.build();

		BookOrder saved = bookOrderRepository.save(bookOrder);

		return BookOrderResponse.builder()
			.bookOrderId(saved.getId())
			.orderId(saved.getOrder().getId())
			.bookId(saved.getBook().getId())
			.quantity(saved.getQuantity())
			.price(saved.getPrice())
			.build();
	}

	/** 도서 주문 조회 */
	@Override
	public BookOrderResponse getBookOrder(Long bookOrderId) {
		BookOrder order = bookOrderRepository.findByIdAndDeletedAtIsNull(bookOrderId)
			.orElseThrow(BookOrderNotFoundException::new);

		return BookOrderResponse.builder()
			.bookOrderId(order.getId())
			.orderId(order.getOrder().getId())
			.bookId(order.getBook().getId())
			.quantity(order.getQuantity())
			.price(order.getPrice())
			.build();
	}

	/** 도서 주문 수정 */
	@Override
	public BookOrderResponse updateBookOrder(Long bookOrderId, BookOrderUpdateRequest request) {
		BookOrder order = bookOrderRepository.findByIdAndDeletedAtIsNull(bookOrderId)
			.orElseThrow(BookOrderNotFoundException::new);

		order.setQuantity(request.getQuantity());
		order.setPrice(request.getPrice());

		return BookOrderResponse.builder()
			.bookOrderId(order.getId())
			.orderId(order.getOrder().getId())
			.bookId(order.getBook().getId())
			.quantity(order.getQuantity())
			.price(order.getPrice())
			.build();
	}

	/** 도서 주문 삭제 */
	@Override
	public void deleteBookOrder(Long bookOrderId) {
		BookOrder order = bookOrderRepository.findByIdAndDeletedAtIsNull(bookOrderId)
			.orElseThrow(BookOrderNotFoundException::new);
		bookOrderRepository.delete(order);
	}
}
