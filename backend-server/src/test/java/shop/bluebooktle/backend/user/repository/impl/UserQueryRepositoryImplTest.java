package shop.bluebooktle.backend.user.repository.impl;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book_order.entity.BookOrder;
import shop.bluebooktle.backend.book_order.entity.OrderPackaging;
import shop.bluebooktle.backend.book_order.entity.PackagingOption;
import shop.bluebooktle.backend.config.JpaAuditingConfiguration;
import shop.bluebooktle.backend.config.QueryDslConfig;
import shop.bluebooktle.backend.order.entity.DeliveryRule;
import shop.bluebooktle.backend.order.entity.Order;
import shop.bluebooktle.backend.order.entity.OrderState;
import shop.bluebooktle.backend.payment.entity.Payment;
import shop.bluebooktle.backend.payment.entity.PaymentDetail;
import shop.bluebooktle.backend.payment.entity.PaymentType;
import shop.bluebooktle.backend.user.dto.UserNetSpentAmountDto;
import shop.bluebooktle.common.converter.ProfileAwareStringCryptoConverter;
import shop.bluebooktle.common.domain.auth.UserProvider;
import shop.bluebooktle.common.domain.auth.UserStatus;
import shop.bluebooktle.common.domain.auth.UserType;
import shop.bluebooktle.common.domain.order.OrderStatus;
import shop.bluebooktle.common.domain.order.Region;
import shop.bluebooktle.common.dto.user.request.UserSearchRequest;
import shop.bluebooktle.common.dto.user.response.UserMembershipLevelResponse;
import shop.bluebooktle.common.entity.auth.MembershipLevel;
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.util.CryptoUtils;

@DataJpaTest
@ActiveProfiles("test")
@Import({
	QueryDslConfig.class,
	JpaAuditingConfiguration.class,
	CryptoUtils.class,
	ProfileAwareStringCryptoConverter.class
})
class UserQueryRepositoryImplTest {

	private UserQueryRepositoryImpl userQueryRepository;

	@Autowired
	private EntityManager em;

	@Autowired
	private JPAQueryFactory queryFactory;

	private User user1, user2, user3;
	private MembershipLevel general, royal, gold, platinum;
	private OrderState preparingState;
	private DeliveryRule deliveryRule;

	@BeforeEach
	void setUp() {
		userQueryRepository = new UserQueryRepositoryImpl(queryFactory);

		preparingState = new OrderState(OrderStatus.PREPARING);
		em.persist(preparingState);

		deliveryRule = DeliveryRule.builder()
			.ruleName("테스트 배송 정책").deliveryFee(new BigDecimal("3000"))
			.region(Region.ALL).isActive(true).build();
		em.persist(deliveryRule);

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

		user1 = User.builder()
			.loginId("user1")
			.name("김일반")
			.nickname("general")
			.email("user1@test.com")
			.birth("19940610")
			.type(UserType.USER)
			.status(UserStatus.ACTIVE)
			.membershipLevel(general)
			.phoneNumber("010-1111-1111")
			.encodedPassword("password!@#$%^")
			.pointBalance(BigDecimal.ZERO)
			.provider(UserProvider.BLUEBOOKTLE)
			.build();
		user2 = User.builder()
			.loginId("user2")
			.name("이휴면")
			.nickname("dormant")
			.email("user2@test.com")
			.birth("19950711")
			.type(UserType.USER)
			.status(UserStatus.DORMANT)
			.membershipLevel(general)
			.phoneNumber("010-2222-2222")
			.encodedPassword("password!@#$%^")
			.pointBalance(BigDecimal.ZERO)
			.provider(UserProvider.BLUEBOOKTLE)
			.build();
		user3 = User.builder()
			.loginId("admin1")
			.name("박관리")
			.nickname("admin")
			.email("admin@test.com")
			.birth("19960612")
			.type(UserType.ADMIN)
			.status(UserStatus.ACTIVE)
			.membershipLevel(general)
			.phoneNumber("010-3333-3333")
			.encodedPassword("password!@#$%^")
			.pointBalance(BigDecimal.ZERO)
			.provider(UserProvider.BLUEBOOKTLE)
			.build();
		em.persist(user1);
		em.persist(user2);
		em.persist(user3);

		em.flush();
		em.clear();
	}

