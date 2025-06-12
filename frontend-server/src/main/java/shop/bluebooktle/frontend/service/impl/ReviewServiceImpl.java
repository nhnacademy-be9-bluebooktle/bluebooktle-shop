package shop.bluebooktle.frontend.service.impl;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.common.dto.book.response.img.ImgResponse;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.review.request.ReviewRegisterRequest;
import shop.bluebooktle.common.dto.review.request.ReviewRequest;
import shop.bluebooktle.common.dto.review.request.ReviewUpdateRequest;
import shop.bluebooktle.common.dto.review.response.ReviewResponse;
import shop.bluebooktle.frontend.repository.ImageServerClient;
import shop.bluebooktle.frontend.repository.ImgRepository;
import shop.bluebooktle.frontend.repository.ReviewRepository;
import shop.bluebooktle.frontend.service.AdminImgService;
import shop.bluebooktle.frontend.service.ReviewService;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
	private final ReviewRepository reviewRepository;
	private final AdminImgService adminImgService;
	private final ImageServerClient imageServerClient;
	private final ImgRepository imgRepository;

	// 리뷰작성
	@Override
	public void addReview(Long userId, Long bookOrderId, ReviewRequest reviewRequest) {
		List<String> uploadedUrls = new ArrayList<>();

		if (reviewRequest.getImageFiles() != null && !reviewRequest.getImageFiles().isEmpty()) {
			for (MultipartFile file : reviewRequest.getImageFiles()) {
				if (!file.isEmpty()) {
					String uploadedMinioUrl = uploadToMinio(file);
					String imageKey = uploadedMinioUrl.substring(uploadedMinioUrl.indexOf("/bluebooktle-bookimage"));
					uploadedUrls.add("/images" + imageKey);
				}
			}
		}

		ReviewRegisterRequest backendRequest = ReviewRegisterRequest.builder()
			.star(reviewRequest.getStar())
			.reviewContent(reviewRequest.getReviewContent())
			.imgUrls(uploadedUrls)
			.build();

		reviewRepository.addReview(bookOrderId, backendRequest);
	}

	@Override
	public void updateReivew(Long reviewId, ReviewRequest reviewRequest) {
		List<String> finalImgUrlsForRequest = new ArrayList<>();

		if (reviewRequest.getImageFiles() != null && !reviewRequest.getImageFiles().isEmpty()) {
			ImgResponse existingImg = imgRepository.getImageByReviewId(reviewId);
			if (existingImg != null && existingImg.getImgUrl() != null && existingImg.getImgUrl()
				.contains("/bluebooktle-bookimage")) {
				String oldImageUrl = existingImg.getImgUrl();
				String oldObjectName = oldImageUrl.substring(oldImageUrl.lastIndexOf("/") + 1);

				adminImgService.deleteImage(oldObjectName);
				log.info("기존 리뷰 이미지 미니오에서 삭제: {}", oldObjectName);

			}

			for (MultipartFile file : reviewRequest.getImageFiles()) {
				if (!file.isEmpty()) {
					String uploadedMinioUrl = uploadToMinio(file);
					String imageKey = uploadedMinioUrl.substring(uploadedMinioUrl.indexOf("/bluebooktle-bookimage"));
					finalImgUrlsForRequest.add("/images" + imageKey);
				}
			}
		}

		ReviewUpdateRequest backendRequest = ReviewUpdateRequest.builder()
			.star(reviewRequest.getStar())
			.reviewContent(reviewRequest.getReviewContent())
			.imgUrls(finalImgUrlsForRequest)
			.build();

		reviewRepository.updateReview(reviewId, backendRequest);
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

	// 도서상세페이지 리뷰 목록 조회
	@Override
	public Page<ReviewResponse> getReviewsForBook(Long bookId, Pageable pageable) {
		PaginationData<ReviewResponse> paginationData =
			reviewRepository.getReviewsForBook(bookId, pageable.getPageNumber(), pageable.getPageSize());

		List<ReviewResponse> content = paginationData.getContent();
		long total = paginationData.getTotalElements();

		return new PageImpl<>(content, pageable, total);

	}

	@Override
	public boolean toggleReviewLike(Long reviewId) {
		return reviewRepository.toggleReviewLike(reviewId);
	}

	private String uploadToMinio(MultipartFile file) {
		String presignedUploadUrl = adminImgService.getPresignedUploadUrl();
		URI uri = URI.create(presignedUploadUrl);

		// 1) path 분해
		String[] segments = uri.getPath().split("/", 3);
		String bucket = segments[1];
		String objectName = segments[2];

		// 2) 쿼리 파라미터 분해
		Map<String, String> queryParams = Arrays.stream(uri.getQuery().split("&"))
			.map(s -> s.split("=", 2))
			.collect(Collectors.toMap(
				kv -> URLDecoder.decode(kv[0], StandardCharsets.UTF_8),
				kv -> URLDecoder.decode(kv[1], StandardCharsets.UTF_8)
			));
		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", file.getContentType());

		try {
			imageServerClient.upload(
				bucket,
				objectName,
				queryParams,
				headers,
				file.getBytes()
			);
		} catch (IOException e) {
			throw new UncheckedIOException("MinIO 업로드 실패", e);
		}

		log.info("업로드 완료 → URL: {}", presignedUploadUrl.split("\\?")[0]);
		return presignedUploadUrl.split("\\?")[0]; // 순수 URL만 리턴
	}
}
