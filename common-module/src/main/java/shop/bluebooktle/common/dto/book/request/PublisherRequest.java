package shop.bluebooktle.common.dto.book.request;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class PublisherRequest {
	@NotBlank
	@Length(max = 20)
	private String name;
}
