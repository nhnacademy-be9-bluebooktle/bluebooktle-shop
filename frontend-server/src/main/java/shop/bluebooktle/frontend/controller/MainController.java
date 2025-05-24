package shop.bluebooktle.frontend.controller;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import shop.bluebooktle.frontend.service.AdminCategoryService;

@Controller
@RequiredArgsConstructor
public class MainController {

	private final AdminCategoryService adminCategoryService;

	@GetMapping("/")
	public String mainPage(
		Model model,
		HttpServletRequest request,
		HttpServletResponse response
	) {
		boolean hasGuestId = Arrays.stream(Optional.ofNullable(request.getCookies()).orElse(new Cookie[0]))
			.anyMatch(cookie -> "GUEST_ID".equals(cookie.getName()));

		if (!hasGuestId) {
			String guestId = UUID.randomUUID().toString();
			ResponseCookie cookie = ResponseCookie.from("GUEST_ID", guestId)
				.httpOnly(true)
				.path("/")
				.maxAge(Duration.ofDays(7))
				.build();
			response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
		}
		return "main";
	}
}