package shop.bluebooktle.frontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.common.dto.auth.request.LoginRequest;
import shop.bluebooktle.common.dto.auth.request.SignupRequest;
import shop.bluebooktle.common.dto.auth.response.TokenResponse;
import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;
import shop.bluebooktle.frontend.service.impl.AuthServiceImpl;

@Slf4j
@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class AuthController {

	private final AuthServiceImpl authService;

	@GetMapping("/login")
	public String loginForm(Model model,
		@ModelAttribute("errorCode") String errorCode,
		@ModelAttribute("errorMessage") String errorMessage) {
		if (errorCode != null && !errorCode.isEmpty()) {
			model.addAttribute("error", "true");
		}
		return "auth/login_form";
	}

	@PostMapping("/login")
	public String login(@ModelAttribute LoginRequest loginRequest,
		HttpServletResponse response) {

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

		redirectAttributes.addFlashAttribute("error", "true");
		redirectAttributes.addFlashAttribute("errorCode", errorCode.getCode());
		redirectAttributes.addFlashAttribute("errorMessage", errorCode.getMessage());

		return "redirect:/login";
	}

	@GetMapping("/signup")
	public String signupForm(Model model) {
		if (!model.containsAttribute("signupRequest")) {
			model.addAttribute("signupRequest", new SignupRequest());
		}
		return "auth/signup_form";
	}

	@PostMapping("/signup")
	public String signup(@Valid @ModelAttribute("signupRequest") SignupRequest signupRequest,
		BindingResult bindingResult,
		RedirectAttributes redirectAttributes) {

		if (bindingResult.hasErrors()) {
			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.signupRequest",
				bindingResult);
			redirectAttributes.addFlashAttribute("signupRequest", signupRequest);
			return "redirect:/signup";
		}

		try {
			authService.signup(signupRequest);
			redirectAttributes.addFlashAttribute("globalSuccessMessage", "회원가입이 성공적으로 완료되었습니다. 로그인해주세요.");
			redirectAttributes.addFlashAttribute("globalSuccessTitle", "회원가입 성공!");
			return "redirect:/login";
		} catch (ApplicationException e) {
			redirectAttributes.addFlashAttribute("error", "true");
			redirectAttributes.addFlashAttribute("errorCode", e.getErrorCode().getCode());
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
			redirectAttributes.addFlashAttribute("signupRequest", signupRequest);
			return "redirect:/signup";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "true");
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
			redirectAttributes.addFlashAttribute("signupRequest", signupRequest);
			return "redirect:/signup";
		}
	}

	@PostMapping("/logout")
	public String handleLogout(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		deleteCookie(response, "accessToken");
		deleteCookie(response, "refreshToken");
		redirectAttributes.addFlashAttribute("globalSuccessMessage", "로그아웃 되었습니다!");
		redirectAttributes.addFlashAttribute("globalSuccessTitle", "로그아웃 성공!");
		log.info("로그아웃 성공");
		return "redirect:/";
	}

	private void deleteCookie(HttpServletResponse response, String cookieName) {
		Cookie cookie = new Cookie(cookieName, null);
		cookie.setPath("/");
		cookie.setMaxAge(0);
		response.addCookie(cookie);
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