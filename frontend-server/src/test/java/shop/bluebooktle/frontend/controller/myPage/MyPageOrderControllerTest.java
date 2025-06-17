package shop.bluebooktle.frontend.controller.myPage;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import shop.bluebooktle.common.domain.order.OrderStatus;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.order.response.OrderDetailResponse;
import shop.bluebooktle.common.dto.order.response.OrderHistoryResponse;
import shop.bluebooktle.common.dto.payment.request.PaymentCancelRequest;
import shop.bluebooktle.frontend.service.OrderService;
import shop.bluebooktle.frontend.service.PaymentsService;

@ExtendWith(MockitoExtension.class)
class MyPageOrderControllerTest {

	@InjectMocks
	private MyPageOrderController myPageOrderController;

	@Mock
	private OrderService orderService;

	@Mock
	private PaymentsService paymentsService;

	@Mock
	private Model model;

	@Mock
	private RedirectAttributes redirectAttributes;

	private PaginationData<OrderHistoryResponse> mockPaginationData;
	private OrderDetailResponse mockOrderDetailResponse;
	private PaymentCancelRequest mockPaymentCancelRequest;

	@BeforeEach
	void setUp() {
		OrderHistoryResponse orderHistoryResponse = new OrderHistoryResponse(
			1L,
			LocalDateTime.now(),
			"테스트 상품 외 1개",
			BigDecimal.valueOf(10000),
			"ORD123",
			OrderStatus.COMPLETED,
			"http://example.com/thumbnail.jpg"
		);

		PaginationData.PaginationInfo paginationInfo = new PaginationData.PaginationInfo(
			1,
			1L,
			0,
			7,
			true,
			true,
			false,
			false
		);

		mockPaginationData = new PaginationData<>(
			Collections.singletonList(orderHistoryResponse),
			paginationInfo
		);

		mockOrderDetailResponse = new OrderDetailResponse(
			1L,
			"ORD123",
			LocalDateTime.now(),
			"주문자 이름",
			BigDecimal.valueOf(10000),
			"카드",
			OrderStatus.COMPLETED,
			"수령인 이름",
			"01099998888",
			"receiver@example.com",
			"서울시 강남구",
			"역삼동 123-45",
			"06130",
			Collections.emptyList(),
			BigDecimal.valueOf(10000),
			BigDecimal.ZERO,
			BigDecimal.ZERO,
			BigDecimal.valueOf(3000),
			"TRACK12345",
			BigDecimal.ZERO
		);

		mockPaymentCancelRequest = new PaymentCancelRequest("PAY123", "주문 취소");
	}

	@Test
	@DisplayName("userOrdersPage: 주문 내역 페이지 성공적으로 로드")
	void userOrdersPage_success() {
		// Given
		int page = 0;
		int size = 7;
		OrderStatus status = null;

		when(orderService.getOrderHistory(page, size, status)).thenReturn(mockPaginationData);

		// When
		String viewName = myPageOrderController.userOrdersPage(page, size, status, model);

		// Then
		assertThat(viewName).isEqualTo("mypage/order_list");
		verify(orderService).getOrderHistory(page, size, status);
		verify(model).addAttribute("ordersPage", mockPaginationData);
		verify(model).addAttribute("status", status);
	}

	@Test
	@DisplayName("userOrdersPage: 특정 상태로 주문 내역 조회")
	void userOrdersPage_withStatusFilter() {
		// Given
		int page = 0;
		int size = 7;
		OrderStatus status = OrderStatus.SHIPPING;

		when(orderService.getOrderHistory(page, size, status)).thenReturn(mockPaginationData);

		// When
		String viewName = myPageOrderController.userOrdersPage(page, size, status, model);

		// Then
		assertThat(viewName).isEqualTo("mypage/order_list");
		verify(orderService).getOrderHistory(page, size, status);
		verify(model).addAttribute("ordersPage", mockPaginationData);
		verify(model).addAttribute("status", status);
	}

