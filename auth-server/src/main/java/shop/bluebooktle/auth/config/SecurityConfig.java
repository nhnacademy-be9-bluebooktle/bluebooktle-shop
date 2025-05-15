package shop.bluebooktle.auth.config;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private static final String[] SWAGGER_PATHS = {
		"/swagger-ui/**",
		"/swagger-ui.html",
		"/v3/api-docs/**",
		"/swagger-resources/**",
	};

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	@Order(1)
	public SecurityFilterChain h2ConsoleSecurityFilterChain(HttpSecurity http) throws Exception {
		http
			.securityMatcher(antMatcher("/h2-console/**"))
			.authorizeHttpRequests(authorize -> authorize
				.anyRequest().permitAll()
			)
			.headers(headers -> headers
				.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
			)
			.csrf(AbstractHttpConfigurer::disable);

		return http.build();
	}

	@Bean
	@Order(2)
	public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable)
			.formLogin(AbstractHttpConfigurer::disable)
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(authorize -> authorize
				.requestMatchers("/**").permitAll()
				.requestMatchers(SWAGGER_PATHS).permitAll()
				.anyRequest().authenticated()
			);
		return http.build();
	}
}