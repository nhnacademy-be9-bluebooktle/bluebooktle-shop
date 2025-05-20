package shop.bluebooktle.backend.book.service;

import java.util.List;

import shop.bluebooktle.backend.book.dto.response.AladinBookResponse;

public interface AladinBookService {

	List<AladinBookResponse> searchBooks(String query);

	AladinBookResponse getBookByIsbn(String isbn);

	// AladinBookItemBySearch searchBookByIsbn(String isbn);

}