	@Test
	@DisplayName("cancelOrder: 주문 취소 성공")
	void cancelOrder_success() {
		// Given
		String orderKey = "ORD123";
		doNothing().when(orderService).cancelOrder(orderKey);

		// When
		String viewName = myPageOrderController.cancelOrder(orderKey, redirectAttributes);

		// Then
		assertThat(viewName).isEqualTo("redirect:/mypage/orders/" + orderKey);
		verify(orderService).cancelOrder(orderKey);
		verifyNoInteractions(redirectAttributes);
	}

	@Test
	@DisplayName("cancelOrder: 주문 취소 중 예외 발생")
	void cancelOrder_exception() {
		// Given
		String orderKey = "ORD123";
		String errorMessage = "주문 취소 실패";
		doThrow(new RuntimeException(errorMessage)).when(orderService).cancelOrder(orderKey);

		// When
		String viewName = myPageOrderController.cancelOrder(orderKey, redirectAttributes);

		// Then
		assertThat(viewName).isEqualTo("redirect:/mypage/orders/" + orderKey);
		verify(orderService).cancelOrder(orderKey);
		verify(redirectAttributes).addFlashAttribute("globalErrorMessage", errorMessage);
	}

	@Test
	@DisplayName("orderDetailPage: 주문 상세 페이지 성공적으로 로드")
	void orderDetailPage_success() {
		// Given
		String orderKey = "ORD123";
		when(orderService.getOrderDetailByOrderKey(orderKey)).thenReturn(mockOrderDetailResponse);

		// When
		ModelAndView mav = myPageOrderController.orderDetailPage(orderKey, redirectAttributes);

		// Then
		assertThat(mav.getViewName()).isEqualTo("mypage/order_detail");
		assertThat(mav.getModel()).containsKey("order");
		assertThat(mav.getModel().get("order")).isEqualTo(mockOrderDetailResponse);
		assertThat(mav.getModel()).containsKey("isMember");
		assertThat(mav.getModel().get("isMember")).isEqualTo(true);
		verify(orderService).getOrderDetailByOrderKey(orderKey);
		verifyNoInteractions(redirectAttributes);
	}

	@Test
	@DisplayName("orderDetailPage: 주문 상세 페이지 로드 중 예외 발생")
	void orderDetailPage_exception() {
		// Given
		String orderKey = "ORD123";
		String errorMessage = "주문 정보를 찾을 수 없습니다.";
		doThrow(new RuntimeException(errorMessage)).when(orderService).getOrderDetailByOrderKey(orderKey);

		// When
		ModelAndView mav = myPageOrderController.orderDetailPage(orderKey, redirectAttributes);

		// Then
		assertThat(mav.getViewName()).isEqualTo("redirect:/");
		verify(orderService).getOrderDetailByOrderKey(orderKey);
		verify(redirectAttributes).addFlashAttribute("globalErrorMessage", errorMessage);
		assertThat(mav.getModel()).doesNotContainKey("order");
	}

	@Test
	@DisplayName("paymentCancel: 결제 취소 성공")
	void paymentCancel_success() {
		// Given
		String orderKey = "ORD123";
		doNothing().when(paymentsService).cancel(any(PaymentCancelRequest.class));

		// When
		String viewName = myPageOrderController.paymentCancel(orderKey, mockPaymentCancelRequest, redirectAttributes);

		// Then
		assertThat(viewName).isEqualTo("redirect:/mypage/orders/" + orderKey);
		verify(paymentsService).cancel(mockPaymentCancelRequest);
		verifyNoInteractions(redirectAttributes);
	}

	@Test
	@DisplayName("paymentCancel: 결제 취소 중 예외 발생")
	void paymentCancel_exception() {
		// Given
		String orderKey = "ORD123";
		String errorMessage = "결제 취소 실패";
		doThrow(new RuntimeException(errorMessage)).when(paymentsService).cancel(any(PaymentCancelRequest.class));

		// When
		String viewName = myPageOrderController.paymentCancel(orderKey, mockPaymentCancelRequest, redirectAttributes);

		// Then
		assertThat(viewName).isEqualTo("redirect:/mypage/orders/" + orderKey);
		verify(paymentsService).cancel(mockPaymentCancelRequest);
		verify(redirectAttributes).addFlashAttribute("globalErrorMessage", errorMessage);
	}
}