package shop.bluebooktle.backend.user.service.impl;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.user.repository.MembershipLevelRepository;
import shop.bluebooktle.backend.user.repository.UserRepository;
import shop.bluebooktle.backend.user.service.UserService;
import shop.bluebooktle.common.dto.user.request.AdminUserUpdateRequest;
import shop.bluebooktle.common.dto.user.request.UserSearchRequest;
import shop.bluebooktle.common.dto.user.request.UserUpdateRequest;
import shop.bluebooktle.common.dto.user.response.AdminUserResponse;
import shop.bluebooktle.common.dto.user.response.UserResponse;
import shop.bluebooktle.common.dto.user.response.UserTotalPointResponse;
import shop.bluebooktle.common.entity.auth.MembershipLevel;
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.exception.auth.UserNotFoundException;
import shop.bluebooktle.common.exception.membership.MembershipNotFoundException;
import shop.bluebooktle.common.exception.user.InvalidUserIdException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	private final MembershipLevelRepository membershipLevelRepository;

	@Override
	@Transactional
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
			.phoneNumber(userUpdateRequest.getPhoneNumber())
			.build();
		userRepository.save(updatedUser);
	}

	@Override
	@Transactional
	public Page<AdminUserResponse> findUsers(UserSearchRequest request, Pageable pageable) {
		Page<User> userPage = userRepository.findUsersBySearchRequest(request, pageable);
		return userPage.map(AdminUserResponse::fromEntity);
	}

	@Override
	@Transactional
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
	public UserTotalPointResponse findUserTotalPoints(Long userId) {
		BigDecimal totalPoints = userRepository.findPointBalanceByLoginId(userId).orElse(BigDecimal.ZERO);
		return new UserTotalPointResponse(totalPoints);
	}
}
