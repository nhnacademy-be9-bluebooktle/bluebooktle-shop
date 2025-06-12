package shop.bluebooktle.frontend.config.feign.decoder;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.common.dto.auth.request.TokenRefreshRequest;
import shop.bluebooktle.common.dto.auth.response.TokenResponse;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;
import shop.bluebooktle.frontend.exception.TokenRefreshAndRetryNeededException;
import shop.bluebooktle.frontend.service.AuthService;
import shop.bluebooktle.frontend.util.CookieTokenUtil;

@Slf4j
@RequiredArgsConstructor
public class GlobalFeignErrorDecoder implements ErrorDecoder {

	private final ObjectMapper objectMapper;
	private final CookieTokenUtil cookieTokenUtil;
	private final ObjectProvider<AuthService> authServiceProvider;
	private static final Object tokenRefreshLock = new Object();

	@Override
	public Exception decode(String methodKey, Response response) {
		String requestUrl = response.request().url();
		JsendResponse<Object> jsendResponse = null;
		String responseBodyString = "";

		if (response.body() != null) {
			try {
				byte[] bodyBytes = Util.toByteArray(response.body().asInputStream());
				responseBodyString = new String(bodyBytes, Util.UTF_8);
				if (!responseBodyString.isEmpty()) {
					jsendResponse = objectMapper.readValue(responseBodyString, new TypeReference<>() {
					});
				}
			} catch (IOException e) {
				log.warn("응답 본문 읽기 또는 JsendResponse 파싱 실패: {}", requestUrl, e);
				return createFallbackException(response, requestUrl, "응답 본문 파싱 실패: " + e.getMessage(),
					responseBodyString);
			}
		}

		Exception originalException = buildExceptionFromResponse(response, jsendResponse, requestUrl,
			responseBodyString);

		boolean isAccessTokenExpiredError = false;
		if (originalException instanceof ApplicationException ae) {
			ErrorCode code = ae.getErrorCode();
			if (code == ErrorCode.UNAUTHORIZED || code == ErrorCode.AUTH_TOKEN_VALIDATION_FAILED) {
				isAccessTokenExpiredError = true;
			}
		}

		if (requestUrl.contains("/auth/refresh")) {
			isAccessTokenExpiredError = false;
		}

		if (isAccessTokenExpiredError) {
			ServletRequestAttributes sra = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
			HttpServletRequest currentRequest = (sra != null) ? sra.getRequest() : null;
			HttpServletResponse currentResponse = (sra != null) ? sra.getResponse() : null;

			if (currentRequest != null && currentResponse != null) {
				return handleTokenRefresh(currentRequest, currentResponse, requestUrl, response.status(),
					originalException);
			}
		}

		return originalException;
	}

	private Exception handleTokenRefresh(HttpServletRequest currentRequest, HttpServletResponse currentResponse,
		String originalRequestUrl, int originalResponseStatus, Exception originalException) {
		Optional<String> refreshTokenOptional = cookieTokenUtil.getRefreshToken(currentRequest);

		if (refreshTokenOptional.isEmpty()) {
			cookieTokenUtil.clearTokens(currentResponse);
			log.warn("토큰 재발급 시도 실패: 리프레시 토큰이 존재하지 않습니다. 최초 오류를 반환합니다.");
			return originalException;
		}

		synchronized (tokenRefreshLock) {
			try {
				TokenRefreshRequest tokenRefreshRequest = new TokenRefreshRequest(refreshTokenOptional.get());
				AuthService authService = authServiceProvider.getObject();
				TokenResponse tokenResponse = authService.refreshToken(currentResponse, tokenRefreshRequest);

				if (tokenResponse != null && tokenResponse.getAccessToken() != null) {
					currentRequest.setAttribute(CookieTokenUtil.ACCESS_TOKEN_COOKIE_NAME,
						tokenResponse.getAccessToken());
					String retryMessage = String.format("Access Token 재발급 성공, 재시도 필요. URL: %s, Status: %d",
						originalRequestUrl, originalResponseStatus);
					return new TokenRefreshAndRetryNeededException(retryMessage);
				} else {
					log.warn("토큰 재발급에 실패했습니다(응답 형식 오류). 최초 오류를 반환합니다.");
					cookieTokenUtil.clearTokens(currentResponse);
					return originalException;
				}
			} catch (Exception refreshException) {
				log.error("토큰 재발급 중 예외 발생. 최초 오류를 반환합니다.", refreshException);
				cookieTokenUtil.clearTokens(currentResponse);
				return originalException;
			}
		}
	}

