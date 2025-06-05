package shop.bluebooktle.common.dto.book.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import shop.bluebooktle.common.dto.book.BookSaleInfoState;

@Getter
@Value
@Builder
@AllArgsConstructor
public class BookUpdateRequest {

	@NotBlank(message = "도서 제목은 필수 값입니다.")
	String title;

	@NotBlank(message = "도서 설명은 필수 값입니다.")
	String description;

	String index;

	LocalDate publishDate;

	@NotNull(message = "판매 가격은 필수 값입니다.")
	BigDecimal price;

	@NotNull(message = "할인 가격은 필수 값입니다.")
	BigDecimal salePrice;

	@NotNull(message = "재고 수는 필수 값입니다.")
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

	boolean isAladinImg;

	MultipartFile imageFile;
}