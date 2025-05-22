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
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.frontend.repository.AdminAuthorRepository;
import shop.bluebooktle.frontend.service.AdminAuthorService;

@Service
@RequiredArgsConstructor
public class AdminAuthorServiceImpl implements AdminAuthorService {

	private final AdminAuthorRepository adminAuthorRepository;

	@Override
	public Page<AuthorResponse> getAuthors(int page, int size, String searchKeyword) {
		Pageable pageable = PageRequest.of(page, size);

		String keyword = null;
		if (searchKeyword != null && !searchKeyword.isBlank()) {
			keyword = searchKeyword;
		}

		PaginationData<AuthorResponse> response = adminAuthorRepository.getPagedAuthors(page, size,
			keyword);
		PaginationData<AuthorResponse> data = response;
		List<AuthorResponse> categories = data.getContent();
		return new PageImpl<>(categories, pageable, data.getTotalElements());
	}

	@Override
	public AuthorResponse getAuthor(Long id) {
		AuthorResponse response = adminAuthorRepository.getAuthor(id);
		return response;
	}

	@Override
	public void addAuthor(AuthorRegisterRequest request) {
		adminAuthorRepository.addAuthor(request);
	}

	@Override
	public void updateAuthor(Long authorId, AuthorUpdateRequest request) {
		adminAuthorRepository.updateAuthor(authorId, request);
	}

	@Override
	public void deleteAuthor(Long authorId) {
		adminAuthorRepository.deleteAuthor(authorId);
	}
}
