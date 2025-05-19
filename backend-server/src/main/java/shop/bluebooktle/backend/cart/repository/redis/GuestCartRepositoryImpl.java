package shop.bluebooktle.backend.cart.repository.redis;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class GuestCartRepositoryImpl implements GuestCartRepository {

	private final RedisTemplate<String, Object> redisTemplate;

	private String getKey(String guestId) {
		return "guest:" + guestId;
	}

	@Override
	public void addBook(String guestId, Long bookId, int quantity) {
		HashOperations<String, Object, Object> ops = redisTemplate.opsForHash();
		ops.put(getKey(guestId), bookId.toString(), quantity);
	}

	@Override
	public void increaseQuantity(String guestId, Long bookId, int quantity) {
		HashOperations<String, Object, Object> ops = redisTemplate.opsForHash();
		Integer current = (Integer)ops.get(getKey(guestId), bookId.toString());
		ops.put(getKey(guestId), bookId.toString(), current == null ? quantity : current + quantity);
	}

	@Override
	public void decreaseQuantity(String guestId, Long bookId, int quantity) {
		HashOperations<String, Object, Object> ops = redisTemplate.opsForHash();
		Integer current = (Integer)ops.get(getKey(guestId), bookId.toString());
		if (current != null) {
			int updated = Math.max(1, current - quantity);
			ops.put(getKey(guestId), bookId.toString(), updated);
		}
	}

	@Override
	public void removeBook(String guestId, Long bookId) {
		redisTemplate.opsForHash().delete(getKey(guestId), bookId.toString());
	}

	@Override
	public void removeSelectedBooks(String guestId, List<Long> bookIds) {
		redisTemplate.opsForHash().delete(getKey(guestId), bookIds.stream().map(String::valueOf).toArray());
	}

	@Override
	public Map<Long, Integer> getCart(String guestId) {
		Map<Object, Object> raw = redisTemplate.opsForHash().entries(getKey(guestId));
		return raw.entrySet().stream()
			.collect(Collectors.toMap(
				e -> Long.parseLong((String)e.getKey()),
				e -> (Integer)e.getValue()
			));
	}
}