package shop.bluebooktle.frontend.service;

import java.util.List;

import org.springframework.data.domain.Page;

import shop.bluebooktle.common.dto.book.request.BookAllRegisterByAladinRequest;
import shop.bluebooktle.common.dto.book.request.BookFormRequest;
import shop.bluebooktle.common.dto.book.request.BookUpdateRequest;
import shop.bluebooktle.common.dto.book.response.AdminBookResponse;
import shop.bluebooktle.common.dto.book.response.AladinBookResponse;
import shop.bluebooktle.common.dto.book.response.BookAllResponse;
import shop.bluebooktle.common.dto.book.response.BookInfoResponse;

public interface AdminBookService {
	BookAllResponse getBook(Long bookId);

	void registerBook(BookFormRequest request);

	Page<BookInfoResponse> getPagedBooks(int page, int size, String searchKeyword);

	Page<AdminBookResponse> getPagedBooksByAdmin(int page, int size, String searchKeyword);

	void deleteBook(Long bookId);

	void updateBook(Long bookId, BookUpdateRequest bookUpdateRequest);

	void registerBookByAladin(BookAllRegisterByAladinRequest request);

	List<AladinBookResponse> searchAladin(String keyword, int page, int size);
}