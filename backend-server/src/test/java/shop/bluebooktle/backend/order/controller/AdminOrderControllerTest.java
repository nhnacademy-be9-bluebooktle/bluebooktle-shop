package shop.bluebooktle.backend.order.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.bluebooktle.backend.order.service.OrderService;
import shop.bluebooktle.common.domain.order.OrderStatus;
import shop.bluebooktle.common.dto.order.request.AdminOrderSearchRequest;
import shop.bluebooktle.common.dto.order.request.AdminOrderStatusUpdateRequest;
import shop.bluebooktle.common.dto.order.request.AdminOrderTrackingNumberUpdateRequest;
import shop.bluebooktle.common.dto.order.response.AdminOrderDetailResponse;
import shop.bluebooktle.common.dto.order.response.AdminOrderListResponse;
import shop.bluebooktle.common.dto.order.response.OrderItemResponse;
import shop.bluebooktle.common.security.AuthUserLoader;
import shop.bluebooktle.common.util.JwtUtil;

@ActiveProfiles("test")
@WebMvcTest(controllers = AdminOrderController.class)
public class AdminOrderControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private OrderService orderService;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private JwtUtil jwtUtil;

	@MockitoBean
	private AuthUserLoader authUserLoader;

	@Test
	@DisplayName("관리자 주문 목록 조회 - 성공")
	@WithMockUser(roles = "ADMIN")
	void searchOrders_success() throws Exception {
		AdminOrderListResponse order1 = new AdminOrderListResponse(
			1L,
			"ORD-123",
			LocalDateTime.now(),
			"홍길동",
			"testuser",
			"이순신",
			new BigDecimal("10000"),
			OrderStatus.PENDING,
			"CARD"
		);
		Page<AdminOrderListResponse> page = new PageImpl<>(List.of(order1), PageRequest.of(0, 10), 1);
		given(orderService.searchOrders(any(AdminOrderSearchRequest.class), any(Pageable.class)))
			.willReturn(page);

		mockMvc.perform(get("/api/admin/orders")
				.param("page", "0")
				.param("size", "10")
				.param("orderStatus", "PENDING")
				.param("orderCode", "ORD-123"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.content").isArray())
			.andExpect(jsonPath("$.data.content[0].orderKey").value("ORD-123"))
			.andExpect(jsonPath("$.data.content[0].ordererLoginId").value("testuser"));

		verify(orderService).searchOrders(any(AdminOrderSearchRequest.class), any(Pageable.class));
	}

	@Test
	@DisplayName("관리자 주문 상세 조회 - 성공")
	@WithMockUser(roles = "ADMIN")
	void getAdminOrderDetail_success() throws Exception {
		Long orderId = 1L;

		OrderItemResponse orderItem = OrderItemResponse.builder()
			.bookOrderId(101L)
			.bookId(201L)
			.bookTitle("테스트 책 제목")
			.bookThumbnailUrl("http://example.com/thumbnail.jpg")
			.quantity(2)
			.price(new BigDecimal("5000"))
			.packagingOptions(Collections.emptyList())
			.appliedItemCoupons(Collections.emptyList())
			.finalItemPrice(new BigDecimal("10000"))
			.build();

		AdminOrderDetailResponse detailResponse = new AdminOrderDetailResponse(
			orderId,
			"ORD-123",
			LocalDateTime.now(),
			"홍길동",
			"testuser",
			"010-1234-5678",
			"orderer@example.com",
			"이순신",
			"010-8765-4321",
			"receiver@example.com",
			"12345",
			"서울시 강남구",
			"테스트 상세 주소",
			List.of(orderItem),
			"CARD",
			new BigDecimal("10000"),
			new BigDecimal("0"),
			new BigDecimal("0"),
			new BigDecimal("0"),
			new BigDecimal("3000"),
			new BigDecimal("13000"),
			OrderStatus.PENDING,
			"송장번호123",
			null,
			null
		);
		given(orderService.getAdminOrderDetail(orderId)).willReturn(detailResponse);

		mockMvc.perform(get("/api/admin/orders/{orderId}", orderId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.orderKey").value("ORD-123"))
			.andExpect(jsonPath("$.data.ordererLoginId").value("testuser"))
			.andExpect(jsonPath("$.data.orderItems").isArray())
			.andExpect(jsonPath("$.data.orderItems[0].bookTitle").value("테스트 책 제목"));

		verify(orderService).getAdminOrderDetail(orderId);
	}

	@Test
	@DisplayName("주문 상태 업데이트 - 성공")
	@WithMockUser(roles = "ADMIN")
	void updateOrderStatus_success() throws Exception {
		Long orderId = 1L;
		AdminOrderStatusUpdateRequest request = new AdminOrderStatusUpdateRequest(OrderStatus.COMPLETED);

		doNothing().when(orderService).updateOrderStatus(eq(orderId), eq(OrderStatus.COMPLETED));

		mockMvc.perform(post("/api/admin/orders/{orderId}/update-status", orderId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"));

		verify(orderService).updateOrderStatus(eq(orderId), eq(OrderStatus.COMPLETED));
	}

	@Test
	@DisplayName("운송장 번호 업데이트 - 성공")
	@WithMockUser(roles = "ADMIN")
	void updateTrackingNumber_success() throws Exception {
		Long orderId = 1L;
		AdminOrderTrackingNumberUpdateRequest request = new AdminOrderTrackingNumberUpdateRequest("NEW_TRACKING_123");

		doNothing().when(orderService).updateOrderTrackingNumber(eq(orderId), eq("NEW_TRACKING_123"));

		mockMvc.perform(post("/api/admin/orders/{orderId}/update-tracking", orderId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"));

		verify(orderService).updateOrderTrackingNumber(eq(orderId), eq("NEW_TRACKING_123"));
	}
}