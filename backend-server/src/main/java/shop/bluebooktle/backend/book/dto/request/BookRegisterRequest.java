package shop.bluebooktle.backend.book.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

@Getter
@Value
@Builder
@AllArgsConstructor
public class BookRegisterRequest {

	@NotBlank(message = "도서 제목은 필수 값입니다.")
	@Positive(message = "도서 ID는 양수여야 합니다.")
	String title;

	@NotBlank(message = "도서 설명은 필수 값입니다.")
	String description;

	LocalDate publishDate;

	@NotBlank(message = "ISBN 값은 필수 값입니다.")
	@Size(min = 13, max = 13, message = "ISBN 값은 13자여야 합니다")
	String isbn;
}