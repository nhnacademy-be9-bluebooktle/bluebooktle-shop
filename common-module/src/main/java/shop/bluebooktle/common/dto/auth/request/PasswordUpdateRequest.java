package shop.bluebooktle.common.dto.auth.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PasswordUpdateRequest {

	@NotEmpty(message = "현재 비밀번호는 필수입니다.")
	private String currentPassword;

	@NotEmpty(message = "새 비밀번호는 필수입니다.")
	@Size(min = 8, message = "비밀번호는 8자 이상으로 입력해주세요.")
	private String newPassword;

	@NotEmpty(message = "새 비밀번호 확인은 필수입니다.")
	private String confirmNewPassword;
}