package shop.bluebooktle.backend.coupon.jpa;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import jakarta.persistence.EntityManager;
import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.BookCategory;
import shop.bluebooktle.backend.book.entity.BookSaleInfo;
import shop.bluebooktle.backend.book.entity.Category;
import shop.bluebooktle.backend.config.JpaAuditingConfiguration;
import shop.bluebooktle.backend.config.QueryDslConfig;
import shop.bluebooktle.backend.coupon.entity.BookCoupon;
import shop.bluebooktle.backend.coupon.entity.CategoryCoupon;
import shop.bluebooktle.backend.coupon.entity.Coupon;
import shop.bluebooktle.backend.coupon.entity.CouponType;
import shop.bluebooktle.backend.coupon.entity.UserCoupon;
import shop.bluebooktle.backend.coupon.repository.impl.UserCouponQueryRepositoryImpl;
import shop.bluebooktle.common.converter.ProfileAwareStringCryptoConverter;
import shop.bluebooktle.common.domain.auth.UserProvider;
import shop.bluebooktle.common.domain.auth.UserStatus;
import shop.bluebooktle.common.domain.auth.UserType;
import shop.bluebooktle.common.domain.coupon.CouponTypeTarget;
import shop.bluebooktle.common.dto.coupon.response.UsableUserCouponMapResponse;
import shop.bluebooktle.common.entity.auth.MembershipLevel;
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.util.CryptoUtils;

@DataJpaTest
@ActiveProfiles("test")
@Import({QueryDslConfig.class,
	JpaAuditingConfiguration.class,
	CryptoUtils.class, ProfileAwareStringCryptoConverter.class,
})
public class UserCouponQueryRepositoryTest {

	@Autowired
	private UserCouponQueryRepositoryImpl userCouponQueryRepository;

	@Autowired
	private EntityManager em;

	private User user;
	private Coupon coupon;

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

		em.flush();
		em.clear();
	}

	@Test
	@DisplayName("사용 가능한 쿠폰 수 조회")
	void testCouponAllUsableCoupons() {
		Long count = userCouponQueryRepository.couponAllUsableCoupons(user.getId());
		assertThat(count).isEqualTo(1);
	}

	@Test
	@DisplayName("이번 달 내 만료 예정 쿠폰 수 조회")
	void testCouponExpiringThisMonth() {
		Long count = userCouponQueryRepository.couponExpiringThisMonth(user.getId());
		assertThat(count).isEqualTo(1);
	}

	@Test
	@DisplayName("사용 가능한 쿠폰 목록 - 도서 쿠폰 및 주문 쿠폰 포함")
	void testFindAllByUsableUserCouponForOrder() {
		// given
		Book book = Book.builder()
			.title("도서 제목")
			.isbn("1234567890123")
			.description("설명")
			.index("목차")
			.publishDate(LocalDateTime.now().minusDays(30))
			.build();
		em.persist(book);

		BookSaleInfo saleInfo = BookSaleInfo.builder()
			.book(book)
			.price(BigDecimal.valueOf(15000))
			.salePrice(BigDecimal.valueOf(12000))
			.stock(100)
			.isPackable(true)
			.salePercentage(BigDecimal.valueOf(20))
			.build();
		em.persist(saleInfo);

		Coupon managedCoupon = em.find(Coupon.class, coupon.getId());
		BookCoupon bookCoupon = BookCoupon.builder()
			.book(book)
			.coupon(managedCoupon)
			.build();
		em.persist(bookCoupon);

		CouponType orderCouponType = CouponType.builder()
			.name("주문할인")
			.target(CouponTypeTarget.ORDER)
			.minimumPayment(BigDecimal.valueOf(10000))
			.build();
		em.persist(orderCouponType);

		Coupon orderCoupon = Coupon.builder()
			.type(orderCouponType)
			.couponName("주문쿠폰")
			.build();
		em.persist(orderCoupon);

		UserCoupon userOrderCoupon = UserCoupon.builder()
			.coupon(orderCoupon)
			.user(user)
			.availableStartAt(LocalDateTime.now().minusDays(1))
			.availableEndAt(LocalDateTime.now().plusDays(1))
			.build();
		em.persist(userOrderCoupon);

		em.flush();
		em.clear();

		// when
		UsableUserCouponMapResponse result = userCouponQueryRepository
			.findAllByUsableUserCouponForOrder(user.getId(), List.of(book.getId()));

		// then
		assertThat(result.getUsableUserCouponMap()).isNotEmpty();
		assertThat(result.getUsableUserCouponMap().get(book.getId())).isNotEmpty();
		assertThat(result.getUsableUserCouponMap().get(-1L)).isNotEmpty();
	}

	@Test
	@DisplayName("사용 가능한 쿠폰 목록 - 카테고리 쿠폰 포함")
	void testFindAllByUsableUserCouponForOrder_categoryCoupon() {
		// given
		Book book = Book.builder()
			.title("카테고리 테스트 도서")
			.isbn("9876543210987")
			.description("설명")
			.index("목차")
			.publishDate(LocalDateTime.now().minusDays(10))
			.build();
		em.persist(book);

		Category category = Category.builder()
			.name("문학")
			.build();
		category.setCategoryPath("문학/한국문학");
		em.persist(category);

		BookCategory bookCategory = BookCategory.builder()
			.book(book)
			.category(category)
			.build();
		em.persist(bookCategory);

		CouponType categoryCouponType = CouponType.builder()
			.name("카테고리 할인")
			.target(CouponTypeTarget.BOOK)
			.minimumPayment(BigDecimal.valueOf(10000))
			.build();
		em.persist(categoryCouponType);

		Coupon categoryCoupon = Coupon.builder()
			.type(categoryCouponType)
			.couponName("카테고리쿠폰")
			.build();
		em.persist(categoryCoupon);

		UserCoupon userCategoryCoupon = UserCoupon.builder()
			.coupon(categoryCoupon)
			.user(user)
			.availableStartAt(LocalDateTime.now().minusDays(1))
			.availableEndAt(LocalDateTime.now().plusDays(1))
			.build();
		em.persist(userCategoryCoupon);

		CategoryCoupon categoryCouponMap = CategoryCoupon.builder()
			.coupon(categoryCoupon)
			.category(category)
			.build();
		em.persist(categoryCouponMap);

		em.flush();
		em.clear();

		// when
		UsableUserCouponMapResponse result = userCouponQueryRepository
			.findAllByUsableUserCouponForOrder(user.getId(), List.of(book.getId()));

		// then
		assertThat(result.getUsableUserCouponMap()).isNotEmpty();
		assertThat(result.getUsableUserCouponMap().get(book.getId())).isNotEmpty();
		assertThat(result.getUsableUserCouponMap().get(book.getId()))
			.anyMatch(coupon -> "카테고리쿠폰".equals(coupon.getCouponName()));
	}

}
