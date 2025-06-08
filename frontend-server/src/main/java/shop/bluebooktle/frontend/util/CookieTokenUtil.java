package shop.bluebooktle.frontend.util;

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CookieTokenUtil {

	public static final String ACCESS_TOKEN_COOKIE_NAME = "BB_AT";
	public static final String REFRESH_TOKEN_COOKIE_NAME = "BB_RT";

	public Optional<String> getAccessToken(HttpServletRequest request) {
		return getCookieValue(request, ACCESS_TOKEN_COOKIE_NAME);
	}

	public Optional<String> getRefreshToken(HttpServletRequest request) {
		return getCookieValue(request, REFRESH_TOKEN_COOKIE_NAME);
	}

	public void saveTokens(HttpServletResponse response, String accessToken, String refreshToken) {
		saveTokenAsCookie(response, ACCESS_TOKEN_COOKIE_NAME, accessToken);
		saveTokenAsCookie(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken);
	}

	public void clearTokens(HttpServletResponse response) {
		deleteCookie(response, ACCESS_TOKEN_COOKIE_NAME);
		deleteCookie(response, REFRESH_TOKEN_COOKIE_NAME);
	}

	private Optional<String> getCookieValue(HttpServletRequest request, String cookieName) {
		if (request.getCookies() == null) {
			return Optional.empty();
		}
		return Arrays.stream(request.getCookies())
			.filter(cookie -> cookieName.equals(cookie.getName()))
			.map(Cookie::getValue)
			.findFirst();
	}

	private void saveTokenAsCookie(HttpServletResponse response, String cookieName, String tokenValue) {
		if (tokenValue == null)
			return;
		Map<String, Object> claims = JwtPayloadUtil.getPayloadClaims(tokenValue);

		Cookie cookie = new Cookie(cookieName, tokenValue);
		cookie.setHttpOnly(true);
		cookie.setSecure(false);
		cookie.setPath("/");
		Long expTimestampInSeconds = JwtPayloadUtil.getClaim(claims, "exp", Long.class);
		if (expTimestampInSeconds != null) {
			long currentTimeInSeconds = System.currentTimeMillis() / 1000;
			int maxAgeInSeconds = (int)(expTimestampInSeconds - currentTimeInSeconds);
			if (maxAgeInSeconds > 0) {
				cookie.setMaxAge(maxAgeInSeconds);
			} else {
				cookie.setMaxAge(0);
			}
		}
		// cookie.setSameSite("Strict");
		response.addCookie(cookie);
	}

	private void deleteCookie(HttpServletResponse response, String cookieName) {
		Cookie cookie = new Cookie(cookieName, null);
		cookie.setHttpOnly(true);
		cookie.setSecure(false);
		cookie.setPath("/");
		cookie.setMaxAge(0);
		response.addCookie(cookie);
	}

	public static final String GUEST_ID_COOKIE_NAME = "GUEST_ID";

	public String getOrCreateGuestId(HttpServletRequest request, HttpServletResponse response) {
		Optional<String> existing = getCookieValue(request, GUEST_ID_COOKIE_NAME);
		if (existing.isPresent()) {
			return existing.get();
		}

		String guestId = UUID.randomUUID().toString();
		ResponseCookie cookie = ResponseCookie.from(GUEST_ID_COOKIE_NAME, guestId)
			.httpOnly(true)
			.path("/")
			.maxAge(Duration.ofDays(7))
			.build();
		response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

		return guestId;
	}
}
