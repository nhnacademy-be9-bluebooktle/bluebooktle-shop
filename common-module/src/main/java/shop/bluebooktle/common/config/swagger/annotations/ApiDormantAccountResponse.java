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
@ApiResponse(responseCode = "403", description = "비활성화(휴면) 상태 계정입니다. (status: \"error\", code: \"A004\")",
	content = @Content(mediaType = "application/json",
		schema = @Schema(implementation = JsendResponse.class),
		examples = @ExampleObject(
			name = "DormantAccount",
			summary = "휴면 계정 (A004)",
			value = "{\"status\":\"error\",\"message\":\"비활성화(휴면) 상태 계정입니다. 인증 후 활성화 해주세요.\",\"code\":\"A004\"}")))
public @interface ApiDormantAccountResponse {
}