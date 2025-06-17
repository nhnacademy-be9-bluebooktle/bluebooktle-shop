package shop.bluebooktle.common.domain.user;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import shop.bluebooktle.common.domain.auth.UserStatus;
import shop.bluebooktle.common.domain.auth.UserType;
import shop.bluebooktle.common.dto.user.response.AdminUserResponse;

class AdminUserResponseTest {

	@Test
	@DisplayName("Builder로 객체 생성 테스트")
	void builderTest() {
		// given
		AdminUserResponse response = AdminUserResponse.builder()
			.userId(1L)
			.loginId("admin")
			.name("관리자")
			.nickname("adminNick")
			.email("admin@example.com")
			.phoneNumber("01012345678")
			.birthDate("1990-01-01")
			.pointBalance(BigDecimal.valueOf(10000))
			.userType(UserType.USER)
			.userStatus(UserStatus.ACTIVE)
			.membershipId(5L)
			.membershipName("Gold")
			.lastLoginAt(LocalDateTime.of(2024, 6, 1, 12, 0))
			.createdAt(LocalDateTime.of(2024, 1, 1, 10, 0))
			.deletedAt(null)
			.build();

		// then
		assertThat(response.getUserId()).isEqualTo(1L);
		assertThat(response.getLoginId()).isEqualTo("admin");
		assertThat(response.getName()).isEqualTo("관리자");
		assertThat(response.getNickname()).isEqualTo("adminNick");
		assertThat(response.getEmail()).isEqualTo("admin@example.com");
		assertThat(response.getPhoneNumber()).isEqualTo("01012345678");
		assertThat(response.getBirthDate()).isEqualTo("1990-01-01");
		assertThat(response.getPointBalance()).isEqualByComparingTo(BigDecimal.valueOf(10000));
		assertThat(response.getUserType()).isEqualTo(UserType.USER);
		assertThat(response.getUserStatus()).isEqualTo(UserStatus.ACTIVE);
		assertThat(response.getMembershipId()).isEqualTo(5L);
		assertThat(response.getMembershipName()).isEqualTo("Gold");
		assertThat(response.getLastLoginAt()).isEqualTo(LocalDateTime.of(2024, 6, 1, 12, 0));
		assertThat(response.getCreatedAt()).isEqualTo(LocalDateTime.of(2024, 1, 1, 10, 0));
		assertThat(response.getDeletedAt()).isNull();
	}

}
