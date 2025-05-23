package shop.bluebooktle.backend.book.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.bluebooktle.common.dto.book.request.author.AuthorRegisterRequest;
import shop.bluebooktle.common.dto.book.request.author.AuthorUpdateRequest;
import shop.bluebooktle.common.dto.book.response.author.AuthorResponse;

public interface AuthorService {

	// 작가 생성
	void registerAuthor(AuthorRegisterRequest authorRegisterRequest);

	// (작가 아이디로) 작가 조회
	AuthorResponse getAuthor(Long authorId);

	// (작가 아이디로) 작가 수정
	void updateAuthor(Long authorId, AuthorUpdateRequest authorUpdateRequest);

	// (작가 아이디로) 작가 삭제
	void deleteAuthor(Long authorId);

	// 작가 이름으로 작가 조회 및 저장 로직
	AuthorResponse registerAuthorByName(String authorName);

	// 작가 목록 조회
	Page<AuthorResponse> getAuthors(Pageable pageable);

	// 작가 키워드 조회
	Page<AuthorResponse> searchAuthors(String searchKeyword, Pageable pageable);

}
