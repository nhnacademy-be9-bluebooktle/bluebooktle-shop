package shop.bluebooktle.frontend.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.RequestContextFilter;

@Configuration
public class WebConfig {

	@Bean
	public FilterRegistrationBean<RequestContextFilter> requestContextFilter() {
		FilterRegistrationBean<RequestContextFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new RequestContextFilter());

		return registrationBean;
	}
}