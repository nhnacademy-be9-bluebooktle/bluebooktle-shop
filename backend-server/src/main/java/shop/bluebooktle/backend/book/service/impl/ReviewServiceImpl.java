package shop.bluebooktle.backend.book.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.entity.Img;
import shop.bluebooktle.backend.book.repository.ImgRepository;
import shop.bluebooktle.backend.book.repository.ReviewRepository;
import shop.bluebooktle.backend.book.service.ReviewService;
import shop.bluebooktle.backend.book_order.entity.BookOrder;
import shop.bluebooktle.backend.book_order.jpa.BookOrderRepository;
import shop.bluebooktle.backend.user.repository.UserRepository;
import shop.bluebooktle.common.dto.book.request.ReviewRequest;
import shop.bluebooktle.common.dto.book.response.ReviewResponse;
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.entity.review.Review;
import shop.bluebooktle.common.exception.auth.UserNotFoundException;
import shop.bluebooktle.common.exception.book.ImgNotFoundException;
import shop.bluebooktle.common.exception.book_order.BookOrderNotFoundException;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

	private final ReviewRepository reviewRepository;
	private final UserRepository userRepository;
	private final BookOrderRepository bookOrderRepository;
	private final ImgRepository imgRepository;

	@Override
	@Transactional
	public ReviewResponse addReview(Long userId, Long bookOrderId, ReviewRequest reviewRequest) {
		User user = userRepository.findById(userId)
			.orElseThrow(UserNotFoundException::new);

		BookOrder bookOrder = bookOrderRepository.findById(bookOrderId)
			.orElseThrow(BookOrderNotFoundException::new);

		Img img = imgRepository.findById(reviewRequest.getImgId()).orElseThrow(ImgNotFoundException::new);

		Review review = Review.builder()
			.user(user)
			.bookOrder(bookOrder)
			.img(img)
			.star(reviewRequest.getStar())
			.reviewContent(reviewRequest.getReviewContent())
			.likes(0)
			.build();

		Review saved = reviewRepository.save(review);

		return ReviewResponse.builder()
			.reviewId(saved.getId())
			.userId(saved.getUser().getId())
			.bookOrderId(saved.getBookOrder().getId())
			.imgId(saved.getImg() != null ? saved.getImg().getId() : null)
			.star(saved.getStar())
			.reviewContent(saved.getReviewContent())
			.likes(saved.getLikes())
			.createdAt(saved.getCreatedAt())
			.build();
	}

	@Override
	public Page<ReviewResponse> getReviews(Long bookOrderId, int page, int size) {
		PageRequest pageRequest = PageRequest.of(page, size);

		Page<Review> reviewPage = reviewRepository.findAllByBookOrder_Id(bookOrderId, pageRequest);

		return reviewPage.map(review -> ReviewResponse.builder()
			.reviewId(review.getId())
			.userId(review.getUser().getId())
			.bookOrderId(review.getBookOrder().getId())
			.imgId(review.getImg() != null ? review.getImg().getId() : null)
			.star(review.getStar())
			.reviewContent(review.getReviewContent())
			.likes(review.getLikes())
			.createdAt(review.getCreatedAt())
			.build()
		);
	}
}
