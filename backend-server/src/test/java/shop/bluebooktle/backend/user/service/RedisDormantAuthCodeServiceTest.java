package shop.bluebooktle.backend.user.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Duration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import shop.bluebooktle.backend.user.service.impl.RedisDormantAuthCodeServiceImpl;

@ExtendWith(MockitoExtension.class)
class RedisDormantAuthCodeServiceTest {

	@Mock
	private RedisTemplate<String, String> redisTemplate; // 키와 값을 String 형태로 저장할 수 있는 Redis

	@Mock
	private ValueOperations<String, String> valueOperations; // 각각의 요소가 String 형태인 자료들을 문자열 구조로 저장

	@InjectMocks
	private RedisDormantAuthCodeServiceImpl redisDormantAuthCodeService;

	private static final String DORMANT_AUTH_CODE_PREFIX = "dormantAuthCode:";
	private static final int AUTH_CODE_LENGTH = 6;
	private static final Duration ttl = Duration.ofMinutes(5);

	@Test
	@DisplayName("인증 코드 생성 및 저장 성공")
	void generateAndSaveAuthCode_success() {

		Long userId = 1L;
		String key = DORMANT_AUTH_CODE_PREFIX + userId;

		when(redisTemplate.opsForValue()).thenReturn(valueOperations);

		String result = redisDormantAuthCodeService.generateAndSaveAuthCode(userId);

		// 생성 확인
		assertNotNull(result);
		assertEquals(AUTH_CODE_LENGTH, result.length());
		assertTrue(result.matches("\\d{6}"));

		// 저장 확인
		verify(redisTemplate.opsForValue(), times(1)).set(key, result, ttl);
	}

	@Test
	@DisplayName("인증 코드 생성 실패 - 인증 코드 길이가 0보다 작은 경우")
	void generateAndSaveAuthCode_fail_authCodeLength_lessThanZero() throws NoSuchMethodException {
		Method method = redisDormantAuthCodeService.getClass()
			.getDeclaredMethod("generateNumericCode", int.class);
		method.setAccessible(true);

		Throwable exception = assertThrows(InvocationTargetException.class, () -> {
			method.invoke(redisDormantAuthCodeService, 0);
		});

		Throwable cause = exception.getCause();
		assertThat(cause).isInstanceOf(IllegalArgumentException.class)
			.hasMessage("인증 코드 길이는 0보다 커야 합니다.");
	}

	@Test
	@DisplayName("Redis에 저장된 인증 코드와 사용자가 제출한 코드가 동일한 경우")
	void verifyAuthCode_success() {
		Long userId = 1L;
		String submittedCode = "123456";
		String key = DORMANT_AUTH_CODE_PREFIX + userId;

		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.get(key)).thenReturn("123456");

		boolean result = redisDormantAuthCodeService.verifyAuthCode(userId, submittedCode);

		verify(valueOperations, times(1)).get(key);
		assertTrue(result);
	}

	@Test
	@DisplayName("생성된 인증 코드 폐기 성공")
	void deleteAuthCode_success() {
		Long userId = 1L;
		String key = DORMANT_AUTH_CODE_PREFIX + userId;

		redisDormantAuthCodeService.deleteAuthCode(userId);

		verify(redisTemplate, times(1)).delete(key);
	}
}
