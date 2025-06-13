package shop.bluebooktle.backend.coupon.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.BDDMockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import shop.bluebooktle.backend.book_order.entity.BookOrder;
import shop.bluebooktle.backend.book_order.entity.UserCouponBookOrder;
import shop.bluebooktle.backend.coupon.entity.AbsoluteCoupon;
import shop.bluebooktle.backend.coupon.entity.Coupon;
import shop.bluebooktle.backend.coupon.entity.CouponType;
import shop.bluebooktle.backend.coupon.entity.RelativeCoupon;
import shop.bluebooktle.backend.coupon.entity.UserCoupon;
import shop.bluebooktle.backend.coupon.repository.AbsoluteCouponRepository;
import shop.bluebooktle.backend.coupon.repository.RelativeCouponRepository;
import shop.bluebooktle.backend.coupon.service.impl.CouponCalculationServiceImpl;
import shop.bluebooktle.backend.order.entity.DeliveryRule;
import shop.bluebooktle.backend.order.entity.Order;
import shop.bluebooktle.backend.order.entity.OrderState;
import shop.bluebooktle.common.domain.coupon.CouponTypeTarget;
import shop.bluebooktle.common.dto.coupon.CalculatedDiscountDetails;
import shop.bluebooktle.common.entity.auth.User;

@ExtendWith(MockitoExtension.class)
class CouponCalculationServiceTest {

	@InjectMocks
	private CouponCalculationServiceImpl service;

	@Mock
	private AbsoluteCouponRepository absoluteCouponRepository;

	@Mock
	private RelativeCouponRepository relativeCouponRepository;

	@Test
	@DisplayName("쿠폰 정보 없음 - 할인 0원")
	void null_coupon_return_zero() {
		UserCouponBookOrder input = UserCouponBookOrder.builder().userCoupon(null).build();
		CalculatedDiscountDetails result = service.calculateDiscountDetails(input);
		assertThat(result.getAppliedDiscountAmount()).isEqualTo(BigDecimal.ZERO);
	}

	@Test
	@DisplayName("쿠폰 엔티티 내부 null - 할인 0원")
	void coupon_null_inside_userCoupon() {
		UserCoupon userCoupon = UserCoupon.builder().coupon(null).build();
		UserCouponBookOrder input = UserCouponBookOrder.builder().userCoupon(userCoupon).build();
		CalculatedDiscountDetails result = service.calculateDiscountDetails(input);
		assertThat(result.getAppliedDiscountAmount()).isEqualTo(BigDecimal.ZERO);
	}

	@Test
	@DisplayName("BookOrder null - 할인 0원")
	void bookOrder_null_should_return_zero() {
		CouponType type = CouponType.builder()
			.target(CouponTypeTarget.BOOK)
			.minimumPayment(BigDecimal.ZERO)
			.build();
		Coupon coupon = Coupon.builder().type(type).build();
		UserCoupon userCoupon = UserCoupon.builder().coupon(coupon).build();
		UserCouponBookOrder input = UserCouponBookOrder.builder().userCoupon(userCoupon).bookOrder(null).build();

		CalculatedDiscountDetails result = service.calculateDiscountDetails(input);
		assertThat(result.getAppliedDiscountAmount()).isEqualTo(BigDecimal.ZERO);
	}

	@Test
	@DisplayName("Order null 또는 bookOrder 비어있음 - 할인 0원")
	void order_null_or_empty_bookOrder() {
		// given
		CouponType type = CouponType.builder()
			.target(CouponTypeTarget.ORDER)
			.minimumPayment(BigDecimal.ZERO)
			.build();

		Coupon coupon = Coupon.builder()
			.type(type)
			.build();

		UserCoupon userCoupon = UserCoupon.builder()
			.coupon(coupon)
			.build();

		// case 1: null order
		UserCouponBookOrder input1 = UserCouponBookOrder.builder()
			.userCoupon(userCoupon)
			.order(null)
			.build();

		// when
		CalculatedDiscountDetails result1 = service.calculateDiscountDetails(input1);

		// then
		assertThat(result1.getAppliedDiscountAmount()).isEqualTo(BigDecimal.ZERO);

		// case 2: empty bookOrders
		Order order = Order.builder()
			.orderState(mock(OrderState.class)) // 필수값 세팅
			.deliveryRule(mock(DeliveryRule.class)) // 필수값 세팅
			.user(mock(User.class)) // 필수값 세팅
			.orderName("test")
			.requestedDeliveryDate(LocalDateTime.now())
			.shippedAt(LocalDateTime.now())
			.deliveryFee(BigDecimal.ZERO)
			.ordererName("test")
			.ordererPhoneNumber("010-0000-0000")
			.receiverName("test")
			.receiverPhoneNumber("010-0000-0000")
			.address("addr")
			.detailAddress("detail")
			.postalCode("12345")
			.trackingNumber("track")
			.orderKey("key")
			.ordererEmail("a@a.com")
			.receiverEmail("b@b.com")
			.couponDiscountAmount(BigDecimal.ZERO)
			.pointUseAmount(BigDecimal.ZERO)
			.originalAmount(BigDecimal.ZERO)
			.saleDiscountAmount(BigDecimal.ZERO)
			.build();

		// bookOrders 비워둠 (기본값: new ArrayList<>() 초기화됨)
		UserCouponBookOrder input2 = UserCouponBookOrder.builder()
			.userCoupon(userCoupon)
			.order(order)
			.build();

		// when
		CalculatedDiscountDetails result2 = service.calculateDiscountDetails(input2);

		// then
		assertThat(result2.getAppliedDiscountAmount()).isEqualTo(BigDecimal.ZERO);
	}

