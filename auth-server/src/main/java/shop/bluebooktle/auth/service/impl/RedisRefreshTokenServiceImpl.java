package shop.bluebooktle.auth.service.impl;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import shop.bluebooktle.auth.service.RefreshTokenService;

@Service
public class RedisRefreshTokenServiceImpl implements RefreshTokenService {
	private final RedisTemplate<String, String> redisTemplate;

	public RedisRefreshTokenServiceImpl(RedisTemplate<String, String> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public void save(Long userId, String refreshToken, long ttlMillis) {
		redisTemplate.opsForValue().set("refreshToken:" + userId, refreshToken, ttlMillis, TimeUnit.MILLISECONDS);
	}

	public String findByUserId(Long userId) {
		return redisTemplate.opsForValue().get("refreshToken:" + userId);
	}

	public void deleteByUserId(Long userId) {
		redisTemplate.delete("refreshToken:" + userId);
	}
}