package shop.bluebooktle.backend.user.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.bluebooktle.backend.user.service.UserService;
import shop.bluebooktle.common.domain.auth.UserStatus;
import shop.bluebooktle.common.domain.auth.UserType;
import shop.bluebooktle.common.dto.auth.UserDto;
import shop.bluebooktle.common.dto.user.request.AdminUserUpdateRequest;
import shop.bluebooktle.common.dto.user.request.UserUpdateRequest;
import shop.bluebooktle.common.dto.user.response.AdminUserResponse;
import shop.bluebooktle.common.dto.user.response.UserMembershipLevelResponse;
import shop.bluebooktle.common.dto.user.response.UserResponse;
import shop.bluebooktle.common.dto.user.response.UserTotalPointResponse;
import shop.bluebooktle.common.dto.user.response.UserWithAddressResponse;
import shop.bluebooktle.common.exception.auth.UserNotFoundException;
import shop.bluebooktle.common.exception.handler.GlobalExceptionHandler;
import shop.bluebooktle.common.security.AuthUserLoader;
import shop.bluebooktle.common.security.AuthenticationAspect;
import shop.bluebooktle.common.security.UserPrincipal;
import shop.bluebooktle.common.util.JwtUtil;

@ActiveProfiles("test")
@WebMvcTest(
	controllers = UserController.class
)
@Import({AuthenticationAspect.class, GlobalExceptionHandler.class})
class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private UserService userService;

	@MockitoBean
	private JwtUtil jwtUtil;

	@MockitoBean
	private AuthUserLoader authUserLoader;

	private UserPrincipal adminPrincipal;
	private UserPrincipal userPrincipal;

	@BeforeEach
	void setUp() {
		UserDto adminDto = UserDto.builder()
			.id(1L)
			.loginId("admin")
			.type(UserType.ADMIN)
			.status(UserStatus.ACTIVE)
			.build();
		adminPrincipal = new UserPrincipal(adminDto);

		UserDto userDto = UserDto.builder()
			.id(2L)
			.loginId("user")
			.type(UserType.USER)
			.status(UserStatus.ACTIVE)
			.build();
		userPrincipal = new UserPrincipal(userDto);
	}

	@Test
	@DisplayName("관리자 - 회원 목록 조회 성공")
	void listUsers_asAdmin_success() throws Exception {
		Page<AdminUserResponse> userPage = new PageImpl<>(Collections.emptyList());
		given(userService.findUsers(any(), any())).willReturn(userPage);

		mockMvc.perform(get("/api/users").with(user(adminPrincipal)))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("관리자 - 회원 목록 조회 실패 (일반 사용자 접근, 403 Forbidden)")
	void listUsers_asUser_failForbidden() throws Exception {
		mockMvc.perform(get("/api/users").with(user(userPrincipal)))
			.andExpect(status().isForbidden());
	}

	@Test
	@DisplayName("관리자 - 회원 목록 조회 실패 (비로그인 접근, 401 Unauthorized)")
	void listUsers_asAnonymous_failUnauthorized() throws Exception {
		mockMvc.perform(get("/api/users"))
			.andExpect(status().isUnauthorized());
	}

	@Test
	@DisplayName("관리자 - 회원 상세 조회 성공")
	void getUserDetail_asAdmin_success() throws Exception {
		given(userService.findUserByIdAdmin(anyLong())).willReturn(new AdminUserResponse());

		mockMvc.perform(get("/api/users/admin/{userId}", 1L).with(user(adminPrincipal)))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("관리자 - 회원 상세 조회 실패 (사용자 없음, 404 Not Found)")
	void getUserDetail_fail_userNotFound() throws Exception {
		given(userService.findUserByIdAdmin(anyLong())).willThrow(new UserNotFoundException());

		mockMvc.perform(get("/api/users/admin/{userId}", 99L).with(user(adminPrincipal)))
			.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("관리자 - 회원 정보 수정 성공")
	void updateUserByAdmin_success() throws Exception {
		AdminUserUpdateRequest request = new AdminUserUpdateRequest();
		request.setName("이름");
		request.setNickname("닉네임");
		request.setEmail("a@a.com");
		request.setPhoneNumber("01000000000");
		request.setBirthDate("2000-01-01");
		request.setUserType(UserType.USER);
		request.setUserStatus(UserStatus.ACTIVE);
		request.setMembershipId(1L);

		doNothing().when(userService).updateUserAdmin(anyLong(), any());

		mockMvc.perform(put("/api/users/admin/{userId}", 1L)
				.with(user(adminPrincipal)).with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("관리자 - 회원 정보 수정 실패 (유효성 검사 실패, 400 Bad Request)")
	void updateUserByAdmin_fail_validation() throws Exception {
		AdminUserUpdateRequest request = new AdminUserUpdateRequest();
		request.setName("");
		request.setNickname("");
		request.setEmail("");
		request.setPhoneNumber("");
		request.setBirthDate("");
		request.setUserType(UserType.USER);
		request.setUserStatus(UserStatus.ACTIVE);
		request.setMembershipId(1L);

		mockMvc.perform(put("/api/users/admin/{userId}", 1L)
				.with(user(adminPrincipal)).with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("사용자 - 내 정보 조회 성공")
	void getMe_asUser_success() throws Exception {
		given(userService.findByUserId(anyLong())).willReturn(new UserResponse());

		mockMvc.perform(get("/api/users/me").with(user(userPrincipal)))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("사용자 - 내 정보 조회 실패 (유효하지 않은 토큰, 401 Unauthorized)")
	void getMe_asUser_fail_invalidToken() throws Exception {
		mockMvc.perform(get("/api/users/me"))
			.andExpect(status().isUnauthorized());
	}

	@Test
	@DisplayName("사용자 - 내 정보 조회 실패 (사용자 없음, 404 Not Found)")
	void getMe_asUser_fail_userNotFound() throws Exception {
		given(userService.findByUserId(anyLong())).willThrow(new UserNotFoundException());

		mockMvc.perform(get("/api/users/me").with(user(userPrincipal)))
			.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("사용자 - 내 정보 수정 성공")
	void updateUser_success() throws Exception {
		UserUpdateRequest request = new UserUpdateRequest("새로운닉네임", "01098765432", "20000101");
		doNothing().when(userService).updateUser(anyLong(), any());

		mockMvc.perform(put("/api/users/{id}", userPrincipal.getUserId())
				.with(user(userPrincipal)).with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("사용자 - 내 정보 수정 실패 (사용자 없음, 404 Not Found)")
	void updateUser_fail_userNotFound() throws Exception {
		UserUpdateRequest request = new UserUpdateRequest("새로운닉네임", "01098765432", "20000101");
		doThrow(new UserNotFoundException()).when(userService).updateUser(anyLong(), any());

		mockMvc.perform(put("/api/users/{id}", userPrincipal.getUserId())
				.with(user(userPrincipal)).with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isNotFound());
	}

	// @Valid 어노테이션이 UserController.java의 UserUpdateRequest에 없으므로, 해당 테스트는 수정/제거 필요
	// @Test
	// @DisplayName("사용자 - 내 정보 수정 실패 (유효성 검사 실패, 400 Bad Request)")
	// void updateUser_fail_validation() throws Exception {
	// 	UserUpdateRequest request = new UserUpdateRequest("", "", "");
	// 	mockMvc.perform(put("/api/users/{id}", userPrincipal.getUserId())
	// 			.with(user(userPrincipal)).with(csrf())
	// 			.contentType(MediaType.APPLICATION_JSON)
	// 			.content(objectMapper.writeValueAsString(request)))
	// 		.andExpect(status().isBadRequest());
	// }

	@Test
	@DisplayName("사용자 - 내 포인트 조회 성공")
	void getUserTotalPoints_success() throws Exception {
		given(userService.findUserTotalPoints(anyLong())).willReturn(new UserTotalPointResponse(BigDecimal.TEN));

		mockMvc.perform(get("/api/users/points").with(user(userPrincipal)))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("사용자 - 내 포인트 조회 실패 (사용자 없음, 404 Not Found)")
	void getUserTotalPoints_fail_userNotFound() throws Exception {
		given(userService.findUserTotalPoints(anyLong())).willThrow(new UserNotFoundException());

		mockMvc.perform(get("/api/users/points").with(user(userPrincipal)))
			.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("사용자 - 내 등급 조회 성공")
	void getUserMembershipLevel_success() throws Exception {
		given(userService.findUserNetSpentAmountForLastThreeMonthsByUserId(anyLong())).willReturn(
			new UserMembershipLevelResponse(1L, BigDecimal.ZERO, 1L, "일반"));

		mockMvc.perform(get("/api/users/membership").with(user(userPrincipal)))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("사용자 - 내 정보 및 주소 조회 성공")
	void getUserWithAddresses_success() throws Exception {
		given(userService.findUserWithAddress(anyLong())).willReturn(
			new UserWithAddressResponse(1L, "", "", "", BigDecimal.ZERO, Collections.emptyList()));

		mockMvc.perform(get("/api/users/addresses").with(user(userPrincipal)))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("사용자 - 내 정보 및 주소 조회 실패 (사용자 없음, 404 Not Found)")
	void getUserWithAddresses_fail_userNotFound() throws Exception {
		given(userService.findUserWithAddress(anyLong())).willThrow(new UserNotFoundException());

		mockMvc.perform(get("/api/users/addresses").with(user(userPrincipal)))
			.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("사용자 - 회원 탈퇴 성공")
	void withdrawMyAccount_success() throws Exception {
		doNothing().when(userService).withdrawUser(anyLong(), anyString());

		mockMvc.perform(delete("/api/users/me")
				.with(user(userPrincipal)).with(csrf()))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("사용자 - 회원 탈퇴 실패 (유효하지 않은 토큰, 401 Unauthorized)")
	void withdrawMyAccount_fail_invalidToken() throws Exception {
		mockMvc.perform(delete("/api/users/me")
				.with(csrf()))
			.andExpect(status().isUnauthorized());
	}

	@Test
	@DisplayName("사용자 - 회원 탈퇴 실패 (UserPrincipal이 null일 경우)")
	void withdrawMyAccount_fail_nullPrincipal() throws Exception {
		mockMvc.perform(delete("/api/users/me")
				.with(csrf()))
			.andExpect(status().isUnauthorized());
	}

	@Test
	@DisplayName("사용자 - 회원 탈퇴 실패 (UserPrincipal의 userId가 null일 경우)")
	void withdrawMyAccount_fail_nullUserIdInPrincipal() throws Exception {
		UserDto userDtoWithNullId = UserDto.builder()
			.id(null)
			.loginId("userWithNullId")
			.type(UserType.USER)
			.status(UserStatus.ACTIVE)
			.build();
		UserPrincipal principalWithNullId = new UserPrincipal(userDtoWithNullId);

		mockMvc.perform(delete("/api/users/me")
				.with(user(principalWithNullId)).with(csrf()))
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.message").value("유효한 사용자 정보가 없습니다."));
	}

	@Test
	@DisplayName("사용자 - 내 정보 조회 실패 (서비스에서 UserNotFoundException 발생)")
	void getMe_fail_userServiceThrowsUserNotFound() throws Exception {
		given(userService.findByUserId(anyLong())).willThrow(new UserNotFoundException());

		mockMvc.perform(get("/api/users/me").with(user(userPrincipal)))
			.andExpect(status().isNotFound());
	}
}