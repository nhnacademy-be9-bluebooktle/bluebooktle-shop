package shop.bluebooktle.backend.order.repository.impl;

import static shop.bluebooktle.backend.book_order.entity.QBookOrder.*;
import static shop.bluebooktle.backend.order.entity.QDeliveryRule.*;
import static shop.bluebooktle.backend.order.entity.QOrder.*;
import static shop.bluebooktle.backend.order.entity.QOrderState.*;
import static shop.bluebooktle.common.entity.auth.QUser.*;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.order.entity.Order;
import shop.bluebooktle.backend.order.repository.OrderQueryRepository;

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
}