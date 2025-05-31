package shop.bluebooktle.common.dto.order.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderPackagingResponse {
	private Long packageId;
	private String name;
	private BigDecimal price;
	private int quantity;
}