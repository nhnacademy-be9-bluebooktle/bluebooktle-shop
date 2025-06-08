package shop.bluebooktle.backend.order.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.BookImg;
import shop.bluebooktle.backend.book.entity.BookSaleInfo;
import shop.bluebooktle.backend.book.entity.Img;
import shop.bluebooktle.backend.book.repository.BookRepository;
import shop.bluebooktle.backend.book.repository.BookSaleInfoRepository;
import shop.bluebooktle.backend.book_order.entity.BookOrder;
import shop.bluebooktle.backend.book_order.entity.OrderPackaging;
import shop.bluebooktle.backend.book_order.entity.PackagingOption;
import shop.bluebooktle.backend.book_order.entity.UserCouponBookOrder;
import shop.bluebooktle.backend.book_order.jpa.BookOrderRepository;
import shop.bluebooktle.backend.book_order.jpa.PackagingOptionRepository;
import shop.bluebooktle.backend.coupon.entity.Coupon;
import shop.bluebooktle.backend.coupon.entity.CouponType;
import shop.bluebooktle.backend.coupon.entity.UserCoupon;
import shop.bluebooktle.backend.coupon.service.CouponCalculationService;
import shop.bluebooktle.backend.order.entity.DeliveryRule;
import shop.bluebooktle.backend.order.entity.Order;
import shop.bluebooktle.backend.order.entity.OrderState;
import shop.bluebooktle.backend.order.repository.DeliveryRuleRepository;
import shop.bluebooktle.backend.order.repository.OrderRepository;
import shop.bluebooktle.backend.order.repository.OrderStateRepository;
import shop.bluebooktle.backend.order.service.OrderService;
import shop.bluebooktle.backend.payment.entity.Payment;
import shop.bluebooktle.backend.payment.repository.PaymentRepository;
import shop.bluebooktle.backend.user.repository.UserRepository;
import shop.bluebooktle.common.domain.order.OrderStatus;
import shop.bluebooktle.common.dto.book.BookSaleInfoState;
import shop.bluebooktle.common.dto.coupon.CalculatedDiscountDetails;
import shop.bluebooktle.common.dto.coupon.response.AppliedCouponResponse;
import shop.bluebooktle.common.dto.order.request.OrderCreateRequest;
import shop.bluebooktle.common.dto.order.request.OrderItemRequest;
import shop.bluebooktle.common.dto.order.response.OrderConfirmDetailResponse;
import shop.bluebooktle.common.dto.order.response.OrderHistoryResponse;
import shop.bluebooktle.common.dto.order.response.OrderItemResponse;
import shop.bluebooktle.common.dto.order.response.OrderPackagingResponse;
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;
import shop.bluebooktle.common.exception.auth.UserNotFoundException;
import shop.bluebooktle.common.exception.book.BookNotFoundException;
import shop.bluebooktle.common.exception.book.BookSaleInfoNotFoundException;
import shop.bluebooktle.common.exception.book_order.PackagingOptionNotFoundException;
import shop.bluebooktle.common.exception.order.OrderNotFoundException;
import shop.bluebooktle.common.exception.order.delivery_rule.DeliveryRuleNotFoundException;
import shop.bluebooktle.common.exception.order.order_state.OrderStateNotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

	private final OrderRepository orderRepository;
	private final DeliveryRuleRepository deliveryRuleRepository;
	private final CouponCalculationService couponCalculationService;
	private final UserRepository userRepository;
	private final OrderStateRepository orderStateRepository;
	private final BookRepository bookRepository;
	private final PaymentRepository paymentRepository;
	private final BookOrderRepository bookOrderRepository;
	private final PackagingOptionRepository packagingOptionRepository;
	private final BookSaleInfoRepository bookSaleInfoRepository;

	@Override
	public Page<OrderHistoryResponse> getUserOrders(
		Long userId,
		OrderStatus status,
		Pageable pageable) {

		User user = userRepository.findById(userId)
			.orElseThrow(UserNotFoundException::new);

		Page<Order> orderPage;
		if (status == null) {
			orderPage = orderRepository.findByUser(user, pageable);
		} else {
			orderPage = orderRepository.findByUserAndOrderState_State(user, status, pageable);
		}

		return orderPage.map(o -> {
			BigDecimal paidAmount = o.getOriginalAmount()
				.subtract(Optional.ofNullable(o.getCouponDiscountAmount())
					.orElse(BigDecimal.ZERO))
				.subtract(Optional.ofNullable(o.getPointUseAmount())
					.orElse(BigDecimal.ZERO))
				.add(Optional.ofNullable(o.getDeliveryFee())
					.orElse(BigDecimal.ZERO));

			String thumbnailUrl = o.getBookOrders().stream()
				.findFirst() // 대표 BookOrder 하나를 가져온 뒤
				.flatMap(bookOrder -> {
					Book book = bookOrder.getBook();
					if (book == null)
						return Optional.<BookImg>empty();
					return book.getBookImgs().stream().findFirst();
				})
				.map(BookImg::getImg)       // BookImg 에서 Img 엔티티 꺼내고
				.map(Img::getImgUrl)      // Img 엔티티에서 URL 꺼내기
				.orElse("");               // 없으면 빈 문자열 혹은 기본 URL

			return new OrderHistoryResponse(
				o.getId(),
				o.getCreatedAt(),
				o.getOrderName(),
				paidAmount,
				o.getOrderKey(),
				o.getOrderState().getState(),
				thumbnailUrl
			);
		});
	}

	@Transactional
	public Long createOrder(OrderCreateRequest request) {

		OrderState initialState = orderStateRepository.findByState(OrderStatus.PENDING).orElseThrow(
			OrderStateNotFoundException::new);

		DeliveryRule deliveryRule = deliveryRuleRepository.findById(request.deliveryRuleId()).orElseThrow(
			DeliveryRuleNotFoundException::new);
		User user;
		if (request.userId() != null) {
			user = userRepository.findById(request.userId())
				.orElseThrow(UserNotFoundException::new);
		} else {
			user = null;
		}
		Order order = Order.builder()
			.orderState(initialState)
			.deliveryRule(deliveryRule)
			.user(user)
			.orderName(request.orderName())
			.requestedDeliveryDate(
				request.requestedDeliveryDate() != null ? request.requestedDeliveryDate().atStartOfDay() : null)
			.shippedAt(null)
			.deliveryFee(request.deliveryFee())
			.ordererName(request.ordererName())
			.ordererEmail(request.ordererEmail())
			.ordererPhoneNumber(request.ordererPhoneNumber())
			.receiverName(request.receiverName())
			.receiverEmail(request.receiverEmail())
			.receiverPhoneNumber(request.receiverPhoneNumber())
			.postalCode(request.postalCode())
			.address(request.address())
			.detailAddress(request.detailAddress())
			.couponDiscountAmount(request.couponDiscountAmount())
			.pointUseAmount(request.pointUseAmount())
			.saleDiscountAmount(request.saleDiscountAmount())
			.originalAmount(request.originalAmount())
			.trackingNumber(null)
			.orderKey(request.orderKey())
			.build();

		Order saved = orderRepository.save(order);

		for (OrderItemRequest itemReq : request.orderItems()) {
			createSingleBookOrder(saved, itemReq);
		}

		return saved.getId();
	}

	private void createSingleBookOrder(Order order, OrderItemRequest item) {
		Book book = bookRepository.findById(item.bookId())
			.orElseThrow(BookNotFoundException::new);

		BookSaleInfo bookSaleInfo = bookSaleInfoRepository.findByBook(book)
			.orElseThrow(BookSaleInfoNotFoundException::new);

		if (bookSaleInfo.getBookSaleInfoState() != BookSaleInfoState.AVAILABLE) {
			throw new ApplicationException(ErrorCode.BOOK_STATE_NOT_AVAILABLE);
		}

		BookOrder bookOrder = BookOrder.builder()
			.order(order)
			.book(book)
			.quantity(item.bookQuantity())
			.price(item.salePrice())
			.build();

		bookOrderRepository.save(bookOrder);

		if (item.packagingOptionId() != null) {
			PackagingOption packOpt = packagingOptionRepository.findById(item.packagingOptionId())
				.orElseThrow(PackagingOptionNotFoundException::new);

			OrderPackaging packaging = OrderPackaging.builder()
				.packagingOption(packOpt)
				.bookOrder(bookOrder)
				.quantity(item.packagingQuantity())
				.build();

			bookOrder.getOrderPackagings().add(packaging);

			bookOrderRepository.save(bookOrder);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public OrderConfirmDetailResponse getOrderByKey(String orderKey, Long userId) {
		Order order = orderRepository.findByOrderKey(orderKey)
			.orElseThrow(OrderNotFoundException::new);

		if (userId != null && order.getUser() != null) {
			if (!order.getUser().getId().equals(userId)) {
				throw new OrderNotFoundException("접근 권한이 없습니다.");
			}
		}

		return buildOrderDetailsResponse(order);
	}

	@Override
	@Transactional(readOnly = true)
	public OrderConfirmDetailResponse getOrderById(Long orderId, Long userId) {
		Order order = orderRepository.findById(orderId)
			.orElseThrow(OrderNotFoundException::new);

		if (userId != null && order.getUser() != null) {
			if (!order.getUser().getId().equals(userId)) {
				throw new OrderNotFoundException("접근 권한이 없습니다.");
			}
		}

		return buildOrderDetailsResponse(order);
	}

	private OrderConfirmDetailResponse buildOrderDetailsResponse(Order order) {
		User user = order.getUser();

		List<UserCouponBookOrder> allUserCouponBookOrdersInOrder = order.getUserCouponBookOrders() == null ?
			Collections.emptyList() : order.getUserCouponBookOrders();

		List<OrderItemResponse> orderItems = (order.getBookOrders() == null) ? Collections.emptyList() :
			order.getBookOrders().stream()
				.map(bookOrder -> mapToOrderItemResponse(bookOrder, allUserCouponBookOrdersInOrder))
				.collect(Collectors.toList());

		List<AppliedCouponResponse> appliedCoupons = allUserCouponBookOrdersInOrder.stream()
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

		BigDecimal couponDiscountTotal = appliedCoupons.stream()
			.map(AppliedCouponResponse::getAppliedDiscountAmount)
			.filter(java.util.Objects::nonNull)
			.reduce(BigDecimal.ZERO, BigDecimal::add);

		BigDecimal deliveryFee = order.getDeliveryFee() == null ? BigDecimal.ZERO : order.getDeliveryFee();
		BigDecimal totalAmount = subTotal
			.add(packagingTotal)
			.add(deliveryFee)
			.subtract(couponDiscountTotal);

		Optional<Payment> paymentOpt = paymentRepository.findByOrder(order);

		OrderConfirmDetailResponse.OrderConfirmDetailResponseBuilder builder = OrderConfirmDetailResponse.builder()
			.orderId(order.getId())
			.orderName(order.getOrderName())
			.receiverName(order.getReceiverName())
			.receiverPhoneNumber(order.getReceiverPhoneNumber())
			.ordererName(order.getOrdererName())
			.ordererPhoneNumber(order.getOrdererPhoneNumber())
			.address(order.getAddress())
			.detailAddress(order.getDetailAddress())
			.postalCode(order.getPostalCode())
			.deliveryFee(deliveryFee)
			.orderItems(orderItems)
			.appliedCoupons(appliedCoupons)
			.userPointBalance(user != null ? user.getPointBalance() : BigDecimal.ZERO)
			.subTotal(subTotal)
			.packagingTotal(packagingTotal)
			.couponDiscountTotal(couponDiscountTotal)
			.totalAmount(totalAmount)
			.pointUseAmount(order.getPointUseAmount())
			.orderKey(order.getOrderKey());

		if (paymentOpt.isPresent()) {
			Payment payment = paymentOpt.get();
			builder.paymentKey(payment.getPaymentDetail().getKey())
				.paymentMethod(payment.getPaymentDetail().getPaymentType().getMethod())
				.paidAmount(payment.getPaidAmount())
				.paidAt(payment.getCreatedAt());
		}

		return builder.build();
	}

	private OrderItemResponse mapToOrderItemResponse(BookOrder bookOrder,
		List<UserCouponBookOrder> allUserCouponBookOrdersInOrder) {
		Book book = bookOrder.getBook();
		if (book == null) {
			log.warn("BookOrder ID {} 에 연결된 Book 정보가 없습니다.", bookOrder.getId());
			return OrderItemResponse.builder()
				.bookOrderId(bookOrder.getId())
				.quantity(bookOrder.getQuantity())
				.price(bookOrder.getPrice())
				.packagingOptions(Collections.emptyList())
				.appliedItemCoupons(Collections.emptyList()) // 빈 리스트로 초기화
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

		List<AppliedCouponResponse> itemSpecificCoupons = allUserCouponBookOrdersInOrder.stream()
			.filter(ucbo -> ucbo.getBookOrder() != null && ucbo.getBookOrder().getId().equals(bookOrder.getId()))
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

		return OrderItemResponse.builder()
			.bookOrderId(bookOrder.getId())
			.bookId(book.getId())
			.bookTitle(book.getTitle())
			.bookThumbnailUrl(thumbnailUrl)
			.quantity(bookOrder.getQuantity())
			.price(bookOrder.getPrice())
			.packagingOptions(packagingOptions)
			.appliedItemCoupons(itemSpecificCoupons)
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

	@Override
	public void shipOrder(Long orderId) {
		Order order = orderRepository.findById(orderId)
			.orElseThrow(OrderNotFoundException::new);

		if (!OrderStatus.PENDING.equals(order.getOrderState().getState())) {
			throw new ApplicationException(ErrorCode.INVALID_INPUT_VALUE,
				"결제 대기 상태의 주문만 배송 처리할 수 있습니다. 현재 상태: " + order.getOrderState().getState().name());
		}

		OrderState shippingState = orderStateRepository.findByState(OrderStatus.SHIPPING)
			.orElseThrow(() -> new ApplicationException(ErrorCode.INTERNAL_SERVER_ERROR, "'SHIPPING' 상태를 찾을 수 없습니다."));

		order.changeOrderState(shippingState);
		order.changeShippedAt(LocalDateTime.now());

		orderRepository.save(order);
	}
}