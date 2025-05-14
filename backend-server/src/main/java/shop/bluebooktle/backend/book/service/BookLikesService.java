package shop.bluebooktle.backend.book.service;

import java.util.List;

import shop.bluebooktle.backend.book.dto.response.BookLikesResponse;

public interface BookLikesService {
	// 사용자가 도서 좋아요 누르기
	void like(Long bookId, Long userId);

	// 사용자가 누른 도서 좋아요 취소
	void unlike(Long bookId, Long userId);

	// 도서 좋아요 여부 확인
	BookLikesResponse isLiked(Long bookId, Long userId);

	// 도서 좋아요 수 확인
	BookLikesResponse countLikes(Long bookId);

	// 좋아요 누른 도서 조회
	List<BookLikesResponse> getBooksLikedByUser(Long userId);
}
