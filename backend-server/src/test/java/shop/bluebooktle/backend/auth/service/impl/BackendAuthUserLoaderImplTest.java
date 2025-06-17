package shop.bluebooktle.backend.auth.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import shop.bluebooktle.backend.user.repository.UserRepository;
import shop.bluebooktle.common.domain.auth.UserStatus;
import shop.bluebooktle.common.domain.auth.UserType;
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.exception.auth.UserNotFoundException;
import shop.bluebooktle.common.security.UserPrincipal;

@ExtendWith(MockitoExtension.class)
class BackendAuthUserLoaderImplTest {

	@InjectMocks
	private BackendAuthUserLoaderImpl backendAuthUserLoader;

	@Mock
	private UserRepository userRepository;

	@Test
	@DisplayName("사용자 ID로 UserPrincipal 로드 성공")
	void loadUserById_success() {
		// given
		Long userId = 1L;
		User mockUser = User.builder()
			.id(userId)
			.loginId("testuser")
			.nickname("테스트유저")
			.type(UserType.USER)
			.status(UserStatus.ACTIVE)
			.build();

		// userRepository.findById가 mockUser를 포함한 Optional을 반환하도록 설정
		when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

		// when
		UserPrincipal userPrincipal = backendAuthUserLoader.loadUserById(userId);

		// then
		assertNotNull(userPrincipal);
		assertEquals(userId, userPrincipal.getUserId());
		assertEquals("testuser", userPrincipal.getLoginId());
		assertEquals("테스트유저", userPrincipal.getNickname());
		assertEquals(UserType.USER, userPrincipal.getUserType());
		assertEquals(UserStatus.ACTIVE, userPrincipal.getUserStatus());

		// userRepository.findById가 1번 호출되었는지 검증
		verify(userRepository, times(1)).findById(userId);
	}

	@Test
	@DisplayName("사용자 ID로 UserPrincipal 로드 실패 - 사용자를 찾을 수 없음")
	void loadUserById_fail_userNotFound() {
		// given
		Long userId = 99L;

		// userRepository.findById가 비어있는 Optional을 반환하도록 설정
		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		// when & then
		UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
			backendAuthUserLoader.loadUserById(userId);
		});

		// 예외 메시지가 기대한 대로인지 확인
		assertTrue(exception.getMessage().contains("사용자를 찾을 수 없습니다 (ID: " + userId + ")"));

		// userRepository.findById가 1번 호출되었는지 검증
		verify(userRepository, times(1)).findById(userId);
	}

	@Test
	@DisplayName("사용자 ID로 UserPrincipal 로드 실패 - 데이터베이스 오류")
	void loadUserById_fail_databaseError() {
		// given
		Long userId = 1L;
		RuntimeException dbException = new RuntimeException("DB connection failed");

		// userRepository.findById 호출 시 dbException 예외를 던지도록 설정
		when(userRepository.findById(userId)).thenThrow(dbException);

		// when & then
		UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
			backendAuthUserLoader.loadUserById(userId);
		});

		// 예외 메시지가 기대한 대로인지 확인
		assertTrue(exception.getMessage().contains("AuthServer에서 사용자 정보 조회 중 오류 발생 (ID: " + userId + ")"));
		// 원인(cause) 예외가 원래 발생했던 dbException인지 확인
		assertEquals(dbException, exception.getCause());

		// userRepository.findById가 1번 호출되었는지 검증
		verify(userRepository, times(1)).findById(userId);
	}
}