package shop.bluebooktle.backend.book.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.book.dto.response.AladinApiResponse;
import shop.bluebooktle.backend.book.dto.response.AladinBookResponse;
import shop.bluebooktle.backend.book.service.AladinBookService;

@Service
@RequiredArgsConstructor
@Slf4j
public class AladinBookServiceImpl implements AladinBookService {

	private final RestTemplate restTemplate = new RestTemplate();

	private final String TTB_KEY = "ttbsua64581504003";
	private final String BASE_URL = "http://www.aladin.co.kr/ttb/api/ItemSearch.aspx";

	//알라딘 도서 조회
	@Override
	public List<AladinBookResponse> searchBooks(String query) {
		String url = UriComponentsBuilder
			.fromHttpUrl(BASE_URL)
			.queryParam("ttbkey", TTB_KEY)
			.queryParam("Query", query)
			.queryParam("QueryType", "Title")
			.queryParam("start", 1)
			.queryParam("SearchTarget", "Book")
			.queryParam("output", "js")
			.queryParam("Version", "20131101")
			.queryParam("Cover", "Big")
			.build()
			.toUriString();

		ResponseEntity<AladinApiResponse> response = restTemplate.getForEntity(url, AladinApiResponse.class);

		return response.getBody().getItem().stream()
			.map(AladinBookResponse::from)
			.collect(Collectors.toList());

	}

	@Override
	public AladinBookResponse getBookByIsbn(String isbn) {
		List<AladinBookResponse> books = searchBooks(isbn);
		return books.getFirst();
	}
}
