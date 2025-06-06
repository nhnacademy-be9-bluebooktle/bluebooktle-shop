package shop.bluebooktle.common.dto.book.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.bluebooktle.common.dto.book.BookSaleInfoState;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BookFormRequest {
	@NotBlank(message = "도서 제목은 필수 값입니다.")
	String title;

	@NotBlank(message = "ISBN 값은 필수 값입니다.")
	@Size(min = 13, max = 13, message = "ISBN 값은 13자여야 합니다")
	String isbn;

	@NotBlank(message = "도서 설명은 필수 값입니다.")
	String description;

	String index;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@NotNull
	LocalDate publishDate;

	@NotNull(message = "판매 가격은 필수 값입니다.")
	@Positive(message = "판매 가격은 양수값이어야 합니다.")
	@Digits(integer = 8, fraction = 0, message = "판매 가격은 최대 99999999.99까지 가능합니다.")
	BigDecimal price;

	@NotNull(message = "할인 가격은 필수 값입니다.")
	@Positive(message = "할인 가격은 양수값이어야 합니다.")
	@Digits(integer = 8, fraction = 0, message = "판매 가격은 최대 99999999.99까지 가능합니다.")
	BigDecimal salePrice;

	@NotNull(message = "재고 수는 필수 값입니다.")
	@Positive(message = "재고는 양수값이어야 합니다.")
	Integer stock;

	Boolean isPackable;

	@NotNull(message = "판매상태는 필수 값입니다.")
	BookSaleInfoState state;

	@NotEmpty(message = "작가는 최소 1개 이상 필요합니다.")
	List<@Positive(message = "작가 ID는 양수값이어야 합니다.") Long> authorIdList;

	@NotEmpty(message = "출판사는 최소 1개 이상 필요합니다.")
	List<@Positive(message = "출판사 ID는 양수값이어야 합니다.") Long> publisherIdList;

	@NotEmpty(message = "카테고리는 최소 1개 이상 필요합니다.")
	List<@Positive(message = "카테고리 ID는 양수값이어야 합니다.") Long> categoryIdList;

	List<@Positive(message = "태그 ID는 양수값이어야 합니다.") Long> tagIdList;

	@NotNull
	MultipartFile imageFile;
}
