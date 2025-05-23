package shop.bluebooktle.frontend.config.feign.decoder;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import feign.FeignException;
import feign.Response;
import feign.Util;
import feign.codec.Decoder;
import lombok.RequiredArgsConstructor;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

@RequiredArgsConstructor
public class JsendDecoder implements Decoder {
	private final ObjectMapper objectMapper;

	@Override
	public Object decode(Response response, Type type) throws IOException, FeignException {
		if (response.status() == 204 || response.body() == null) {
			if (Void.class.equals(type) || type.getTypeName().equals("void") || type.getTypeName().equals("Void")) {
				return null;
			}
			if (isOptionalType(type)) {
				return Optional.empty();
			}
			throw new ApplicationException(ErrorCode.BAD_GATEWAY,
				"외부 서비스로부터 빈 응답(HTTP " + response.status() + ")을 받았으나, 반환 타입(" + type.getTypeName() + ")은 데이터를 기대합니다.");
		}

		String responseBodyString = Util.toString(response.body().asReader(Util.UTF_8));

		JavaType actualDataType = TypeFactory.defaultInstance().constructType(type);
		JavaType jsendResponseType = TypeFactory.defaultInstance()
			.constructParametricType(JsendResponse.class, actualDataType);

		JsendResponse<?> jsendResponse;
		try {
			jsendResponse = objectMapper.readValue(responseBodyString, jsendResponseType);
		} catch (IOException e) {
			throw new ApplicationException(ErrorCode.BAD_GATEWAY,
				"외부 서비스 응답 파싱 실패 (HTTP " + response.status() + "): " + e.getMessage(), e);
		}

		if (jsendResponse == null) {
			throw new ApplicationException(ErrorCode.BAD_GATEWAY,
				"외부 서비스 응답 파싱 결과가 null입니다 (HTTP " + response.status() + ")");
		}

		if ("success".equals(jsendResponse.status())) {
			Object data = jsendResponse.data();

			if (data == null && !Void.class.equals(type) && !type.getTypeName().equals("void") && !isOptionalType(
				type)) {
				throw new ApplicationException(ErrorCode.BAD_GATEWAY,
					"외부 서비스 응답 데이터가 없습니다 (status: success, HTTP " + response.status() + ").");
			}
			if (data == null && isOptionalType(type)) {
				return Optional.empty();
			}
			return data;
		} else {
			String responseCodeStr = jsendResponse.code();
			String responseMessage = jsendResponse.message();
			ErrorCode determinedErrorCode = ErrorCode.BAD_GATEWAY;

			if (responseCodeStr != null && !responseCodeStr.trim().isEmpty()) {
				ErrorCode foundByCode = ErrorCode.findByStringCode(responseCodeStr);
				if (foundByCode != null) {
					determinedErrorCode = foundByCode;
				}
			}

			String messageToThrow = responseMessage != null ? responseMessage : determinedErrorCode.getMessage();
			if (responseCodeStr != null && ErrorCode.findByStringCode(responseCodeStr) == null) {
				messageToThrow = String.format("외부 서비스 오류 (code: %s, HTTP %d): %s", responseCodeStr, response.status(),
					messageToThrow);
			} else {
				messageToThrow = String.format("%s (HTTP %d)", messageToThrow, response.status());
			}

			throw new ApplicationException(determinedErrorCode, messageToThrow);
		}
	}

	private boolean isOptionalType(Type type) {
		if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType)type;
			return parameterizedType.getRawType().equals(Optional.class);
		}
		return false;
	}
}