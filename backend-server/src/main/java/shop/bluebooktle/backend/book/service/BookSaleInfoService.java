package shop.bluebooktle.backend.book.service;

import java.util.Optional;

import shop.bluebooktle.backend.book.dto.response.BookSaleInfoRegisterResponse;
import shop.bluebooktle.backend.book.dto.response.BookSaleInfoUpdateResponse;
import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.BookSaleInfo;
import shop.bluebooktle.common.dto.book.request.BookSaleInfoRegisterRequest;
import shop.bluebooktle.common.dto.book.request.BookSaleInfoUpdateRequest;

public interface BookSaleInfoService {

	BookSaleInfoRegisterResponse save(BookSaleInfoRegisterRequest request);

	BookSaleInfo findById(Long id);

	BookSaleInfoUpdateResponse update(Long id, BookSaleInfoUpdateRequest request);

	void deleteById(Long id);

	Optional<BookSaleInfo> findByBook(Book book);
}