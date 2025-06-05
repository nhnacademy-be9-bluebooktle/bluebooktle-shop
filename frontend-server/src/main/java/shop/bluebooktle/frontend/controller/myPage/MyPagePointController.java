package shop.bluebooktle.frontend.controller.myPage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.point.response.PointHistoryResponse;
import shop.bluebooktle.frontend.service.PointHistoryService;
import shop.bluebooktle.frontend.service.UserService;

@Slf4j
@Controller
@RequestMapping("/mypage/points")
@RequiredArgsConstructor
public class MyPagePointController {

	private final PointHistoryService pointHistoryService;
	private final UserService userService;

	@GetMapping
	public String viewMyPoints(
		@PageableDefault(size = 20) Pageable pageable,
		Model model,
		HttpServletRequest request
	) {
		log.info("포인트 조회 경로");

		model.addAttribute("pageTitle", "내 포인트 내역");
		model.addAttribute("currentURI", request.getRequestURI());

		PaginationData<PointHistoryResponse> histories =
			pointHistoryService.getMyPointHistories(pageable);
		model.addAttribute("pointHistories", histories);
		model.addAttribute("totalPoints", userService.getUserTotalPoints().totalPointBalance());

		return "mypage/point_log";
	}
}
