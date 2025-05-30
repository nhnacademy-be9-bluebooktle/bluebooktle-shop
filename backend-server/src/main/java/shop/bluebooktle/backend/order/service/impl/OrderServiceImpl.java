package shop.bluebooktle.backend.order.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.BookImg;
import shop.bluebooktle.backend.book.entity.Img;
import shop.bluebooktle.backend.book_order.entity.BookOrder;
import shop.bluebooktle.backend.book_order.entity.OrderPackaging;
import shop.bluebooktle.backend.book_order.entity.PackagingOption;
import shop.bluebooktle.backend.book_order.entity.UserCouponBookOrder;
import shop.bluebooktle.backend.coupon.entity.Coupon;
import shop.bluebooktle.backend.coupon.entity.CouponType;
import shop.bluebooktle.backend.coupon.entity.UserCoupon;
import shop.bluebooktle.backend.coupon.service.CouponCalculationService;
import shop.bluebooktle.backend.order.entity.Order;
import shop.bluebooktle.backend.order.repository.OrderRepository;
import shop.bluebooktle.backend.order.service.OrderService;
import shop.bluebooktle.backend.user.repository.UserRepository;
import shop.bluebooktle.common.domain.order.OrderStatus;
import shop.bluebooktle.common.dto.coupon.CalculatedDiscountDetails;
import shop.bluebooktle.common.dto.coupon.response.AppliedCouponResponse;
import shop.bluebooktle.common.dto.order.response.OrderConfirmDetailResponse;
import shop.bluebooktle.common.dto.order.response.OrderItemResponse;
import shop.bluebooktle.common.dto.order.response.OrderPackagingResponse;
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.exception.order.OrderNotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

	private final OrderRepository orderRepository;
	private final UserRepository userRepository;
	private final CouponCalculationService couponCalculationService;

	@Override
	public List<Order> getUserOrders(User user, OrderStatus status, LocalDateTime start, LocalDateTime end,
		Pageable pageable) {
		if (status == null && start != null && end != null) {
			return orderRepository.findByUserAndCreatedAtBetween(user, start, end, pageable);
		} else if (status != null && start == null && end == null) {
			return orderRepository.findByUserAndOrderState_State(user, status, pageable);
		} else if (status != null && start != null && end != null) {
			return orderRepository.findByUserAndOrderState_StateAndCreatedAtBetween(user, status, start, end, pageable);
		} else {
			return orderRepository.findByUser(user, pageable);
		}
	}

	@Override
	public Order getOrderByOrderKey(String orderKey) {
		return orderRepository.findByOrderKey(orderKey)
			.orElseThrow(() -> new OrderNotFoundException("주문을 찾을 수 없습니다."));
	}

	@Override
	public OrderConfirmDetailResponse getOrderDetailsForConfirmation(Long orderId, Long userId) {

		Order order = orderRepository.findFullOrderDetailsById(orderId)
			.orElseThrow(() -> new OrderNotFoundException("주문을 찾을 수 없거나 접근 권한이 없습니다."));

		// 비회원 주문인 경우 userId가 null일 수 있음
		User user = order.getUser();
		if ((user == null && userId != null) || (user != null && !Objects.equals(user.getId(), userId))) {
			throw new OrderNotFoundException("주문을 찾을 수 없거나 접근 권한이 없습니다.");
		}

		List<OrderItemResponse> orderItems = (order.getBookOrders() == null) ? Collections.emptyList() :
			order.getBookOrders().stream()
				.map(this::mapToOrderItemResponse)
				.collect(Collectors.toList());

		List<UserCouponBookOrder> appliedCouponEntities = order.getUserCouponBookOrders();
		if (appliedCouponEntities == null) {
			appliedCouponEntities = Collections.emptyList();
		}

		List<AppliedCouponResponse> usedCoupons = appliedCouponEntities.stream()
			.map(ucbo -> {
				CalculatedDiscountDetails discountDetails = couponCalculationService.calculateDiscountDetails(ucbo);

				UserCoupon userCoupon = ucbo.getUserCoupon();
				Coupon coupon = userCoupon.getCoupon();
				CouponType couponType = coupon.getCouponType();

				return AppliedCouponResponse.builder()
					.userCouponId(userCoupon.getId())
					.availableStartAt(userCoupon.getAvailableStartAt())
					.availableEndAt(userCoupon.getAvailableEndAt())
					.couponName(coupon.getCouponName())
					.couponTypeName(couponType.getName())
					.target(couponType.getTarget())
					.policyTypeDescription(discountDetails.getPolicyTypeDescription())
					.originalDiscountValue(discountDetails.getOriginalDiscountValue())
					.maxDiscountAmountForPercentage(discountDetails.getMaxDiscountAmountForPercentage())
					.appliedDiscountAmount(discountDetails.getAppliedDiscountAmount())
					.build();
			})
			.collect(Collectors.toList());

		BigDecimal subTotal = orderItems.stream()
			.filter(item -> item.getPrice() != null && item.getQuantity() > 0)
			.map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
			.reduce(BigDecimal.ZERO, BigDecimal::add);

		BigDecimal packagingTotal = orderItems.stream()
			.flatMap(item -> item.getPackagingOptions() != null ? item.getPackagingOptions().stream() :
				Collections.<OrderPackagingResponse>emptyList().stream())
			.filter(pkg -> pkg.getPrice() != null && pkg.getQuantity() > 0)
			.map(pkg -> pkg.getPrice().multiply(BigDecimal.valueOf(pkg.getQuantity())))
			.reduce(BigDecimal.ZERO, BigDecimal::add);

		BigDecimal couponDiscountTotal = usedCoupons.stream()
			.map(AppliedCouponResponse::getAppliedDiscountAmount)
			.filter(java.util.Objects::nonNull)
			.reduce(BigDecimal.ZERO, BigDecimal::add);

		BigDecimal deliveryFee = order.getDeliveryFee() == null ? BigDecimal.ZERO : order.getDeliveryFee();
		BigDecimal totalAmount = subTotal
			.add(packagingTotal)
			.add(deliveryFee)
			.subtract(couponDiscountTotal);

		return OrderConfirmDetailResponse.builder()
			.orderId(order.getId())
			.orderName(order.getOrderName())
			.receiverName(order.getReceiverName())
			.receiverPhoneNumber(order.getReceiverPhoneNumber())
			.address(order.getAddress())
			.detailAddress(order.getDetailAddress())
			.postalCode(order.getPostalCode())
			.deliveryFee(deliveryFee)
			.orderItems(orderItems)
			.appliedCoupons(usedCoupons)
			.userPointBalance(user != null ? user.getPointBalance() : BigDecimal.ZERO)
			.subTotal(subTotal)
			.packagingTotal(packagingTotal)
			.couponDiscountTotal(couponDiscountTotal)
			.totalAmount(totalAmount)
			.build();
	}

	private OrderItemResponse mapToOrderItemResponse(BookOrder bookOrder) {
		Book book = bookOrder.getBook();
		if (book == null) {
			log.warn("BookOrder ID {} 에 연결된 Book 정보가 없습니다.", bookOrder.getId());
			return OrderItemResponse.builder()
				.bookOrderId(bookOrder.getId())
				.quantity(bookOrder.getQuantity())
				.price(bookOrder.getPrice())
				.packagingOptions(Collections.emptyList())
				.bookTitle("알 수 없는 상품")
				.build();
		}

		String thumbnailUrl = (book.getBookImgs() == null) ? null :
			book.getBookImgs().stream()
				.filter(bi -> bi != null && bi.isThumbnail() && bi.getImg() != null)
				.map(BookImg::getImg)
				.map(Img::getImgUrl)
				.findFirst()
				.orElse(null);

		List<OrderPackagingResponse> packagingOptions =
			(bookOrder.getOrderPackagings() == null) ? Collections.emptyList() :
				bookOrder.getOrderPackagings().stream()
					.map(this::mapToOrderPackagingResponse)
					.collect(Collectors.toList());

		return OrderItemResponse.builder()
			.bookOrderId(bookOrder.getId())
			.bookId(book.getId())
			.bookTitle(book.getTitle())
			.bookThumbnailUrl(thumbnailUrl)
			.quantity(bookOrder.getQuantity())
			.price(bookOrder.getPrice())
			.packagingOptions(packagingOptions)
			.build();
	}

	private OrderPackagingResponse mapToOrderPackagingResponse(OrderPackaging orderPackaging) {
		if (orderPackaging == null || orderPackaging.getPackagingOption() == null) {
			log.warn("OrderPackaging 또는 PackagingOption 정보가 없습니다.");
			return OrderPackagingResponse.builder().name("포장 정보 없음").price(BigDecimal.ZERO).quantity(0).build();
		}
		PackagingOption option = orderPackaging.getPackagingOption();
		return OrderPackagingResponse.builder()
			.packageId(option.getId())
			.name(option.getName())
			.price(option.getPrice())
			.quantity(orderPackaging.getQuantity())
			.build();
	}

}