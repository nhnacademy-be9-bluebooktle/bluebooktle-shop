package shop.bluebooktle.frontend.service;

import org.springframework.data.domain.Page;

import shop.bluebooktle.common.dto.book.response.BookLikesListResponse;

public interface MyPageBookLikesService {
	// 사용자의 좋아요 목록 조회
	Page<BookLikesListResponse> getMyPageBookLikes(int page, int size);

	// 도서 좋아요 삭제
	void unlike(Long bookId);
}
