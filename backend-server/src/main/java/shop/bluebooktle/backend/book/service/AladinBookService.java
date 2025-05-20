package shop.bluebooktle.backend.book.service;

import java.util.List;

import shop.bluebooktle.common.dto.book.response.AladinBookResponse;

public interface AladinBookService {

	List<AladinBookResponse> searchBooks(String query);

	AladinBookResponse getBookByIsbn(String isbn);

	// AladinBookItemBySearch searchBookByIsbn(String isbn);

}
