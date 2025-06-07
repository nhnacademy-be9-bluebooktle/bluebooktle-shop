package shop.bluebooktle.backend.book.service;

import java.util.List;

import shop.bluebooktle.common.dto.book.response.BookInfoResponse;
import shop.bluebooktle.common.dto.book.response.author.AuthorResponse;

public interface BookAuthorService {

	// 도서 작가 등록
	AuthorResponse registerBookAuthor(Long bookId, Long authorId);

	List<AuthorResponse> registerBookAuthor(Long bookId, List<Long> authorIdList);

	void updateBookAuthor(Long bookId, List<Long> authorIdList);

	// 특정 도서의 모든 작가 목록 조회
	List<AuthorResponse> getAuthorByBookId(Long bookId);

	// 특정 작가의 모든 도서 목록 조회
	List<BookInfoResponse> getBookByAuthorId(Long AuthorId);

	// 도서 작가 삭제
	void deleteBookAuthor(Long bookId, Long authorId);

}
