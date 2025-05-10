package shop.bluebooktle.backend.coupon.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.Category;
import shop.bluebooktle.backend.book.repository.BookRepository;
import shop.bluebooktle.backend.book.repository.CategoryRepository;
import shop.bluebooktle.backend.coupon.entity.BookCoupon;
import shop.bluebooktle.backend.coupon.entity.CategoryCoupon;
import shop.bluebooktle.backend.coupon.entity.Coupon;
import shop.bluebooktle.backend.coupon.entity.CouponType;
import shop.bluebooktle.backend.coupon.repository.BookCouponRepository;
import shop.bluebooktle.backend.coupon.repository.CategoryCouponRepository;
import shop.bluebooktle.backend.coupon.repository.CouponRepository;
import shop.bluebooktle.backend.coupon.repository.CouponTypeRepository;
import shop.bluebooktle.backend.coupon.service.CouponService;
import shop.bluebooktle.common.domain.CouponTypeTarget;
import shop.bluebooktle.common.dto.coupon.request.CouponRegisterRequest;
import shop.bluebooktle.common.dto.coupon.response.CouponResponse;
import shop.bluebooktle.common.exception.book.BookNotFoundException;
import shop.bluebooktle.common.exception.coupon.CouponNameAlreadyExistsException;
import shop.bluebooktle.common.exception.coupon.CouponTypeNotFoundException;
import shop.bluebooktle.common.exception.coupon.InvalidCouponTargetException;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

	private final CouponRepository couponRepository;
	private final CouponTypeRepository couponTypeRepository;
	private final BookCouponRepository bookCouponRepository;
	private final CategoryCouponRepository categoryCouponRepository;

	private final BookRepository bookRepository;
	private final CategoryRepository categoryRepository;

	// Coupon 등록
	@Transactional
	@Override
	public void registerCoupon(CouponRegisterRequest request) {
		// couponType 없을 경우 예외처리
		CouponType couponType = couponTypeRepository.findById(request.getCouponTypeId())
			.orElseThrow(CouponTypeNotFoundException::new);
		// 도서와 카테고리 둘 다 지정할 경우
		if (request.getBookId() != null && request.getCategoryId() != null) {
			throw new InvalidCouponTargetException("도서 쿠폰과 카테고리 쿠폰은 동시에 설정 할 수 없습니다.");
		}
		// target ='BOOK' 인데 도서나 카테고리가 지정 안되었을 경우
		if (couponType.getTarget() == CouponTypeTarget.BOOK && request.getBookId() == null
			&& request.getCategoryId() == null
		) {
			throw new InvalidCouponTargetException("도서관련 쿠폰은 도서나 카테고리 중 하나를 선택해야 합니다.");
		}
		// target = 'ORDER' 인데 도서나 카테고리를 선택한 경우
		if (couponType.getTarget() == CouponTypeTarget.ORDER && request.getBookId() != null
			|| request.getCategoryId() != null) {
			throw new InvalidCouponTargetException("주문관련 쿠폰은 도서나 카테고리를 선택할 수 없습니다.");
		}

		//이미 존재하는 쿠폰 이름일 경우
		if (couponRepository.existsByCouponName(request.getName())) {
			throw new CouponNameAlreadyExistsException();
		}

		Coupon coupon = couponRepository.save(Coupon.builder()
			.type(couponType)
			.couponName(request.getName())
			.availableStartAt(request.getAvailableStartAt())
			.availableEndAt(request.getAvailableEndAt())
			.build());

		// 특정 도서 쿠폰
		if (request.getBookId() != null) {
			Book book = bookRepository.findById(request.getBookId())
				.orElseThrow(() -> new BookNotFoundException("존재하지 않는 도서입니다."));
			bookCouponRepository.save(new BookCoupon(coupon, book));
		}
		// 특정 카테고리 쿠폰
		if (request.getCategoryId() != null) {
			Category category = categoryRepository.findById(request.getCategoryId())
				.orElseThrow();
			//.orElseThrow(() -> CategoryNotFoundException("존재하지 않는 카테고리입니다.")); TODO 카테고리 Exception 추가 시 주석 제거
			categoryCouponRepository.save(new CategoryCoupon(coupon, category));
		}

	}

	// 전체 Coupon 조회
	@Override
	@Transactional
	public Page<CouponResponse> getAllCoupons(Pageable pageable) {
		return couponRepository.findAllWithCouponType(pageable)
			.map(coupon -> {
				CouponType couponType = coupon.getCouponType();
				return CouponResponse.builder()
					.couponName(coupon.getCouponName())
					.couponTypeName(couponType.getName())
					.target(couponType.getTarget())
					.availableStartAt(coupon.getAvailableStartAt())
					.availableEndAt(coupon.getAvailableEndAt())
					.createdAt(coupon.getCreatedAt())
					.build();
			});
	}
}
