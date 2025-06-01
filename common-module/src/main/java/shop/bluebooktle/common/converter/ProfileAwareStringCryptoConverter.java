package shop.bluebooktle.common.converter;

import java.util.List;

import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;

import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;
import shop.bluebooktle.common.util.CryptoUtils;

@Converter
@Component
@RequiredArgsConstructor()
public class ProfileAwareStringCryptoConverter implements StringAttributeCryptoConverter {

	private final Environment environment;
	private final CryptoUtils cryptoUtils;

	private static final List<String> CRYPTO_ACTIVE_PROFILES = List.of("prod", "local");

	@Override
	public String convertToDatabaseColumn(String attribute) {
		if (attribute == null) {
			return null;
		}
		if (environment.acceptsProfiles(Profiles.of(CRYPTO_ACTIVE_PROFILES.toArray(new String[0])))) {
			return cryptoUtils.encrypt(attribute);
		}
		return attribute;
	}

	@Override
	public String convertToEntityAttribute(String dbData) {
		if (dbData == null) {
			return null;
		}
		if (environment.acceptsProfiles(Profiles.of(CRYPTO_ACTIVE_PROFILES.toArray(new String[0])))) {
			return cryptoUtils.decrypt(dbData);
		}
		return dbData;
	}
}