package shop.bluebooktle.backend.book.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.bluebooktle.backend.book.dto.response.BookInfoResponse;
import shop.bluebooktle.backend.book.dto.response.TagInfoResponse;

public interface BookTagService {
	// 도서 출판사 등록
	void registerBookTag(Long bookId, Long tagId);

	// 도서 출판사 삭제
	void deleteBookTag(Long bookId, Long tagId);

	// 특정 도서의 출판사 목록 조회
	List<TagInfoResponse> getTagsByBookId(Long bookId);

	// 특정 출판사 안에 등록된 도서 조회
	Page<BookInfoResponse> searchBooksByTag(Long tagId, Pageable pageable);
}
