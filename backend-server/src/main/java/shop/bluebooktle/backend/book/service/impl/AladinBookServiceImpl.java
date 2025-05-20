package shop.bluebooktle.backend.book.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.book.adapter.AladinAdaptor;
import shop.bluebooktle.backend.book.dto.response.AladinApiResponse;
import shop.bluebooktle.backend.book.dto.response.AladinBookResponse;
import shop.bluebooktle.backend.book.service.AladinBookService;

@Service
@RequiredArgsConstructor
@Slf4j
public class AladinBookServiceImpl implements AladinBookService {

	private final AladinAdaptor aladinAdaptor;

	//알라딘 도서 조회
	@Override
	public List<AladinBookResponse> searchBooks(String query) {
		AladinApiResponse response = aladinAdaptor.searchBooks(query);
		
		return response.getItem().stream()
			.map(AladinBookResponse::from)
			.collect(Collectors.toList());

	}

	@Override
	public AladinBookResponse getBookByIsbn(String isbn) {
		List<AladinBookResponse> books = searchBooks(isbn);
		return books.getFirst();
	}
}
