package shop.bluebooktle.backend.book.service;

import java.util.List;

import shop.bluebooktle.common.dto.book.response.PublisherInfoResponse;

public interface BookPublisherService {

	// 도서 출판사 등록
	PublisherInfoResponse registerBookPublisher(Long bookId, Long publisherId);

	List<PublisherInfoResponse> registerBookPublisher(Long bookId, List<Long> publisherIdList);

	List<PublisherInfoResponse> updateBookPublisher(Long bookId, List<Long> publisherIdList);
}
