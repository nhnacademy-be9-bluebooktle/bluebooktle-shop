package shop.bluebooktle.backend.order.repository.impl;

import static shop.bluebooktle.backend.order.entity.QDeliveryRule.*;
import static shop.bluebooktle.backend.order.entity.QOrder.*;
import static shop.bluebooktle.backend.order.entity.QRefund.*;
import static shop.bluebooktle.common.entity.auth.QUser.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.order.entity.Refund;
import shop.bluebooktle.backend.order.repository.RefundQueryRepository;
import shop.bluebooktle.common.domain.refund.RefundSearchType;
import shop.bluebooktle.common.domain.refund.RefundStatus;
import shop.bluebooktle.common.dto.refund.request.RefundSearchRequest;

@RequiredArgsConstructor
public class RefundQueryRepositoryImpl implements RefundQueryRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<Refund> searchRefunds(RefundSearchRequest request, Pageable pageable) {
		JPAQuery<Refund> query = queryFactory
			.selectFrom(refund)
			.join(refund.order, order).fetchJoin()
			.leftJoin(order.user, user).fetchJoin()
			.where(
				dateRangeFilter(request.startDate(), request.endDate()),
				statusFilter(request.status()),
				searchKeyword(request.searchType(), request.keyword())
			)
			.orderBy(refund.date.desc());

		List<Refund> refunds = query
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		long total = query.fetchCount();

		return new PageImpl<>(refunds, pageable, total);
	}

	@Override
	public Optional<Refund> findRefundDetailsById(Long refundId) {
		Refund result = queryFactory
			.selectFrom(refund)
			.join(refund.order, order).fetchJoin()
			.leftJoin(order.user, user).fetchJoin()
			.leftJoin(order.deliveryRule, deliveryRule).fetchJoin()
			.where(refund.id.eq(refundId))
			.fetchOne();
		return Optional.ofNullable(result);
	}

	private BooleanExpression dateRangeFilter(LocalDate startDate, LocalDate endDate) {
		if (startDate == null || endDate == null)
			return null;
		return refund.date.between(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX));
	}

	private BooleanExpression statusFilter(RefundStatus status) {
		return (status != null) ? refund.status.eq(status) : null;
	}

	private BooleanExpression searchKeyword(RefundSearchType type, String keyword) {
		if (!StringUtils.hasText(keyword) || type == null)
			return null;
		return switch (type) {
			case ORDER_KEY -> order.orderKey.containsIgnoreCase(keyword);
			case ORDERER_NAME -> order.ordererName.containsIgnoreCase(keyword);
		};
	}
}