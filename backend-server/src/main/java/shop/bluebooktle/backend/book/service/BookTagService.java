package shop.bluebooktle.backend.book.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.bluebooktle.backend.book.dto.response.BookInfoResponse;
import shop.bluebooktle.backend.book.dto.response.TagInfoResponse;

public interface BookTagService {
	// 도서 태그 등록
	void registerBookTag(Long tagId, Long bookId);

	// 도서 태그 삭제
	void deleteBookTag(Long tagId, Long bookId);

	// 특정 도서의 태그 목록 조회
	List<TagInfoResponse> getTagsByBookId(Long bookId);

	// 특정 태그 안에 등록된 도서 조회
	Page<BookInfoResponse> searchBooksByTag(Long tagId, Pageable pageable);
}
