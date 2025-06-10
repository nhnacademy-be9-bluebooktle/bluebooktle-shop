package shop.bluebooktle.backend.book.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.service.BookImgService;
import shop.bluebooktle.common.dto.book.request.BookImgRegisterRequest;
import shop.bluebooktle.common.dto.book.response.BookImgResponse;
import shop.bluebooktle.common.dto.book.response.img.ImgResponse;
import shop.bluebooktle.common.dto.common.JsendResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class BookImgController {
	private final BookImgService bookImgService;

	@PostMapping("/api/books/{bookId}/images")
	public ResponseEntity<JsendResponse<Void>> registerBookImg(
		@PathVariable Long bookId,
		@Valid @RequestBody BookImgRegisterRequest req
	) {
		bookImgService.registerBookImg(bookId, req);
		return ResponseEntity
			.status(HttpStatus.CREATED)
			.body(JsendResponse.success());
	}

	@GetMapping("/api/books/{bookId}/images")
	public ResponseEntity<JsendResponse<ImgResponse>> getImagesByBook(
		@PathVariable Long bookId
	) {
		ImgResponse images = bookImgService.getImgByBookId(bookId);
		return ResponseEntity
			.ok(JsendResponse.success(images));
	}

	@GetMapping("/api/books/{bookId}/images/thumbnail")
	public ResponseEntity<JsendResponse<BookImgResponse>> getThumbnailByBook(
		@PathVariable Long bookId
	) {
		Optional<BookImgResponse> thumbnail = bookImgService.getThumbnailByBookId(bookId);
		if (thumbnail.isPresent()) {
			return ResponseEntity
				.ok(JsendResponse.success(thumbnail.get()));
		} else {

			return ResponseEntity
				.ok(JsendResponse.success(null));
		}
	}

	@DeleteMapping("/api/books/{bookId}/images/{imgId}")
	public ResponseEntity<JsendResponse<Void>> deleteBookImg(
		@PathVariable Long bookId,
		@PathVariable Long imgId
	) {
		bookImgService.deleteBookImg(bookId, imgId);
		return ResponseEntity
			.status(HttpStatus.NO_CONTENT)
			.body(JsendResponse.success());
	}
}
