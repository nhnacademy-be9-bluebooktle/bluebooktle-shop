package shop.bluebooktle.backend.book_order.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.repository.BookRepository;
import shop.bluebooktle.backend.book_order.entity.BookOrder;
import shop.bluebooktle.backend.book_order.jpa.BookOrderRepository;
import shop.bluebooktle.backend.book_order.service.impl.BookOrderServiceImpl;
import shop.bluebooktle.backend.order.entity.Order;
import shop.bluebooktle.backend.order.repository.OrderRepository;
import shop.bluebooktle.common.dto.book_order.request.BookOrderRegisterRequest;
import shop.bluebooktle.common.dto.book_order.request.BookOrderUpdateRequest;
import shop.bluebooktle.common.dto.book_order.response.BookOrderResponse;
import shop.bluebooktle.common.exception.book.BookNotFoundException;
import shop.bluebooktle.common.exception.book_order.BookOrderNotFoundException;
import shop.bluebooktle.common.exception.order.OrderNotFoundException;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class BookOrderServiceTest {

	@InjectMocks
	private BookOrderServiceImpl service;

	@Mock
	private BookOrderRepository bookOrderRepository;
	@Mock
	private BookRepository bookRepository;
	@Mock
	private OrderRepository orderRepository;

	@Test
	@DisplayName("도서 주문 생성 - 성공")
	void createBookOrder_success() {
		// given
		Order orderMock = mock(Order.class);
		given(orderMock.getId()).willReturn(11L);
		given(orderRepository.findById(11L)).willReturn(Optional.of(orderMock));

		Book bookMock = mock(Book.class);
		given(bookMock.getId()).willReturn(22L);
		given(bookRepository.findById(22L)).willReturn(Optional.of(bookMock));

		BookOrder saved = BookOrder.builder()
			.order(orderMock)
			.book(bookMock)
			.quantity(4)
			.price(BigDecimal.valueOf(10000))
			.build();
		ReflectionTestUtils.setField(saved, "id", 33L);
		given(bookOrderRepository.save(any())).willReturn(saved);

		BookOrderRegisterRequest req = BookOrderRegisterRequest.builder()
			.orderId(11L)
			.bookId(22L)
			.quantity(4)
			.price(BigDecimal.valueOf(10000))
			.build();

		// when
		BookOrderResponse resp = service.createBookOrder(req);

		// then
		assertThat(resp.getBookOrderId()).isEqualTo(33L);
		assertThat(resp.getOrderId()).isEqualTo(11L);
		assertThat(resp.getBookId()).isEqualTo(22L);
		assertThat(resp.getQuantity()).isEqualTo(4);
		assertThat(resp.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(10000));
	}

	@Test
	@DisplayName("도서 주문 생성 - Order 없음 예외")
	void createBookOrder_orderNotFound() {
		// given
		given(orderRepository.findById(anyLong())).willReturn(Optional.empty());
		BookOrderRegisterRequest req = BookOrderRegisterRequest.builder()
			.orderId(99L).bookId(1L).quantity(1).price(BigDecimal.ZERO).build();

		// when & then
		assertThatThrownBy(() -> service.createBookOrder(req))
			.isInstanceOf(OrderNotFoundException.class);
		then(bookOrderRepository).should(never()).save(any());
	}

	@Test
	@DisplayName("도서 주문 생성 - Book 없음 예외")
	void createBookOrder_bookNotFound() {
		// given
		Order orderMock = mock(Order.class);
		ReflectionTestUtils.setField(orderMock, "id", 11L);
		given(orderRepository.findById(11L)).willReturn(Optional.of(orderMock));
		given(bookRepository.findById(anyLong())).willReturn(Optional.empty());

		BookOrderRegisterRequest req = BookOrderRegisterRequest.builder()
			.orderId(11L).bookId(99L).quantity(1).price(BigDecimal.ZERO).build();

		// when & then
		assertThatThrownBy(() -> service.createBookOrder(req))
			.isInstanceOf(BookNotFoundException.class);
		then(bookOrderRepository).should(never()).save(any());
	}

	@Test
	@DisplayName("도서 주문 조회 - 성공")
	void getBookOrder_success() {
		// given
		Order orderMock = mock(Order.class);
		given(orderMock.getId()).willReturn(55L);
		Book bookMock = mock(Book.class);
		given(bookMock.getId()).willReturn(66L);

		BookOrder bo = BookOrder.builder()
			.order(orderMock)
			.book(bookMock)
			.quantity(2)
			.price(BigDecimal.valueOf(5000))
			.build();
		ReflectionTestUtils.setField(bo, "id", 77L);
		given(bookOrderRepository.findById(77L))
			.willReturn(Optional.of(bo));

		// when
		BookOrderResponse resp = service.getBookOrder(77L);

		// then
		assertThat(resp.getBookOrderId()).isEqualTo(77L);
		assertThat(resp.getOrderId()).isEqualTo(55L);
		assertThat(resp.getBookId()).isEqualTo(66L);
		assertThat(resp.getQuantity()).isEqualTo(2);
		assertThat(resp.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(5000));
	}

	@Test
	@DisplayName("도서 주문 조회 - 실패")
	void getBookOrder_notFound() {
		// given
		given(bookOrderRepository.findById(anyLong()))
			.willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> service.getBookOrder(123L))
			.isInstanceOf(BookOrderNotFoundException.class);
	}

	@Test
	@DisplayName("도서 주문 수정 - 성공")
	void updateBookOrder_success() {
		// given
		Order orderMock = mock(Order.class);
		given(orderMock.getId()).willReturn(55L);
		Book bookMock = mock(Book.class);
		given(bookMock.getId()).willReturn(66L);

		BookOrder bo = BookOrder.builder()
			.order(orderMock)
			.book(bookMock)
			.quantity(2)
			.price(BigDecimal.valueOf(8000))
			.build();
		ReflectionTestUtils.setField(bo, "id", 88L);
		given(bookOrderRepository.findById(88L))
			.willReturn(Optional.of(bo));

		BookOrderUpdateRequest req = BookOrderUpdateRequest.builder()
			.quantity(5)
			.price(BigDecimal.valueOf(12000))
			.build();

		// when
		BookOrderResponse resp = service.updateBookOrder(88L, req);

		// then: 변경된 필드 확인
		assertThat(resp.getQuantity()).isEqualTo(5);
		assertThat(resp.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(12000));
	}

	@Test
	@DisplayName("도서 주문 수정 - 실패")
	void updateBookOrder_notFound() {
		// given
		given(bookOrderRepository.findById(anyLong()))
			.willReturn(Optional.empty());
		BookOrderUpdateRequest req = BookOrderUpdateRequest.builder()
			.quantity(1).price(BigDecimal.ZERO).build();

		// when & then
		assertThatThrownBy(() -> service.updateBookOrder(999L, req))
			.isInstanceOf(BookOrderNotFoundException.class);
	}

	@Test
	@DisplayName("도서 주문 삭제 - 성공")
	void deleteBookOrder_success() {
		// given
		BookOrder bo = BookOrder.builder()
			.order(mock(Order.class))
			.book(mock(Book.class))
			.quantity(1)
			.price(BigDecimal.ZERO)
			.build();
		ReflectionTestUtils.setField(bo, "id", 99L);
		given(bookOrderRepository.findById(99L))
			.willReturn(Optional.of(bo));

		// when
		service.deleteBookOrder(99L);

		// then
		then(bookOrderRepository).should().delete(bo);
	}

	@Test
	@DisplayName("도서 주문 삭제 - 실패")
	void deleteBookOrder_notFound() {
		// given
		given(bookOrderRepository.findById(anyLong()))
			.willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> service.deleteBookOrder(123L))
			.isInstanceOf(BookOrderNotFoundException.class);
	}
}
