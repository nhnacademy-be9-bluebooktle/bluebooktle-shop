package shop.bluebooktle.auth.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import shop.bluebooktle.auth.service.impl.RedisAccessTokenServiceImpl;
import shop.bluebooktle.common.util.JwtUtil;

@ExtendWith(MockitoExtension.class)
class RedisAccessTokenServiceTest {

	@Mock
	private RedisTemplate<String, String> redisTemplate;

	@Mock
	private ValueOperations<String, String> valueOperations;

	@Mock
	private JwtUtil jwtUtil;

	@InjectMocks
	private RedisAccessTokenServiceImpl accessTokenService;

	private static final String ACCESS_TOKEN = "dummy-token";
	private static final String BLACKLIST_KEY = "blacklist:" + ACCESS_TOKEN;

	@BeforeEach
	void setUp() {
		lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
	}

	@Test
	@DisplayName("isBlacklisted - 토큰이 블랙리스트에 있는 경우 true 반환")
	void isBlacklisted_true() {
		when(redisTemplate.hasKey(BLACKLIST_KEY)).thenReturn(true);

		boolean result = accessTokenService.isBlacklisted(ACCESS_TOKEN);

		assertThat(result).isTrue();
		verify(redisTemplate).hasKey(BLACKLIST_KEY);
	}

	@Test
	@DisplayName("isBlacklisted - 토큰이 블랙리스트에 없는 경우 false 반환")
	void isBlacklisted_false() {
		when(redisTemplate.hasKey(BLACKLIST_KEY)).thenReturn(false);

		boolean result = accessTokenService.isBlacklisted(ACCESS_TOKEN);

		assertThat(result).isFalse();
		verify(redisTemplate).hasKey(BLACKLIST_KEY);
	}

	@Test
	@DisplayName("addToBlacklist - 남은 시간이 0보다 크면 Redis에 저장")
	void addToBlacklist_positiveRemainingTime() {
		when(jwtUtil.getRemainingTimeMillis(ACCESS_TOKEN)).thenReturn(5000L);

		accessTokenService.addToBlacklist(ACCESS_TOKEN);

		verify(redisTemplate.opsForValue()).set(eq(BLACKLIST_KEY), eq("true"), eq(Duration.ofMillis(5000L)));
	}

	@Test
	@DisplayName("addToBlacklist - 남은 시간이 0 이하면 Redis에 저장하지 않음")
	void addToBlacklist_zeroRemainingTime() {
		when(jwtUtil.getRemainingTimeMillis(ACCESS_TOKEN)).thenReturn(0L);

		accessTokenService.addToBlacklist(ACCESS_TOKEN);

		verify(valueOperations, never()).set(anyString(), anyString(), any(Duration.class));
	}
}
