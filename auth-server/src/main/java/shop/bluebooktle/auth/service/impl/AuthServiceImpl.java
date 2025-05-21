package shop.bluebooktle.auth.service.impl;

import java.math.BigDecimal;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.auth.repository.MembershipLevelRepository;
import shop.bluebooktle.auth.repository.UserRepository;
import shop.bluebooktle.auth.service.AuthService;
import shop.bluebooktle.auth.service.RefreshTokenService;
import shop.bluebooktle.common.domain.auth.UserStatus;
import shop.bluebooktle.common.domain.auth.UserType;
import shop.bluebooktle.common.dto.auth.request.LoginRequest;
import shop.bluebooktle.common.dto.auth.request.SignupRequest;
import shop.bluebooktle.common.dto.auth.request.TokenRefreshRequest;
import shop.bluebooktle.common.dto.auth.response.TokenResponse;
import shop.bluebooktle.common.entity.auth.MembershipLevel;
import shop.bluebooktle.common.entity.auth.User;
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
public class AuthServiceImpl implements AuthService {

	private final UserRepository userRepository;
	private final MembershipLevelRepository membershipLevelRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;
	private final RefreshTokenService refreshTokenService;

	@Override
	@Transactional
	public void signup(SignupRequest signupRequest) {
		if (userRepository.existsByLoginId(signupRequest.getLoginId())) {
			throw new LoginIdAlreadyExistsException();
		}
		if (userRepository.existsByEmail(signupRequest.getEmail())) {
			throw new EmailAlreadyExistsException();
		}

		MembershipLevel defaultLevel = membershipLevelRepository.findById(1L)
			.orElseGet(() -> {
				MembershipLevel newLevel = MembershipLevel.builder()
					.name("일반")
					.rate(1)
					.maxNetSpent(BigDecimal.valueOf(100000L))
					.minNetSpent(BigDecimal.valueOf(0L))
					.build();
				return membershipLevelRepository.save(newLevel);
			});

		User user = User.builder()
			.loginId(signupRequest.getLoginId())
			.encodedPassword(passwordEncoder.encode(signupRequest.getPassword()))
			.name(signupRequest.getName())
			.email(signupRequest.getEmail())
			.nickname(signupRequest.getNickname())
			.phoneNumber(signupRequest.getPhoneNumber())
			.birth(signupRequest.getBirth())
			.status(UserStatus.ACTIVE)
			.membershipLevel(defaultLevel)
			.build();

		User savedUser = userRepository.save(user);
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

		user.updateLastLoginAt();
		userRepository.save(user);

		String accessToken = jwtUtil.createAccessToken(user.getId(), user.getNickname(), user.getType());
		String refreshToken = jwtUtil.createRefreshToken(user.getId(), user.getNickname(), user.getType());

		long refreshTokenExpirationMillis = jwtUtil.getRefreshTokenExpirationMillis();
		refreshTokenService.save(user.getId(), refreshToken, refreshTokenExpirationMillis);

		return new TokenResponse(accessToken, refreshToken);
	}

	@Override
	public TokenResponse refreshToken(TokenRefreshRequest tokenRefreshRequest) {
		String refreshToken = tokenRefreshRequest.getRefreshToken();

		if (!jwtUtil.validateToken(refreshToken)) {
			throw new InvalidRefreshTokenException();
		}

		String userNickname = jwtUtil.getUserNicknameFromToken(refreshToken);
		Long userId = jwtUtil.getUserIdFromToken(refreshToken);
		UserType userType = jwtUtil.getUserTypeFromToken(refreshToken);

		User user = userRepository.findById(userId)
			.orElseThrow(UserNotFoundException::new);

		if (user.getStatus() == UserStatus.DORMANT) {
			throw new DormantAccountException("휴면 계정입니다. 로그인이 필요합니다.");
		} else if (user.getStatus() == UserStatus.WITHDRAWN) {
			throw new WithdrawnAccountException("탈퇴된 계정입니다.");
		}

		String newAccessToken = jwtUtil.createAccessToken(userId, userNickname, userType);
		String newRefreshToken = jwtUtil.createRefreshToken(userId, userNickname, userType);

		long refreshTokenExpirationMillis = jwtUtil.getRefreshTokenExpirationMillis();
		refreshTokenService.save(user.getId(), newRefreshToken, refreshTokenExpirationMillis);
		refreshTokenService.deleteByUserId(userId);

		return new TokenResponse(newAccessToken, newRefreshToken);
	}
}