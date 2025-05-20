package shop.bluebooktle.common.dto.book.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

@Getter
@Value
@Builder
@AllArgsConstructor
public class BookUpdateRequest {

	@NotBlank(message = "도서 제목은 필수 값입니다.")
	String title;

	@NotBlank(message = "도서 설명은 필수 값입니다.")
	String description;
}