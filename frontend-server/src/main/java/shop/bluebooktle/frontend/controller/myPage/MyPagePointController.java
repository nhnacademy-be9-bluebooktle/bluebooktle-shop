package shop.bluebooktle.frontend.controller.myPage;

import java.math.BigDecimal;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.point.request.PointHistoryCreateRequest;
import shop.bluebooktle.common.dto.point.response.PointHistoryResponse;
import shop.bluebooktle.frontend.service.PointService;

@Slf4j
@Controller
@RequestMapping("/mypage/points")
@RequiredArgsConstructor
public class MyPagePointController {

	private final PointService pointService;

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
			pointService.getMyPointHistories(pageable);
		model.addAttribute("pointHistories", histories);

		BigDecimal totalPoints = pointService.getTotalPoints();
		model.addAttribute("totalPoints", totalPoints);

		return "mypage/point_log";
	}

	@PostMapping
	public String createPointHistory(
		@ModelAttribute PointHistoryCreateRequest request,
		RedirectAttributes ra
	) {
		try {
			pointService.createPointHistory(request);
			ra.addFlashAttribute("globalSuccessMessage", "포인트 이력이 등록되었습니다.");
		} catch (Exception e) {
			ra.addFlashAttribute("globalErrorMessage", "포인트 등록 실패: " + e.getMessage());
		}
		return "redirect:/points";
	}
}
