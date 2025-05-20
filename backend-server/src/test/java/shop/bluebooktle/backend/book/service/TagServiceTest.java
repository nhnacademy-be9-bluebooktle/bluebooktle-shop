package shop.bluebooktle.backend.book.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import shop.bluebooktle.backend.book.entity.Tag;
import shop.bluebooktle.backend.book.repository.BookTagRepository;
import shop.bluebooktle.backend.book.repository.TagRepository;
import shop.bluebooktle.backend.book.service.impl.TagServiceImpl;
import shop.bluebooktle.common.dto.book.request.TagRequest;
import shop.bluebooktle.common.dto.book.response.TagInfoResponse;
import shop.bluebooktle.common.exception.book.TagAlreadyExistsException;
import shop.bluebooktle.common.exception.book.TagNotFoundException;

@ExtendWith(MockitoExtension.class)
public class TagServiceTest {

	@InjectMocks
	private TagServiceImpl tagService;

	@Mock
	private TagRepository tagRepository;

	@Mock
	private BookTagRepository bookTagRepository;

	@Test
	@DisplayName("태그 등록 성공")
	void testRegisterTag_Success() {
		// Given: 태그의 이름이 중복되지 않는 상황
		TagRequest request = new TagRequest("New Tag");
		when(tagRepository.existsByName(request.getName())).thenReturn(false);

		// When: registerTag 메서드 호출
		tagService.registerTag(request);

		// Then: 태그 저장 메서드가 호출되었는지 확인
		verify(tagRepository, times(1)).save(any(Tag.class));
	}

	@Test
	@DisplayName("태그 등록 실패 - 이름 중복")
	void testRegisterTag_TagAlreadyExistsException() {
		// Given: 태그의 이름이 이미 존재하는 상황
		TagRequest request = new TagRequest("Duplicate Tag");
		when(tagRepository.existsByName(request.getName())).thenReturn(true);

		// When & Then: 예외 발생 확인
		assertThrows(TagAlreadyExistsException.class, () -> tagService.registerTag(request));
	}

	@Test
	@DisplayName("태그 수정 성공")
	void testUpdateTag_Success() {
		// Given: 특정 ID에 해당하는 태그가 존재하는 상황
		Long tagId = 1L;
		TagRequest request = new TagRequest("Updated Tag");
		Tag existingTag = new Tag(tagId, "Old Tag");
		when(tagRepository.findById(tagId)).thenReturn(Optional.of(existingTag));

		// When: updateTag 메서드 호출
		tagService.updateTag(tagId, request);

		// Then: 태그 이름이 업데이트되고 저장 메서드가 호출되었는지 확인
		assertEquals("Updated Tag", existingTag.getName());
		verify(tagRepository, times(1)).save(existingTag);
	}

	@Test
	@DisplayName("태그 수정 실패 - 태그 존재x")
	void testUpdateTag_TagNotFoundException() {
		// Given: 특정 ID에 태그가 존재하지 않는 상황
		Long tagId = 1L;
		TagRequest request = new TagRequest("Updated Tag");
		when(tagRepository.findById(tagId)).thenReturn(Optional.empty());

		// When & Then: 예외 발생 확인
		assertThrows(TagNotFoundException.class, () -> tagService.updateTag(tagId, request));
	}

	@Test
	@DisplayName("태그 조회 성공")
	void testGetTag_Success() {
		// Given: 특정 ID에 해당하는 태그가 존재하는 상황
		Long tagId = 1L;
		Tag tag = new Tag(tagId, "Tag Name");
		when(tagRepository.findById(tagId)).thenReturn(Optional.of(tag));

		// When: getTag 메서드 호출
		TagInfoResponse response = tagService.getTag(tagId);

		// Then: 반환값이 올바른지 확인
		assertNotNull(response);
		assertEquals(tagId, response.getId());
		assertEquals("Tag Name", response.getName());
	}

	@Test
	@DisplayName("태그 조회 실패 - 태그 존재x")
	void testGetTag_TagNotFoundException() {
		// Given: 특정 ID에 해당하는 태그가 존재하지 않는 상황
		Long tagId = 1L;
		when(tagRepository.findById(tagId)).thenReturn(Optional.empty());

		// When & Then: 예외 발생 확인
		assertThrows(TagNotFoundException.class, () -> tagService.getTag(tagId));
	}

	@Test
	@DisplayName("태그 목록 조회 성공")
	void testGetTags_Success() {
		// Given: 페이징된 태그 데이터를 반환하는 상황
		Pageable pageable = PageRequest.of(0, 10);
		Tag tag1 = new Tag(1L, "Tag 1");
		Tag tag2 = new Tag(2L, "Tag 2");
		List<Tag> tagList = Arrays.asList(tag1, tag2);
		Page<Tag> tagPage = new PageImpl<>(tagList, pageable, tagList.size());
		when(tagRepository.findAll(pageable)).thenReturn(tagPage);

		// When: getTags 메서드 호출
		Page<TagInfoResponse> responsePage = tagService.getTags(pageable);

		// Then: 반환된 Page 객체가 매핑된 데이터인지 확인
		assertNotNull(responsePage);
		assertEquals(2, responsePage.getContent().size());
		assertEquals("Tag 1", responsePage.getContent().get(0).getName());
		assertEquals("Tag 2", responsePage.getContent().get(1).getName());
	}

	@Test
	@DisplayName("태그 삭제 성공 - 관계데이터 bookTag도 삭제")
	void testDeleteTag_Success() {
		// Given: 특정 ID에 해당하는 태그가 존재하는 상황
		Long tagId = 1L;
		Tag tag = new Tag(tagId, "Tag Name");
		when(tagRepository.findById(tagId)).thenReturn(Optional.of(tag));

		// When: deleteTag 메서드 호출
		tagService.deleteTag(tagId);

		// Then: 관련 관계 데이터와 태그 자체가 삭제되었는지 확인
		verify(bookTagRepository, times(1)).deleteAllByTag(tag);
		verify(tagRepository, times(1)).delete(tag);
	}

	@Test
	@DisplayName("태그 삭제 실패 - 태그 존재x")
	void testDeleteTag_TagNotFoundException() {
		// Given: 특정 ID에 해당하는 태그가 존재하지 않는 상황
		Long tagId = 1L;
		when(tagRepository.findById(tagId)).thenReturn(Optional.empty());

		// When & Then: 예외 발생 확인
		assertThrows(TagNotFoundException.class, () -> tagService.deleteTag(tagId));
	}
}