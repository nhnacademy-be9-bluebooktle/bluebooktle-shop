package shop.bluebooktle.backend.book.service;

import java.util.List;

import shop.bluebooktle.backend.book.dto.response.AladinBookResponseDto;

public interface AladinBookService {
	public List<AladinBookResponseDto> searchBooks(String query);

	public AladinBookResponseDto getBookByIsbn(String isbn);
}