	@Test
	@DisplayName("사용자 검색 성공 - 생일 월로 조회")
	void findByBirthdayMonth_success() {
		User activeJuneUser = em.find(User.class, user1.getId());
		User adminJuneUser = em.find(User.class, user3.getId());

		List<User> users = userQueryRepository.findByBirthdayMonth("06");

		assertThat(users).hasSize(2)
			.extracting(User::getId)
			.containsExactlyInAnyOrder(activeJuneUser.getId(), adminJuneUser.getId());
	}

	@Test
	@DisplayName("사용자 검색 성공 - 생일 월에 해당하는 사용자가 없을 때")
	void findByBirthdayMonth_noMatchingUser_returnsEmptyList() {
		List<User> users = userQueryRepository.findByBirthdayMonth("12");
		assertThat(users).isEmpty();
	}

	@Test
	@DisplayName("사용자 검색 성공 - 로그인 아이디로 검색")
	void findUsersBySearchRequest_byLoginId_success() {
		UserSearchRequest request = new UserSearchRequest();
		request.setSearchField("loginId");
		request.setSearchKeyword("user");

		Page<User> result = userQueryRepository.findUsersBySearchRequest(request, PageRequest.of(0, 10));

		assertThat(result.getTotalElements()).isEqualTo(2);
		assertThat(result.getContent()).extracting(User::getLoginId).containsExactlyInAnyOrder("user1", "user2");
	}

	@Test
	@DisplayName("사용자 검색 성공 - 닉네임으로 검색")
	void findUsersBySearchRequest_byNickname_success() {
		UserSearchRequest request = new UserSearchRequest();
		request.setSearchField("nickname");
		request.setSearchKeyword("general");

		Page<User> result = userQueryRepository.findUsersBySearchRequest(request, PageRequest.of(0, 10));

		assertThat(result.getTotalElements()).isEqualTo(1);
		assertThat(result.getContent().get(0).getNickname()).isEqualTo("general");
	}

	@Test
	@DisplayName("사용자 검색 성공 - 이메일로 검색")
	void findUsersBySearchRequest_byEmail_success() {
		UserSearchRequest request = new UserSearchRequest();
		request.setSearchField("email");
		request.setSearchKeyword("admin@test.com");

		Page<User> result = userQueryRepository.findUsersBySearchRequest(request, PageRequest.of(0, 10));

		assertThat(result.getTotalElements()).isEqualTo(1);
		assertThat(result.getContent().get(0).getEmail()).isEqualTo("admin@test.com");
	}

	@Test
	@DisplayName("사용자 검색 성공 - 휴면(DORMANT) 상태로 필터링")
	void findUsersBySearchRequest_filterByDormantStatus_success() {
		UserSearchRequest request = new UserSearchRequest();
		request.setUserStatusFilter("DORMANT");

		Page<User> result = userQueryRepository.findUsersBySearchRequest(request, PageRequest.of(0, 10));

		assertThat(result.getTotalElements()).isEqualTo(1);
		assertThat(result.getContent().get(0).getStatus()).isEqualTo(UserStatus.DORMANT);
	}

	@Test
	@DisplayName("사용자 검색 성공 - 검색 조건이 null이면 전체 조회")
	void findUsersBySearchRequest_success_whenSearchConditionIsNull() {
		UserSearchRequest request = new UserSearchRequest();
		PageRequest pageable = PageRequest.of(0, 10);

		Page<User> result = userQueryRepository.findUsersBySearchRequest(request, pageable);

		assertThat(result.getTotalElements()).isEqualTo(3);
	}

	@Test
	@DisplayName("사용자 검색 성공 - 검색어(keyword)만 null일 경우 무시")
	void findUsersBySearchRequest_success_whenKeywordIsNull() {
		UserSearchRequest request = new UserSearchRequest();
		request.setSearchField("name");
		request.setSearchKeyword(null);

		Page<User> result = userQueryRepository.findUsersBySearchRequest(request, PageRequest.of(0, 10));

		assertThat(result.getTotalElements()).isEqualTo(3);
	}

