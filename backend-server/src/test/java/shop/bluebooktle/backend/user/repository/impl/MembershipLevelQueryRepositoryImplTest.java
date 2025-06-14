package shop.bluebooktle.backend.user.repository.impl;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import shop.bluebooktle.backend.config.JpaAuditingConfiguration;
import shop.bluebooktle.backend.config.QueryDslConfig;
import shop.bluebooktle.common.converter.ProfileAwareStringCryptoConverter;
import shop.bluebooktle.common.dto.membership.MembershipLevelDetailDto;
import shop.bluebooktle.common.entity.auth.MembershipLevel;
import shop.bluebooktle.common.util.CryptoUtils;

@DataJpaTest
@ActiveProfiles("test")
@Import({
	QueryDslConfig.class,
	JpaAuditingConfiguration.class,
	CryptoUtils.class,
	ProfileAwareStringCryptoConverter.class
})
class MembershipLevelQueryRepositoryImplTest {

	private MembershipLevelQueryRepositoryImpl membershipLevelQueryRepository;

	@Autowired
	private EntityManager em;

	@Autowired
	private JPAQueryFactory queryFactory;

	private MembershipLevel general, royal, gold, platinum;

	@BeforeEach
	void setUp() {
		membershipLevelQueryRepository = new MembershipLevelQueryRepositoryImpl(queryFactory);

		general = MembershipLevel.builder().name("일반").rate(1)
			.minNetSpent(BigDecimal.valueOf(0.00)).maxNetSpent(BigDecimal.valueOf(99999.99)).build();
		royal = MembershipLevel.builder().name("로얄").rate(2)
			.minNetSpent(BigDecimal.valueOf(100000.00)).maxNetSpent(BigDecimal.valueOf(199999.99)).build();
		gold = MembershipLevel.builder().name("골드").rate(3)
			.minNetSpent(BigDecimal.valueOf(200000.00)).maxNetSpent(BigDecimal.valueOf(299999.99)).build();
		platinum = MembershipLevel.builder().name("플래티넘").rate(4)
			.minNetSpent(BigDecimal.valueOf(300000.00)).maxNetSpent(BigDecimal.valueOf(99999999.99)).build();

		em.persist(general);
		em.persist(royal);
		em.persist(gold);
		em.persist(platinum);
		em.flush();
		em.clear();
	}

	@Test
	@DisplayName("순수 지출액으로 '로얄' 등급 조회 성공")
	void findByMembershipLevelForAmount_findsRoyal_success() {
		// given
		BigDecimal royalAmount = new BigDecimal("150000.00");

		// when
		MembershipLevel foundLevel = membershipLevelQueryRepository.findByMembershipLevelForAmount(royalAmount);

		// then
		assertNotNull(foundLevel);
		assertEquals("로얄", foundLevel.getName());
	}

	@Test
	@DisplayName("순수 지출액으로 '플래티넘' 등급 조회 성공")
	void findByMembershipLevelForAmount_findsPlatinum_success() {
		// given
		BigDecimal platinumAmount = new BigDecimal("500000.00");

		// when
		MembershipLevel foundLevel = membershipLevelQueryRepository.findByMembershipLevelForAmount(platinumAmount);

		// then
		assertNotNull(foundLevel);
		assertEquals("플래티넘", foundLevel.getName());
	}

	@Test
	@DisplayName("모든 멤버십 등급 목록 조회 (정렬 순서 확인)")
	void findAllMembershipLevels_success() {
		// when
		List<MembershipLevelDetailDto> allLevels = membershipLevelQueryRepository.findAllMembershipLevels();

		// then
		assertNotNull(allLevels);
		assertEquals(4, allLevels.size());
		assertEquals("일반", allLevels.get(0).name());
		assertEquals("로얄", allLevels.get(1).name());
		assertEquals("골드", allLevels.get(2).name());
		assertEquals("플래티넘", allLevels.get(3).name());
	}
}