	private ApplicationException buildExceptionFromResponse(Response response, JsendResponse<Object> jsendResponse,
		String requestUrl, String responseBodyString) {
		if (jsendResponse != null) {
			if ("error".equals(jsendResponse.status()) && jsendResponse.code() != null) {
				String errorCodeString = jsendResponse.code();
				String messageFromJSend = jsendResponse.message();
				ErrorCode resolvedErrorCode = ErrorCode.findByStringCode(errorCodeString);
				if (resolvedErrorCode != null) {
					return new ApplicationException(resolvedErrorCode,
						messageFromJSend != null ? messageFromJSend : resolvedErrorCode.getMessage());
				}
				HttpStatus status = HttpStatus.resolve(response.status());
				ErrorCode ec = ErrorCode.BAD_GATEWAY;
				if (status != null && status.is4xxClientError())
					ec = ErrorCode.INVALID_INPUT_VALUE;
				String refinedMessage = String.format(
					"API SERVER(%s)에서 매핑되지 않은 오류 발생. 원격 코드: %s, 원격 메시지: %s, HTTP 상태: %d", requestUrl, errorCodeString,
					messageFromJSend, response.status());
				return new ApplicationException(ec, refinedMessage);
			} else if ("fail".equals(jsendResponse.status())) {
				Object failData = jsendResponse.data();
				String failMessage = jsendResponse.message() != null ? jsendResponse.message() :
					(failData != null ? "클라이언트 요청 실패: " + failData : ErrorCode.INVALID_INPUT_VALUE.getMessage());
				return new ApplicationException(ErrorCode.INVALID_INPUT_VALUE, failMessage);
			}
		}
		return createFallbackException(response, requestUrl, "API 호출 실패", responseBodyString);
	}

	private ApplicationException createFallbackException(Response response, String requestUrl, String defaultMessage,
		String responseBodyString) {
		HttpStatus status = HttpStatus.resolve(response.status());
		ErrorCode determinedEc = ErrorCode.INTERNAL_SERVER_ERROR;
		String baseMessage;
		String detailMessage = "";

		if (responseBodyString != null && !responseBodyString.isEmpty()) {
			detailMessage = ". 응답 내용: " + responseBodyString;
		}

		if (status != null) {
			if (status.is4xxClientError()) {
				determinedEc = ErrorCode.BAD_GATEWAY;
				if (status == HttpStatus.FORBIDDEN)
					determinedEc = ErrorCode.HANDLE_ACCESS_DENIED;
				if (status == HttpStatus.UNAUTHORIZED && !requestUrl.contains("/auth/refresh")) {
					determinedEc = ErrorCode.UNAUTHORIZED;
				} else if (status == HttpStatus.UNAUTHORIZED && requestUrl.contains("/auth/refresh")) {
					determinedEc = ErrorCode.AUTH_INVALID_REFRESH_TOKEN;
				}
				if (status == HttpStatus.NOT_FOUND)
					determinedEc = ErrorCode.RESOURCE_NOT_FOUND;
				baseMessage = String.format("%s (API SERVER URL: %s)", determinedEc.getMessage(), requestUrl);
			} else if (status.is5xxServerError()) {
				determinedEc = ErrorCode.BAD_GATEWAY;
				baseMessage = String.format("%s (API SERVER URL: %s)", determinedEc.getMessage(), requestUrl);
			} else {
				baseMessage = String.format("%s (API SERVER URL: %s, HTTP 상태: %d)", defaultMessage, requestUrl,
					status.value());
			}
		} else {
			baseMessage = String.format("%s (API SERVER URL: %s, HTTP 상태 코드 없음)", defaultMessage, requestUrl);
		}
		return new ApplicationException(determinedEc, baseMessage + detailMessage);
	}
}