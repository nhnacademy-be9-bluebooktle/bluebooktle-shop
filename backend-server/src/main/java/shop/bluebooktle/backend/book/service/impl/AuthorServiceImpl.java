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
		if (authorRegisterRequest.getName() == null || authorRegisterRequest.getName().trim().isEmpty()) {
			throw new AuthorNameEmptyException();
		}
		if (!authorRepository.findByName(authorRegisterRequest.getName()).isEmpty()) {
			throw new AuthorAlreadyExistsException(authorRegisterRequest.getName());
		}

		Author author = Author.builder().name(authorRegisterRequest.getName()).build();

		authorRepository.save(author);
	}

	@Transactional(readOnly = true)
	@Override
	public AuthorResponse getAuthor(Long authorId) {
		if (authorId == null) {
			throw new AuthorIdNullException();
		}

		Author author = authorRepository.findById(authorId).orElseThrow(() -> new IllegalArgumentException("Author not found"));

		AuthorResponse authorResponse = AuthorResponse.builder()
			.id(author.getId())
			.name(author.getName())
			.build();

		return authorResponse;
	}

	@Transactional
	@Override
	public void updateAuthor(Long authorId, AuthorUpdateRequest authorUpdateRequest) {
		if (authorId == null) {
			throw new AuthorIdNullException();
		}
		if (authorUpdateRequest.getName() == null || authorUpdateRequest.getName().trim().isEmpty()) {
			throw new AuthorNameEmptyException();
		}

		Author author = authorRepository.findById(authorId).orElseThrow(() -> new IllegalArgumentException("Author not found"));

		author.setName(authorUpdateRequest.getName());

		authorRepository.save(author);
	}

	@Transactional
	@Override
	public void deleteAuthor(Long authorId) {
		if (authorId == null) {
			throw new AuthorIdNullException();
		}
		// Author author = authorRepository.findById(authorId).orElseThrow(() -> new IllegalArgumentException("Author not found"));
		// authorRepository.delete(author);
		authorRepository.deleteById(authorId);
	}
}
