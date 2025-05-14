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
@ApiResponse(responseCode = "201", description = "회원가입 성공. (status: \"success\", data: null 이거나 생략됨)",
	content = @Content(mediaType = "application/json",
		schema = @Schema(implementation = JsendResponse.class),
		examples = @ExampleObject(
			name = "SignupSuccess",
			summary = "회원가입 성공 예시",
			value = "{\"status\":\"success\"}")))
public @interface ApiSignupSuccessResponse {
}


