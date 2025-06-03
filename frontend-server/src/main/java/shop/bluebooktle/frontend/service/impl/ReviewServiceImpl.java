package shop.bluebooktle.frontend.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.common.dto.book.request.ReviewRequest;
import shop.bluebooktle.common.dto.book.response.ReviewResponse;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.frontend.repository.ReviewRepository;
import shop.bluebooktle.frontend.service.ReviewService;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
	private final ReviewRepository reviewRepository;

	// 리뷰작성
	@Override
	public void addReview(Long userId, Long bookOrderId, ReviewRequest reviewRequest) {
		reviewRepository.addReview(bookOrderId, reviewRequest);
	}

	// 내가쓴 리뷰 목록조회
	@Override
	public Page<ReviewResponse> getMyReviews(Long userId, Pageable pageable) {
		PaginationData<ReviewResponse> paginationData =
			reviewRepository.getMyReviews(pageable.getPageNumber(), pageable.getPageSize());

		List<ReviewResponse> content = paginationData.getContent();
		long total = paginationData.getTotalElements();

		return new PageImpl<>(content, pageable, total);

	}
}
