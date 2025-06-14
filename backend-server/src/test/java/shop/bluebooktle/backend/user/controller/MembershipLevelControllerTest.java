package shop.bluebooktle.backend.user.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import shop.bluebooktle.backend.user.service.MembershipLevelService;
import shop.bluebooktle.common.domain.auth.UserStatus;
import shop.bluebooktle.common.domain.auth.UserType;
import shop.bluebooktle.common.dto.auth.UserDto;
import shop.bluebooktle.common.dto.membership.MembershipLevelDetailDto;
import shop.bluebooktle.common.security.AuthUserLoader;
import shop.bluebooktle.common.security.UserPrincipal;
import shop.bluebooktle.common.util.JwtUtil;

@ActiveProfiles("test")
@WebMvcTest(MembershipLevelController.class)
class MembershipLevelControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private MembershipLevelService membershipLevelService;

	@MockitoBean
	private JwtUtil jwtUtil;

	@MockitoBean
	private AuthUserLoader authUserLoader;

	private UserPrincipal userPrincipal;

	@BeforeEach
	void setUp() {
		// 인증된 사용자를 시뮬레이션하기 위한 UserPrincipal 객체 생성
		UserDto userDto = UserDto.builder()
			.id(1L)
			.loginId("testuser")
			.type(UserType.USER)
			.status(UserStatus.ACTIVE)
			.build();
		userPrincipal = new UserPrincipal(userDto);
	}

	@Test
	@DisplayName("전체 회원 등급 목록 조회 성공")
	void getMembershipLevels_success() throws Exception {
		List<MembershipLevelDetailDto> mockResponse = List.of(
			new MembershipLevelDetailDto(1L, "일반", 1, BigDecimal.ZERO, new BigDecimal("99999")),
			new MembershipLevelDetailDto(2L, "로얄", 2, new BigDecimal("100000"), new BigDecimal("199999"))
		);
		given(membershipLevelService.getAllMembershipLevels()).willReturn(mockResponse);
		
		mockMvc.perform(get("/api/memberships")
				.with(user(userPrincipal)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data").isArray())
			.andExpect(jsonPath("$.data[0].name").value("일반"));

		verify(membershipLevelService).getAllMembershipLevels();
	}
}