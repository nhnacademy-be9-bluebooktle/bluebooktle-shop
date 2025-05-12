package shop.bluebooktle.backend.book.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PublisherInfoRequest {
	@NotNull
	private Long publisherId;
}
