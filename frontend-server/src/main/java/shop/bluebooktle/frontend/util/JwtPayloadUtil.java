package shop.bluebooktle.frontend.util;

import java.util.Base64;
import java.util.Collections;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JwtPayloadUtil {

	private static final Logger logger = LoggerFactory.getLogger(JwtPayloadUtil.class);
	private static final ObjectMapper objectMapper = new ObjectMapper();

	private JwtPayloadUtil() {
	}

	public static Map<String, Object> getPayloadClaims(String token) {
		if (!StringUtils.hasText(token)) {
			return Collections.emptyMap();
		}

		String[] parts = token.split("\\.");
		if (parts.length < 2) {
			return Collections.emptyMap();
		}

		try {
			String payloadBase64Url = parts[1];
			String payloadBase64 = payloadBase64Url.replace('-', '+').replace('_', '/');
			switch (payloadBase64.length() % 4) {
				case 2:
					payloadBase64 += "==";
					break;
				case 3:
					payloadBase64 += "=";
					break;
				default:
					break;
			}

			byte[] decodedPayloadBytes = Base64.getDecoder().decode(payloadBase64);
			String decodedPayloadJson = new String(decodedPayloadBytes);

			return objectMapper.readValue(decodedPayloadJson, new TypeReference<Map<String, Object>>() {
			});
		} catch (IllegalArgumentException e) {
			logger.warn("Invalid JWT: Base64 decoding failed. Msg: {}. Token starts with: [{}...]",
				e.getMessage(), token.substring(0, Math.min(token.length(), 10)));
		} catch (Exception e) {
			logger.warn("Invalid JWT: Payload parsing failed. Msg: {}. Token starts with: [{}...]",
				e.getMessage(), token.substring(0, Math.min(token.length(), 10)), e);
		}
		return Collections.emptyMap();
	}

	@SuppressWarnings("unchecked")
	public static <T> T getClaim(Map<String, Object> claims, String claimName, Class<T> requiredType) {
		if (claims == null || claims.isEmpty() || !StringUtils.hasText(claimName) || requiredType == null) {
			return null;
		}

		Object value = claims.get(claimName);

		if (value == null) {
			return null;
		}

		if (requiredType.isInstance(value)) {
			return (T)value;
		}

		if (value instanceof Number) {
			Number numValue = (Number)value;
			if (requiredType == Integer.class) {
				return (T)Integer.valueOf(numValue.intValue());
			} else if (requiredType == Long.class) {
				return (T)Long.valueOf(numValue.longValue());
			} else if (requiredType == Double.class) {
				return (T)Double.valueOf(numValue.doubleValue());
			} else if (requiredType == Float.class) {
				return (T)Float.valueOf(numValue.floatValue());
			}
		}

		logger.warn("Claim type mismatch: '{}'. Expected: '{}', Actual: '{}'. Value: '{}'",
			claimName, requiredType.getSimpleName(), value.getClass().getSimpleName(), value);
		return null;
	}
}