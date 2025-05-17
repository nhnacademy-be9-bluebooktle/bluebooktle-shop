package shop.bluebooktle.backend.book.service;

import shop.bluebooktle.backend.book.dto.request.author.AuthorRegisterRequest;
import shop.bluebooktle.backend.book.dto.request.author.AuthorUpdateRequest;
import shop.bluebooktle.backend.book.dto.response.author.AuthorResponse;

public interface AuthorService {

	// 작가 생성
	void registerAuthor(AuthorRegisterRequest authorRegisterRequest);

	// 작가 조회
	AuthorResponse getAuthor(Long authorId);

	// 작가 수정
	void updateAuthor(Long authorId, AuthorUpdateRequest authorUpdateRequest);

	// 작가 삭제
	void deleteAuthor(Long authorId);

}
