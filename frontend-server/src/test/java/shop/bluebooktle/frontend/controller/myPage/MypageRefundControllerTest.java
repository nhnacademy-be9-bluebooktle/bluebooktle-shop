package shop.bluebooktle.frontend.controller.myPage;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import shop.bluebooktle.common.domain.refund.RefundReason;
import shop.bluebooktle.common.dto.refund.request.RefundCreateRequest;
import shop.bluebooktle.frontend.service.RefundService;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class MypageRefundControllerTest {

	@InjectMocks
	private MypageRefundController mypageRefundController;

	@Mock
	private RefundService refundService;

	@Mock
	private BindingResult bindingResult;

	@Mock
	private RedirectAttributes redirectAttributes;

	private RefundCreateRequest mockRefundCreateRequest;

	@BeforeEach
	void setUp() {
		mockRefundCreateRequest = new RefundCreateRequest("ORD123", RefundReason.CHANGE_OF_MIND, "상품이 마음에 들지 않습니다.");
	}

	@Test
	@DisplayName("processRefundRequest: 성공적인 환불 요청")
	void processRefundRequest_success() {
		// Given
		when(bindingResult.hasErrors()).thenReturn(false);
		doNothing().when(refundService).requestRefund(any(RefundCreateRequest.class));

		// When
		String viewName = mypageRefundController.processRefundRequest(mockRefundCreateRequest, bindingResult,
			redirectAttributes);

		// Then
		assertThat(viewName).isEqualTo("redirect:/mypage/orders/" + mockRefundCreateRequest.orderKey());
		verify(refundService).requestRefund(mockRefundCreateRequest);
		verify(redirectAttributes).addFlashAttribute("globalSuccessMessage", "환불 요청이 정상적으로 접수되었습니다.");
		verifyNoMoreInteractions(redirectAttributes);
	}

	@Test
	@DisplayName("processRefundRequest: 유효성 검사 실패 시")
	void processRefundRequest_validationErrors() {
		// Given
		when(bindingResult.hasErrors()).thenReturn(true);

		// When
		String viewName = mypageRefundController.processRefundRequest(mockRefundCreateRequest, bindingResult,
			redirectAttributes);

		// Then
		assertThat(viewName).isEqualTo("redirect:/mypage/orders/" + mockRefundCreateRequest.orderKey());
		verifyNoInteractions(refundService);
		verifyNoMoreInteractions(redirectAttributes);
	}

	@Test
	@DisplayName("processRefundRequest: 환불 요청 중 예외 발생 시")
	void processRefundRequest_exception() {
		// Given
		String errorMessage = "환불 처리 중 예상치 못한 오류가 발생했습니다.";
		when(bindingResult.hasErrors()).thenReturn(false);
		doThrow(new RuntimeException(errorMessage)).when(refundService).requestRefund(any(RefundCreateRequest.class));

		// When
		String viewName = mypageRefundController.processRefundRequest(mockRefundCreateRequest, bindingResult,
			redirectAttributes);

		// Then
		assertThat(viewName).isEqualTo("redirect:/mypage/orders/" + mockRefundCreateRequest.orderKey());
		verify(refundService).requestRefund(mockRefundCreateRequest);
		verify(redirectAttributes).addFlashAttribute("globalErrorMessage", errorMessage);
		verifyNoMoreInteractions(redirectAttributes);
	}
}