package shop.bluebooktle.backend.book.adapter;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import shop.bluebooktle.common.dto.book.response.AladinApiResponse;
import shop.bluebooktle.common.exception.book.AladinBookNotFoundException;

@Component
public class AladinAdaptor {

	private final RestTemplate restTemplate = new RestTemplate();

	private final String TTB_KEY = "ttbsua64581504003";
	private final String BASE_URL = "http://www.aladin.co.kr/ttb/api/ItemSearch.aspx";

	/** 기존 단순 검색 그대로 유지 */
	public AladinApiResponse searchBooks(String query) {
		return searchBooks(query, 1, 10);
	}

	/**
	 * 페이징 지원 검색
	 * @param query 키워드
	 * @param start 검색 시작 인덱스 (1부터)
	 * @param maxResults 한 번에 가져올 개수
	 */
	public AladinApiResponse searchBooks(String query, int start, int maxResults) {
		String url = UriComponentsBuilder
			.fromHttpUrl(BASE_URL)
			.queryParam("ttbkey", TTB_KEY)
			.queryParam("Query", query)
			.queryParam("QueryType", "Title")
			.queryParam("start", start)
			.queryParam("display", maxResults)
			.queryParam("SearchTarget", "Book")
			.queryParam("output", "js")
			.queryParam("Version", "20131101")
			.queryParam("Cover", "Big")
			.build()
			.toUriString();

		ResponseEntity<AladinApiResponse> resp =
			restTemplate.getForEntity(url, AladinApiResponse.class);
		if (resp.getBody() == null) {
			throw new AladinBookNotFoundException(query);
		}
		return resp.getBody();
	}
}