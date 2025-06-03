package shop.bluebooktle.common.dto.user.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReactivateDormantUserRequest {

	@NotBlank(message = "로그인 아이디를 입력해주세요.")
	private String loginId;

	@NotBlank(message = "인증 코드를 입력해주세요.")
	private String authCode;
}