package shop.bluebooktle.backend.book.service;

import java.util.List;
import java.util.Optional;

import shop.bluebooktle.backend.book.dto.request.BookSaleInfoUpdateRequest;
import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.BookSaleInfo;

public interface BookSaleInfoService {

	// 아직수정필요

	BookSaleInfo save(BookSaleInfo bookSaleInfo);

	BookSaleInfo findById(Long id);

	List<BookSaleInfo> findAll();

	BookSaleInfo update(Long id, BookSaleInfoUpdateRequest request);

	void deleteById(Long id);

	Optional<BookSaleInfo> findByBook(Book book);
}