	@Test
	@DisplayName("최소 결제 금액 미달 - 할인 0원")
	void below_minimum_payment() {
		BookOrder bookOrder = BookOrder.builder()
			.price(BigDecimal.valueOf(1000))
			.quantity(1)
			.build();
		CouponType type = CouponType.builder()
			.target(CouponTypeTarget.BOOK)
			.minimumPayment(BigDecimal.valueOf(5000))
			.build();
		Coupon coupon = Coupon.builder().type(type).build();
		UserCoupon userCoupon = UserCoupon.builder().coupon(coupon).build();

		UserCouponBookOrder input = UserCouponBookOrder.builder()
			.userCoupon(userCoupon)
			.bookOrder(bookOrder)
			.build();

		CalculatedDiscountDetails result = service.calculateDiscountDetails(input);
		assertThat(result.getAppliedDiscountAmount()).isEqualTo(BigDecimal.ZERO);
	}

	@Test
	@DisplayName("정액 할인 쿠폰 적용")
	void absolute_coupon_applied() {
		BookOrder bookOrder = BookOrder.builder()
			.price(BigDecimal.valueOf(10000))
			.quantity(1)
			.build();
		CouponType type = CouponType.builder()
			.target(CouponTypeTarget.BOOK)
			.minimumPayment(BigDecimal.valueOf(5000))
			.build();
		Coupon coupon = Coupon.builder().type(type).build();
		UserCoupon userCoupon = UserCoupon.builder().coupon(coupon).build();

		given(absoluteCouponRepository.findByCouponTypeId(null)).willReturn(Optional.of(
			AbsoluteCoupon.builder().discountPrice(BigDecimal.valueOf(1000)).build()
		));

		UserCouponBookOrder input = UserCouponBookOrder.builder()
			.userCoupon(userCoupon)
			.bookOrder(bookOrder)
			.build();

		CalculatedDiscountDetails result = service.calculateDiscountDetails(input);
		assertThat(result.getAppliedDiscountAmount()).isEqualTo(BigDecimal.valueOf(1000));
	}

	@Test
	@DisplayName("정률 할인 쿠폰 최대 금액 초과 시 최대금액 적용")
	void relative_coupon_with_max_discount_applied() {
		BookOrder bookOrder = BookOrder.builder()
			.price(BigDecimal.valueOf(10000))
			.quantity(1)
			.build();
		CouponType type = CouponType.builder()
			.target(CouponTypeTarget.BOOK)
			.minimumPayment(BigDecimal.valueOf(5000))
			.build();
		Coupon coupon = Coupon.builder().type(type).build();
		UserCoupon userCoupon = UserCoupon.builder().coupon(coupon).build();

		given(absoluteCouponRepository.findByCouponTypeId(null)).willReturn(Optional.empty());
		given(relativeCouponRepository.findByCouponTypeId(null)).willReturn(Optional.of(
			RelativeCoupon.builder()
				.discountPercent(30)
				.maximumDiscountPrice(BigDecimal.valueOf(2000))
				.build()
		));

		UserCouponBookOrder input = UserCouponBookOrder.builder()
			.userCoupon(userCoupon)
			.bookOrder(bookOrder)
			.build();

		CalculatedDiscountDetails result = service.calculateDiscountDetails(input);
		assertThat(result.getAppliedDiscountAmount()).isEqualTo(BigDecimal.valueOf(2000));
	}

	@Test
	@DisplayName("쿠폰 정책 없음 - 할인 0원")
	void no_coupon_policy() {
		BookOrder bookOrder = BookOrder.builder()
			.price(BigDecimal.valueOf(10000))
			.quantity(1)
			.build();
		CouponType type = CouponType.builder()
			.target(CouponTypeTarget.BOOK)
			.minimumPayment(BigDecimal.valueOf(5000))
			.build();
		Coupon coupon = Coupon.builder().type(type).build();
		UserCoupon userCoupon = UserCoupon.builder().coupon(coupon).build();

		given(absoluteCouponRepository.findByCouponTypeId(null)).willReturn(Optional.empty());
		given(relativeCouponRepository.findByCouponTypeId(null)).willReturn(Optional.empty());

		UserCouponBookOrder input = UserCouponBookOrder.builder()
			.userCoupon(userCoupon)
			.bookOrder(bookOrder)
			.build();

		CalculatedDiscountDetails result = service.calculateDiscountDetails(input);
		assertThat(result.getAppliedDiscountAmount()).isEqualTo(BigDecimal.ZERO);
	}

