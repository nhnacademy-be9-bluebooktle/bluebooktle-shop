package shop.bluebooktle.frontend.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.common.dto.book.request.author.AuthorRegisterRequest;
import shop.bluebooktle.common.dto.book.request.author.AuthorUpdateRequest;
import shop.bluebooktle.common.dto.book.response.author.AuthorResponse;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.frontend.repository.AuthorRepository;
import shop.bluebooktle.frontend.service.AuthorService;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

	private final AuthorRepository authorRepository;

	@Override
	public Page<AuthorResponse> getAuthors(int page, int size, String searchKeyword) {
		Pageable pageable = PageRequest.of(page, size);

		String keyword = null;
		if (searchKeyword != null && !searchKeyword.isBlank()) {
			keyword = searchKeyword;
		}

		JsendResponse<PaginationData<AuthorResponse>> response = authorRepository.getPagedAuthors(page, size,
			keyword);
		PaginationData<AuthorResponse> data = response.data();
		List<AuthorResponse> categories = data.getContent();
		return new PageImpl<>(categories, pageable, data.getTotalElements());
	}

	@Override
	public AuthorResponse getAuthor(Long id) {
		JsendResponse<AuthorResponse> response = authorRepository.getAuthor(id);
		return response.data();
	}

	@Override
	public void addAuthor(AuthorRegisterRequest request) {
		authorRepository.addAuthor(request);
	}

	@Override
	public void updateAuthor(Long authorId, AuthorUpdateRequest request) {
		authorRepository.updateAuthor(authorId, request);
	}

	@Override
	public void deleteAuthor(Long authorId) {
		authorRepository.deleteAuthor(authorId);
	}
}
