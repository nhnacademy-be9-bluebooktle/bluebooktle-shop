package shop.bluebooktle.backend.coupon.jpa;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import jakarta.persistence.EntityManager;
import shop.bluebooktle.backend.config.JpaAuditingConfiguration;
import shop.bluebooktle.backend.config.QueryDslConfig;
import shop.bluebooktle.backend.coupon.entity.CouponType;
import shop.bluebooktle.backend.coupon.repository.CouponRepository;
import shop.bluebooktle.backend.user.repository.MembershipLevelRepository;
import shop.bluebooktle.backend.user.repository.UserRepository;
import shop.bluebooktle.common.converter.ProfileAwareStringCryptoConverter;
import shop.bluebooktle.common.domain.coupon.CouponTypeTarget;
import shop.bluebooktle.common.dto.coupon.response.CouponTypeResponse;
import shop.bluebooktle.common.util.CryptoUtils;

@DataJpaTest
@ActiveProfiles("test")
@Import({QueryDslConfig.class, JpaAuditingConfiguration.class, CryptoUtils.class,
	ProfileAwareStringCryptoConverter.class})
class CouponRepositoryTest {
	@Autowired
	private CouponRepository couponRepository;

	@Autowired
	private MembershipLevelRepository membershipLevelRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private EntityManager em;

	@Test
	@DisplayName("쿠폰 정책 전체 조회 테스트")
	void findAllCouponTypeTest() {
		// given
		CouponType couponType = CouponType.builder()
			.name("정책 1")
			.target(CouponTypeTarget.ORDER)
			.minimumPayment(new BigDecimal("10000"))
			.build();
		em.persist(couponType);
		em.flush();

		// when
		Page<CouponTypeResponse> result = couponRepository.findAllByCouponType(PageRequest.of(0, 10));

		// then
		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().getFirst().getName()).isEqualTo("정책 1");
	}
}
