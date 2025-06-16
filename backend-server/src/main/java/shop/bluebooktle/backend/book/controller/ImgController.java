package shop.bluebooktle.backend.book.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.service.ImgService;
import shop.bluebooktle.backend.book.service.MinioService;
import shop.bluebooktle.common.domain.auth.UserType;
import shop.bluebooktle.common.dto.book.response.img.ImgResponse;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.security.Auth;

@RestController
@RequestMapping("/api/imgs")
@RequiredArgsConstructor
@Tag(name = "이미지 API", description = "MinIO 서버 관련 이미지 API")
public class ImgController {
	private final ImgService imgService;
	private final MinioService minioService;

	// TODO: 본인이 등록한 이미지인지 확인하는 로직 필요(delete review image, book image 구분해서 검증해야 할 듯)
	@Operation(summary = "이미지 삭제", description = "MinIO 서버에 등록된 해당 이미지를 삭제합니다.")
	@DeleteMapping("/{image-id}")
	@Auth(type = UserType.USER)
	public JsendResponse<Void> deleteImg(
		@PathVariable(name = "image-id") Long imgId) {
		imgService.deleteImg(imgId);
		return JsendResponse.success();
	}

	@Operation(
		summary = "MinIO 서버 Presigned URL 발급",
		description = "MinIO 서버에 이미지 업로드를 위한 Presigned URL을 발급합니다."
	)
	@GetMapping("/presignedUploadUrl")
	@Auth(type = UserType.USER)
	public ResponseEntity<JsendResponse<String>> getPresignedUploadUrl(@RequestParam String fileName) {
		String presignedUrl = minioService.getPresignedUploadUrl(fileName);
		return ResponseEntity.ok(JsendResponse.success(presignedUrl));
	}

	@Operation(
		summary = "MinIO 서버 이미지 삭제",
		description = "MinIO 서버에 등록된 이미지 파일을 삭제합니다."
	)
	@DeleteMapping("/minioUrl")
	@Auth(type = UserType.ADMIN)
	public ResponseEntity<JsendResponse<Void>> deleteImage(@RequestParam String fileName) {
		minioService.deleteImage(fileName);
		return ResponseEntity.ok(JsendResponse.success());
	}

	@Operation(
		summary = "리뷰 이미지 조회",
		description = "MinIO 서버에 등록된 리뷰 이미지 파일을 조회합니다."
	)
	@GetMapping("/by-review/{review-id}")
	public JsendResponse<ImgResponse> getImgByReviewId(
		@PathVariable(name = "review-id") Long reviewId
	) {
		ImgResponse imgResponse = imgService.getImgByReviewId(reviewId);
		return JsendResponse.success(imgResponse);
	}
}
