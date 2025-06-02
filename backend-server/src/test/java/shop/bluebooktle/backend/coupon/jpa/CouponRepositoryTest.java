package shop.bluebooktle.backend.coupon.jpa;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
import shop.bluebooktle.backend.coupon.dto.CouponSearchRequest;
import shop.bluebooktle.backend.coupon.entity.Coupon;
import shop.bluebooktle.backend.coupon.entity.CouponType;
import shop.bluebooktle.backend.coupon.entity.UserCoupon;
import shop.bluebooktle.backend.coupon.repository.CouponRepository;
import shop.bluebooktle.backend.user.repository.MembershipLevelRepository;
import shop.bluebooktle.backend.user.repository.UserRepository;
import shop.bluebooktle.common.converter.ProfileAwareStringCryptoConverter;
import shop.bluebooktle.common.domain.CouponTypeTarget;
import shop.bluebooktle.common.domain.auth.UserStatus;
import shop.bluebooktle.common.domain.auth.UserType;
import shop.bluebooktle.common.dto.coupon.response.CouponResponse;
import shop.bluebooktle.common.dto.coupon.response.CouponTypeResponse;
import shop.bluebooktle.common.dto.coupon.response.UserCouponResponse;
import shop.bluebooktle.common.entity.auth.MembershipLevel;
import shop.bluebooktle.common.entity.auth.User;
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
	@DisplayName("쿠폰 전체 조회")
	void testFindAllByCoupon() {
		// given
		CouponType type = CouponType.builder()
			.name("타입 A")
			.target(CouponTypeTarget.ORDER)
			.minimumPayment(new BigDecimal("10000"))
			.build();
		em.persist(type);

		Coupon coupon = Coupon.builder()
			.couponName("쿠폰1")
			.type(type)
			.build();
		em.persist(coupon);

		em.flush();
		em.clear();

		// when
		CouponSearchRequest request = new CouponSearchRequest(); // target 없음
		Page<CouponResponse> result = couponRepository.findAllByCoupon(request, PageRequest.of(0, 10));

		// then
		assertThat(result.getTotalElements()).isEqualTo(1);
		assertThat(result.getContent().getFirst().getCouponName()).isEqualTo("쿠폰1");
	}

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

	@Test
	@DisplayName("유저별 쿠폰 전체 조회 테스트")
	void findAllByUserCouponTest() {
		// given
		MembershipLevel membership = membershipLevelRepository.save(
			MembershipLevel.builder()
				.name("BASIC")
				.rate(1)
				.minNetSpent(BigDecimal.ZERO)
				.maxNetSpent(BigDecimal.valueOf(100000))
				.build()
		);

		User user = userRepository.save(
			User.builder()
				.membershipLevel(membership)
				.loginId("test123")
				.encodedPassword("password123")
				.name("테스트 유저")
				.nickname("test")
				.email("test@example.com")
				.birth("2025-01-01")
				.phoneNumber("01012345678")
				.type(UserType.USER)
				.status(UserStatus.ACTIVE)
				.build()
		);
		em.persist(user);

		CouponType couponType = CouponType.builder()
			.name("정책 2")
			.target(CouponTypeTarget.BOOK)
			.minimumPayment(new BigDecimal("5000"))
			.build();
		em.persist(couponType);

		Coupon coupon = Coupon.builder()
			.couponName("쿠폰 1")
			.type(couponType)
			.build();
		em.persist(coupon);

		UserCoupon userCoupon = UserCoupon.builder()
			.user(user)
			.coupon(coupon)
			.availableStartAt(LocalDateTime.now().minusDays(1))
			.availableEndAt(LocalDateTime.now().plusDays(1))
			.build();
		em.persist(userCoupon);

		em.flush();

		// when
		Page<UserCouponResponse> result = couponRepository.findAllByUserCoupon(user, PageRequest.of(0, 10));

		// then
		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().getFirst().couponName()).isEqualTo("쿠폰 1");
		assertThat(result.getContent().getFirst().couponTypeName()).isEqualTo("정책 2");
	}
}
