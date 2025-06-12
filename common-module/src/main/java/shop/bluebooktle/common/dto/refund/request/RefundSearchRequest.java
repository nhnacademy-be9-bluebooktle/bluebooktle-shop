package shop.bluebooktle.common.dto.refund.request;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import shop.bluebooktle.common.domain.refund.RefundSearchType;
import shop.bluebooktle.common.domain.refund.RefundStatus;

public record RefundSearchRequest(
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	LocalDate startDate,

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	LocalDate endDate,

	RefundStatus status,
	RefundSearchType searchType,
	String keyword
) {
}