package shop.bluebooktle.common.config.swagger.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import shop.bluebooktle.common.dto.common.JsendResponse;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ApiResponse(responseCode = "400", description = "입력값 유효성 검사 실패. (status: \"fail\", data: {필드명: \"오류메시지\"})",
	content = @Content(mediaType = "application/json",
		schema = @Schema(implementation = JsendResponse.class), // JsendResponse<Map<String, String>>
		examples = @ExampleObject(
			name = "ValidationFail",
			summary = "유효성 검사 실패 예시",
			value = "{\"status\":\"fail\",\"data\":{\"password\":\"비밀번호는 필수입니다.\",\"phoneNumber\":\"이메일은 필수입니다.\",\"name\":\"이름은 필수입니다.\",\"email\":\"이메일은 필수입니다.\"}}")))
public @interface ApiValidationFailResponse {
}