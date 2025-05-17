package shop.bluebooktle.backend.book.service;

import java.util.List;

import shop.bluebooktle.backend.book.dto.response.BookInfoResponse;
import shop.bluebooktle.backend.book.dto.response.author.AuthorResponse;

public interface BookAuthorService {

	// 도서 작가 등록
	void registerBookAuthor(Long bookId, Long authorId);

	// 특정 도서의 모든 작가 목록 조회
	List<AuthorResponse> getAuthorByBookId(Long bookId);

	// 특정 작가의 모든 도서 목록 조회
	List<BookInfoResponse> getBookByAuthorId(Long AuthorId);

	// 도서 작가 삭제
	void deleteBookAuthor(Long bookId, Long authorId);

}
