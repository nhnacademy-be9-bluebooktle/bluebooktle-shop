package shop.bluebooktle.frontend.service;

import org.springframework.data.domain.Page;

import shop.bluebooktle.common.dto.book.request.TagRequest;
import shop.bluebooktle.common.dto.book.response.TagInfoResponse;

public interface AdminTagService {

	// 태그 목록 조회
	Page<TagInfoResponse> getTags(int page, int size, String searchKeyword);

	// 단일 태그 조회
	TagInfoResponse getTag(Long id);

	// 태그 등록
	void createTag(TagRequest request);

	// 태그 수정 (태그명 수정)
	void updateTag(Long id, TagRequest request);

	// 태그 삭제
	void deleteTag(Long id);
}
