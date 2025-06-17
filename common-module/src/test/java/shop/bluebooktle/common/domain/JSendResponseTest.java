package shop.bluebooktle.common.domain;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import shop.bluebooktle.common.dto.common.JsendResponse;

class JSendResponseTest {

	@Test
	@DisplayName("success(data) 응답 테스트")
	void successWithDataTest() {
		// given
		String data = "정상 응답";

		// when
		JsendResponse<String> response = JsendResponse.success(data);

		// then
		assertThat(response.status()).isEqualTo("success");
		assertThat(response.data()).isEqualTo(data);
		assertThat(response.message()).isNull();
		assertThat(response.code()).isNull();
	}

	@Test
	@DisplayName("success() (void) 응답 테스트")
	void successVoidTest() {
		// when
		JsendResponse<Void> response = JsendResponse.success();

		// then
		assertThat(response.status()).isEqualTo("success");
		assertThat(response.data()).isNull();
		assertThat(response.message()).isNull();
		assertThat(response.code()).isNull();
	}

	@Test
	@DisplayName("fail(data) 응답 테스트")
	void failWithDataTest() {
		// given
		String fieldError = "이름은 필수입니다.";

		// when
		JsendResponse<String> response = JsendResponse.fail(fieldError);

		// then
		assertThat(response.status()).isEqualTo("fail");
		assertThat(response.data()).isEqualTo(fieldError);
		assertThat(response.message()).isNull();
		assertThat(response.code()).isNull();
	}

	@Test
	@DisplayName("error(message) 응답 테스트")
	void errorMessageOnlyTest() {
		// given
		String message = "서버 오류 발생";

		// when
		JsendResponse<Void> response = JsendResponse.error(message);

		// then
		assertThat(response.status()).isEqualTo("error");
		assertThat(response.data()).isNull();
		assertThat(response.message()).isEqualTo(message);
		assertThat(response.code()).isNull();
	}

	@Test
	@DisplayName("error(message, code) 응답 테스트")
	void errorWithCodeTest() {
		// given
		String message = "인증 실패";
		String code = "AUTH_001";

		// when
		JsendResponse<Void> response = JsendResponse.error(message, code);

		// then
		assertThat(response.status()).isEqualTo("error");
		assertThat(response.message()).isEqualTo(message);
		assertThat(response.code()).isEqualTo(code);
		assertThat(response.data()).isNull();
	}

	@Test
	@DisplayName("error(message, code, data) 응답 테스트")
	void errorWithDataTest() {
		// given
		String message = "유효성 검사 실패";
		String code = "VAL_400";
		String detail = "이메일 형식이 올바르지 않습니다.";

		// when
		JsendResponse<String> response = JsendResponse.error(message, code, detail);

		// then
		assertThat(response.status()).isEqualTo("error");
		assertThat(response.message()).isEqualTo(message);
		assertThat(response.code()).isEqualTo(code);
		assertThat(response.data()).isEqualTo(detail);
	}

}
