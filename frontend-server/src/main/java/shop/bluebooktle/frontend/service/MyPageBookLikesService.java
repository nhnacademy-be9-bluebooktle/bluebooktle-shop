package shop.bluebooktle.frontend.service;

import shop.bluebooktle.common.dto.book.response.BookLikesListResponse;
import shop.bluebooktle.common.dto.common.PaginationData;

public interface MyPageBookLikesService {
	// 사용자의 좋아요 목록 조회
	PaginationData<BookLikesListResponse> getMyPageBookLikes(int page, int size);

	// 도서 좋아요 삭제
	void unlike(Long bookId);
}
