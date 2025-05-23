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

		try {
			PaginationData<TagInfoResponse> data = tagRepository.getTags(page, size, keyword);
			List<TagInfoResponse> tags = data.getContent();
			return new PageImpl<>(tags, pageable, data.getTotalElements());
		} catch (Exception e) {
			throw new TagListFetchException();
		}
	}

	@Override
	public TagInfoResponse getTag(Long id) {
		try {
			return tagRepository.getTag(id);
		} catch (Exception e) {
			throw new TagNotFoundException();
		}
	}

	@Override
	public void createTag(TagRequest request) {
		try {
			tagRepository.createTag(request);
		} catch (Exception e) {
			throw new TagCreateException(e);
		}
	}

	@Override
	public void updateTag(Long id, TagRequest request) {
		try {
			tagRepository.updateTag(id, request); // 실제 데이터 수정 (name만 수정)
		} catch (Exception e) {
			throw new TagUpdateException();
		}
	}

	@Override
	public void deleteTag(Long id) {
		try {
			tagRepository.deleteTag(id);
		} catch (Exception e) {
			throw new TagDeleteException(e);
		}
	}
}
