package shop.bluebooktle.backend.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import shop.bluebooktle.backend.user.client.AuthServerClient;
import shop.bluebooktle.backend.user.client.VerificationMessageClient;
import shop.bluebooktle.backend.user.dto.DoorayMessagePayload;
import shop.bluebooktle.backend.user.repository.MembershipLevelRepository;
import shop.bluebooktle.backend.user.repository.UserRepository;
import shop.bluebooktle.backend.user.service.impl.UserServiceImpl;
import shop.bluebooktle.common.domain.auth.UserStatus;
import shop.bluebooktle.common.domain.auth.UserType;
import shop.bluebooktle.common.dto.user.request.AdminUserUpdateRequest;
import shop.bluebooktle.common.dto.user.request.ReactivateDormantUserRequest;
import shop.bluebooktle.common.dto.user.request.UserSearchRequest;
import shop.bluebooktle.common.dto.user.request.UserUpdateRequest;
import shop.bluebooktle.common.dto.user.response.AdminUserResponse;
import shop.bluebooktle.common.dto.user.response.UserResponse;
import shop.bluebooktle.common.dto.user.response.UserTotalPointResponse;
import shop.bluebooktle.common.dto.user.response.UserWithAddressResponse;
import shop.bluebooktle.common.entity.auth.Address;
import shop.bluebooktle.common.entity.auth.MembershipLevel;
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.auth.UserNotFoundException;
import shop.bluebooktle.common.exception.membership.MembershipNotFoundException;
import shop.bluebooktle.common.exception.user.InvalidUserIdException;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private MembershipLevelRepository membershipLevelRepository;

	@Mock
	private AuthServerClient authServerClient;

	@Mock
	private DormantAuthCodeService dormantAuthCodeService;

	@Mock
	private VerificationMessageClient verificationMessageClient;

	@InjectMocks
	private UserServiceImpl userService;

	@Test
	@DisplayName("사용자 아이디로 사용자 조회 성공")
	void findByUserId_success() {

		Long userId = 1L;

		User user = User.builder()
			.id(userId)
			.email("<EMAIL>")
			.phoneNumber("010-1234-5678")
			.build();

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));

		UserResponse userResponse = userService.findByUserId(userId);

		verify(userRepository).findById(userId);

		assertNotNull(userResponse);
		assertEquals(userId, userResponse.getId());
		assertEquals(user.getEmail(), userResponse.getEmail());
		assertEquals(user.getPhoneNumber(), userResponse.getPhoneNumber());
	}

	@Test
	@DisplayName("사용자 아이디로 사용자 조회 실패 - 사용자 아이디가 null인 경우")
	void findByUserId_fail_userId_null() {

		Long userId = null;

		assertThrows(InvalidUserIdException.class, () -> {
			userService.findByUserId(userId);
		});

		verify(userRepository, never()).findById(userId);
	}

	@Test
	@DisplayName("사용자 아이디로 사용자 조회 실패 - 사용자 아이디가 유효하지 않은 경우")
	void findByUserId_fail_userId_invalid() {
		Long userId = 1L;

		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		assertThrows(UserNotFoundException.class, () -> {
			userService.findByUserId(userId);
		});

		verify(userRepository).findById(userId);
	}

	@Test
	@DisplayName("사용자 아이디로 사용자 정보 수정 성공")
	void updateUser_success() {

		long userId = 1L;

		User user = User.builder()
			.id(userId)
			.nickname("nickname")
			.phoneNumber("phoneNumber")
			.birth("birth")
			.build();

		UserUpdateRequest userUpdateRequest = new UserUpdateRequest("updatedNickname", "updatedPhoneNumber", "updatedBirth");

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));

		userService.updateUser(userId, userUpdateRequest);

		verify(userRepository).findById(userId);
		verify(userRepository).save(user);

		ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
		verify(userRepository).save(userArgumentCaptor.capture());
		User updatedUser = userArgumentCaptor.getValue();

		assertEquals(userUpdateRequest.getNickname(), updatedUser.getNickname());
		assertEquals(userUpdateRequest.getPhoneNumber(), updatedUser.getPhoneNumber());
		assertEquals(userUpdateRequest.getBirthDate(), updatedUser.getBirth());
	}

	@Test
	@DisplayName("사용자 아이디로 사용자 정보 수정 실패 - 사용자 아이디가 유효하지 않은 경우")
	void updateUser_fail_userId_invalid() {

		long userId = 1L;

		User user = User.builder()
			.id(userId)
			.nickname("nickname")
			.phoneNumber("phoneNumber")
			.birth("birth")
			.build();

		UserUpdateRequest userUpdateRequest = new UserUpdateRequest("updatedNickname", "updatedPhoneNumber", "updatedBirth");

		when(userRepository.findById(999L)).thenReturn(Optional.empty());

		assertThrows(UserNotFoundException.class, () -> {
			userService.updateUser(999L, userUpdateRequest);
		});

		verify(userRepository).findById(999L);
		verify(userRepository, never()).save(any(User.class));
	}

	@Test
	@DisplayName("관리자의 사용자 목록 페이징 조회 성공")
	void findUsers_success() {

		UserSearchRequest userSearchRequest = new UserSearchRequest();
		Pageable pageable = PageRequest.of(0, 10);

		User user1 = User.builder().id(1L).build();
		User user2 = User.builder().id(2L).build();
		List<User> users = List.of(user1, user2);

		Page<User> page = new PageImpl<>(users, pageable, users.size());

		when(userRepository.findUsersBySearchRequest(userSearchRequest, pageable)).thenReturn(page);

		Page<AdminUserResponse> adminUserResponses = userService.findUsers(userSearchRequest, pageable);

		verify(userRepository).findUsersBySearchRequest(userSearchRequest, pageable);

		assertEquals(2, adminUserResponses.getTotalElements());
		assertEquals(user1.getId(), adminUserResponses.getContent().getFirst().getUserId());
		assertEquals(user2.getId(), adminUserResponses.getContent().getLast().getUserId());
		assertEquals(0, adminUserResponses.getNumber());
		assertEquals(10, adminUserResponses.getSize());
	}

	@Test
	@DisplayName("관리자의 사용자 목록 페이징 조회 성공 - 조회 결과가 없을 경우")
	void findUsers_success_emptyResult() {

		UserSearchRequest userSearchRequest = new UserSearchRequest();
		Pageable pageable = PageRequest.of(0, 10);

		Page<User> page = new PageImpl<>(List.of(), pageable, 0);

		when(userRepository.findUsersBySearchRequest(userSearchRequest, pageable)).thenReturn(page);

		Page<AdminUserResponse> adminUserResponses = userService.findUsers(userSearchRequest, pageable);

		verify(userRepository).findUsersBySearchRequest(userSearchRequest, pageable);
		assertEquals(0, adminUserResponses.getTotalElements());
		assertTrue(adminUserResponses.getContent().isEmpty());
	}

	@Test
	@DisplayName("관리자의 사용자 조회 성공")
	void findUserByIdAdmin_success() {

		Long userId = 1L;

		User user = User.builder().id(userId).build();

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));

		AdminUserResponse adminUserResponse = userService.findUserByIdAdmin(userId);

		verify(userRepository).findById(userId);

		assertNotNull(adminUserResponse);
		assertEquals(userId, adminUserResponse.getUserId());
	}

	@Test
	@DisplayName("관리자의 사용자 조회 실패 - 사용자 아이디가 유효하지 않은 경우")
	void findUserByIdAdmin_fail_userId_invalid() {

		Long userId = 1L;

		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		assertThrows(UserNotFoundException.class, () -> {
			userService.findUserByIdAdmin(userId);
		});

		verify(userRepository).findById(userId);
	}

	@Test
	@DisplayName("관리자의 사용자 수정 성공")
	void updateUserAdmin_success() {

		Long userId = 1L;

		User user = User.builder()
			.id(userId)
			.name("name")
			.nickname("nickname")
			.email("email")
			.phoneNumber("phoneNumber")
			.birth("birth")
			.type(UserType.USER)
			.status(UserStatus.ACTIVE)
			.build();

		MembershipLevel membershipLevel = new MembershipLevel("로얄",2, new BigDecimal("100000.00"), new BigDecimal("199999.99"));
		ReflectionTestUtils.setField(membershipLevel, "id", 1L);

		AdminUserUpdateRequest adminUserUpdateRequest = new AdminUserUpdateRequest();
		adminUserUpdateRequest.setName("updatedName");
		adminUserUpdateRequest.setNickname("updatedNickname");
		adminUserUpdateRequest.setEmail("updatedEmail");
		adminUserUpdateRequest.setPhoneNumber("updatedPhoneNumber");
		adminUserUpdateRequest.setBirthDate("updatedBirthDate");
		adminUserUpdateRequest.setUserType(UserType.USER);
		adminUserUpdateRequest.setUserStatus(UserStatus.ACTIVE);
		adminUserUpdateRequest.setMembershipId(1L);

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(membershipLevelRepository.findById(1L)).thenReturn(Optional.of(membershipLevel));

		userService.updateUserAdmin(userId, adminUserUpdateRequest);

		verify(userRepository).findById(userId);
		verify(membershipLevelRepository).findById(1L);

		ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
		verify(userRepository).save(userArgumentCaptor.capture());
		User updatedUser = userArgumentCaptor.getValue();

		assertEquals(adminUserUpdateRequest.getName(), updatedUser.getName());
		assertEquals(adminUserUpdateRequest.getNickname(), updatedUser.getNickname());
		assertEquals(adminUserUpdateRequest.getEmail(), updatedUser.getEmail());
		assertEquals(adminUserUpdateRequest.getPhoneNumber(), updatedUser.getPhoneNumber());
		assertEquals(adminUserUpdateRequest.getBirthDate(), updatedUser.getBirth());
		assertEquals(adminUserUpdateRequest.getMembershipId(), updatedUser.getMembershipLevel().getId());
	}

	@Test
	@DisplayName("관리자의 사용자 수정 실패 - 사용자 아이디가 유효하지 않은 경우")
	void updateUserAdmin_fail_userId_invalid() {

		when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

		assertThrows(UserNotFoundException.class, () -> {
			userService.updateUserAdmin(999L, new AdminUserUpdateRequest());
		});

		verify(userRepository).findById(anyLong());
	}

	@Test
	@DisplayName("관리자의 사용자 수정 실패 - 회원 등급 아이디가 null인 경우")
	void updateUserAdmin_fail_membershipId_null() {
		Long userId = 1L;

		User user = User.builder()
			.id(userId)
			.name("name")
			.nickname("nickname")
			.email("email")
			.phoneNumber("phoneNumber")
			.birth("birth")
			.type(UserType.USER)
			.status(UserStatus.ACTIVE)
			.build();

		AdminUserUpdateRequest adminUserUpdateRequest = new AdminUserUpdateRequest();
		adminUserUpdateRequest.setName("updatedName");
		adminUserUpdateRequest.setNickname("updatedNickname");
		adminUserUpdateRequest.setEmail("updatedEmail");
		adminUserUpdateRequest.setPhoneNumber("updatedPhoneNumber");
		adminUserUpdateRequest.setBirthDate("updatedBirthDate");
		adminUserUpdateRequest.setUserType(UserType.USER);
		adminUserUpdateRequest.setUserStatus(UserStatus.ACTIVE);

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));

		userService.updateUserAdmin(userId, adminUserUpdateRequest);

		verify(userRepository).findById(userId);

		ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
		verify(userRepository).save(userArgumentCaptor.capture());
		User updatedUser = userArgumentCaptor.getValue();

		assertEquals(adminUserUpdateRequest.getName(), updatedUser.getName());
		assertEquals(adminUserUpdateRequest.getNickname(), updatedUser.getNickname());
		assertEquals(adminUserUpdateRequest.getEmail(), updatedUser.getEmail());
		assertEquals(adminUserUpdateRequest.getPhoneNumber(), updatedUser.getPhoneNumber());
		assertEquals(adminUserUpdateRequest.getBirthDate(), updatedUser.getBirth());
	}

	@Test
	@DisplayName("관리자의 사용자 수정 실패 - 회원 등급을 찾을 수 없는 경우")
	void updateUserAdmin_fail_membershipLevel_notFound() {
		Long userId = 1L;

		User user = User.builder()
			.id(userId)
			.name("name")
			.nickname("nickname")
			.email("email")
			.phoneNumber("phoneNumber")
			.birth("birth")
			.type(UserType.USER)
			.status(UserStatus.ACTIVE)
			.build();

		AdminUserUpdateRequest adminUserUpdateRequest = new AdminUserUpdateRequest();
		adminUserUpdateRequest.setName("updatedName");
		adminUserUpdateRequest.setNickname("updatedNickname");
		adminUserUpdateRequest.setEmail("updatedEmail");
		adminUserUpdateRequest.setPhoneNumber("updatedPhoneNumber");
		adminUserUpdateRequest.setBirthDate("updatedBirthDate");
		adminUserUpdateRequest.setUserType(UserType.USER);
		adminUserUpdateRequest.setUserStatus(UserStatus.ACTIVE);
		adminUserUpdateRequest.setMembershipId(2L);

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(membershipLevelRepository.findById(adminUserUpdateRequest.getMembershipId())).thenReturn(Optional.empty());

		assertThrows(MembershipNotFoundException.class, () -> {
			userService.updateUserAdmin(userId, adminUserUpdateRequest);
		});

		verify(userRepository).findById(userId);
		verify(membershipLevelRepository).findById(2L);
	}

	@Test
	@DisplayName("사용자의 총 포인트 조회 성공 - 잔액이 있는 경우")
	void findUserTotalPoints_success_whenPointBalanceExists() {

		Long userId = 1L;
		User user = User.builder()
			.id(userId)
			.pointBalance(BigDecimal.valueOf(500.00))
			.build();

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(userRepository.findPointBalanceByLoginId(userId)).thenReturn(Optional.of(user.getPointBalance()));

		UserTotalPointResponse userTotalPointResponse = userService.findUserTotalPoints(userId);

		verify(userRepository).findById(userId);
		verify(userRepository).findPointBalanceByLoginId(userId);

		assertEquals(user.getPointBalance(), userTotalPointResponse.totalPointBalance());
	}

	@Test
	@DisplayName("사용자의 총 포인트 조회 성공 - 잔액이 없는 경우")
	void findUserTotalPoints_success_whenPointBalanceNotExists() {

		Long userId = 1L;
		User user = User.builder()
			.id(userId)
			.build();

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(userRepository.findPointBalanceByLoginId(userId)).thenReturn(Optional.of(user.getPointBalance()));

		UserTotalPointResponse userTotalPointResponse = userService.findUserTotalPoints(userId);

		verify(userRepository).findById(userId);
		verify(userRepository).findPointBalanceByLoginId(userId);

		assertEquals(user.getPointBalance(), BigDecimal.ZERO);
	}

	@Test
	@DisplayName("사용자의 총 포인트 조회 실패 - 사용자 아이디가 null인 경우")
	void findUserTotalPoints_fail_userId_null() {

		Long userId = null;

		assertThrows(IllegalArgumentException.class, () -> {
			userService.findUserTotalPoints(userId);
		});

		verify(userRepository, never()).findById(userId);
		verify(userRepository, never()).findPointBalanceByLoginId(userId);
	}

	@Test
	@DisplayName("사용자의 총 포인트 조회 실패 - 사용자 아이디가 유효하지 않은 경우")
	void findUserTotalPoints_fail_userId_invalid() {

		Long userId = 1L;

		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		assertThrows(UserNotFoundException.class, () -> {
			userService.findUserTotalPoints(userId);
		});

		verify(userRepository).findById(userId);
		verify(userRepository, never()).findPointBalanceByLoginId(userId);
	}

	@Test
	@DisplayName("사용자의 정보 (주소 포함) 조회 성공")
	void findUserWithAddress_success() {

		Address address1 = Address.builder()
			.alias("alias1")
			.roadAddress("roadAddress1")
			.detailAddress("detailAddress1")
			.postalCode("postalCode1")
			.build();
		ReflectionTestUtils.setField(address1, "id", 1L);

		Address address2 = Address.builder()
			.alias("alias2")
			.roadAddress("roadAddress2")
			.detailAddress("detailAddress2")
			.postalCode("postalCode2")
			.build();
		ReflectionTestUtils.setField(address2, "id", 2L);

		Long userId = 1L;

		User user = User.builder()
			.id(userId)
			.name("name")
			.email("email")
			.phoneNumber("phoneNumber")
			.pointBalance(BigDecimal.valueOf(500.00))
			.build();

		user.addAddress(address1);
		user.addAddress(address2);

		when(userRepository.findUserWithAddresses(userId)).thenReturn(Optional.of(user));

		UserWithAddressResponse userWithAddressResponse = userService.findUserWithAddress(userId);

		verify(userRepository).findUserWithAddresses(userId);

		assertEquals(user.getName(), userWithAddressResponse.name());
		assertEquals(user.getEmail(), userWithAddressResponse.email());
		assertEquals(user.getPhoneNumber(), userWithAddressResponse.phoneNumber());
		assertEquals(user.getPointBalance(), userWithAddressResponse.pointBalance());
		assertEquals(address1.getId(), userWithAddressResponse.addresses().get(0).getAddressId());
		assertEquals(address2.getId(), userWithAddressResponse.addresses().get(1).getAddressId());
	}

	@Test
	@DisplayName("사용자의 정보 (주소 포함) 조회 실패 - 사용자의 아이디가 유효하지 않을 때")
	void findUserWithAddress_fail_userId_invalid() {

		Long userId = 1L;

		when(userRepository.findUserWithAddresses(userId)).thenReturn(Optional.empty());

		assertThrows(UserNotFoundException.class, () -> {
			userService.findUserWithAddress(userId);
		});

		verify(userRepository).findUserWithAddresses(userId);
	}

	@Test
	@DisplayName("사용자 탈퇴 처리 성공")
	void withdrawUser_success() {

		Long userId = 1L;
		String accessTokenForAuthLogout = "accessTokenForAuthLogout";

		User user = User.builder()
			.id(userId)
			.status(UserStatus.ACTIVE)
			.build();

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));

		userService.withdrawUser(userId, accessTokenForAuthLogout);

		verify(userRepository).findById(userId);
		verify(userRepository).delete(user);
		verify(authServerClient).logout(accessTokenForAuthLogout);
	}

	@Test
	@DisplayName("사용자 탈퇴 처리 - 이미 탈퇴한 사용자일 때")
	void withdrawUser_already_withdrawn() {

		Long userId = 1L;
		String accessTokenForAuthLogout = "accessTokenForAuthLogout";

		User user = User.builder()
			.id(userId)
			.status(UserStatus.WITHDRAWN)
			.build();

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));

		userService.withdrawUser(userId, accessTokenForAuthLogout);

		verify(userRepository).findById(userId);
		verify(userRepository, never()).delete(user);
		verify(authServerClient, never()).logout(accessTokenForAuthLogout);
	}

	@Test
	@DisplayName("사용자 탈퇴 처리 - 로그아웃 실패")
	void withdrawUser_exception_with_logout() {

		Long userId = 1L;
		String accessTokenForAuthLogout = "accessTokenForAuthLogout";

		User user = User.builder()
			.id(userId)
			.status(UserStatus.ACTIVE)
			.build();

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		doThrow(new RuntimeException("로그아웃 실패")).when(authServerClient).logout(accessTokenForAuthLogout);

		userService.withdrawUser(userId, accessTokenForAuthLogout);

		verify(userRepository).findById(userId);
		verify(userRepository).delete(user);
		verify(authServerClient).logout(accessTokenForAuthLogout);
	}

	@Test
	@DisplayName("사용자 탈퇴 처리 실패 - 사용자 아이디가 null인 경우")
	void withdrawUser_fail_userId_null() {

		Long userId = null;
		String accessTokenForAuthLogout = "accessTokenForAuthLogout";

		assertThrows(InvalidUserIdException.class, () -> {
			userService.withdrawUser(userId, accessTokenForAuthLogout);
		});

		verify(userRepository, never()).findById(userId);
		verify(userRepository, never()).delete(any(User.class));
		verify(authServerClient, never()).logout(accessTokenForAuthLogout);
	}

	@Test
	@DisplayName("사용자 탈퇴 처리 실패 - 사용자 아이디가 유효하지 않은 경우")
	void withdrawUser_fail_userId_invalid() {

		Long userId = 1L;
		String accessTokenForAuthLogout = "accessTokenForAuthLogout";

		User user = User.builder()
			.id(1L)
			.status(UserStatus.ACTIVE)
			.build();

		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		assertThrows(UserNotFoundException.class, () -> {
			userService.withdrawUser(userId, accessTokenForAuthLogout);
		});

		verify(userRepository).findById(userId);
		verify(userRepository, never()).delete(user);
		verify(authServerClient, never()).logout(accessTokenForAuthLogout);
	}

	@Test
	@DisplayName("휴면 사용자 활성화 성공")
	void reactivateDormantUser_success() {

		try {
			Constructor<ReactivateDormantUserRequest> constructor =
				ReactivateDormantUserRequest.class.getDeclaredConstructor();
			constructor.setAccessible(true);

			ReactivateDormantUserRequest reactivateDormantUserRequest = constructor.newInstance();
			reactivateDormantUserRequest.setLoginId("loginId");
			reactivateDormantUserRequest.setAuthCode("authCode");

			Long userId = 1L;

			User user = User.builder()
				.id(userId)
				.loginId("loginId")
				.status(UserStatus.DORMANT)
				.build();

			when(userRepository.findByLoginId(reactivateDormantUserRequest.getLoginId())).thenReturn(Optional.of(user));
			when(dormantAuthCodeService.verifyAuthCode(user.getId(), reactivateDormantUserRequest.getAuthCode())).thenReturn(true);

			userService.reactivateDormantUser(reactivateDormantUserRequest);

			verify(userRepository).findByLoginId(reactivateDormantUserRequest.getLoginId());
			verify(dormantAuthCodeService).verifyAuthCode(user.getId(), reactivateDormantUserRequest.getAuthCode());

			ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
			verify(userRepository).save(userArgumentCaptor.capture());
			User savedUser = userArgumentCaptor.getValue();
			assertEquals(UserStatus.ACTIVE, savedUser.getStatus());

			verify(dormantAuthCodeService).deleteAuthCode(user.getId());

		} catch (Exception e) {
			fail("테스트 중 예외 발생" + e.getMessage());
		}
	}

	@Test
	@DisplayName("휴면 사용자 활성화 실패 - 사용자 아이디가 유효하지 않은 경우")
	void reactivateDormantUser_fail_userId_invalid() {

		try {
			Constructor<ReactivateDormantUserRequest> constructor =
				ReactivateDormantUserRequest.class.getDeclaredConstructor();
			constructor.setAccessible(true);

			ReactivateDormantUserRequest reactivateDormantUserRequest = constructor.newInstance();
			reactivateDormantUserRequest.setLoginId("loginId");
			reactivateDormantUserRequest.setAuthCode("authCode");

			when(userRepository.findByLoginId(reactivateDormantUserRequest.getLoginId())).thenReturn(Optional.empty());

			assertThrows(UserNotFoundException.class, () -> {
				userService.reactivateDormantUser(reactivateDormantUserRequest);
			});

			verify(userRepository).findByLoginId(reactivateDormantUserRequest.getLoginId());
			verify(dormantAuthCodeService, never()).verifyAuthCode(anyLong(), anyString());
			verify(userRepository, never()).save(any(User.class));
			verify(dormantAuthCodeService, never()).deleteAuthCode(anyLong());

		} catch (Exception e) {
			fail("테스트 중 예외 발생" + e.getMessage());
		}
	}

	@Test
	@DisplayName("휴면 사용자 활성화 실패 - 휴면 상태의 사용자가 아닌 경우")
	void reactivateDormantUser_fail_userStatus_notDormant() {

		try {
			Constructor<ReactivateDormantUserRequest> constructor =
				ReactivateDormantUserRequest.class.getDeclaredConstructor();
			constructor.setAccessible(true);

			ReactivateDormantUserRequest reactivateDormantUserRequest = constructor.newInstance();
			reactivateDormantUserRequest.setLoginId("loginId");
			reactivateDormantUserRequest.setAuthCode("authCode");

			Long userId = 1L;

			User user = User.builder()
				.id(userId)
				.loginId("loginId")
				.status(UserStatus.ACTIVE)
				.build();

			when(userRepository.findByLoginId(reactivateDormantUserRequest.getLoginId())).thenReturn(Optional.of(user));

			assertThrows(ApplicationException.class, () -> {
				userService.reactivateDormantUser(reactivateDormantUserRequest);
			});

			verify(userRepository).findByLoginId(reactivateDormantUserRequest.getLoginId());
			verify(dormantAuthCodeService, never()).verifyAuthCode(anyLong(), anyString());
			verify(userRepository, never()).save(any(User.class));
			verify(dormantAuthCodeService, never()).deleteAuthCode(anyLong());

		} catch (Exception e) {
			fail("테스트 중 예외 발생" + e.getMessage());
		}
	}

	@Test
	@DisplayName("휴면 사용자 활성화 실패 - 인증 코드가 유효하거나 일치하지 않은 경우")
	void reactivateDormantUser_fail_invalidAuthCode() {
		try {
			Constructor<ReactivateDormantUserRequest> constructor =
				ReactivateDormantUserRequest.class.getDeclaredConstructor();
			constructor.setAccessible(true);

			ReactivateDormantUserRequest reactivateDormantUserRequest = constructor.newInstance();
			reactivateDormantUserRequest.setLoginId("loginId");
			reactivateDormantUserRequest.setAuthCode("authCode");

			Long userId = 1L;

			User user = User.builder()
				.id(userId)
				.loginId("loginId")
				.status(UserStatus.DORMANT)
				.build();

			when(userRepository.findByLoginId(reactivateDormantUserRequest.getLoginId())).thenReturn(Optional.of(user));
			when(dormantAuthCodeService.verifyAuthCode(user.getId(), reactivateDormantUserRequest.getAuthCode())).thenReturn(false);

			assertThrows(ApplicationException.class, () -> {
				userService.reactivateDormantUser(reactivateDormantUserRequest);
			});

			verify(userRepository).findByLoginId(reactivateDormantUserRequest.getLoginId());
			verify(dormantAuthCodeService).verifyAuthCode(user.getId(), reactivateDormantUserRequest.getAuthCode());
			verify(userRepository, never()).save(user);
			verify(dormantAuthCodeService, never()).deleteAuthCode(user.getId());

		} catch (Exception e) {
			fail("테스트 중 예외 발생" + e.getMessage());
		}
	}

	@Test
	@DisplayName("휴면 사용자에게 인증 코드 발급 성공")
	void issueDormantAuthCode_success() {

		String loginId = "loginId";

		User user = User.builder()
			.id(1L)
			.loginId(loginId)
			.status(UserStatus.DORMANT)
			.build();

		when(userRepository.findByLoginId(loginId)).thenReturn(Optional.of(user));

		userService.issueDormantAuthCode(loginId);

		verify(userRepository).findByLoginId(loginId);
		verify(dormantAuthCodeService).generateAndSaveAuthCode(user.getId());
		verify(verificationMessageClient).sendMessage(any(DoorayMessagePayload.class));
	}

	@Test
	@DisplayName("휴면 사용자에게 인증 코드 발급 실패 - 로그인 아이디가 null인 경우")
	void issueDormantAuthCode_fail_loginId_null() {

		String loginId = null;

		assertThrows(InvalidUserIdException.class, () -> {
			userService.issueDormantAuthCode(loginId);
		});

		verify(userRepository, never()).findByLoginId(loginId);
		verify(dormantAuthCodeService, never()).generateAndSaveAuthCode(anyLong());
		verify(verificationMessageClient, never()).sendMessage(any(DoorayMessagePayload.class));
	}

	@Test
	@DisplayName("휴면 사용자에게 인증 코드 발급 실패 - 로그인 아이디가 유효하지 않은 경우")
	void issueDormantAuthCode_fail_loginId_invalid() {

		String loginId = "loginId";

		User user = User.builder()
			.id(1L)
			.loginId(loginId)
			.status(UserStatus.DORMANT)
			.build();

		when(userRepository.findByLoginId(loginId)).thenReturn(Optional.empty());

		assertThrows(UserNotFoundException.class, () -> {
			userService.issueDormantAuthCode(loginId);
		});

		verify(userRepository).findByLoginId(loginId);
		verify(dormantAuthCodeService, never()).generateAndSaveAuthCode(anyLong());
		verify(verificationMessageClient, never()).sendMessage(any(DoorayMessagePayload.class));

	}

	@Test
	@DisplayName("휴면 사용자에게 인증 코드 발급 실패 - 사용자의 상태가 휴면 상태가 아닌 경우")
	void issueDormantAuthCode_fail_userStatus_notDormant() {

		String loginId = "loginId";

		User user = User.builder()
			.id(1L)
			.loginId(loginId)
			.status(UserStatus.ACTIVE)
			.build();

		when(userRepository.findByLoginId(loginId)).thenReturn(Optional.of(user));

		assertThrows(ApplicationException.class, () -> {
			userService.issueDormantAuthCode(loginId);
		});

		verify(userRepository).findByLoginId(loginId);
		verify(dormantAuthCodeService, never()).generateAndSaveAuthCode(user.getId());
		verify(verificationMessageClient, never()).sendMessage(any(DoorayMessagePayload.class));
	}

}
