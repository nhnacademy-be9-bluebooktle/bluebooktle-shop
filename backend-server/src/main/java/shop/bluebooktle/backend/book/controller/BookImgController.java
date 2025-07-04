package shop.bluebooktle.backend.book.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.service.BookImgService;
import shop.bluebooktle.common.dto.book.response.img.ImgResponse;
import shop.bluebooktle.common.dto.common.JsendResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Tag(name = "도서 이미지 조회 API", description = "도서 이미지 조회 API")
public class BookImgController {
	private final BookImgService bookImgService;

	@Operation(summary = "도서 이미지 조회", description = "도서에 등록된 이미지를 조회합니다.")
	@GetMapping("/api/books/{book-id}/images")
	public ResponseEntity<JsendResponse<ImgResponse>> getImagesByBook(
		@PathVariable(name = "book-id") Long bookId
	) {
		ImgResponse images = bookImgService.getImgByBookId(bookId);
		return ResponseEntity
			.ok(JsendResponse.success(images));
	}

}
