package shop.bluebooktle.common.dto.book_order.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class PackagingOptionInfoResponse {
	Long id;
	String name;
	BigDecimal price;
}
