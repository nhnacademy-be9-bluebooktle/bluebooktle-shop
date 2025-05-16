package shop.bluebooktle.common.dto.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SignupRequest {
	@NotBlank(message = "로그인 아이디는 필수입니다.")
	@Size(min = 4, max = 20, message = "로그인 아이디는 4자 이상 20자 이하로 입력해주세요.")
	private String loginId;

	@NotBlank(message = "비밀번호는 필수입니다.")
	@Size(min = 8, message = "비밀번호는 8자 이상으로 입력해주세요.")
	private String password;

	@NotBlank(message = "이름은 필수입니다.")
	private String name;

	@NotBlank(message = "이메일은 필수입니다.")
	@Email(message = "유효한 이메일 형식이 아닙니다.")
	private String email;

	@NotBlank(message = "닉네임은 필수입니다.")
	@Size(max = 20, message = "닉네임는 20자 이하로 입력해주세요.")
	private String nickname;

	@NotBlank(message = "생일은 필수입니다.")
	@Size(min = 8, max = 8, message = "유효한 생일 형식이 아닙니다.")
	private String birth;

	@NotBlank(message = "이메일은 필수입니다.")
	@Size(min = 11, max = 11, message = "유효한 전화번호 형식이 아닙니다.")
	private String phoneNumber;
}