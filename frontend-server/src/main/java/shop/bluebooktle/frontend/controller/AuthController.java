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
		model.addAttribute("globalErrorMessage", "error");
		model.addAttribute("globalErrorTitle", "title");
		return "auth/login_form";
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

	@GetMapping("/signup")
	public String signupForm(Model model) { // ModelAttribute를 사용하여 빈 SignupRequest 객체를 폼에 전달
		model.addAttribute("signupRequest", new SignupRequest());
		return "auth/signup_form";
	}

	// 회원가입 요청을 처리하는 POST 메소드
	@PostMapping("/signup")
	public String signup(@Valid @ModelAttribute SignupRequest signupRequest,
		BindingResult bindingResult,
		RedirectAttributes redirectAttributes) {

		if (bindingResult.hasErrors()) {
			// 폼 유효성 검사 실패 시, 에러 메시지와 함께 다시 회원가입 폼으로
			// bindingResult.getFieldErrors() 등을 사용하여 구체적인 에러를 모델에 추가할 수 있음
			// 여기서는 단순화하여 RedirectAttributes에 일반적인 에러 메시지를 담아 리다이렉션
			// 또는 signup_form.html에서 직접 th:errors를 사용할 수 있도록 모델에 bindingResult 자체를 넘길 수도 있음
			log.warn("Signup form validation failed: {}", bindingResult.getAllErrors());
			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.signupRequest",
				bindingResult);
			redirectAttributes.addFlashAttribute("signupRequest", signupRequest); // 입력값 유지를 위해
			return "redirect:/signup"; // GET /signup으로 리다이렉트하여 폼을 다시 보여줌
		}

		try {
			authService.signup(signupRequest);
			log.info("회원가입 성공: {}", signupRequest.getEmail());
			redirectAttributes.addFlashAttribute("signupSuccess", "회원가입이 성공적으로 완료되었습니다. 로그인해주세요.");
			return "redirect:/login"; // 회원가입 성공 시 로그인 페이지로 리다이렉션
		} catch (ApplicationException e) {
			log.warn("회원가입 실패: {}, errorCode: {}", e.getMessage(), e.getErrorCode().getCode());
			// ApplicationException 발생 시 (예: 아이디 중복, 이메일 중복 등)
			// RedirectAttributes를 사용하여 에러 메시지를 회원가입 폼으로 전달
			redirectAttributes.addFlashAttribute("error", "true");
			redirectAttributes.addFlashAttribute("errorCode", e.getErrorCode().getCode());
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
			redirectAttributes.addFlashAttribute("signupRequest", signupRequest); // 입력값 유지를 위해
			return "redirect:/signup"; // GET /signup으로 리다이렉트하여 폼을 다시 보여줌
		} catch (Exception e) {
			log.error("회원가입 중 알 수 없는 오류 발생", e);
			redirectAttributes.addFlashAttribute("error", "true");
			redirectAttributes.addFlashAttribute("errorMessage", "알 수 없는 오류로 회원가입에 실패했습니다. 잠시 후 다시 시도해주세요.");
			redirectAttributes.addFlashAttribute("signupRequest", signupRequest); // 입력값 유지를 위해
			return "redirect:/signup"; // GET /signup으로 리다이렉트하여 폼을 다시 보여줌
		}
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