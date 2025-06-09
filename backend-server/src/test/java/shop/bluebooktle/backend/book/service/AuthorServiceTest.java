package shop.bluebooktle.backend.book.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
import org.springframework.test.util.ReflectionTestUtils;

import shop.bluebooktle.backend.book.entity.Author;
import shop.bluebooktle.backend.book.repository.AuthorRepository;
import shop.bluebooktle.backend.book.repository.BookAuthorRepository;
import shop.bluebooktle.backend.book.service.impl.AuthorServiceImpl;
import shop.bluebooktle.backend.elasticsearch.service.BookElasticSearchService;
import shop.bluebooktle.common.dto.book.request.author.AuthorRegisterRequest;
import shop.bluebooktle.common.dto.book.request.author.AuthorUpdateRequest;
import shop.bluebooktle.common.dto.book.response.author.AuthorResponse;
import shop.bluebooktle.common.exception.book.AuthorAlreadyExistsException;
import shop.bluebooktle.common.exception.book.AuthorNotFoundException;

@ExtendWith(MockitoExtension.class)
public class AuthorServiceTest {

	@InjectMocks
	private AuthorServiceImpl authorService;

	@Mock
	private AuthorRepository authorRepository;

	@Mock
	private BookElasticSearchService bookElasticSearchService;

	@Mock
	private BookAuthorRepository bookAuthorRepository;

	// 작가 등록

	@Test
	@DisplayName("작가 등록 성공")
	void registerAuthor_success() {

		AuthorRegisterRequest authorRegisterRequest = new AuthorRegisterRequest("홍길동");
		when(authorRepository.existsByName("홍길동")).thenReturn(false);

		authorService.registerAuthor(authorRegisterRequest);

		verify(authorRepository, times(1)).save(any(Author.class));
	}

	@Test
	@DisplayName("작가 등록 실패 - 이름이 중복인 경우")
	void registerAuthor_fail_name_duplicate() {
		AuthorRegisterRequest authorRegisterRequest = new AuthorRegisterRequest("홍길동");
		when(authorRepository.existsByName("홍길동")).thenReturn(true);

		assertThrows(AuthorAlreadyExistsException.class, () -> authorService.registerAuthor(authorRegisterRequest));
	}

	// 작가 조회

