package shop.bluebooktle.backend.order.repository.impl;

import static shop.bluebooktle.backend.book.entity.QBook.*;
import static shop.bluebooktle.backend.book_order.entity.QBookOrder.*;
import static shop.bluebooktle.backend.order.entity.QDeliveryRule.*;
import static shop.bluebooktle.backend.order.entity.QOrder.*;
import static shop.bluebooktle.backend.order.entity.QOrderState.*;
import static shop.bluebooktle.backend.order.entity.QRefund.*;
import static shop.bluebooktle.backend.payment.entity.QPayment.*;
import static shop.bluebooktle.backend.payment.entity.QPaymentDetail.*;
import static shop.bluebooktle.backend.payment.entity.QPaymentType.*;
import static shop.bluebooktle.backend.point.entity.QPaymentPointHistory.*;
import static shop.bluebooktle.common.entity.auth.QUser.*;
import static shop.bluebooktle.common.entity.point.QPointHistory.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book_order.entity.QBookOrder;
import shop.bluebooktle.backend.book_order.entity.QOrderPackaging;
import shop.bluebooktle.backend.book_order.entity.QPackagingOption;
import shop.bluebooktle.backend.order.entity.Order;
import shop.bluebooktle.backend.order.repository.OrderQueryRepository;
import shop.bluebooktle.common.domain.order.AdminOrderSearchType;
import shop.bluebooktle.common.domain.order.OrderStatus;
import shop.bluebooktle.common.dto.order.request.AdminOrderSearchRequest;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepositoryImpl implements OrderQueryRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public Optional<Order> findFullOrderDetailsById(Long orderId) {
		Order foundOrder = queryFactory
			.selectFrom(order)
			.leftJoin(order.user, user).fetchJoin()
			.leftJoin(order.orderState, orderState).fetchJoin()
			.leftJoin(order.deliveryRule, deliveryRule).fetchJoin()
			.leftJoin(order.bookOrders, bookOrder).fetchJoin()
			.where(order.id.eq(orderId))
			.fetchOne();
		return Optional.ofNullable(foundOrder);
	}

	@Override
	public Optional<Order> findOrderDetailsByOrderKey(String orderKey) {
		Order result = queryFactory
			.selectFrom(order)
			.leftJoin(order.user, user).fetchJoin()
			.leftJoin(order.orderState, orderState).fetchJoin()
			.leftJoin(order.bookOrders, bookOrder).fetchJoin()
			.leftJoin(order.payment, payment).fetchJoin()
			.leftJoin(payment.paymentDetail, paymentDetail).fetchJoin()
			.leftJoin(paymentDetail.paymentType, paymentType).fetchJoin()
			.leftJoin(bookOrder.book, book).fetchJoin()
			.where(order.orderKey.eq(orderKey))
			.fetchOne();
		return Optional.ofNullable(result);
	}

	@Override
	public Page<Order> searchOrders(AdminOrderSearchRequest searchRequest, Pageable pageable) {
		JPAQuery<Order> query = queryFactory
			.select(order)
			.from(order)
			.leftJoin(order.user, user)
			.leftJoin(order.orderState, orderState)
			.leftJoin(order.payment, payment)
			.leftJoin(payment.paymentDetail, paymentDetail)
			.leftJoin(paymentDetail.paymentType, paymentType)
			.where(
				searchKeyword(searchRequest.searchKeywordType(), searchRequest.searchKeyword()),
				orderStatusFilter(searchRequest.orderStatusFilter()),
				paymentMethodFilter(searchRequest.paymentMethodFilter()),
				dateRangeFilter(searchRequest.startDate(), searchRequest.endDate())
			)
			.orderBy(order.createdAt.desc());

		List<Order> orders = query
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		JPAQuery<Long> countQuery = queryFactory
			.select(order.countDistinct())
			.from(order)
			.leftJoin(order.user, user)
			.leftJoin(order.orderState, orderState)
			.leftJoin(order.payment, payment)
			.leftJoin(payment.paymentDetail, paymentDetail)
			.leftJoin(paymentDetail.paymentType, paymentType)
			.where(
				searchKeyword(searchRequest.searchKeywordType(), searchRequest.searchKeyword()),
				orderStatusFilter(searchRequest.orderStatusFilter()),
				paymentMethodFilter(searchRequest.paymentMethodFilter()),
				dateRangeFilter(searchRequest.startDate(), searchRequest.endDate())
			);

		long total = countQuery.fetchOne();

		return new PageImpl<>(orders, pageable, total);
	}

	private BooleanExpression searchKeyword(AdminOrderSearchType type, String keyword) {
		if (!StringUtils.hasText(keyword) || type == null) {
			return null;
		}
		return switch (type) {
			case ORDER_KEY -> order.orderKey.containsIgnoreCase(keyword);
			case ORDERER_NAME -> order.ordererName.containsIgnoreCase(keyword);
			case ORDERER_LOGIN_ID -> user.loginId.containsIgnoreCase(keyword);
			case RECEIVER_NAME -> order.receiverName.containsIgnoreCase(keyword);
			case PRODUCT_NAME -> JPAExpressions
				.selectFrom(bookOrder)
				.where(
					bookOrder.order.id.eq(order.id),
					bookOrder.book.title.containsIgnoreCase(keyword)
				).exists();
		};
	}

	private BooleanExpression orderStatusFilter(OrderStatus status) {
		if (status == null) {
			return null;
		}
		return order.orderState.state.eq(status);
	}

	private BooleanExpression paymentMethodFilter(String method) {
		if (!StringUtils.hasText(method)) { // 수정된 코드
			return null;
		}
		return payment.paymentDetail.paymentType.method.eq(method);
	}

	private BooleanExpression dateRangeFilter(LocalDate startDate, LocalDate endDate) {
		if (startDate == null || endDate == null) {
			return null;
		}
		return order.createdAt.between(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX));
	}

	@Override
	public Optional<Order> findAdminOrderDetailsByOrderId(Long orderId) {
		Order result = queryFactory
			.selectFrom(order)
			.leftJoin(order.user, user).fetchJoin()
			.leftJoin(order.orderState, orderState).fetchJoin()
			.leftJoin(order.bookOrders, bookOrder).fetchJoin()
			.leftJoin(order.payment, payment).fetchJoin()
			.leftJoin(payment.paymentDetail, paymentDetail).fetchJoin()
			.leftJoin(paymentDetail.paymentType, paymentType).fetchJoin()
			.leftJoin(bookOrder.book, book).fetchJoin()
			.leftJoin(order.refund, refund).fetchJoin()
			.where(order.id.eq(orderId))
			.fetchOne();
		return Optional.ofNullable(result);
	}

	@Override
	public Optional<Order> findOrderForRefund(Long orderId) {
		Order result = queryFactory
			.selectFrom(order)
			.leftJoin(order.orderState, orderState).fetchJoin()
			.leftJoin(order.user, user).fetchJoin()
			.leftJoin(order.payment, payment).fetchJoin()
			.leftJoin(payment.paymentDetail, paymentDetail).fetchJoin()
			.leftJoin(payment.paymentPointHistory, paymentPointHistory).fetchJoin()
			.leftJoin(paymentPointHistory.pointHistory, pointHistory).fetchJoin()
			.leftJoin(order.refund, refund).fetchJoin()
			.where(order.id.eq(orderId))
			.fetchOne();

		return Optional.ofNullable(result);
	}

	@Override
	public BigDecimal findTotalPackagingPriceByOrderId(Long orderId) {
		QOrderPackaging orderPackaging = QOrderPackaging.orderPackaging;
		QPackagingOption packagingOption = QPackagingOption.packagingOption;
		QBookOrder bookOrder = QBookOrder.bookOrder;

		Integer totalPackagingPrice = queryFactory
			.select(
				orderPackaging.quantity.multiply(packagingOption.price).sum()
			)
			.from(orderPackaging)
			.join(orderPackaging.packagingOption, packagingOption)
			.join(orderPackaging.bookOrder, bookOrder)
			.where(bookOrder.order.id.eq(orderId))
			.fetchOne();

		return totalPackagingPrice != null ? BigDecimal.valueOf(totalPackagingPrice) : BigDecimal.ZERO;
	}
}