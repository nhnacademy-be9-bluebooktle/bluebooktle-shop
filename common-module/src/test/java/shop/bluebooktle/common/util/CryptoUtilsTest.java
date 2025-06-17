package shop.bluebooktle.common.util;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.util.Base64;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

class CryptoUtilsTest {

	CryptoUtils cryptoUtils;

	// 256-bit (32 bytes) 키 → Base64 인코딩
	private final String validBase64Key = Base64.getEncoder()
		.encodeToString("12345678901234567890123456789012".getBytes());
	private final String shortKey = Base64.getEncoder().encodeToString("short".getBytes());

	@BeforeEach
	void setUp() {
		cryptoUtils = new CryptoUtils();
	}

	@Test
	@DisplayName("초기화 실패 - 키 설정 안 됨")
	void init_fail_emptyKey() {
		ReflectionTestUtils.setField(cryptoUtils, "base64EncodedSecretKey", "");
		assertThatThrownBy(cryptoUtils::init)
			.isInstanceOf(ApplicationException.class)
			.hasMessageContaining("encrypt.key")
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.CRYPTO_INITIALIZATION_FAILED);
	}

	@Test
	@DisplayName("초기화 실패 - Base64 디코딩 불가")
	void init_fail_invalidBase64() {
		String invalidBase64Key = "invalid$$base64===";
		ReflectionTestUtils.setField(cryptoUtils, "base64EncodedSecretKey", invalidBase64Key);
		assertThatThrownBy(cryptoUtils::init)
			.isInstanceOf(ApplicationException.class)
			.hasMessageContaining("Base64")
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.CRYPTO_INITIALIZATION_FAILED);
	}

	@Test
	@DisplayName("초기화 실패 - 잘못된 키 길이")
	void init_fail_invalidKeyLength() {
		ReflectionTestUtils.setField(cryptoUtils, "base64EncodedSecretKey", shortKey);
		assertThatThrownBy(cryptoUtils::init)
			.isInstanceOf(ApplicationException.class)
			.hasMessageContaining("AES 키 길이")
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.CRYPTO_INITIALIZATION_FAILED);
	}

	@Test
	@DisplayName("초기화 성공")
	void init_success() {
		ReflectionTestUtils.setField(cryptoUtils, "base64EncodedSecretKey", validBase64Key);
		cryptoUtils.init();
		assertThat(ReflectionTestUtils.getField(cryptoUtils, "secretKeySpec")).isNotNull();
	}

	@Test
	@DisplayName("암호화/복호화 정상 작동")
	void encrypt_decrypt_success() {
		ReflectionTestUtils.setField(cryptoUtils, "base64EncodedSecretKey", validBase64Key);
		cryptoUtils.init();

		String plain = "암호화할 텍스트입니다!";
		String encrypted = cryptoUtils.encrypt(plain);
		String decrypted = cryptoUtils.decrypt(encrypted);

		assertThat(encrypted).isNotEqualTo(plain);
		assertThat(decrypted).isEqualTo(plain);
	}

	@Test
	@DisplayName("암호화 null 입력 시 null 반환")
	void encrypt_null() {
		ReflectionTestUtils.setField(cryptoUtils, "base64EncodedSecretKey", validBase64Key);
		cryptoUtils.init();

		assertThat(cryptoUtils.encrypt(null)).isNull();
	}

	@Test
	@DisplayName("복호화 null 입력 시 null 반환")
	void decrypt_null() {
		ReflectionTestUtils.setField(cryptoUtils, "base64EncodedSecretKey", validBase64Key);
		cryptoUtils.init();

		assertThat(cryptoUtils.decrypt(null)).isNull();
	}

	@Test
	@DisplayName("복호화 실패 - 너무 짧은 문자열")
	void decrypt_tooShort() {
		ReflectionTestUtils.setField(cryptoUtils, "base64EncodedSecretKey", validBase64Key);
		cryptoUtils.init();

		String tooShort = Base64.getEncoder().encodeToString("shorttext".getBytes());
		assertThatThrownBy(() -> cryptoUtils.decrypt(tooShort))
			.isInstanceOf(ApplicationException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.CRYPTO_DECRYPTION_FAILED);

	}

	@Test
	@DisplayName("복호화 실패 - 암호문 조작됨 (AEADBadTagException)")
	void decrypt_modifiedCipherText() {
		ReflectionTestUtils.setField(cryptoUtils, "base64EncodedSecretKey", validBase64Key);
		cryptoUtils.init();

		String encrypted = cryptoUtils.encrypt("안전한 문자열입니다.");
		// 암호문을 일부 바꿔서 위조
		String modified = encrypted.substring(0, encrypted.length() - 2) + "==";

		assertThatThrownBy(() -> cryptoUtils.decrypt(modified))
			.isInstanceOf(ApplicationException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.CRYPTO_INVALID_DATA_FOR_DECRYPTION);
	}
}