package shop.bluebooktle.common.dto.book.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import shop.bluebooktle.common.dto.book.request.AladinBookItem;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AladinApiResponse {
	private List<AladinBookItem> item;
}
