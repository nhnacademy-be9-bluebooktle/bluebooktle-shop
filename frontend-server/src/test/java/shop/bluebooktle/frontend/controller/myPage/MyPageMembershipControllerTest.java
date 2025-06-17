package shop.bluebooktle.frontend.controller.myPage;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.ui.Model;

import jakarta.servlet.http.HttpServletRequest;
import shop.bluebooktle.common.dto.membership.MembershipLevelDetailDto;
import shop.bluebooktle.common.dto.user.response.UserMembershipLevelResponse;
import shop.bluebooktle.frontend.service.MembershipLevelService;
import shop.bluebooktle.frontend.service.UserService;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class MyPageMembershipControllerTest {

	@InjectMocks
	private MyPageMembershipController myPageMembershipController;

	@Mock
	private MembershipLevelService membershipLevelService;

	@Mock
	private UserService userService;

	@Mock
	private Model model;

	@Mock
	private HttpServletRequest request;

	private List<MembershipLevelDetailDto> mockMembershipLevels;

	@BeforeEach
	void setUp() {
		mockMembershipLevels = Arrays.asList(
			new MembershipLevelDetailDto(1L, "브론즈", 1, new BigDecimal("0.00"), new BigDecimal("99999.99")),
			new MembershipLevelDetailDto(2L, "실버", 3, new BigDecimal("100000.00"), new BigDecimal("199999.99")),
			new MembershipLevelDetailDto(3L, "골드", 5, new BigDecimal("200000.00"), new BigDecimal("299999.99")),
			new MembershipLevelDetailDto(4L, "플래티넘", 7, new BigDecimal("300000.00"), new BigDecimal("99999999.99"))
		);
	}

	@Test
	@DisplayName("userMembershipPage: 성공적인 페이지 로드 - 중간 레벨, 다음 레벨까지 남은 금액 존재")
	void userMembershipPage_success_midLevelWithNextLevelDiff() {
		// Given
		UserMembershipLevelResponse userMembership = new UserMembershipLevelResponse(
			1L, new BigDecimal("150000.00"), 2L, "실버"
		);

		when(userService.getUserMembership()).thenReturn(userMembership);
		when(membershipLevelService.getMembershipLevels()).thenReturn(mockMembershipLevels);
		when(request.getRequestURI()).thenReturn("/mypage/membership");

		// When
		String viewName = myPageMembershipController.userMembershipPage(model, request);

		// Then
		assertThat(viewName).isEqualTo("mypage/membership");

		verify(model).addAttribute("currentURI", "/mypage/membership");
		verify(model).addAttribute("userMembership", userMembership);
		verify(model).addAttribute("membershipLevels", mockMembershipLevels);
		verify(model).addAttribute("currentLevelId", 2L);
		verify(model).addAttribute("gaugePercent", "50.0000");
		verify(model).addAttribute("nextLevelDiff", new BigDecimal("50000.00"));
		verify(model).addAttribute("formattedNetAmount", "150,000");
		verify(model).addAttribute("formattedNextLevelDiff", "50,000");
	}

	@Test
	@DisplayName("userMembershipPage: 성공적인 페이지 로드 - 최고 레벨, 다음 레벨 없음")
	void userMembershipPage_success_topLevel() {
		// Given
		UserMembershipLevelResponse userMembership = new UserMembershipLevelResponse(
			1L, new BigDecimal("350000.00"), 4L, "플래티넘"
		);

		when(userService.getUserMembership()).thenReturn(userMembership);
		when(membershipLevelService.getMembershipLevels()).thenReturn(mockMembershipLevels);
		when(request.getRequestURI()).thenReturn("/mypage/membership");

		// When
		String viewName = myPageMembershipController.userMembershipPage(model, request);

		// Then
		assertThat(viewName).isEqualTo("mypage/membership");

		verify(model).addAttribute("currentURI", "/mypage/membership");
		verify(model).addAttribute("userMembership", userMembership);
		verify(model).addAttribute("membershipLevels", mockMembershipLevels);
		verify(model).addAttribute("currentLevelId", 4L);
		verify(model).addAttribute("gaugePercent", "100.0000");
		verify(model).addAttribute("nextLevelDiff", null);
		verify(model).addAttribute("formattedNetAmount", "350,000");
		verify(model).addAttribute("formattedNextLevelDiff", null);
	}

	@Test
	@DisplayName("userMembershipPage: 성공적인 페이지 로드 - 최하위 레벨, 다음 레벨까지 남은 금액 존재")
	void userMembershipPage_success_lowestLevelWithNextLevelDiff() {
		// Given
		UserMembershipLevelResponse userMembership = new UserMembershipLevelResponse(
			1L, new BigDecimal("50000.00"), 1L, "브론즈"
		);

		when(userService.getUserMembership()).thenReturn(userMembership);
		when(membershipLevelService.getMembershipLevels()).thenReturn(mockMembershipLevels);
		when(request.getRequestURI()).thenReturn("/mypage/membership");

		// When
		String viewName = myPageMembershipController.userMembershipPage(model, request);

		// Then
		assertThat(viewName).isEqualTo("mypage/membership");

		verify(model).addAttribute("currentURI", "/mypage/membership");
		verify(model).addAttribute("userMembership", userMembership);
		verify(model).addAttribute("membershipLevels", mockMembershipLevels);
		verify(model).addAttribute("currentLevelId", 1L);
		verify(model).addAttribute("gaugePercent", "16.6700");
		verify(model).addAttribute("nextLevelDiff", new BigDecimal("50000.00"));
		verify(model).addAttribute("formattedNetAmount", "50,000");
		verify(model).addAttribute("formattedNextLevelDiff", "50,000");
	}

	@Test
	@DisplayName("userMembershipPage: 회원 등급 목록이 비어있는 경우")
	void userMembershipPage_emptyMembershipLevels() {
		// Given
		UserMembershipLevelResponse userMembership = new UserMembershipLevelResponse(
			1L, new BigDecimal("50000.00"), 1L, "브론즈"
		);

		when(userService.getUserMembership()).thenReturn(userMembership);
		when(membershipLevelService.getMembershipLevels()).thenReturn(Collections.emptyList());
		when(request.getRequestURI()).thenReturn("/mypage/membership");

		// When
		String viewName = myPageMembershipController.userMembershipPage(model, request);

		// Then
		assertThat(viewName).isEqualTo("mypage/membership");

		verify(model).addAttribute("currentURI", "/mypage/membership");
		verify(model).addAttribute("userMembership", userMembership);
		verify(model).addAttribute("membershipLevels", Collections.emptyList());
		verify(model).addAttribute("currentLevelId", 1L);
		verify(model).addAttribute("nextLevelDiff", null);
		verify(model).addAttribute("gaugePercent", "16.6700");
		verify(model).addAttribute("formattedNetAmount", "50,000");
		verify(model).addAttribute("formattedNextLevelDiff", null);
	}
}