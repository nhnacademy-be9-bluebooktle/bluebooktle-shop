package shop.bluebooktle.common.dto.user.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class IssueDormantAuthCodeRequest {
	@NotBlank(message = "로그인 아이디를 입력해주세요.")
	private String loginId;
}