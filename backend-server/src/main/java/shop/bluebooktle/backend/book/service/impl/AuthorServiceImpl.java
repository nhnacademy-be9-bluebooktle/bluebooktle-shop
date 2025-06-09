package shop.bluebooktle.backend.book.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.entity.Author;
import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.BookAuthor;
import shop.bluebooktle.backend.book.repository.AuthorRepository;
import shop.bluebooktle.backend.book.repository.BookAuthorRepository;
import shop.bluebooktle.backend.book.service.AuthorService;
import shop.bluebooktle.backend.elasticsearch.service.BookElasticSearchService;
import shop.bluebooktle.common.dto.book.request.author.AuthorRegisterRequest;
import shop.bluebooktle.common.dto.book.request.author.AuthorUpdateRequest;
import shop.bluebooktle.common.dto.book.response.author.AuthorResponse;
import shop.bluebooktle.common.exception.book.AuthorAlreadyExistsException;
import shop.bluebooktle.common.exception.book.AuthorCannotDeleteException;
import shop.bluebooktle.common.exception.book.AuthorNotFoundException;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

	private final AuthorRepository authorRepository;
	private final BookAuthorRepository bookAuthorRepository;
	private final BookElasticSearchService bookElasticSearchService;

	@Override
	public void registerAuthor(AuthorRegisterRequest authorRegisterRequest) {

		String name = authorRegisterRequest.getName();

		if (authorRepository.existsByName(name)) {
			throw new AuthorAlreadyExistsException(name);
		}

		Author author = Author.builder()
			.name(authorRegisterRequest.getName())
			.build();

		authorRepository.save(author);

	}

	@Transactional(readOnly = true)
	@Override
	public AuthorResponse getAuthor(Long authorId) {

		Author author = authorRepository.findById(authorId)
			.orElseThrow(() -> new AuthorNotFoundException(authorId));

		return AuthorResponse.builder()
			.id(author.getId())
			.name(author.getName())
			.createdAt(author.getCreatedAt())
			.build();
	}

	@Override
	public void updateAuthor(Long authorId, AuthorUpdateRequest authorUpdateRequest) {

		String name = authorUpdateRequest.getName();
		Author author = authorRepository.findById(authorId).orElseThrow(() -> new AuthorNotFoundException(authorId));

		if (authorRepository.existsByName(name)) {
			throw new AuthorAlreadyExistsException(name);
		}

		List<Book> bookList = bookAuthorRepository.findByAuthor(author)
			.stream()
			.map(BookAuthor::getBook)
			.toList();

		// 엘라스틱에 등록된 도서 정보 수정
		bookElasticSearchService.updateAuthorName(bookList, name, author.getName());

		author.setName(name);
		authorRepository.save(author);

	}

	@Override
	public void deleteAuthor(Long authorId) {

		Author author = authorRepository.findById(authorId).orElseThrow(() -> new AuthorNotFoundException(authorId));

		// 출판사에 등록된 도서가 있을 경우 삭제 불가
		if (bookAuthorRepository.existsByAuthor(author)) {
			throw new AuthorCannotDeleteException();
		}
		authorRepository.delete(author);
	}

	@Override
	public AuthorResponse registerAuthorByName(String authorName) {
		Author author = authorRepository.findByName(authorName)
			.orElseGet(() -> authorRepository.save(
				Author.builder()
					.name(authorName)
					.build()
			));

		return AuthorResponse.builder()
			.id(author.getId())
			.name(author.getName())
			.createdAt(author.getCreatedAt())
			.build();
	}

	@Override
	public Page<AuthorResponse> getAuthors(Pageable pageable) {
		Page<Author> authors = authorRepository.findAll(pageable);
		return authors.map(a -> AuthorResponse.builder()
			.id(a.getId())
			.name(a.getName())
			.createdAt(a.getCreatedAt())
			.build());
	}

	@Override
	public Page<AuthorResponse> searchAuthors(String searchKeyword, Pageable pageable) {
		Page<Author> authors = authorRepository.searchByNameContaining(searchKeyword, pageable);
		return authors.map(a -> new AuthorResponse(a.getId(), a.getName(), a.getCreatedAt()));
	}
}