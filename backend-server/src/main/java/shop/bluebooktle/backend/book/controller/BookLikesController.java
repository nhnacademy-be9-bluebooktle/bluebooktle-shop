package shop.bluebooktle.backend.book.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.dto.request.BookLikesRequest;
import shop.bluebooktle.backend.book.dto.response.BookLikesResponse;
import shop.bluebooktle.backend.book.service.BookLikesService;
import shop.bluebooktle.common.dto.common.JsendResponse;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookLikesController {
	private final BookLikesService bookLikesService;

	/** 도서 좋아요 등록 */
	@PostMapping("/{bookId}/likes")
	public JsendResponse<Void> likeBook(
		@PathVariable Long bookId,
		@RequestParam Long userId) {
		bookLikesService.like(BookLikesRequest.builder()
			.bookId(bookId)
			.userId(userId)
			.build());
		return JsendResponse.success();
	}

	/** 도서 좋아요 취소 */
	@DeleteMapping("/{bookId}/likes")
	public JsendResponse<Void> unlikeBook(
		@PathVariable Long bookId,
		@RequestParam Long userId) {
		bookLikesService.unlike(BookLikesRequest.builder()
			.bookId(bookId)
			.userId(userId)
			.build());
		return JsendResponse.success();
	}

	/** 로그인 사용자: 도서 좋아요 여부 확인 */
	@GetMapping("/{bookId}/likes/status")
	public JsendResponse<BookLikesResponse> isLiked(
		@PathVariable Long bookId,
		@RequestParam Long userId) {
		BookLikesResponse response = bookLikesService.isLiked(
			BookLikesRequest.builder()
				.bookId(bookId)
				.userId(userId)
				.build());
		return JsendResponse.success(response);
	}

	/** 로그인 관계없이 도서 좋아요 수 확인 */
	@GetMapping("/{bookId}/likes/count")
	public JsendResponse<BookLikesResponse> countLikes(@PathVariable Long bookId) {
		BookLikesResponse response = bookLikesService.countLikes(
			BookLikesRequest.builder()
				.bookId(bookId)
				.userId(0L)  // 로그인 사용자 아님을 명시적으로 표현
				.build());
		return JsendResponse.success(response);
	}

	/** 로그인 사용자: 좋아요한 도서 목록 조회 */
	@GetMapping("/likes")
	public JsendResponse<List<BookLikesResponse>> getBooksLiked(@RequestParam Long userId) {
		List<BookLikesResponse> response = bookLikesService.getBooksLikedByUser(
			BookLikesRequest.builder()
				.userId(userId)
				.build());
		return JsendResponse.success(response);
	}
}
