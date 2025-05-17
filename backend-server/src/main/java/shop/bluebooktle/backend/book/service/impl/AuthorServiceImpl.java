package shop.bluebooktle.backend.book.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.dto.request.author.AuthorRegisterRequest;
import shop.bluebooktle.backend.book.dto.request.author.AuthorUpdateRequest;
import shop.bluebooktle.backend.book.dto.response.author.AuthorResponse;
import shop.bluebooktle.backend.book.entity.Author;
import shop.bluebooktle.backend.book.repository.AuthorRepository;
import shop.bluebooktle.backend.book.service.AuthorService;
import shop.bluebooktle.common.exception.book.AuthorAlreadyExistsException;
import shop.bluebooktle.common.exception.book.AuthorFieldNullException;
import shop.bluebooktle.common.exception.book.AuthorIdNullException;
import shop.bluebooktle.common.exception.book.AuthorNotFoundException;
import shop.bluebooktle.common.exception.book.AuthorUpdateFieldMissingException;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

	private final AuthorRepository authorRepository;

	@Transactional
	@Override
	public void registerAuthor(AuthorRegisterRequest authorRegisterRequest) {

		String name = authorRegisterRequest.getName();
		String description = authorRegisterRequest.getDescription();
		String authorKey = authorRegisterRequest.getAuthorKey();

		if (name == null || description == null || authorKey == null) {
			throw new AuthorFieldNullException();
		}

		if (name.trim().isEmpty() || description.trim().isEmpty() || authorKey.trim().isEmpty()) {
			throw new AuthorFieldNullException();
		}

		if (authorRepository.existsByName(name)) {
			throw new AuthorAlreadyExistsException(name);
		}

		Author author = Author.builder()
			.name(authorRegisterRequest.getName())
			.description(authorRegisterRequest.getDescription())
			.authorKey(authorRegisterRequest.getAuthorKey())
			.build();

		authorRepository.save(author);
	}

	@Transactional
	@Override
	public AuthorResponse getAuthor(Long authorId) {

		if (authorId == null) {
			throw new AuthorIdNullException();
		}

		Author author = authorRepository.findById(authorId).orElseThrow(() -> new AuthorNotFoundException(authorId));

		return AuthorResponse.builder()
			.id(author.getId())
			.name(author.getName())
			.description(author.getDescription())
			.authorKey(author.getAuthorKey())
			.createdAt(author.getCreatedAt())
			.build();
	}

	@Transactional
	@Override
	public void updateAuthor(Long authorId, AuthorUpdateRequest authorUpdateRequest) {

		String name = authorUpdateRequest.getName();
		String description = authorUpdateRequest.getDescription();
		String authorKey = authorUpdateRequest.getAuthorKey();

		if (authorId == null) {
			throw new AuthorIdNullException();
		}

		if ((name == null || name.trim().isEmpty()) &&
			(description == null || description.trim().isEmpty()) &&
			(authorKey == null || authorKey.trim().isEmpty())) {
			throw new AuthorUpdateFieldMissingException();
		}

		Author author = authorRepository.findById(authorId).orElseThrow(() -> new AuthorNotFoundException(authorId));

		if (name != null) {
			author.setName(name);
		}
		if (description != null) {
			author.setDescription(description);
		}
		if (authorKey != null) {
			author.setAuthorKey(authorKey);
		}

		authorRepository.save(author);

	}

	@Transactional
	@Override
	public void deleteAuthor(Long authorId) {
		if (authorId == null) {
			throw new AuthorIdNullException();
		}
		Author author = authorRepository.findById(authorId).orElseThrow(() -> new AuthorNotFoundException(authorId));
		authorRepository.delete(author);
	}
}