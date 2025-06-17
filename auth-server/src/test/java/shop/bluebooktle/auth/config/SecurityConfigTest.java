package shop.bluebooktle.auth.config;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

class SecurityConfigTest {

	SecurityConfig config = new SecurityConfig();

	@Test
	@DisplayName("PasswordEncoder는 BCryptPasswordEncoder를 사용한다")
	void passwordEncoder_create_success() {
		PasswordEncoder encoder = config.passwordEncoder();
		assertThat(encoder)
			.isNotNull()
			.isInstanceOf(PasswordEncoder.class);

	}

	@Test
	@DisplayName("h2ConsoleSecurityFilterChain이 생성된다")
	void h2ConsoleSecurityFilterChain_create_success() throws Exception {
		HttpSecurity http = Mockito.mock(HttpSecurity.class, Mockito.RETURNS_DEEP_STUBS);
		SecurityFilterChain filterChain = config.h2ConsoleSecurityFilterChain(http);
		assertThat(filterChain).isNotNull();
	}

	@Test
	@DisplayName("apiSecurityFilterChain이 생성된다")
	void apiSecurityFilterChain_create_success() throws Exception {
		HttpSecurity http = Mockito.mock(HttpSecurity.class, Mockito.RETURNS_DEEP_STUBS);
		SecurityFilterChain filterChain = config.apiSecurityFilterChain(http);
		assertThat(filterChain).isNotNull();
	}
}
