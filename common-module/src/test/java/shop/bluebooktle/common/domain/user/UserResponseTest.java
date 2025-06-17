package shop.bluebooktle.common.domain.user;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import shop.bluebooktle.common.domain.auth.UserProvider;
import shop.bluebooktle.common.domain.auth.UserStatus;
import shop.bluebooktle.common.domain.auth.UserType;
import shop.bluebooktle.common.dto.user.response.UserResponse;
import shop.bluebooktle.common.entity.auth.MembershipLevel;
import shop.bluebooktle.common.entity.auth.User;

class UserResponseTest {

	@Test
	@DisplayName("Builder를 이용한 UserResponse 생성 테스트")
	void builderTest() {
		UserResponse response = UserResponse.builder()
			.id(1L)
			.loginId("user1")
			.name("홍길동")
			.email("hong@example.com")
			.nickname("길동이")
			.birth("1990-01-01")
			.phoneNumber("01012345678")
			.pointBalance(BigDecimal.valueOf(10000))
			.type(UserType.USER)
			.status(UserStatus.ACTIVE)
			.lastLoginAt(LocalDateTime.of(2024, 6, 1, 10, 0))
			.membershipLevelName("Gold")
			.provider(UserProvider.BLUEBOOKTLE)
			.build();

		assertThat(response.getId()).isEqualTo(1L);
		assertThat(response.getLoginId()).isEqualTo("user1");
		assertThat(response.getName()).isEqualTo("홍길동");
		assertThat(response.getEmail()).isEqualTo("hong@example.com");
		assertThat(response.getNickname()).isEqualTo("길동이");
		assertThat(response.getBirth()).isEqualTo("1990-01-01");
		assertThat(response.getPhoneNumber()).isEqualTo("01012345678");
		assertThat(response.getPointBalance()).isEqualByComparingTo("10000");
		assertThat(response.getType()).isEqualTo(UserType.USER);
		assertThat(response.getStatus()).isEqualTo(UserStatus.ACTIVE);
		assertThat(response.getLastLoginAt()).isEqualTo(LocalDateTime.of(2024, 6, 1, 10, 0));
		assertThat(response.getMembershipLevelName()).isEqualTo("Gold");
		assertThat(response.getProvider()).isEqualTo(UserProvider.BLUEBOOKTLE);
	}

	@Test
	@DisplayName("fromEntity(User) 변환 테스트")
	void fromEntityTest() {
		MembershipLevel level = MembershipLevel.builder()
			.name("Platinum")
			.build();

		User user = User.builder()
			.id(2L)
			.loginId("user2")
			.name("이몽룡")
			.email("lee@example.com")
			.nickname("몽룡이")
			.birth("1992-03-03")
			.phoneNumber("01098765432")
			.pointBalance(BigDecimal.valueOf(5000))
			.type(UserType.USER)
			.status(UserStatus.DORMANT)
			.lastLoginAt(LocalDateTime.of(2024, 5, 10, 14, 30))
			.provider(UserProvider.PAYCO)
			.membershipLevel(level)
			.build();

		UserResponse response = UserResponse.fromEntity(user);

		assertThat(response.getId()).isEqualTo(2L);
		assertThat(response.getLoginId()).isEqualTo("user2");
		assertThat(response.getName()).isEqualTo("이몽룡");
		assertThat(response.getEmail()).isEqualTo("lee@example.com");
		assertThat(response.getNickname()).isEqualTo("몽룡이");
		assertThat(response.getBirth()).isEqualTo("1992-03-03");
		assertThat(response.getPhoneNumber()).isEqualTo("01098765432");
		assertThat(response.getPointBalance()).isEqualByComparingTo("5000");
		assertThat(response.getType()).isEqualTo(UserType.USER);
		assertThat(response.getStatus()).isEqualTo(UserStatus.DORMANT);
		assertThat(response.getLastLoginAt()).isEqualTo(LocalDateTime.of(2024, 5, 10, 14, 30));
		assertThat(response.getProvider()).isEqualTo(UserProvider.PAYCO);
		assertThat(response.getMembershipLevelName()).isEqualTo("Platinum");
	}
}

