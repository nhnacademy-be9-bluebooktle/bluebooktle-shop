package shop.bluebooktle.backend.coupon.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import shop.bluebooktle.common.domain.coupon.CouponTypeTarget;
import shop.bluebooktle.common.dto.coupon.request.CouponRegisterRequest;
import shop.bluebooktle.common.dto.coupon.response.CouponResponse;
import shop.bluebooktle.common.exception.book.BookNotFoundException;
import shop.bluebooktle.common.exception.book.CategoryNotFoundException;
import shop.bluebooktle.common.exception.coupon.CouponTypeNotFoundException;
import shop.bluebooktle.common.exception.coupon.InvalidCouponTargetException;

@Service
@RequiredArgsConstructor
@Transactional
public class CouponServiceImpl implements CouponService {

	private final CouponRepository couponRepository;
	private final CouponTypeRepository couponTypeRepository;
	private final BookCouponRepository bookCouponRepository;
	private final CategoryCouponRepository categoryCouponRepository;

	private final BookRepository bookRepository;
	private final CategoryRepository categoryRepository;

	@Override
	public void registerCoupon(CouponRegisterRequest request) {
		CouponType couponType = couponTypeRepository.findById(request.getCouponTypeId())
			.orElseThrow(CouponTypeNotFoundException::new);

		validateCouponTarget(couponType, request.getBookId(), request.getCategoryId());

		Coupon coupon = couponRepository.save(Coupon.builder()
			.type(couponType)
			.couponName(request.getName())
			.build());

		// 특정 도서 쿠폰
		if (request.getBookId() != null) {
			Book book = bookRepository.findById(request.getBookId())
				.orElseThrow(BookNotFoundException::new);
			bookCouponRepository.save(new BookCoupon(coupon, book));
		}
		// 특정 카테고리 쿠폰
		if (request.getCategoryId() != null) {
			Category category = categoryRepository.findById(request.getCategoryId())
				.orElseThrow(CategoryNotFoundException::new);
			categoryCouponRepository.save(new CategoryCoupon(coupon, category));
		}
	}

	@Override
	@Transactional(readOnly = true)
	public Page<CouponResponse> getAllCoupons(String searchCouponName, Pageable pageable) {
		return couponRepository.findAllByCoupon(searchCouponName, pageable);
	}

	private void validateCouponTarget(CouponType couponType, Long bookId, Long categoryId) {
		if (bookId != null && categoryId != null) {
			throw new InvalidCouponTargetException("도서 쿠폰과 카테고리 쿠폰은 동시에 설정 할 수 없습니다.");
		}
		if (couponType.getTarget() == CouponTypeTarget.BOOK && bookId == null
			&& categoryId == null
		) {
			throw new InvalidCouponTargetException("도서관련 쿠폰은 도서나 카테고리 중 하나를 선택해야 합니다.");
		}
		if (couponType.getTarget() == CouponTypeTarget.ORDER && (bookId != null
			|| categoryId != null)) {
			throw new InvalidCouponTargetException("주문관련 쿠폰은 도서나 카테고리를 선택할 수 없습니다.");
		}
	}
}
