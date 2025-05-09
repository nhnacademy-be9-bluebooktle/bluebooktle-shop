package shop.bluebooktle.common.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsendResponse<T> {

	private final String status;
	private T data;
	private String message;
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