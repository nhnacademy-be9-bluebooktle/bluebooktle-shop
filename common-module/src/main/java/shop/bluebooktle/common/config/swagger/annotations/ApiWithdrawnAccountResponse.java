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
@ApiResponse(responseCode = "403", description = "탈퇴한 계정입니다. (status: \"error\", code: \"A011\")",
	content = @Content(mediaType = "application/json",
		schema = @Schema(implementation = JsendResponse.class),
		examples = @ExampleObject(
			name = "WithdrawnAccount",
			summary = "탈퇴 계정 (A011)",
			value = "{\"status\":\"error\",\"message\":\"탈퇴한 계정입니다.\",\"code\":\"A011\"}")))
public @interface ApiWithdrawnAccountResponse {
}