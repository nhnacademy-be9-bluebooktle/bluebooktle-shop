package shop.bluebooktle.common.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Jsend 표준 응답 DTO")
public class JsendResponse<T> {

	@Schema(
		description = "응답 상태 (항상 'success'를 가정)",
		example = "success",
		requiredMode = Schema.RequiredMode.REQUIRED)
	private final String status;

	@Schema(description = "응답 데이터", nullable = true)
	private T data;

	@Schema(
		description = "메시지 (status가 'fail' 또는 'error'인 경우 상세 설명)",
		example = "요청 처리에 실패했습니다.",
		nullable = true)
	private String message;

	@Schema(description = "에러 코드 (status가 'error'인 경우 식별 코드)",
		example = "C001",
		nullable = true)
	private String code;

	private JsendResponse(String status, T data, String message, String code) {
		this.status = status;
		this.data = data;
		this.message = message;
		this.code = code;
	}

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