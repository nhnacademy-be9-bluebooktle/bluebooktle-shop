// package shop.bluebooktle.backend.cart.controller;
//
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
//
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.http.MediaType;
// import org.springframework.test.context.ActiveProfiles;
// import org.springframework.test.web.servlet.MockMvc;
//
// import com.fasterxml.jackson.databind.ObjectMapper;
//
// import shop.bluebooktle.backend.user.repository.UserRepository;
// import shop.bluebooktle.common.domain.auth.UserStatus;
// import shop.bluebooktle.common.domain.auth.UserType;
// import shop.bluebooktle.common.entity.auth.User;
//
// @SpringBootTest
// @AutoConfigureMockMvc
// @ActiveProfiles("test")
// class MemberCartControllerTest {
//
// 	@Autowired
// 	MockMvc mockMvc;
//
// 	@Autowired
// 	ObjectMapper objectMapper;
//
// 	@Autowired
// 	UserRepository userRepository;
//
// 	private User testUser;
// 	private Map<String, Object> user;
//
// 	@BeforeEach
// 	void setup() {
// 		testUser = User.builder()
// 			.loginId("testUser")
// 			.name("테스트회원")
// 			.email("test@email.com")
// 			.type(UserType.USER)
// 			.status(UserStatus.ACTIVE)
// 			.build();
//
// 		userRepository.save(testUser);
//
// 		user = new HashMap<>();
// 		user.put("userId", testUser.getId());
// 		user.put("loginId", testUser.getLoginId());
// 		user.put("name", testUser.getName());
// 		user.put("email", testUser.getEmail());
// 		user.put("type", testUser.getType().name());
// 		user.put("status", testUser.getStatus().name());
// 	}
//
// 	@Test
// 	@DisplayName("회원 장바구니 도서 추가")
// 	void addBookToCart() throws Exception {
// 		Map<String, Object> request = Map.of(
// 			"user", user,
// 			"bookId", 1001L,
// 			"quantity", 3
// 		);
//
// 		mockMvc.perform(post("/api/cart/member")
// 				.contentType(MediaType.APPLICATION_JSON)
// 				.content(objectMapper.writeValueAsString(request)))
// 			.andExpect(status().isOk())
// 			.andExpect(jsonPath("$.status").value("success"));
// 	}
//
// 	@Test
// 	@DisplayName("회원 장바구니 목록 조회")
// 	void getCartItems() throws Exception {
// 		mockMvc.perform(get("/api/cart/member")
// 				.contentType(MediaType.APPLICATION_JSON)
// 				.content(objectMapper.writeValueAsString(user)))
// 			.andExpect(status().isOk())
// 			.andExpect(jsonPath("$.status").value("success"))
// 			.andExpect(jsonPath("$.data").isArray());
// 	}
//
// 	@Test
// 	@DisplayName("회원 장바구니 수량 증가")
// 	void increaseQuantity() throws Exception {
// 		Map<String, Object> request = Map.of(
// 			"user", user,
// 			"bookId", 1001L,
// 			"quantity", 2
// 		);
//
// 		mockMvc.perform(patch("/api/cart/member/increase")
// 				.contentType(MediaType.APPLICATION_JSON)
// 				.content(objectMapper.writeValueAsString(request)))
// 			.andExpect(status().isOk())
// 			.andExpect(jsonPath("$.status").value("success"));
// 	}
//
// 	@Test
// 	@DisplayName("회원 장바구니 수량 감소")
// 	void decreaseQuantity() throws Exception {
// 		Map<String, Object> request = Map.of(
// 			"user", user,
// 			"bookId", 1001L,
// 			"quantity", 1
// 		);
//
// 		mockMvc.perform(patch("/api/cart/member/decrease")
// 				.contentType(MediaType.APPLICATION_JSON)
// 				.content(objectMapper.writeValueAsString(request)))
// 			.andExpect(status().isOk())
// 			.andExpect(jsonPath("$.status").value("success"));
// 	}
//
// 	@Test
// 	@DisplayName("회원 장바구니 단일 도서 삭제")
// 	void removeBook() throws Exception {
// 		Map<String, Object> request = Map.of(
// 			"user", user,
// 			"bookId", 1001L
// 		);
//
// 		mockMvc.perform(delete("/api/cart/member")
// 				.contentType(MediaType.APPLICATION_JSON)
// 				.content(objectMapper.writeValueAsString(request)))
// 			.andExpect(status().isOk())
// 			.andExpect(jsonPath("$.status").value("success"));
// 	}
//
// 	@Test
// 	@DisplayName("회원 장바구니 선택 도서 삭제")
// 	void removeSelectedBooks() throws Exception {
// 		Map<String, Object> request = Map.of(
// 			"user", user,
// 			"bookIds", List.of(1001L, 1002L)
// 		);
//
// 		mockMvc.perform(delete("/api/cart/member/selected")
// 				.contentType(MediaType.APPLICATION_JSON)
// 				.content(objectMapper.writeValueAsString(request)))
// 			.andExpect(status().isOk())
// 			.andExpect(jsonPath("$.status").value("success"));
// 	}
// }
