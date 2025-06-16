package shop.bluebooktle.auth.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import shop.bluebooktle.auth.event.type.UserLoginEvent;
import shop.bluebooktle.auth.event.type.UserSignUpEvent;
import shop.bluebooktle.auth.mq.producer.WelcomeCouponIssueProducer;
import shop.bluebooktle.auth.repository.MembershipLevelRepository;
import shop.bluebooktle.auth.repository.UserRepository;
import shop.bluebooktle.auth.repository.payco.PaycoApiClient;
import shop.bluebooktle.auth.repository.payco.PaycoAuthClient;
import shop.bluebooktle.auth.service.impl.AuthServiceImpl;
import shop.bluebooktle.common.domain.auth.UserProvider;
import shop.bluebooktle.common.domain.auth.UserStatus;
import shop.bluebooktle.common.domain.auth.UserType;
import shop.bluebooktle.common.dto.auth.request.LoginRequest;
import shop.bluebooktle.common.dto.auth.request.PasswordUpdateRequest;
import shop.bluebooktle.common.dto.auth.request.PaycoProfileData;
import shop.bluebooktle.common.dto.auth.request.PaycoProfileMember;
import shop.bluebooktle.common.dto.auth.request.PaycoProfileResponse;
import shop.bluebooktle.common.dto.auth.request.PaycoTokenResponse;
import shop.bluebooktle.common.dto.auth.request.SignupRequest;
import shop.bluebooktle.common.dto.auth.request.TokenRefreshRequest;
import shop.bluebooktle.common.dto.auth.response.TokenResponse;
import shop.bluebooktle.common.dto.coupon.request.WelcomeCouponIssueMessage;
import shop.bluebooktle.common.entity.auth.MembershipLevel;
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.auth.AuthenticationFailedException;
import shop.bluebooktle.common.exception.auth.DormantAccountException;
import shop.bluebooktle.common.exception.auth.EmailAlreadyExistsException;
import shop.bluebooktle.common.exception.auth.InvalidRefreshTokenException;
import shop.bluebooktle.common.exception.auth.LoginIdAlreadyExistsException;
import shop.bluebooktle.common.exception.auth.UserNotFoundException;
import shop.bluebooktle.common.util.JwtUtil;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@InjectMocks
	private AuthServiceImpl authService;

	@Mock
	private UserRepository userRepository;
	@Mock
	private MembershipLevelRepository membershipLevelRepository;
	@Mock
	private PasswordEncoder passwordEncoder;
	@Mock
	private JwtUtil jwtUtil;
	@Mock
	private RefreshTokenService refreshTokenService;
	@Mock
	private AccessTokenService accessTokenService;
	@Mock
	private PaycoAuthClient paycoAuthClient;
	@Mock
	private PaycoApiClient paycoApiClient;
	@Mock
	private WelcomeCouponIssueProducer welcomeCouponIssueProducer;
	@Mock
	private ApplicationEventPublisher eventPublisher;

	private MembershipLevel defaultMembershipLevel;

	@BeforeEach
	void setUp() {
		// 기타 설정...
		defaultMembershipLevel = MembershipLevel.builder()
			.name("일반")
			.rate(10)
			.minNetSpent(BigDecimal.ZERO)
			.maxNetSpent(BigDecimal.valueOf(100000))
			.build();
	}

	@Test
	@DisplayName("회원가입 성공")
	void signup_success() {
		// given
		SignupRequest request = new SignupRequest();
		request.setLoginId("testUser");
		request.setPassword("pw1234");
		request.setEmail("test@example.com");
		request.setName("홍길동");
		request.setNickname("길동이");
		request.setPhoneNumber("01012345678");
		request.setBirth("19900101");

		when(userRepository.existsByLoginId(request.getLoginId())).thenReturn(false);
		when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);

		MembershipLevel level = MembershipLevel.builder()
			.name("일반")
			.rate(1)
			.minNetSpent(BigDecimal.ZERO)
			.maxNetSpent(new BigDecimal("99999.99"))
			.build();
		when(membershipLevelRepository.findByName("일반")).thenReturn(Optional.of(level));
		when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPw");

		// when
		authService.signup(request);

		// then
		verify(userRepository).save(any(User.class));
		verify(eventPublisher).publishEvent(any(UserSignUpEvent.class));
		verify(welcomeCouponIssueProducer).send(any(WelcomeCouponIssueMessage.class));
	}

	@Test
	@DisplayName("회원가입 실패 - 중복 ID")
	void signup_fail_duplicateLoginId() {
		// given
		SignupRequest request = new SignupRequest();
		request.setLoginId("duplicate");
		request.setEmail("a@a.com");

		when(userRepository.existsByLoginId("duplicate")).thenReturn(true);

		// expect
		assertThatThrownBy(() -> authService.signup(request))
			.isInstanceOf(LoginIdAlreadyExistsException.class);
	}

	@Test
	@DisplayName("회원가입 실패 - 중복 이메일")
	void signup_fail_duplicateEmail() {
		// given
		SignupRequest request = new SignupRequest();
		request.setLoginId("new");
		request.setEmail("dup@example.com");

		when(userRepository.existsByLoginId("new")).thenReturn(false);
		when(userRepository.existsByEmail("dup@example.com")).thenReturn(true);

		// expect
		assertThatThrownBy(() -> authService.signup(request))
			.isInstanceOf(EmailAlreadyExistsException.class);
	}

	@Test
	@DisplayName("로그인 성공")
	void login_success() {
		// given
		String loginId = "user1";
		String password = "pw123";

		MembershipLevel level = MembershipLevel.builder()
			.name("일반")
			.rate(1)
			.minNetSpent(BigDecimal.ZERO)
			.maxNetSpent(new BigDecimal("99999.99"))
			.build();

		User user = User.builder()
			.id(1L)
			.loginId(loginId)
			.encodedPassword("encoded")  // 실제 password 필드로 들어감
			.nickname("nick")
			.membershipLevel(level)
			.status(UserStatus.ACTIVE)
			.provider(UserProvider.BLUEBOOKTLE)
			.build();

		LoginRequest request = new LoginRequest();
		request.setLoginId(loginId);
		request.setPassword(password);

		when(userRepository.findByLoginId(loginId)).thenReturn(Optional.of(user));
		when(passwordEncoder.matches(password, "encoded")).thenReturn(true);
		when(jwtUtil.createAccessToken(eq(1L), eq("nick"), eq(UserType.USER))).thenReturn("access");
		when(jwtUtil.createRefreshToken(eq(1L), eq("nick"), eq(UserType.USER))).thenReturn("refresh");
		when(jwtUtil.getRefreshTokenExpirationMillis()).thenReturn(1000L);

		// when
		TokenResponse response = authService.login(request);

		// then
		assertThat(response.getAccessToken()).isEqualTo("access");
		assertThat(response.getRefreshToken()).isEqualTo("refresh");
		verify(refreshTokenService).save(eq(1L), eq("refresh"), eq(1000L));
		verify(eventPublisher).publishEvent(any(UserLoginEvent.class));
	}

	@Test
	@DisplayName("로그인 실패 - 유저 없음")
	void login_fail_userNotFound() {
		LoginRequest request = new LoginRequest();
		request.setLoginId("unknown");
		request.setPassword("pw");

		when(userRepository.findByLoginId("unknown")).thenReturn(Optional.empty());

		assertThatThrownBy(() -> authService.login(request))
			.isInstanceOf(AuthenticationFailedException.class);
	}

	@Test
	@DisplayName("로그인 실패 - 비밀번호 불일치")
	void login_fail_wrongPassword() {
		User user = User.builder()
			.loginId("id")
			.encodedPassword("encoded")
			.status(UserStatus.ACTIVE)
			.provider(UserProvider.BLUEBOOKTLE)
			.build();

		LoginRequest request = new LoginRequest();
		request.setLoginId("id");
		request.setPassword("wrong");

		when(userRepository.findByLoginId("id")).thenReturn(Optional.of(user));
		when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);

		assertThatThrownBy(() -> authService.login(request))
			.isInstanceOf(AuthenticationFailedException.class);
	}

	@Test
	@DisplayName("회원가입 실패 - 기본 멤버십 없음")
	void signup_fail_noDefaultMembershipLevel() {
		// given
		SignupRequest request = new SignupRequest();
		request.setLoginId("user123");
		request.setEmail("test@test.com");
		request.setPassword("pass1234");
		request.setNickname("닉네임");
		request.setBirth("19900101");
		request.setName("이름");
		request.setPhoneNumber("01012345678");

		when(userRepository.existsByLoginId(any())).thenReturn(false);
		when(userRepository.existsByEmail(any())).thenReturn(false);
		when(membershipLevelRepository.findByName("일반")).thenReturn(Optional.empty());

		// expect
		assertThatThrownBy(() -> authService.signup(request))
			.hasMessageContaining("기본 멤버십 등급을 찾을 수 없습니다.");
	}

	@Test
	@DisplayName("로그인 실패 - 휴면 계정")
	void login_fail_dormantUser() {
		// given
		User user = User.builder()
			.loginId("dormant")
			.encodedPassword("encoded")
			.status(UserStatus.DORMANT)
			.provider(UserProvider.BLUEBOOKTLE)
			.build();

		LoginRequest request = new LoginRequest();
		request.setLoginId("dormant");
		request.setPassword("pw");

		when(userRepository.findByLoginId("dormant")).thenReturn(Optional.of(user));
		when(passwordEncoder.matches(any(), any())).thenReturn(true);

		// expect
		assertThatThrownBy(() -> authService.login(request))
			.isInstanceOf(DormantAccountException.class)
			.hasMessageContaining("휴면 계정");
	}

	@Test
	@DisplayName("로그인 실패 - PAYCO 계정 일반 로그인 시도")
	void login_fail_paycoAccountViaFormLogin() {
		// given
		User user = User.builder()
			.loginId("paycoUser")
			.encodedPassword("encoded")
			.status(UserStatus.ACTIVE)
			.provider(UserProvider.PAYCO)
			.build();

		LoginRequest request = new LoginRequest();
		request.setLoginId("paycoUser");
		request.setPassword("pw");

		when(userRepository.findByLoginId("paycoUser")).thenReturn(Optional.of(user));
		when(passwordEncoder.matches(any(), any())).thenReturn(true);

		// expect
		assertThatThrownBy(() -> authService.login(request))
			.isInstanceOf(AuthenticationFailedException.class)
			.hasMessageContaining("PAYCO 계정");
	}

	@Test
	@DisplayName("로그아웃 - RefreshToken 삭제 및 AccessToken 블랙리스트 추가")
	void logout_success() {
		String accessToken = "dummyToken";

		when(jwtUtil.getUserIdFromToken(accessToken)).thenReturn(1L);

		authService.logout(accessToken);

		verify(refreshTokenService).deleteByUserId(1L);
		verify(accessTokenService).addToBlacklist(accessToken);
	}

	@Test
	@DisplayName("리프레시 토큰 갱신 성공")
	void refreshToken_success() {
		TokenRefreshRequest request = new TokenRefreshRequest("refresh123");

		when(jwtUtil.validateToken("refresh123")).thenReturn(true);
		when(jwtUtil.getUserIdFromToken("refresh123")).thenReturn(1L);
		when(refreshTokenService.findByUserId(1L)).thenReturn("refresh123");

		User user = User.builder()
			.id(1L)
			.nickname("nick")
			.status(UserStatus.ACTIVE)
			.type(UserType.USER)
			.build();

		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		when(jwtUtil.createAccessToken(1L, "nick", UserType.USER)).thenReturn("access");
		when(jwtUtil.createRefreshToken(1L, "nick", UserType.USER)).thenReturn("refresh");
		when(jwtUtil.getRefreshTokenExpirationMillis()).thenReturn(1000L);

		TokenResponse response = authService.refreshToken(request);

		assertThat(response.getAccessToken()).isEqualTo("access");
		assertThat(response.getRefreshToken()).isEqualTo("refresh");
	}

	@Test
	@DisplayName("비밀번호 변경 실패 - 소셜 로그인 사용자는 변경 불가")
	void changePassword_fail_socialUser() {
		// given
		PasswordUpdateRequest req = new PasswordUpdateRequest();
		req.setCurrentPassword("currentPassword");
		req.setNewPassword("newPassword123");
		req.setConfirmNewPassword("newPassword123");

		User socialUser = User.builder()
			.id(1L)
			.loginId("social_user")
			.provider(UserProvider.PAYCO)
			.build();

		when(userRepository.findById(1L)).thenReturn(Optional.of(socialUser));

		// when & then
		assertThatThrownBy(() -> authService.changePassword(1L, req))
			.isInstanceOf(ApplicationException.class)
			.hasMessageContaining("소셜 로그인"); // 또는 전체 메시지 그대로 넣어도 됨

		verify(userRepository).findById(1L);
		verifyNoMoreInteractions(userRepository);
	}

	@Test
	@DisplayName("비밀번호 변경 성공")
	void changePassword_success() {
		PasswordUpdateRequest req = new PasswordUpdateRequest();
		req.setCurrentPassword("oldPw");
		req.setNewPassword("newPw1234");
		req.setConfirmNewPassword("newPw1234");

		User user = User.builder()
			.id(1L)
			.loginId("user1")
			.encodedPassword("encodedOldPw")
			.provider(UserProvider.BLUEBOOKTLE)
			.status(UserStatus.ACTIVE)
			.build();

		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		when(passwordEncoder.matches("oldPw", "encodedOldPw")).thenReturn(true);
		when(passwordEncoder.encode("newPw1234")).thenReturn("encodedNewPw");

		authService.changePassword(1L, req);

		verify(userRepository).save(argThat(saved ->
			saved.getPassword().equals("encodedNewPw")
		));
	}

	@Test
	@DisplayName("리프레시 토큰 갱신 실패 - 유효하지 않은 토큰")
	void refreshToken_fail_invalidToken() {
		TokenRefreshRequest request = new TokenRefreshRequest("invalidToken");

		when(jwtUtil.validateToken("invalidToken")).thenReturn(false);

		assertThatThrownBy(() -> authService.refreshToken(request))
			.isInstanceOf(InvalidRefreshTokenException.class)
			.hasMessageContaining("유효하지 않은 인증정보");
	}

	@Test
	@DisplayName("리프레시 토큰 갱신 실패 - 저장된 토큰과 일치하지 않음")
	void refreshToken_fail_tokenMismatch() {
		TokenRefreshRequest request = new TokenRefreshRequest("incomingToken");

		when(jwtUtil.validateToken("incomingToken")).thenReturn(true);
		when(jwtUtil.getUserIdFromToken("incomingToken")).thenReturn(1L);
		when(refreshTokenService.findByUserId(1L)).thenReturn("storedDifferentToken");

		assertThatThrownBy(() -> authService.refreshToken(request))
			.isInstanceOf(InvalidRefreshTokenException.class)
			.hasMessageContaining("유효하지 않은 인증정보");
	}

	@Test
	@DisplayName("리프레시 토큰 갱신 실패 - 유저 없음")
	void refreshToken_fail_userNotFound() {
		TokenRefreshRequest request = new TokenRefreshRequest("refresh123");

		when(jwtUtil.validateToken("refresh123")).thenReturn(true);
		when(jwtUtil.getUserIdFromToken("refresh123")).thenReturn(99L);
		when(refreshTokenService.findByUserId(99L)).thenReturn("refresh123");
		when(userRepository.findById(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> authService.refreshToken(request))
			.isInstanceOf(UserNotFoundException.class)
			.hasMessageContaining("사용자를 찾을 수 없습니다");
	}

	@Test
	@DisplayName("리프레시 토큰 갱신 실패 - 유저 상태가 휴면일 경우")
	void refreshToken_fail_dormantUser() {
		TokenRefreshRequest request = new TokenRefreshRequest("refresh123");

		when(jwtUtil.validateToken("refresh123")).thenReturn(true);
		when(jwtUtil.getUserIdFromToken("refresh123")).thenReturn(1L);
		when(refreshTokenService.findByUserId(1L)).thenReturn("refresh123");

		User dormantUser = User.builder()
			.id(1L)
			.nickname("휴면유저")
			.status(UserStatus.DORMANT)
			.type(UserType.USER)
			.build();

		when(userRepository.findById(1L)).thenReturn(Optional.of(dormantUser));

		assertThatThrownBy(() -> authService.refreshToken(request))
			.isInstanceOf(DormantAccountException.class)
			.hasMessageContaining("비활성화 상태 계정입니다. 인증 후 활성화 해주세요.");
	}

	@Test
	@DisplayName("비밀번호 변경 실패 - 새 비밀번호와 확인 비밀번호 불일치")
	void changePassword_fail_newPasswordMismatch() {
		PasswordUpdateRequest req = new PasswordUpdateRequest();
		req.setCurrentPassword("currentPassword");
		req.setNewPassword("newPassword123");
		req.setConfirmNewPassword("differentPassword123");

		assertThatThrownBy(() -> authService.changePassword(1L, req))
			.isInstanceOf(ApplicationException.class)
			.hasMessageContaining("일치하지 않습니다");
	}

	@Test
	@DisplayName("비밀번호 변경 실패 - 현재 비밀번호 불일치")
	void changePassword_fail_currentPasswordMismatch() {
		PasswordUpdateRequest req = new PasswordUpdateRequest();
		req.setCurrentPassword("wrongCurrentPassword");
		req.setNewPassword("newPassword123");
		req.setConfirmNewPassword("newPassword123");

		User user = User.builder()
			.id(1L)
			.loginId("user1")
			.encodedPassword("encodedPassword") // 저장된 비밀번호
			.provider(UserProvider.BLUEBOOKTLE)
			.build();

		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		when(passwordEncoder.matches("wrongCurrentPassword", "encodedPassword")).thenReturn(false);

		assertThatThrownBy(() -> authService.changePassword(1L, req))
			.isInstanceOf(AuthenticationFailedException.class)
			.hasMessageContaining("현재 비밀번호가 일치하지 않습니다");
	}

	@Test
	@DisplayName("PAYCO 로그인 성공")
	void paycoLogin_success() {
		String code = "valid-code";

		PaycoTokenResponse tokenRes = new PaycoTokenResponse();
		tokenRes.setAccessToken("access-token");

		PaycoProfileMember member = new PaycoProfileMember();
		member.setIdNo("payco123");
		member.setName("홍길동");
		member.setEmail("test@payco.com");

		PaycoProfileData data = new PaycoProfileData();
		data.setMember(member);

		PaycoProfileResponse profileRes = new PaycoProfileResponse();
		profileRes.setData(data);

		User user = User.builder()
			.id(1L)
			.loginId("payco123")
			.nickname("홍길동")
			.provider(UserProvider.PAYCO)
			.status(UserStatus.ACTIVE)
			.type(UserType.USER)
			.build();

		when(paycoAuthClient.getToken(argThat(map ->
			map != null &&
				"valid-code".equals(map.get("code")) &&
				"authorization_code".equals(map.get("grant_type"))
		))).thenReturn(tokenRes);

		when(paycoApiClient.getProfile(eq("access-token"), nullable(String.class))).thenReturn(profileRes);

		when(userRepository.findByLoginId("payco123")).thenReturn(Optional.of(user));
		when(jwtUtil.createAccessToken(1L, "홍길동", UserType.USER)).thenReturn("access");
		when(jwtUtil.createRefreshToken(1L, "홍길동", UserType.USER)).thenReturn("refresh");
		when(jwtUtil.getRefreshTokenExpirationMillis()).thenReturn(1000L);

		TokenResponse response = authService.paycoLogin(code);

		assertThat(response.getAccessToken()).isEqualTo("access");
		assertThat(response.getRefreshToken()).isEqualTo("refresh");
		verify(refreshTokenService).save(1L, "refresh", 1000L);
		verify(eventPublisher).publishEvent(any(UserLoginEvent.class));
	}

	@Test
	@DisplayName("PAYCO 로그인 실패 - 사용자 ID 없음")
	void paycoLogin_fail_missingLoginId() {
		String code = "valid-code";

		PaycoTokenResponse tokenRes = new PaycoTokenResponse();
		tokenRes.setAccessToken("access-token");

		PaycoProfileMember member = new PaycoProfileMember();
		member.setIdNo(""); // loginId 비어있음

		PaycoProfileData data = new PaycoProfileData();
		data.setMember(member);

		PaycoProfileResponse profileRes = new PaycoProfileResponse();
		profileRes.setData(data);

		when(paycoAuthClient.getToken(any())).thenReturn(tokenRes);
		when(paycoApiClient.getProfile(eq("access-token"), nullable(String.class))).thenReturn(profileRes);

		assertThatThrownBy(() -> authService.paycoLogin(code))
			.isInstanceOf(ApplicationException.class)
			.hasMessageContaining("PAYCO 사용자 ID를 가져올 수 없습니다.");
	}

	@Test
	@DisplayName("PAYCO 로그인 실패 - 다른 방식으로 가입된 계정")
	void paycoLogin_fail_wrongProvider() {
		String code = "valid-code";

		PaycoTokenResponse tokenRes = new PaycoTokenResponse();
		tokenRes.setAccessToken("access-token");

		PaycoProfileMember member = new PaycoProfileMember();
		member.setIdNo("payco123");

		PaycoProfileData data = new PaycoProfileData();
		data.setMember(member);

		PaycoProfileResponse profileRes = new PaycoProfileResponse();
		profileRes.setData(data);

		User user = User.builder()
			.id(1L)
			.loginId("payco123")
			.nickname("홍길동")
			.provider(UserProvider.BLUEBOOKTLE)
			.status(UserStatus.ACTIVE)
			.type(UserType.USER)
			.build();

		when(paycoAuthClient.getToken(any())).thenReturn(tokenRes);
		when(paycoApiClient.getProfile(eq("access-token"), nullable(String.class))).thenReturn(profileRes);
		when(userRepository.findByLoginId("payco123")).thenReturn(Optional.of(user));

		assertThatThrownBy(() -> authService.paycoLogin(code))
			.isInstanceOf(AuthenticationFailedException.class)
			.hasMessageContaining("해당 ID는 이미 다른 방식으로 가입된 계정입니다.");
	}

	@Test
	@DisplayName("PAYCO 로그인 실패 - 비활성화된 계정")
	void paycoLogin_fail_inactiveUser() {
		String code = "valid-code";

		PaycoTokenResponse tokenRes = new PaycoTokenResponse();
		tokenRes.setAccessToken("access-token");

		PaycoProfileMember member = new PaycoProfileMember();
		member.setIdNo("payco123");

		PaycoProfileData data = new PaycoProfileData();
		data.setMember(member);

		PaycoProfileResponse profileRes = new PaycoProfileResponse();
		profileRes.setData(data);

		User user = User.builder()
			.id(1L)
			.loginId("payco123")
			.nickname("홍길동")
			.provider(UserProvider.PAYCO)
			.status(UserStatus.DORMANT)
			.type(UserType.USER)
			.build();

		when(paycoAuthClient.getToken(any())).thenReturn(tokenRes);
		when(paycoApiClient.getProfile(eq("access-token"), nullable(String.class))).thenReturn(profileRes);
		when(userRepository.findByLoginId("payco123")).thenReturn(Optional.of(user));

		assertThatThrownBy(() -> authService.paycoLogin(code))
			.isInstanceOf(AuthenticationFailedException.class)
			.hasMessageContaining("비활성화된 계정입니다.");
	}

	@Test
	@DisplayName("registerPaycoUser 실패 - 이미 존재하는 loginId")
	void registerPaycoUser_fail_loginIdExists() {
		PaycoProfileMember member = new PaycoProfileMember();
		member.setIdNo("payco123");

		when(userRepository.existsByLoginId("payco123")).thenReturn(true);

		assertThatThrownBy(() -> invokeRegisterPaycoUser(member))
			.isInstanceOf(LoginIdAlreadyExistsException.class)
			.hasMessageContaining("PAYCO ID 충돌");
	}

	@Test
	@DisplayName("registerPaycoUser 실패 - 이미 존재하는 이메일")
	void registerPaycoUser_fail_emailExists() {
		PaycoProfileMember member = new PaycoProfileMember();
		member.setIdNo("payco123");
		member.setEmail("existing@example.com");

		when(userRepository.existsByLoginId("payco123")).thenReturn(false);
		when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

		assertThatThrownBy(() -> invokeRegisterPaycoUser(member))
			.isInstanceOf(EmailAlreadyExistsException.class)
			.hasMessageContaining("이미 가입된 이메일");
	}

	@Test
	@DisplayName("registerPaycoUser 실패 - 기본 멤버십 등급 없음")
	void registerPaycoUser_fail_defaultMembershipMissing() {
		PaycoProfileMember member = new PaycoProfileMember();
		member.setIdNo("payco123");

		when(membershipLevelRepository.findByName("일반")).thenReturn(Optional.empty());

		assertThatThrownBy(() -> invokeRegisterPaycoUser(member))
			.isInstanceOf(ApplicationException.class)
			.hasMessageContaining("기본 멤버십 등급을 찾을 수 없습니다");
	}

	@Test
	@DisplayName("registerPaycoUser 실패 - 이름 없음")
	void registerPaycoUser_fail_nameMissing() {
		PaycoProfileMember member = new PaycoProfileMember();
		member.setIdNo("payco123");
		member.setEmail("abc@payco.com");
		member.setName(""); // 이름 없음

		when(userRepository.existsByLoginId("payco123")).thenReturn(false);
		when(userRepository.existsByEmail("abc@payco.com")).thenReturn(false);
		when(membershipLevelRepository.findByName("일반")).thenReturn(Optional.of(defaultMembershipLevel));

		assertThatThrownBy(() -> invokeRegisterPaycoUser(member))
			.isInstanceOf(ApplicationException.class)
			.hasMessageContaining("사용자 이름을 전달받지 못했습니다");
	}

	private void invokeRegisterPaycoUser(PaycoProfileMember member) {
		try {
			Method method = AuthServiceImpl.class.getDeclaredMethod("registerPaycoUser", PaycoProfileMember.class);
			method.setAccessible(true);
			method.invoke(authService, member);
		} catch (InvocationTargetException e) {
			throw (RuntimeException)e.getCause(); // 내부 예외 전달
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Nested
	@DisplayName("normalizePhoneNumber 테스트")
	class NormalizePhoneNumberTest {

		private AuthServiceImpl authService;

		@BeforeEach
		void setUp() {
			authService = Mockito.mock(AuthServiceImpl.class, CALLS_REAL_METHODS); // 실제 메서드 사용
		}

		@ParameterizedTest(name = "[{index}] {0} → {1}")
		@CsvSource({
			"821012345678,01012345678",
			"+82-10-1234-5678,01012345678",
			"01012345678,01012345678",
			"abc010-1234-5678def,01012345678",
			"8210123456,01000000000",
			"null,01000000000",
			"'',01000000000",
			"010-0000-0000,01000000000",
			"821055566677,01055566677"
		})
		void testNormalizePhoneNumber(String input, String expected) {
			if ("null".equals(input)) {
				input = null;
			}
			String result = invokeNormalizePhoneNumber(authService, input);
			assertThat(result).isEqualTo(expected);
		}

		// private method invoke
		private String invokeNormalizePhoneNumber(AuthServiceImpl authService, String input) {
			try {
				Method method = AuthServiceImpl.class.getDeclaredMethod("normalizePhoneNumber", String.class);
				method.setAccessible(true);
				return (String)method.invoke(authService, input);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	@DisplayName("requestPaycoToken 실패 - 예외 발생 시 ApplicationException으로 감쌈")
	@Test
	void requestPaycoToken_fail_throwWrappedException() throws Exception {
		// given
		String code = "invalid-code";
		Map<String, String> expectedFormParams = new HashMap<>();
		expectedFormParams.put("grant_type", "authorization_code");
		expectedFormParams.put("client_id", "test-client-id");
		expectedFormParams.put("client_secret", "test-client-secret");
		expectedFormParams.put("code", code);
		expectedFormParams.put("redirect_uri", "http://localhost/redirect");

		// 내부 필드 강제로 세팅
		ReflectionTestUtils.setField(authService, "paycoClientId", "test-client-id");
		ReflectionTestUtils.setField(authService, "paycoClientSecret", "test-client-secret");
		ReflectionTestUtils.setField(authService, "paycoRedirectUri", "http://localhost/redirect");

		doThrow(new RuntimeException("PAYCO 서버 오류"))
			.when(paycoAuthClient).getToken(expectedFormParams);

		// when
		Method method = AuthServiceImpl.class.getDeclaredMethod("requestPaycoToken", String.class);
		method.setAccessible(true);

		Throwable thrown = catchThrowable(() -> method.invoke(authService, code));

		// then
		assertThat(thrown).isInstanceOf(InvocationTargetException.class);
		Throwable cause = thrown.getCause();
		assertThat(cause).isInstanceOf(ApplicationException.class);
		assertThat(cause.getMessage()).contains("PAYCO 토큰 요청 중 오류 발생");
	}

	@Nested
	@DisplayName("requestPaycoProfile 테스트")
	class RequestPaycoProfileTest {

		@BeforeEach
		void setUp() {
			ReflectionTestUtils.setField(authService, "paycoClientId", "test-client-id");
		}

		@Test
		@DisplayName("성공 - 프로필 응답 정상")
		void requestPaycoProfile_success() {
			PaycoProfileMember member = new PaycoProfileMember();
			member.setIdNo("payco123");

			PaycoProfileData data = new PaycoProfileData();
			data.setMember(member);

			PaycoProfileResponse response = new PaycoProfileResponse();
			response.setData(data);

			when(paycoApiClient.getProfile("access-token", "test-client-id")).thenReturn(response);

			PaycoProfileResponse result = invokeRequestPaycoProfile();

			assertThat(result.getData().getMember().getIdNo()).isEqualTo("payco123");
		}

		@Test
		@DisplayName("실패 - 응답 파싱 실패 (null 포함)")
		void requestPaycoProfile_fail_parsing() {
			// given
			when(paycoApiClient.getProfile("access-token", "test-client-id"))
				.thenReturn(new PaycoProfileResponse()); // null member

			// when
			Throwable thrown = catchThrowable(this::invokeRequestPaycoProfile);

			// then
			assertThat(thrown).isInstanceOf(RuntimeException.class);
			assertThat(thrown.getCause()).isInstanceOf(InvocationTargetException.class);
			assertThat(thrown.getCause().getCause())
				.isInstanceOf(ApplicationException.class)
				.hasMessageContaining("프로필 응답 파싱 실패");
		}

		@Test
		@DisplayName("실패 - 예외 발생 (Feign 오류 등)")
		void requestPaycoProfile_fail_exception() {
			// given
			when(paycoApiClient.getProfile("access-token", "test-client-id"))
				.thenThrow(new RuntimeException("Feign error"));

			// when
			Throwable thrown = catchThrowable(this::invokeRequestPaycoProfile);

			// then
			assertThat(thrown).isInstanceOf(RuntimeException.class);
			assertThat(thrown.getCause()).isInstanceOf(InvocationTargetException.class);
			assertThat(thrown.getCause().getCause())
				.isInstanceOf(ApplicationException.class)
				.hasMessageContaining("프로필 요청 중 오류 발생");
		}

		// reflection으로 private 메서드 호출
		private PaycoProfileResponse invokeRequestPaycoProfile() {
			try {
				Method method = AuthServiceImpl.class.getDeclaredMethod("requestPaycoProfile", String.class);
				method.setAccessible(true);
				return (PaycoProfileResponse)method.invoke(authService, "access-token");
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

}