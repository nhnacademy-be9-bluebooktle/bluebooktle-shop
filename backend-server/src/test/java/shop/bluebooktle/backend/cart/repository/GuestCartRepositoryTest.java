// package shop.bluebooktle.backend.cart.repository;
//
// import static org.assertj.core.api.Assertions.*;
//
// import java.util.List;
// import java.util.Map;
//
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
// import org.springframework.context.annotation.Import;
// import org.springframework.data.redis.core.RedisTemplate;
//
// import shop.bluebooktle.backend.cart.repository.redis.GuestCartRepository;
// import shop.bluebooktle.backend.cart.repository.redis.GuestCartRepositoryImpl;
// import shop.bluebooktle.backend.config.RedisConfig;
//
// @DataRedisTest
// @Import({RedisConfig.class})
// class GuestCartRepositoryTest {
//
// 	@Autowired
// 	private RedisTemplate<String, Object> redisTemplate;
//
// 	private GuestCartRepository guestCartRepository;
//
// 	private final String guestId = "guest-test-123";
//
// 	@BeforeEach
// 	void setUp() {
// 		guestCartRepository = new GuestCartRepositoryImpl(redisTemplate);
// 		redisTemplate.getConnectionFactory().getConnection().flushDb(); // Redis 초기화
// 	}
//
// 	@Test
// 	@DisplayName("도서 추가 및 조회")
// 	void addAndGetCart() {
// 		guestCartRepository.addBook(guestId, 101L, 2);
// 		guestCartRepository.addBook(guestId, 202L, 5);
//
// 		Map<Long, Integer> cart = guestCartRepository.getCart(guestId);
//
// 		assertThat(cart).hasSize(2)
// 			.containsEntry(101L, 2)
// 			.containsEntry(202L, 5);
// 	}
//
// 	@Test
// 	@DisplayName("수량 증가")
// 	void increaseQuantity() {
// 		guestCartRepository.addBook(guestId, 101L, 1);
// 		guestCartRepository.increaseQuantity(guestId, 101L, 3);
//
// 		Map<Long, Integer> cart = guestCartRepository.getCart(guestId);
// 		assertThat(cart.get(101L)).isEqualTo(4);
// 	}
//
// 	@Test
// 	@DisplayName("수량 감소 - 최소 1 보장")
// 	void decreaseQuantity() {
// 		guestCartRepository.addBook(guestId, 101L, 2);
// 		guestCartRepository.decreaseQuantity(guestId, 101L, 1);
// 		guestCartRepository.decreaseQuantity(guestId, 101L, 5); // 1 이하로 감소 시도
//
// 		Map<Long, Integer> cart = guestCartRepository.getCart(guestId);
// 		assertThat(cart.get(101L)).isEqualTo(1);
// 	}
//
// 	@Test
// 	@DisplayName("단일 도서 제거")
// 	void removeBook() {
// 		guestCartRepository.addBook(guestId, 101L, 2);
// 		guestCartRepository.addBook(guestId, 202L, 1);
//
// 		guestCartRepository.removeBook(guestId, 101L);
//
// 		Map<Long, Integer> cart = guestCartRepository.getCart(guestId);
// 		assertThat(cart).hasSize(1)
// 			.doesNotContainKey(101L)
// 			.containsKey(202L);
// 	}
//
// 	@Test
// 	@DisplayName("선택 도서 일괄 제거")
// 	void removeSelectedBooks() {
// 		guestCartRepository.addBook(guestId, 1L, 1);
// 		guestCartRepository.addBook(guestId, 2L, 2);
// 		guestCartRepository.addBook(guestId, 3L, 3);
//
// 		guestCartRepository.removeSelectedBooks(guestId, List.of(1L, 3L));
//
// 		Map<Long, Integer> cart = guestCartRepository.getCart(guestId);
// 		assertThat(cart).hasSize(1)
// 			.containsKey(2L)
// 			.doesNotContainKey(1L)
// 			.doesNotContainKey(3L);
// 	}
// }