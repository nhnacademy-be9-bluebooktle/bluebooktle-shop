package shop.bluebooktle.auth.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.auth.repository.MembershipLevelRepository;
import shop.bluebooktle.auth.repository.UserRepository;
import shop.bluebooktle.auth.repository.payco.PaycoApiClient;
import shop.bluebooktle.auth.repository.payco.PaycoAuthClient;
import shop.bluebooktle.auth.service.AccessTokenService;
import shop.bluebooktle.auth.service.AuthService;
import shop.bluebooktle.auth.service.RefreshTokenService;
import shop.bluebooktle.common.domain.auth.UserProvider;
import shop.bluebooktle.common.domain.auth.UserStatus;
import shop.bluebooktle.common.domain.auth.UserType;
import shop.bluebooktle.common.dto.auth.request.LoginRequest;
import shop.bluebooktle.common.dto.auth.request.PasswordUpdateRequest;
import shop.bluebooktle.common.dto.auth.request.PaycoProfileMember;
import shop.bluebooktle.common.dto.auth.request.PaycoProfileResponse;
import shop.bluebooktle.common.dto.auth.request.PaycoTokenResponse;
import shop.bluebooktle.common.dto.auth.request.SignupRequest;
import shop.bluebooktle.common.dto.auth.request.TokenRefreshRequest;
import shop.bluebooktle.common.dto.auth.response.TokenResponse;
import shop.bluebooktle.common.entity.auth.MembershipLevel;
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;
import shop.bluebooktle.common.exception.auth.AuthenticationFailedException;
import shop.bluebooktle.common.exception.auth.DormantAccountException;
import shop.bluebooktle.common.exception.auth.EmailAlreadyExistsException;
import shop.bluebooktle.common.exception.auth.InvalidRefreshTokenException;
import shop.bluebooktle.common.exception.auth.LoginIdAlreadyExistsException;
import shop.bluebooktle.common.exception.auth.UserNotFoundException;
import shop.bluebooktle.common.exception.auth.WithdrawnAccountException;
import shop.bluebooktle.common.util.JwtUtil;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AuthServiceImpl implements AuthService {

	private final UserRepository userRepository;
	private final MembershipLevelRepository membershipLevelRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;
	private final RefreshTokenService refreshTokenService;
	private final AccessTokenService accessTokenService;
	private final PaycoAuthClient paycoAuthClient;
	private final PaycoApiClient paycoApiClient;

	@Value("${oauth.payco.client-id}")
	private String paycoClientId;
	@Value("${oauth.payco.client-secret}")
	private String paycoClientSecret;
	@Value("${oauth.payco.redirect-uri}")
	private String paycoRedirectUri;

	@Override
	@Transactional
	public void signup(SignupRequest signupRequest) {
		if (userRepository.existsByLoginId(signupRequest.getLoginId())) {
			throw new LoginIdAlreadyExistsException();
		}
		if (userRepository.existsByEmail(signupRequest.getEmail())) {
			throw new EmailAlreadyExistsException();
		}

		MembershipLevel defaultLevel = membershipLevelRepository.findByName("일반")
			.orElseThrow(() -> new ApplicationException(ErrorCode.INTERNAL_SERVER_ERROR, "기본 멤버십 등급을 찾을 수 없습니다."));

		User user = User.builder()
			.loginId(signupRequest.getLoginId())
			.encodedPassword(passwordEncoder.encode(signupRequest.getPassword()))
			.name(signupRequest.getName())
			.email(signupRequest.getEmail())
			.nickname(signupRequest.getNickname())
			.phoneNumber(signupRequest.getPhoneNumber())
			.birth(signupRequest.getBirth())
			.status(UserStatus.ACTIVE)
			.type(UserType.USER)
			.provider(UserProvider.BLUEBOOKTLE)
			.membershipLevel(defaultLevel)
			.build();

		userRepository.save(user);
	}

	@Override
	@Transactional
	public TokenResponse login(LoginRequest loginRequest) {
		User user = userRepository.findByLoginId(loginRequest.getLoginId())
			.orElseThrow(AuthenticationFailedException::new);

		if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
			throw new AuthenticationFailedException();
		}
		if (user.getStatus() != UserStatus.ACTIVE) {
			switch (user.getStatus()) {
				case DORMANT:
					throw new DormantAccountException("휴면 계정입니다. 인증 후 활성화 해주세요.");
				case WITHDRAWN:
					throw new WithdrawnAccountException("탈퇴된 계정입니다.");
				default:
					throw new AuthenticationFailedException("비활성화된 계정입니다.");
			}
		}
		if (user.getProvider() == UserProvider.PAYCO) {
			throw new AuthenticationFailedException("PAYCO 계정입니다. PAYCO 로그인 버튼을 이용해주세요.");
		}

		user.updateLastLoginAt();
		userRepository.save(user);

		String accessToken = jwtUtil.createAccessToken(user.getId(), user.getNickname(), user.getType());
		String refreshToken = jwtUtil.createRefreshToken(user.getId(), user.getNickname(), user.getType());

		long refreshTokenExpirationMillis = jwtUtil.getRefreshTokenExpirationMillis();
		refreshTokenService.save(user.getId(), refreshToken, refreshTokenExpirationMillis);

		return new TokenResponse(accessToken, refreshToken);
	}

	@Override
	@Transactional
	public void logout(String accessToken) {
		Long userId = jwtUtil.getUserIdFromToken(accessToken);
		refreshTokenService.deleteByUserId(userId);
		accessTokenService.addToBlacklist(accessToken);
	}

	@Override
	@Transactional
	public TokenResponse refreshToken(TokenRefreshRequest tokenRefreshRequest) {
		String refreshToken = tokenRefreshRequest.getRefreshToken();
		if (!jwtUtil.validateToken(refreshToken)) {
			throw new InvalidRefreshTokenException();
		}
		Long userId = jwtUtil.getUserIdFromToken(refreshToken);
		String redisRefreshToken = refreshTokenService.findByUserId(userId);
		if (redisRefreshToken == null || !redisRefreshToken.equals(refreshToken)) {
			throw new InvalidRefreshTokenException();
		}
		User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
		if (user.getStatus() != UserStatus.ACTIVE) { /* ... 기존 예외 처리 ... */ }
		String newAccessToken = jwtUtil.createAccessToken(userId, user.getNickname(), user.getType());
		String newRefreshToken = jwtUtil.createRefreshToken(userId, user.getNickname(), user.getType());
		refreshTokenService.save(user.getId(), newRefreshToken, jwtUtil.getRefreshTokenExpirationMillis());
		return new TokenResponse(newAccessToken, newRefreshToken);
	}

	@Override
	@Transactional
	public void changePassword(Long userId, PasswordUpdateRequest request) {
		if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
			throw new ApplicationException(ErrorCode.INVALID_INPUT_VALUE, "새 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
		}

		User user = userRepository.findById(userId)
			.orElseThrow(UserNotFoundException::new);

		if (user.getProvider() != UserProvider.BLUEBOOKTLE) {
			throw new ApplicationException(ErrorCode.INVALID_INPUT_VALUE, "소셜 로그인 사용자는 비밀번호를 변경할 수 없습니다.");
		}

		if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
			throw new AuthenticationFailedException("현재 비밀번호가 일치하지 않습니다.");
		}

		user.updatePassword(passwordEncoder.encode(request.getNewPassword()));
		userRepository.save(user);
	}

	@Override
	@Transactional
	public TokenResponse paycoLogin(String code) {
		PaycoTokenResponse paycoToken = requestPaycoToken(code);
		PaycoProfileResponse profileResponse = requestPaycoProfile(paycoToken.getAccessToken());
		PaycoProfileMember paycoMember = profileResponse.getData().getMember();
		String loginId = paycoMember.getIdNo();

		if (!StringUtils.hasText(loginId)) {
			throw new ApplicationException(ErrorCode.AUTH_OAUTH_LOGIN_FAILED, "PAYCO 사용자 ID를 가져올 수 없습니다.");
		}

		User user = userRepository.findByLoginId(loginId)
			.orElseGet(() -> registerPaycoUser(paycoMember));

		if (user.getProvider() != UserProvider.PAYCO) {
			throw new AuthenticationFailedException("해당 ID는 이미 다른 방식으로 가입된 계정입니다.");
		}
		if (user.getStatus() != UserStatus.ACTIVE) {
			throw new AuthenticationFailedException("비활성화된 계정입니다.");
		}
		user.updateLastLoginAt();
		userRepository.save(user);

		String accessToken = jwtUtil.createAccessToken(user.getId(), user.getNickname(), user.getType());
		String refreshToken = jwtUtil.createRefreshToken(user.getId(), user.getNickname(), user.getType());

		long refreshTokenExpirationMillis = jwtUtil.getRefreshTokenExpirationMillis();
		refreshTokenService.save(user.getId(), refreshToken, refreshTokenExpirationMillis);

		log.info("PAYCO 로그인 성공. User ID: {}", user.getId());
		return new TokenResponse(accessToken, refreshToken);
	}

	private User registerPaycoUser(PaycoProfileMember paycoMember) {
		String loginId = paycoMember.getIdNo();
		String email = paycoMember.getEmail();
		String name = paycoMember.getName();
		String mobile = paycoMember.getMobile();
		String birthday = paycoMember.getBirthday();
		log.info("신규 PAYCO 사용자 등록 시작. Login ID: {}", loginId);

		if (userRepository.existsByLoginId(loginId)) {
			throw new LoginIdAlreadyExistsException("PAYCO ID 충돌. 잠시 후 다시 시도하세요.");
		}
		if (StringUtils.hasText(email) && userRepository.existsByEmail(email)) {
			throw new EmailAlreadyExistsException("이미 가입된 이메일입니다.");
		}

		MembershipLevel defaultLevel = membershipLevelRepository.findByName("일반")
			.orElseThrow(() -> new ApplicationException(ErrorCode.INTERNAL_SERVER_ERROR, "기본 멤버십 등급을 찾을 수 없습니다."));

		String nickname = "payco_" + loginId;
		String finalBirth = StringUtils.hasText(birthday) ? birthday.replaceAll("[^0-9]", "") : "00000000";
		String finalPhoneNumber = StringUtils.hasText(mobile) ? normalizePhoneNumber(mobile) : "01000000000";

		if ("00000000".equals(finalBirth) || "01000000000".equals(finalPhoneNumber)) {
			log.warn("PAYCO 신규 가입 시, birth/phoneNumber에 PAYCO 정보가 없거나 변환 실패하여 기본값을 사용합니다.");
		}

		User newUser = User.builder()
			.loginId(loginId)
			.encodedPassword(passwordEncoder.encode(UUID.randomUUID().toString()))
			.name(StringUtils.hasText(name) ? name : "PAYCO_USER")
			.email(email)
			.nickname(nickname)
			.birth(finalBirth)
			.phoneNumber(finalPhoneNumber)
			.status(UserStatus.ACTIVE)
			.type(UserType.USER)
			.provider(UserProvider.PAYCO)
			.membershipLevel(defaultLevel)
			.build();

		return userRepository.save(newUser);
	}

	private String normalizePhoneNumber(String paycoMobile) {
		if (paycoMobile == null)
			return "01000000000";
		String normalized = paycoMobile.replaceAll("[^0-9]", "");
		if (normalized.startsWith("8210") && normalized.length() == 12) {
			return "010" + normalized.substring(4);
		} else if (normalized.startsWith("010") && normalized.length() == 11) {
			return normalized;
		}
		return "01000000000";
	}

	private PaycoTokenResponse requestPaycoToken(String code) {
		Map<String, String> formParams = new HashMap<>();
		formParams.put("grant_type", "authorization_code");
		formParams.put("client_id", paycoClientId);
		formParams.put("client_secret", paycoClientSecret);
		formParams.put("code", code);
		formParams.put("redirect_uri", paycoRedirectUri);
		try {
			return paycoAuthClient.getToken(formParams);
		} catch (Exception e) {
			throw new ApplicationException(ErrorCode.AUTH_OAUTH_LOGIN_FAILED, "PAYCO 토큰 요청 중 오류 발생: " + e.getMessage());
		}
	}

	private PaycoProfileResponse requestPaycoProfile(String accessToken) {

		try {
			PaycoProfileResponse profileResponse = paycoApiClient.getProfile(accessToken, paycoClientId);
			if (profileResponse == null || profileResponse.getData() == null
				|| profileResponse.getData().getMember() == null
				|| profileResponse.getData().getMember().getIdNo() == null) {
				throw new ApplicationException(ErrorCode.AUTH_OAUTH_LOGIN_FAILED, "PAYCO 프로필 응답 파싱 실패 (Feign)");
			}
			return profileResponse;
		} catch (Exception e) {
			throw new ApplicationException(ErrorCode.AUTH_OAUTH_LOGIN_FAILED,
				"PAYCO 프로필 요청 중 오류 발생: " + e.getMessage());
		}
	}
}