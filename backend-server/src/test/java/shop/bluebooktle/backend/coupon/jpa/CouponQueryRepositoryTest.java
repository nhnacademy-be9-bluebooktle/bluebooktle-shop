package shop.bluebooktle.backend.coupon.jpa;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
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
import shop.bluebooktle.backend.coupon.entity.Coupon;
import shop.bluebooktle.backend.coupon.entity.CouponType;
import shop.bluebooktle.backend.coupon.entity.UserCoupon;
import shop.bluebooktle.backend.coupon.repository.CouponRepository;
import shop.bluebooktle.common.converter.ProfileAwareStringCryptoConverter;
import shop.bluebooktle.common.domain.auth.UserProvider;
import shop.bluebooktle.common.domain.auth.UserStatus;
import shop.bluebooktle.common.domain.auth.UserType;
import shop.bluebooktle.common.domain.coupon.CouponTypeTarget;
import shop.bluebooktle.common.domain.coupon.UserCouponFilterType;
import shop.bluebooktle.common.dto.coupon.response.CouponResponse;
import shop.bluebooktle.common.dto.coupon.response.CouponTypeResponse;
import shop.bluebooktle.common.dto.coupon.response.UserCouponResponse;
import shop.bluebooktle.common.entity.auth.MembershipLevel;
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.util.CryptoUtils;

@DataJpaTest
@ActiveProfiles("test")
@Import({
	QueryDslConfig.class,
	JpaAuditingConfiguration.class,
	CryptoUtils.class, ProfileAwareStringCryptoConverter.class,
})
class CouponQueryRepositoryTest {

	@Autowired
	private CouponRepository couponQueryRepository;

	@Autowired
	private EntityManager em;

	private User user;
	private Coupon coupon;
	private UserCoupon userCoupon;

	@BeforeEach
	void setUp() {
		// membershipLevel
		MembershipLevel level = MembershipLevel.builder()
			.name("기본등급")
			.rate(1)
			.minNetSpent(BigDecimal.ZERO)
			.maxNetSpent(BigDecimal.valueOf(999999))
			.build();
		em.persist(level);

		// User
		User user = User.builder()
			.membershipLevel(level)
			.loginId("tester1")
			.encodedPassword("encoded_pw")
			.name("홍길동")
			.email("hong@test.com")
			.nickname("길동이")
			.birth("1990-01-01")
			.phoneNumber("010-1234-5678")
			.provider(UserProvider.BLUEBOOKTLE)
			.type(UserType.USER)
			.status(UserStatus.ACTIVE)
			.build();
		em.persist(user);
		this.user = user;

		// CouponType
		CouponType couponType = CouponType.builder()
			.name("도서할인")
			.target(CouponTypeTarget.BOOK)
			.minimumPayment(BigDecimal.valueOf(10000))
			.build();
		em.persist(couponType);

		Coupon coupon = Coupon.builder()
			.type(couponType)
			.couponName("테스트쿠폰")
			.build();
		em.persist(coupon);
		this.coupon = coupon;

		UserCoupon userCoupon = UserCoupon.builder()
			.coupon(coupon)
			.user(user)
			.availableStartAt(LocalDateTime.now().minusDays(1))
			.availableEndAt(LocalDateTime.now().plusDays(1))
			.usedAt(null)
			.build();
		em.persist(userCoupon);
		this.userCoupon = userCoupon;

		em.flush();
		em.clear();
	}

	@Test
	@DisplayName("쿠폰 조회 - 이름 검색")
	void findAllByCoupon_findByName() {
		// given
		String searchKeyword = "테스트";
		PageRequest pageable = PageRequest.of(0, 10);

		// when
		Page<CouponResponse> result = couponQueryRepository.findAllByCoupon(searchKeyword, pageable);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getContent()).hasSize(1);

		CouponResponse response = result.getContent().getFirst();
		assertThat(response.getCouponName()).isEqualTo("테스트쿠폰");
	}

	@Test
	@DisplayName("쿠폰 조회")
	void findAllByCoupon() {
		Page<CouponResponse> result = couponQueryRepository.findAllByCoupon(null, PageRequest.of(0, 10));
		assertThat(result.getContent()).hasSize(1);
	}

	@Test
	@DisplayName("쿠폰 정책 전체 조회")
	void findAllByCouponType() {
		// given
		PageRequest pageable = PageRequest.of(0, 10);

		// when
		Page<CouponTypeResponse> result = couponQueryRepository.findAllByCouponType(pageable);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getContent()).hasSize(1);

		CouponTypeResponse response = result.getContent().getFirst();
		assertThat(response.getName()).isEqualTo("도서할인");
		assertThat(response.getDiscountPrice()).isNull();
		assertThat(response.getMaximumDiscountPrice()).isNull();
		assertThat(response.getDiscountPercent()).isNull();
	}

	/* 유저 쿠폰 조회 */
	@Test
	@DisplayName("유저 쿠폰 조회 - 전체 조회")
	void findAllByUserCoupon_all() {
		PageRequest pageable = PageRequest.of(0, 10);
		Page<UserCouponResponse> result = couponQueryRepository.findAllByUserCoupon(user.getId(),
			UserCouponFilterType.ALL, pageable);

		assertThat(result).isNotNull();
		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().get(0).getCouponName()).isEqualTo("테스트쿠폰");
	}

	@Test
	@DisplayName("유저 쿠폰 조회 - 사용 가능")
	void findAllByUserCoupon_usable() {
		PageRequest pageable = PageRequest.of(0, 10);
		Page<UserCouponResponse> result = couponQueryRepository.findAllByUserCoupon(user.getId(),
			UserCouponFilterType.USABLE, pageable);

		assertThat(result).isNotNull();
		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().get(0).getUsedAt()).isNull(); // 사용 안됨
	}

	@Test
	@DisplayName("유저 쿠폰 조회 - 사용 완료")
	void findAllByUserCoupon_used() {
		userCoupon = em.merge(userCoupon);
		userCoupon.useCoupon();
		em.flush();

		PageRequest pageable = PageRequest.of(0, 10);
		Page<UserCouponResponse> result = couponQueryRepository.findAllByUserCoupon(user.getId(),
			UserCouponFilterType.USED, pageable);

		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().get(0).getUsedAt()).isNotNull();
	}

	@Test
	@DisplayName("유저 쿠폰 조회 - 만료")
	void findAllByUserCoupon_expired() {
		userCoupon.cancelCoupon();
		userCoupon = UserCoupon.builder()
			.coupon(coupon)
			.user(user)
			.availableStartAt(LocalDateTime.now().minusDays(5))
			.availableEndAt(LocalDateTime.now().minusDays(1))
			.usedAt(null)
			.build();
		em.persist(userCoupon);
		em.flush();

		PageRequest pageable = PageRequest.of(0, 10);
		Page<UserCouponResponse> result = couponQueryRepository.findAllByUserCoupon(user.getId(),
			UserCouponFilterType.EXPIRED, pageable);

		assertThat(result).isNotNull();
		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().get(0).getUsedAt()).isNull();
		assertThat(result.getContent().get(0).getAvailableEndAt()).isBefore(LocalDateTime.now());
	}

}
