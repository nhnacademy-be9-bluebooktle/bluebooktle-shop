package shop.bluebooktle.backend.coupon.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.Category;
import shop.bluebooktle.backend.book.repository.BookRepository;
import shop.bluebooktle.backend.book.repository.CategoryRepository;
import shop.bluebooktle.backend.coupon.entity.BookCoupon;
import shop.bluebooktle.backend.coupon.entity.Coupon;
import shop.bluebooktle.backend.coupon.entity.CouponType;
import shop.bluebooktle.backend.coupon.repository.BookCouponRepository;
import shop.bluebooktle.backend.coupon.repository.CategoryCouponRepository;
import shop.bluebooktle.backend.coupon.repository.CouponRepository;
import shop.bluebooktle.backend.coupon.repository.CouponTypeRepository;
import shop.bluebooktle.backend.coupon.service.impl.CouponServiceImpl;
import shop.bluebooktle.common.domain.coupon.CouponTypeTarget;
import shop.bluebooktle.common.dto.coupon.request.CouponRegisterRequest;
import shop.bluebooktle.common.dto.coupon.response.CouponResponse;
import shop.bluebooktle.common.exception.coupon.CouponTypeNotFoundException;
import shop.bluebooktle.common.exception.coupon.InvalidCouponTargetException;

@ExtendWith(MockitoExtension.class)
@Slf4j
class CouponServiceTest {

	@InjectMocks
	private CouponServiceImpl couponService;

	@Mock
	private CouponRepository couponRepository;
	@Mock
	private CouponTypeRepository couponTypeRepository;
	@Mock
	private CategoryCouponRepository categoryCouponRepository;
	@Mock
	private BookCouponRepository bookCouponRepository;

	@Mock
	private CategoryRepository categoryRepository;
	@Mock
	private BookRepository bookRepository;

	@Test
	@DisplayName("전체 쿠폰 조회 - 성공")
	void getAllCoupons_success() {
		// given
		String keyword = "할인";
		Pageable pageable = PageRequest.of(0, 10);

		CouponResponse response1 = CouponResponse.builder()
			.id(1L)
			.couponName("할인 쿠폰")
			.build();
		CouponResponse response2 = CouponResponse.builder()
			.id(2L)
			.couponName("무료 배송 쿠폰")
			.build();

		Page<CouponResponse> pageResult = new PageImpl<>(List.of(response1, response2), pageable, 2);
		given(couponRepository.findAllByCoupon(keyword, pageable)).willReturn(pageResult);

		// when
		Page<CouponResponse> result = couponService.getAllCoupons(keyword, pageable);

		// then
		assertThat(result.getTotalElements()).isEqualTo(2);
		assertThat(result.getContent().get(0).getCouponName()).contains("할인");
		verify(couponRepository).findAllByCoupon(keyword, pageable);
	}

	@Test
	@DisplayName("특정 도서 쿠폰 등록 - 성공")
	void registerBookCoupon_success() {
		//given
		CouponType couponType = CouponType.builder()
			.target(CouponTypeTarget.BOOK)
			.build();
		Book book = Book.builder()
			.build();

		CouponRegisterRequest request = CouponRegisterRequest.builder()
			.couponTypeId(1L)
			.name("도서 쿠폰")
			.bookId(1L)
			.build();

		given(couponTypeRepository.findById(1L)).willReturn(Optional.of(couponType));
		given(bookRepository.findById(1L)).willReturn(Optional.of(book));
		given(couponRepository.save(any())).willAnswer(i -> i.getArguments()[0]);

		//when
		couponService.registerCoupon(request);

		//then
		verify(couponTypeRepository).findById(1L); // 쿠폰 타입 조회 확인
		verify(couponRepository).save(any(Coupon.class)); // 쿠폰 저장 확인
		verify(bookCouponRepository).save(any(BookCoupon.class)); // bookCoupon 저장 확인
	}

	@Test
	@DisplayName("특정 카테고리 쿠폰 등록 - 성공")
	void registerCategoryCoupon_success() {
		//given
		CouponType couponType = CouponType.builder()
			.target(CouponTypeTarget.BOOK)
			.build();
		Category parentCategory = Category.builder()
			.name("IT")
			.build();
		Category childCategory = Category.builder()
			.parentCategory(parentCategory)
			.name("java 기초")
			.build();
		ReflectionTestUtils.setField(childCategory, "id", 2L);

		CouponRegisterRequest request = CouponRegisterRequest.builder()
			.couponTypeId(1L)
			.name("java 카테고리 쿠폰")
			.categoryId(2L)
			.build();
		given(couponTypeRepository.findById(1L)).willReturn(Optional.of(couponType));
		given(categoryRepository.findById(2L)).willReturn(Optional.of(childCategory));
		given(couponRepository.save(any())).willAnswer(i -> i.getArguments()[0]);

		//when
		couponService.registerCoupon(request);

		//then
		verify(couponRepository).save(any(Coupon.class));
		verify(categoryCouponRepository).save(any());
	}

