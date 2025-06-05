package shop.bluebooktle.common.dto.book.request.author;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class AuthorRequest {
	Long id;
	@NotBlank
	@Length(max = 50)
	String name;
}
