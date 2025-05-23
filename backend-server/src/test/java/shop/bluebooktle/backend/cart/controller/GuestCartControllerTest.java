package shop.bluebooktle.backend.cart.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")  // ✅ 테스트용 설정 적용
class GuestCartControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	RedisTemplate<String, Object> redisTemplate;

	private final String guestId = "guest-test";
	private final String redisKey = guestId;

	@AfterEach
	void tearDown() {
		redisTemplate.delete(redisKey);
	}

	// @Test
	// @DisplayName("도서 추가")
	// void addBookToGuestCart() throws Exception {
	// 	Map<String, Object> request = Map.of(
	// 		"id", guestId,
	// 		"bookId", 101L,
	// 		"quantity", 2
	// 	);
	//
	// 	mockMvc.perform(post("/api/cart/guest")
	// 			.contentType(MediaType.APPLICATION_JSON)
	// 			.content(objectMapper.writeValueAsString(request)))
	// 		.andExpect(status().isOk())
	// 		.andExpect(jsonPath("$.status").value("success"));
	// }

	@Test
	@DisplayName("장바구니 목록 조회")
	void getGuestCartItems() throws Exception {
		redisTemplate.opsForHash().put(redisKey, "101", 3);

		mockMvc.perform(get("/api/cart/guest")
				.param("guestId", guestId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data[0].bookId").value(101))
			.andExpect(jsonPath("$.data[0].quantity").value(3));
	}

	@Test
	@DisplayName("수량 증가")
	void increaseGuestQuantity() throws Exception {
		redisTemplate.opsForHash().put(redisKey, "101", 1);

		Map<String, Object> request = Map.of(
			"id", guestId,
			"bookId", 101L,
			"quantity", 2
		);

		mockMvc.perform(patch("/api/cart/guest/increase")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"));
	}

	@Test
	@DisplayName("수량 감소")
	void decreaseGuestQuantity() throws Exception {
		redisTemplate.opsForHash().put(redisKey, "101", 3);

		Map<String, Object> request = Map.of(
			"id", guestId,
			"bookId", 101L,
			"quantity", 2
		);

		mockMvc.perform(patch("/api/cart/guest/decrease")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"));
	}

	@Test
	@DisplayName("도서 단일 삭제")
	void removeBookFromGuestCart() throws Exception {
		redisTemplate.opsForHash().put(redisKey, "101", 1);

		Map<String, Object> request = Map.of(
			"id", guestId,
			"bookId", 101L
		);

		mockMvc.perform(delete("/api/cart/guest")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"));
	}

	@Test
	@DisplayName("도서 여러 개 삭제")
	void removeSelectedBooksFromGuestCart() throws Exception {
		redisTemplate.opsForHash().put(redisKey, "101", 1);
		redisTemplate.opsForHash().put(redisKey, "102", 1);

		Map<String, Object> request = Map.of(
			"id", guestId,
			"bookIds", List.of(101L, 102L)
		);

		mockMvc.perform(delete("/api/cart/guest/selected")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"));
	}
}
