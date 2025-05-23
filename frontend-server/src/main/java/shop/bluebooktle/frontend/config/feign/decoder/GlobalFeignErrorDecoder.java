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

		ServletRequestAttributes sra = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
		HttpServletRequest currentRequest = (sra != null) ? sra.getRequest() : null;
		HttpServletResponse currentResponse = (sra != null) ? sra.getResponse() : null;

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

		boolean isAccessTokenExpiredError = false;
		if (jsendResponse != null && "error".equals(jsendResponse.status()) && jsendResponse.code() != null) {
			ErrorCode resolvedErrorCode = ErrorCode.findByStringCode(jsendResponse.code());
			if (ErrorCode.UNAUTHORIZED.equals(resolvedErrorCode)
				|| ErrorCode.AUTH_TOKEN_VALIDATION_FAILED.getCode().equals(jsendResponse.code())) {
				isAccessTokenExpiredError = true;
			}
		} else if (response.status() == HttpStatus.UNAUTHORIZED.value()) {
			if (!requestUrl.contains("/auth/refresh")) {
				isAccessTokenExpiredError = true;
			}
		}

		if (isAccessTokenExpiredError
			&& !requestUrl.contains("/auth/refresh") && currentRequest != null && currentResponse != null) {
			return handleTokenRefresh(currentRequest, currentResponse, requestUrl, response.status());
		}

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

	private Exception handleTokenRefresh(HttpServletRequest currentRequest, HttpServletResponse currentResponse,
		String originalRequestUrl, int originalResponseStatus) {
		Optional<String> refreshTokenOptional = cookieTokenUtil.getRefreshToken(currentRequest);
		if (refreshTokenOptional.isEmpty()) {
			cookieTokenUtil.clearTokens(currentResponse);
			return new ApplicationException(ErrorCode.AUTH_INVALID_REFRESH_TOKEN,
				"Refresh Token이 없습니다. 다시 로그인해주세요.");
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
					cookieTokenUtil.clearTokens(currentResponse);
					return new ApplicationException(ErrorCode.AUTH_TOKEN_REISSUE_FAILED,
						"토큰 재발급에 실패했습니다(응답 형식 오류). 다시 로그인해주세요.");
				}
			} catch (Exception refreshException) {
				log.error("토큰 재발급 중 예외 발생", refreshException);
				cookieTokenUtil.clearTokens(currentResponse);
				if (refreshException instanceof ApplicationException ae) {
					if (ae.getErrorCode().equals(ErrorCode.AUTH_INVALID_REFRESH_TOKEN)) {
						log.warn("Refresh Token 만료 또는 유효하지 않음: {}", ae.getMessage());
						return new ApplicationException(ae.getErrorCode(), "세션이 만료되었습니다. 다시 로그인해주세요.");
					}
					return new ApplicationException(ErrorCode.AUTH_TOKEN_REISSUE_FAILED,
						"토큰 재발급 중 오류: " + ae.getMessage());
				}
				return new ApplicationException(ErrorCode.AUTH_TOKEN_REISSUE_FAILED,
					"토큰 재발급 중 예상치 못한 오류가 발생했습니다. 다시 로그인해주세요.");
			}
		}
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