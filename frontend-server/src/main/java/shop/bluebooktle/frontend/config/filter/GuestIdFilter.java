package shop.bluebooktle.frontend.config.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import shop.bluebooktle.frontend.util.CookieTokenUtil;

@RequiredArgsConstructor
public class GuestIdFilter implements Filter {

	private final CookieTokenUtil cookieTokenUtil;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
		throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest)request;
		HttpServletResponse httpResponse = (HttpServletResponse)response;

		cookieTokenUtil.getOrCreateGuestId(httpRequest, httpResponse);

		chain.doFilter(request, response);
	}
}