package shop.bluebooktle.common.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.bluebooktle.common.domain.UserStatus;
import shop.bluebooktle.common.domain.UserType;
import shop.bluebooktle.common.entity.MembershipLevel;
import shop.bluebooktle.common.entity.User;

@Getter
@NoArgsConstructor
public class SignupRequestDto {

	@NotBlank(message = "로그인 아이디는 필수입니다.")
	@Size(min = 4, max = 50, message = "로그인 아이디는 4자 이상 50자 이하로 입력해주세요.")
	private String loginId;

	@NotBlank(message = "비밀번호는 필수입니다.")
	@Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
	private String password;

	@NotBlank(message = "이름은 필수입니다.")
	@Size(max = 20)
	private String name;

	@NotBlank(message = "이메일은 필수입니다.")
	@Email(message = "올바른 이메일 형식이 아닙니다.")
	@Size(max = 50)
	private String email;

	@NotNull(message = "생년월일은 필수입니다.")
	private String birth;

	@NotBlank(message = "전화번호는 필수입니다.")
	@Size(max = 11, message = "전화번호는 11자리 이내로 입력해주세요.")
	private String phoneNumber;

	@Builder
	public SignupRequestDto(String loginId, String password, String name, String email, String birth,
		String phoneNumber) {
		this.loginId = loginId;
		this.password = password;
		this.name = name;
		this.email = email;
		this.birth = birth;
		this.phoneNumber = phoneNumber;
	}

	public User toEntity(String encodedPassword, MembershipLevel membershipLevel) {
		return User.builder()
			.loginId(this.loginId)
			.encodedPassword(encodedPassword)
			.name(this.name)
			.email(this.email)
			.birth(this.birth)
			.phoneNumber(this.phoneNumber)
			.membershipLevel(membershipLevel)
			.pointBalance(BigDecimal.ZERO)
			.type(UserType.USER)
			.status(UserStatus.ACTIVE)
			.build();
	}
}