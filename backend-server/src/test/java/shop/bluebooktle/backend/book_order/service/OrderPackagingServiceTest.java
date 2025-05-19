package shop.bluebooktle.backend.book_order.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
import shop.bluebooktle.backend.book_order.entity.BookOrder;
import shop.bluebooktle.backend.book_order.entity.OrderPackaging;
import shop.bluebooktle.backend.book_order.entity.PackagingOption;
import shop.bluebooktle.backend.book_order.repository.BookOrderRepository;
import shop.bluebooktle.backend.book_order.repository.OrderPackagingRepository;
import shop.bluebooktle.backend.book_order.repository.PackagingOptionRepository;
import shop.bluebooktle.backend.book_order.service.impl.OrderPackagingServiceImpl;
import shop.bluebooktle.backend.order.entity.Order;
import shop.bluebooktle.common.dto.book_order.request.OrderPackagingRegisterRequest;
import shop.bluebooktle.common.dto.book_order.request.OrderPackagingUpdateRequest;
import shop.bluebooktle.common.dto.book_order.response.OrderPackagingResponse;
import shop.bluebooktle.common.exception.book_order.BookOrderNotFoundException;
import shop.bluebooktle.common.exception.book_order.OrderPackagingNotFoundException;
import shop.bluebooktle.common.exception.book_order.PackagingOptionNotFoundException;
import shop.bluebooktle.common.exception.book_order.PackagingQuantityExceedException;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class OrderPackagingServiceTest {

	@InjectMocks
	private OrderPackagingServiceImpl service;

	@Mock
	private OrderPackagingRepository orderPackagingRepository;
	@Mock
	private BookOrderRepository bookOrderRepository;
	@Mock
	private PackagingOptionRepository packagingOptionRepository;

	@Test
	@DisplayName("포장 옵션 추가 - 성공")
	void addOrderPackaging_success() {
		// given
		Order orderMock = mock(Order.class);
		ReflectionTestUtils.setField(orderMock, "id", 99L); // 필드에 값을 강제로 주입해 주입
		Book bookMock = mock(Book.class);
		ReflectionTestUtils.setField(bookMock, "id", 88L);

		BookOrder bookOrder = BookOrder.builder()
			.order(orderMock)
			.book(bookMock)
			.quantity(5)
			.price(BigDecimal.ZERO)
			.build();
		ReflectionTestUtils.setField(bookOrder, "id", 1L); // BookOrder id 주입
		given(bookOrderRepository.findById(1L)).willReturn(Optional.of(bookOrder));

		PackagingOption option = PackagingOption.builder().build();
		ReflectionTestUtils.setField(option, "id", 2L);
		given(packagingOptionRepository.findById(2L)).willReturn(Optional.of(option));

		// 비교하기 위한, 저장될 OrderPackaging 객체
		OrderPackaging saved = OrderPackaging.builder()
			.bookOrder(bookOrder)
			.packagingOption(option)
			.quantity(3)
			.build();
		ReflectionTestUtils.setField(saved, "id", 10L);
		ReflectionTestUtils.setField(saved, "createdAt", LocalDateTime.now());
		given(orderPackagingRepository.save(any())).willReturn(saved);

		// 요청 DTO 생성
		OrderPackagingRegisterRequest request = OrderPackagingRegisterRequest.builder()
			.packagingOptionId(2L)
			.quantity(3)
			.build();

		// when
		OrderPackagingResponse response = service.addOrderPackaging(1L, request);

		// then
		assertThat(response.getOrderPackagingId()).isEqualTo(10L);
		assertThat(response.getBookOrderId()).isEqualTo(1L);
		assertThat(response.getPackagingOptionId()).isEqualTo(2L);
		assertThat(response.getQuantity()).isEqualTo(3);
		then(orderPackagingRepository).should().save(any());
	}

	@Test
	@DisplayName("포장 옵션 추가 - 주문 조회 실패")
	void addOrderPackaging_bookNotFound() {
		given(bookOrderRepository.findById(anyLong())).willReturn(Optional.empty());
		OrderPackagingRegisterRequest req = OrderPackagingRegisterRequest.builder()
			.packagingOptionId(1L)
			.quantity(1)
			.build();

		assertThatThrownBy(() -> service.addOrderPackaging(99L, req))
			.isInstanceOf(BookOrderNotFoundException.class);
		then(orderPackagingRepository).should(never()).save(any());
	}

	@Test
	@DisplayName("포장 옵션 추가 - 옵션 조회 실패")
	void addOrderPackaging_optionNotFound() {
		Order orderMock = mock(Order.class);
		ReflectionTestUtils.setField(orderMock, "id", 99L);
		BookOrder bookOrder = BookOrder.builder()
			.order(orderMock)
			.book(mock(Book.class))
			.quantity(5)
			.price(BigDecimal.ZERO)
			.build();
		ReflectionTestUtils.setField(bookOrder, "id", 1L);
		given(bookOrderRepository.findById(1L)).willReturn(Optional.of(bookOrder));
		given(packagingOptionRepository.findById(anyLong())).willReturn(Optional.empty());

		OrderPackagingRegisterRequest req = OrderPackagingRegisterRequest.builder()
			.packagingOptionId(99L)
			.quantity(1)
			.build();

		assertThatThrownBy(() -> service.addOrderPackaging(1L, req))
			.isInstanceOf(PackagingOptionNotFoundException.class);
		then(orderPackagingRepository).should(never()).save(any());
	}

	@Test
	@DisplayName("포장 옵션 추가 - 수량 초과 예외")
	void addOrderPackaging_quantityExceed() {
		Order orderMock = mock(Order.class);
		ReflectionTestUtils.setField(orderMock, "id", 99L);
		BookOrder bookOrder = BookOrder.builder()
			.order(orderMock)
			.book(mock(Book.class))
			.quantity(2) // 주문한 책 수량: 2
			.price(BigDecimal.ZERO)
			.build();
		ReflectionTestUtils.setField(bookOrder, "id", 1L);
		given(bookOrderRepository.findById(1L)).willReturn(Optional.of(bookOrder));

		PackagingOption option = PackagingOption.builder().build();
		ReflectionTestUtils.setField(option, "id", 2L);
		given(packagingOptionRepository.findById(2L)).willReturn(Optional.of(option));

		OrderPackagingRegisterRequest request = OrderPackagingRegisterRequest.builder()
			.packagingOptionId(2L)
			.quantity(5) // 요청 수량: 5
			.build();

		assertThatThrownBy(() -> service.addOrderPackaging(1L, request))
			.isInstanceOf(PackagingQuantityExceedException.class);
		then(orderPackagingRepository).should(never()).save(any());
	}

	@Test
	@DisplayName("포장 옵션 조회 - 성공")
	void getOrderPackaging_success() {
		// given
		Order orderMock = mock(Order.class);
		ReflectionTestUtils.setField(orderMock, "id", 99L);
		Book bookMock = mock(Book.class);
		ReflectionTestUtils.setField(bookMock, "id", 88L);
		BookOrder bookOrder = BookOrder.builder()
			.order(orderMock)
			.book(bookMock)
			.quantity(1)
			.price(BigDecimal.ZERO)
			.build();
		ReflectionTestUtils.setField(bookOrder, "id", 3L);

		PackagingOption option = PackagingOption.builder().build();
		ReflectionTestUtils.setField(option, "id", 4L);
		OrderPackaging op = OrderPackaging.builder()
			.bookOrder(bookOrder)
			.packagingOption(option)
			.quantity(2)
			.build();
		ReflectionTestUtils.setField(op, "id", 7L);
		ReflectionTestUtils.setField(op, "createdAt", LocalDateTime.now());
		given(orderPackagingRepository.findById(7L)).willReturn(Optional.of(op));

		// when
		OrderPackagingResponse resp = service.getOrderPackaging(7L);

		// then
		assertThat(resp.getOrderPackagingId()).isEqualTo(7L);
		assertThat(resp.getBookOrderId()).isEqualTo(3L);
		assertThat(resp.getPackagingOptionId()).isEqualTo(4L);
		assertThat(resp.getQuantity()).isEqualTo(2);
	}

	@Test
	@DisplayName("포장 옵션 조회 - 실패")
	void getOrderPackaging_notFound() {
		given(orderPackagingRepository.findById(anyLong())).willReturn(Optional.empty());
		assertThatThrownBy(() -> service.getOrderPackaging(100L))
			.isInstanceOf(OrderPackagingNotFoundException.class);
	}

	@Test
	@DisplayName("포장 옵션 수정 - 성공")
	void updateOrderPackaging_success() {
		Order orderMock = mock(Order.class);
		ReflectionTestUtils.setField(orderMock, "id", 99L);
		Book bookMock = mock(Book.class);
		ReflectionTestUtils.setField(bookMock, "id", 88L);
		BookOrder bookOrder = BookOrder.builder()
			.order(orderMock)
			.book(bookMock)
			.quantity(1)
			.price(BigDecimal.ZERO)
			.build();
		ReflectionTestUtils.setField(bookOrder, "id", 6L);

		PackagingOption existingOption = PackagingOption.builder().build();
		ReflectionTestUtils.setField(existingOption, "id", 1L);
		OrderPackaging op = OrderPackaging.builder()
			.bookOrder(bookOrder)
			.packagingOption(existingOption)
			.quantity(1)
			.build();
		ReflectionTestUtils.setField(op, "id", 5L);
		given(orderPackagingRepository.findById(5L)).willReturn(Optional.of(op));

		PackagingOption newOpt = PackagingOption.builder().build();
		ReflectionTestUtils.setField(newOpt, "id", 9L);
		given(packagingOptionRepository.findById(9L)).willReturn(Optional.of(newOpt));

		OrderPackagingUpdateRequest req = OrderPackagingUpdateRequest.builder()
			.packagingOptionId(9L)
			.quantity(3)
			.build();

		// when
		OrderPackagingResponse resp = service.updateOrderPackaging(5L, req);

		// then
		assertThat(resp.getPackagingOptionId()).isEqualTo(9L);
		assertThat(resp.getQuantity()).isEqualTo(3);
	}

	@Test
	@DisplayName("포장 옵션 수정 - 옵션 미존재 실패")
	void updateOrderPackaging_notExist() {
		OrderPackagingUpdateRequest req = OrderPackagingUpdateRequest.builder()
			.packagingOptionId(1L)
			.quantity(1)
			.build();

		given(orderPackagingRepository.findById(anyLong())).willReturn(Optional.empty());
		assertThatThrownBy(() -> service.updateOrderPackaging(123L, req))
			.isInstanceOf(OrderPackagingNotFoundException.class);
	}

	@Test
	@DisplayName("포장 옵션 수정 - 옵션 찾기 실패")
	void updateOrderPackaging_notFound() {
		OrderPackagingUpdateRequest req = OrderPackagingUpdateRequest.builder()
			.packagingOptionId(1L)
			.quantity(1)
			.build();

		OrderPackaging op = OrderPackaging.builder().build();
		ReflectionTestUtils.setField(op, "id", 5L);
		given(orderPackagingRepository.findById(5L)).willReturn(Optional.of(op));
		given(packagingOptionRepository.findById(anyLong())).willReturn(Optional.empty());
		assertThatThrownBy(() -> service.updateOrderPackaging(5L, req))
			.isInstanceOf(PackagingOptionNotFoundException.class);
	}

	@Test
	@DisplayName("포장 옵션 삭제 - 성공")
	void deleteOrderPackaging_success() {
		OrderPackaging op = OrderPackaging.builder().build();
		ReflectionTestUtils.setField(op, "id", 8L);
		given(orderPackagingRepository.findById(8L)).willReturn(Optional.of(op));

		// when
		service.deleteOrderPackaging(8L);

		// then
		then(orderPackagingRepository).should().delete(op);
	}

	@Test
	@DisplayName("포장 옵션 삭제 - 실패")
	void deleteOrderPackaging_notFound() {
		given(orderPackagingRepository.findById(anyLong())).willReturn(Optional.empty());
		assertThatThrownBy(() -> service.deleteOrderPackaging(77L))
			.isInstanceOf(OrderPackagingNotFoundException.class);
	}
}
