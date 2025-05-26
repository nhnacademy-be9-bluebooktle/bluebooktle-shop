package shop.bluebooktle.backend.book_order.controller;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

@WebMvcTest(PackagingOptionController.class)
@AutoConfigureMockMvc(addFilters = false)
class PackagingOptionControllerTest {

	// @Autowired
	// private MockMvc mockMvc;
	//
	// @Autowired
	// private ObjectMapper objectMapper;
	//
	// @MockitoBean
	// private PackagingOptionService packagingOptionService;
	//
	// @Test
	// @DisplayName("포장 옵션 등록 - 성공")
	// void createPackagingOption_success() throws Exception {
	// 	// given
	// 	PackagingOptionRequest req = PackagingOptionRequest.builder()
	// 		.name("Gift Wrap")
	// 		.price(BigDecimal.valueOf(500))
	// 		.build();
	// 	PackagingOptionInfoResponse respDto = PackagingOptionInfoResponse.builder()
	// 		.id(1L).name("Gift Wrap").price(BigDecimal.valueOf(500)).build();
	// 	given(packagingOptionService.createPackagingOption(any())).willReturn(respDto);
	//
	// 	// when & then
	// 	mockMvc.perform(post("/api/options")
	// 			.contentType(MediaType.APPLICATION_JSON)
	// 			.content(objectMapper.writeValueAsString(req)))
	// 		.andExpect(status().isCreated())
	// 		.andExpect(jsonPath("$.status").value("success"))
	// 		.andExpect(jsonPath("$.data.id").value(1))
	// 		.andExpect(jsonPath("$.data.name").value("Gift Wrap"));
	//
	// 	then(packagingOptionService).should().createPackagingOption(any(PackagingOptionRequest.class));
	// }
	//
	// @Test
	// @DisplayName("전체 조회 - 성공")
	// void getPackagingOptions_noKeyword() throws Exception {
	// 	// given
	// 	PackagingOptionInfoResponse dto = new PackagingOptionInfoResponse(2L, "Box", BigDecimal.valueOf(300));
	// 	Pageable pageable = PageRequest.of(0, 10);
	// 	given(packagingOptionService.getPackagingOptions(pageable))
	// 		.willReturn(new PageImpl<>(List.of(dto), pageable, 1));
	//
	// 	// when & then
	// 	mockMvc.perform(get("/api/options")
	// 			.contentType(MediaType.APPLICATION_JSON))
	// 		.andExpect(status().isOk())
	// 		.andExpect(jsonPath("$.status").value("success"))
	// 		.andExpect(jsonPath("$.data.content[0].id").value(2));
	//
	// 	then(packagingOptionService).should().getPackagingOptions(pageable);
	// }
	//
	// @Test
	// @DisplayName("검색 조회 - 성공")
	// void getPackagingOptions_withKeyword() throws Exception {
	// 	// given
	// 	String keyword = "Gift";
	// 	PackagingOptionInfoResponse dto = new PackagingOptionInfoResponse(3L, "Gift Box", BigDecimal.valueOf(1000));
	// 	Pageable pageable = PageRequest.of(0, 10);
	// 	given(packagingOptionService.searchPackagingOption(eq(keyword), eq(pageable)))
	// 		.willReturn(new PageImpl<>(List.of(dto), pageable, 1));
	//
	// 	// when & then
	// 	mockMvc.perform(get("/api/options")
	// 			.param("searchKeyword", keyword)
	// 			.contentType(MediaType.APPLICATION_JSON))
	// 		.andExpect(status().isOk())
	// 		.andExpect(jsonPath("$.status").value("success"))
	// 		.andExpect(jsonPath("$.data.content[0].name").value("Gift Box"));
	//
	// 	then(packagingOptionService).should().searchPackagingOption(keyword, pageable);
	// }
	//
	// @Test
	// @DisplayName("단건 조회 - 성공")
	// void getPackagingOption_success() throws Exception {
	// 	// given
	// 	PackagingOptionInfoResponse dto = new PackagingOptionInfoResponse(4L, "Envelope", BigDecimal.valueOf(200));
	// 	given(packagingOptionService.getPackagingOption(4L)).willReturn(dto);
	//
	// 	// when & then
	// 	mockMvc.perform(get("/api/options/4")
	// 			.contentType(MediaType.APPLICATION_JSON))
	// 		.andExpect(status().isOk())
	// 		.andExpect(jsonPath("$.data.id").value(4))
	// 		.andExpect(jsonPath("$.data.name").value("Envelope"));
	// }
	//
	// @Test
	// @DisplayName("수정 - 성공")
	// void updatePackagingOption_success() throws Exception {
	// 	// given
	// 	PackagingOptionRequest req = PackagingOptionRequest.builder()
	// 		.name("New Name").price(BigDecimal.valueOf(800)).build();
	// 	PackagingOptionInfoResponse dto = new PackagingOptionInfoResponse(5L, "New Name", BigDecimal.valueOf(800));
	// 	given(packagingOptionService.updatePackagingOption(eq(5L), any())).willReturn(dto);
	//
	// 	// when & then
	// 	mockMvc.perform(put("/api/options/5")
	// 			.contentType(MediaType.APPLICATION_JSON)
	// 			.content(objectMapper.writeValueAsString(req)))
	// 		.andExpect(status().isOk())
	// 		.andExpect(jsonPath("$.data.name").value("New Name"));
	//
	// 	then(packagingOptionService).should().updatePackagingOption(eq(5L), any(PackagingOptionRequest.class));
	// }
	//
	// @Test
	// @DisplayName("삭제 - 성공")
	// void deletePackagingOption_success() throws Exception {
	// 	// given: delete returns void
	// 	willDoNothing().given(packagingOptionService).deletePackagingOption(6L);
	//
	// 	// when & then
	// 	mockMvc.perform(delete("/api/options/6")
	// 			.contentType(MediaType.APPLICATION_JSON))
	// 		.andExpect(status().isOk())
	// 		.andExpect(jsonPath("$.status").value("success"));
	//
	// 	then(packagingOptionService).should().deletePackagingOption(6L);
	// }
}
