package shop.bluebooktle.common.dto.order.request;

import java.time.LocalDate;

import shop.bluebooktle.common.domain.order.AdminOrderSearchType;
import shop.bluebooktle.common.domain.order.OrderStatus;

public record AdminOrderSearchRequest(
	AdminOrderSearchType searchKeywordType,
	String searchKeyword,
	OrderStatus orderStatusFilter,
	LocalDate startDate,
	LocalDate endDate,
	String paymentMethodFilter
) {
}
