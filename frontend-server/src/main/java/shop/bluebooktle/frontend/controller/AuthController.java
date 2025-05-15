package shop.bluebooktle.frontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.common.dto.auth.request.LoginRequest;
import shop.bluebooktle.common.dto.auth.response.TokenResponse;
import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;
import shop.bluebooktle.frontend.service.AuthService;

@Slf4j
@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@GetMapping("/login")
	public String loginForm(Model model,
		@ModelAttribute("errorCode") String errorCode,
		@ModelAttribute("errorMessage") String errorMessage) {
		if (errorCode != null && !errorCode.isEmpty()) {
			model.addAttribute("error", "true");
		}
		return "auth/login_form";
	}

	@GetMapping("/signup")
	public String joinForm() {
		return "auth/signup_form";
	}

	@PostMapping("/login")
	public String login(@ModelAttribute LoginRequest loginRequest,
		HttpServletResponse response,
		RedirectAttributes redirectAttributes) {

		TokenResponse tokenResponse = authService.login(loginRequest);
		Cookie accessTokenCookie = new Cookie("accessToken", tokenResponse.getAccessToken());
		accessTokenCookie.setPath("/");
		response.addCookie(accessTokenCookie);

		Cookie refreshTokenCookie = new Cookie("refreshToken", tokenResponse.getRefreshToken());
		refreshTokenCookie.setHttpOnly(true);
		refreshTokenCookie.setPath("/");
		response.addCookie(refreshTokenCookie);

		log.info("로그인 성공: {}", loginRequest.getLoginId());
		return "redirect:/";

	}

	@ExceptionHandler(ApplicationException.class)
	public String handleApplicationException(ApplicationException ex, RedirectAttributes redirectAttributes) {
		ErrorCode errorCode = ex.getErrorCode();
		String errorMessage = ex.getMessage();

		redirectAttributes.addFlashAttribute("error", "true");
		redirectAttributes.addFlashAttribute("errorCode", errorCode.getCode());
		redirectAttributes.addFlashAttribute("errorMessage", errorMessage);

		return "redirect:/login";
	}

	@ExceptionHandler(Exception.class)
	public String handleApplicationException(Exception ex, RedirectAttributes redirectAttributes) {
		String errorMessage = ex.getMessage();

		redirectAttributes.addFlashAttribute("error", "true");
		redirectAttributes.addFlashAttribute("errorCode", "C001");
		redirectAttributes.addFlashAttribute("errorMessage", errorMessage);

		return "redirect:/login";
	}
}