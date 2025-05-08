package shop.bluebooktle.backend.book.dto;

import java.util.List;

import lombok.Data;
import lombok.Getter;

@Data
public class AladinApiResponse {
	private List<AladinBookItem> item;
}