	@Test
	@DisplayName("사용자 검색 성공 - 검색 필드(field)만 null일 경우 무시")
	void findUsersBySearchRequest_success_whenFieldIsNull() {
		UserSearchRequest request = new UserSearchRequest();
		request.setSearchField(null);
		request.setSearchKeyword("some-keyword");

		Page<User> result = userQueryRepository.findUsersBySearchRequest(request, PageRequest.of(0, 10));

		assertThat(result.getTotalElements()).isEqualTo(3);
	}

	@Test
	@DisplayName("사용자 검색 성공 - 유효하지 않은 검색 필드는 무시")
	void findUsersBySearchRequest_success_whenSearchFieldIsInvalid() {
		UserSearchRequest request = new UserSearchRequest();
		request.setSearchField("invalidField");
		request.setSearchKeyword("user1");

		Page<User> result = userQueryRepository.findUsersBySearchRequest(request, PageRequest.of(0, 10));

		assertThat(result.getTotalElements()).isEqualTo(3);
	}

	@Test
	@DisplayName("사용자 검색 성공 - 유효하지 않은 UserType 필터는 무시")
	void findUsersBySearchRequest_success_whenUserTypeIsInvalid() {
		UserSearchRequest request = new UserSearchRequest();
		request.setUserTypeFilter("INVALID_TYPE");

		Page<User> result = userQueryRepository.findUsersBySearchRequest(request, PageRequest.of(0, 10));

		assertThat(result.getTotalElements()).isEqualTo(3);
	}

	@Test
	@DisplayName("사용자 검색 성공 - 유효하지 않은 UserStatus 필터는 무시")
	void findUsersBySearchRequest_success_whenUserStatusIsInvalid() {
		UserSearchRequest request = new UserSearchRequest();
		request.setUserStatusFilter("INVALID_STATUS");

		Page<User> result = userQueryRepository.findUsersBySearchRequest(request, PageRequest.of(0, 10));

		assertThat(result.getTotalElements()).isEqualTo(3);
	}

	@Test
	@DisplayName("사용자 검색 성공 - 여러 필터 조건 조합")
	void findUsersBySearchRequest_success_withCombinedFilters() {
		UserSearchRequest request = new UserSearchRequest();
		request.setSearchField("name");
		request.setSearchKeyword("김");
		request.setUserStatusFilter("ACTIVE");

		Page<User> result = userQueryRepository.findUsersBySearchRequest(request, PageRequest.of(0, 10));

		assertThat(result.getTotalElements()).isEqualTo(1);
		assertThat(result.getContent().get(0).getName()).isEqualTo("김일반");
	}

	@Test
	@DisplayName("사용자 검색 성공 - ID 내림차순 정렬")
	void findUsersBySearchRequest_withSortingDescById_success() {
		UserSearchRequest request = new UserSearchRequest();
		PageRequest pageable = PageRequest.of(0, 10, Sort.by("id").descending());

		Page<User> result = userQueryRepository.findUsersBySearchRequest(request, pageable);

		assertThat(result.getContent()).hasSize(3).isSortedAccordingTo((u1, u2) -> u2.getId().compareTo(u1.getId()));
	}

	@Test
	@DisplayName("사용자 검색 성공 - name 오름차순 정렬 (default 케이스)")
	void findUsersBySearchRequest_withSortingAscByName_success() {
		UserSearchRequest request = new UserSearchRequest();
		PageRequest pageable = PageRequest.of(0, 10, Sort.by("name").ascending());

		Page<User> result = userQueryRepository.findUsersBySearchRequest(request, pageable);

		assertThat(result.getContent()).hasSize(3).isSortedAccordingTo((u1, u2) -> u2.getId().compareTo(u1.getId()));
	}