	@Test
	@DisplayName("도서, 카테고리 같이 들어올 경우 - 실패")
	void registerBookAndCategoryCoupon_fail() {
		//given
		CouponType couponType = CouponType.builder()
			.target(CouponTypeTarget.BOOK)
			.build();

		CouponRegisterRequest request = CouponRegisterRequest.builder()
			.couponTypeId(1L)
			.name("잘못된 쿠폰")
			.bookId(1L)
			.categoryId(2L)
			.build();

		given(couponTypeRepository.findById(1L)).willReturn(Optional.of(couponType));

		assertThrows(InvalidCouponTargetException.class, () -> couponService.registerCoupon(request));

		verify(couponRepository, never()).save(any(Coupon.class));
	}

	@Test
	@DisplayName("주문 쿠폰 등록 - 성공")
	void registerOrderCoupon_success() {
		CouponType couponType = CouponType.builder()
			.target(CouponTypeTarget.ORDER)
			.build();

		CouponRegisterRequest request = CouponRegisterRequest.builder()
			.couponTypeId(1L)
			.name("20000원 이상 구매시 1000원 할인")
			.build();

		given(couponTypeRepository.findById(1L)).willReturn(Optional.of(couponType));
		given(couponRepository.save(any())).willAnswer(i -> i.getArguments()[0]);

		couponService.registerCoupon(request);

		//then
		verify(couponRepository).save(any(Coupon.class));
		verify(bookCouponRepository, never()).save(any());
		verify(categoryCouponRepository, never()).save(any());

	}

	@Test
	@DisplayName("존재하지 않는 couponTypeId로 등록 시 - 실패")
	void registerCoupon_couponTypeNotFound_fail() {
		// given
		CouponRegisterRequest request = CouponRegisterRequest.builder()
			.couponTypeId(123L)
			.name("test")
			.bookId(null)
			.categoryId(null)
			.build();

		given(couponTypeRepository.findById(123L)).willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> couponService.registerCoupon(request))
			.isInstanceOf(CouponTypeNotFoundException.class);
	}

	@Test
	@DisplayName("도서 쿠폰인데 bookId, categoryId 모두 누락 - 실패")
	void registerCoupon_bookTargetWithoutBookOrCategory_fail() {
		CouponType couponType = CouponType.builder()
			.target(CouponTypeTarget.BOOK)
			.build();
		CouponRegisterRequest request = CouponRegisterRequest.builder()
			.couponTypeId(1L)
			.name("'IT 특정' 도서쿠폰")
			.bookId(null)
			.categoryId(null)
			.build();

		given(couponTypeRepository.findById(1L)).willReturn(Optional.of(couponType));

		assertThatThrownBy(() -> couponService.registerCoupon(request))
			.isInstanceOf(InvalidCouponTargetException.class);
	}

	@DisplayName("주문 쿠폰인데 bookId가 들어옴 - 실패")
	@Test
	void registerOrderCoupon_withBookId_shouldFail() {
		CouponType couponType = CouponType.builder().build();
		ReflectionTestUtils.setField(couponType, "target", CouponTypeTarget.ORDER);

		CouponRegisterRequest request = CouponRegisterRequest.builder()
			.couponTypeId(1L)
			.name("주문쿠폰 오류")
			.bookId(1L)
			.build();

		given(couponTypeRepository.findById(1L)).willReturn(Optional.of(couponType));

		assertThatThrownBy(() -> couponService.registerCoupon(request))
			.isInstanceOf(InvalidCouponTargetException.class);
	}

	@DisplayName("주문 쿠폰인데 categoryId가 들어옴 - 실패")
	@Test
	void registerOrderCoupon_withCategoryId_shouldFail() {
		CouponType couponType = CouponType.builder().build();
		ReflectionTestUtils.setField(couponType, "target", CouponTypeTarget.ORDER);

		CouponRegisterRequest request = CouponRegisterRequest.builder()
			.couponTypeId(1L)
			.name("주문쿠폰 오류")
			.categoryId(2L)
			.build();

		given(couponTypeRepository.findById(1L)).willReturn(Optional.of(couponType));

		assertThatThrownBy(() -> couponService.registerCoupon(request))
			.isInstanceOf(InvalidCouponTargetException.class);
	}

}
