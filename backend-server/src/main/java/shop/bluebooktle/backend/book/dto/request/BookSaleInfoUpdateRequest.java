package shop.bluebooktle.backend.book.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import shop.bluebooktle.backend.book.entity.BookSaleInfo;

@Getter
@Value
@Builder
@AllArgsConstructor
public class BookSaleInfoUpdateRequest {

	@Positive(message = "가격은 0보다 커야합니다.")
	BigDecimal price;

	@PositiveOrZero(message = "할인가격은 0 이상이어야 합니다.")
	BigDecimal salePrice;

	@PositiveOrZero(message = "재고 수량은 0 이상이어야 합니다.")
	Integer stock;

	Boolean isPackable;

	BookSaleInfo.State state;

	public BookSaleInfo toEntity(BookSaleInfo existingEntity) {
		return BookSaleInfo.builder()
			.id(existingEntity.getId()) // 기존 ID 유지
			.book(existingEntity.getBook()) // 기존 Book 정보 유지
			.price(this.price != null ? this.price : existingEntity.getPrice()) // 요청 데이터가 null이면 기존 데이터 유지
			.salePrice(this.salePrice != null ? this.salePrice : existingEntity.getSalePrice())
			.stock(this.stock != null ? this.stock : existingEntity.getStock())
			.isPackable(this.isPackable != null ? this.isPackable : existingEntity.isPackable())
			.salePercentage(existingEntity.getSalePercentage()) // 할인율은 변경하지 않음
			.state(this.state != null ? this.state : existingEntity.getState()) // 상태 업데이트 또는 기존 상태 유지
			.viewCount(existingEntity.getViewCount()) // 조회 수는 기존 값을 유지
			.searchCount(existingEntity.getSearchCount()) // 검색 수 그대로 유지
			.build();
	}
}