	@Test
	@DisplayName("사용자 멤버십 등급 수정 성공")
	void updateMembershipLevel_success() {
		userQueryRepository.updateMembershipLevel(user1.getId(), royal.getId());
		em.flush();
		em.clear();

		Long updatedMembershipId = userQueryRepository.findMembershipIdById(user1.getId());
		assertEquals(royal.getId(), updatedMembershipId);
	}

	@Test
	@DisplayName("최근 3개월간 모든 유저의 순수 지출액 조회 성공")
	void findUserNetSpentAmountForLastThreeMonths_success() {
		createAndPersistOrderAndPayment(user1, "130000", -1);
		createAndPersistOrderAndPayment(user3, "50000", -2);
		em.flush();
		em.clear();

		List<UserNetSpentAmountDto> result = userQueryRepository.findUserNetSpentAmountForLastThreeMonths();

		assertThat(result).hasSize(3);
		UserNetSpentAmountDto user1Dto = result.stream()
			.filter(dto -> dto.userId().equals(user1.getId()))
			.findFirst()
			.get();
		UserNetSpentAmountDto user2Dto = result.stream()
			.filter(dto -> dto.userId().equals(user2.getId()))
			.findFirst()
			.get();
		UserNetSpentAmountDto user3Dto = result.stream()
			.filter(dto -> dto.userId().equals(user3.getId()))
			.findFirst()
			.get();

		assertThat(user1Dto.netAmount().compareTo(new BigDecimal("130000"))).isZero();
		assertThat(user3Dto.netAmount().compareTo(new BigDecimal("50000"))).isZero();
		assertThat(user2Dto.netAmount().compareTo(BigDecimal.ZERO)).isZero();
	}

	@Test
	@DisplayName("순수 지출액 계산 성공 - 결제 없는 주문은 0원으로 계산")
	void findUserNetSpentAmount_whenOrderExistsButNoPayment_returnsZero() {
		User testUser1 = em.find(User.class, user1.getId());
		Order orderWithoutPayment = createTestOrder(testUser1, "no-payment-key", "100000", -1);
		em.persist(orderWithoutPayment);
		em.flush();
		em.clear();

		UserMembershipLevelResponse response = userQueryRepository.findUserNetSpentAmountForLastThreeMonthsByUserId(
			user1.getId());

		assertThat(response.netAmount().compareTo(BigDecimal.ZERO)).isZero();
	}

	@Test
	@DisplayName("순수 지출액 계산 성공 - 포장비 포함")
	void getNetAmountExpression_success_withPackagingCost() {
		User testUser1 = em.find(User.class, user1.getId());
		Book testBook = createTestBook("테스트 책");
		PackagingOption testPackaging = createTestPackaging("테스트 포장", "1000");

		Order order = createTestOrder(testUser1, "wrapping-order", "50000", -1);
		em.persist(order);

		BookOrder bookOrder = BookOrder.builder()
			.book(testBook)
			.order(order)
			.price(new BigDecimal("50000"))
			.quantity(1)
			.build();
		em.persist(bookOrder);

		OrderPackaging orderPackaging = OrderPackaging.builder()
			.bookOrder(bookOrder)
			.packagingOption(testPackaging)
			.quantity(2)
			.build();
		em.persist(orderPackaging);

		createAndPersistPaymentForOrder(order, "50000");
		em.flush();
		em.clear();

		UserMembershipLevelResponse response = userQueryRepository.findUserNetSpentAmountForLastThreeMonthsByUserId(
			user1.getId());

		assertThat(response.netAmount().compareTo(new BigDecimal("48000"))).isZero();
	}

	@Test
	@DisplayName("사용자 등급 조회 성공 - 지출액 0원일 때 '일반' 등급 반환")
	void findUserNetSpentAmount_success_whenAmountIsZero() {
		User testUser2 = em.find(User.class, user2.getId());

		UserMembershipLevelResponse response = userQueryRepository.findUserNetSpentAmountForLastThreeMonthsByUserId(
			testUser2.getId());

		assertThat(response.netAmount().compareTo(BigDecimal.ZERO)).isEqualTo(0);
		assertThat(response.membershipLevelName()).isEqualTo("일반");
	}

