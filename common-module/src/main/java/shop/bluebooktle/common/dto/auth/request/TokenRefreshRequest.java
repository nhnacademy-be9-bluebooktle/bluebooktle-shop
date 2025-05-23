package shop.bluebooktle.common.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TokenRefreshRequest {
	@NotBlank(message = "리프레시 토큰은 필수입니다.")
	private String refreshToken;
}