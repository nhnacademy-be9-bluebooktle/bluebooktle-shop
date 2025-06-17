package shop.bluebooktle.frontend.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import shop.bluebooktle.common.dto.auth.request.LoginRequest;
import shop.bluebooktle.common.dto.auth.request.SignupRequest;
import shop.bluebooktle.common.dto.user.request.IssueDormantAuthCodeRequest;
import shop.bluebooktle.common.dto.user.request.ReactivateDormantUserRequest;
import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;
import shop.bluebooktle.frontend.config.advice.GlobalCartCountAdvice;
import shop.bluebooktle.frontend.config.advice.GlobalCategoryInfoAdvice;
import shop.bluebooktle.frontend.config.advice.GlobalUserInfoAdvice;
import shop.bluebooktle.frontend.service.AuthService;
import shop.bluebooktle.frontend.service.UserService;
import shop.bluebooktle.frontend.util.CookieTokenUtil;

@WebMvcTest(
	controllers = AuthController.class,
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
@Import(RefreshAutoConfiguration.class)
@ActiveProfiles("test")
public class AuthControllerTest {
	@MockitoBean
	AuthService authService;
	@MockitoBean
	UserService userService;
	@MockitoBean
	CookieTokenUtil cookieTokenUtil;

	@Autowired
	MockMvc mvc;

	@Value("${oauth.payco.client-id}")
	String paycoClientId;
	@Value("${oauth.payco.redirect-uri}")
	String paycoRedirectUri;

	@Test
	@DisplayName("GET /login - 에러 없을 때")
	void getLogin_noError() throws Exception {
		mvc.perform(get("/login"))
			.andExpect(status().isOk())
			.andExpect(view().name("auth/login_form"))
			.andExpect(model().attribute("paycoClientId", paycoClientId))
			.andExpect(model().attribute("paycoRedirectUriEncoded",
				URLEncoder.encode(paycoRedirectUri, StandardCharsets.UTF_8)));
	}

	@Test
	@DisplayName("GET /login - flash 에러 있을 때")
	void getLogin_withError() throws Exception {
		mvc.perform(get("/login")
				.flashAttr("errorCode","E100")
				.flashAttr("errorMessage","msg"))
			.andExpect(status().isOk())
			.andExpect(view().name("auth/login_form"))
			.andExpect(model().attribute("error","true"));
	}

	@Test
	@DisplayName("POST /login 성공 → /merge")
	void postLogin_success() throws Exception {
		var req = new LoginRequest();
		req.setLoginId("u");
		req.setPassword("p");
		mvc.perform(post("/login")
				.flashAttr("loginRequest", req))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/merge"));
		then(authService).should().login(any(), eq(req));
	}

	@Test
	@DisplayName("POST /login - 휴면 계정(ApplicationException.AUTH_INACTIVE_ACCOUNT)")
	void postLogin_inactive() throws Exception {
		var req = new LoginRequest();
		req.setLoginId("u");
		req.setPassword("p");

		willThrow(new ApplicationException(ErrorCode.AUTH_INACTIVE_ACCOUNT))
			.given(authService).login(any(), any());
		mvc.perform(post("/login")
				.flashAttr("loginRequest", req))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/auth/reactive"));
	}

	@Test
	@DisplayName("POST /login - 기타 ApplicationException")
	void postLogin_appEx() throws Exception {
		var req = new LoginRequest();
		req.setLoginId("u");
		req.setPassword("p");

		willThrow(new ApplicationException(ErrorCode.INTERNAL_SERVER_ERROR))
			.given(authService).login(any(), any());
		mvc.perform(post("/login")
				.flashAttr("loginRequest", req))
			.andExpect(status().is3xxRedirection())
			.andExpect(flash().attribute("error","true"))
			.andExpect(flash().attribute("errorCode", ErrorCode.INTERNAL_SERVER_ERROR.getCode()))
			.andExpect(flash().attribute("errorMessage", ErrorCode.INTERNAL_SERVER_ERROR.getMessage()))
			.andExpect(redirectedUrl("/login"));
	}

	@Test
	@DisplayName("POST /login - 기타 Exception")
	void postLogin_otherEx() throws Exception {
		var req = new LoginRequest();
		req.setLoginId("u");
		req.setPassword("p");

		willThrow(new RuntimeException("oops"))
			.given(authService).login(any(), any());
		mvc.perform(post("/login")
				.flashAttr("loginRequest", req))
			.andExpect(status().is3xxRedirection())
			.andExpect(flash().attribute("error","true"))
			.andExpect(flash().attribute("errorCode","C001"))
			.andExpect(flash().attribute("errorMessage","oops"))
			.andExpect(redirectedUrl("/login"));
	}

	@Test
	@DisplayName("GET /signup 폼")
	void getSignup() throws Exception {
		mvc.perform(get("/signup"))
			.andExpect(status().isOk())
			.andExpect(view().name("auth/signup_form"))
			.andExpect(model().attributeExists("signupRequest"));
	}

	@Test
	@DisplayName("POST /signup - 바인딩 에러 → /signup")
	void postSignup_bindingError() throws Exception {
		// 그냥 빈 폼으로 보내면 validation 에러가 난다
		mvc.perform(post("/signup"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/signup"))
			.andExpect(flash().attributeExists(
				"org.springframework.validation.BindingResult.signupRequest",
				"signupRequest"));
	}

	@Test
	@DisplayName("POST /signup - ApplicationException → /signup")
	void postSignup_appEx() throws Exception {
		// stub: any(SignupRequest.class) 로 변경
		willThrow(new ApplicationException(ErrorCode.INTERNAL_SERVER_ERROR))
			.given(authService).signup(any(SignupRequest.class));
		mvc.perform(post("/signup")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("loginId",     "user1")
				.param("email",       "user@example.com")
				.param("birth",       "20000101")   // 8자리 YYYYMMDD
				.param("phoneNumber", "01012345678")
				.param("password",    "password123")
				.param("nickname",    "nick")
				.param("name",        "홍길동")
			)
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/signup"))
			.andExpect(flash().attribute("error",         "true"))
			.andExpect(flash().attribute("errorCode",     ErrorCode.INTERNAL_SERVER_ERROR.getCode()))
			.andExpect(flash().attribute("errorMessage",  ErrorCode.INTERNAL_SERVER_ERROR.getMessage()));
	}

	@Test
	@DisplayName("POST /signup - 기타 Exception → /signup, error=true 플래시")
	void postSignup_otherEx() throws Exception {
		// RuntimeException 을 던지도록 stub
		willThrow(new RuntimeException("fail"))
			.given(authService).signup(any(SignupRequest.class));

		mvc.perform(post("/signup")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("loginId", "user1")
				.param("email", "user@example.com")
				// birth를 8글자로 맞춘다
				.param("birth", "20000101")
				.param("phoneNumber", "01012345678")
				.param("password", "password123")
				.param("nickname", "nick")
				.param("name", "홍길동")
			)
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/signup"))
			.andExpect(flash().attribute("error", "true"))
			.andExpect(flash().attribute("errorMessage", "fail"))
			// signupRequest도 플래시에 담겨야 하므로 존재 여부만 확인
			.andExpect(flash().attributeExists("signupRequest"));
	}

	@Test
	@DisplayName("GET /logout - 메시지 없을 때 → /")
	void getLogout_noMsg() throws Exception {
		mvc.perform(get("/logout"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/"));
		then(cookieTokenUtil).should().clearTokens(any());
	}

	@Test
	@DisplayName("GET /logout - 메시지 있을 때 유지")
	void getLogout_withMsg() throws Exception {
		mvc.perform(get("/logout")
				.flashAttr("globalSuccessMessage","bye"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/"))
			.andExpect(flash().attribute("globalSuccessMessage","bye"));
	}

	@Test
	@DisplayName("GET /oauth/payco 성공 → /merge")
	void oauthPayco_success() throws Exception {
		mvc.perform(get("/oauth/payco")
				.param("code","c").param("state","s"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/merge"));
		then(authService).should().paycoLogin(any(), eq("c"));
	}

	@Test @DisplayName("GET /oauth/payco - ApplicationException → /login")
	void oauthPayco_appEx() throws Exception {
		willThrow(new ApplicationException(ErrorCode.AUTH_INACTIVE_ACCOUNT))
			.given(authService).paycoLogin(any(), eq("c"));
		mvc.perform(get("/oauth/payco")
				.param("code","c").param("state","s"))
			.andExpect(status().is3xxRedirection())
			.andExpect(flash().attribute("error","true"))
			.andExpect(flash().attribute("errorCode", ErrorCode.AUTH_INACTIVE_ACCOUNT.getCode()))
			.andExpect(redirectedUrl("/login"));
	}

	@Test @DisplayName("GET /oauth/payco - 기타 Exception → /login")
	void oauthPayco_otherEx() throws Exception {
		willThrow(new RuntimeException())
			.given(authService).paycoLogin(any(), eq("c"));
		mvc.perform(get("/oauth/payco")
				.param("code","c").param("state","s"))
			.andExpect(status().is3xxRedirection())
			.andExpect(flash().attribute("error","true"))
			.andExpect(flash().attribute("errorMessage","PAYCO 로그인 중 오류가 발생했습니다. 다시 시도해주세요."))
			.andExpect(redirectedUrl("/login"));
	}

	@Test
	@DisplayName("GET /auth/reactive 폼")
	void getReactiveForm() throws Exception {
		mvc.perform(get("/auth/reactive"))
			.andExpect(status().isOk())
			.andExpect(view().name("auth/reactive_form"));
	}

	@Test
	@DisplayName("POST /auth/dormant/issue-code - ApplicationException")
	void postIssueDormantCode_appEx() throws Exception {
		willThrow(new ApplicationException(ErrorCode.INTERNAL_SERVER_ERROR))
			.given(userService).requestDormantCode(any(IssueDormantAuthCodeRequest.class));

		mvc.perform(post("/auth/dormant/issue-code")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("loginId", "someUser")
			)
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/auth/reactive"))
			.andExpect(flash().attribute("formErrorSource", "issueCodeForm"))
			// ApplicationException 분기에서 e.getMessage()
			.andExpect(flash().attribute("errorMessage", ErrorCode.INTERNAL_SERVER_ERROR.getMessage()))
			.andExpect(flash().attribute("errorCode", ErrorCode.INTERNAL_SERVER_ERROR.getCode()));
	}

	@Test
	@DisplayName("POST /auth/dormant/issue-code - 기타 Exception")
	void postIssueDormantCode_otherEx() throws Exception {
		willThrow(new RuntimeException("uh-oh"))
			.given(userService).requestDormantCode(any(IssueDormantAuthCodeRequest.class));

		mvc.perform(post("/auth/dormant/issue-code")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("loginId", "someUser")
			)
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/auth/reactive"))
			.andExpect(flash().attribute("formErrorSource", "issueCodeForm"))
			// 기타 Exception 분기에서 하드코딩된 메시지
			.andExpect(flash().attribute("errorMessage",
				"인증 코드 발급 중 서버 내부 오류가 발생했습니다."))
			.andExpect(flash().attribute("errorCode", ErrorCode.INTERNAL_SERVER_ERROR.getCode()));
	}

	@Test
	@DisplayName("POST /auth/reactivate-account - ApplicationException")
	void postReactivate_appEx() throws Exception {
		// service.processReactivateDormant(...) 시 AUTH_INACTIVE_ACCOUNT 예외 발생
		willThrow(new ApplicationException(ErrorCode.AUTH_INACTIVE_ACCOUNT))
			.given(userService).processReactivateDormant(any(ReactivateDormantUserRequest.class));

		mvc.perform(post("/auth/reactivate-account")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("loginId", "dormantUser")
				.param("authCode", "123456")
			)
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/auth/reactive"))
			.andExpect(flash().attribute("formErrorSource", "reactivateForm"))
			.andExpect(flash().attribute("errorMessage", ErrorCode.AUTH_INACTIVE_ACCOUNT.getMessage()))
			.andExpect(flash().attribute("errorCode", ErrorCode.AUTH_INACTIVE_ACCOUNT.getCode()));
	}

	@Test
	@DisplayName("POST /auth/reactivate-account - 기타 Exception")
	void postReactivate_otherEx() throws Exception {
		// service.processReactivateDormant(...) 시 RuntimeException 발생
		willThrow(new RuntimeException("uh-oh"))
			.given(userService).processReactivateDormant(any(ReactivateDormantUserRequest.class));

		mvc.perform(post("/auth/reactivate-account")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("loginId", "dormantUser")
				.param("authCode", "123456")
			)
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/auth/reactive"))
			.andExpect(flash().attribute("formErrorSource", "reactivateForm"))
			.andExpect(flash().attribute("errorMessage",
				"계정 활성화 중 서버 내부 오류가 발생했습니다."))
			.andExpect(flash().attribute("errorCode", ErrorCode.INTERNAL_SERVER_ERROR.getCode()));
	}

}
