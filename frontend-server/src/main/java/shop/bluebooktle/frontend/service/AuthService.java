package shop.bluebooktle.frontend.service;

import org.springframework.stereotype.Service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.common.dto.auth.request.LoginRequest;
import shop.bluebooktle.common.dto.auth.request.SignupRequest;
import shop.bluebooktle.common.dto.auth.request.TokenRefreshRequest;
import shop.bluebooktle.common.dto.auth.response.TokenResponse;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;
import shop.bluebooktle.frontend.repository.AuthRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

	private final AuthRepository authRepository;

	public void signup(SignupRequest signupRequest) {
		JsendResponse<Void> response = authRepository.signup(signupRequest);

		if (response == null) {
			throw new ApplicationException(ErrorCode.BAD_GATEWAY, "회원가입 중 외부 서비스 응답이 없습니다.");
		}

		if (!"success".equals(response.getStatus())) {
			log.warn("Login failed via auth-server (JSend status: {}) for ID: {}. Message: {}, Code: {}, Data: {}",
				response.getStatus(), signupRequest.getEmail(), response.getMessage(), response.getCode(),
				response.getData());
			ErrorCode determinedErrorCode = determineErrorCodeFromJsend(response, ErrorCode.INVALID_INPUT_VALUE);
			String message = response.getMessage() != null ? response.getMessage() : determinedErrorCode.getMessage();
			throw new ApplicationException(determinedErrorCode, message);
		}
	}

	public TokenResponse login(LoginRequest loginRequest) {
		try {
			JsendResponse<TokenResponse> response = authRepository.login(loginRequest);
			if (response == null) {
				throw new ApplicationException(ErrorCode.BAD_GATEWAY, "로그인 중 외부 서비스 응답이 없습니다.");
			}

			if ("success".equals(response.getStatus()) && response.getData() != null) {
				return response.getData();
			} else {
				log.warn("Login failed via auth-server (JSend status: {}) for ID: {}. Message: {}, Code: {}, Data: {}",
					response.getStatus(), loginRequest.getLoginId(), response.getMessage(), response.getCode(),
					response.getData());
				ErrorCode determinedErrorCode = determineErrorCodeFromJsend(response,
					ErrorCode.AUTH_AUTHENTICATION_FAILED);
				String message =
					response.getMessage() != null ? response.getMessage() : determinedErrorCode.getMessage();
				throw new ApplicationException(determinedErrorCode, message);
			}
		} catch (FeignException.FeignClientException e) {
			log.debug(String.valueOf(e.responseBody()));
			throw e;
		} catch (Exception e) {
			throw e;
		}

	}

	public TokenResponse refreshToken(TokenRefreshRequest tokenRefreshRequest) {
		JsendResponse<TokenResponse> response = authRepository.refreshToken(tokenRefreshRequest);

		if (response == null) {
			throw new ApplicationException(ErrorCode.BAD_GATEWAY, "토큰 갱신 중 외부 서비스 응답이 없습니다.");
		}

		if ("success".equals(response.getStatus()) && response.getData() != null) {
			return response.getData();
		} else {
			log.warn("Token refresh failed via auth-server (JSend status: {}) Message: {}, Code: {}, Data: {}",
				response.getStatus(), response.getMessage(), response.getCode(), response.getData());
			ErrorCode determinedErrorCode = determineErrorCodeFromJsend(response, ErrorCode.AUTH_INVALID_REFRESH_TOKEN);
			String message = response.getMessage() != null ? response.getMessage() : determinedErrorCode.getMessage();
			throw new ApplicationException(determinedErrorCode, message);
		}
	}

	private ErrorCode determineErrorCodeFromJsend(JsendResponse<?> jsendResponse, ErrorCode defaultErrorCode) {
		if (jsendResponse != null && jsendResponse.getCode() != null) {
			ErrorCode found = ErrorCode.findByStringCode(jsendResponse.getCode());
			if (found != null) {
				return found;
			}
		}
		return defaultErrorCode;
	}
}