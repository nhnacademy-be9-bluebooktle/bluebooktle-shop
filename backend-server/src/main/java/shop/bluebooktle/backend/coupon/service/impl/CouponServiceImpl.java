package shop.bluebooktle.backend.coupon.service.impl;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.Category;
import shop.bluebooktle.backend.book.repository.BookRepository;
import shop.bluebooktle.backend.book.repository.CategoryRepository;
import shop.bluebooktle.backend.coupon.dto.CouponSearchRequest;
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
import shop.bluebooktle.common.dto.coupon.request.CouponUpdateRequest;
import shop.bluebooktle.common.dto.coupon.response.CouponResponse;
import shop.bluebooktle.common.exception.book.BookNotFoundException;
import shop.bluebooktle.common.exception.book.CategoryNotFoundException;
import shop.bluebooktle.common.exception.coupon.CouponNotFoundException;
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

	// Coupon 등록
	@Override
	public void registerCoupon(CouponRegisterRequest request) {
		// couponType 없을 경우 예외처리
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

	// 전체 Coupon 조회
	@Override
	@Transactional(readOnly = true)
	public Page<CouponResponse> getAllCoupons(Pageable pageable) {
		CouponSearchRequest request = new CouponSearchRequest();
		return couponRepository.findAllByCoupon(request, pageable);
	}

	// 수정
	@Override
	public void updateCoupon(Long couponId, CouponUpdateRequest request) {
		Coupon coupon = couponRepository.findById(couponId)
			.orElseThrow(CouponNotFoundException::new);
		CouponType couponType = coupon.getCouponType();
		validateCouponTarget(couponType, request.getBookId(), request.getCategoryId());

		// dirty checking 방식
		coupon.update(request.getName());
		// TODO [쿠폰] save로 변경
		if (couponType.getTarget() == CouponTypeTarget.BOOK) {
			updateCouponTarget(coupon, request);
		}
	}

	//삭제
	@Override
	public void deleteCoupon(Long couponId) {
		Coupon coupon = couponRepository.findById(couponId)
			.orElseThrow(CouponNotFoundException::new);

		bookCouponRepository.deleteByCoupon(coupon);
		categoryCouponRepository.deleteByCoupon(coupon);
		couponRepository.delete(coupon);

	}

	// 공통 유효성 검사
	private void validateCouponTarget(CouponType couponType, Long bookId, Long categoryId) {
		// 도서와 카테고리 둘 다 지정할 경우
		if (bookId != null && categoryId != null) {
			throw new InvalidCouponTargetException("도서 쿠폰과 카테고리 쿠폰은 동시에 설정 할 수 없습니다.");
		}
		// target ='BOOK' 인데 도서나 카테고리가 지정 안되었을 경우
		if (couponType.getTarget() == CouponTypeTarget.BOOK && bookId == null
			&& categoryId == null
		) {
			throw new InvalidCouponTargetException("도서관련 쿠폰은 도서나 카테고리 중 하나를 선택해야 합니다.");
		}
		// target = 'ORDER' 인데 도서나 카테고리를 선택한 경우
		if (couponType.getTarget() == CouponTypeTarget.ORDER && (bookId != null
			|| categoryId != null)) {
			throw new InvalidCouponTargetException("주문관련 쿠폰은 도서나 카테고리를 선택할 수 없습니다.");
		}
	}

	private void updateCouponTarget(Coupon coupon, CouponUpdateRequest request) {
		Long newBookId = request.getBookId();
		Long newCategoryId = request.getCategoryId();

		// 기존 데이터 조회
		Optional<BookCoupon> currentBook = bookCouponRepository.findByCoupon(coupon);
		Optional<CategoryCoupon> currentCategory = categoryCouponRepository.findByCoupon(coupon);

		// Book
		if (newBookId != null) {
			// 기존 도서가 없거나 || 기존 도서와 다른 경우
			if (currentBook.isEmpty() || !currentBook.get().getBook().getId().equals(newBookId)) {
				currentBook.ifPresent(bookCouponRepository::delete);
				Book book = bookRepository.findById(newBookId)
					.orElseThrow(BookNotFoundException::new);
				bookCouponRepository.save(new BookCoupon(coupon, book));
			}
		} else {
			currentBook.ifPresent(bookCouponRepository::delete);
		}
		// Category
		if (newCategoryId != null) {
			if (currentCategory.isEmpty() || !currentCategory.get().getCategory().getId().equals(newCategoryId)) {
				currentCategory.ifPresent(categoryCouponRepository::delete);
				Category category = categoryRepository.findById(newCategoryId)
					.orElseThrow(CategoryNotFoundException::new);
				categoryCouponRepository.save(new CategoryCoupon(coupon, category));
			}
		} else {
			currentCategory.ifPresent(categoryCouponRepository::delete);
		}
	}
}
