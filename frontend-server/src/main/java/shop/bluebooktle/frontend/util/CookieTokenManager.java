package shop.bluebooktle.frontend.util;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CookieTokenManager {

	public static final String ACCESS_TOKEN_COOKIE_NAME = "accessToken";
	public static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";

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
}
