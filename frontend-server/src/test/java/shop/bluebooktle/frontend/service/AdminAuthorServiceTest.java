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
import shop.bluebooktle.common.dto.book.request.author.AuthorRegisterRequest;
import shop.bluebooktle.common.dto.book.request.author.AuthorUpdateRequest;
import shop.bluebooktle.common.dto.book.response.TagInfoResponse;
import shop.bluebooktle.common.dto.book.response.author.AuthorResponse;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.exception.book.TagCreateException;
import shop.bluebooktle.common.exception.book.TagDeleteException;
import shop.bluebooktle.common.exception.book.TagListFetchException;
import shop.bluebooktle.common.exception.book.TagNotFoundException;
import shop.bluebooktle.common.exception.book.TagUpdateException;
import shop.bluebooktle.frontend.repository.AdminAuthorRepository;
import shop.bluebooktle.frontend.repository.AdminTagRepository;
import shop.bluebooktle.frontend.service.impl.AdminAuthorServiceImpl;
import shop.bluebooktle.frontend.service.impl.AdminTagServiceImpl;

@ExtendWith(MockitoExtension.class)
public class AdminAuthorServiceTest {
	@Mock
	AdminAuthorRepository authorRepository;

	@InjectMocks
	AdminAuthorServiceImpl authorService;

	@Test
	@DisplayName("작가 목록 조회 - 키워드 있음")
	void getAuthors_withKeyword() {
		int page = 0, size = 10;
		String keyword = "김";

		List<AuthorResponse> authors = List.of(
			new AuthorResponse(1L, "김작가", LocalDateTime.now())
		);
		PaginationData<AuthorResponse> mockData = new PaginationData<>(
			authors,
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

		when(authorRepository.getPagedAuthors(page, size, keyword)).thenReturn(mockData);

		Page<AuthorResponse> result = authorService.getAuthors(page, size, keyword);

		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().getFirst().getName()).isEqualTo("김작가");
	}

	@Test
	@DisplayName("작가 목록 조회 - 키워드 null")
	void getAuthors_nullKeyword() {
		int page = 0, size = 10;

		List<AuthorResponse> authors = List.of(
			new AuthorResponse(2L, "무명", LocalDateTime.now())
		);
		PaginationData<AuthorResponse> mockData = new PaginationData<>(
			authors,
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

		when(authorRepository.getPagedAuthors(page, size, null)).thenReturn(mockData);

		Page<AuthorResponse> result = authorService.getAuthors(page, size, null);

		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().getFirst().getName()).isEqualTo("무명");
	}

	@Test
	@DisplayName("작가 목록 조회 - 키워드 빈 문자열")
	void getAuthors_blankKeyword() {
		int page = 0, size = 10;
		String keyword = "   ";

		List<AuthorResponse> authors = List.of(
			new AuthorResponse(3L, "공백작가", LocalDateTime.now())
		);
		PaginationData<AuthorResponse> mockData = new PaginationData<>(
			authors,
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

		when(authorRepository.getPagedAuthors(page, size, null)).thenReturn(mockData);

		Page<AuthorResponse> result = authorService.getAuthors(page, size, keyword);

		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().getFirst().getName()).isEqualTo("공백작가");
	}

	@Test
	@DisplayName("작가 단건 조회 성공")
	void getAuthor_success() {
		AuthorResponse response = new AuthorResponse(10L, "단건작가", LocalDateTime.now());
		when(authorRepository.getAuthor(10L)).thenReturn(response);

		AuthorResponse result = authorService.getAuthor(10L);

		assertThat(result.getId()).isEqualTo(10L);
		assertThat(result.getName()).isEqualTo("단건작가");
	}

	@Test
	@DisplayName("작가 등록 성공")
	void addAuthor_success() {
		AuthorRegisterRequest request = new AuthorRegisterRequest("신작가");

		authorService.addAuthor(request);

		verify(authorRepository, times(1)).addAuthor(request);
	}

	@Test
	@DisplayName("작가 수정 성공")
	void updateAuthor_success() {
		AuthorUpdateRequest request = AuthorUpdateRequest.builder().name("수정작가").build();

		authorService.updateAuthor(1L, request);

		verify(authorRepository, times(1)).updateAuthor(1L, request);
	}

	@Test
	@DisplayName("작가 삭제 성공")
	void deleteAuthor_success() {
		authorService.deleteAuthor(5L);

		verify(authorRepository, times(1)).deleteAuthor(5L);
	}
}
