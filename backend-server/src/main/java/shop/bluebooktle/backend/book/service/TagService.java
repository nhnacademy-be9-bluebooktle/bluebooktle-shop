package shop.bluebooktle.backend.book.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.bluebooktle.backend.book.dto.request.TagRequest;
import shop.bluebooktle.backend.book.dto.response.TagInfoResponse;

public interface TagService {

	// 태그 등록
	void registerTag(TagRequest request);

	// 태그 수정
	void updateTag(Long tagId, TagRequest request);

	// 태그 조회
	TagInfoResponse getTag(Long tagId);

	// 태그 목록 조회
	Page<TagInfoResponse> getTags(Pageable pageable);

	// 태그 삭제
	void deleteTag(Long tagId);
}
