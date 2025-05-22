package shop.bluebooktle.frontend.config;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import shop.bluebooktle.frontend.util.CookieTokenUtil;

@Component
@RequiredArgsConstructor
public class FeignBearerTokenInterceptor implements RequestInterceptor {

	private final CookieTokenUtil cookieTokenUtil;

	@Override
	public void apply(RequestTemplate template) {
		String url = template.url();
		if (url.contains("/auth/refresh") || url.contains("/auth/login") || url.contains("/auth/signup")) {
			return;
		}

		ServletRequestAttributes requestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
		if (requestAttributes != null) {
			HttpServletRequest request = requestAttributes.getRequest();
			Optional<String> accessToken = cookieTokenUtil.getAccessToken(request);

			accessToken.ifPresent(token -> template.header("Authorization", "Bearer " + token));
		}
	}
}