package shop.bluebooktle.common.dto.user.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.bluebooktle.common.domain.auth.UserProvider;
import shop.bluebooktle.common.domain.auth.UserStatus;
import shop.bluebooktle.common.domain.auth.UserType;
import shop.bluebooktle.common.entity.auth.User;

@Getter
@Setter
@NoArgsConstructor
public class UserResponse {
	private Long id;
	private String loginId;
	private String name;
	private String email;
	private String nickname;
	private String birth;
	private String phoneNumber;
	private UserProvider provider;
	private BigDecimal pointBalance;
	private UserType type;
	private UserStatus status;
	private LocalDateTime lastLoginAt;
	private String membershipLevelName;

	@Builder
	public UserResponse(Long id, String loginId, String name, String email, String nickname, String birth,
		String phoneNumber, BigDecimal pointBalance, UserType type, UserStatus status,
		LocalDateTime lastLoginAt, String membershipLevelName,
		UserProvider provider) {
		this.id = id;
		this.loginId = loginId;
		this.name = name;
		this.email = email;
		this.nickname = nickname;
		this.birth = birth;
		this.phoneNumber = phoneNumber;
		this.pointBalance = pointBalance;
		this.type = type;
		this.status = status;
		this.lastLoginAt = lastLoginAt;
		this.membershipLevelName = membershipLevelName;
		this.provider = provider;
	}

	public static UserResponse fromEntity(User user) {
		return UserResponse.builder()
			.id(user.getId())
			.loginId(user.getLoginId())
			.name(user.getName())
			.email(user.getEmail())
			.nickname(user.getNickname())
			.birth(user.getBirth())
			.phoneNumber(user.getPhoneNumber())
			.pointBalance(user.getPointBalance())
			.type(user.getType())
			.status(user.getStatus())
			.lastLoginAt(user.getLastLoginAt())
			.provider(user.getProvider())
			.membershipLevelName(user.getMembershipLevel() != null ? user.getMembershipLevel().getName() : null)
			.build();
	}
}