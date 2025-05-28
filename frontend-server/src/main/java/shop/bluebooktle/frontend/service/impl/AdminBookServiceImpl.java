package shop.bluebooktle.frontend.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.common.dto.book.request.BookAllRegisterRequest;
import shop.bluebooktle.common.dto.book.response.AladinBookResponse;
import shop.bluebooktle.common.dto.book.response.BookAllResponse;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.frontend.repository.AdminBookRepository;
import shop.bluebooktle.frontend.service.AdminBookService;

@Service
@RequiredArgsConstructor
public class AdminBookServiceImpl implements AdminBookService {
	private final AdminBookRepository adminBookRepository;

	@Override
	public Page<BookAllResponse> getPagedBooks(int page, int size, String searchKeyword) {
		Pageable pageable = PageRequest.of(page, size);

		String keyword = null;
		if (searchKeyword != null && !searchKeyword.isBlank()) {
			keyword = searchKeyword;
		}
		PaginationData<BookAllResponse> data = adminBookRepository.getPagedBooks(page, size, keyword);
		List<BookAllResponse> categories = data.getContent();
		return new PageImpl<>(categories, pageable, data.getTotalElements());
	}

	@Override
	public BookAllResponse getBook(Long bookId) {
		return adminBookRepository.getBook(bookId);
	}

	@Override
	public void registerBook(BookAllRegisterRequest bookAllRegisterRequest) {
		adminBookRepository.registerBook(bookAllRegisterRequest);
	}

	@Override
	public void deleteBook(Long bookId) {
		adminBookRepository.deleteBook(bookId);
	}

	@Override
	public List<AladinBookResponse> searchAladin(String keyword, int page, int size) {
		return adminBookRepository.searchAladinBooks(keyword, page, size);
	}
}
