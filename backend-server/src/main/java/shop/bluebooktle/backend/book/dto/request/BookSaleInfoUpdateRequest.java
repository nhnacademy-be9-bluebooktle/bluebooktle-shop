package shop.bluebooktle.backend.book.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import shop.bluebooktle.common.dto.book.BookSaleInfoState;

@Getter
@Value
@Builder
@AllArgsConstructor
public class BookSaleInfoUpdateRequest {
	@NotNull(message = "도서 ID는 필수 값입니다.")
	@Positive(message = "도서 ID는 양수여야 합니다.")
	Long bookId;

	@Positive(message = "가격은 0보다 커야합니다.")
	BigDecimal price;

	@PositiveOrZero(message = "할인가격은 0 이상이어야 합니다.")
	BigDecimal salePrice;

	@PositiveOrZero(message = "재고 수량은 0 이상이어야 합니다.")
	Integer stock;

	Boolean isPackable;

	@Builder.Default
	BookSaleInfoState bookSaleInfoState = BookSaleInfoState.AVAILABLE;

}