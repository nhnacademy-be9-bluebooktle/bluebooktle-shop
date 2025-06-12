package shop.bluebooktle.backend.point.repository;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import shop.bluebooktle.backend.config.JpaAuditingConfiguration;
import shop.bluebooktle.backend.config.QueryDslConfig;
import shop.bluebooktle.backend.user.repository.MembershipLevelRepository;
import shop.bluebooktle.backend.user.repository.UserRepository;
import shop.bluebooktle.common.converter.PointSourceTypeConverter;
import shop.bluebooktle.common.converter.ProfileAwareStringCryptoConverter;
import shop.bluebooktle.common.domain.auth.UserStatus;
import shop.bluebooktle.common.domain.auth.UserType;
import shop.bluebooktle.common.domain.point.PointSourceTypeEnum;
import shop.bluebooktle.common.entity.auth.MembershipLevel;
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.entity.point.PointHistory;
import shop.bluebooktle.common.util.CryptoUtils;

@DataJpaTest
@ActiveProfiles("test")
@Import({QueryDslConfig.class, JpaAuditingConfiguration.class, CryptoUtils.class,
	ProfileAwareStringCryptoConverter.class, PointSourceTypeConverter.class})
public class PointHistoryRepositoryTest {

	@Autowired
	private PointHistoryRepository pointHistoryRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private MembershipLevelRepository membershipLevelRepository;
	private User testUser;

	private User createUser() {
		MembershipLevel ml = membershipLevelRepository.save(
			MembershipLevel.builder()
				.name("BASIC")
				.rate(1)
				.minNetSpent(BigDecimal.ZERO)
				.maxNetSpent(BigDecimal.valueOf(100_000))
				.build()
		);

		return userRepository.save(
			User.builder()
				.membershipLevel(ml)
				.loginId("user" + System.nanoTime()) // 중복 방지를 위해 매번 유니크한 값을 넣어줌
				.encodedPassword("pwd123")
				.name("테스터")
				.nickname("nick" + System.nanoTime())
				.email("test" + System.nanoTime() + "@example.com")
				.birth("2003-02-02")
				.phoneNumber("01012345678")
				.type(UserType.USER)
				.status(UserStatus.ACTIVE)
				.build()
		);
	}

	@BeforeEach
	void setUp() {
		testUser = createUser();
	}

	@Test
	@Transactional
	@DisplayName("PointHistory 저장 테스트")
	void save() {
		PointHistory pointHistory = PointHistory.builder()
			.user(testUser)
			.sourceType(PointSourceTypeEnum.PAYMENT_EARN)
			.value(BigDecimal.valueOf(100_000))
			.build();

		PointHistory savedPointHistory = pointHistoryRepository.save(pointHistory);

		assertThat(savedPointHistory.getId()).isNotNull();
	}

	@Test
	@DisplayName("사용자 ID로 포인트 내역을 최신순으로 페이지네이션하여 조회한다")
	void findByUserIdOrderByCreatedAtDesc() {

		pointHistoryRepository.save(PointHistory.builder()
			.user(testUser)
			.sourceType(PointSourceTypeEnum.REVIEW_EARN)
			.value(new BigDecimal("100"))
			.build());

		PointHistory latestHistory = pointHistoryRepository.save(PointHistory.builder()
			.user(testUser)
			.sourceType(PointSourceTypeEnum.PAYMENT_EARN)
			.value(new BigDecimal("500"))
			.build());

		PageRequest pageRequest = PageRequest.of(0, 5);
		Page<PointHistory> resultPage = pointHistoryRepository.findByUserIdOrderByCreatedAtDesc(testUser.getId(),
			pageRequest);

		assertThat(resultPage.getTotalElements()).isEqualTo(2);

		assertThat(resultPage.getNumberOfElements()).isEqualTo(2);

		assertThat(resultPage.getContent().getFirst().getId()).isEqualTo(latestHistory.getId());
		assertThat(resultPage.getContent().getFirst().getValue()).isEqualByComparingTo("500");
	}
}
