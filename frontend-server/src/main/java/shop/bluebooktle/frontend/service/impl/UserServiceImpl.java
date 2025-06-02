package shop.bluebooktle.frontend.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.user.request.AdminUserUpdateRequest;
import shop.bluebooktle.common.dto.user.request.UserSearchRequest;
import shop.bluebooktle.common.dto.user.request.UserUpdateRequest;
import shop.bluebooktle.common.dto.user.response.AdminUserResponse;
import shop.bluebooktle.common.dto.user.response.UserResponse;
import shop.bluebooktle.common.dto.user.response.UserTotalPointResponse;
import shop.bluebooktle.common.dto.user.response.UserWithAddressResponse;
import shop.bluebooktle.frontend.repository.UserRepository;
import shop.bluebooktle.frontend.service.UserService;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;

	@Override
	public UserResponse getMe() {
		return userRepository.getMe();
	}

	@Override
	public void updateUser(long id, UserUpdateRequest userUpdateRequest) {
		userRepository.updateUser(id, userUpdateRequest);
	}

	@Override
	public Page<AdminUserResponse> listUsers(UserSearchRequest request, Pageable pageable) {
		String sortParam = pageable.getSort().stream()
			.map(order -> order.getProperty() + "," + order.getDirection().name())
			.collect(Collectors.joining(","));

		if (sortParam.isEmpty()) {
			sortParam = "id,DESC";
		}

		PaginationData<AdminUserResponse> response = userRepository.listUsers(
			request.getSearchField(),
			request.getSearchKeyword(),
			request.getUserTypeFilter(),
			request.getUserStatusFilter(),
			pageable.getPageNumber(),
			pageable.getPageSize(),
			sortParam
		);

		List<AdminUserResponse> users = response.getContent();
		return new PageImpl<>(users, pageable, response.getTotalElements());

	}

	@Override
	public AdminUserResponse getUserDetail(Long userId) {
		return userRepository.getUserDetail(userId);

	}

	@Override
	public void updateUser(Long userId, AdminUserUpdateRequest request) {
		userRepository.updateUser(userId, request);
	}

	@Override
	public UserTotalPointResponse getUserTotalPoints() {
		return userRepository.getUserTotalPoints();
	}

	@Override
	public UserWithAddressResponse getUserWithAddresses() {
		return userRepository.getUserWithAddresses();
	}
}
