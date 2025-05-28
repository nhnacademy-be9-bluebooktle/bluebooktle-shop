package shop.bluebooktle.common.dto.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import shop.bluebooktle.common.domain.auth.UserStatus;
import shop.bluebooktle.common.domain.auth.UserType;

@Data
@NoArgsConstructor
public class AdminUserUpdateRequest {

	@NotBlank(message = "이름은 필수입니다.")
	private String name;

	@NotBlank(message = "닉네임은 필수입니다.")
	private String nickname;

	@NotBlank(message = "이메일은 필수입니다.")
	@Email(message = "유효한 이메일 형식이 아닙니다.")
	private String email;

	@NotBlank(message = "연락처는 필수입니다.")
	@Size(min = 11, max = 11, message = "유효한 연락처 형식이 아닙니다.")
	private String phoneNumber;

	@NotBlank(message = "생일은 필수입니다.")
	private String birthDate;

	private UserType userType;

	private UserStatus userStatus;

	private Long membershipId;
}