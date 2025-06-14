package shop.bluebooktle.common.dto.order.request;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.bluebooktle.common.domain.order.AdminOrderSearchType;
import shop.bluebooktle.common.domain.order.OrderStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminOrderSearchRequest {
	private AdminOrderSearchType searchKeywordType;
	private String searchKeyword;
	private OrderStatus orderStatusFilter;
	private LocalDate startDate;
	private LocalDate endDate;
	private String paymentMethodFilter;
}
