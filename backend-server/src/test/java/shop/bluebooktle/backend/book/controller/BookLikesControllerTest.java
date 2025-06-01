package shop.bluebooktle.backend.book.controller;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

@WebMvcTest(BookLikesController.class)
public class BookLikesControllerTest {
	//
	// @Autowired
	// private MockMvc mockMvc;
	//
	// @MockitoBean
	// private BookLikesService bookLikesService;
	//
	// @MockitoBean
	// private JwtUtil jwtUtil;
	//
	// @MockitoBean
	// private AuthUserLoader authUserLoader;
	//
	// @Test
	// @DisplayName("도서 좋아요 등록 - 성공")
	// @WithMockUser
	// void registerLikeBook_success() throws Exception {
	// 	mockMvc.perform(post("/api/books/1/likes")
	// 			.param("userId", "1").with(csrf()))
	// 		.andExpect(status().isCreated())
	// 		.andExpect(jsonPath("$.status").value("success"));
	// }
	//
	// @Test
	// @DisplayName("도서 좋아요 취소 - 성공")
	// @WithMockUser
	// void unlikeBook_success() throws Exception {
	// 	mockMvc.perform(delete("/api/books/1/likes")
	// 			.param("userId", "1").with(csrf()))
	// 		.andExpect(status().isOk())
	// 		.andExpect(jsonPath("$.status").value("success"));
	// }
	//
	// @Test
	// @DisplayName("좋아요 상태 조회 - 성공")
	// @WithMockUser
	// void isLiked_success() throws Exception {
	// 	BookLikesResponse response = BookLikesResponse.builder()
	// 		.bookId(1L)
	// 		.isLiked(true)
	// 		.countLikes(5)
	// 		.build();
	// 	given(bookLikesService.isLiked(1L, 2L)).willReturn(response);
	//
	// 	mockMvc.perform(get("/api/books/1/likes/status")
	// 			.param("userId", "2"))
	// 		.andExpect(status().isOk())
	// 		.andExpect(jsonPath("$.status").value("success"))
	// 		.andExpect(jsonPath("$.data.bookId").value(1))
	// 		.andExpect(jsonPath("$.data.liked").value(true))
	// 		.andExpect(jsonPath("$.data.countLikes").value(5));
	// }
	//
	// @Test
	// @DisplayName("도서 좋아요 수 조회 - 성공")
	// @WithMockUser
	// void countLikes_success() throws Exception {
	// 	BookLikesResponse response = BookLikesResponse.builder()
	// 		.bookId(1L)
	// 		.isLiked(true)
	// 		.countLikes(10)
	// 		.build();
	// 	given(bookLikesService.countLikes(1L)).willReturn(response);
	//
	// 	mockMvc.perform(get("/api/books/1/likes/count"))
	// 		.andExpect(status().isOk())
	// 		.andExpect(jsonPath("$.status").value("success"))
	// 		.andExpect(jsonPath("$.data.countLikes").value(10));
	// }
	//
	// @Test
	// @DisplayName("좋아요 목록 조회 - 성공")
	// @WithMockUser
	// void getBooksLiked_success() throws Exception {
	// 	BookLikesListResponse response1 = BookLikesListResponse.builder()
	// 		.bookName("사과가 쿵!")
	// 		.authorName(List.of("다다 히로시", "정근"))
	// 		.price(BigDecimal.valueOf(5000))
	// 		.imgUrl("http://bluebooktle/apple.jpg")
	// 		.build();
	// 	BookLikesListResponse response2 = BookLikesListResponse.builder()
	// 		.bookName("멀쩡한 어른이 되긴 글렀군")
	// 		.authorName(List.of("최고운", "짱구", "맹구"))
	// 		.price(BigDecimal.valueOf(14220))
	// 		.imgUrl("http://bluebooktle/shin.jpg")
	// 		.build();
	//
	// 	given(bookLikesService.getBooksLikedByUser(nullable(UserPrincipal.class)))
	// 		.willReturn(Arrays.asList(response1, response2));
	//
	// 	mockMvc.perform(get("/api/books/likes").with(csrf()))
	// 		.andExpect(status().isOk())
	// 		.andExpect(jsonPath("$.status").value("success"))
	// 		.andExpect(jsonPath("$.data[0].bookName").value("사과가 쿵!"))
	// 		.andExpect(jsonPath("$.data[0].authorName[0]").value("다다 히로시"))
	// 		.andExpect(jsonPath("$.data[0].price").value(BigDecimal.valueOf(5000)));
	// }
}
