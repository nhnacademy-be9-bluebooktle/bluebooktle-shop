package shop.bluebooktle.backend.user.service.impl; // RefreshTokenService 구현체와 동일한 패키지 또는 적절히 조정

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.user.service.DormantAuthCodeService;

@Service
@RequiredArgsConstructor
public class RedisDormantAuthCodeServiceImpl implements DormantAuthCodeService {

	private final RedisTemplate<String, String> redisTemplate;
	private static final String DORMANT_AUTH_CODE_PREFIX = "dormantAuthCode:";
	private static final int AUTH_CODE_LENGTH = 6;
	private static final Duration ttl = Duration.ofMinutes(5);

	@Override
	public String generateAndSaveAuthCode(Long userId) {
		String authCode = generateNumericCode(AUTH_CODE_LENGTH);
		String key = DORMANT_AUTH_CODE_PREFIX + userId;
		redisTemplate.opsForValue().set(key, authCode, ttl);
		return authCode;
	}

	@Override
	public boolean verifyAuthCode(Long userId, String submittedCode) {
		String key = DORMANT_AUTH_CODE_PREFIX + userId;
		String storedCode = redisTemplate.opsForValue().get(key);
		return Objects.equals(storedCode, submittedCode);
	}

	@Override
	public void deleteAuthCode(Long userId) {
		String key = DORMANT_AUTH_CODE_PREFIX + userId;
		redisTemplate.delete(key);
	}

	private String generateNumericCode(int length) {
		if (length <= 0) {
			throw new IllegalArgumentException("인증 코드 길이는 0보다 커야 합니다.");
		}
		long min = (long)Math.pow(10, length - 1);
		long max = (long)Math.pow(10, length) - 1;
		long randomNum = ThreadLocalRandom.current().nextLong(min, max + 1);
		return String.format("%0" + length + "d", randomNum);
	}
}