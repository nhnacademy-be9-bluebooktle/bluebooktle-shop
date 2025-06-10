package shop.bluebooktle.backend.book.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.service.BookImgService;
import shop.bluebooktle.common.dto.book.response.img.ImgResponse;
import shop.bluebooktle.common.dto.common.JsendResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class BookImgController {
	private final BookImgService bookImgService;

	@GetMapping("/api/books/{book-id}/images")
	public ResponseEntity<JsendResponse<ImgResponse>> getImagesByBook(
		@PathVariable(name = "book-id") Long bookId
	) {
		ImgResponse images = bookImgService.getImgByBookId(bookId);
		return ResponseEntity
			.ok(JsendResponse.success(images));
	}
	
}
