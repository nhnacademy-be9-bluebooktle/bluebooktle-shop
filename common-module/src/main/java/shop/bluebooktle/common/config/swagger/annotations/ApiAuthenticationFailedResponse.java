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
@ApiResponse(responseCode = "401", description = "인증 실패 (아이디 또는 비밀번호 불일치). (status: \"error\", code: \"A001\")",
	content = @Content(mediaType = "application/json",
		schema = @Schema(implementation = JsendResponse.class),
		examples = @ExampleObject(
			name = "AuthenticationFailed",
			summary = "인증 실패 (A001)",
			value = "{\"status\":\"error\",\"message\":\"아이디 또는 비밀번호가 일치하지 않습니다.\",\"code\":\"A001\"}")))
public @interface ApiAuthenticationFailedResponse {
}