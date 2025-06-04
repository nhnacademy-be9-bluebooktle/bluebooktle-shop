package shop.bluebooktle.backend.book.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.service.ImgService;
import shop.bluebooktle.backend.book.service.MinioService;
import shop.bluebooktle.common.dto.book.request.img.ImgRegisterRequest;
import shop.bluebooktle.common.dto.book.request.img.ImgUpdateRequest;
import shop.bluebooktle.common.dto.book.response.img.ImgResponse;
import shop.bluebooktle.common.dto.common.JsendResponse;

@RestController
@RequestMapping("/api/imgs")
@RequiredArgsConstructor
public class ImgController {
	private final ImgService imgService;
	private final MinioService minioService;

	// 이미지 생성
	@PostMapping
	public JsendResponse<Void> registerImg(
		@Valid @RequestBody ImgRegisterRequest imgRegisterRequest
	) {
		imgService.registerImg(imgRegisterRequest);
		return JsendResponse.success();
	}

	// 이미지 조회
	@GetMapping("/{id}")
	public JsendResponse<ImgResponse> getImg(
		@PathVariable Long id
	) {
		ImgResponse imgResponse = imgService.getImg(id);
		return JsendResponse.success(imgResponse);
	}

	// 이미지 수정
	@PutMapping("/{id}")
	public JsendResponse<Void> updateImg(
		@PathVariable Long id,
		@Valid @RequestBody ImgUpdateRequest imgUpdateRequest
	) {
		imgService.updateImg(id, imgUpdateRequest);
		return JsendResponse.success();
	}

	// 이미지 삭제
	@DeleteMapping("/{id}")
	public JsendResponse<Void> deleteImg(
		@PathVariable Long id
	) {
		imgService.deleteImg(id);
		return JsendResponse.success();
	}

	//minio 이미지 업로드 Presigned URL 발급 API
	@GetMapping("/presignedUploadUrl")
	public ResponseEntity<JsendResponse<String>> getPresignedUploadUrl(@RequestParam String fileName) {
		String presignedUrl = minioService.getPresignedUploadUrl(fileName);
		return ResponseEntity.ok(JsendResponse.success(presignedUrl));
	}

	@DeleteMapping("/minioUrl")
	public ResponseEntity<JsendResponse<Void>> deleteMinioUrl(@RequestParam String fileName) {
		minioService.deleteMinioBucket(fileName);
		return ResponseEntity.ok(JsendResponse.success());
	}
}
