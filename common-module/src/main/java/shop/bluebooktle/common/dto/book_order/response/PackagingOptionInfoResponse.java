package shop.bluebooktle.common.dto.book_order.response;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PackagingOptionInfoResponse {
	Long packagingOptionId;
	String name;
	BigDecimal price;
}
