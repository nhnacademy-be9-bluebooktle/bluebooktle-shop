package shop.bluebooktle.common.dto.user.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {
	@NotBlank(message = "닉네임은 필수입니다.")
	@Size(max = 20, message = "닉네임는 20자 이하로 입력해주세요.")
	private String nickname;

	@NotBlank(message = "연락처는 필수입니다.")
	@Size(min = 11, max = 11, message = "유효한 연락처 형식이 아닙니다.")
	private String phoneNumber;
	
	@NotBlank(message = "생일은 필수입니다.")
	private String birthDate;
}