package shop.bluebooktle.common.converter;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;

import shop.bluebooktle.common.util.CryptoUtils;

class ProfileAwareStringCryptoConverterTest {

	Environment environment;
	CryptoUtils cryptoUtils;
	ProfileAwareStringCryptoConverter converter;

	@BeforeEach
	void setUp() {
		environment = mock(Environment.class);
		cryptoUtils = mock(CryptoUtils.class);
		converter = new ProfileAwareStringCryptoConverter(environment, cryptoUtils);
	}

	@Nested
	@DisplayName("prod 프로필 비활성화 시")
	class NonProdProfile {

		@BeforeEach
		void disableProdProfile() {
			when(environment.acceptsProfiles("prod")).thenReturn(false);
		}

		@Test
		@DisplayName("암호화 생략")
		void noEncrypt() {
			String result = converter.convertToDatabaseColumn("plain");

			assertThat(result).isEqualTo("plain");
			verifyNoInteractions(cryptoUtils);
		}

		@Test
		@DisplayName("복호화 생략")
		void noDecrypt() {
			String result = converter.convertToEntityAttribute("plain");

			assertThat(result).isEqualTo("plain");
			verifyNoInteractions(cryptoUtils);
		}
	}

	@Test
	@DisplayName("null 입력 시 null 반환")
	void handleNullValues() {
		assertThat(converter.convertToDatabaseColumn(null)).isNull();
		assertThat(converter.convertToEntityAttribute(null)).isNull();
		verifyNoInteractions(environment, cryptoUtils);
	}

}
