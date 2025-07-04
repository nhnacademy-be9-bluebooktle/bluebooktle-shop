package shop.bluebooktle.frontend.repository;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import shop.bluebooktle.common.dto.book.BookSortType;
import shop.bluebooktle.common.dto.book.response.BookDetailResponse;
import shop.bluebooktle.common.dto.book.response.BookInfoResponse;
import shop.bluebooktle.common.dto.book.response.BookLikesResponse;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.frontend.config.feign.FeignGlobalConfig;
import shop.bluebooktle.frontend.config.retry.RetryWithTokenRefresh;

@FeignClient(name = "backend-server", contextId = "bookRepository", path = "/api/books", configuration = FeignGlobalConfig.class)
public interface BookRepository {

	@GetMapping
	PaginationData<BookInfoResponse> searchBooks(
		@RequestParam("page") int page,
		@RequestParam("size") int size,
		@RequestParam(value = "searchKeyword", required = false) String searchKeyword,
		@RequestParam("bookSortType") BookSortType bookSortType
	);

	// 도서 상세 조회
	@GetMapping("/{bookId}")
	BookDetailResponse getBookDetail(@PathVariable("bookId") Long bookId);

	// 좋아요 등록
	@PostMapping("/{bookId}/likes")
	@RetryWithTokenRefresh
	Void likeBook(@PathVariable("bookId") Long bookId);

	// 좋아요 취소
	@DeleteMapping("/{bookId}/likes")
	@RetryWithTokenRefresh
	Void unlikeBook(@PathVariable("bookId") Long bookId);

	// 좋아요 여부 확인
	@GetMapping("/{bookId}/likes/status")
	@RetryWithTokenRefresh
	BookLikesResponse isLiked(@PathVariable("bookId") Long bookId);

	// 좋아요 수 확인
	@GetMapping("/{bookId}/likes/count")
	BookLikesResponse countLikes(@PathVariable("bookId") Long bookId);
}
