package shop.bluebooktle.frontend.controller.myPage;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import shop.bluebooktle.common.dto.membership.MembershipLevelDetailDto;
import shop.bluebooktle.common.dto.user.response.UserMembershipLevelResponse;
import shop.bluebooktle.frontend.service.MembershipLevelService;
import shop.bluebooktle.frontend.service.UserService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mypage/membership")
public class MyPageMembershipController {

	private final MembershipLevelService membershipLevelService;
	private final UserService userService;

	@GetMapping
	public String userMembershipPage(Model model, HttpServletRequest request) {
		model.addAttribute("currentURI", request.getRequestURI());

		UserMembershipLevelResponse userMembership = userService.getUserMembership();
		List<MembershipLevelDetailDto> membershipLevels = membershipLevelService.getMembershipLevels();

		BigDecimal netAmount = userMembership.netAmount();
		Long currentLevelId = userMembership.membershipLevelId();

		BigDecimal gaugePercent = netAmount
			.min(new BigDecimal("300000"))
			.divide(new BigDecimal("300000"), 4, RoundingMode.HALF_UP)
			.multiply(BigDecimal.valueOf(100))
			.min(BigDecimal.valueOf(100));

		BigDecimal nextLevelDiff = null;
		for (int i = 0; i < membershipLevels.size(); i++) {
			MembershipLevelDetailDto level = membershipLevels.get(i);
			if (level.id().equals(currentLevelId) && i + 1 < membershipLevels.size()) {
				MembershipLevelDetailDto next = membershipLevels.get(i + 1);
				nextLevelDiff = next.minNetSpent().subtract(netAmount).max(BigDecimal.ZERO);
				break;
			}
		}

		String formattedNetAmount = String.format("%,d", netAmount.intValue());
		String formattedNextLevelDiff = nextLevelDiff != null
			? String.format("%,d", nextLevelDiff.intValue())
			: null;

		// 모델에 데이터 추가
		model.addAttribute("userMembership", userMembership);
		model.addAttribute("membershipLevels", membershipLevels);
		model.addAttribute("currentLevelId", currentLevelId);
		String gaugePercentStr = gaugePercent.max(BigDecimal.ONE).toPlainString();
		model.addAttribute("gaugePercent", gaugePercentStr);
		model.addAttribute("nextLevelDiff", nextLevelDiff);
		model.addAttribute("formattedNetAmount", formattedNetAmount);
		model.addAttribute("formattedNextLevelDiff", formattedNextLevelDiff);

		return "mypage/membership";
	}
}
