package shop.bluebooktle.common.dto.auth.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@AllArgsConstructor
@Schema(description = "토큰 응답 DTO")
public class TokenResponse {
	private String accessToken;
	private String refreshToken;
}