	@Test
	@DisplayName("작가 조회 성공")
	void getAuthor_success() {
		Long authorId = 1L;
		Author author = Author.builder()
			.name("홍길동")
			.build();

		ReflectionTestUtils.setField(author, "id", authorId);

		when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));

		AuthorResponse authorResponse = authorService.getAuthor(authorId);

		assertNotNull(authorResponse);
		assertEquals(authorId, authorResponse.getId());
		assertEquals("홍길동", authorResponse.getName());
	}

	@Test
	@DisplayName("작가 조회 실패 - 존재하지 않는 ID")
	void getAuthor_fail_not_found() {
		Long authorId = 999L;
		when(authorRepository.findById(authorId)).thenReturn(Optional.empty());

		assertThrows(AuthorNotFoundException.class, () -> authorService.getAuthor(authorId));
	}

	// 작가 수정

	@Test
	@DisplayName("작가 수정 성공")
	void updateAuthor_success() {
		Long authorId = 1L;
		AuthorUpdateRequest authorUpdateRequest = AuthorUpdateRequest.builder()
			.name("청길동")
			.build();

		Author author = Author.builder()
			.name("홍길동")
			.build();

		ReflectionTestUtils.setField(author, "id", authorId);

		when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));
		when(authorRepository.existsByName("청길동")).thenReturn(false);
		when(bookAuthorRepository.findByAuthor(author)).thenReturn(List.of());
		doNothing().when(bookElasticSearchService).updateAuthorName(anyList(), anyString(), anyString());
		authorService.updateAuthor(authorId, authorUpdateRequest);

		assertEquals("청길동", author.getName());
		verify(authorRepository, times(1)).save(author);
	}

	@Test
	@DisplayName("작가 수정 실패 - 유효하지 않은 ID")
	void updateAuthor_fail_not_found() {
		Long authorId = 999L;
		AuthorUpdateRequest authorUpdateRequest = AuthorUpdateRequest.builder()
			.name("청길동")
			.build();

		when(authorRepository.findById(authorId)).thenReturn(Optional.empty());

		assertThrows(AuthorNotFoundException.class, () -> authorService.updateAuthor(authorId, authorUpdateRequest));
	}

	// 작가 삭제

	@Test
	@DisplayName("작가 삭제 성공")
	void deleteAuthor_success() {
		Long authorId = 1L;
		Author author = Author.builder()
			.name("홍길동")
			.build();

		ReflectionTestUtils.setField(author, "id", authorId);

		when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));
		when(bookAuthorRepository.existsByAuthor(author)).thenReturn(false);
		authorService.deleteAuthor(authorId);

		verify(authorRepository, times(1)).delete(author);
	}

	@Test
	@DisplayName("작가 삭제 실패 - 존재하지 않는 ID")
	void deleteAuthor_fail_not_found() {
		Long authorId = 999L;

		when(authorRepository.findById(authorId)).thenReturn(Optional.empty());

		assertThrows(AuthorNotFoundException.class, () -> authorService.deleteAuthor(authorId));
	}

	// 이름으로 작가 등록

	@Test
	@DisplayName("이름으로 작가 등록 - 작가가 이미 있는 경우")
	void registerAuthorByName_already_exists() {
		String authorName = "홍길동";
		Author author = Author.builder()
			.name(authorName)
			.build();

		ReflectionTestUtils.setField(author, "id", 1L);

		when(authorRepository.findByName(authorName)).thenReturn(Optional.of(author));

		AuthorResponse authorResponse = authorService.registerAuthorByName(authorName);

		assertEquals(author.getId(), authorResponse.getId());
		assertEquals(authorName, authorResponse.getName());

		verify(authorRepository, never()).save(any(Author.class));
	}

	@Test
	@DisplayName("이름으로 작가 등록 - 작가가 없는 경우")
	void registerAuthorByName_new() {
		String authorName = "청길동";
		Author author = Author.builder()
			.name(authorName)
			.build();

		ReflectionTestUtils.setField(author, "id", 2L);

		when(authorRepository.findByName(authorName)).thenReturn(Optional.empty());
		when(authorRepository.save(any(Author.class))).thenReturn(author);

		AuthorResponse authorResponse = authorService.registerAuthorByName(authorName);

		assertEquals(author.getId(), authorResponse.getId());
		assertEquals(authorName, authorResponse.getName());

		verify(authorRepository, times(1)).save(any(Author.class));
	}

	@Test
	@DisplayName("작가 목록 조회 성공 - 페이징")
	void getAuthors_success() {
		Pageable pageable = PageRequest.of(0, 2);

		Author author1 = Author.builder()
			.name("홍길동")
			.build();

		ReflectionTestUtils.setField(author1, "id", 1L);

		Author author2 = Author.builder()
			.name("청길동")
			.build();

		ReflectionTestUtils.setField(author2, "id", 2L);

		Page<Author> authorPage = new PageImpl<>(List.of(author1, author2));

		when(authorRepository.findAll(pageable)).thenReturn(authorPage);

		Page<AuthorResponse> authorResponsePage = authorService.getAuthors(pageable);

		assertEquals(2, authorResponsePage.getTotalElements());
		List<AuthorResponse> authorResponses = authorResponsePage.getContent();
		assertEquals("홍길동", authorResponses.get(0).getName());
		assertEquals("청길동", authorResponses.get(1).getName());

	}

	@Test
	@DisplayName("작가 부분 이름 검색 성공 - 페이징")
	void searchAuthors_success() {
		String keyword = "길동";
		Pageable pageable = PageRequest.of(0, 2);

		Author author1 = Author.builder()
			.name("홍길동")
			.build();

		ReflectionTestUtils.setField(author1, "id", 1L);

		Author author2 = Author.builder()
			.name("청길동")
			.build();

		ReflectionTestUtils.setField(author2, "id", 2L);

		Page<Author> authorPage = new PageImpl<>(List.of(author1, author2));

		when(authorRepository.searchByNameContaining(keyword, pageable)).thenReturn(authorPage);

		Page<AuthorResponse> authorResponses = authorService.searchAuthors(keyword, pageable);

		assertEquals(2, authorResponses.getTotalElements());
		List<AuthorResponse> content = authorResponses.getContent();
		assertEquals("홍길동", content.get(0).getName());
		assertEquals("청길동", content.get(1).getName());

		verify(authorRepository, times(1)).searchByNameContaining(keyword, pageable);
	}
}