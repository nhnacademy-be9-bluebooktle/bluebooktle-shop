package shop.bluebooktle.backend.book.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import shop.bluebooktle.backend.book.entity.BookSaleInfo;

@Getter
@Value
@AllArgsConstructor
@Builder
public class BookRegisterByAladinRequest {
	@NotBlank(message = "isbn를 입력해주세요")
	@Size(min = 13, max = 13, message = "isbn은 13자여야 합니다")
	String isbn;

	@NotNull(message = "재고를 입력해주세요")
	@Min(value = 0, message = "재고는 0 이상이어야 합니다.")
	Integer stock;

	Boolean isPackable;

	@NotNull(message = "판매 상태를 입력해주세요.")
	BookSaleInfo.State state;
}
