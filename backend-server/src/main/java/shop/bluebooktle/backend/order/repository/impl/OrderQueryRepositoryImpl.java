// package shop.bluebooktle.backend.order.repository.impl; // Repository 패키지 경로에 맞게 조정
//
// import static shop.bluebooktle.backend.book.entity.QBook.*;
// import static shop.bluebooktle.backend.book.entity.QBookImg.*;
// import static shop.bluebooktle.backend.book.entity.QImg.*;
// import static shop.bluebooktle.backend.book_order.entity.QBookOrder.*;
// import static shop.bluebooktle.backend.book_order.entity.QOrderPackaging.*;
// import static shop.bluebooktle.backend.book_order.entity.QPackagingOption.*;
// import static shop.bluebooktle.backend.book_order.entity.QUserCouponBookOrder.*;
// import static shop.bluebooktle.backend.coupon.entity.QCoupon.*;
// import static shop.bluebooktle.backend.coupon.entity.QCouponType.*;
// import static shop.bluebooktle.backend.coupon.entity.QUserCoupon.*;
// import static shop.bluebooktle.backend.order.entity.QOrder.*;
// import static shop.bluebooktle.backend.order.entity.QOrderState.*;
//
// import java.math.BigDecimal;
// import java.util.List;
// import java.util.Map;
// import java.util.Optional;
// import java.util.stream.Collectors;
//
// import org.springframework.stereotype.Repository;
//
// import com.querydsl.core.types.Projections;
// import com.querydsl.jpa.impl.JPAQueryFactory;
//
// import lombok.RequiredArgsConstructor;
// import shop.bluebooktle.backend.order.entity.Order;
// import shop.bluebooktle.backend.order.repository.OrderQueryRepository;
// import shop.bluebooktle.common.dto.order.response.OrderItemResponse;
// import shop.bluebooktle.common.dto.order.response.OrderPackagingResponse;
// import shop.bluebooktle.common.dto.order.response.UsedCouponResponse;
//
// @Repository
// @RequiredArgsConstructor
// public class OrderQueryRepositoryImpl implements OrderQueryRepository {
//
// 	private final JPAQueryFactory queryFactory;
//
// 	@Override
// 	public Optional<Order> findOrderBaseByIdAndUserId(Long orderId, Long userId) {
// 		// 기본 주문 정보와 사용자 검증
// 		// @EntityGraph와 유사한 효과를 내기 위해 필요한 join을 명시할 수 있으나,
// 		// 서비스에서 User를 별도 조회하므로 여기서는 Order만 조회
// 		Order foundOrder = queryFactory
// 			.selectFrom(order)
// 			.join(order.orderState, orderState).fetchJoin() // 주문 상태는 자주 사용되므로 fetchJoin
// 			.where(order.id.eq(orderId)
// 				.and(order.user.id.eq(userId)))
// 			.fetchOne();
// 		return Optional.ofNullable(foundOrder);
// 	}
//
// 	@Override
// 	public List<OrderItemResponse> findOrderItemsDtoByOrderId(Long orderId) {
// 		// 1. BookOrder 및 관련 Book 정보 조회 (OrderItemResponse 기본 필드)
// 		List<OrderItemResponse> items = queryFactory
// 			.select(Projections.constructor(OrderItemResponse.class,
// 				bookOrder.id,
// 				book.id,
// 				book.title,
// 				// 썸네일 URL을 위한 서브쿼리 또는 별도 로직 (여기서는 null로 처리하고 서비스에서 채우거나, 아래 packagingOptions처럼 별도 조회 후 합침)
// 				// JPQL/QueryDSL에서 스칼라 서브쿼리로 썸네일 URL 가져오는 것이 복잡할 수 있음.
// 				// 여기서는 간단하게 대표 이미지 하나를 가져오는 방식으로 가정.
// 				// 실제로는 BookImg 테이블에서 isThumbnail=true인 첫번째 이미지를 찾아야 함.
// 				// 여기서는 임시로 book.title을 썸네일 자리에 넣고, 실제 썸네일 로직은 서비스에서 처리하거나 아래처럼 별도 조회.
// 				Projections.constant(null, String.class), // ThumbnailUrl placeholder
// 				bookOrder.quantity,
// 				bookOrder.price
// 				// packagingOptions는 아래에서 별도로 조회하여 채움
// 			))
// 			.from(bookOrder)
// 			.join(bookOrder.book, book)
// 			.where(bookOrder.order.id.eq(orderId))
// 			.fetch();
//
// 		// 2. 각 BookOrder에 대한 썸네일 URL 조회 (만약 위에서 처리 못했다면)
// 		// 이 방식은 N+1 발생 가능성이 있으므로, Book 엔티티에 썸네일 필드를 두거나,
// 		// BookOrder 조회 시 @EntityGraph 또는 fetchJoin을 활용하는 것이 좋음.
// 		// 여기서는 items를 순회하며 각 bookId로 썸네일을 조회하는 대신,
// 		// items를 만든 후, 각 item의 bookId를 이용하여 썸네일 정보를 가져와 채워주는 방식.
// 		// 혹은, 서비스 레이어에서 처리. QueryDSL로만 하려면 복잡해질 수 있음.
// 		// 여기서는 위에서 placeholder로 두고, 서비스에서 처리한다고 가정.
//
// 		// 3. 각 BookOrder에 대한 OrderPackagingResponse 목록 조회
// 		List<Long> bookOrderIds = items.stream().map(OrderItemResponse::getBookOrderId).collect(Collectors.toList());
//
// 		if (!bookOrderIds.isEmpty()) {
// 			List<OrderPackagingResponse> allPackagingOptions = queryFactory
// 				.select(Projections.constructor(OrderPackagingResponse.class,
// 					packagingOption.id,
// 					packagingOption.name,
// 					packagingOption.price,
// 					orderPackaging.quantity,
// 					bookOrder.id // 매핑을 위한 bookOrderId 추가
// 				))
// 				.from(orderPackaging)
// 				.join(orderPackaging.packagingOption, packagingOption)
// 				.join(orderPackaging.bookOrder, bookOrder) // bookOrderId를 얻기 위함
// 				.where(orderPackaging.bookOrder.id.in(bookOrderIds))
// 				.fetch();
//
// 			// bookOrderId를 기준으로 그룹화
// 			Map<Long, List<OrderPackagingResponse>> packagingMap = allPackagingOptions.stream()
// 				.collect(Collectors.groupingBy(op -> {
// 					// OrderPackagingResponse에 bookOrderId를 임시로 담아서 사용 후 제거하거나,
// 					// 별도의 내부 클래스/레코드를 사용하여 bookOrderId와 함께 조회 후 그룹화
// 					// 여기서는 OrderPackagingResponse 생성자에 bookOrderId를 임시로 받고,
// 					// 실제 DTO에서는 해당 필드가 없다고 가정. (아래 items.forEach에서 사용)
// 					// 또는 OrderPackagingResponse DTO에 bookOrderId 필드를 추가하고 아래서 사용.
// 					// 예제에서는 생성자에 bookOrderId를 넣었다고 가정 (위 Projection 수정 필요)
// 					// OrderPackagingResponse DTO에 getBookOrderId()가 있다고 가정 (임시)
// 					Long currentBookOrderId = null; // 실제로는 allPackagingOptions의 각 요소에서 bookOrderId를 가져와야 함
// 					// 아래와 같이 실제 DTO에 bookOrderId 필드를 추가했다고 가정하고 코드를 작성.
// 					// (만약 DTO에 필드가 없다면, 튜플이나 Map으로 받아와서 처리)
// 					// for (OrderPackagingResponse opResp : allPackagingOptions) {
// 					//      if (opResp.getBookOrderId() != null) currentBookOrderId = opResp.getBookOrderId();
// 					// } // 이 방식은 좋지 않음.
//
// 					// 가장 좋은 방식은 OrderPackagingResponse 프로젝션 시 bookOrderId를 함께 가져오고,
// 					// items 순회 시 해당 bookOrderId에 맞는 packagingOptions을 필터링하는 것.
// 					// 여기서는 allPackagingOptions를 bookOrderId로 그룹핑.
// 					// OrderPackagingResponse 생성자 Projection에 bookOrder.bookOrderId를 추가했다고 가정하고,
// 					// 해당 필드를 통해 그룹핑 키를 얻어야 함. (이 부분은 DTO 구조에 따라 조정 필요)
// 					return opResp.getBookOrderId(); // OrderPackagingResponse에 getBookOrderId()가 있다고 가정
// 				}));
//
// 			items.forEach(item -> {
// 				// 썸네일 설정 로직 (예시: bookId로 bookImg 조회)
// 				String thumbnailUrl = queryFactory
// 					.select(img.imgUrl)
// 					.from(bookImg)
// 					.join(bookImg.img, img)
// 					.where(bookImg.book.id.eq(item.getBookId()).and(bookImg.isThumbnail.isTrue()))
// 					.fetchFirst();
// 				item.setBookThumbnailUrl(thumbnailUrl); // Setter 사용
//
// 				// 포장 옵션 설정
// 				// item.setPackagingOptions(packagingMap.get(item.getBookOrderId())); // Setter 사용
// 				// packagingMap 키를 Long으로 (bookOrderId) 사용하려면 OrderPackagingResponse 프로젝션 수정 필요.
// 				// 여기서는 임시로 위에서 만든 packagingMap이 BookOrderId를 키로 가진다고 가정.
// 				List<OrderPackagingResponse> pkgs = allPackagingOptions.stream()
// 					.filter(p -> p.getBookOrderId()
// 						.equals(item.getBookOrderId())) // OrderPackagingResponse에 getBookOrderId() 추가 가정
// 					.collect(Collectors.toList());
// 				item.setPackagingOptions(pkgs);
//
// 			});
// 		}
// 		return items;
// 	}
//
// 	@Override
// 	public List<UsedCouponResponse> findUsedCouponsDtoByOrderId(Long orderId) {
// 		return queryFactory
// 			.select(Projections.constructor(UsedCouponResponse.class,
// 				coupon.couponName,
// 				couponType.target.stringValue(), // ENUM을 String으로
// 				Projections.constant(BigDecimal.ZERO) // 할인 금액은 서비스 로직에서 계산 필요
// 			))
// 			.from(userCouponBookOrder)
// 			.join(userCouponBookOrder.userCoupon, userCoupon)
// 			.join(userCoupon.coupon, coupon)
// 			.join(coupon.couponType, couponType)
// 			.where(userCouponBookOrder.order.id.eq(orderId))
// 			.fetch();
// 	}
// }