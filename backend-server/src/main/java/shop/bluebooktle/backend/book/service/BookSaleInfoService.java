package shop.bluebooktle.backend.book.service;

import java.util.Optional;

import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.BookSaleInfo;
import shop.bluebooktle.common.dto.book.request.BookSaleInfoRegisterRequest;
import shop.bluebooktle.common.dto.book.request.BookSaleInfoUpdateRequest;
import shop.bluebooktle.common.dto.book.response.BookSaleInfoRegisterResponse;
import shop.bluebooktle.common.dto.book.response.BookSaleInfoResponse;
import shop.bluebooktle.common.dto.book.response.BookSaleInfoUpdateResponse;

public interface BookSaleInfoService {

	BookSaleInfoRegisterResponse save(BookSaleInfoRegisterRequest request);

	BookSaleInfoResponse findById(Long id);

	BookSaleInfoUpdateResponse update(Long id, BookSaleInfoUpdateRequest request);

	void deleteById(Long id);

	Optional<BookSaleInfo> findByBook(Book book);
}