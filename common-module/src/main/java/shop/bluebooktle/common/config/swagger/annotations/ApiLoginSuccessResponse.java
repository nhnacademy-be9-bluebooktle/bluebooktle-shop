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
@ApiResponse(responseCode = "200", description = "로그인 성공 및 토큰 발급. (status: \"success\", data: 토큰 정보)",
	content = @Content(mediaType = "application/json",
		schema = @Schema(implementation = JsendResponse.class), // JsendResponse<TokenResponse>
		examples = @ExampleObject(
			name = "LoginSuccess",
			summary = "로그인 성공 예시",
			value = "{\"status\":\"success\",\"data\":{\"accessToken\":\"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlcjEyMyIsInVzZXJJZCI6MSwidXNlclR5cGUiOiJVU0VSIiwiaWF0IjoxNzQ3MjM4MTc2LCJleHAiOjE3NDcyMzk5NzZ9.gVx2Bj3SpOE9XaJ8Wbhs16_tWca2Jqps3RNOnT3LzD4\",\"refreshToken\":\"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlcjEyMyIsInVzZXJJZCI6MSwidXNlclR5cGUiOiJVU0VSIiwiaWF0IjoxNzQ3MjM4MTc2LCJleHAiOjE3NDcyNDE3NzZ9.eq4YT9QkIzLfojW6nJZSr3iIzW-q9nqa1lJ-agI5pYw\"}}")))
public @interface ApiLoginSuccessResponse {
}