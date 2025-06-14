package shop.bluebooktle.backend.order.controller;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import shop.bluebooktle.backend.order.service.RefundService;
import shop.bluebooktle.common.domain.auth.UserStatus;
import shop.bluebooktle.common.domain.auth.UserType;
import shop.bluebooktle.common.domain.refund.RefundReason;
import shop.bluebooktle.common.dto.auth.UserDto;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.dto.refund.request.RefundCreateRequest;
import shop.bluebooktle.common.security.UserPrincipal;

@ExtendWith(MockitoExtension.class)
public class RefundControllerTest {

	@Mock
	private RefundService refundService;

	@InjectMocks
	private RefundController refundController;

	private UserPrincipal principal;

	@BeforeEach
	void setUp() {
		UserDto userDto = UserDto.builder()
			.id(1L)
			.loginId("testuser")
			.nickname("테스트닉네임")
			.type(UserType.USER)
			.status(UserStatus.ACTIVE)
			.build();
		principal = new UserPrincipal(userDto);
	}

	@Test
	@DisplayName("사용자 환불 요청 - 로그인 사용자 성공")
	void requestRefund_loggedInUser_success() {
		RefundCreateRequest request = new RefundCreateRequest(
			"ORD-12345",
			RefundReason.CHANGE_OF_MIND,
			"단순 변심으로 인한 환불 요청입니다."
		);
		Long userId = principal.getUserId();

		doNothing().when(refundService).requestRefund(eq(userId), eq(request));

		ResponseEntity<JsendResponse<Void>> responseEntity =
			refundController.requestRefund(request, principal);

		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(responseEntity.getBody()).isNotNull();
		assertThat(responseEntity.getBody().status()).isEqualTo("success");
		assertThat(responseEntity.getBody().data()).isNull();

		verify(refundService, times(1)).requestRefund(eq(userId), eq(request));
	}

	@Test
	@DisplayName("사용자 환불 요청 - 유효성 검사 실패 (orderKey가 null)")
	void requestRefund_validationFailure_orderKeyNull() {
		RefundCreateRequest invalidRequest = new RefundCreateRequest(
			null, // orderKey가 null
			RefundReason.CHANGE_OF_MIND,
			"단순 변심"
		);
	}

}