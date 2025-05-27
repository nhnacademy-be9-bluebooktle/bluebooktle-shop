package shop.bluebooktle.frontend.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.common.dto.auth.request.LoginRequest;
import shop.bluebooktle.common.dto.auth.request.SignupRequest;
import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;
import shop.bluebooktle.frontend.service.AuthService;
import shop.bluebooktle.frontend.util.CookieTokenUtil;

@Slf4j
@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class AuthController {

	@Value("${oauth.payco.client-id}")
	private String paycoClientIdFromConfig;

	@Value("${oauth.payco.redirect-uri}")
	private String paycoRedirectUri;

	private final AuthService authService;
	private final CookieTokenUtil cookieTokenUtil;

	@GetMapping("/login")
	public String loginForm(Model model,
		@ModelAttribute("errorCode") String errorCode,
		@ModelAttribute("errorMessage") String errorMessage) {
		if (errorCode != null && !errorCode.isEmpty()) {
			model.addAttribute("error", "true");
		}

		model.addAttribute("paycoClientId", paycoClientIdFromConfig);

		try {
			String encodedPaycoRedirectUri = URLEncoder.encode(paycoRedirectUri, StandardCharsets.UTF_8);
			model.addAttribute("paycoRedirectUriEncoded", encodedPaycoRedirectUri);
		} catch (Exception e) {
			model.addAttribute("paycoRedirectUriEncoded", paycoRedirectUri);
		}
		return "auth/login_form";
	}

	@PostMapping("/login")
	public String login(@ModelAttribute LoginRequest loginRequest,
		HttpServletResponse response) {
		authService.login(response, loginRequest);
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

	@GetMapping("/logout")
	public String handleLogout(HttpServletResponse response,
		@ModelAttribute("globalSuccessMessage") String message,
		RedirectAttributes redirectAttributes) {

		if (StringUtils.hasText(message)) {
			redirectAttributes.addFlashAttribute("globalSuccessMessage", message);
		}

		cookieTokenUtil.clearTokens(response);
		return "redirect:/";
	}

	@ExceptionHandler(Exception.class)
	public String handleApplicationException(Exception ex, RedirectAttributes redirectAttributes) {
		String errorMessage = ex.getMessage();

		redirectAttributes.addFlashAttribute("error", "true");
		redirectAttributes.addFlashAttribute("errorCode", "C001");
		redirectAttributes.addFlashAttribute("errorMessage", errorMessage);

		return "redirect:/login";
	}

	@GetMapping("/oauth/payco")
	public String oauthPayco(
		@RequestParam("code") String code,
		@RequestParam("state") String state,
		@RequestParam(value = "error", required = false) String error,
		@RequestParam(value = "error_description", required = false) String errorDescription,
		HttpServletResponse response,
		RedirectAttributes redirectAttributes) {

		log.info("PAYCO OAuth Callback received. Code: {}, State: {}", code, state);

		try {
			authService.paycoLogin(response, code);
			log.info("PAYCO 로그인 성공");
			return "redirect:/";

		} catch (ApplicationException e) {
			log.error("PAYCO 로그인 처리 중 오류 발생 (ApplicationException): {}", e.getMessage());
			redirectAttributes.addFlashAttribute("error", "true");
			redirectAttributes.addFlashAttribute("errorCode", e.getErrorCode().getCode());
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
			return "redirect:/login";
		} catch (Exception e) {
			log.error("PAYCO 로그인 처리 중 알 수 없는 오류 발생 {} : {}", error, errorDescription, e);
			redirectAttributes.addFlashAttribute("error", "true");
			redirectAttributes.addFlashAttribute("errorMessage", "PAYCO 로그인 중 오류가 발생했습니다. 다시 시도해주세요.");
			return "redirect:/login";
		}
	}
}