package shop.bluebooktle.frontend.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.RequestContextFilter;

import shop.bluebooktle.frontend.config.filter.GuestIdFilter;
import shop.bluebooktle.frontend.util.CookieTokenUtil;

@Configuration
public class WebConfig {

	@Bean
	public FilterRegistrationBean<RequestContextFilter> requestContextFilter() {
		FilterRegistrationBean<RequestContextFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new RequestContextFilter());

		return registrationBean;
	}

	@Bean
	public FilterRegistrationBean<GuestIdFilter> guestIdFilter(CookieTokenUtil cookieTokenUtil) {
		FilterRegistrationBean<GuestIdFilter> registration = new FilterRegistrationBean<>();
		registration.setFilter(new GuestIdFilter(cookieTokenUtil));
		registration.addUrlPatterns("/*");
		registration.setOrder(0);
		return registration;
	}
}