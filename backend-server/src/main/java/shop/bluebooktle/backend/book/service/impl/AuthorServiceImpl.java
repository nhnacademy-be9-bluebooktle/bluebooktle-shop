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
@Transactional
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

	private final AuthorRepository authorRepository;

	@Override
	public void registerAuthor(AuthorRegisterRequest authorRegisterRequest) {

		String name = authorRegisterRequest.getName();
		String description = authorRegisterRequest.getDescription();

		if (name == null || description == null) {
			throw new AuthorFieldNullException();
		}

		if (name.trim().isEmpty() || description.trim().isEmpty()) {
			throw new AuthorFieldNullException();
		}

		Author author = Author.builder()
			.name(authorRegisterRequest.getName())
			.description(authorRegisterRequest.getDescription())
			.build();

		if (author.getAuthorKey() == null) {
			author.setAuthorKey("null");
		}

		Author authorSaved = authorRepository.save(author);

		String key = "a" + authorSaved.getId();
		authorSaved.setAuthorKey(key);
		authorRepository.save(authorSaved);
	}

	@Transactional(readOnly = true)
	@Override
	public AuthorResponse getAuthor(Long authorId) {

		Author author = authorRepository.findById(authorId).orElseThrow(() -> new AuthorNotFoundException(authorId)); // #TODO

		return AuthorResponse.builder()
			.id(author.getId())
			.name(author.getName())
			.description(author.getDescription())
			.authorKey(author.getAuthorKey())
			.createdAt(author.getCreatedAt())
			.build();
	}

	@Override
	public void updateAuthor(Long authorId, AuthorUpdateRequest authorUpdateRequest) {

		String name = authorUpdateRequest.getName();
		String description = authorUpdateRequest.getDescription();

		if ((name == null || name.trim().isEmpty()) &&
			(description == null || description.trim().isEmpty())) {
			throw new AuthorUpdateFieldMissingException(); // #TODO
		}

		Author author = authorRepository.findById(authorId).orElseThrow(() -> new AuthorNotFoundException(authorId));

		if (name != null) {
			author.setName(name);
		}
		if (description != null) {
			author.setDescription(description);
		}

		authorRepository.save(author);

	}

	@Override
	public void deleteAuthor(Long authorId) {

		Author author = authorRepository.findById(authorId).orElseThrow(() -> new AuthorNotFoundException(authorId));
		authorRepository.delete(author);
	}
}