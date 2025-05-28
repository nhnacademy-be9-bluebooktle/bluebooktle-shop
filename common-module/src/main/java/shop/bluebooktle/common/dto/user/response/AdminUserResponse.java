package shop.bluebooktle.common.dto.user.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.bluebooktle.common.domain.auth.UserStatus;
import shop.bluebooktle.common.domain.auth.UserType;
import shop.bluebooktle.common.entity.auth.User;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserResponse {
	private Long userId;
	private String loginId;
	private String name;
	private String nickname;
	private String email;
	private String phoneNumber;
	private String birthDate;
	private BigDecimal pointBalance;
	private UserType userType;
	private UserStatus userStatus;
	private String membershipName;
	private Long membershipId;
	private LocalDateTime lastLoginAt;
	private LocalDateTime createdAt;
	private LocalDateTime deletedAt;

	public static AdminUserResponse fromEntity(User user) {
		return AdminUserResponse.builder()
			.userId(user.getId())
			.loginId(user.getLoginId())
			.name(user.getName())
			.nickname(user.getNickname())
			.email(user.getEmail())
			.phoneNumber(user.getPhoneNumber())
			.birthDate(user.getBirth())
			.pointBalance(user.getPointBalance())
			.userType(user.getType())
			.userStatus(user.getStatus())
			.membershipId(user.getMembershipLevel() != null ? user.getMembershipLevel().getId() : null)
			.membershipName(user.getMembershipLevel() != null ? user.getMembershipLevel().getName() : null)
			.lastLoginAt(user.getLastLoginAt())
			.createdAt(user.getCreatedAt())
			.deletedAt(user.getDeletedAt())
			.build();
	}
}