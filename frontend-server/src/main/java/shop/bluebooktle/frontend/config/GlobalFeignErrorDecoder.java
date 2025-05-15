package shop.bluebooktle.frontend.config;

import java.io.IOException;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;

@RequiredArgsConstructor
public class GlobalFeignErrorDecoder implements ErrorDecoder {

	private final ObjectMapper objectMapper;

	@Override
	public Exception decode(String methodKey, Response response) {
		String requestUrl = response.request().url();
		JsendResponse<Object> jsendResponse;
		String responseBodyString;

		if (response.body() != null) {
			try {
				byte[] bodyBytes = Util.toByteArray(response.body().asInputStream());
				responseBodyString = new String(bodyBytes, Util.UTF_8);
				jsendResponse = objectMapper.readValue(responseBodyString,
					new TypeReference<>() {
					});
			} catch (IOException e) {
				HttpStatus status = HttpStatus.resolve(response.status());
				ErrorCode ec = (status != null && status.is5xxServerError()) ? ErrorCode.BAD_GATEWAY :
					ErrorCode.INVALID_INPUT_VALUE;
				String message = String.format("%s (API SERVER URL: %s, 사유: 응답 본문 파싱 실패)", ec.getMessage(), requestUrl);
				return new ApplicationException(ec, message);
			}
		} else {
			HttpStatus status = HttpStatus.resolve(response.status());
			ErrorCode ec =
				(status != null && status.is5xxServerError()) ? ErrorCode.BAD_GATEWAY : ErrorCode.INVALID_INPUT_VALUE;
			String message = String.format("%s (API SERVER URL: %s, 사유: 응답 본문 없음)", ec.getMessage(), requestUrl);
			return new ApplicationException(ec, message);
		}

		if (jsendResponse != null && "error".equals(jsendResponse.getStatus()) && jsendResponse.getCode() != null) {
			String errorCodeString = jsendResponse.getCode();
			String messageFromJSend = jsendResponse.getMessage();

			ErrorCode resolvedErrorCode = ErrorCode.findByStringCode(errorCodeString);
			if (resolvedErrorCode != null) {
				return new ApplicationException(resolvedErrorCode,
					messageFromJSend != null ? messageFromJSend : resolvedErrorCode.getMessage());
			} else {
				HttpStatus status = HttpStatus.resolve(response.status());
				ErrorCode ec = ErrorCode.BAD_GATEWAY;
				if (status != null && status.is4xxClientError()) {
					ec = ErrorCode.INVALID_INPUT_VALUE;
				}
				String refinedMessage = String.format(
					"API SERVER(%s)에서 매핑되지 않은 오류 발생. 원격 코드: %s, 원격 메시지: %s, HTTP 상태: %d",
					requestUrl, errorCodeString, messageFromJSend, response.status());
				return new ApplicationException(ec, refinedMessage);
			}
		}

		if (jsendResponse != null && "fail".equals(jsendResponse.getStatus())) {
			Object failData = jsendResponse.getData();
			String failMessage = jsendResponse.getMessage() != null ? jsendResponse.getMessage() :
				(failData != null ? "클라이언트 요청 실패: " + failData : ErrorCode.INVALID_INPUT_VALUE.getMessage());
			return new ApplicationException(ErrorCode.INVALID_INPUT_VALUE, failMessage);
		}

		HttpStatus status = HttpStatus.resolve(response.status());
		ErrorCode determinedEc = ErrorCode.INTERNAL_SERVER_ERROR;
		String baseMessage;
		String detailMessage = "";

		if (!responseBodyString.isEmpty()) {
			detailMessage = ". 응답 내용: " + responseBodyString;
		}

		if (status != null) {
			if (status.is4xxClientError()) {
				determinedEc = ErrorCode.BAD_GATEWAY;
				if (status == HttpStatus.UNAUTHORIZED)
					determinedEc = ErrorCode.UNAUTHORIZED;
				if (status == HttpStatus.FORBIDDEN)
					determinedEc = ErrorCode.HANDLE_ACCESS_DENIED;
				baseMessage = String.format("%s (API SERVER URL: %s)", determinedEc.getMessage(), requestUrl);
			} else if (status.is5xxServerError()) {
				determinedEc = ErrorCode.BAD_GATEWAY;
				baseMessage = String.format("%s (API SERVER URL: %s)", determinedEc.getMessage(), requestUrl);
			} else {
				baseMessage = String.format("%s (API SERVER URL: %s, HTTP 상태: %d)", determinedEc.getMessage(),
					requestUrl,
					status.value());
			}
		} else {
			baseMessage = String.format("%s (API SERVER URL: %s, HTTP 상태 코드 없음)", determinedEc.getMessage(),
				requestUrl);
		}
		return new ApplicationException(determinedEc, baseMessage + detailMessage);
	}
}