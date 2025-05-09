package shop.bluebooktle.backend.book.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AladinApiResponse {
	private List<AladinBookItem> item;
}
