package shop.bluebooktle.common.dto.book.request;

import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class BookAllRegisterByAladinRequest {
	@NotBlank(message = "isbn를 입력해주세요")
	@Size(min = 13, max = 13, message = "isbn은 13자여야 합니다")
	String isbn;

	@NotNull(message = "재고를 입력해주세요")
	@Min(value = 0, message = "재고는 0 이상이어야 합니다.")
	Integer stock;

	Boolean isPackable;

	@NotNull(message = "판매 상태를 입력해주세요.")
	BookSaleInfo.State state;

	@NotEmpty(message = "카테고리는 최소 1개 이상 필요합니다.")
	List<@Positive(message = "카테고리 ID는 양수값이어야 합니다.") Long> categoryIdList;

	List<@Positive(message = "태그 ID는 양수값이어야 합니다.") Long> tagIdList;
}
