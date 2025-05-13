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

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

	private final AuthorRepository authorRepository;

	@Transactional
	@Override
	public void registerAuthor(AuthorRegisterRequest authorRegisterRequest) {
		if (authorRegisterRequest.getName() == null || authorRegisterRequest.getName().trim().isEmpty()) {
			throw new IllegalArgumentException("Name cannot be empty"); // TODO: 프로젝트 에러 처리 방식으로 변경
		}
		if (!authorRepository.findByName(authorRegisterRequest.getName()).isEmpty()) {
			throw new IllegalArgumentException("Name already exists"); // TODO: 프로젝트 예외 처리 방식
		}

		Author author = Author.builder().name(authorRegisterRequest.getName()).build();

		authorRepository.save(author);
	}

	@Transactional(readOnly = true)
	@Override
	public AuthorResponse getAuthor(Long authorId) {
		if (authorId == null) {
			throw new IllegalArgumentException("AuthorId cannot be null"); // TODO: 프로젝트 에러 처리 방식으로 변경
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
			throw new IllegalArgumentException("AuthorId cannot be null"); // TODO: 프로젝트 에러 처리 방식으로 변경
		}
		if (authorUpdateRequest.getName() == null || authorUpdateRequest.getName().trim().isEmpty()) {
			throw new IllegalArgumentException("Name cannot be empty");
		}

		Author author = authorRepository.findById(authorId).orElseThrow(() -> new IllegalArgumentException("Author not found"));

		author.setName(authorUpdateRequest.getName());

		authorRepository.save(author);
	}

	@Transactional
	@Override
	public void deleteAuthor(Long authorId) {
		if (authorId == null) {
			throw new IllegalArgumentException("AuthorId cannot be null");
		}
		// Author author = authorRepository.findById(authorId).orElseThrow(() -> new IllegalArgumentException("Author not found"));
		// authorRepository.delete(author);
		authorRepository.deleteById(authorId);
	}
}
