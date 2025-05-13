package shop.bluebooktle.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouterConfig {

	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
			.route("backend-server", r -> r.path("/api/**")
				.uri("lb://backend-server"))
			.route("auth-server", r -> r.path("/auth/**")
				.uri("lb://auth-server"))
			.route("frontend-server", r -> r.path("/**")
				.uri("lb://frontend-server"))
			.build();
	}
}
