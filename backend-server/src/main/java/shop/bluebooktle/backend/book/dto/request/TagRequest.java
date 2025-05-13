package shop.bluebooktle.backend.book.dto.request;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TagRequest {
	@NotBlank
	@Length(max = 20)
	private String name;
}
