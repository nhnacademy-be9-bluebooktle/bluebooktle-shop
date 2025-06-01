package shop.bluebooktle.common.util;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.AEADBadTagException;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

@Component
public class CryptoUtils {

	private static final String ALGORITHM = "AES";
	private static final String TRANSFORMATION = "AES/GCM/NoPadding";
	private static final int IV_LENGTH_BYTES = 12;
	private static final int TAG_LENGTH_BITS = 128;

	@Value("${encrypt.key}")
	private String base64EncodedSecretKey;

	private SecretKeySpec secretKeySpec;

	@PostConstruct
	public void init() {
		if (base64EncodedSecretKey == null || base64EncodedSecretKey.isEmpty()) {
			throw new ApplicationException(ErrorCode.CRYPTO_INITIALIZATION_FAILED,
				"'encrypt.key' 프로퍼티가 설정되지 않았거나 비어있습니다.");
		}
		try {
			byte[] secretKeyBytes = Base64.getDecoder().decode(base64EncodedSecretKey);
			if (secretKeyBytes.length != 16 && secretKeyBytes.length != 24
				&& secretKeyBytes.length != 32) {
				throw new ApplicationException(ErrorCode.CRYPTO_INITIALIZATION_FAILED,
					"잘못된 AES 키 길이입니다. Base64 디코딩 후 16, 24, 또는 32 바이트여야 합니다.");
			}
			this.secretKeySpec = new SecretKeySpec(secretKeyBytes, ALGORITHM);
		} catch (IllegalArgumentException e) {
			throw new ApplicationException(ErrorCode.CRYPTO_INITIALIZATION_FAILED, "잘못된 Base64 인코딩 키입니다", e);
		}
	}

	public String encrypt(String value) {
		if (value == null) {
			return null;
		}
		try {
			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			byte[] iv = new byte[IV_LENGTH_BYTES];
			SecureRandom random = new SecureRandom();
			random.nextBytes(iv);
			GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(TAG_LENGTH_BITS, iv);

			cipher.init(Cipher.ENCRYPT_MODE, this.secretKeySpec, gcmParameterSpec);

			byte[] encryptedPayloadBytes = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));

			byte[] combinedIvAndEncryptedPayload = new byte[iv.length + encryptedPayloadBytes.length];
			System.arraycopy(iv, 0, combinedIvAndEncryptedPayload, 0, iv.length);
			System.arraycopy(encryptedPayloadBytes, 0, combinedIvAndEncryptedPayload, iv.length,
				encryptedPayloadBytes.length);

			return Base64.getEncoder().encodeToString(combinedIvAndEncryptedPayload);

		} catch (Exception e) {
			throw new ApplicationException(ErrorCode.CRYPTO_ENCRYPTION_FAILED, "데이터 암호화 중 예기치 않은 오류: " + e.getMessage(),
				e);
		}
	}

	public String decrypt(String encryptedValueWithIv) {
		if (encryptedValueWithIv == null) {
			return null;
		}

		try {
			byte[] combinedIvAndEncryptedPayload = Base64.getDecoder().decode(encryptedValueWithIv);
			if (combinedIvAndEncryptedPayload.length < IV_LENGTH_BYTES + (TAG_LENGTH_BITS / 8)) {
				throw new ApplicationException(ErrorCode.CRYPTO_INVALID_DATA_FOR_DECRYPTION,
					String.format("암호문 데이터 길이가 너무 짧습니다. (길이: %d, 최소 기대 길이: %d)",
						combinedIvAndEncryptedPayload.length, IV_LENGTH_BYTES + (TAG_LENGTH_BITS / 8)));
			}
			byte[] iv = Arrays.copyOfRange(combinedIvAndEncryptedPayload, 0, IV_LENGTH_BYTES);
			byte[] encryptedPayloadBytes = Arrays.copyOfRange(combinedIvAndEncryptedPayload, IV_LENGTH_BYTES,
				combinedIvAndEncryptedPayload.length);

			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(TAG_LENGTH_BITS, iv);

			cipher.init(Cipher.DECRYPT_MODE, this.secretKeySpec, gcmParameterSpec);

			byte[] decryptedValueBytes = cipher.doFinal(encryptedPayloadBytes);

			return new String(decryptedValueBytes, StandardCharsets.UTF_8);
		} catch (AEADBadTagException | IllegalArgumentException e) {
			throw new ApplicationException(ErrorCode.CRYPTO_INVALID_DATA_FOR_DECRYPTION, e);
		} catch (Exception e) {
			throw new ApplicationException(ErrorCode.CRYPTO_DECRYPTION_FAILED, e);
		}
	}
}