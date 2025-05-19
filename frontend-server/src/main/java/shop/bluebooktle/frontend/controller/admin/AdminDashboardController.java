package shop.bluebooktle.frontend.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminDashboardController {

	@GetMapping("/dashboard")
	public String dashboardPage(Model model, HttpServletRequest request) {
		log.info("어드민 대시보드 페이지 요청");
		model.addAttribute("currentURI", request.getRequestURI());
		model.addAttribute("pageTitle", "관리자 대시보드");
		return "admin/dashboard";
	}
}