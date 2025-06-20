package shop.bluebooktle.backend.review.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.BookSaleInfo;
import shop.bluebooktle.backend.book.entity.Img;
import shop.bluebooktle.backend.book.event.type.UserReviewPointEvent;
import shop.bluebooktle.backend.book.repository.BookSaleInfoRepository;
import shop.bluebooktle.backend.book.repository.ImgRepository;
import shop.bluebooktle.backend.book_order.entity.BookOrder;
import shop.bluebooktle.backend.book_order.jpa.BookOrderRepository;
import shop.bluebooktle.backend.elasticsearch.service.BookElasticSearchService;
import shop.bluebooktle.backend.review.entity.Review;
import shop.bluebooktle.backend.review.entity.ReviewLikes;
import shop.bluebooktle.backend.review.repository.ReviewLikesRepository;
import shop.bluebooktle.backend.review.repository.ReviewRepository;
import shop.bluebooktle.backend.review.service.ReviewService;
import shop.bluebooktle.backend.user.repository.UserRepository;
import shop.bluebooktle.common.dto.review.request.ReviewRegisterRequest;
import shop.bluebooktle.common.dto.review.request.ReviewUpdateRequest;
import shop.bluebooktle.common.dto.review.response.ReviewResponse;
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.exception.InvalidInputValueException;
import shop.bluebooktle.common.exception.auth.UserNotFoundException;
import shop.bluebooktle.common.exception.book.BookSaleInfoNotFoundException;
import shop.bluebooktle.common.exception.book.ReviewAuthorizationException;
import shop.bluebooktle.common.exception.book_order.BookOrderNotFoundException;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

	private final ReviewRepository reviewRepository;
	private final UserRepository userRepository;
	private final BookOrderRepository bookOrderRepository;
	private final ImgRepository imgRepository;
	private final BookSaleInfoRepository bookSaleInfoRepository;
	private final ReviewLikesRepository reviewLikesRepository;
	private final ApplicationEventPublisher eventPublisher;

	private final BookElasticSearchService bookElasticSearchService;

	@Override
	public ReviewResponse addReview(Long userId, Long bookOrderId, ReviewRegisterRequest reviewRegisterRequest) {
		User user = userRepository.findById(userId)
			.orElseThrow(UserNotFoundException::new);

		BookOrder bookOrder = bookOrderRepository.findById(bookOrderId)
			.orElseThrow(BookOrderNotFoundException::new);

		if (!bookOrder.getOrder().getUser().getId().equals(userId)) {
			throw new ReviewAuthorizationException();
		}

		Img img = null;
		List<String> imageUrls = reviewRegisterRequest.getImgUrls();
		if (imageUrls != null && !imageUrls.isEmpty()) {
			String firstImageUrl = imageUrls.getFirst();
			img = imgRepository.findByImgUrl(firstImageUrl)
				.orElseGet(() -> {
					Img newImg = Img.builder().imgUrl(firstImageUrl).build();
					return imgRepository.save(newImg);
				});
		}

		Review review = Review.builder()
			.user(user)
			.bookOrder(bookOrder)
			.img(img)
			.star(reviewRegisterRequest.getStar())
			.reviewContent(reviewRegisterRequest.getReviewContent())
			.likes(0)
			.build();

		Review saved = reviewRepository.save(review);

		Book book = bookOrder.getBook();

		BookSaleInfo bookSaleInfo = bookSaleInfoRepository.findByBook(book)
			.orElseThrow(BookSaleInfoNotFoundException::new);

		bookSaleInfo.addReviewAndCalculateStar(saved.getStar());

		bookSaleInfoRepository.save(bookSaleInfo);

		eventPublisher.publishEvent(new UserReviewPointEvent(userId));

		// 엘라스틱 서치 도서에 대한 리뷰 정보 등록
		bookElasticSearchService.updateReviewCountAndStar(bookSaleInfo);

		return ReviewResponse.builder()
			.reviewId(saved.getId())
			.userId(saved.getUser().getId())
			.bookOrderId(saved.getBookOrder().getId())
			.imgUrl(saved.getImg() != null ? saved.getImg().getImgUrl() : null)
			.star(saved.getStar())
			.reviewContent(saved.getReviewContent())
			.likes(saved.getLikes())
			.createdAt(saved.getCreatedAt())
			.build();
	}

	@Override
	public ReviewResponse updateReview(Long reviewId, ReviewUpdateRequest reviewUpdateRequest) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new InvalidInputValueException("Review not found with ID: " + reviewId));

		int oldStar = review.getStar();

		Img imgToUpdate = null;
		List<String> imageUrls = reviewUpdateRequest.getImgUrls();
		if (imageUrls != null && !imageUrls.isEmpty()) {
			String firstImageUrl = imageUrls.getFirst();
			imgToUpdate = imgRepository.findByImgUrl(firstImageUrl)
				.orElseGet(() -> {
					Img newImg = Img.builder().imgUrl(firstImageUrl).build();
					return imgRepository.save(newImg);
				});
		}
		review.updateDetails(reviewUpdateRequest.getStar(), reviewUpdateRequest.getReviewContent(), imgToUpdate);

		Review updatedReview = reviewRepository.save(review);

		Book book = review.getBookOrder().getBook();
		BookSaleInfo bookSaleInfo = bookSaleInfoRepository.findByBook(book)
			.orElseThrow(BookSaleInfoNotFoundException::new);

		bookSaleInfo.updateReviewAndRecalculateStar(oldStar, updatedReview.getStar());
		bookSaleInfoRepository.save(bookSaleInfo);

		// 엘라스틱 서치 도서에 대한 리뷰 정보 수정
		bookElasticSearchService.updateReviewCountAndStar(bookSaleInfo);

		return ReviewResponse.builder()
			.reviewId(updatedReview.getId())
			.userId(updatedReview.getUser().getId())
			.bookOrderId(updatedReview.getBookOrder().getId())
			.imgUrl(updatedReview.getImg() != null ? updatedReview.getImg().getImgUrl() : null)
			.star(updatedReview.getStar())
			.reviewContent(updatedReview.getReviewContent())
			.likes(updatedReview.getLikes())
			.createdAt(updatedReview.getCreatedAt())
			.build();
	}

	@Override
	@Transactional(readOnly = true)
	public Page<ReviewResponse> getMyReviews(Long userId, Pageable pageable) {
		Page<Review> reviewPage = reviewRepository.findReviewsByUserId(userId, pageable);
		return reviewPage.map(review -> ReviewResponse.builder()
			.reviewId(review.getId())
			.userId(review.getUser().getId())
			.bookOrderId(review.getBookOrder().getId())
			.bookTitle(review.getBookOrder().getBook().getTitle())
			.imgUrl(review.getImg() != null ? review.getImg().getImgUrl() : null)
			.star(review.getStar())
			.reviewContent(review.getReviewContent())
			.likes(review.getLikes())
			.createdAt(review.getCreatedAt())
			.build());
	}

	@Override
	@Transactional(readOnly = true)
	public Page<ReviewResponse> getReviewsForBook(Long bookId, Pageable pageable) {
		Page<Review> reviews = reviewRepository.findReviewsByBookId(bookId, pageable);
		return reviews.map(review -> ReviewResponse.builder()
			.reviewId(review.getId())
			.userId(review.getUser() != null ? review.getUser().getId() : null)
			.nickname(review.getUser().getNickname())
			.imgUrl((review.getImg() != null) ? review.getImg().getImgUrl() : null)
			.star(review.getStar())
			.reviewContent(review.getReviewContent())
			.likes(review.getLikes())
			.createdAt(review.getCreatedAt())
			.build());
	}

	@Override
	public boolean toggleReviewLike(Long reviewId, Long userId) {
		Optional<ReviewLikes> activeLike = reviewLikesRepository.findByUserIdAndReviewId(userId, reviewId);

		Optional<ReviewLikes> softDeletedLike = reviewLikesRepository.findSoftDeletedByUserIdAndReviewId(userId,
			reviewId);

		boolean liked;

		if (activeLike.isPresent()) {
			reviewLikesRepository.deleteByUserIdAndReviewId(userId, reviewId);
			liked = false;
			decrementReviewLikeCount(reviewId);
		} else if (softDeletedLike.isPresent()) {
			reviewLikesRepository.undeleteByUserIdAndReviewId(userId, reviewId);
			liked = true;
			incrementReviewLikeCount(reviewId);
		} else {
			User user = userRepository.findById(userId)
				.orElseThrow(UserNotFoundException::new);
			Review review = reviewRepository.findById(reviewId)
				.orElseThrow(InvalidInputValueException::new);

			ReviewLikes newLikes = ReviewLikes.builder()
				.user(user)
				.review(review)
				.build();

			reviewLikesRepository.save(newLikes);
			liked = true;
			incrementReviewLikeCount(reviewId);
		}
		return liked;
	}

	private void incrementReviewLikeCount(Long reviewId) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(InvalidInputValueException::new);
		review.incrementLikeCount();
		reviewRepository.save(review);
	}

	private void decrementReviewLikeCount(Long reviewId) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(InvalidInputValueException::new);
		review.decrementLikeCount();
		reviewRepository.save(review);
	}

}