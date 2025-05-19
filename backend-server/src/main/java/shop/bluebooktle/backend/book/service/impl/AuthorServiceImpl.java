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

		if (name == null) {
			throw new AuthorFieldNullException();
		}

		if (name.trim().isEmpty()) {
			throw new AuthorFieldNullException();
		}

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

		Author author = authorRepository.findById(authorId).orElseThrow(() -> new AuthorNotFoundException(authorId)); // #TODO

		return AuthorResponse.builder()
			.id(author.getId())
			.name(author.getName())
			.createdAt(author.getCreatedAt())
			.build();
	}

	@Override
	public void updateAuthor(Long authorId, AuthorUpdateRequest authorUpdateRequest) {

		String name = authorUpdateRequest.getName();

		if ((name == null || name.trim().isEmpty())) {
			throw new AuthorUpdateFieldMissingException(); // #TODO
		}

		Author author = authorRepository.findById(authorId).orElseThrow(() -> new AuthorNotFoundException(authorId));

		author.setName(name);

		authorRepository.save(author);

	}

	@Override
	public void deleteAuthor(Long authorId) {

		Author author = authorRepository.findById(authorId).orElseThrow(() -> new AuthorNotFoundException(authorId));
		authorRepository.delete(author);
	}
}