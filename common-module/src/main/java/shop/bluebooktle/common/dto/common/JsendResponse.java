package shop.bluebooktle.common.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Jsend 표준 응답 DTO")
public record JsendResponse<T>(
	@Schema(
		description = "응답 상태",
		example = "success",
		requiredMode = Schema.RequiredMode.REQUIRED)
	String status,

	@Schema(description = "응답 데이터", nullable = true)
	T data,

	@Schema(
		description = "메시지 (status가 'fail' 또는 'error'인 경우 상세 설명)",
		example = "요청 처리에 실패했습니다.",
		nullable = true)
	String message,

	@Schema(description = "에러 코드 (status가 'error'인 경우 식별 코드)",
		example = "C001",
		nullable = true)
	String code
) {

	public static <T> JsendResponse<T> success(T data) {
		return new JsendResponse<>("success", data, null, null);
	}

	public static JsendResponse<Void> success() {
		return new JsendResponse<>("success", null, null, null);
	}

	public static <T> JsendResponse<T> fail(T data) {
		return new JsendResponse<>("fail", data, null, null);
	}

	public static JsendResponse<Void> error(String message) {
		return new JsendResponse<>("error", null, message, null);
	}

	public static JsendResponse<Void> error(String message, String code) {
		return new JsendResponse<>("error", null, message, code);
	}

	public static <T> JsendResponse<T> error(String message, String code, T data) {
		return new JsendResponse<>("error", data, message, code);
	}
}