package shop.bluebooktle.common.dto.book.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class PublisherInfoResponse {
	private Long id;
	private String name;

}
