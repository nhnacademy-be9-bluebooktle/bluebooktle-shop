package shop.bluebooktle.backend.book.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.bluebooktle.backend.book.dto.request.PublisherRegisterRequest;
import shop.bluebooktle.backend.book.dto.request.PublisherUpdateRequest;
import shop.bluebooktle.backend.book.dto.response.PublisherInfoResponse;

public interface PublisherService {

	// 출판사 등록
	void addPublisher(PublisherRegisterRequest request);

	// 출판사 수정
	void updatePublisher(PublisherUpdateRequest request);

	// 출판사 조회
	PublisherInfoResponse getPublisher(Long publisherId);

	// 출판사 목록 조회
	Page<PublisherInfoResponse> getPublishers(Pageable pageable);

	// 출판사 삭제
	void deletePublisher(Long publisherId);

}
