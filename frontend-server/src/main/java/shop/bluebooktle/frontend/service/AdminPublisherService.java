package shop.bluebooktle.frontend.service;

import org.springframework.data.domain.Page;

import shop.bluebooktle.common.dto.book.request.PublisherRequest;
import shop.bluebooktle.common.dto.book.response.PublisherInfoResponse;

public interface AdminPublisherService {
	// 출판사 목록 조회
	Page<PublisherInfoResponse> getPublishers(int page, int size, String searchKeyword);

	// 단일 출판사 조회
	PublisherInfoResponse getPublisher(Long id);

	// 출판사 등록
	void createPublisher(PublisherRequest request);

	// 출판사 수정
	void updatePublisher(Long id, PublisherRequest request);

	// 출판사 삭제
	void deletePublisher(Long id);
}
