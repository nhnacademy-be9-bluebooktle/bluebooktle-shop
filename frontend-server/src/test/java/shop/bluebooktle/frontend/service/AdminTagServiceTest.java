package shop.bluebooktle.frontend.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import shop.bluebooktle.common.dto.book.request.TagRequest;
import shop.bluebooktle.common.dto.book.response.TagInfoResponse;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.exception.book.TagCreateException;
import shop.bluebooktle.common.exception.book.TagDeleteException;
import shop.bluebooktle.common.exception.book.TagListFetchException;
import shop.bluebooktle.common.exception.book.TagNotFoundException;
import shop.bluebooktle.common.exception.book.TagUpdateException;
import shop.bluebooktle.frontend.repository.AdminTagRepository;
import shop.bluebooktle.frontend.service.impl.AdminTagServiceImpl;

@ExtendWith(MockitoExtension.class)
class AdminTagServiceTest {
	@Mock
	private AdminTagRepository tagRepository;

	@InjectMocks
	private AdminTagServiceImpl tagService;

	@Test
	@DisplayName("태그 목록 조회 성공")
	void getTags_success() {
		// given
		int page = 0, size = 10;
		String keyword = "힐링";
		List<TagInfoResponse> tags = List.of(new TagInfoResponse(1L, "힐링", LocalDateTime.now()));
		PaginationData<TagInfoResponse> mockData = new PaginationData<>(
			tags,
			new PaginationData.PaginationInfo(
				1,     // totalPages
				1L,    // totalElements
				0,     // currentPage
				10,    // pageSize
				true,  // isFirst
				true,  // isLast
				false, // hasNext
				false  // hasPrevious
			)
		);

		when(tagRepository.getTags(page, size, keyword)).thenReturn(mockData);

		// when
		Page<TagInfoResponse> result = tagService.getTags(page, size, keyword);

		// then
		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().getFirst().getName()).isEqualTo("힐링");
	}

	@Test
	@DisplayName("검색 키워드가 null인 경우 전체 태그 목록 조회")
	void getTags_withNullKeyword() {
		// given
		int page = 0, size = 10;
		List<TagInfoResponse> tags = List.of(new TagInfoResponse(1L, "전체", LocalDateTime.now()));
		PaginationData<TagInfoResponse> mockData = new PaginationData<>(
			tags,
			new PaginationData.PaginationInfo(
				1,     // totalPages
				1L,    // totalElements
				0,     // currentPage
				10,    // pageSize
				true,  // isFirst
				true,  // isLast
				false, // hasNext
				false  // hasPrevious
			)
		);
		when(tagRepository.getTags(page, size, null)).thenReturn(mockData);

		// when
		Page<TagInfoResponse> result = tagService.getTags(page, size, null);

		// then
		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().getFirst().getName()).isEqualTo("전체");
	}

	@Test
	@DisplayName("검색 키워드가 공백 문자열인 경우 전체 태그 목록 조회")
	void getTags_withBlankKeyword() {
		// given
		int page = 0, size = 10;
		String blankKeyword = "   "; // 공백 문자열
		List<TagInfoResponse> tags = List.of(new TagInfoResponse(1L, "빈문자", LocalDateTime.now()));
		PaginationData<TagInfoResponse> mockData = new PaginationData<>(
			tags,
			new PaginationData.PaginationInfo(
				1,     // totalPages
				1L,    // totalElements
				0,     // currentPage
				10,    // pageSize
				true,  // isFirst
				true,  // isLast
				false, // hasNext
				false  // hasPrevious
			)
		);
		when(tagRepository.getTags(page, size, null)).thenReturn(mockData); // keyword = null 로 호출됨

		// when
		Page<TagInfoResponse> result = tagService.getTags(page, size, blankKeyword);

		// then
		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().getFirst().getName()).isEqualTo("빈문자");
	}

	@Test
	@DisplayName("태그 목록 조회 실패 시 예외 발생")
	void getTags_exceptionThrown() {
		when(tagRepository.getTags(anyInt(), anyInt(), any())).thenThrow(RuntimeException.class);
		assertThatThrownBy(() -> tagService.getTags(0, 10, "test"))
			.isInstanceOf(TagListFetchException.class);
	}

	@Test
	@DisplayName("태그 단건 조회 성공")
	void getTag_success() {
		TagInfoResponse response = new TagInfoResponse(1L, "자기계발", LocalDateTime.now());
		when(tagRepository.getTag(1L)).thenReturn(response);

		TagInfoResponse result = tagService.getTag(1L);

		assertThat(result.getName()).isEqualTo("자기계발");
	}

	@Test
	@DisplayName("태그 단건 조회 실패 시 예외 발생")
	void getTag_exceptionThrown() {
		when(tagRepository.getTag(1L)).thenThrow(RuntimeException.class);
		assertThatThrownBy(() -> tagService.getTag(1L))
			.isInstanceOf(TagNotFoundException.class);
	}

	@Test
	@DisplayName("태그 생성 성공")
	void createTag_success() {
		TagRequest request = new TagRequest("새로운태그");

		// doNothing 생략 가능
		tagService.createTag(request);

		verify(tagRepository, times(1)).createTag(request);
	}

	@Test
	@DisplayName("태그 생성 실패 시 예외 발생")
	void createTag_exceptionThrown() {
		TagRequest request = new TagRequest("에러태그");
		doThrow(RuntimeException.class).when(tagRepository).createTag(request);

		assertThatThrownBy(() -> tagService.createTag(request))
			.isInstanceOf(TagCreateException.class);
	}

	@Test
	@DisplayName("태그 수정 성공")
	void updateTag_success() {
		TagRequest request = new TagRequest("수정태그");
		tagService.updateTag(1L, request);
		verify(tagRepository, times(1)).updateTag(1L, request);
	}

	@Test
	@DisplayName("태그 수정 실패 시 예외 발생")
	void updateTag_exceptionThrown() {
		TagRequest request = new TagRequest("실패태그");
		doThrow(RuntimeException.class).when(tagRepository).updateTag(1L, request);

		assertThatThrownBy(() -> tagService.updateTag(1L, request))
			.isInstanceOf(TagUpdateException.class);
	}

	@Test
	@DisplayName("태그 삭제 성공")
	void deleteTag_success() {
		tagService.deleteTag(1L);
		verify(tagRepository, times(1)).deleteTag(1L);
	}

	@Test
	@DisplayName("태그 삭제 실패 시 예외 발생")
	void deleteTag_exceptionThrown() {
		doThrow(RuntimeException.class).when(tagRepository).deleteTag(1L);

		assertThatThrownBy(() -> tagService.deleteTag(1L))
			.isInstanceOf(TagDeleteException.class);
	}
}
