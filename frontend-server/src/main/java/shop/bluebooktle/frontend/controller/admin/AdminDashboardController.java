package shop.bluebooktle.frontend.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.common.dto.admin.DashboardStatusResponse;
import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.frontend.service.DashboardService;

@Slf4j
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminDashboardController {

	private final DashboardService dashboardService;

	@GetMapping("/dashboard")
	public String dashboardPage(Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
		log.info("어드민 대시보드 페이지 요청");

		try {
			DashboardStatusResponse statusResponse = dashboardService.getDashboardStatus();
			model.addAttribute("dashboardStatus", statusResponse);

		} catch (ApplicationException e) {
			log.error("대시보드 데이터 조회 실패: {}", e.getMessage());
			redirectAttributes.addFlashAttribute("globalErrorMessage",
				"데이터를 불러오는 중 오류가 발생했습니다: " + e.getErrorCode().getMessage());
			redirectAttributes.addFlashAttribute("globalErrorTitle", e.getErrorCode().getCode());
			return "redirect:/";
		} catch (Exception e) {
			log.error("대시보드 데이터 조회 중 알 수 없는 오류 발생", e);
			redirectAttributes.addFlashAttribute("globalErrorMessage", "알 수 없는 오류로 데이터를 불러올 수 없습니다.");
			return "redirect:/admin";
		}

		model.addAttribute("currentURI", request.getRequestURI());
		model.addAttribute("pageTitle", "관리자 대시보드");
		return "admin/dashboard";
	}
}