	@Test
	@DisplayName("ORDER 쿠폰 타입 - 주문의 총 가격 합계 계산")
	void order_coupon_target_baseAmountForDiscount_should_be_calculated() {
		CouponType type = CouponType.builder()
			.target(CouponTypeTarget.ORDER)
			.minimumPayment(BigDecimal.ZERO)
			.build();

		// ID 강제 설정
		ReflectionTestUtils.setField(type, "id", 1L);

		Coupon coupon = Coupon.builder()
			.type(type)
			.build();

		UserCoupon userCoupon = UserCoupon.builder()
			.coupon(coupon)
			.build();

		BookOrder book1 = BookOrder.builder()
			.price(BigDecimal.valueOf(1000))
			.quantity(2)
			.build(); // 2000

		BookOrder book2 = BookOrder.builder()
			.price(BigDecimal.valueOf(1500))
			.quantity(1)
			.build(); // 1500

		Order order = Order.builder()
			.orderState(mock(OrderState.class))
			.deliveryRule(mock(DeliveryRule.class))
			.user(mock(User.class))
			.orderName("테스트 주문")
			.requestedDeliveryDate(LocalDateTime.now())
			.shippedAt(LocalDateTime.now())
			.deliveryFee(BigDecimal.ZERO)
			.ordererName("홍길동")
			.ordererPhoneNumber("010-0000-0000")
			.receiverName("수신자")
			.receiverPhoneNumber("010-0000-0000")
			.address("서울시 강남구")
			.detailAddress("101호")
			.postalCode("12345")
			.trackingNumber("123456789")
			.orderKey("key")
			.ordererEmail("test@example.com")
			.receiverEmail("receiver@example.com")
			.couponDiscountAmount(BigDecimal.ZERO)
			.pointUseAmount(BigDecimal.ZERO)
			.originalAmount(BigDecimal.ZERO)
			.saleDiscountAmount(BigDecimal.ZERO)
			.build();

		order.getBookOrders().add(book1);
		order.getBookOrders().add(book2); // 총 3500원

		UserCouponBookOrder input = UserCouponBookOrder.builder()
			.userCoupon(userCoupon)
			.order(order)
			.build();

		given(absoluteCouponRepository.findByCouponTypeId(1L))
			.willReturn(Optional.of(
				AbsoluteCoupon.builder()
					.discountPrice(BigDecimal.valueOf(1000))
					.build()
			));

		// when
		CalculatedDiscountDetails result = service.calculateDiscountDetails(input);

		// then
		assertThat(result.getAppliedDiscountAmount()).isEqualTo(BigDecimal.valueOf(1000));
	}

	@Test
	@DisplayName("정액 할인 > 총 결제 금액인 경우 - 총 금액으로 조정됨")
	void absolute_coupon_discount_exceeds_total_should_be_capped() {
		// given
		BookOrder bookOrder = BookOrder.builder()
			.price(BigDecimal.valueOf(1000))
			.quantity(1)
			.build();

		CouponType type = CouponType.builder()
			.target(CouponTypeTarget.BOOK)
			.minimumPayment(BigDecimal.ZERO)
			.build();
		Coupon coupon = Coupon.builder().type(type).build();
		UserCoupon userCoupon = UserCoupon.builder().coupon(coupon).build();

		given(absoluteCouponRepository.findByCouponTypeId(null)).willReturn(Optional.of(
			AbsoluteCoupon.builder().discountPrice(BigDecimal.valueOf(2000)).build()
		));

		UserCouponBookOrder input = UserCouponBookOrder.builder()
			.userCoupon(userCoupon)
			.bookOrder(bookOrder)
			.build();

		// when
		CalculatedDiscountDetails result = service.calculateDiscountDetails(input);

		// then
		assertThat(result.getAppliedDiscountAmount()).isEqualTo(BigDecimal.valueOf(1000));
	}

	@Test
	@DisplayName("계산된 할인 금액이 음수일 경우 0으로 조정")
	void negative_discount_should_be_zero() {
		// given
		BookOrder bookOrder = BookOrder.builder()
			.price(BigDecimal.valueOf(1000))
			.quantity(1)
			.build();

		CouponType type = CouponType.builder()
			.target(CouponTypeTarget.BOOK)
			.minimumPayment(BigDecimal.ZERO)
			.build();
		Coupon coupon = Coupon.builder().type(type).build();
		UserCoupon userCoupon = UserCoupon.builder().coupon(coupon).build();

		given(absoluteCouponRepository.findByCouponTypeId(null)).willReturn(Optional.of(
			AbsoluteCoupon.builder().discountPrice(BigDecimal.valueOf(-500)).build()
		));

		UserCouponBookOrder input = UserCouponBookOrder.builder()
			.userCoupon(userCoupon)
			.bookOrder(bookOrder)
			.build();

		// when
		CalculatedDiscountDetails result = service.calculateDiscountDetails(input);

		// then
		assertThat(result.getAppliedDiscountAmount()).isEqualTo(BigDecimal.ZERO);
	}

}
