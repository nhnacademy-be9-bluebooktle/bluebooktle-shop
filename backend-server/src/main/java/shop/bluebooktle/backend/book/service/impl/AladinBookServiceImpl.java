package shop.bluebooktle.backend.book.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.book.adapter.AladinAdaptor;
import shop.bluebooktle.backend.book.service.AladinBookService;
import shop.bluebooktle.common.dto.book.response.AladinApiResponse;
import shop.bluebooktle.common.dto.book.response.AladinBookResponse;
import shop.bluebooktle.common.exception.book.AladinBookNotFoundException;

@Service
@RequiredArgsConstructor
@Slf4j
public class AladinBookServiceImpl implements AladinBookService {

	private final AladinAdaptor aladinAdaptor;

	//알라딘 도서 조회
	@Override
	public List<AladinBookResponse> searchBooks(String query) {
		AladinApiResponse response = aladinAdaptor.searchBooks(query);

		if (response == null) {
			throw new AladinBookNotFoundException("알라딘에서의 응답이 null입니다.");
		}

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