	@Test
	@DisplayName("사용자 등급 조회 성공 - 일치하는 등급이 없을 때")
	void findUserNetSpentAmount_whenNoMatchingLevel_returnsNoLevelName() {
		em.createQuery("DELETE FROM User").executeUpdate();
		em.createQuery("DELETE FROM MembershipLevel").executeUpdate();

		MembershipLevel newRoyal = MembershipLevel.builder().name("새로운 로얄").rate(2)
			.minNetSpent(BigDecimal.valueOf(100000.00)).maxNetSpent(BigDecimal.valueOf(199999.99)).build();
		em.persist(newRoyal);

		User testUser = User.builder()
			.loginId("testuser_no_level")
			.name("무등급")
			.nickname("no_level")
			.email("no@level.com")
			.birth("20000101")
			.type(UserType.USER)
			.status(UserStatus.ACTIVE)
			.membershipLevel(newRoyal)
			.phoneNumber("010-0000-0000")
			.encodedPassword("password!@#$%^")
			.pointBalance(BigDecimal.ZERO)
			.provider(UserProvider.BLUEBOOKTLE)
			.build();
		em.persist(testUser);
		em.flush();
		em.clear();

		UserMembershipLevelResponse response = userQueryRepository.findUserNetSpentAmountForLastThreeMonthsByUserId(
			testUser.getId());

		assertThat(response.netAmount().compareTo(BigDecimal.ZERO)).isZero();
		assertThat(response.membershipLevelId()).isNull();
		assertThat(response.membershipLevelName()).isEqualTo("등급 없음");
	}

	// ===============================================
	// ============ 테스트 헬퍼 메소드 (Helper Methods) =============
	// ===============================================

	private void createAndPersistOrderAndPayment(User user, String amount, int monthsToSubtract) {
		User managedUser = em.find(User.class, user.getId());
		Order order = createTestOrder(managedUser, "order-key-" + managedUser.getLoginId(), amount, monthsToSubtract);
		em.persist(order);
		createAndPersistPaymentForOrder(order, amount);
	}

	private void createAndPersistPaymentForOrder(Order order, String amount) {
		PaymentType pt = new PaymentType("Toss-" + order.getId());
		em.persist(pt);
		PaymentDetail pd = createTestPaymentDetail(pt);
		em.persist(pd);
		Payment p = Payment.builder().order(order).paidAmount(new BigDecimal(amount)).paymentDetail(pd).build();
		em.persist(p);
	}

	private Book createTestBook(String title) {
		Book book = Book.builder()
			.title(title).isbn(UUID.randomUUID().toString().substring(0, 13))
			.description("설명").index("목차").build();
		em.persist(book);
		return book;
	}

	private PackagingOption createTestPackaging(String name, String price) {
		PackagingOption packagingOption = PackagingOption.builder()
			.name(name).price(new BigDecimal(price)).build();
		em.persist(packagingOption);
		return packagingOption;
	}

	private Order createTestOrder(User user, String orderKey, String amount, int monthsToSubtract) {
		Order order = Order.builder()
			.user(user).originalAmount(new BigDecimal(amount)).deliveryFee(new BigDecimal("3000"))
			.postalCode("12345").address("테스트 주소").detailAddress("101호")
			.orderKey(orderKey).orderName("테스트 주문")
			.ordererName(user.getName()).ordererPhoneNumber(user.getPhoneNumber()).ordererEmail(user.getEmail())
			.receiverName(user.getName()).receiverPhoneNumber(user.getPhoneNumber()).receiverEmail(user.getEmail())
			.orderState(preparingState).deliveryRule(deliveryRule).build();
		ReflectionTestUtils.setField(order, "createdAt", LocalDateTime.now().plusMonths(monthsToSubtract));
		return order;
	}

	private PaymentDetail createTestPaymentDetail(PaymentType paymentType) {
		return PaymentDetail.builder()
			.paymentType(paymentType)
			.requestedAt(LocalDateTime.now())
			.paymentStatus(shop.bluebooktle.common.domain.payment.PaymentStatus.DONE)
			.build();
	}
}