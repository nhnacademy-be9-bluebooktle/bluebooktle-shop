package shop.bluebooktle.frontend.controller.admin;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import shop.bluebooktle.common.domain.order.AdminOrderSearchType;
import shop.bluebooktle.common.domain.order.OrderStatus;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.order.request.AdminOrderSearchRequest;
import shop.bluebooktle.common.dto.order.request.AdminOrderStatusUpdateRequest;
import shop.bluebooktle.common.dto.order.request.AdminOrderTrackingNumberUpdateRequest;
import shop.bluebooktle.common.dto.order.response.AdminOrderDetailResponse;
import shop.bluebooktle.frontend.config.advice.GlobalCartCountAdvice;
import shop.bluebooktle.frontend.config.advice.GlobalCategoryInfoAdvice;
import shop.bluebooktle.frontend.config.advice.GlobalUserInfoAdvice;
import shop.bluebooktle.frontend.service.AdminOrderService;

@WebMvcTest(
	controllers = AdminOrderController.class,
	excludeFilters = {
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = GlobalUserInfoAdvice.class),
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = GlobalCartCountAdvice.class),
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = GlobalCategoryInfoAdvice.class)
	}
)
@TestPropertySource(properties = {
	"server.gateway-url=http://localhost:8080",
	"minio.endpoint=http://localhost:9000", // 더미값
	"toss.client-key=testClientKey"
})
class AdminOrderControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private AdminOrderService adminOrderService;

	@Test
	@DisplayName("GET /admin/orders — 리스트 정상")
	void listOrders() throws Exception {
		// 빈 페이지를 반환하도록 stub
		given(adminOrderService.searchOrders(any(AdminOrderSearchRequest.class), any()))
			.willReturn(new PaginationData<>(
				new PageImpl<>(List.of(), PageRequest.of(0, 10), 0)
			));

		mockMvc.perform(get("/admin/orders"))
			.andExpect(status().isOk())
			.andExpect(view().name("admin/order/order_list"))
			.andExpect(model().attribute("pageTitle", "주문 관리"))
			.andExpect(model().attributeExists("currentURI"))
			.andExpect(model().attributeExists("orderPage"))
			.andExpect(model().attributeExists("searchCriteria"))
			.andExpect(model().attribute("orderStatusOptions", OrderStatus.values()))
			.andExpect(model().attribute("paymentMethodOptions",
				List.of("전체", "신용카드", "가상계좌", "카카오페이", "네이버페이")))
			.andExpect(model().attribute("searchKeywordTypes", AdminOrderSearchType.values()));
	}

	@Test
	@DisplayName("GET /admin/orders/{id} — 상세 정상")
	void viewOrder() throws Exception {
		var detail = new AdminOrderDetailResponse(
			5L,                                         // orderId
			"ORD5",                                     // orderKey
			LocalDateTime.of(2025, 1, 2, 12, 0),        // orderDate
			"홍길동",                                   // ordererName
			"user1",                                    // ordererLoginId
			"010-1111-2222",                            // ordererPhoneNumber
			"hong@example.com",                         // ordererEmail
			"이영희",                                   // receiverName
			"010-3333-4444",                            // receiverPhoneNumber
			"lee@example.com",                          // receiverEmail
			"12345",                                    // postalCode
			"서울시 강남구",                            // address
			"역삼동 123-45",                             // detailAddress
			List.of(),                                  // orderItems
			"card",                                     // paymentMethod
			BigDecimal.valueOf(10000),                  // productAmount
			BigDecimal.ZERO,                            // totalPackagingFee
			BigDecimal.ZERO,                            // pointUserAmount
			BigDecimal.ZERO,                            // couponDiscountAmount
			BigDecimal.valueOf(3000),                   // deliveryFee
			BigDecimal.valueOf(13000),                  // paidAmount
			OrderStatus.PENDING,                        // orderStatus
			"TRK123",                                   // trackingNumber
			null,                                       // refundReason
			null                                        // refundReasonDetail
		);
		given(adminOrderService.getOrderDetail(5L)).willReturn(detail);

		mockMvc.perform(get("/admin/orders/5"))
			.andExpect(status().isOk())
			.andExpect(view().name("admin/order/order_detail"))
			.andExpect(model().attributeExists("currentURI"))
			.andExpect(model().attribute("pageTitle", "주문 상세 (번호: 5)"))
			.andExpect(model().attribute("order", detail))
			.andExpect(model().attribute("updatableOrderStatuses",
				List.of(OrderStatus.values()).stream()
					.filter(s -> s != OrderStatus.RETURNED_REQUEST
						&& s != OrderStatus.RETURNED
						&& s != OrderStatus.CANCELED)
					.toList()
			));
	}

	@Test
	@DisplayName("POST /admin/orders/{id}/update-status — 성공")
	void updateOrderStatus_success() throws Exception {
		mockMvc.perform(post("/admin/orders/7/update-status")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("status", OrderStatus.COMPLETED.name())
			)
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/admin/orders/7"))
			.andExpect(flash().attribute("globalSuccessMessage",
				"주문(ID: 7) 상태가 '" + OrderStatus.COMPLETED.getDescription() + "'(으)로 성공적으로 변경되었습니다."));
		then(adminOrderService).should().updateOrderStatus(eq(7L), any(AdminOrderStatusUpdateRequest.class));
	}

	@Test
	@DisplayName("POST /admin/orders/{id}/update-status — 예외")
	void updateOrderStatus_exception() throws Exception {
		willThrow(new RuntimeException("err"))
			.given(adminOrderService).updateOrderStatus(eq(4L), any());

		mockMvc.perform(post("/admin/orders/4/update-status")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("status", OrderStatus.CANCELED.name())
			)
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/admin/orders/4"))
			.andExpect(flash().attribute("globalErrorMessage",
				"주문 상태 변경 중 오류가 발생했습니다: err"));
	}

	@Test
	@DisplayName("POST /admin/orders/{id}/update-tracking — 성공")
	void updateTracking_success() throws Exception {
		mockMvc.perform(post("/admin/orders/9/update-tracking")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("trackingNumber", "TN123")
			)
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/admin/orders/9"))
			.andExpect(flash().attribute("globalSuccessMessage",
				"주문(ID: 9)의 운송장 번호가 성공적으로 등록/수정되었습니다."));
		then(adminOrderService).should().updateOrderTrackingNumber(eq(9L), any(AdminOrderTrackingNumberUpdateRequest.class));
	}

	@Test
	@DisplayName("POST /admin/orders/{id}/update-tracking — 예외")
	void updateTracking_exception() throws Exception {
		willThrow(new RuntimeException("fail"))
			.given(adminOrderService).updateOrderTrackingNumber(eq(3L), any());

		mockMvc.perform(post("/admin/orders/3/update-tracking")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("trackingNumber", "T123")
			)
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/admin/orders/3"))
			.andExpect(flash().attribute("globalErrorMessage",
				"운송장 번호 업데이트 중 오류가 발생했습니다: fail"));
	}

}
