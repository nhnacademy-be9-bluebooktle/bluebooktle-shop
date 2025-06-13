package shop.bluebooktle.frontend.repository;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import shop.bluebooktle.common.dto.book.response.BookLikesListResponse;
import shop.bluebooktle.frontend.config.feign.FeignGlobalConfig;
import shop.bluebooktle.frontend.config.retry.RetryWithTokenRefresh;

/** 백엔드의 /api/tags REST 엔드포인트를 호출하는 HTTP 클라이언트 */
@FeignClient(url = "${server.gateway-url}", name = "MyPageBookLikesRepository", path = "/api/books", configuration = FeignGlobalConfig.class)
public interface MyPageBookLikesRepository {
	// 좋아요 목록 조회
	@GetMapping("/likes")
	@RetryWithTokenRefresh
	List<BookLikesListResponse> getMyPageBookLikes();

	// 도서 좋아요 해제
	@DeleteMapping("{bookId}/likes")
	@RetryWithTokenRefresh
	void unlike(
		@PathVariable("bookId") Long bookId
	);
}
