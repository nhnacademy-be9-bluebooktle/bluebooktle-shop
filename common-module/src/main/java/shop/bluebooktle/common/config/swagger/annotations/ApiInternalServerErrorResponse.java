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

@Target({ElementType.METHOD, ElementType.TYPE}) // 메소드 또는 클래스 레벨에 적용 가능
@Retention(RetentionPolicy.RUNTIME)
@ApiResponse(responseCode = "500", description = "서버 내부 오류. (status: \"error\", code: \"C001\")",
	content = @Content(mediaType = "application/json",
		schema = @Schema(implementation = JsendResponse.class),
		examples = @ExampleObject(
			name = "InternalServerError",
			summary = "서버 내부 오류 (C001)",
			value = "{\"status\":\"error\",\"message\":\"서버 내부 오류가 발생했습니다.\",\"code\":\"C001\"}")))
public @interface ApiInternalServerErrorResponse {
}
