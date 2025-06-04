package shop.bluebooktle.backend.book.service;

import java.util.List;

import org.springframework.data.domain.Page;

import shop.bluebooktle.common.dto.book.request.BookRegisterRequest;
import shop.bluebooktle.common.dto.book.request.BookUpdateRequest;
import shop.bluebooktle.common.dto.book.response.BookAllResponse;
import shop.bluebooktle.common.dto.book.response.BookCartOrderResponse;
import shop.bluebooktle.common.dto.book.response.BookInfoResponse;
import shop.bluebooktle.common.dto.book.response.BookRegisterResponse;
import shop.bluebooktle.common.dto.book.response.BookResponse;
import shop.bluebooktle.common.dto.book.response.BookUpdateResponse;
import shop.bluebooktle.common.dto.common.PaginationData;

public interface BookService {

	BookRegisterResponse registerBook(BookRegisterRequest request);

	BookResponse findBookById(Long bookId);

	BookUpdateResponse updateBook(Long bookId, BookUpdateRequest request);

	void deleteBook(Long bookId);

	BookAllResponse findBookAllById(Long id);

	List<BookAllResponse> getBookAllByTitle(String title);

	Page<BookInfoResponse> findAllBooks(int page, int size, String searchKeyword);

	BookCartOrderResponse getBookCartOrder(Long bookId, int quantity);

	// //메인페이지에 표시될 정보(id, title, author, price, salePrice, imgUrl) 조회
	// Page<BookInfoResponse> getBooksForMainPage(Long bookId, Pageable pageable);
	//
	// //제목으로 검색하여 표시될 정보(id, title, author, price, salePrice, imgUrl) 조회
	// Page<BookInfoResponse> searchBooksByTitle(String title, Pageable pageable);
}
