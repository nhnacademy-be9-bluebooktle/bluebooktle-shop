package shop.bluebooktle.backend.book.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import shop.bluebooktle.backend.book.entity.BookSaleInfo;

@Getter
@Value
@Builder
@AllArgsConstructor
public class BookAllRegisterRequest {

	@NotBlank(message = "도서 제목은 필수 값입니다.")
	String title;

	@NotBlank(message = "ISBN 값은 필수 값입니다.")
	@Size(min = 13, max = 13, message = "ISBN 값은 13자여야 합니다")
	String isbn;

	@NotBlank(message = "도서 설명은 필수 값입니다.")
	String description;

	LocalDate publishDate;

	@NotEmpty(message = "작가 이름은 최소 1개 이상 필요합니다.")
	List<@NotBlank(message = "작가 이름은 빈 값일 수 없습니다.") String> author;

	@NotBlank(message = "출판사 이름은 필수 값입니다.")
	String publisher;

	@NotEmpty(message = "카테고리는 최소 1개 이상 필요합니다.")
	List<@NotBlank(message = "카테고리 이름은 빈 값일 수 없습니다.") String> category;

	List<String> imageUrl;

	@NotNull(message = "판매 가격은 필수 값입니다.")
	BigDecimal price;

	@NotNull(message = "할인 가격은 필수 값입니다.")
	BigDecimal salePrice;

	@NotNull(message = "재고 수는 필수 값입니다.")
	Integer stock;

	// @NotNull(message = "할인율은 필수 값입니다.")
	// BigDecimal salePercentage;

	Boolean isPackable;

	@NotNull(message = "판매상태는 필수 값입니다.")
	BookSaleInfo.State state;

	@NotNull(message = "태그는 필수 값입니다.")
	List<String> tag;

}