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
@ApiResponse(responseCode = "409", description = "이미 사용 중인 로그인 아이디입니다. (status: \"error\", code: \"A002\")",
	content = @Content(mediaType = "application/json",
		schema = @Schema(implementation = JsendResponse.class),
		examples = @ExampleObject(
			name = "LoginIdConflict",
			summary = "아이디 중복 (A002)",
			value = "{\"status\":\"error\",\"message\":\"이미 사용 중인 아이디입니다.\",\"code\":\"A002\"}")))
public @interface ApiLoginIdConflictResponse {
}