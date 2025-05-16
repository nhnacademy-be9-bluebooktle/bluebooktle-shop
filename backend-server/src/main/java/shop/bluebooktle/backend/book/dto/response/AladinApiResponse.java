package shop.bluebooktle.backend.book.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import shop.bluebooktle.backend.book.dto.request.AladinBookItem;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AladinApiResponse {
	private List<AladinBookItem> item;
}
