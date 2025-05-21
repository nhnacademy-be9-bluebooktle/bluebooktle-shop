package shop.bluebooktle.frontend.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.common.dto.book.request.TagRequest;
import shop.bluebooktle.common.dto.book.response.TagInfoResponse;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.exception.book.TagCreateException;
import shop.bluebooktle.common.exception.book.TagDeleteException;
import shop.bluebooktle.common.exception.book.TagListFetchException;
import shop.bluebooktle.common.exception.book.TagNotFoundException;
import shop.bluebooktle.common.exception.book.TagUpdateException;
import shop.bluebooktle.frontend.repository.AdminTagRepository;
import shop.bluebooktle.frontend.service.AdminTagService;

@Service
@RequiredArgsConstructor
public class AdminTagServiceImpl implements AdminTagService {
	private final AdminTagRepository tagRepository;

	@Override
	public Page<TagInfoResponse> getTags(int page, int size, String searchKeyword) {
		Pageable pageable = PageRequest.of(page, size); // 페이징 정보를 Spring Data Pageable 객체로 변환해 주는 작업

		String keyword = null; // 빈 문자열은 null로 간주
		if (searchKeyword != null && !searchKeyword.isBlank()) {
			keyword = searchKeyword;
		}

		JsendResponse<PaginationData<TagInfoResponse>> response = tagRepository.getTags(page, size, keyword);

		if (!"success".equalsIgnoreCase(response.status())) { // JSend 상태가 success 가 아니면 태그 목록 조회 예외를 던짐
			throw new TagListFetchException();
		}
		// Feign 으로 받은 JSend 형태의 페이징 응답 -> Spring Data Page<T> 객체로 변환해 주는 작업
		PaginationData<TagInfoResponse> data = response.data(); // 페이징된 태그 목록과 객체 꺼냄
		List<TagInfoResponse> tags = data.getContent();
		return new PageImpl<>(tags, pageable, data.getTotalElements());
	}

	@Override
	public TagInfoResponse getTag(Long id) {
		JsendResponse<TagInfoResponse> response = tagRepository.getTag(id);
		if (!"success".equalsIgnoreCase(response.status())) {
			throw new TagNotFoundException();
		}
		return response.data();
	}

	@Override
	public void createTag(TagRequest request) {
		JsendResponse<Void> response = tagRepository.createTag(request);
		if (!"success".equalsIgnoreCase(response.status())) { // JSend 상태가 success 가 아니면 예외를 던짐
			throw new TagCreateException();
		}
	}

	@Override
	public void updateTag(Long id, TagRequest request) {
		JsendResponse<Void> response = tagRepository.updateTag(id, request);
		if (!"success".equalsIgnoreCase(response.status())) { // JSend 상태가 success 가 아니면 예외를 던짐
			throw new TagUpdateException();
		}
	}

	@Override
	public void deleteTag(Long id) {
		JsendResponse<Void> response = tagRepository.deleteTag(id);
		if (!"success".equalsIgnoreCase(response.status())) { // JSend 상태가 success 가 아니면 예외를 던짐
			throw new TagDeleteException();
		}
	}
}
