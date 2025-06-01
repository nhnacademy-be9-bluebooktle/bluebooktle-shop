package shop.bluebooktle.backend.book.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.service.BookLikesService;
import shop.bluebooktle.common.domain.auth.UserType;
import shop.bluebooktle.common.dto.book.response.BookLikesListResponse;
import shop.bluebooktle.common.dto.book.response.BookLikesResponse;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.security.Auth;
import shop.bluebooktle.common.security.UserPrincipal;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookLikesController {
	private final BookLikesService bookLikesService;

	@PostMapping("/{bookId}/likes")
	@Operation(summary = "도서 좋아요 등록", description = "로그인 사용자가 도서를 좋아요 누릅니다.")
	@Auth(type = UserType.USER)
	public ResponseEntity<JsendResponse<Void>> likeBook(
		@PathVariable Long bookId,
		@Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal) {
		bookLikesService.like(bookId, userPrincipal.getUserId());
		return ResponseEntity.status(HttpStatus.CREATED).body(JsendResponse.success());
	}

	@DeleteMapping("/{bookId}/likes")
	@Operation(summary = "도서 좋아요 취소", description = "로그인 사용자가 도서를 좋아요 취소합니다.")
	@Auth(type = UserType.USER)
	public ResponseEntity<JsendResponse<Void>> unlikeBook(
		@PathVariable Long bookId,
		@Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal) {
		bookLikesService.unlike(bookId, userPrincipal.getUserId());
		return ResponseEntity.ok(JsendResponse.success());
	}

	@GetMapping("/{bookId}/likes/status")
	@Operation(summary = "도서 좋아요 여부 확인", description = "로그인 사용자가 도서 좋아요 누른 여부를 확인합니다.")
	@Auth(type = UserType.USER)
	public ResponseEntity<JsendResponse<BookLikesResponse>> isLiked(
		@PathVariable Long bookId,
		@Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal) {
		BookLikesResponse response = bookLikesService.isLiked(bookId, userPrincipal.getUserId());
		return ResponseEntity.ok(JsendResponse.success(response));
	}

	/** 로그인 관계없이 도서 좋아요 수 확인 */
	@GetMapping("/{bookId}/likes/count")
	public ResponseEntity<JsendResponse<BookLikesResponse>> countLikes(
		@PathVariable Long bookId) {
		BookLikesResponse response = bookLikesService.countLikes(bookId);
		return ResponseEntity.ok(JsendResponse.success(response));
	}

	@Operation(summary = "좋아요 누른 도서 목록 조회", description = "로그인 사용자가 좋아요한 도서 목록을 조회합니다.")
	@Auth(type = UserType.USER)
	@GetMapping("/likes")
	public ResponseEntity<JsendResponse<List<BookLikesListResponse>>> getBooksLiked(
		@Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal) {
		List<BookLikesListResponse> response = bookLikesService.getBooksLikedByUser(userPrincipal);
		return ResponseEntity.ok(JsendResponse.success(response));
	}
}
