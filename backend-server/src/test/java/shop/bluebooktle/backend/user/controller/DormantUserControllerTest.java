package shop.bluebooktle.backend.user.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.bluebooktle.backend.user.service.UserService;
import shop.bluebooktle.common.dto.user.request.IssueDormantAuthCodeRequest;
import shop.bluebooktle.common.dto.user.request.ReactivateDormantUserRequest;
import shop.bluebooktle.common.exception.handler.GlobalExceptionHandler;
import shop.bluebooktle.common.security.AuthUserLoader;
import shop.bluebooktle.common.util.JwtUtil;

@ActiveProfiles("test")
@WebMvcTest(
	controllers = UserController.class,
	excludeAutoConfiguration = {SecurityAutoConfiguration.class}
)
@Import({GlobalExceptionHandler.class})
class DormantUserControllerTest {

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

	@Test
	@DisplayName("공개 - 휴면 계정 활성화 성공")
	void reactivateDormantUser_success() throws Exception {
		ReactivateDormantUserRequest request = new ReactivateDormantUserRequest();
		request.setLoginId("dormantUser");
		request.setAuthCode("authCode123");
		doNothing().when(userService).reactivateDormantUser(any());

		mockMvc.perform(post("/api/users/dormant/reactivate")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("공개 - 휴면 계정 인증 코드 발급 성공")
	void issueDormantAuthCode_success() throws Exception {
		IssueDormantAuthCodeRequest request = new IssueDormantAuthCodeRequest();
		request.setLoginId("dormantUser");
		doNothing().when(userService).issueDormantAuthCode(anyString());

		mockMvc.perform(post("/api/users/dormant/issue-code")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("공개 - 휴면 계정 인증 코드 발급 실패 (유효성 검사 실패)")
	void issueDormantAuthCode_fail_validation() throws Exception {
		IssueDormantAuthCodeRequest request = new IssueDormantAuthCodeRequest();
		request.setLoginId("");

		mockMvc.perform(post("/api/users/dormant/issue-code")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest());
	}
}