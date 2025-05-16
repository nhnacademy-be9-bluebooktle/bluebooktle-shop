package shop.bluebooktle.backend.book.service;

import java.util.List;

import shop.bluebooktle.backend.book.dto.response.AladinBookResponse;

public interface AladinBookService {
	public List<AladinBookResponse> searchBooks(String query);

	public AladinBookResponse getBookByIsbn(String isbn);
}
