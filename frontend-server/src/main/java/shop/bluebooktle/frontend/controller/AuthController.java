package shop.bluebooktle.frontend.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
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
import shop.bluebooktle.common.dto.user.request.IssueDormantAuthCodeRequest;
import shop.bluebooktle.common.dto.user.request.ReactivateDormantUserRequest;
import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;
import shop.bluebooktle.frontend.service.AuthService;
import shop.bluebooktle.frontend.service.UserService;
import shop.bluebooktle.frontend.util.CookieTokenUtil;

@Slf4j
@Controller
@RequestMapping("/")
@RequiredArgsConstructor
@RefreshScope
public class AuthController {

	private final UserService userService;
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
		return "redirect:/merge";

	}

	@ExceptionHandler(ApplicationException.class)
	public String handleApplicationException(ApplicationException ex, RedirectAttributes redirectAttributes) {
		ErrorCode errorCode = ex.getErrorCode();
		if (errorCode.getCode().equals(ErrorCode.AUTH_INACTIVE_ACCOUNT.getCode())) {
			return "redirect:/auth/reactive";
		}

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
			return "redirect:/merge";

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

	@GetMapping("/auth/reactive")
	public String reactiveDormantUserForm() {
		return "auth/reactive_form";
	}

	@PostMapping("/auth/dormant/issue-code")
	public String handleIssueDormantCodeForm(@ModelAttribute IssueDormantAuthCodeRequest request,
		RedirectAttributes redirectAttributes) {
		try {
			userService.requestDormantCode(request);
			redirectAttributes.addFlashAttribute("globalSuccessMessage",
				"인증코드가 발송되었습니다. 인증코드의 유효기간은 5분입니다. ");
		} catch (ApplicationException e) {
			redirectAttributes.addFlashAttribute("formErrorSource", "issueCodeForm");
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
			redirectAttributes.addFlashAttribute("errorCode", e.getErrorCode().getCode());
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("formErrorSource", "issueCodeForm");
			redirectAttributes.addFlashAttribute("errorMessage", "인증 코드 발급 중 서버 내부 오류가 발생했습니다.");
			redirectAttributes.addFlashAttribute("errorCode", ErrorCode.INTERNAL_SERVER_ERROR.getCode());
		}
		return "redirect:/auth/reactive";
	}

	@PostMapping("/auth/reactivate-account")
	public String handleReactivateDormantUserForm(@ModelAttribute ReactivateDormantUserRequest request,
		RedirectAttributes redirectAttributes) {
		try {
			userService.processReactivateDormant(request);
			redirectAttributes.addFlashAttribute("globalSuccessMessage", "계정이 성공적으로 활성화되었습니다. 다시 로그인해주세요.");
			return "redirect:/login";
		} catch (ApplicationException e) {
			log.warn("Frontend AuthController: 휴면 계정 활성화 요청 실패 (form): loginId={}, errorCode={}, errorMessage={}",
				request.getLoginId(), e.getErrorCode().getCode(), e.getMessage());
			redirectAttributes.addFlashAttribute("formErrorSource", "reactivateForm");
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
			redirectAttributes.addFlashAttribute("errorCode", e.getErrorCode().getCode());
		} catch (Exception e) {
			log.error("Frontend AuthController: 휴면 계정 활성화 요청 중 예상치 못한 오류 (form): loginId={}", request.getLoginId(), e);
			redirectAttributes.addFlashAttribute("formErrorSource", "reactivateForm");
			redirectAttributes.addFlashAttribute("errorMessage", "계정 활성화 중 서버 내부 오류가 발생했습니다.");
			redirectAttributes.addFlashAttribute("errorCode", ErrorCode.INTERNAL_SERVER_ERROR.getCode());
		}
		return "redirect:/auth/reactive";
	}
}