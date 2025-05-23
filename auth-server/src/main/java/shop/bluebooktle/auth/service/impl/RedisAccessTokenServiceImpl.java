package shop.bluebooktle.auth.service.impl;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.auth.service.AccessTokenService;
import shop.bluebooktle.common.util.JwtUtil;

@Service
@RequiredArgsConstructor
public class RedisAccessTokenServiceImpl implements AccessTokenService {
	private final RedisTemplate<String, String> redisTemplate;
	private final JwtUtil jwtUtil;

	private static final String BLACKLIST_KEY = "blacklist:";

	@Override
	public boolean isBlacklisted(String accessToken) {
		String redisKey = BLACKLIST_KEY + accessToken;
		Boolean hasKey = redisTemplate.hasKey(redisKey);
		return Boolean.TRUE.equals(hasKey);
	}

	@Override
	public void addToBlacklist(String accessToken) {
		long remainingMillis = jwtUtil.getRemainingTimeMillis(accessToken);
		if (remainingMillis > 0) {
			String redisKey = BLACKLIST_KEY + accessToken;
			redisTemplate.opsForValue().set(redisKey, "true", Duration.ofMillis(remainingMillis));
			System.out.println("Token " + accessToken + " blacklisted for " + remainingMillis + " ms.");
		}
	}
}
