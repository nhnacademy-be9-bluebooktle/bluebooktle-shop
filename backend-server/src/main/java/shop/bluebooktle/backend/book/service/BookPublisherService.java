package shop.bluebooktle.backend.book.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.bluebooktle.common.dto.book.response.BookInfoResponse;
import shop.bluebooktle.common.dto.book.response.PublisherInfoResponse;

public interface BookPublisherService {

	// 도서 출판사 등록
	void registerBookPublisher(Long bookId, Long publisherId);

	void registerBookPublisher(Long bookId, List<Long> publisherIdList);

	void updateBookPublisher(Long bookId, List<Long> publisherIdList);

	// 도서 출판사 삭제
	void deleteBookPublisher(Long bookId, Long publisherId);

	// 특정 도서의 출판사 목록 조회
	List<PublisherInfoResponse> getPublishersByBookId(Long bookId);

	// 특정 출판사 안에 등록된 도서 조회
	Page<BookInfoResponse> searchBooksByPublisher(Long publisherId, Pageable pageable);
}
