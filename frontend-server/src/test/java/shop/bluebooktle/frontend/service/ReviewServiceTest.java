package shop.bluebooktle.frontend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import shop.bluebooktle.common.dto.book.response.img.ImgResponse;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.review.request.ReviewRegisterRequest;
import shop.bluebooktle.common.dto.review.request.ReviewRequest;
import shop.bluebooktle.common.dto.review.request.ReviewUpdateRequest;
import shop.bluebooktle.common.dto.review.response.ReviewResponse;
import shop.bluebooktle.frontend.repository.ImageServerClient;
import shop.bluebooktle.frontend.repository.ImgRepository;
import shop.bluebooktle.frontend.repository.ReviewRepository;
import shop.bluebooktle.frontend.service.impl.ReviewServiceImpl;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

	@InjectMocks
	private ReviewServiceImpl reviewService;

	@Mock
	private ReviewRepository reviewRepository; // 목(Mock) 객체
	@Mock
	private AdminImgService adminImgService; // 목(Mock) 객체
	@Mock
	private ImageServerClient imageServerClient; // 목(Mock) 객체
	@Mock
	private ImgRepository imgRepository; // 목(Mock) 객체

	private Long userId;
	private Long bookOrderId;
	private Long reviewId;
	private Long bookId;
	private ReviewRequest reviewRequest;
	private MultipartFile mockFile;
	private String minioPresignedUrl;
	private String minioUploadedUrl;

	@BeforeEach
	void setUp() {
		userId = 1L;
		bookOrderId = 101L;
		reviewId = 201L;
		bookId = 301L;

		mockFile = new MockMultipartFile(
			"image",
			"test.jpg",
			"image/jpeg",
			"test image content".getBytes()
		);

		minioPresignedUrl = "http://localhost:9000/bluebooktle-bookimage/some-object-name?X-Amz-Signature=xxx";
		minioUploadedUrl = "http://localhost:9000/bluebooktle-bookimage/some-object-name";

		reviewRequest = ReviewRequest.builder()
			.star(5)
			.reviewContent("Great book, highly recommend!")
			.imageFiles(List.of(mockFile))
			.build();
	}

	@Test
	@DisplayName("리뷰 작성 성공 - 이미지 포함")
	void addReview_successWithImage() throws IOException {
		when(adminImgService.getPresignedUploadUrl()).thenReturn(minioPresignedUrl);
		doNothing().when(imageServerClient).upload(
			anyString(), anyString(), anyMap(), anyMap(), any(byte[].class)
		);

		when(reviewRepository.addReview(anyLong(), any(ReviewRegisterRequest.class)))
			.thenReturn(ReviewResponse.builder().reviewId(reviewId).build());

		reviewService.addReview(userId, bookOrderId, reviewRequest);

		verify(adminImgService, times(1)).getPresignedUploadUrl();
		verify(imageServerClient, times(1)).upload(
			eq("bluebooktle-bookimage"), // bucket
			eq("some-object-name"),     // objectName
			anyMap(),                   // queryParams
			anyMap(),                   // headers
			any(byte[].class)           // file bytes
		);

		verify(reviewRepository, times(1)).addReview(eq(bookOrderId), argThat(req ->
			req.getStar().equals(reviewRequest.getStar()) &&
				req.getReviewContent().equals(reviewRequest.getReviewContent()) &&
				req.getImgUrls().contains("/images/bluebooktle-bookimage/some-object-name")
		));
	}

	@Test
	@DisplayName("리뷰 작성 성공 - 이미지 없음")
	void addReview_successNoImage() {
		// 이미지 파일이 없는 ReviewRequest 생성
		ReviewRequest noImageReviewRequest = ReviewRequest.builder()
			.star(4)
			.reviewContent("Good read!")
			.imageFiles(Collections.emptyList()) // 이미지 파일 없음
			.build();

		// ReviewRepository.addReview가 ReviewResponse를 반환하므로, when().thenReturn() 사용
		when(reviewRepository.addReview(anyLong(), any(ReviewRegisterRequest.class)))
			.thenReturn(ReviewResponse.builder().reviewId(reviewId).build()); // 더미 ReviewResponse 객체 반환

		// 서비스 메서드 호출
		reviewService.addReview(userId, bookOrderId, noImageReviewRequest);

		// 이미지 관련 메서드가 호출되지 않았는지 검증
		verify(adminImgService, never()).getPresignedUploadUrl();
		verify(imageServerClient, never()).upload(
			anyString(), anyString(), anyMap(), anyMap(), any(byte[].class)
		);

		// reviewRepository.addReview가 올바른 인자(이미지 URL 없음)로 호출되었는지 검증
		verify(reviewRepository, times(1)).addReview(eq(bookOrderId), argThat(req ->
			req.getStar().equals(noImageReviewRequest.getStar()) &&
				req.getReviewContent().equals(noImageReviewRequest.getReviewContent()) &&
				req.getImgUrls().isEmpty()
		));
	}

	@Test
	@DisplayName("리뷰 작성 실패 - MinIO 업로드 중 IOException 발생")
	void addReview_uploadFailureThrowsUncheckedIOException() {
		when(adminImgService.getPresignedUploadUrl()).thenReturn(minioPresignedUrl);

		doThrow(new UncheckedIOException("Simulated MinIO upload error", new IOException("Original IO error")))
			.when(imageServerClient).upload(
				anyString(), anyString(), anyMap(), anyMap(), any(byte[].class)
			);

		// UncheckedIOException이 발생하는지 확인
		UncheckedIOException thrown = assertThrows(UncheckedIOException.class, () ->
			reviewService.addReview(userId, bookOrderId, reviewRequest)
		);

		// 예외 메시지 검증
		assertTrue(thrown.getMessage().contains("Simulated MinIO upload error")); // 메시지 변경
		assertTrue(thrown.getCause() instanceof IOException); // 원인 예외 검증

		// reviewRepository는 호출되지 않아야 함
		verify(reviewRepository, never()).addReview(anyLong(), any(ReviewRegisterRequest.class));
	}

	@Test
	@DisplayName("리뷰 수정 성공 - 기존 이미지 삭제 후 새 이미지 업로드")
	void updateReview_successWithImageReplacement() throws IOException {
		ImgResponse existingImg = ImgResponse.builder()
			.id(1L)
			.imgUrl("http://localhost:9000/bluebooktle-bookimage/old-object-name")
			.createdAt(LocalDateTime.now())
			.build();
		when(imgRepository.getImageByReviewId(reviewId)).thenReturn(existingImg);
		doNothing().when(adminImgService).deleteImage(anyString());

		// 새 이미지 업로드 관련 목킹
		when(adminImgService.getPresignedUploadUrl()).thenReturn(minioPresignedUrl);
		doNothing().when(imageServerClient).upload(
			anyString(), anyString(), anyMap(), anyMap(), any(byte[].class)
		);

		// ReviewRepository.updateReview가 ReviewResponse를 반환하므로, when().thenReturn() 사용
		when(reviewRepository.updateReview(anyLong(), any(ReviewUpdateRequest.class)))
			.thenReturn(ReviewResponse.builder().reviewId(reviewId).build()); // 더미 ReviewResponse 객체 반환

		// 서비스 메서드 호출
		reviewService.updateReivew(reviewId, reviewRequest); //

		// 기존 이미지 삭제 메서드 호출 검증
		verify(imgRepository, times(1)).getImageByReviewId(reviewId);
		verify(adminImgService, times(1)).deleteImage("old-object-name");

		// 새 이미지 업로드 관련 메서드 호출 검증
		verify(adminImgService, times(1)).getPresignedUploadUrl();
		verify(imageServerClient, times(1)).upload(
			eq("bluebooktle-bookimage"),
			eq("some-object-name"),
			anyMap(), anyMap(), any(byte[].class)
		);

		// reviewRepository.updateReview가 올바른 인자(새 이미지 URL 포함)로 호출되었는지 검증
		verify(reviewRepository, times(1)).updateReview(eq(reviewId), argThat(req ->
			req.getStar().equals(reviewRequest.getStar()) &&
				req.getReviewContent().equals(reviewRequest.getReviewContent()) &&
				req.getImgUrls().contains("/images/bluebooktle-bookimage/some-object-name")
		));
	}

	@Test
	@DisplayName("내가 쓴 리뷰 목록 조회 성공 - PaginationData 사용")
	void getMyReviews_success_withPaginationData() {
		Pageable pageable = PageRequest.of(0, 10);
		LocalDateTime now = LocalDateTime.now();

		// ReviewResponse 리스트 생성 (PaginationData의 content)
		List<ReviewResponse> reviewList = List.of(
			ReviewResponse.builder()
				.reviewId(reviewId).userId(1L).bookOrderId(1001L).bookTitle("Book 1")
				.nickname("UserA").imgUrl(null).star(5).reviewContent("Great book!").likes(10)
				.createdAt(now.minusDays(5)).build(),
			ReviewResponse.builder()
				.reviewId(reviewId + 1).userId(1L).bookOrderId(1002L).bookTitle("Book 2")
				.nickname("UserA").imgUrl("/images/book/img2.jpg").star(4).reviewContent("Good read.")
				.likes(5).createdAt(now.minusDays(2)).build()
		);

		// PaginationInfo 객체 생성 (PaginationData의 pagination)
		PaginationData.PaginationInfo paginationInfo = new PaginationData.PaginationInfo(
			1, // totalPages
			2L, // totalElements
			0, // currentPage
			10, // pageSize
			true, // isFirst
			true, // isLast
			false, // hasNext
			false // hasPrevious
		);

		// ReviewRepository가 반환할 PaginationData 객체 생성
		PaginationData<ReviewResponse> paginationData = new PaginationData<>(reviewList, paginationInfo);

		when(reviewRepository.getMyReviews(pageable.getPageNumber(), pageable.getPageSize()))
			.thenReturn(paginationData);

		// 서비스 메서드 호출
		Page<ReviewResponse> result = reviewService.getMyReviews(userId, pageable);

		// 결과 검증
		assertNotNull(result);
		assertEquals(paginationData.getTotalElements(), result.getTotalElements()); // getTotalElements()로 검증
		assertEquals(reviewList.size(), result.getContent().size());
		assertEquals(reviewList.get(0).getReviewContent(), result.getContent().get(0).getReviewContent());
		assertEquals(reviewList.get(0).getNickname(), result.getContent().get(0).getNickname());

		// reviewRepository 호출 검증
		verify(reviewRepository, times(1)).getMyReviews(pageable.getPageNumber(), pageable.getPageSize());
	}

	@Test
	@DisplayName("도서 상세 페이지 리뷰 목록 조회 성공 ")
	void getReviewsForBook_success_withPaginationData() {
		Pageable pageable = PageRequest.of(0, 5);
		LocalDateTime now = LocalDateTime.now();

		// ReviewResponse 리스트 생성
		List<ReviewResponse> reviewList = List.of(
			ReviewResponse.builder()
				.reviewId(reviewId).userId(1L).bookOrderId(3001L).bookTitle("Target Book")
				.nickname("Reviewer1").imgUrl("/images/book/imgA.jpg").star(5).reviewContent("Awesome!")
				.likes(20).createdAt(now.minusDays(10)).build(),
			ReviewResponse.builder()
				.reviewId(reviewId + 1).userId(2L).bookOrderId(3002L).bookTitle("Target Book")
				.nickname("Reviewer2").imgUrl(null).star(4).reviewContent("Worth reading.")
				.likes(15).createdAt(now.minusDays(7)).build()
		);

		// PaginationInfo 객체 생성
		PaginationData.PaginationInfo paginationInfo = new PaginationData.PaginationInfo(
			1, // totalPages
			2L, // totalElements
			0, // currentPage
			5, // pageSize
			true, // isFirst
			true, // isLast
			false, // hasNext
			false // hasPrevious
		);

		// ReviewRepository가 반환할 PaginationData 객체 생성
		PaginationData<ReviewResponse> paginationData = new PaginationData<>(reviewList, paginationInfo);

		when(reviewRepository.getReviewsForBook(bookId, pageable.getPageNumber(), pageable.getPageSize()))
			.thenReturn(paginationData);

		// 서비스 메서드 호출
		Page<ReviewResponse> result = reviewService.getReviewsForBook(bookId, pageable);

		// 결과 검증
		assertNotNull(result);
		assertEquals(paginationData.getTotalElements(), result.getTotalElements());
		assertEquals(reviewList.size(), result.getContent().size());
		assertEquals(reviewList.get(0).getReviewContent(), result.getContent().get(0).getReviewContent());
		assertEquals(reviewList.get(0).getNickname(), result.getContent().get(0).getNickname());

		// reviewRepository 호출 검증
		verify(reviewRepository, times(1)).getReviewsForBook(bookId, pageable.getPageNumber(), pageable.getPageSize());
	}

	@Test
	@DisplayName("리뷰 좋아요/취소 토글 성공")
	void toggleReviewLike_success() {
		// 좋아요가 성공적으로 토글되었을 때 true 반환을 목킹
		when(reviewRepository.toggleReviewLike(reviewId)).thenReturn(true);

		boolean result = reviewService.toggleReviewLike(reviewId);

		assertTrue(result);
		verify(reviewRepository, times(1)).toggleReviewLike(reviewId);
	}

	@Test
	@DisplayName("리뷰 좋아요/취소 토글 실패")
	void toggleReviewLike_failure() {
		// 좋아요 토글이 실패했을 때 false 반환을 목킹
		when(reviewRepository.toggleReviewLike(reviewId)).thenReturn(false);

		boolean result = reviewService.toggleReviewLike(reviewId);

		assertFalse(result);
		verify(reviewRepository, times(1)).toggleReviewLike(reviewId);
	}
}