package shop.bluebooktle.backend.book.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import shop.bluebooktle.backend.book.entity.Author;
import shop.bluebooktle.backend.book.repository.AuthorRepository;
import shop.bluebooktle.backend.book.service.impl.AuthorServiceImpl;
import shop.bluebooktle.common.dto.book.request.author.AuthorRegisterRequest;
import shop.bluebooktle.common.exception.book.AuthorAlreadyExistsException;
import shop.bluebooktle.common.exception.book.AuthorFieldNullException;

@ExtendWith(MockitoExtension.class)
public class AuthorServiceTest {

	@InjectMocks
	private AuthorServiceImpl authorService;

	@Mock
	private AuthorRepository authorRepository;

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
	@DisplayName("작가 등록 실패 - 이름이 null인 경우")
	void registerAuthor_fail_name_null() {
		AuthorRegisterRequest authorRegisterRequest = new AuthorRegisterRequest(null);

		assertThrows(AuthorFieldNullException.class, () -> authorService.registerAuthor(authorRegisterRequest));
	}

	@Test
	@DisplayName("작가 등록 실패 - 이름이 공백 문자열인 경우")
	void registerAuthor_fail_name_blank() {
		AuthorRegisterRequest authorRegisterRequest = new AuthorRegisterRequest(" ");

		assertThrows(AuthorFieldNullException.class, () -> authorService.registerAuthor(authorRegisterRequest));
	}

	@Test
	@DisplayName("작가 등록 실패 - 이름이 중복인 경우")
	void registerAuthor_fail_name_duplicate() {
		AuthorRegisterRequest authorRegisterRequest = new AuthorRegisterRequest("홍길동");
		when(authorRepository.existsByName("홍길동")).thenReturn(true);

		assertThrows(AuthorAlreadyExistsException.class, () -> authorService.registerAuthor(authorRegisterRequest));
	}

	// 작가 조회

	void getAuthor_success() {
		Long authorId = 1L;
	}































}
