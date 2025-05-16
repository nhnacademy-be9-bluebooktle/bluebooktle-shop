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
import shop.bluebooktle.common.exception.book.AuthorIdNullException;
import shop.bluebooktle.common.exception.book.AuthorNameEmptyException;

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
			throw new RuntimeException(); // TODO #1
		}

		if (name.trim().isEmpty() || description.trim().isEmpty() || authorKey.trim().isEmpty()) {
			throw new RuntimeException(); // TODO #2
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
			throw new RuntimeException(); // TODO #3
		}

		Author author = authorRepository.findById(authorId).orElseThrow(() -> new RuntimeException()); // TODO #4

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
			throw new RuntimeException(); // TODO #5
		}

		if ((name == null || name.trim().isEmpty()) &&
			(description == null || description.trim().isEmpty()) &&
			(authorKey == null || authorKey.trim().isEmpty())) {
			throw new RuntimeException(); // TODO #6
		}

		Author author = authorRepository.findById(authorId).orElseThrow(() -> new RuntimeException()); // TODO #7

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
			throw new RuntimeException(); // TODO #8
		}
		Author author = authorRepository.findById(authorId).orElseThrow(() -> new RuntimeException()); // TODO #9
		authorRepository.delete(author);
	}
}