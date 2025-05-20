package shop.bluebooktle.backend.book.adapter;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import shop.bluebooktle.backend.book.dto.response.AladinApiResponse;
import shop.bluebooktle.common.exception.book.AladinBookNotFoundException;

@Component
public class AladinAdaptor {

	private final RestTemplate restTemplate = new RestTemplate();

	private final String TTB_KEY = "ttbsua64581504003";
	private final String BASE_URL = "http://www.aladin.co.kr/ttb/api/ItemSearch.aspx";

	public AladinApiResponse searchBooks(String query) {
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

		if (response.getBody() == null) {
			throw new AladinBookNotFoundException(query);
		}

		return response.getBody();
	}

}
