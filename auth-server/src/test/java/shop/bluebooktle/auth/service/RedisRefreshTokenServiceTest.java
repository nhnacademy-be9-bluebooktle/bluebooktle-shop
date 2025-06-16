package shop.bluebooktle.auth.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import shop.bluebooktle.auth.service.impl.RedisRefreshTokenServiceImpl;

@ExtendWith(MockitoExtension.class)
class RedisRefreshTokenServiceTest {

	@Mock
	private RedisTemplate<String, String> redisTemplate;

	@Mock
	private ValueOperations<String, String> valueOperations;

	@InjectMocks
	private RedisRefreshTokenServiceImpl refreshTokenService;

	private static final Long USER_ID = 1L;
	private static final String REFRESH_TOKEN = "mock-refresh-token";
	private static final long TTL_MILLIS = 60000L;

	@Test
	@DisplayName("save - 리프레시 토큰 저장 및 TTL 설정")
	void save_refreshToken_withTTL() {
		lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);

		refreshTokenService.save(USER_ID, REFRESH_TOKEN, TTL_MILLIS);

		verify(redisTemplate.opsForValue()).set("refreshToken:" + USER_ID, REFRESH_TOKEN, TTL_MILLIS,
			TimeUnit.MILLISECONDS);
	}

	@Test
	@DisplayName("findByUserId - Redis에서 리프레시 토큰 조회")
	void find_refreshToken_byUserId() {
		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.get("refreshToken:" + USER_ID)).thenReturn(REFRESH_TOKEN);

		String result = refreshTokenService.findByUserId(USER_ID);

		assertThat(result).isEqualTo(REFRESH_TOKEN);
	}

	@Test
	@DisplayName("deleteByUserId - Redis에서 리프레시 토큰 삭제")
	void delete_refreshToken_byUserId() {
		refreshTokenService.deleteByUserId(USER_ID);

		verify(redisTemplate).delete("refreshToken:" + USER_ID);
	}
}
