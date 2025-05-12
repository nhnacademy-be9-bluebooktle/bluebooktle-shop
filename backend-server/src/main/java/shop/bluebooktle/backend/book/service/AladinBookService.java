package shop.bluebooktle.backend.book.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.dto.AladinApiResponse;
import shop.bluebooktle.backend.book.dto.response.AladinBookResponseDto;

@Service
@RequiredArgsConstructor
public class AladinBookService {

	private final RestTemplate restTemplate = new RestTemplate();

	private final String TTB_KEY = "ttbehfk11131717001";
	private final String BASE_URL = "http://www.aladin.co.kr/ttb/api/ItemSearch.aspx";

	//알라딘 도서 조회
	public List<AladinBookResponseDto> searchBooks(String query) {
		String url = UriComponentsBuilder
			.fromHttpUrl(BASE_URL)
			.queryParam("ttbkey", TTB_KEY)
			.queryParam("Query", query)
			.queryParam("QueryType", "Title")
			.queryParam("start", 1)
			.queryParam("SearchTarget", "Book")
			.queryParam("output", "js")
			.queryParam("Version", "20131101")
			.build()
			.toUriString();

		ResponseEntity<AladinApiResponse> response = restTemplate.getForEntity(url, AladinApiResponse.class);

		return response.getBody().getItem().stream()
			.map(AladinBookResponseDto::from)
			.collect(Collectors.toList());

	}

	public AladinBookResponseDto getBookByIsbn(String isbn) {
		List<AladinBookResponseDto> books = searchBooks(isbn);
		return books.getFirst();
	}
}
