package shop.bluebooktle.backend.book.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.bluebooktle.common.dto.book.request.PublisherRequest;
import shop.bluebooktle.common.dto.book.response.PublisherInfoResponse;

public interface PublisherService {

	// 출판사 등록
	void registerPublisher(PublisherRequest request);

	// 출판사 수정
	void updatePublisher(Long publisherId, PublisherRequest request);

	// 출판사 조회
	PublisherInfoResponse getPublisher(Long publisherId);

	// 출판사 목록 조회
	Page<PublisherInfoResponse> getPublishers(Pageable pageable);

	// 태그 키워드 조회
	Page<PublisherInfoResponse> searchPublishers(String searchKeyword, Pageable pageable);

	// 출판사 삭제
	void deletePublisher(Long publisherId);

	PublisherInfoResponse registerPublisherByName(String publisherName);
}
