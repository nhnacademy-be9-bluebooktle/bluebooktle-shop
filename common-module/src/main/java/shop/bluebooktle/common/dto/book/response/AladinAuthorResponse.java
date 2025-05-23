package shop.bluebooktle.common.dto.book.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AladinAuthorResponse {
	private Integer authorId;
	private String authorName;
}
