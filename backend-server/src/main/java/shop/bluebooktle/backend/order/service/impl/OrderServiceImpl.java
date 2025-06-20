package shop.bluebooktle.backend.order.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
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
import shop.bluebooktle.backend.book_order.jpa.UserCouponBookOrderRepository;
import shop.bluebooktle.backend.coupon.entity.Coupon;
import shop.bluebooktle.backend.coupon.entity.CouponType;
import shop.bluebooktle.backend.coupon.entity.UserCoupon;
import shop.bluebooktle.backend.coupon.repository.UserCouponRepository;
import shop.bluebooktle.backend.coupon.service.CouponCalculationService;
import shop.bluebooktle.backend.order.dto.response.OrderCancelMessage;
import shop.bluebooktle.backend.order.dto.response.OrderShippingMessage;
import shop.bluebooktle.backend.order.entity.DeliveryRule;
import shop.bluebooktle.backend.order.entity.Order;
import shop.bluebooktle.backend.order.entity.OrderState;
import shop.bluebooktle.backend.order.entity.Refund;
import shop.bluebooktle.backend.order.repository.DeliveryRuleRepository;
import shop.bluebooktle.backend.order.repository.OrderRepository;
import shop.bluebooktle.backend.order.repository.OrderStateRepository;
import shop.bluebooktle.backend.order.service.OrderService;
import shop.bluebooktle.backend.payment.entity.Payment;
import shop.bluebooktle.backend.payment.entity.PaymentDetail;
import shop.bluebooktle.backend.payment.entity.PaymentType;
import shop.bluebooktle.backend.payment.repository.PaymentRepository;
import shop.bluebooktle.backend.point.repository.PointHistoryRepository;
import shop.bluebooktle.backend.user.repository.UserRepository;
import shop.bluebooktle.common.domain.order.OrderStatus;
import shop.bluebooktle.common.domain.point.PointSourceTypeEnum;
import shop.bluebooktle.common.domain.refund.RefundReason;
import shop.bluebooktle.common.dto.book.BookSaleInfoState;
import shop.bluebooktle.common.dto.coupon.CalculatedDiscountDetails;
import shop.bluebooktle.common.dto.coupon.response.AppliedCouponResponse;
import shop.bluebooktle.common.dto.order.request.AdminOrderSearchRequest;
import shop.bluebooktle.common.dto.order.request.OrderCreateRequest;
import shop.bluebooktle.common.dto.order.request.OrderItemRequest;
import shop.bluebooktle.common.dto.order.response.AdminOrderDetailResponse;
import shop.bluebooktle.common.dto.order.response.AdminOrderListResponse;
import shop.bluebooktle.common.dto.order.response.OrderConfirmDetailResponse;
import shop.bluebooktle.common.dto.order.response.OrderDetailResponse;
import shop.bluebooktle.common.dto.order.response.OrderHistoryResponse;
import shop.bluebooktle.common.dto.order.response.OrderItemResponse;
import shop.bluebooktle.common.dto.order.response.OrderPackagingResponse;
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.entity.point.PointHistory;
import shop.bluebooktle.common.exception.auth.UserNotFoundException;
import shop.bluebooktle.common.exception.book.BookNotFoundException;
import shop.bluebooktle.common.exception.book.BookSaleInfoNotFoundException;
import shop.bluebooktle.common.exception.book_order.PackagingOptionNotFoundException;
import shop.bluebooktle.common.exception.coupon.UserCouponAlreadyUsedException;
import shop.bluebooktle.common.exception.coupon.UserCouponNotFoundException;
import shop.bluebooktle.common.exception.order.BookNotOrderableException;
import shop.bluebooktle.common.exception.order.OrderNotFoundException;
import shop.bluebooktle.common.exception.order.StockNotEnoughException;
import shop.bluebooktle.common.exception.order.delivery_rule.DeliveryRuleNotFoundException;
import shop.bluebooktle.common.exception.order.order_state.OrderInvalidStateException;
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
	private final PointHistoryRepository pointHistoryRepository;
	private final ApplicationEventPublisher eventPublisher;
	private final UserCouponRepository userCouponRepository;
	private final UserCouponBookOrderRepository userCouponBookOrderRepository;

	@Override
	public Page<OrderHistoryResponse> getUserOrders(
		Long userId,
		OrderStatus status,
		Pageable pageable) {

		User user = userRepository.findById(userId)
			.orElseThrow(UserNotFoundException::new);

		Page<Order> orderPage;
		if (status == null) {
			orderPage = orderRepository.findByUserOrderByCreatedAtDesc(user, pageable);
		} else {
			orderPage = orderRepository.findByUserAndOrderState_StateOrderByCreatedAtDesc(user, status, pageable);
		}

		return orderPage.map(o -> {

			BigDecimal totalPackagingFee = o.getBookOrders().stream()
				.flatMap(bookOrder -> bookOrder.getOrderPackagings().stream())
				.map(packaging -> packaging.getPackagingOption().getPrice()
					.multiply(BigDecimal.valueOf(packaging.getQuantity())))
				.reduce(BigDecimal.ZERO, BigDecimal::add);

			BigDecimal paidAmount = o.getOriginalAmount()
				.subtract(Optional.ofNullable(o.getCouponDiscountAmount())
					.orElse(BigDecimal.ZERO))
				.subtract(Optional.ofNullable(o.getPointUseAmount())
					.orElse(BigDecimal.ZERO))
				.add(Optional.ofNullable(o.getDeliveryFee())
					.orElse(BigDecimal.ZERO))
				.add(totalPackagingFee)
				.subtract(Optional.ofNullable(o.getSaleDiscountAmount())
					.orElse(BigDecimal.ZERO));

			String thumbnailUrl = o.getBookOrders().stream()
				.findFirst()
				.flatMap(bookOrder -> {
					Book book = bookOrder.getBook();
					if (book == null)
						return Optional.empty();
					return book.getBookImgs().stream().findFirst();
				})
				.map(BookImg::getImg)
				.map(Img::getImgUrl)
				.orElse("");

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
			if (!request.pointUseAmount().equals(BigDecimal.ZERO)) {
				user.subtractPoint(request.pointUseAmount());
				userRepository.save(user);

				PointHistory history = PointHistory.builder()
					.user(user)
					.sourceType(PointSourceTypeEnum.PAYMENT_USE)
					.value(request.pointUseAmount())
					.build();
				pointHistoryRepository.save(history);
			}
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

		List<BookOrder> savedBookOrders = new ArrayList<>();
		for (OrderItemRequest itemReq : request.orderItems()) {
			savedBookOrders.add(createSingleBookOrder(saved, itemReq));
		}

		if (user != null) {
			for (int i = 0; i < request.orderItems().size(); i++) {
				OrderItemRequest itemReq = request.orderItems().get(i);
				Long itemCouponId = itemReq.itemCouponId();
				if (itemCouponId != null) {
					UserCoupon userCoupon = userCouponRepository.findById(itemCouponId)
						.orElseThrow(UserCouponNotFoundException::new);

					if (userCoupon.getUsedAt() != null) {
						throw new UserCouponAlreadyUsedException();
					}

					UserCouponBookOrder usage = UserCouponBookOrder.builder()
						.user(user)
						.order(saved)
						.bookOrder(savedBookOrders.get(i))
						.userCoupon(userCoupon)
						.build();
					userCouponBookOrderRepository.save(usage);
					userCoupon.useCoupon();
					userCouponRepository.save(userCoupon);
				}
			}
			if (request.orderCouponId() != null) {
				UserCoupon orderCoupon = userCouponRepository.findById(request.orderCouponId())
					.orElseThrow(UserCouponNotFoundException::new);

				if (orderCoupon.getUsedAt() != null) {
					throw new UserCouponAlreadyUsedException();
				}

				UserCouponBookOrder usage = UserCouponBookOrder.builder()
					.user(user)
					.order(saved)
					.bookOrder(null)
					.userCoupon(orderCoupon)
					.build();
				userCouponBookOrderRepository.save(usage);
				orderCoupon.useCoupon();
				userCouponRepository.save(orderCoupon);
			}
		}

		eventPublisher.publishEvent(new OrderCancelMessage(saved.getId()));

		return saved.getId();
	}


	@Transactional
	public BookOrder createSingleBookOrder(Order order, OrderItemRequest item) {
		Book book = bookRepository.findById(item.bookId())
			.orElseThrow(BookNotFoundException::new);

		BookSaleInfo bookSaleInfo = bookSaleInfoRepository.findByBook(book)
			.orElseThrow(BookSaleInfoNotFoundException::new);

		if (bookSaleInfo.getBookSaleInfoState() != BookSaleInfoState.AVAILABLE
			&& bookSaleInfo.getBookSaleInfoState() != BookSaleInfoState.LOW_STOCK) {
			throw new BookNotOrderableException(
				" %s 는 현재 판매 가능한 상태가 아닙니다.".formatted(book.getTitle()));
		}

		if (bookSaleInfo.getStock() < item.bookQuantity()) {
			throw new StockNotEnoughException(
				" %s 상품의 재고가 부족합니다. (남은 수량: %d개)".formatted(book.getTitle(), bookSaleInfo.getStock()));
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
		}

		int stock = bookSaleInfo.getStock() - item.bookQuantity();
		bookSaleInfo.updateStock(stock);
		if (stock <= 0) {
			bookSaleInfo.changeSaleState(BookSaleInfoState.SALE_ENDED);
		}
		bookSaleInfoRepository.save(bookSaleInfo);

		return bookOrder;
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
			builder.paymentKey(payment.getPaymentDetail().getPaymentKey())
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
	@Transactional
	public void cancelOrderMember(String orderKey, Long userId) {

		Order order = orderRepository.findByOrderKey(orderKey)
			.orElseThrow(OrderNotFoundException::new);

		User user = order.getUser();
		if ((user == null && userId != null) || (user != null && !Objects.equals(user.getId(), userId))) {
			throw new OrderNotFoundException();
		}

		if (order.getOrderState().getState() != OrderStatus.PENDING
			&& order.getOrderState().getState() != OrderStatus.SHIPPING) {
			throw new OrderInvalidStateException("배송이 시작되면 주문을 취소할 수 없습니다.");
		}
		cancelOrderInternal(order);
	}

	@Override
	@Transactional
	public void cancelOrderNonMember(String orderKey) {

		Order order = orderRepository.findByOrderKey(orderKey)
			.orElseThrow(OrderNotFoundException::new);

		if (order.getOrderState().getState() != OrderStatus.PENDING
			&& order.getOrderState().getState() != OrderStatus.SHIPPING) {
			throw new OrderInvalidStateException("배송이 시작되면 주문을 취소할 수 없습니다.");
		}
		cancelOrderInternal(order);
	}

	@Override
	@Transactional
	public void cancelOrderListener(Long orderId) {
		Order order = orderRepository.findOrderForCancelById(orderId)
			.orElseThrow(OrderNotFoundException::new);

		if (order.getOrderState().getState() != OrderStatus.PENDING) {
			return;
		}
		cancelOrderInternal(order);
	}

	@Override
	@Transactional
	public void completeOrder(Long orderId) {
		Order order = orderRepository.findById(orderId)
			.orElse(null);

		if (order == null) {
			throw new OrderNotFoundException();
		}
		if (order.getOrderState().getState() == OrderStatus.SHIPPING) {
			updateOrderStatus(orderId, OrderStatus.COMPLETED);
		}
	}

	@Override
	@Transactional
	public void cancelOrderInternal(Order order) {

		User user = order.getUser();
		if (user != null) {
			BigDecimal usedPoints = order.getPointUseAmount();
			if (usedPoints != null && usedPoints.compareTo(BigDecimal.ZERO) > 0) {
				user.addPoint(usedPoints);

				PointHistory pointHistory = PointHistory.builder()
					.user(user)
					.sourceType(PointSourceTypeEnum.ORDER_CANCEL)
					.value(usedPoints)
					.build();
				pointHistoryRepository.save(pointHistory);
			}
			List<UserCouponBookOrder> couponUsages = userCouponBookOrderRepository.findByOrder(order);
			for (UserCouponBookOrder usage : couponUsages) {
				UserCoupon userCoupon = usage.getUserCoupon();
				userCoupon.cancelCoupon();
				userCouponRepository.save(userCoupon);
			}
			userCouponBookOrderRepository.deleteAll(couponUsages);
		}

		OrderState canceledState = orderStateRepository.findByState(OrderStatus.CANCELED)
			.orElseThrow(OrderStateNotFoundException::new);
		order.changeOrderState(canceledState);

		for (BookOrder bookOrder : order.getBookOrders()) {
			BookSaleInfo bookSaleInfo = bookOrder.getBook().getBookSaleInfo();
			int currentStock = bookSaleInfo.getStock();
			int orderedQuantity = bookOrder.getQuantity();
			bookSaleInfo.updateStock(currentStock + orderedQuantity);

			if (bookSaleInfo.getBookSaleInfoState() == BookSaleInfoState.SALE_ENDED) {
				bookSaleInfo.changeSaleState(BookSaleInfoState.AVAILABLE);
			}
		}
	}

	@Override
	public OrderDetailResponse getOrderDetailByUserId(String orderKey, Long userId) {

		Order order = orderRepository.findOrderDetailsByOrderKey(orderKey)
			.orElseThrow(OrderNotFoundException::new);

		if (order.getUser() != null) {
			if (!order.getUser().getId().equals(userId)) {
				throw new OrderNotFoundException("접근 권한이 없습니다.");
			}
		}
		return getOrderDetailByOrderKey(orderKey);
	}

	@Override
	public OrderDetailResponse getOrderDetailByOrderKey(String orderKey) {
		Order order = orderRepository.findOrderDetailsByOrderKey(orderKey)
			.orElseThrow(OrderNotFoundException::new);

		List<OrderItemResponse> itemResponses = order.getBookOrders().stream()
			.map(this::mapToItemResponse)
			.toList();

		Payment payment = order.getPayment();

		BigDecimal paidAmount = Optional.ofNullable(payment)
			.map(Payment::getPaidAmount)
			.orElse(null);

		String paidMethod = Optional.ofNullable(payment)
			.map(Payment::getPaymentDetail)
			.map(PaymentDetail::getPaymentType)
			.map(PaymentType::getMethod)
			.orElse(null);

		BigDecimal totalPackagingFee = itemResponses.stream()
			.flatMap(item -> item.getPackagingOptions().stream())
			.map(packaging -> packaging.getPrice()
				.multiply(BigDecimal.valueOf(packaging.getQuantity())))
			.reduce(BigDecimal.ZERO, BigDecimal::add);

		return new OrderDetailResponse(
			order.getId(),
			order.getOrderKey(),
			order.getCreatedAt(),
			order.getOrdererName(),
			paidAmount,
			paidMethod,
			order.getOrderState().getState(),
			order.getReceiverName(),
			order.getReceiverPhoneNumber(),
			order.getReceiverEmail(),
			order.getAddress(),
			order.getDetailAddress(),
			order.getPostalCode(),
			itemResponses,
			order.getOriginalAmount().subtract(order.getSaleDiscountAmount()),
			order.getPointUseAmount(),
			order.getCouponDiscountAmount(),
			order.getDeliveryFee(),
			order.getTrackingNumber(),
			totalPackagingFee
		);
	}

	@Override
	public Page<AdminOrderListResponse> searchOrders(AdminOrderSearchRequest searchRequest, Pageable pageable) {
		Page<Order> orderPage = orderRepository.searchOrders(searchRequest, pageable);
		return orderPage.map(this::convertToResponseDto); // 메소드 이름 변경
	}

	private AdminOrderListResponse convertToResponseDto(Order order) {
		String paymentMethod = null;
		if (order.getPayment() != null && order.getPayment().getPaymentDetail() != null
			&& order.getPayment().getPaymentDetail().getPaymentType() != null) {
			paymentMethod = order.getPayment().getPaymentDetail().getPaymentType().getMethod();
		}

		BigDecimal actualPaidAmount = Optional.ofNullable(order.getPayment())
			.map(Payment::getPaidAmount)
			.orElse(null);

		return new AdminOrderListResponse(
			order.getId(),
			order.getOrderKey(),
			order.getCreatedAt(),
			order.getOrdererName(),
			order.getUser() != null ? order.getUser().getLoginId() : "비회원",
			order.getReceiverName(),
			actualPaidAmount,
			order.getOrderState().getState(),
			paymentMethod
		);
	}

	private OrderItemResponse mapToItemResponse(BookOrder bookOrder) {
		Book book = bookOrder.getBook();
		String thumbnailUrl = book.getBookImgs().stream()
			.filter(BookImg::isThumbnail)
			.findFirst()
			.map(bookImg -> bookImg.getImg().getImgUrl())
			.orElse(null);
		List<OrderPackagingResponse> packagingResponses = bookOrder.getOrderPackagings().stream()
			.map(this::mapToPackagingResponse)
			.toList();
		List<AppliedCouponResponse> couponResponses = bookOrder.getUserCouponBookOrdersAssociatedWithThisBookOrder()
			.stream()
			.map(this::mapToAppliedCouponResponse)
			.toList();

		BigDecimal totalDiscountAmount = couponResponses.stream()
			.map(AppliedCouponResponse::getAppliedDiscountAmount)
			.reduce(BigDecimal.ZERO, BigDecimal::add);

		BigDecimal totalPackagingFee = packagingResponses.stream()
			.map(p -> p.getPrice().multiply(BigDecimal.valueOf(p.getQuantity())))
			.reduce(BigDecimal.ZERO, BigDecimal::add);

		BigDecimal basePrice = bookOrder.getPrice().multiply(BigDecimal.valueOf(bookOrder.getQuantity()));
		BigDecimal finalPrice = basePrice.subtract(totalDiscountAmount).add(totalPackagingFee);

		return OrderItemResponse.builder()
			.bookOrderId(bookOrder.getId())
			.bookId(book.getId())
			.bookTitle(book.getTitle())
			.bookThumbnailUrl(thumbnailUrl)
			.quantity(bookOrder.getQuantity())
			.price(bookOrder.getPrice())
			.packagingOptions(packagingResponses)
			.appliedItemCoupons(couponResponses)
			.finalItemPrice(finalPrice)
			.build();
	}

	private OrderPackagingResponse mapToPackagingResponse(OrderPackaging packaging) {
		PackagingOption option = packaging.getPackagingOption();
		return OrderPackagingResponse.builder()
			.packageId(option.getId())
			.name(option.getName())
			.price(option.getPrice())
			.quantity(packaging.getQuantity())
			.build();
	}

	private AppliedCouponResponse mapToAppliedCouponResponse(UserCouponBookOrder ucb) {
		UserCoupon userCoupon = ucb.getUserCoupon();
		Coupon coupon = userCoupon.getCoupon();
		return AppliedCouponResponse.builder()
			.userCouponBookOrderId(ucb.getId())
			.couponName(coupon.getCouponName())
			.build();
	}

	@Override
	public AdminOrderDetailResponse getAdminOrderDetail(Long orderId) {
		Order order = orderRepository.findAdminOrderDetailsByOrderId(orderId)
			.orElseThrow(OrderNotFoundException::new);

		List<OrderItemResponse> itemResponses = order.getBookOrders().stream()
			.map(this::mapToItemResponse)
			.toList();

		Payment payment = order.getPayment();

		BigDecimal productAmount = order.getOriginalAmount()
			.subtract(Optional.ofNullable(order.getSaleDiscountAmount()).orElse(BigDecimal.ZERO));

		BigDecimal totalPackagingFee = itemResponses.stream()
			.flatMap(item -> item.getPackagingOptions().stream())
			.map(packaging -> packaging.getPrice().multiply(BigDecimal.valueOf(packaging.getQuantity())))
			.reduce(BigDecimal.ZERO, BigDecimal::add);

		BigDecimal paidAmount = Optional.ofNullable(payment)
			.map(Payment::getPaidAmount)
			.orElse(null);

		String paidMethod = Optional.ofNullable(payment)
			.map(Payment::getPaymentDetail)
			.map(PaymentDetail::getPaymentType)
			.map(PaymentType::getMethod)
			.orElse(null);

		Refund refund = order.getRefund();
		RefundReason refundReason = Optional.ofNullable(refund).map(Refund::getReason).orElse(null);
		String refundReasonDetail = Optional.ofNullable(refund).map(Refund::getReasonDetail).orElse(null);

		return new AdminOrderDetailResponse(
			order.getId(),
			order.getOrderKey(),
			order.getCreatedAt(),
			order.getOrdererName(),
			order.getUser() != null ? order.getUser().getLoginId() : "비회원",
			order.getOrdererPhoneNumber(),
			order.getOrdererEmail(),
			order.getReceiverName(),
			order.getReceiverPhoneNumber(),
			order.getReceiverEmail(),
			order.getPostalCode(),
			order.getAddress(),
			order.getDetailAddress(),
			itemResponses,
			paidMethod,
			productAmount,
			totalPackagingFee,
			order.getPointUseAmount(),
			order.getCouponDiscountAmount(),
			order.getDeliveryFee(),
			paidAmount,
			order.getOrderState().getState(),
			order.getTrackingNumber(),
			refundReason,
			refundReasonDetail
		);
	}

	@Override
	@Transactional
	public void updateOrderStatus(Long orderId, OrderStatus status) {
		Order order = orderRepository.findById(orderId)
			.orElseThrow(OrderNotFoundException::new);

		OrderState newState = orderStateRepository.findByState(status)
			.orElseThrow(OrderStateNotFoundException::new);

		if (order.getOrderState().getState() == OrderStatus.CANCELED
			|| order.getOrderState().getState() == OrderStatus.RETURNED_REQUEST
			|| order.getOrderState().getState() == OrderStatus.RETURNED) {
			throw new OrderInvalidStateException(
				"%s 주문의 상태를 변경할 수 없습니다.".formatted(order.getOrderState().getState().getDescription()));
		}

		if (order.getOrderState().getState() != OrderStatus.SHIPPING && status == OrderStatus.COMPLETED) {
			throw new OrderInvalidStateException("배송 중이 아닌 상태에서 배송완료 상태로 변경할 수 없습니다.");
		}

		order.changeOrderState(newState);
		orderRepository.save(order);

		if (status == OrderStatus.SHIPPING) {
			order.changeShippedAt(LocalDateTime.now());
			if (order.getRequestedDeliveryDate() == null) {
				eventPublisher.publishEvent(new OrderShippingMessage(order.getId()));
			}
		}
	}

	@Override
	@Transactional
	public void updateOrderTrackingNumber(Long orderId, String trackingNumber) {
		Order order = orderRepository.findById(orderId)
			.orElseThrow(OrderNotFoundException::new);

		OrderState newState = orderStateRepository.findByState(OrderStatus.SHIPPING)
			.orElseThrow(OrderStateNotFoundException::new);

		if (order.getOrderState().getState() == OrderStatus.CANCELED) {
			throw new OrderInvalidStateException("취소된 주문의 상태를 변경할 수 없습니다.");
		}
		order.changeOrderState(newState);
		order.changeTrackingNumber(trackingNumber);
		orderRepository.save(order);
	}
}