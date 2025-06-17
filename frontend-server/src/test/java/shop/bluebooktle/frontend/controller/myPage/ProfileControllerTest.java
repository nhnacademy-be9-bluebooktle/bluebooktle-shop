package shop.bluebooktle.frontend.controller.myPage;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.argThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import shop.bluebooktle.common.domain.auth.UserProvider;
import shop.bluebooktle.common.domain.auth.UserStatus;
import shop.bluebooktle.common.domain.auth.UserType;
import shop.bluebooktle.common.dto.auth.request.PasswordUpdateRequest;
import shop.bluebooktle.common.dto.user.request.UserUpdateRequest;
import shop.bluebooktle.common.dto.user.response.UserResponse;
import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;
import shop.bluebooktle.frontend.service.AuthService;
import shop.bluebooktle.frontend.service.UserService;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ProfileControllerTest {

	@InjectMocks
	private ProfileController profileController;

	@Mock
	private UserService userService;

	@Mock
	private AuthService authService;

	@Mock
	private Model model;

	@Mock
	private RedirectAttributes redirectAttributes;

	@Mock
	private BindingResult bindingResult;

	private UserResponse mockUserResponse;

	@BeforeEach
	void setUp() {
		mockUserResponse = new UserResponse(
			1L,
			"testuser",
			"테스트이름",
			"test@example.com",
			"testnickname",
			"19900101",
			"01012345678",
			BigDecimal.ZERO,
			UserType.USER,
			UserStatus.ACTIVE,
			LocalDateTime.now(),
			"브론즈",
			UserProvider.BLUEBOOKTLE
		);
	}

	@Test
	@DisplayName("userProfilePage: 성공적으로 페이지 로드")
	void userProfilePage_success() {
		when(userService.getMe()).thenReturn(mockUserResponse);
		when(model.containsAttribute("UserUpdateRequest")).thenReturn(false);
		when(model.containsAttribute("PasswordUpdateRequest")).thenReturn(false);

		String viewName = profileController.userProfilePage(model, redirectAttributes);

		assertThat(viewName).isEqualTo("mypage/profile");
		verify(model).addAttribute(eq("user"), eq(mockUserResponse));
		verify(model).addAttribute(eq("UserUpdateRequest"), any(UserUpdateRequest.class));
		verify(model).addAttribute(eq("PasswordUpdateRequest"), any(PasswordUpdateRequest.class));

		verify(model).addAttribute(eq("UserUpdateRequest"), argThat(arg ->
			((UserUpdateRequest)arg).getNickname().equals("testnickname") &&
				((UserUpdateRequest)arg).getPhoneNumber().equals("01012345678") &&
				((UserUpdateRequest)arg).getBirthDate().equals("1990-01-01")
		));
	}

	@Test
	@DisplayName("userProfilePage: ApplicationException 발생 시 리다이렉트")
	void userProfilePage_applicationException() {
		ApplicationException exception = new ApplicationException(ErrorCode.AUTH_USER_NOT_FOUND, "사용자를 찾을 수 없습니다.");
		when(userService.getMe()).thenThrow(exception);

		String viewName = profileController.userProfilePage(model, redirectAttributes);

		assertThat(viewName).isEqualTo("redirect:/");
		verify(redirectAttributes).addFlashAttribute("globalErrorMessage", "사용자를 찾을 수 없습니다.");
		// Change "U001" to "A006" to match the actual error code for AUTH_USER_NOT_FOUND
		verify(redirectAttributes).addFlashAttribute("globalErrorTitle", "A006");
	}

	@Test
	@DisplayName("updateUserProfile: 성공적인 프로필 업데이트")
	void updateUserProfile_success() {
		UserUpdateRequest userUpdateRequest = new UserUpdateRequest("newnickname", "01098765432", "2000-05-15");
		when(bindingResult.hasErrors()).thenReturn(false);
		doNothing().when(userService).updateUser(eq(1L), any(UserUpdateRequest.class));

		String viewName = profileController.updateUserProfile(1L, userUpdateRequest, bindingResult, redirectAttributes);

		assertThat(viewName).isEqualTo("redirect:/mypage/profile");
		verify(userService).updateUser(eq(1L), argThat(arg ->
			((UserUpdateRequest)arg).getBirthDate().equals("20000515")
		));
		verify(redirectAttributes).addFlashAttribute("globalSuccessMessage", "프로필이 업데이트되었습니다");
	}

	@Test
	@DisplayName("updateUserProfile: 유효성 검사 실패 시 리다이렉트")
	void updateUserProfile_validationErrors() {
		UserUpdateRequest userUpdateRequest = new UserUpdateRequest("nn", "invalid", "1234");
		when(bindingResult.hasErrors()).thenReturn(true);

		String viewName = profileController.updateUserProfile(1L, userUpdateRequest, bindingResult, redirectAttributes);

		assertThat(viewName).isEqualTo("redirect:/mypage/profile");
		verify(redirectAttributes).addFlashAttribute(
			eq("org.springframework.validation.BindingResult.UserUpdateRequest"), eq(bindingResult));
		verify(redirectAttributes).addFlashAttribute(eq("UserUpdateRequest"), eq(userUpdateRequest));
		verifyNoInteractions(userService);
	}

	@Test
	@DisplayName("updateUserProfile: ApplicationException 발생 시 리다이렉트")
	void updateUserProfile_applicationException() {
		UserUpdateRequest userUpdateRequest = new UserUpdateRequest("newnickname", "01098765432", "2000-05-15");
		ApplicationException exception = new ApplicationException(ErrorCode.INVALID_INPUT_VALUE, "유효하지 않은 입력 값입니다.");
		when(bindingResult.hasErrors()).thenReturn(false);
		doThrow(exception).when(userService).updateUser(eq(1L), any(UserUpdateRequest.class));

		String viewName = profileController.updateUserProfile(1L, userUpdateRequest, bindingResult, redirectAttributes);

		assertThat(viewName).isEqualTo("redirect:/mypage/profile");
		verify(redirectAttributes).addFlashAttribute("globalErrorMessage", "유효하지 않은 입력 값입니다.");
		verify(redirectAttributes).addFlashAttribute("globalErrorTitle", "C002");
		verify(redirectAttributes).addFlashAttribute(eq("UserUpdateRequest"), argThat(arg ->
			((UserUpdateRequest)arg).getBirthDate().equals("2000-05-15")
		));
	}

	@Test
	@DisplayName("updateUserProfile: 일반 Exception 발생 시 리다이렉트")
	void updateUserProfile_generalException() {
		UserUpdateRequest userUpdateRequest = new UserUpdateRequest("newnickname", "01098765432", "2000-05-15");
		when(bindingResult.hasErrors()).thenReturn(false);
		doThrow(new RuntimeException("DB connection failed")).when(userService)
			.updateUser(eq(1L), any(UserUpdateRequest.class));

		String viewName = profileController.updateUserProfile(1L, userUpdateRequest, bindingResult, redirectAttributes);

		assertThat(viewName).isEqualTo("redirect:/mypage/profile");
		verify(redirectAttributes).addFlashAttribute(eq("globalErrorMessage"),
			eq("프로필 업데이트 중 오류가 발생했습니다: DB connection failed"));
		verify(redirectAttributes).addFlashAttribute(eq("UserUpdateRequest"), argThat(arg ->
			((UserUpdateRequest)arg).getBirthDate().equals("2000-05-15")
		));
	}

	@Test
	@DisplayName("changePassword: 성공적인 비밀번호 변경")
	void changePassword_success() {
		PasswordUpdateRequest passwordUpdateRequest = new PasswordUpdateRequest();
		passwordUpdateRequest.setCurrentPassword("oldPass");
		passwordUpdateRequest.setNewPassword("newPass123!");
		passwordUpdateRequest.setConfirmNewPassword("newPass123!");

		when(bindingResult.hasErrors()).thenReturn(false);
		doNothing().when(authService).changePassword(any(PasswordUpdateRequest.class));

		String viewName = profileController.changePassword(passwordUpdateRequest, bindingResult, redirectAttributes);

		assertThat(viewName).isEqualTo("redirect:/logout");
		verify(authService).changePassword(eq(passwordUpdateRequest));
		verify(redirectAttributes).addFlashAttribute("globalSuccessMessage", "비밀번호가 성공적으로 변경되었습니다. \n다시 로그인해주세요");
	}

	@Test
	@DisplayName("changePassword: 유효성 검사 실패 시 리다이렉트")
	void changePassword_validationErrors() {
		PasswordUpdateRequest passwordUpdateRequest = new PasswordUpdateRequest();
		passwordUpdateRequest.setCurrentPassword("old");
		passwordUpdateRequest.setNewPassword("new");
		passwordUpdateRequest.setConfirmNewPassword("new");

		when(bindingResult.hasErrors()).thenReturn(true);

		String viewName = profileController.changePassword(passwordUpdateRequest, bindingResult, redirectAttributes);

		assertThat(viewName).isEqualTo("redirect:/mypage/profile#passwordChangeSection");
		verify(redirectAttributes).addFlashAttribute(
			eq("org.springframework.validation.BindingResult.PasswordUpdateRequest"), eq(bindingResult));
		verify(redirectAttributes).addFlashAttribute(eq("PasswordUpdateRequest"), eq(passwordUpdateRequest));
		verifyNoInteractions(authService);
	}

	@Test
	@DisplayName("changePassword: 새 비밀번호와 확인 비밀번호 불일치 시")
	void changePassword_mismatchPasswords() {

		PasswordUpdateRequest passwordUpdateRequest = new PasswordUpdateRequest();
		passwordUpdateRequest.setCurrentPassword("oldPass");
		passwordUpdateRequest.setNewPassword("newPass123!");
		passwordUpdateRequest.setConfirmNewPassword("wrongPass");

		when(bindingResult.hasErrors()).thenReturn(false);

		String viewName = profileController.changePassword(passwordUpdateRequest, bindingResult, redirectAttributes);

		assertThat(viewName).isEqualTo("redirect:/mypage/profile#passwordChangeSection");
		verify(bindingResult).rejectValue(eq("confirmNewPassword"), eq("password.mismatch"),
			eq("새 비밀번호와 확인 비밀번호가 일치하지 않습니다."));
		verify(redirectAttributes).addFlashAttribute(
			eq("org.springframework.validation.BindingResult.PasswordUpdateRequest"), eq(bindingResult));
		verify(redirectAttributes).addFlashAttribute(eq("PasswordUpdateRequest"), eq(passwordUpdateRequest));
		verifyNoInteractions(authService);
	}

	@Test
	@DisplayName("changePassword: ApplicationException 발생 시")
	void changePassword_applicationException() {
		PasswordUpdateRequest passwordUpdateRequest = new PasswordUpdateRequest();
		passwordUpdateRequest.setCurrentPassword("oldPass");
		passwordUpdateRequest.setNewPassword("newPass123!");
		passwordUpdateRequest.setConfirmNewPassword("newPass123!");

		ApplicationException exception = new ApplicationException(ErrorCode.AUTH_PASSWORD_MISMATCH,
			"현재 비밀번호가 일치하지 않습니다.");
		when(bindingResult.hasErrors()).thenReturn(false);
		doThrow(exception).when(authService).changePassword(any(PasswordUpdateRequest.class));

		String viewName = profileController.changePassword(passwordUpdateRequest, bindingResult, redirectAttributes);

		assertThat(viewName).isEqualTo("redirect:/mypage/profile#passwordChangeSection");
		verify(redirectAttributes).addFlashAttribute("globalErrorMessage", "현재 비밀번호가 일치하지 않습니다.");
		verify(redirectAttributes).addFlashAttribute("globalErrorTitle", "A007");
		verify(redirectAttributes).addFlashAttribute(eq("PasswordUpdateRequest"), eq(passwordUpdateRequest));
	}

	@Test
	@DisplayName("withdrawAccount: 성공적인 회원 탈퇴")
	void withdrawAccount_success() {
		doNothing().when(userService).withdrawAccount();

		String viewName = profileController.withdrawAccount(redirectAttributes);

		assertThat(viewName).isEqualTo("redirect:/logout");
		verify(userService).withdrawAccount();
		verify(redirectAttributes).addFlashAttribute("globalSuccessMessage",
			"회원 탈퇴가 정상적으로 처리되었습니다. 그동안 블루북틀을 이용해주셔서 감사합니다.");
	}

	@Test
	@DisplayName("withdrawAccount: ApplicationException 발생 시 회원 탈퇴 실패")
	void withdrawAccount_applicationException() {
		ApplicationException exception = new ApplicationException(ErrorCode.AUTH_USER_NOT_FOUND,
			"사용자를 찾을 수 없습니다.");
		doThrow(exception).when(userService).withdrawAccount();

		String viewName = profileController.withdrawAccount(redirectAttributes);

		assertThat(viewName).isEqualTo("redirect:/mypage/profile");
		verify(redirectAttributes).addFlashAttribute("globalErrorMessage", "사용자를 찾을 수 없습니다.");
		verify(redirectAttributes).addFlashAttribute("globalErrorTitle", "A006");
	}

	@Test
	@DisplayName("withdrawAccount: 일반 Exception 발생 시 회원 탈퇴 실패")
	void withdrawAccount_generalException() {
		doThrow(new RuntimeException("데이터베이스 연결 오류")).when(userService).withdrawAccount();

		String viewName = profileController.withdrawAccount(redirectAttributes);

		assertThat(viewName).isEqualTo("redirect:/mypage/profile");
		verify(redirectAttributes).addFlashAttribute("globalErrorMessage", "회원 탈퇴 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
	}
}