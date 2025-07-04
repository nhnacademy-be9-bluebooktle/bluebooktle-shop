package shop.bluebooktle.backend.review.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.BDDMockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.BookSaleInfo;
import shop.bluebooktle.backend.book.entity.Img;
import shop.bluebooktle.backend.book.repository.BookSaleInfoRepository;
import shop.bluebooktle.backend.book.repository.ImgRepository;
import shop.bluebooktle.backend.book_order.entity.BookOrder;
import shop.bluebooktle.backend.book_order.jpa.BookOrderRepository;
import shop.bluebooktle.backend.elasticsearch.service.BookElasticSearchService;
import shop.bluebooktle.backend.order.entity.Order;
import shop.bluebooktle.backend.review.entity.Review;
import shop.bluebooktle.backend.review.entity.ReviewLikes;
import shop.bluebooktle.backend.review.repository.ReviewLikesRepository;
import shop.bluebooktle.backend.review.repository.ReviewRepository;
import shop.bluebooktle.backend.review.service.impl.ReviewServiceImpl;
import shop.bluebooktle.backend.user.repository.UserRepository;
import shop.bluebooktle.common.dto.review.request.ReviewRegisterRequest;
import shop.bluebooktle.common.dto.review.request.ReviewUpdateRequest;
import shop.bluebooktle.common.dto.review.response.ReviewResponse;
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.exception.InvalidInputValueException;
import shop.bluebooktle.common.exception.book.ReviewAuthorizationException;
import shop.bluebooktle.common.exception.book_order.BookOrderNotFoundException;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

	@InjectMocks
	private ReviewServiceImpl reviewService;

	@Mock
	private ReviewRepository reviewRepository;
	@Mock
	private UserRepository userRepository;
	@Mock
	private BookOrderRepository bookOrderRepository;
	@Mock
	private ImgRepository imgRepository;
	@Mock
	private BookSaleInfoRepository bookSaleInfoRepository;
	@Mock
	private ReviewLikesRepository reviewLikesRepository;
	@Mock
	private BookElasticSearchService bookElasticSearchService;
	@Mock
	private ApplicationEventPublisher eventPublisher;

	private User testUser;
	private Book testBook;
	private BookOrder testBookOrder;
	private BookSaleInfo testBookSaleInfo;
	private Review testReview;
	private Img testImg;
	private Order orderMock;

	@BeforeEach
	void setUp() {
		testUser = User.builder().nickname("testuser").build();
		ReflectionTestUtils.setField(testUser, "id", 1L);

		testBook = Book.builder().title("테스트 책 제목").build();
		ReflectionTestUtils.setField(testBook, "id", 10L);

		orderMock = mock(Order.class);
		ReflectionTestUtils.setField(orderMock, "id", 11L);
		lenient().when(orderMock.getUser()).thenReturn(testUser);

		testBookOrder = BookOrder.builder()
			.order(orderMock)
			.book(testBook)
			.quantity(1)
			.price(BigDecimal.valueOf(15.99))
			.build();
		ReflectionTestUtils.setField(testBookOrder, "id", 100L);

		testBookSaleInfo = BookSaleInfo.builder()
			.book(testBook)
			.star(BigDecimal.valueOf(5))
			.reviewCount(1L)
			.build();
		ReflectionTestUtils.setField(testBookSaleInfo, "id", 200L);

		testImg = Img.builder().imgUrl("http://example.com/test_image.jpg").build();
		ReflectionTestUtils.setField(testImg, "id", 1L);

		testReview = Review.builder()
			.user(testUser)
			.bookOrder(testBookOrder)
			.img(testImg)
			.star(5)
			.reviewContent("정말 좋은 책입니다!")
			.likes(0)
			.build();
		ReflectionTestUtils.setField(testReview, "id", 300L);
		ReflectionTestUtils.setField(testReview, "createdAt", LocalDateTime.now());
	}

	@Test
	@DisplayName("리뷰 등록 성공")
	void addReview_success_withImage() {
		// Given
		ReviewRegisterRequest request = ReviewRegisterRequest.builder()
			.star(5)
			.reviewContent("정말 좋은 책입니다!")
			.imgUrls(List.of("http://example.com/test_image.jpg"))
			.build();

		given(userRepository.findById(testUser.getId())).willReturn(Optional.of(testUser));
		given(bookOrderRepository.findById(testBookOrder.getId())).willReturn(Optional.of(testBookOrder));
		given(imgRepository.findByImgUrl(anyString())).willReturn(Optional.empty());
		given(imgRepository.save(any(Img.class))).willReturn(testImg);
		given(reviewRepository.save(any(Review.class))).willReturn(testReview);
		given(bookSaleInfoRepository.findByBook(any(Book.class))).willReturn(Optional.of(testBookSaleInfo));
		given(bookSaleInfoRepository.save(any(BookSaleInfo.class))).willReturn(testBookSaleInfo);

		// When
		ReviewResponse response = reviewService.addReview(testUser.getId(), testBookOrder.getId(), request);

		// Then
		assertThat(response).isNotNull();
		assertThat(response.getReviewId()).isEqualTo(testReview.getId());
		assertThat(response.getImgUrl()).isEqualTo(testImg.getImgUrl());
		assertThat(response.getStar()).isEqualTo(request.getStar());
		assertThat(response.getReviewContent()).isEqualTo(request.getReviewContent());
	}

	@Test
	@DisplayName("리뷰 등록 실패 - 주문자와 리뷰 작성자 불일치")
	void addReview_authorizationFailed_throwsException() {
		// Given
		ReviewRegisterRequest request = ReviewRegisterRequest.builder()
			.star(5)
			.reviewContent("리뷰 내용")
			.build();
		User anotherUser = User.builder().nickname("another").build();
		ReflectionTestUtils.setField(anotherUser, "id", 2L);

		Order orderWithAnotherUser = mock(Order.class);
		given(orderWithAnotherUser.getUser()).willReturn(anotherUser);

		BookOrder bookOrderOfAnotherUser = BookOrder.builder()
			.order(orderWithAnotherUser)
			.book(testBook)
			.quantity(1)
			.price(BigDecimal.valueOf(10.00))
			.build();
		ReflectionTestUtils.setField(bookOrderOfAnotherUser, "id", 101L);

		given(userRepository.findById(testUser.getId())).willReturn(Optional.of(testUser));
		given(bookOrderRepository.findById(bookOrderOfAnotherUser.getId())).willReturn(
			Optional.of(bookOrderOfAnotherUser));

		// When & Then
		assertThatThrownBy(() -> reviewService.addReview(testUser.getId(), bookOrderOfAnotherUser.getId(), request))
			.isInstanceOf(ReviewAuthorizationException.class);
	}

	@Test
	@DisplayName("리뷰 등록 실패 - 책 주문을 찾을 수 없음")
	void addReview_bookOrderNotFound_throwsException() {
		// Given
		ReviewRegisterRequest request = ReviewRegisterRequest.builder()
			.star(5)
			.reviewContent("리뷰 내용")
			.build();
		given(userRepository.findById(anyLong())).willReturn(Optional.of(testUser));
		given(bookOrderRepository.findById(anyLong())).willReturn(Optional.empty());

		// When & Then
		assertThatThrownBy(() -> reviewService.addReview(testUser.getId(), testBookOrder.getId(), request))
			.isInstanceOf(BookOrderNotFoundException.class);
	}

	@Test
	@DisplayName("리뷰 수정 성공")
	void updateReview_success_newImage() {
		// Given
		String newImageUrl = "http://example.com/new_image.jpg";
		Img newImg = Img.builder().imgUrl(newImageUrl).build();
		ReflectionTestUtils.setField(newImg, "id", 2L);

		ReviewUpdateRequest request = ReviewUpdateRequest.builder()
			.star(3)
			.reviewContent("이미지 변경된 리뷰입니다.")
			.imgUrls(List.of(newImageUrl))
			.build();

		ReflectionTestUtils.setField(testReview, "star", 5);

		given(reviewRepository.findById(testReview.getId())).willReturn(Optional.of(testReview));
		given(imgRepository.findByImgUrl(newImageUrl)).willReturn(Optional.empty()); // 새로운 이미지는 DB에 없음
		given(imgRepository.save(any(Img.class))).willReturn(newImg); // 새로운 이미지 저장
		given(reviewRepository.save(any(Review.class))).willReturn(testReview);
		given(bookSaleInfoRepository.findByBook(any(Book.class))).willReturn(Optional.of(testBookSaleInfo));
		given(bookSaleInfoRepository.save(any(BookSaleInfo.class))).willReturn(testBookSaleInfo);

		// When
		ReviewResponse response = reviewService.updateReview(testReview.getId(), request);

		// Then
		assertThat(response).isNotNull();
		assertThat(response.getReviewId()).isEqualTo(testReview.getId());
		assertThat(response.getStar()).isEqualTo(request.getStar());
		assertThat(response.getReviewContent()).isEqualTo(request.getReviewContent());
		assertThat(response.getImgUrl()).isEqualTo(newImageUrl); // 새로운 이미지 URL로 변경되었는지 확인
		then(imgRepository).should().save(any(Img.class)); // 새로운 이미지 저장 호출 검증
		then(bookElasticSearchService).should().updateReviewCountAndStar(any(BookSaleInfo.class));
	}

	@Test
	@DisplayName("리뷰 수정 실패 - 리뷰를 찾을 수 없음")
	void updateReview_reviewNotFound_throwsException() {
		// Given
		ReviewUpdateRequest request = ReviewUpdateRequest.builder()
			.star(4)
			.reviewContent("수정할 내용")
			.build();

		given(reviewRepository.findById(anyLong())).willReturn(Optional.empty());

		// When & Then
		assertThatThrownBy(() -> reviewService.updateReview(testReview.getId(), request))
			.isInstanceOf(InvalidInputValueException.class)
			.hasMessageContaining("Review not found with ID");
	}

	@Test
	@DisplayName("마이페이지 리뷰 조회 성공")
	void getMyReviews_success() {
		// Given
		Pageable pageable = PageRequest.of(0, 10);
		List<Review> reviewList = List.of(testReview);
		Page<Review> reviewPage = new PageImpl<>(reviewList, pageable, 1);

		given(reviewRepository.findReviewsByUserId(testUser.getId(), pageable)).willReturn(reviewPage);

		// When
		Page<ReviewResponse> responsePage = reviewService.getMyReviews(testUser.getId(), pageable);

		// Then
		assertThat(responsePage).isNotNull();
		assertThat(responsePage.getTotalElements()).isEqualTo(1);
		ReviewResponse response = responsePage.getContent().getFirst();
		assertThat(response.getReviewId()).isEqualTo(testReview.getId());
		assertThat(response.getUserId()).isEqualTo(testUser.getId());
		assertThat(response.getBookOrderId()).isEqualTo(testBookOrder.getId());
		assertThat(response.getBookTitle()).isEqualTo(testBook.getTitle());
		assertThat(response.getImgUrl()).isEqualTo(testImg.getImgUrl());
		assertThat(response.getStar()).isEqualTo(testReview.getStar());
		assertThat(response.getReviewContent()).isEqualTo(testReview.getReviewContent());
		assertThat(response.getLikes()).isEqualTo(testReview.getLikes());
	}

	@Test
	@DisplayName("도서 리뷰 조회 성공")
	void getReviewsForBook_success() {
		// Given
		Pageable pageable = PageRequest.of(0, 10);
		List<Review> reviewList = List.of(testReview);
		Page<Review> reviewPage = new PageImpl<>(reviewList, pageable, 1);

		given(reviewRepository.findReviewsByBookId(testBook.getId(), pageable)).willReturn(reviewPage);

		// When
		Page<ReviewResponse> responsePage = reviewService.getReviewsForBook(testBook.getId(), pageable);

		// Then
		assertThat(responsePage).isNotNull();
		assertThat(responsePage.getTotalElements()).isEqualTo(1);
		ReviewResponse response = responsePage.getContent().getFirst();
		assertThat(response.getReviewId()).isEqualTo(testReview.getId());
		assertThat(response.getUserId()).isEqualTo(testUser.getId());
		assertThat(response.getNickname()).isEqualTo(testUser.getNickname());
		assertThat(response.getImgUrl()).isEqualTo(testImg.getImgUrl());
		assertThat(response.getStar()).isEqualTo(testReview.getStar());
		assertThat(response.getReviewContent()).isEqualTo(testReview.getReviewContent());
		assertThat(response.getLikes()).isEqualTo(testReview.getLikes());
	}

	@Test
	@DisplayName("리뷰 좋아요 성공")
	void toggleReviewLike_noLikeExists_addsNewLike() {
		// Given
		given(reviewLikesRepository.findByUserIdAndReviewId(testUser.getId(), testReview.getId()))
			.willReturn(Optional.empty());
		given(reviewLikesRepository.findSoftDeletedByUserIdAndReviewId(testUser.getId(), testReview.getId()))
			.willReturn(Optional.empty());
		given(userRepository.findById(testUser.getId())).willReturn(Optional.of(testUser));
		given(reviewRepository.findById(testReview.getId())).willReturn(Optional.of(testReview));
		given(reviewLikesRepository.save(any(ReviewLikes.class))).willReturn(mock(ReviewLikes.class));

		// When
		boolean liked = reviewService.toggleReviewLike(testReview.getId(), testUser.getId());

		// Then
		assertThat(liked).isTrue();
		assertThat(testReview.getLikes()).isEqualTo(1);
	}

	@Test
	@DisplayName("리뷰 좋아요 실패 - 리뷰를 찾을 수 없음")
	void toggleReviewLike_reviewNotFound_throwsException() {
		// Given
		given(reviewLikesRepository.findByUserIdAndReviewId(anyLong(), anyLong()))
			.willReturn(Optional.empty());
		given(reviewLikesRepository.findSoftDeletedByUserIdAndReviewId(anyLong(), anyLong()))
			.willReturn(Optional.empty());
		given(userRepository.findById(anyLong())).willReturn(Optional.of(testUser));
		given(reviewRepository.findById(anyLong())).willReturn(Optional.empty());

		// When & Then
		assertThatThrownBy(() -> reviewService.toggleReviewLike(testReview.getId(), testUser.getId()))
			.isInstanceOf(InvalidInputValueException.class);
	}

	@Test
	@DisplayName("리뷰 좋아요 취소 성공")
	void toggleReviewLike_likeExists_removesLike() {
		// Given
		ReviewLikes existingLike = ReviewLikes.builder()
			.user(testUser)
			.review(testReview)
			.build();
		ReflectionTestUtils.setField(existingLike, "id", 400L);

		ReflectionTestUtils.setField(testReview, "likes", 1);

		given(reviewLikesRepository.findByUserIdAndReviewId(testUser.getId(), testReview.getId()))
			.willReturn(Optional.of(existingLike));
		given(reviewLikesRepository.findSoftDeletedByUserIdAndReviewId(testUser.getId(), testReview.getId()))
			.willReturn(Optional.empty());
		given(reviewRepository.findById(testReview.getId())).willReturn(Optional.of(testReview));

		// When
		boolean liked = reviewService.toggleReviewLike(testReview.getId(), testUser.getId());

		// Then
		assertThat(liked).isFalse();
		assertThat(testReview.getLikes()).isZero();
		then(reviewLikesRepository).should().deleteByUserIdAndReviewId(testUser.getId(), testReview.getId());
		then(reviewRepository).should().save(testReview);
	}

}