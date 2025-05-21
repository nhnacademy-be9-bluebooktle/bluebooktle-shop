package shop.bluebooktle.backend.book.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.entity.Tag;
import shop.bluebooktle.backend.book.repository.BookTagRepository;
import shop.bluebooktle.backend.book.repository.TagRepository;
import shop.bluebooktle.backend.book.service.TagService;
import shop.bluebooktle.common.dto.book.request.TagRequest;
import shop.bluebooktle.common.dto.book.response.TagInfoResponse;
import shop.bluebooktle.common.exception.book.TagAlreadyExistsException;
import shop.bluebooktle.common.exception.book.TagNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional
public class TagServiceImpl implements TagService {

	private final TagRepository tagRepository;
	private final BookTagRepository bookTagRepository;

	@Override
	public void registerTag(TagRequest request) {
		if (tagRepository.existsByName(request.getName())) {
			throw new TagAlreadyExistsException("태그명 : " + request.getName());
		}

		Tag tag = Tag.builder()
			.name(request.getName())
			.build();
		tagRepository.save(tag);
	}

	@Override
	public void updateTag(Long tagId, TagRequest request) {
		Tag tag = tagRepository.findById(tagId)
			.orElseThrow(() -> new TagNotFoundException(tagId));
		tag.setName(request.getName());
		tagRepository.save(tag);
	}

	@Override
	@Transactional(readOnly = true)
	public TagInfoResponse getTag(Long publisherId) {
		Tag tag = tagRepository.findById(publisherId)
			.orElseThrow(() -> new TagNotFoundException(publisherId));
		return new TagInfoResponse(tag.getId(), tag.getName(), tag.getCreatedAt());
	}

	@Override
	@Transactional(readOnly = true)
	public Page<TagInfoResponse> getTags(Pageable pageable) {
		Page<Tag> tags = tagRepository.findAllByDeletedAtIsNull(pageable);
		return tags.map(tag -> new TagInfoResponse(tag.getId(), tag.getName(), tag.getCreatedAt()));
	}

	@Override
	public void deleteTag(Long tagId) {
		Tag tag = tagRepository.findById(tagId)
			.orElseThrow(() -> new TagNotFoundException(tagId));
		// 도서태그 관계테이블 삭제
		bookTagRepository.deleteAllByTag(tag);
		// 태그 삭제
		tagRepository.delete(tag);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<TagInfoResponse> searchTags(String searchKeyword, Pageable pageable) {
		Page<Tag> tags = tagRepository.searchByNameContaining(searchKeyword, pageable);
		return tags.map(tag -> new TagInfoResponse(tag.getId(), tag.getName(), tag.getCreatedAt()));
	}
}
