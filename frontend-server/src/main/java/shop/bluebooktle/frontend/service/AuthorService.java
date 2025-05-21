package shop.bluebooktle.frontend.service;

import org.springframework.data.domain.Page;

import shop.bluebooktle.common.dto.book.request.author.AuthorRegisterRequest;
import shop.bluebooktle.common.dto.book.request.author.AuthorUpdateRequest;
import shop.bluebooktle.common.dto.book.response.author.AuthorResponse;

public interface AuthorService {
	// 작가 리스트 조회
	Page<AuthorResponse> getAuthors(int page, int size, String searchKeyword);

	// 작가 조회
	AuthorResponse getAuthor(Long id);

	// 작가 생성
	void addAuthor(AuthorRegisterRequest request);

	// 작가 수정
	void updateAuthor(Long authorId, AuthorUpdateRequest request);

	// 작가 삭제
	void deleteAuthor(Long authorId);
}
