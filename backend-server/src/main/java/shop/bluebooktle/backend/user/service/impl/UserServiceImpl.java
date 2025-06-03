package shop.bluebooktle.backend.user.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.user.client.AuthServerClient;
import shop.bluebooktle.backend.user.repository.MembershipLevelRepository;
import shop.bluebooktle.backend.user.repository.UserRepository;
import shop.bluebooktle.backend.user.service.DormantAuthCodeService;
import shop.bluebooktle.backend.user.service.UserService;
import shop.bluebooktle.common.domain.auth.UserStatus;
import shop.bluebooktle.common.dto.user.request.AdminUserUpdateRequest;
import shop.bluebooktle.common.dto.user.request.ReactivateDormantUserRequest;
import shop.bluebooktle.common.dto.user.request.UserSearchRequest;
import shop.bluebooktle.common.dto.user.request.UserUpdateRequest;
import shop.bluebooktle.common.dto.user.response.AddressResponse;
import shop.bluebooktle.common.dto.user.response.AdminUserResponse;
import shop.bluebooktle.common.dto.user.response.UserResponse;
import shop.bluebooktle.common.dto.user.response.UserTotalPointResponse;
import shop.bluebooktle.common.dto.user.response.UserWithAddressResponse;
import shop.bluebooktle.common.entity.auth.MembershipLevel;
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;
import shop.bluebooktle.common.exception.auth.UserNotFoundException;
import shop.bluebooktle.common.exception.membership.MembershipNotFoundException;
import shop.bluebooktle.common.exception.user.InvalidUserIdException;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	private final MembershipLevelRepository membershipLevelRepository;
	private final AuthServerClient authServerClient;
	private final DormantAuthCodeService dormantAuthCodeService;

	@Override
	@Transactional(readOnly = true)
	public UserResponse findByUserId(Long userId) {
		if (userId == null) {
			throw new InvalidUserIdException();
		}
		Optional<User> user = userRepository.findById(userId);
		if (user.isEmpty()) {
			throw new UserNotFoundException();
		}

		return UserResponse.fromEntity(user.get());
	}

	@Override
	public void updateUser(long id, UserUpdateRequest userUpdateRequest) {
		User user = userRepository.findById(id)
			.orElseThrow(UserNotFoundException::new);

		User updatedUser = User.builder()
			.id(id)
			.email(user.getEmail())
			.name(user.getName())
			.loginId(user.getLoginId())
			.phoneNumber(user.getPhoneNumber())
			.type(user.getType())
			.encodedPassword(user.getPassword())
			.membershipLevel(user.getMembershipLevel())
			.status(user.getStatus())
			.lastLoginAt(user.getLastLoginAt())
			.nickname(userUpdateRequest.getNickname())
			.birth(userUpdateRequest.getBirthDate())
			.provider(user.getProvider())
			.phoneNumber(userUpdateRequest.getPhoneNumber())
			.build();
		userRepository.save(updatedUser);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<AdminUserResponse> findUsers(UserSearchRequest request, Pageable pageable) {
		Page<User> userPage = userRepository.findUsersBySearchRequest(request, pageable);
		return userPage.map(AdminUserResponse::fromEntity);
	}

	@Override
	@Transactional(readOnly = true)
	public AdminUserResponse findUserByIdAdmin(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(UserNotFoundException::new);
		return AdminUserResponse.fromEntity(user);
	}

	@Override
	@Transactional
	public void updateUserAdmin(Long userId, AdminUserUpdateRequest request) {
		User user = userRepository.findById(userId)
			.orElseThrow(UserNotFoundException::new);

		MembershipLevel membershipLevel = null;
		if (request.getMembershipId() != null) {
			membershipLevel = membershipLevelRepository.findById(request.getMembershipId())
				.orElseThrow(() -> new MembershipNotFoundException("회원 등급을 찾을 수 없습니다: " + request.getMembershipId()));
		}

		user.updateAdminInfo(
			request.getName(),
			request.getNickname(),
			request.getEmail(),
			request.getPhoneNumber(),
			request.getBirthDate(),
			request.getUserType(),
			request.getUserStatus(),
			membershipLevel
		);

		userRepository.save(user);
	}

	@Override
	@Transactional(readOnly = true)
	public UserTotalPointResponse findUserTotalPoints(Long userId) {
		BigDecimal totalPoints = userRepository.findPointBalanceByLoginId(userId).orElse(BigDecimal.ZERO);
		return new UserTotalPointResponse(totalPoints);
	}

	@Override
	public UserWithAddressResponse findUserWithAddress(Long userId) {
		User user = userRepository.findUserWithAddresses(userId)
			.orElseThrow(UserNotFoundException::new);

		List<AddressResponse> addresses = user.getAddresses().stream()
			.map(a -> new AddressResponse(
				a.getId(),
				a.getAlias(),
				a.getRoadAddress(),
				a.getDetailAddress(),
				a.getPostalCode()
			)).toList();

		return new UserWithAddressResponse(
			user.getId(),
			user.getName(),
			user.getEmail(),
			user.getPhoneNumber(),
			user.getPointBalance(),
			addresses
		);
	}


	@Override
	@Transactional
	public void withdrawUser(Long userId, String accessTokenForAuthLogout) {
		if (userId == null) {
			throw new InvalidUserIdException("사용자 ID가 제공되지 않았습니다.");
		}
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new UserNotFoundException("탈퇴할 사용자를 찾을 수 없습니다. ID: " + userId));

		if (user.getStatus() == UserStatus.WITHDRAWN) {
			return;
		}

		userRepository.delete(user);

		if (accessTokenForAuthLogout != null && !accessTokenForAuthLogout.isBlank()) {
			try {
				authServerClient.logout(accessTokenForAuthLogout);
			} catch (Exception e) {
				//  front에서 탈퇴 후 로그아웃 처리하므로 로그아웃 실패 시 예외 처리 하지 않고 로그만 남김.
				log.warn("Backend: Auth 서버 로그아웃 호출 중 예외 발생. 사용자 ID: {}. 에러: {}.", userId,
					e.getMessage(), e);
			}
		}
	}

	@Override
	public void reactivateDormantUser(ReactivateDormantUserRequest request) {
		User user = userRepository.findByLoginId(request.getLoginId())
			.orElseThrow(UserNotFoundException::new);

		if (user.getStatus() != UserStatus.DORMANT) {
			throw new ApplicationException(ErrorCode.AUTH_NOT_DORMANT_ACCOUNT, "휴면 상태의 사용자가 아닙니다.");
		}

		if (!dormantAuthCodeService.verifyAuthCode(user.getId(), request.getAuthCode())) {
			throw new ApplicationException(ErrorCode.INVALID_INPUT_VALUE, "인증 코드가 유효하지 않거나 일치하지 않습니다.");
		}

		user.setStatus(UserStatus.ACTIVE);
		user.updateLastLoginAt();
		userRepository.save(user);

		dormantAuthCodeService.deleteAuthCode(user.getId());
	}

	@Override
	@Transactional
	public void issueDormantAuthCode(String loginId) {
		if (loginId == null) {
			throw new InvalidUserIdException();
		}
		User user = userRepository.findByLoginId(loginId)
			.orElseThrow(UserNotFoundException::new);

		if (user.getStatus() != UserStatus.DORMANT) {
			throw new ApplicationException(ErrorCode.AUTH_NOT_DORMANT_ACCOUNT, "휴면 상태의 사용자가 아니므로 인증 코드를 발급할 수 없습니다.");
		}

		String authCode = dormantAuthCodeService.generateAndSaveAuthCode(user.getId());
		// TODO: Dooray Message Sender를 통해 사용자에게 authCode 발송 로직 추가
	}
}
