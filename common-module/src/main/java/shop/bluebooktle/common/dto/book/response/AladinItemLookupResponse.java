package shop.bluebooktle.common.dto.book.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AladinItemLookupResponse {
	private List<AladinBookItemBySearch> item;
}
