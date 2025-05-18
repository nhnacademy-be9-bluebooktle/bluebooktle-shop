package shop.bluebooktle.frontend.config;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class FeignBearerTokenInterceptor implements RequestInterceptor {

	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String BEARER_PREFIX = "Bearer ";
	private static final String TOKEN_COOKIE_NAME = "accessToken";

	@Override
	public void apply(RequestTemplate template) {
		ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
		if (attributes != null) {
			HttpServletRequest request = attributes.getRequest();
			Cookie[] cookies = request.getCookies();

			if (cookies != null) {
				for (Cookie cookie : cookies) {
					if (TOKEN_COOKIE_NAME.equals(cookie.getName())) {
						String token = cookie.getValue();
						if (token != null && !token.isEmpty()) {
							template.header(AUTHORIZATION_HEADER, BEARER_PREFIX + token);
						}
						return;
					}
				}
			}
		}
	}
}