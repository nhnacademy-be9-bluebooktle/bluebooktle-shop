package shop.bluebooktle.backend.book_order.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.bluebooktle.backend.book_order.service.BookOrderService;
import shop.bluebooktle.backend.book_order.service.OrderPackagingService;
import shop.bluebooktle.common.dto.book_order.request.BookOrderRegisterRequest;
import shop.bluebooktle.common.dto.book_order.request.BookOrderUpdateRequest;
import shop.bluebooktle.common.dto.book_order.request.OrderPackagingRegisterRequest;
import shop.bluebooktle.common.dto.book_order.request.OrderPackagingUpdateRequest;
import shop.bluebooktle.common.dto.book_order.response.BookOrderResponse;
import shop.bluebooktle.common.dto.book_order.response.OrderPackagingResponse;
import shop.bluebooktle.common.security.AuthUserLoader;
import shop.bluebooktle.common.util.JwtUtil;

@WebMvcTest(controllers = BookOrderController.class)
@WithMockUser
class BookOrderControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private BookOrderService bookOrderService;

	@MockitoBean
	private OrderPackagingService orderPackagingService;

	@MockitoBean
	private JwtUtil jwtUtil;

	@MockitoBean
	private AuthUserLoader authUserLoader;

	@Test
	@DisplayName("도서 주문 등록 - 성공")
	void createBookOrder_success() throws Exception {
		BookOrderRegisterRequest req = BookOrderRegisterRequest.builder()
			.bookId(10L)
			.orderId(20L)
			.quantity(3)
			.price(BigDecimal.valueOf(4500))
			.build();

		BookOrderResponse resp = BookOrderResponse.builder()
			.bookOrderId(1L)
			.orderId(10L)
			.bookId(20L)
			.quantity(3)
			.price(BigDecimal.valueOf(4500))
			.build();

		given(bookOrderService.createBookOrder(any(BookOrderRegisterRequest.class)))
			.willReturn(resp);

		mockMvc.perform(post("/api/book-orders")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req))
				.with(csrf()))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.bookOrderId").value(1))
			.andExpect(jsonPath("$.data.quantity").value(3));

		then(bookOrderService).should().createBookOrder(any(BookOrderRegisterRequest.class));
	}

	@Test
	@DisplayName("도서 주문 단건 조회 - 성공")
	void getBookOrder_success() throws Exception {
		BookOrderResponse resp = BookOrderResponse.builder()
			.bookOrderId(2L)
			.orderId(11L)
			.bookId(21L)
			.quantity(5)
			.price(BigDecimal.valueOf(7500))
			.build();

		given(bookOrderService.getBookOrder(2L)).willReturn(resp);

		mockMvc.perform(get("/api/book-orders/{id}", 2L))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.bookOrderId").value(2))
			.andExpect(jsonPath("$.data.price").value(7500));

		then(bookOrderService).should().getBookOrder(2L);
	}

	@Test
	@DisplayName("도서 주문 수정 - 성공")
	void updateBookOrder_success() throws Exception {
		BookOrderUpdateRequest request = BookOrderUpdateRequest.builder()
			.bookId(20L)
			.orderId(10L)
			.quantity(10)
			.price(BigDecimal.valueOf(5000))
			.build();

		BookOrderResponse responseDto = BookOrderResponse.builder()
			.bookOrderId(3L)
			.orderId(10L)
			.bookId(20L)
			.quantity(10)
			.price(BigDecimal.valueOf(5000))
			.build();

		given(bookOrderService.updateBookOrder(eq(3L), any(BookOrderUpdateRequest.class)))
			.willReturn(responseDto);

		mockMvc.perform(put("/api/book-orders/{bookOrderId}", 3L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.bookOrderId").value(3))
			.andExpect(jsonPath("$.data.bookId").value(20))
			.andExpect(jsonPath("$.data.orderId").value(10))
			.andExpect(jsonPath("$.data.quantity").value(10))
			.andExpect(jsonPath("$.data.price").value(5000));

		then(bookOrderService).should()
			.updateBookOrder(eq(3L), any(BookOrderUpdateRequest.class));
	}

	@Test
	@DisplayName("도서 주문 삭제 - 성공")
	void deleteBookOrder_success() throws Exception {
		willDoNothing().given(bookOrderService).deleteBookOrder(4L);

		mockMvc.perform(delete("/api/book-orders/{id}", 4L)
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"));

		then(bookOrderService).should().deleteBookOrder(4L);
	}

	@Test
	@DisplayName("포장 추가 - 성공")
	void addPackaging_success() throws Exception {
		OrderPackagingRegisterRequest req = OrderPackagingRegisterRequest.builder()
			.packagingOptionId(7L)
			.quantity(2)
			.build();

		LocalDateTime now = LocalDateTime.now();
		OrderPackagingResponse resp = OrderPackagingResponse.builder()
			.orderPackagingId(10L)
			.bookOrderId(5L)
			.packagingOptionId(7L)
			.quantity(2)
			.createdAt(now)
			.build();

		given(orderPackagingService.addOrderPackaging(eq(5L), any(OrderPackagingRegisterRequest.class)))
			.willReturn(resp);

		mockMvc.perform(post("/api/book-orders/{id}/packaging", 5L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req))
				.with(csrf()))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.orderPackagingId").value(10));

		then(orderPackagingService).should().addOrderPackaging(eq(5L), any(OrderPackagingRegisterRequest.class));
	}

	@Test
	@DisplayName("포장 단건 조회 - 성공")
	void getPackaging_success() throws Exception {
		LocalDateTime now = LocalDateTime.now();
		OrderPackagingResponse resp = OrderPackagingResponse.builder()
			.orderPackagingId(11L)
			.bookOrderId(6L)
			.packagingOptionId(8L)
			.quantity(1)
			.createdAt(now)
			.build();

		given(orderPackagingService.getOrderPackaging(11L)).willReturn(resp);

		mockMvc.perform(get("/api/book-orders/packagings/{id}", 11L))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.packagingOptionId").value(8));

		then(orderPackagingService).should().getOrderPackaging(11L);
	}

	@Test
	@DisplayName("포장 수정 - 성공")
	void updatePackaging_success() throws Exception {
		OrderPackagingUpdateRequest req = OrderPackagingUpdateRequest.builder()
			.packagingOptionId(9L)
			.quantity(3)
			.build();

		LocalDateTime now = LocalDateTime.now();
		OrderPackagingResponse resp = OrderPackagingResponse.builder()
			.orderPackagingId(12L)
			.bookOrderId(7L)
			.packagingOptionId(9L)
			.quantity(3)
			.createdAt(now)
			.build();

		given(orderPackagingService.updateOrderPackaging(eq(12L), any(OrderPackagingUpdateRequest.class)))
			.willReturn(resp);

		mockMvc.perform(put("/api/book-orders/packagings/{id}", 12L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.quantity").value(3));

		then(orderPackagingService).should().updateOrderPackaging(eq(12L), any(OrderPackagingUpdateRequest.class));
	}

	@Test
	@DisplayName("포장 삭제 - 성공")
	void deletePackaging_success() throws Exception {
		willDoNothing().given(orderPackagingService).deleteOrderPackaging(13L);

		mockMvc.perform(delete("/api/book-orders/packagings/{id}", 13L)
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"));

		then(orderPackagingService).should().deleteOrderPackaging(13L);
	}
}
