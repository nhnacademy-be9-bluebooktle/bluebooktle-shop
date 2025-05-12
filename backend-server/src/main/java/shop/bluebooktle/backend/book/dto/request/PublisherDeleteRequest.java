package shop.bluebooktle.backend.book.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PublisherDeleteRequest {
	private Long publisherId;
}
