package shop.bluebooktle.backend.order.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
import shop.bluebooktle.backend.order.service.impl.OrderServiceImpl;
import shop.bluebooktle.backend.payment.entity.Payment;
import shop.bluebooktle.backend.payment.entity.PaymentDetail;
import shop.bluebooktle.backend.payment.entity.PaymentType;
import shop.bluebooktle.backend.payment.repository.PaymentRepository;
import shop.bluebooktle.backend.point.repository.PointHistoryRepository;
import shop.bluebooktle.backend.user.repository.UserRepository;
import shop.bluebooktle.common.domain.order.AdminOrderSearchType;
import shop.bluebooktle.common.domain.order.OrderStatus;
import shop.bluebooktle.common.domain.refund.RefundReason;
import shop.bluebooktle.common.dto.book.BookSaleInfoState;
import shop.bluebooktle.common.dto.coupon.response.AppliedCouponResponse;
import shop.bluebooktle.common.dto.order.request.AdminOrderSearchRequest;
import shop.bluebooktle.common.dto.order.request.OrderCreateRequest;
import shop.bluebooktle.common.dto.order.request.OrderItemRequest;
import shop.bluebooktle.common.dto.order.response.AdminOrderDetailResponse;
import shop.bluebooktle.common.dto.order.response.AdminOrderListResponse;
import shop.bluebooktle.common.dto.order.response.OrderConfirmDetailResponse;
import shop.bluebooktle.common.dto.order.response.OrderDetailResponse;
import shop.bluebooktle.common.dto.order.response.OrderHistoryResponse;
import shop.bluebooktle.common.dto.order.response.OrderPackagingResponse;
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.entity.point.PointHistory;
import shop.bluebooktle.common.exception.auth.UserNotFoundException;
import shop.bluebooktle.common.exception.book.BookNotFoundException;
import shop.bluebooktle.common.exception.book.BookSaleInfoNotFoundException;
import shop.bluebooktle.common.exception.book_order.PackagingOptionNotFoundException;
import shop.bluebooktle.common.exception.order.OrderNotFoundException;
import shop.bluebooktle.common.exception.order.StockNotEnoughException;
import shop.bluebooktle.common.exception.order.order_state.OrderInvalidStateException;
import shop.bluebooktle.common.exception.order.order_state.OrderStateNotFoundException;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

	@Mock
	private OrderRepository orderRepository;
	@Mock
	private UserRepository userRepository;
	@Mock
	private OrderStateRepository orderStateRepository;
	@Mock
	private BookRepository bookRepository;
	@Mock
	private PaymentRepository paymentRepository;
	@Mock
	private BookOrderRepository bookOrderRepository;
	@Mock
	private PackagingOptionRepository packagingOptionRepository;
	@Mock
	private BookSaleInfoRepository bookSaleInfoRepository;
	@Mock
	private PointHistoryRepository pointHistoryRepository;
	@Mock
	private ApplicationEventPublisher eventPublisher;
	@Mock
	private UserCouponRepository userCouponRepository;
	@Mock
	private UserCouponBookOrderRepository userCouponBookOrderRepository;
	@Mock
	private CouponCalculationService couponCalculationService;
	@Mock
	private DeliveryRuleRepository deliveryRuleRepository;

	@InjectMocks
	private OrderServiceImpl orderService;

	private User user;
	private OrderState orderStateCompleted;
	private OrderState orderStatePending;
	private Book book;
	private BookImg bookImg;
	private Img img;
	private Order order;
	private DeliveryRule deliveryRule;
	private OrderState pendingOrderState;
	private BookSaleInfo bookSaleInfo;
	private PackagingOption packagingOption;
	private OrderState orderStateCanceled;
	private OrderState orderStateReturnedRequest;
	private OrderState orderStateReturned;
	private Payment payment;
	private PaymentDetail paymentDetail;
	private UserCouponBookOrder userCouponBookOrder;
	private UserCoupon userCoupon;
	private Coupon coupon;
	private PaymentType paymentType;
	private OrderPackaging orderPackaging;
	private OrderState orderStateShipping;
	private BookOrder bookOrder;
	private Refund refund;

	@BeforeEach
	void setUp() {
		order = mock(Order.class);
		user = mock(User.class);
		orderStateCompleted = mock(OrderState.class);
		orderStatePending = mock(OrderState.class);
		book = mock(Book.class);
		bookImg = mock(BookImg.class);
		img = mock(Img.class);
		deliveryRule = mock(DeliveryRule.class);
		pendingOrderState = mock(OrderState.class);
		bookSaleInfo = mock(BookSaleInfo.class);
		packagingOption = mock(PackagingOption.class);

		payment = mock(Payment.class);
		paymentDetail = mock(PaymentDetail.class);
		paymentType = mock(PaymentType.class);
		userCouponBookOrder = mock(UserCouponBookOrder.class);
		userCoupon = mock(UserCoupon.class);
		coupon = mock(Coupon.class);
		orderPackaging = mock(OrderPackaging.class);
		orderStateShipping = mock(OrderState.class);
		bookOrder = mock(BookOrder.class);
		orderStateCanceled = mock(OrderState.class);
		refund = mock(Refund.class);

		orderStateReturnedRequest = mock(OrderState.class);
		orderStateReturned = mock(OrderState.class);
	}

	@Test
	@DisplayName("사용자의 모든 주문 내역을 성공적으로 조회한다 (상태 필터 없음)")
	void getUserOrders_Success_NoStatusFilter() {
		Long userId = 1L;
		Pageable pageable = PageRequest.of(0, 10);

		when(orderStateCompleted.getState()).thenReturn(OrderStatus.COMPLETED);
		when(orderStatePending.getState()).thenReturn(OrderStatus.PENDING);
		when(img.getImgUrl()).thenReturn("thumbnail_url_1");
		when(bookImg.getImg()).thenReturn(img);
		when(book.getBookImgs()).thenReturn(List.of(bookImg));

		Order order1 = mock(Order.class);
		when(order1.getId()).thenReturn(1L);
		when(order1.getCreatedAt()).thenReturn(LocalDateTime.now().minusDays(5));
		when(order1.getOrderName()).thenReturn("Order 1");
		when(order1.getOriginalAmount()).thenReturn(BigDecimal.valueOf(50000));
		when(order1.getCouponDiscountAmount()).thenReturn(BigDecimal.valueOf(5000));
		when(order1.getPointUseAmount()).thenReturn(BigDecimal.valueOf(2000));
		when(order1.getDeliveryFee()).thenReturn(BigDecimal.valueOf(3000));
		when(order1.getSaleDiscountAmount()).thenReturn(BigDecimal.valueOf(1000));
		when(order1.getOrderKey()).thenReturn("order_key_1");
		when(order1.getOrderState()).thenReturn(orderStateCompleted);
		PackagingOption packagingOption1 = mock(PackagingOption.class);
		when(packagingOption1.getPrice()).thenReturn(BigDecimal.valueOf(1000));
		OrderPackaging orderPackaging1 = mock(OrderPackaging.class);
		when(orderPackaging1.getPackagingOption()).thenReturn(packagingOption1);
		when(orderPackaging1.getQuantity()).thenReturn(2);

		BookOrder bookOrder1_1 = mock(BookOrder.class);
		when(bookOrder1_1.getBook()).thenReturn(book);
		when(bookOrder1_1.getOrderPackagings()).thenReturn(List.of(orderPackaging1));
		when(order1.getBookOrders()).thenReturn(List.of(bookOrder1_1));

		Order order2 = mock(Order.class);
		when(order2.getId()).thenReturn(2L);
		when(order2.getCreatedAt()).thenReturn(LocalDateTime.now().minusDays(2));
		when(order2.getOrderName()).thenReturn("Order 2");
		when(order2.getOriginalAmount()).thenReturn(BigDecimal.valueOf(30000));
		when(order2.getCouponDiscountAmount()).thenReturn(BigDecimal.ZERO);
		when(order2.getPointUseAmount()).thenReturn(BigDecimal.ZERO);
		when(order2.getDeliveryFee()).thenReturn(BigDecimal.valueOf(2500));
		when(order2.getSaleDiscountAmount()).thenReturn(BigDecimal.ZERO);
		when(order2.getOrderKey()).thenReturn("order_key_2");
		when(order2.getOrderState()).thenReturn(orderStatePending);

		BookOrder bookOrder2_1 = mock(BookOrder.class);
		when(bookOrder2_1.getBook()).thenReturn(book);
		when(bookOrder2_1.getOrderPackagings()).thenReturn(Collections.emptyList());
		when(order2.getBookOrders()).thenReturn(List.of(bookOrder2_1));

		List<Order> orders = List.of(order1, order2);
		Page<Order> orderPage = new PageImpl<>(orders, pageable, orders.size());

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(orderRepository.findByUserOrderByCreatedAtDesc(user, pageable)).thenReturn(orderPage);

		Page<OrderHistoryResponse> result = orderService.getUserOrders(userId, null, pageable);

		verify(userRepository).findById(userId);
		verify(orderRepository).findByUserOrderByCreatedAtDesc(user, pageable);
	}

	@Test
	@DisplayName("사용자의 특정 상태 주문 내역을 성공적으로 조회한다")
	void getUserOrders_Success_WithStatusFilter() {
		Long userId = 1L;
		OrderStatus status = OrderStatus.COMPLETED;
		Pageable pageable = PageRequest.of(0, 10);

		when(img.getImgUrl()).thenReturn("thumbnail_url_1");
		when(bookImg.getImg()).thenReturn(img);
		when(book.getBookImgs()).thenReturn(List.of(bookImg));

		Order order1 = mock(Order.class);
		when(order1.getId()).thenReturn(1L);
		when(order1.getCreatedAt()).thenReturn(LocalDateTime.now().minusDays(5));
		when(order1.getOrderName()).thenReturn("Order 1");
		when(order1.getOriginalAmount()).thenReturn(BigDecimal.valueOf(50000));
		when(order1.getCouponDiscountAmount()).thenReturn(BigDecimal.valueOf(5000));
		when(order1.getPointUseAmount()).thenReturn(BigDecimal.valueOf(2000));
		when(order1.getDeliveryFee()).thenReturn(BigDecimal.valueOf(3000));
		when(order1.getSaleDiscountAmount()).thenReturn(BigDecimal.valueOf(1000));
		when(order1.getOrderKey()).thenReturn("order_key_1");
		when(order1.getOrderState()).thenReturn(orderStateCompleted);
		when(order1.getOrderState().getState()).thenReturn(OrderStatus.COMPLETED);

		PackagingOption packagingOption1 = mock(PackagingOption.class);
		when(packagingOption1.getPrice()).thenReturn(BigDecimal.valueOf(1000));
		OrderPackaging orderPackaging1 = mock(OrderPackaging.class);
		when(orderPackaging1.getPackagingOption()).thenReturn(packagingOption1);
		when(orderPackaging1.getQuantity()).thenReturn(2);

		BookOrder bookOrder1_1 = mock(BookOrder.class);
		when(bookOrder1_1.getBook()).thenReturn(book);
		when(bookOrder1_1.getOrderPackagings()).thenReturn(List.of(orderPackaging1));
		when(order1.getBookOrders()).thenReturn(List.of(bookOrder1_1));

		List<Order> orders = List.of(order1);
		Page<Order> orderPage = new PageImpl<>(orders, pageable, orders.size());

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(orderRepository.findByUserAndOrderState_StateOrderByCreatedAtDesc(user, status, pageable)).thenReturn(
			orderPage);

		Page<OrderHistoryResponse> result = orderService.getUserOrders(userId, status, pageable);

		verify(userRepository).findById(userId);
		verify(orderRepository).findByUserAndOrderState_StateOrderByCreatedAtDesc(user, status, pageable);
	}

	@Test
	@DisplayName("존재하지 않는 사용자 ID로 조회 시 UserNotFoundException이 발생")
	void getUserOrders_ThrowsUserNotFoundException_WhenUserDoesNotExist() {
		Long userId = 999L;
		Pageable pageable = PageRequest.of(0, 10);

		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		assertThrows(UserNotFoundException.class, () -> orderService.getUserOrders(userId, null, pageable));

		verify(userRepository).findById(userId);
		verifyNoInteractions(orderRepository);
	}

	@Test
	@DisplayName("성공적인 주문 생성 - 포인트 및 쿠폰 사용 없음")
	void createOrder_Success_NoPointNoCoupon() {

		when(orderStateRepository.findByState(OrderStatus.PENDING)).thenReturn(Optional.of(pendingOrderState));

		when(deliveryRuleRepository.findById(anyLong())).thenReturn(Optional.of(deliveryRule));

		when(userRepository.findById(1L)).thenReturn(Optional.of(user));

		when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
		when(bookRepository.findById(2L)).thenReturn(Optional.of(book));

		OrderItemRequest itemRequest1 = new OrderItemRequest(1L, 2, BigDecimal.valueOf(10000), null, null, null);
		OrderItemRequest itemRequest2 = new OrderItemRequest(2L, 1, BigDecimal.valueOf(5000), null, null, null);

		OrderCreateRequest request = OrderCreateRequest.builder()
			.userId(1L)
			.deliveryRuleId(10L)
			.orderName("테스트 주문")
			.requestedDeliveryDate(null)
			.deliveryFee(BigDecimal.valueOf(3000))
			.ordererName("주문자")
			.ordererEmail("orderer@test.com")
			.ordererPhoneNumber("010-1234-5678")
			.receiverName("수령자")
			.receiverEmail("receiver@test.com")
			.receiverPhoneNumber("010-8765-4321")
			.postalCode("12345")
			.address("테스트 주소")
			.detailAddress("상세 주소")
			.couponDiscountAmount(BigDecimal.ZERO)
			.pointUseAmount(BigDecimal.ZERO)
			.saleDiscountAmount(BigDecimal.ZERO)
			.originalAmount(BigDecimal.valueOf(25000))
			.orderKey("unique_order_key_123")
			.orderItems(List.of(itemRequest1, itemRequest2))
			.orderCouponId(null)
			.build();

		Order savedOrder = mock(Order.class);
		when(savedOrder.getId()).thenReturn(1L);
		when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

		when(bookOrderRepository.save(any(BookOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));

		Long resultOrderId = orderService.createOrder(request);

		assertNotNull(resultOrderId);
		assertEquals(1L, resultOrderId);

		verify(orderStateRepository).findByState(OrderStatus.PENDING);
		verify(deliveryRuleRepository).findById(request.deliveryRuleId());
		verify(userRepository).findById(request.userId());
		verify(orderRepository).save(any(Order.class));
		verify(bookRepository, times(2)).findById(anyLong());
		verify(bookOrderRepository, times(2)).save(any(BookOrder.class));
		verify(eventPublisher).publishEvent(any(OrderCancelMessage.class));

		verify(user, never()).subtractPoint(any(BigDecimal.class));
		verify(pointHistoryRepository, never()).save(any(PointHistory.class));
		verify(userCouponRepository, never()).findById(anyLong());
		verify(userCouponBookOrderRepository, never()).save(any());
		verify(userRepository, never()).save(user);
	}

	@Test
	@DisplayName("성공적인 도서 주문 생성 - 패키징 옵션 없음")
	void createSingleBookOrder_Success_NoPackaging() {
		Long bookId = 1L;
		Integer bookQuantity = 2;
		BigDecimal salePrice = BigDecimal.valueOf(10000);
		OrderItemRequest itemRequest = new OrderItemRequest(bookId, bookQuantity, salePrice, null, null, null);

		when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
		when(bookSaleInfo.getBookSaleInfoState()).thenReturn(BookSaleInfoState.AVAILABLE);
		when(bookSaleInfo.getStock()).thenReturn(10);
		when(bookSaleInfoRepository.findByBook(book)).thenReturn(Optional.of(bookSaleInfo));

		OrderServiceImpl spyOrderService = spy(orderService);

		try {
			java.lang.reflect.Method method = OrderServiceImpl.class.getDeclaredMethod("createSingleBookOrder",
				Order.class, OrderItemRequest.class);
			method.setAccessible(true);
			method.invoke(orderService, order, itemRequest);
		} catch (Exception e) {
			fail("Exception occurred: " + e.getMessage());
		}

		verify(bookRepository).findById(bookId);
		verify(bookSaleInfoRepository).findByBook(book);
		verify(bookSaleInfo).getBookSaleInfoState();
		verify(bookSaleInfo, times(2)).getStock();
		verify(bookOrderRepository).save(any(BookOrder.class));

		verify(bookSaleInfo, never()).changeSaleState(any());
		verify(bookSaleInfoRepository).save(bookSaleInfo);

		verify(packagingOptionRepository, never()).findById(anyLong());
		verify(bookOrderRepository, never()).save(
			argThat(bo -> bo.getOrderPackagings() != null && !bo.getOrderPackagings().isEmpty()));
	}

	@Test
	@DisplayName("성공적인 도서 주문 생성 - 패키징 옵션 포함")
	void createSingleBookOrder_Success_WithPackaging() {
		Long bookId = 1L;
		Integer bookQuantity = 1;
		BigDecimal salePrice = BigDecimal.valueOf(15000);
		Long packagingOptionId = 100L;
		Integer packagingQuantity = 1;
		OrderItemRequest itemRequest = new OrderItemRequest(bookId, bookQuantity, salePrice, packagingOptionId,
			packagingQuantity, null);

		when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
		when(bookSaleInfo.getBookSaleInfoState()).thenReturn(BookSaleInfoState.AVAILABLE);
		when(bookSaleInfo.getStock()).thenReturn(5);
		when(bookSaleInfoRepository.findByBook(book)).thenReturn(Optional.of(bookSaleInfo));
		when(packagingOptionRepository.findById(packagingOptionId)).thenReturn(Optional.of(packagingOption));

		when(bookOrderRepository.save(any(BookOrder.class)))
			.thenAnswer(invocation -> {
				BookOrder bookOrder = invocation.getArgument(0);
				if (bookOrder.getOrderPackagings() == null) {
					bookOrder.setOrderPackagings(new ArrayList<>());
				}
				return bookOrder;
			});

		try {
			java.lang.reflect.Method method = OrderServiceImpl.class.getDeclaredMethod("createSingleBookOrder",
				Order.class, OrderItemRequest.class);
			method.setAccessible(true);
			method.invoke(orderService, order, itemRequest);
		} catch (Exception e) {
			fail("Exception occurred: " + e.getMessage());
		}

		verify(bookRepository).findById(bookId);
		verify(bookSaleInfoRepository).findByBook(book);
		verify(packagingOptionRepository).findById(packagingOptionId);

		verify(bookSaleInfo, never()).changeSaleState(any());
		verify(bookSaleInfoRepository).save(bookSaleInfo);
	}

	@Test
	@DisplayName("도서 재고 부족일 때 StockNotEnoughException 발생")
	void createSingleBookOrder_ThrowsStockNotEnoughException() {
		Long bookId = 1L;
		Integer bookQuantity = 10;
		BigDecimal salePrice = BigDecimal.valueOf(10000);
		OrderItemRequest itemRequest = new OrderItemRequest(bookId, bookQuantity, salePrice, null, null, null);

		when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
		when(bookSaleInfo.getBookSaleInfoState()).thenReturn(BookSaleInfoState.AVAILABLE);
		when(bookSaleInfo.getStock()).thenReturn(5);
		when(book.getTitle()).thenReturn("테스트 도서");
		when(bookSaleInfoRepository.findByBook(book)).thenReturn(Optional.of(bookSaleInfo));

		InvocationTargetException thrownException = assertThrows(InvocationTargetException.class, () -> {
			java.lang.reflect.Method method = OrderServiceImpl.class.getDeclaredMethod("createSingleBookOrder",
				Order.class, OrderItemRequest.class);
			method.setAccessible(true);
			method.invoke(orderService, order, itemRequest);
		});

		Throwable cause = thrownException.getCause();
		assertNotNull(cause);
		assertTrue(cause instanceof StockNotEnoughException);

		StockNotEnoughException actualException = (StockNotEnoughException)cause;
		assertTrue(actualException.getMessage().contains("테스트 도서 상품의 재고가 부족합니다. (남은 수량: 5개)"));

		verify(bookRepository).findById(bookId);
		verify(bookSaleInfoRepository).findByBook(book);
		verify(bookSaleInfo).getBookSaleInfoState();
		verify(bookSaleInfo, times(2)).getStock();
		verify(bookOrderRepository, never()).save(any(BookOrder.class));
		verify(bookSaleInfoRepository, never()).save(any(BookSaleInfo.class));
	}

	@Test
	@DisplayName("도서 주문 후 재고가 0 이하가 되어 SALE_ENDED 상태로 변경")
	void createSingleBookOrder_ChangesToSaleEnded() {

		Long bookId = 1L;
		Integer bookQuantity = 5;
		BigDecimal salePrice = BigDecimal.valueOf(10000);
		OrderItemRequest itemRequest = new OrderItemRequest(bookId, bookQuantity, salePrice, null, null, null);

		when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
		when(bookSaleInfo.getBookSaleInfoState()).thenReturn(BookSaleInfoState.AVAILABLE);
		when(bookSaleInfo.getStock()).thenReturn(5);
		when(bookSaleInfoRepository.findByBook(book)).thenReturn(Optional.of(bookSaleInfo));

		try {
			java.lang.reflect.Method method = OrderServiceImpl.class.getDeclaredMethod("createSingleBookOrder",
				Order.class, OrderItemRequest.class);
			method.setAccessible(true);
			method.invoke(orderService, order, itemRequest);
		} catch (Exception e) {
			fail("Exception occurred: " + e.getMessage());
		}

		verify(bookSaleInfo).changeSaleState(BookSaleInfoState.SALE_ENDED);
		verify(bookSaleInfoRepository).save(bookSaleInfo);
	}

	@Test
	@DisplayName("도서 정보가 없을 때 BookNotFoundException 발생")
	void createSingleBookOrder_ThrowsBookNotFoundException() {
		Long bookId = 1L;
		Integer bookQuantity = 1;
		BigDecimal salePrice = BigDecimal.valueOf(10000);
		OrderItemRequest itemRequest = new OrderItemRequest(bookId, bookQuantity, salePrice, null, null, null);

		when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

		InvocationTargetException thrownException = assertThrows(InvocationTargetException.class, () -> {
			java.lang.reflect.Method method = OrderServiceImpl.class.getDeclaredMethod("createSingleBookOrder",
				Order.class, OrderItemRequest.class);
			method.setAccessible(true);
			method.invoke(orderService, order, itemRequest);
		});

		Throwable cause = thrownException.getCause();
		assertNotNull(cause);
		assertTrue(cause instanceof BookNotFoundException);

		BookNotFoundException actualException = (BookNotFoundException)cause;
		assertTrue(actualException.getMessage().contains("도서를 찾을 수 없습니다."));

		verify(bookRepository).findById(bookId);
		verify(bookSaleInfoRepository, never()).findByBook(any());
		verify(bookOrderRepository, never()).save(any());
	}

	@Test
	@DisplayName("도서 판매 정보가 없을 때 BookSaleInfoNotFoundException 발생")
	void createSingleBookOrder_ThrowsBookSaleInfoNotFoundException() {
		Long bookId = 1L;
		Integer bookQuantity = 1;
		BigDecimal salePrice = BigDecimal.valueOf(10000);
		OrderItemRequest itemRequest = new OrderItemRequest(bookId, bookQuantity, salePrice, null, null, null);

		when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
		when(bookSaleInfoRepository.findByBook(book)).thenReturn(Optional.empty());

		InvocationTargetException thrownException = assertThrows(InvocationTargetException.class, () -> {
			java.lang.reflect.Method method = OrderServiceImpl.class.getDeclaredMethod("createSingleBookOrder",
				Order.class, OrderItemRequest.class);
			method.setAccessible(true);
			method.invoke(orderService, order, itemRequest);
		});

		Throwable cause = thrownException.getCause();
		assertNotNull(cause);
		assertTrue(cause instanceof BookSaleInfoNotFoundException);

		BookSaleInfoNotFoundException actualException = (BookSaleInfoNotFoundException)cause;
		assertTrue(actualException.getMessage().contains("도서 판매 정보를 찾을 수 없습니다."));

		verify(bookRepository).findById(bookId);
		verify(bookSaleInfoRepository).findByBook(book);
		verify(bookOrderRepository, never()).save(any());
	}

	@Test
	@DisplayName("패키징 옵션이 없을 때 PackagingOptionNotFoundException 발생")
	void createSingleBookOrder_ThrowsPackagingOptionNotFoundException() {
		Long bookId = 1L;
		Integer bookQuantity = 1;
		BigDecimal salePrice = BigDecimal.valueOf(10000);
		Long packagingOptionId = 999L;
		Integer packagingQuantity = 1;
		OrderItemRequest itemRequest = new OrderItemRequest(bookId, bookQuantity, salePrice, packagingOptionId,
			packagingQuantity, null);

		when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
		when(bookSaleInfo.getBookSaleInfoState()).thenReturn(BookSaleInfoState.AVAILABLE);
		when(bookSaleInfo.getStock()).thenReturn(10);
		when(bookSaleInfoRepository.findByBook(book)).thenReturn(Optional.of(bookSaleInfo));
		when(packagingOptionRepository.findById(packagingOptionId)).thenReturn(Optional.empty());

		InvocationTargetException thrownException = assertThrows(InvocationTargetException.class, () -> {
			java.lang.reflect.Method method = OrderServiceImpl.class.getDeclaredMethod("createSingleBookOrder",
				Order.class, OrderItemRequest.class);
			method.setAccessible(true);
			method.invoke(orderService, order, itemRequest);
		});

		Throwable cause = thrownException.getCause();
		assertNotNull(cause);
		assertTrue(cause instanceof PackagingOptionNotFoundException);

		PackagingOptionNotFoundException actualException = (PackagingOptionNotFoundException)cause;
		assertTrue(actualException.getMessage().contains("주문 포장 옵션을 찾을 수 없습니다."));

		verify(bookRepository).findById(bookId);
		verify(bookSaleInfoRepository).findByBook(book);
		verify(bookOrderRepository).save(any(BookOrder.class));
		verify(packagingOptionRepository).findById(packagingOptionId);
	}

	@Test
	@DisplayName("성공적인 주문 조회 - 회원, 본인 주문")
	void getOrderById_Success_LoggedInUserOwnOrder() {
		Long orderId = 1L;
		Long userId = 100L;

		when(order.getId()).thenReturn(orderId);

		when(order.getOrderName()).thenReturn("테스트 주문");

		when(order.getOrderKey()).thenReturn("test_order_key");

		when(order.getPointUseAmount()).thenReturn(BigDecimal.valueOf(1000));
		when(order.getDeliveryFee()).thenReturn(BigDecimal.valueOf(3000));

		when(order.getOrdererName()).thenReturn("주문자");

		when(order.getOrdererPhoneNumber()).thenReturn("010-1234-5678");
		when(order.getReceiverName()).thenReturn("수령자");

		when(order.getReceiverPhoneNumber()).thenReturn("010-8765-4321");
		when(order.getPostalCode()).thenReturn("12345");
		when(order.getAddress()).thenReturn("서울시");
		when(order.getDetailAddress()).thenReturn("강남구");
		when(order.getUser()).thenReturn(user);
		when(user.getId()).thenReturn(userId);

		BookOrder bookOrder = mock(BookOrder.class);
		when(bookOrder.getBook()).thenReturn(book);
		when(bookOrder.getQuantity()).thenReturn(1);
		when(bookOrder.getPrice()).thenReturn(BigDecimal.valueOf(10000));
		when(book.getTitle()).thenReturn("테스트 도서");
		when(book.getBookImgs()).thenReturn(List.of(bookImg));

		when(bookOrder.getOrderPackagings()).thenReturn(new ArrayList<>());

		Payment payment = mock(Payment.class);
		PaymentDetail paymentDetail = mock(PaymentDetail.class);
		when(payment.getPaymentDetail()).thenReturn(paymentDetail);
		when(paymentDetail.getPaymentType()).thenReturn(paymentType);

		when(paymentType.getMethod()).thenReturn("CARD");

		UserCouponBookOrder userCouponBookOrder = mock(UserCouponBookOrder.class);
		UserCoupon userCoupon = mock(UserCoupon.class);
		Coupon coupon = mock(Coupon.class);

		when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
		when(order.getBookOrders()).thenReturn(List.of(bookOrder));
		when(paymentRepository.findByOrder(order)).thenReturn(Optional.of(payment));

		when(order.getUser()).thenReturn(user);

		OrderConfirmDetailResponse response = orderService.getOrderById(orderId, userId);

		verify(orderRepository).findById(orderId);
		verify(order, times(3)).getUser();
		verify(user).getId();
		verify(order, times(2)).getBookOrders();
		verify(paymentRepository).findByOrder(order);

	}

	@Test
	@DisplayName("성공적인 주문 조회 - 비회원 주문")
	void getOrderById_Success_GuestOrder() {
		Long orderId = 1L;
		Long userId = null;

		when(order.getId()).thenReturn(orderId);

		when(order.getOrderName()).thenReturn("테스트 주문");

		when(order.getOrderKey()).thenReturn("test_order_key");

		when(order.getPointUseAmount()).thenReturn(BigDecimal.valueOf(1000));
		when(order.getDeliveryFee()).thenReturn(BigDecimal.valueOf(3000));

		when(order.getOrdererName()).thenReturn("주문자");

		when(order.getOrdererPhoneNumber()).thenReturn("010-1234-5678");
		when(order.getReceiverName()).thenReturn("수령자");

		when(order.getReceiverPhoneNumber()).thenReturn("010-8765-4321");
		when(order.getPostalCode()).thenReturn("12345");
		when(order.getAddress()).thenReturn("서울시");
		when(order.getDetailAddress()).thenReturn("강남구");
		when(order.getUser()).thenReturn(null);
		when(order.getUserCouponBookOrders()).thenReturn(Collections.emptyList());

		BookOrder bookOrder = mock(BookOrder.class);
		when(bookOrder.getBook()).thenReturn(book);
		when(bookOrder.getQuantity()).thenReturn(1);
		when(bookOrder.getPrice()).thenReturn(BigDecimal.valueOf(10000));
		when(book.getTitle()).thenReturn("테스트 도서");
		when(book.getBookImgs()).thenReturn(List.of(bookImg));

		when(bookOrder.getOrderPackagings()).thenReturn(new ArrayList<>());

		Payment payment = mock(Payment.class);
		PaymentDetail paymentDetail = mock(PaymentDetail.class);
		when(payment.getPaymentDetail()).thenReturn(paymentDetail);
		when(paymentDetail.getPaymentType()).thenReturn(paymentType);

		when(paymentType.getMethod()).thenReturn("CARD");

		when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
		when(order.getBookOrders()).thenReturn(List.of(bookOrder));
		when(paymentRepository.findByOrder(order)).thenReturn(Optional.of(payment));

		OrderConfirmDetailResponse response = orderService.getOrderById(orderId, userId);

		verify(orderRepository).findById(orderId);
		verify(order).getUser();

		verify(paymentRepository).findByOrder(order);
	}

	@Test
	@DisplayName("주문이 없을 때 OrderNotFoundException 발생")
	void getOrderById_ThrowsOrderNotFoundException_OrderDoesNotExist() {
		Long orderId = 999L;
		Long userId = 100L;

		when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

		OrderNotFoundException exception = assertThrows(OrderNotFoundException.class, () ->
			orderService.getOrderById(orderId, userId)
		);

		assertEquals("주문을 찾을 수 없습니다.", exception.getMessage());
		verify(orderRepository).findById(orderId);
		verifyNoMoreInteractions(orderRepository);
	}

	@Test
	@DisplayName("접근 권한이 없을 때 OrderNotFoundException 발생")
	void getOrderById_ThrowsOrderNotFoundException_AccessDenied() {
		Long orderId = 1L;
		Long requestingUserId = 100L;
		Long orderOwnerId = 200L;

		when(order.getUser()).thenReturn(user);
		when(user.getId()).thenReturn(orderOwnerId);

		when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

		OrderNotFoundException exception = assertThrows(OrderNotFoundException.class, () ->
			orderService.getOrderById(orderId, requestingUserId)
		);

		assertEquals("접근 권한이 없습니다.", exception.getMessage());
		verify(orderRepository).findById(orderId);
		verify(order, times(2)).getUser();
		verify(user).getId();
		verifyNoMoreInteractions(orderRepository, order, user);
	}

	@Test
	@DisplayName("mapToOrderPackagingResponse 성공 - 유효한 정보")
	void mapToOrderPackagingResponse_Success_ValidInfo() {
		Long packagingId = 1L;
		String name = "선물 포장";
		BigDecimal price = BigDecimal.valueOf(2500);
		Integer quantity = 1;

		when(orderPackaging.getPackagingOption()).thenReturn(packagingOption);
		when(packagingOption.getId()).thenReturn(packagingId);
		when(packagingOption.getName()).thenReturn(name);
		when(packagingOption.getPrice()).thenReturn(price);
		when(orderPackaging.getQuantity()).thenReturn(quantity);

		OrderServiceImpl spyOrderService = spy(orderService);

		try {
			java.lang.reflect.Method method = OrderServiceImpl.class.getDeclaredMethod("mapToOrderPackagingResponse",
				OrderPackaging.class);
			method.setAccessible(true);
			OrderPackagingResponse response = (OrderPackagingResponse)method.invoke(spyOrderService, orderPackaging);

			assertNotNull(response);
			assertEquals(packagingId, response.getPackageId());
			assertEquals(name, response.getName());
			assertEquals(price, response.getPrice());
			assertEquals(quantity, response.getQuantity());

			verify(orderPackaging, times(2)).getPackagingOption();
			verify(packagingOption).getId();
			verify(packagingOption).getName();
			verify(packagingOption).getPrice();
			verify(orderPackaging).getQuantity();

		} catch (Exception e) {
			fail("Exception occurred: " + e.getMessage());
		}
	}

	@Test
	@DisplayName("mapToOrderPackagingResponse 예외 - OrderPackaging이 null")
	void mapToOrderPackagingResponse_NullOrderPackaging() {
		OrderServiceImpl spyOrderService = spy(orderService);

		try {
			java.lang.reflect.Method method = OrderServiceImpl.class.getDeclaredMethod("mapToOrderPackagingResponse",
				OrderPackaging.class);
			method.setAccessible(true);
			OrderPackagingResponse response = (OrderPackagingResponse)method.invoke(spyOrderService, (Object)null);

			assertNotNull(response);
			assertEquals("포장 정보 없음", response.getName());
			assertEquals(BigDecimal.ZERO, response.getPrice());
			assertEquals(0, response.getQuantity());

			verifyNoInteractions(orderPackaging); // orderPackaging은 호출되지 않아야 함

		} catch (Exception e) {
			fail("Exception occurred: " + e.getMessage());
		}
	}

	@Test
	@DisplayName("mapToOrderPackagingResponse 예외 - PackagingOption이 null")
	void mapToOrderPackagingResponse_NullPackagingOption() {
		when(orderPackaging.getPackagingOption()).thenReturn(null);

		OrderServiceImpl spyOrderService = spy(orderService);

		try {
			java.lang.reflect.Method method = OrderServiceImpl.class.getDeclaredMethod("mapToOrderPackagingResponse",
				OrderPackaging.class);
			method.setAccessible(true);
			OrderPackagingResponse response = (OrderPackagingResponse)method.invoke(spyOrderService, orderPackaging);

			assertNotNull(response);
			assertEquals("포장 정보 없음", response.getName());
			assertEquals(BigDecimal.ZERO, response.getPrice());
			assertEquals(0, response.getQuantity());

			verify(orderPackaging).getPackagingOption();

		} catch (Exception e) {
			fail("Exception occurred: " + e.getMessage());
		}
	}

	@Test
	@DisplayName("cancelOrderMember 성공 - 회원 본인 주문 취소")
	void cancelOrderMember_Success_OwnOrder() {
		String orderKey = "test_order_key";
		Long userId = 1L;

		when(orderRepository.findByOrderKey(orderKey)).thenReturn(Optional.of(order));
		when(order.getUser()).thenReturn(user);
		when(user.getId()).thenReturn(userId);
		when(order.getOrderState()).thenReturn(orderStatePending);
		when(orderStatePending.getState()).thenReturn(OrderStatus.PENDING);

		OrderServiceImpl spyOrderService = spy(orderService);
		doNothing().when(spyOrderService).cancelOrderInternal(any(Order.class));

		spyOrderService.cancelOrderMember(orderKey, userId);

		verify(orderRepository).findByOrderKey(orderKey);
		verify(order).getUser();
		verify(user).getId();
		verify(order).getOrderState();
		verify(spyOrderService).cancelOrderInternal(order);
	}

	@Test
	@DisplayName("cancelOrderMember 실패 - 주문 없음")
	void cancelOrderMember_Fail_OrderNotFound() {
		String orderKey = "non_existent_key";
		Long userId = 1L;

		when(orderRepository.findByOrderKey(orderKey)).thenReturn(Optional.empty());

		OrderNotFoundException exception = assertThrows(OrderNotFoundException.class, () -> {
			orderService.cancelOrderMember(orderKey, userId);
		});

		assertEquals("주문을 찾을 수 없습니다.", exception.getMessage());
		verify(orderRepository).findByOrderKey(orderKey);
		verifyNoInteractions(userRepository);
	}

	@Test
	@DisplayName("cancelOrderMember 실패 - 접근 권한 없음 (다른 회원 주문)")
	void cancelOrderMember_Fail_AccessDenied_OtherUserOrder() {
		String orderKey = "test_order_key";
		Long userId = 1L;
		Long otherUserId = 2L;

		when(orderRepository.findByOrderKey(orderKey)).thenReturn(Optional.of(order));
		when(order.getUser()).thenReturn(user);
		when(user.getId()).thenReturn(otherUserId);

		OrderNotFoundException exception = assertThrows(OrderNotFoundException.class, () -> {
			orderService.cancelOrderMember(orderKey, userId);
		});

		assertEquals("주문을 찾을 수 없습니다.", exception.getMessage());
		verify(orderRepository).findByOrderKey(orderKey);
		verify(order).getUser();
		verify(user).getId();
	}

	@Test
	@DisplayName("cancelOrderMember 실패 - 비회원 주문에 회원 ID 제공")
	void cancelOrderMember_Fail_GuestOrderWithUserId() {
		String orderKey = "test_order_key";
		Long userId = 1L;

		when(orderRepository.findByOrderKey(orderKey)).thenReturn(Optional.of(order));
		when(order.getUser()).thenReturn(null); // 비회원 주문

		OrderNotFoundException exception = assertThrows(OrderNotFoundException.class, () -> {
			orderService.cancelOrderMember(orderKey, userId);
		});

		assertEquals("주문을 찾을 수 없습니다.", exception.getMessage());
		verify(orderRepository).findByOrderKey(orderKey);
		verify(order).getUser();
	}

	@Test
	@DisplayName("cancelOrderMember 실패 - 주문 취소 불가 상태")
	void cancelOrderMember_Fail_InvalidOrderState() {
		String orderKey = "test_order_key";
		Long userId = 1L;

		when(orderRepository.findByOrderKey(orderKey)).thenReturn(Optional.of(order));
		when(order.getUser()).thenReturn(user);
		when(user.getId()).thenReturn(userId);
		when(order.getOrderState()).thenReturn(orderStateCompleted);
		when(orderStateCompleted.getState()).thenReturn(OrderStatus.COMPLETED);

		OrderServiceImpl spiedOrderService = spy(orderService);

		OrderInvalidStateException exception = assertThrows(OrderInvalidStateException.class, () -> {
			spiedOrderService.cancelOrderMember(orderKey, userId);
		});

		verify(orderRepository).findByOrderKey(orderKey);
		verify(order).getUser();
		verify(user).getId();
		verify(order, times(2)).getOrderState();
		verify(orderStateCompleted, times(2)).getState();
		verify(spiedOrderService, never()).cancelOrderInternal(any(Order.class));
	}

	@Test
	@DisplayName("cancelOrderNonMember 성공 - 비회원 주문 취소")
	void cancelOrderNonMember_Success() {
		String orderKey = "test_order_key";

		when(orderRepository.findByOrderKey(orderKey)).thenReturn(Optional.of(order));
		when(order.getOrderState()).thenReturn(orderStatePending);
		when(orderStatePending.getState()).thenReturn(OrderStatus.PENDING);

		OrderServiceImpl spyOrderService = spy(orderService);
		doNothing().when(spyOrderService).cancelOrderInternal(any(Order.class));

		spyOrderService.cancelOrderNonMember(orderKey);

		verify(orderRepository).findByOrderKey(orderKey);
		verify(order).getOrderState();
		verify(orderStatePending).getState();
		verify(spyOrderService).cancelOrderInternal(order);
	}

	@Test
	@DisplayName("cancelOrderNonMember 실패 - 주문 없음")
	void cancelOrderNonMember_Fail_OrderNotFound() {
		String orderKey = "non_existent_key";

		when(orderRepository.findByOrderKey(orderKey)).thenReturn(Optional.empty());

		OrderNotFoundException exception = assertThrows(OrderNotFoundException.class, () -> {
			orderService.cancelOrderNonMember(orderKey);
		});

		assertEquals("주문을 찾을 수 없습니다.", exception.getMessage());
		verify(orderRepository).findByOrderKey(orderKey);
	}

	@Test
	@DisplayName("cancelOrderNonMember 실패 - 주문 취소 불가 상태")
	void cancelOrderNonMember_Fail_InvalidOrderState() {
		String orderKey = "test_order_key";

		when(orderRepository.findByOrderKey(orderKey)).thenReturn(Optional.of(order));
		when(order.getOrderState()).thenReturn(orderStateCompleted);
		when(orderStateCompleted.getState()).thenReturn(OrderStatus.COMPLETED);

		OrderInvalidStateException exception = assertThrows(OrderInvalidStateException.class, () -> {
			orderService.cancelOrderNonMember(orderKey);
		});

		verify(orderRepository).findByOrderKey(orderKey);
		verify(order, times(2)).getOrderState();
		verify(orderStateCompleted, times(2)).getState();
	}

	@Test
	@DisplayName("cancelOrderListener 성공 - PENDING 상태 주문 취소")
	void cancelOrderListener_Success_PendingOrder() {
		Long orderId = 1L;

		when(orderRepository.findOrderForCancelById(orderId)).thenReturn(Optional.of(order));
		when(order.getOrderState()).thenReturn(orderStatePending);
		when(orderStatePending.getState()).thenReturn(OrderStatus.PENDING);

		OrderServiceImpl spyOrderService = spy(orderService);
		doNothing().when(spyOrderService).cancelOrderInternal(any(Order.class));

		spyOrderService.cancelOrderListener(orderId);

		verify(orderRepository).findOrderForCancelById(orderId);
		verify(order).getOrderState();
		verify(orderStatePending).getState();
		verify(spyOrderService).cancelOrderInternal(order);
	}

	@Test
	@DisplayName("cancelOrderListener 성공 - PENDING 외 상태 주문은 취소 안됨")
	void cancelOrderListener_Success_NonPendingOrder() {
		Long orderId = 1L;

		when(orderRepository.findOrderForCancelById(orderId)).thenReturn(Optional.of(order));
		when(order.getOrderState()).thenReturn(orderStateShipping); // 배송 중 상태
		when(orderStateShipping.getState()).thenReturn(OrderStatus.SHIPPING);

		OrderServiceImpl spyOrderService = spy(orderService);

		spyOrderService.cancelOrderListener(orderId);

		verify(orderRepository).findOrderForCancelById(orderId);
		verify(order).getOrderState();
		verify(orderStateShipping).getState();
		verify(spyOrderService, never()).cancelOrderInternal(any(Order.class)); // cancelOrderInternal 호출 안됨
	}

	@Test
	@DisplayName("cancelOrderListener 실패 - 주문 없음")
	void cancelOrderListener_Fail_OrderNotFound() {
		Long orderId = 999L;

		when(orderRepository.findOrderForCancelById(orderId)).thenReturn(Optional.empty());

		OrderNotFoundException exception = assertThrows(OrderNotFoundException.class, () -> {
			orderService.cancelOrderListener(orderId);
		});

		assertEquals("주문을 찾을 수 없습니다.", exception.getMessage());
		verify(orderRepository).findOrderForCancelById(orderId);
	}

	@Test
	@DisplayName("completeOrder 성공 - SHIPPING 상태에서 COMPLETED로 변경")
	void completeOrder_Success_FromShippingToCompleted() {
		Long orderId = 1L;

		when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
		when(order.getOrderState()).thenReturn(orderStateShipping);
		when(orderStateShipping.getState()).thenReturn(OrderStatus.SHIPPING);

		OrderServiceImpl spyOrderService = spy(orderService);
		doNothing().when(spyOrderService).updateOrderStatus(anyLong(), any(OrderStatus.class));

		spyOrderService.completeOrder(orderId);

		verify(orderRepository).findById(orderId);
		verify(order).getOrderState();
		verify(orderStateShipping).getState();
		verify(spyOrderService).updateOrderStatus(orderId, OrderStatus.COMPLETED);
	}

	@Test
	@DisplayName("completeOrder 실패 - 주문 없음")
	void completeOrder_Fail_OrderNotFound() {
		Long orderId = 999L;

		when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

		OrderNotFoundException exception = assertThrows(OrderNotFoundException.class, () -> {
			orderService.completeOrder(orderId);
		});

		assertEquals("주문을 찾을 수 없습니다.", exception.getMessage());
		verify(orderRepository).findById(orderId);
	}

	@Test
	@DisplayName("completeOrder 실패 - SHIPPING 외 상태에서 COMPLETED 변경 시도")
	void completeOrder_Fail_InvalidStateForCompletion() {
		Long orderId = 1L;

		when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
		when(order.getOrderState()).thenReturn(orderStatePending); // PENDING 상태
		when(orderStatePending.getState()).thenReturn(OrderStatus.PENDING);

		OrderServiceImpl spiedOrderService = spy(orderService);

		spiedOrderService.completeOrder(orderId);

		verify(orderRepository).findById(orderId);
		verify(order).getOrderState();
		verify(orderStatePending).getState();

		verify(spiedOrderService, never()).updateOrderStatus(eq(orderId), eq(OrderStatus.COMPLETED));
	}

	@Test
	@DisplayName("cancelOrderInternal 성공 - 포인트/쿠폰 사용 없음")
	void cancelOrderInternal_Success_NoPointNoCoupon() {
		when(order.getUser()).thenReturn(user);
		when(order.getPointUseAmount()).thenReturn(BigDecimal.ZERO);
		when(userCouponBookOrderRepository.findByOrder(order)).thenReturn(Collections.emptyList());
		when(order.getBookOrders()).thenReturn(List.of(bookOrder));
		when(bookOrder.getBook()).thenReturn(book);
		when(book.getBookSaleInfo()).thenReturn(bookSaleInfo);
		when(bookOrder.getQuantity()).thenReturn(1);
		when(bookSaleInfo.getStock()).thenReturn(5);
		when(bookSaleInfo.getBookSaleInfoState()).thenReturn(BookSaleInfoState.AVAILABLE);
		when(orderStateRepository.findByState(OrderStatus.CANCELED)).thenReturn(Optional.of(orderStateCanceled));
		try {
			java.lang.reflect.Method method = OrderServiceImpl.class.getDeclaredMethod("cancelOrderInternal",
				Order.class);
			method.setAccessible(true);
			method.invoke(orderService, order);
		} catch (Exception e) {
			fail("Exception occurred: " + e.getMessage());
		}

		verify(order).getUser();
		verify(order).getPointUseAmount();
		verify(user, never()).addPoint(any());
		verify(pointHistoryRepository, never()).save(any());
		verify(userCouponBookOrderRepository).findByOrder(order);
		verify(order).changeOrderState(orderStateCanceled);
		verify(order).getBookOrders();
		verify(bookOrder).getBook();
		verify(book).getBookSaleInfo();
		verify(bookSaleInfo).updateStock(anyInt());
		verify(bookSaleInfo).getBookSaleInfoState();
	}

	@Test
	@DisplayName("cancelOrderInternal 성공 - 포인트 사용 포함")
	void cancelOrderInternal_Success_WithPointUse() {
		BigDecimal usedPoints = BigDecimal.valueOf(1000);
		when(order.getUser()).thenReturn(user);
		when(order.getPointUseAmount()).thenReturn(usedPoints);
		when(userCouponBookOrderRepository.findByOrder(order)).thenReturn(Collections.emptyList());
		when(order.getBookOrders()).thenReturn(List.of(bookOrder));
		when(bookOrder.getBook()).thenReturn(book);
		when(book.getBookSaleInfo()).thenReturn(bookSaleInfo);
		when(bookOrder.getQuantity()).thenReturn(1);
		when(bookSaleInfo.getStock()).thenReturn(5);
		when(bookSaleInfo.getBookSaleInfoState()).thenReturn(BookSaleInfoState.AVAILABLE);
		when(orderStateRepository.findByState(OrderStatus.CANCELED)).thenReturn(Optional.of(orderStateCanceled));
		try {
			java.lang.reflect.Method method = OrderServiceImpl.class.getDeclaredMethod("cancelOrderInternal",
				Order.class);
			method.setAccessible(true);
			method.invoke(orderService, order);
		} catch (Exception e) {
			fail("Exception occurred: " + e.getMessage());
		}

		verify(user).addPoint(usedPoints);
		verify(pointHistoryRepository).save(any(PointHistory.class));
		verify(userCouponBookOrderRepository).findByOrder(order);

		verify(order).changeOrderState(orderStateCanceled);
		verify(bookSaleInfo).updateStock(anyInt());
	}

	@Test
	@DisplayName("cancelOrderInternal 성공 - 쿠폰 사용 포함")
	void cancelOrderInternal_Success_WithCouponUse() {
		when(order.getUser()).thenReturn(user);
		when(order.getPointUseAmount()).thenReturn(BigDecimal.ZERO);
		when(userCouponBookOrderRepository.findByOrder(order)).thenReturn(List.of(userCouponBookOrder));
		when(userCouponBookOrder.getUserCoupon()).thenReturn(userCoupon);
		when(order.getBookOrders()).thenReturn(List.of(bookOrder));
		when(bookOrder.getBook()).thenReturn(book);
		when(book.getBookSaleInfo()).thenReturn(bookSaleInfo);
		when(bookOrder.getQuantity()).thenReturn(1);
		when(bookSaleInfo.getStock()).thenReturn(5);
		when(bookSaleInfo.getBookSaleInfoState()).thenReturn(BookSaleInfoState.AVAILABLE);
		when(orderStateRepository.findByState(OrderStatus.CANCELED)).thenReturn(Optional.of(orderStateCanceled));

		try {
			java.lang.reflect.Method method = OrderServiceImpl.class.getDeclaredMethod("cancelOrderInternal",
				Order.class);
			method.setAccessible(true);
			method.invoke(orderService, order);
		} catch (Exception e) {
			fail("Exception occurred: " + e.getMessage());
		}

		verify(userCouponBookOrderRepository).findByOrder(order);
		verify(userCouponBookOrder).getUserCoupon();
		verify(userCoupon).cancelCoupon();
		verify(userCouponRepository).save(userCoupon);
		verify(userCouponBookOrderRepository).deleteAll(List.of(userCouponBookOrder));
		verify(order).changeOrderState(orderStateCanceled);
		verify(bookSaleInfo).updateStock(anyInt());
	}

	@Test
	@DisplayName("cancelOrderInternal 성공 - 재고 복원 및 상태 변경 없음 (AVAILABLE)")
	void cancelOrderInternal_Success_RestoreStockNoStateChangeAvailable() {
		when(order.getUser()).thenReturn(user);
		when(order.getPointUseAmount()).thenReturn(BigDecimal.ZERO);
		when(userCouponBookOrderRepository.findByOrder(order)).thenReturn(Collections.emptyList());
		when(order.getBookOrders()).thenReturn(List.of(bookOrder));
		when(bookOrder.getBook()).thenReturn(book);
		when(book.getBookSaleInfo()).thenReturn(bookSaleInfo);
		when(bookOrder.getQuantity()).thenReturn(1);
		when(bookSaleInfo.getStock()).thenReturn(5);
		when(bookSaleInfo.getBookSaleInfoState()).thenReturn(BookSaleInfoState.AVAILABLE);
		when(orderStateRepository.findByState(OrderStatus.CANCELED)).thenReturn(Optional.of(orderStateCanceled));
		try {
			java.lang.reflect.Method method = OrderServiceImpl.class.getDeclaredMethod("cancelOrderInternal",
				Order.class);
			method.setAccessible(true);
			method.invoke(orderService, order);
		} catch (Exception e) {
			fail("Exception occurred: " + e.getMessage());
		}

		verify(bookSaleInfo).updateStock(5 + 1);
		verify(bookSaleInfo).getBookSaleInfoState();
		verify(bookSaleInfo, never()).changeSaleState(any());
	}

	@Test
	@DisplayName("cancelOrderInternal 성공 - 재고 복원 및 SALE_ENDED에서 AVAILABLE로 변경")
	void cancelOrderInternal_Success_RestoreStockAndChangeState() {
		when(order.getUser()).thenReturn(user);
		when(order.getPointUseAmount()).thenReturn(BigDecimal.ZERO);
		when(userCouponBookOrderRepository.findByOrder(order)).thenReturn(Collections.emptyList());
		when(order.getBookOrders()).thenReturn(List.of(bookOrder));
		when(bookOrder.getBook()).thenReturn(book);
		when(book.getBookSaleInfo()).thenReturn(bookSaleInfo);
		when(bookOrder.getQuantity()).thenReturn(1);
		when(bookSaleInfo.getStock()).thenReturn(0);
		when(bookSaleInfo.getBookSaleInfoState()).thenReturn(BookSaleInfoState.SALE_ENDED);
		when(orderStateRepository.findByState(OrderStatus.CANCELED)).thenReturn(Optional.of(orderStateCanceled));
		try {
			java.lang.reflect.Method method = OrderServiceImpl.class.getDeclaredMethod("cancelOrderInternal",
				Order.class);
			method.setAccessible(true);
			method.invoke(orderService, order);
		} catch (Exception e) {
			fail("Exception occurred: " + e.getMessage());
		}

		verify(bookSaleInfo).updateStock(0 + 1);
		verify(bookSaleInfo).getBookSaleInfoState();
		verify(bookSaleInfo).changeSaleState(BookSaleInfoState.AVAILABLE);
	}

	@Test
	@DisplayName("cancelOrderInternal 성공 - 비회원 주문 (포인트/쿠폰 로직 건너뜀)")
	void cancelOrderInternal_Success_NonMemberOrder() {
		when(order.getUser()).thenReturn(null);
		when(order.getBookOrders()).thenReturn(List.of(bookOrder));
		when(bookOrder.getBook()).thenReturn(book);
		when(book.getBookSaleInfo()).thenReturn(bookSaleInfo);
		when(bookOrder.getQuantity()).thenReturn(1);
		when(bookSaleInfo.getStock()).thenReturn(5);
		when(bookSaleInfo.getBookSaleInfoState()).thenReturn(BookSaleInfoState.AVAILABLE);
		when(orderStateRepository.findByState(OrderStatus.CANCELED)).thenReturn(Optional.of(orderStateCanceled));
		try {
			java.lang.reflect.Method method = OrderServiceImpl.class.getDeclaredMethod("cancelOrderInternal",
				Order.class);
			method.setAccessible(true);
			method.invoke(orderService, order);
		} catch (Exception e) {
			fail("Exception occurred: " + e.getMessage());
		}

		verify(order).getUser();
		verify(user, never()).addPoint(any());
		verify(pointHistoryRepository, never()).save(any());
		verify(userCouponBookOrderRepository, never()).findByOrder(any());
		verify(userCouponBookOrderRepository, never()).deleteAll(any());
		verify(userCoupon, never()).cancelCoupon();
		verify(userCouponRepository, never()).save(any());

		verify(order).changeOrderState(orderStateCanceled);
		verify(bookSaleInfo).updateStock(anyInt());
	}

	@Test
	@DisplayName("cancelOrderInternal 실패 - CANCELED OrderState 찾을 수 없음")
	void cancelOrderInternal_Fail_OrderStateNotFound() {
		when(order.getUser()).thenReturn(user);
		when(order.getPointUseAmount()).thenReturn(BigDecimal.ZERO);
		when(userCouponBookOrderRepository.findByOrder(order)).thenReturn(Collections.emptyList());

		when(orderStateRepository.findByState(OrderStatus.CANCELED)).thenReturn(Optional.empty()); // CANCELED 상태 없음

		InvocationTargetException thrownException = assertThrows(InvocationTargetException.class, () -> {
			java.lang.reflect.Method method = OrderServiceImpl.class.getDeclaredMethod("cancelOrderInternal",
				Order.class);
			method.setAccessible(true);
			method.invoke(orderService, order);
		});

		Throwable cause = thrownException.getCause();
		assertNotNull(cause);
		assertTrue(cause instanceof OrderStateNotFoundException);

		verify(orderStateRepository).findByState(OrderStatus.CANCELED);
		verify(order, never()).changeOrderState(any());
		verify(bookSaleInfoRepository, never()).save(any());
	}

	@Test
	@DisplayName("getOrderDetailByUserId 성공 - 회원, 본인 주문 (orderKey)")
	void getOrderDetailByUserId_Success_LoggedInUserOwnOrder() {
		String orderKey = "test_order_key_123";
		Long userId = 100L;
		Long orderId = 1L;

		when(order.getUser()).thenReturn(user);
		when(user.getId()).thenReturn(userId);
		when(order.getId()).thenReturn(orderId);
		when(order.getOriginalAmount()).thenReturn(BigDecimal.valueOf(10000));
		when(order.getSaleDiscountAmount()).thenReturn(BigDecimal.ZERO);
		when(order.getPointUseAmount()).thenReturn(BigDecimal.ZERO);
		when(order.getCouponDiscountAmount()).thenReturn(BigDecimal.ZERO);
		when(order.getDeliveryFee()).thenReturn(BigDecimal.valueOf(3000));
		when(order.getCreatedAt()).thenReturn(LocalDateTime.now());
		when(order.getOrdererName()).thenReturn("주문자");
		when(order.getOrderState()).thenReturn(orderStateCompleted);
		when(orderStateCompleted.getState()).thenReturn(OrderStatus.COMPLETED);
		when(order.getReceiverName()).thenReturn("수령자");
		when(order.getReceiverPhoneNumber()).thenReturn("010-1111-2222");
		when(order.getReceiverEmail()).thenReturn("receiver@example.com");
		when(order.getAddress()).thenReturn("서울");
		when(order.getDetailAddress()).thenReturn("강남");
		when(order.getPostalCode()).thenReturn("12345");
		when(order.getTrackingNumber()).thenReturn("TRK123");

		BookOrder bookOrder = mock(BookOrder.class);
		when(order.getBookOrders()).thenReturn(List.of(bookOrder));
		when(bookOrder.getBook()).thenReturn(book);
		when(bookOrder.getId()).thenReturn(200L);
		when(bookOrder.getQuantity()).thenReturn(1);
		when(bookOrder.getPrice()).thenReturn(BigDecimal.valueOf(10000));
		when(book.getId()).thenReturn(300L);
		when(book.getTitle()).thenReturn("테스트 책");
		when(book.getBookImgs()).thenReturn(List.of(bookImg));
		when(bookImg.isThumbnail()).thenReturn(true);
		when(bookImg.getImg()).thenReturn(img);
		when(img.getImgUrl()).thenReturn("http://example.com/thumb.jpg");
		when(bookOrder.getOrderPackagings()).thenReturn(new ArrayList<>());
		when(bookOrder.getUserCouponBookOrdersAssociatedWithThisBookOrder()).thenReturn(
			new ArrayList<>());

		OrderServiceImpl spyOrderService = spy(orderService);
		when(orderRepository.findOrderDetailsByOrderKey(orderKey)).thenReturn(Optional.of(order));

		OrderDetailResponse response = spyOrderService.getOrderDetailByUserId(orderKey, userId);

		verify(orderRepository, times(2)).findOrderDetailsByOrderKey(orderKey);
		verify(order, times(2)).getUser();
		verify(user).getId();
		verify(spyOrderService).getOrderDetailByOrderKey(orderKey);
	}

	@Test
	@DisplayName("getOrderDetailByUserId 성공 - 비회원 주문 (orderKey)")
	void getOrderDetailByUserId_Success_GuestOrder() {
		String orderKey = "guest_order_key_456";
		Long userId = null;

		when(order.getUser()).thenReturn(null); // This makes it a guest order
		when(order.getId()).thenReturn(2L);
		when(order.getOriginalAmount()).thenReturn(BigDecimal.valueOf(5000));
		when(order.getSaleDiscountAmount()).thenReturn(BigDecimal.ZERO);
		when(order.getPointUseAmount()).thenReturn(BigDecimal.ZERO);
		when(order.getCouponDiscountAmount()).thenReturn(BigDecimal.ZERO);
		when(order.getDeliveryFee()).thenReturn(BigDecimal.valueOf(2500));
		when(order.getCreatedAt()).thenReturn(LocalDateTime.now());
		when(order.getOrdererName()).thenReturn("비회원");
		when(order.getOrderState()).thenReturn(orderStateCompleted);
		when(orderStateCompleted.getState()).thenReturn(OrderStatus.COMPLETED);
		when(order.getReceiverName()).thenReturn("비회원 수령자");
		when(order.getReceiverPhoneNumber()).thenReturn("010-9999-8888");
		when(order.getReceiverEmail()).thenReturn("guest@example.com");
		when(order.getAddress()).thenReturn("부산");
		when(order.getDetailAddress()).thenReturn("해운대");
		when(order.getPostalCode()).thenReturn("54321");
		when(order.getTrackingNumber()).thenReturn(null);

		BookOrder bookOrder = mock(BookOrder.class);
		when(order.getBookOrders()).thenReturn(List.of(bookOrder));
		when(bookOrder.getBook()).thenReturn(book);
		when(bookOrder.getId()).thenReturn(201L);
		when(bookOrder.getQuantity()).thenReturn(1);
		when(bookOrder.getPrice()).thenReturn(BigDecimal.valueOf(5000));
		when(book.getId()).thenReturn(301L);
		when(book.getTitle()).thenReturn("비회원 책");
		when(book.getBookImgs()).thenReturn(List.of(bookImg));
		when(bookImg.isThumbnail()).thenReturn(true);
		when(bookImg.getImg()).thenReturn(img);
		when(img.getImgUrl()).thenReturn("http://example.com/guest_thumb.jpg");
		when(bookOrder.getOrderPackagings()).thenReturn(new ArrayList<>());
		when(bookOrder.getUserCouponBookOrdersAssociatedWithThisBookOrder()).thenReturn(new ArrayList<>());

		OrderServiceImpl spyOrderService = spy(orderService);
		when(orderRepository.findOrderDetailsByOrderKey(orderKey)).thenReturn(Optional.of(order));

		OrderDetailResponse response = spyOrderService.getOrderDetailByUserId(orderKey, userId);

		verify(orderRepository, times(2)).findOrderDetailsByOrderKey(orderKey);
		verify(order).getUser();
		verify(spyOrderService).getOrderDetailByOrderKey(orderKey);
	}

	@Test
	@DisplayName("getOrderDetailByUserId 실패 - 주문 없음")
	void getOrderDetailByUserId_ThrowsOrderNotFoundException_OrderDoesNotExist() {
		String orderKey = "non_existent_key";
		Long userId = 100L;

		when(orderRepository.findOrderDetailsByOrderKey(orderKey)).thenReturn(Optional.empty());

		OrderNotFoundException exception = assertThrows(OrderNotFoundException.class, () -> {
			orderService.getOrderDetailByUserId(orderKey, userId);
		});

		assertEquals("주문을 찾을 수 없습니다.", exception.getMessage());
		verify(orderRepository).findOrderDetailsByOrderKey(orderKey);
		verifyNoMoreInteractions(orderRepository); // Ensure no further interaction
	}

	@Test
	@DisplayName("getOrderDetailByUserId 실패 - 접근 권한 없음")
	void getOrderDetailByUserId_ThrowsOrderNotFoundException_AccessDenied() {
		String orderKey = "access_denied_key";
		Long requestingUserId = 100L;
		Long orderOwnerId = 200L;

		when(order.getUser()).thenReturn(user);
		when(user.getId()).thenReturn(orderOwnerId);

		when(orderRepository.findOrderDetailsByOrderKey(orderKey)).thenReturn(Optional.of(order));

		OrderNotFoundException exception = assertThrows(OrderNotFoundException.class, () -> {
			orderService.getOrderDetailByUserId(orderKey, requestingUserId);
		});

		assertEquals("접근 권한이 없습니다.", exception.getMessage());
		verify(orderRepository).findOrderDetailsByOrderKey(orderKey);
		verify(order, times(2)).getUser();
		verify(user).getId();
		verifyNoMoreInteractions(orderRepository, order, user);
	}

	@Test
	@DisplayName("searchOrders 성공")
	void searchOrders_Success() {
		// AdminOrderSearchRequest 객체 생성 방식 변경
		AdminOrderSearchRequest searchRequest = new AdminOrderSearchRequest(
			AdminOrderSearchType.ORDERER_LOGIN_ID,
			"주문자",
			null,
			null,
			null,
			null
		);
		Pageable pageable = PageRequest.of(0, 10);

		Order order1 = mock(Order.class);
		Order order2 = mock(Order.class);

		when(order1.getId()).thenReturn(1L);
		when(order1.getOrderKey()).thenReturn("order_key_1");
		when(order1.getCreatedAt()).thenReturn(LocalDateTime.now());
		when(order1.getOrdererName()).thenReturn("주문자1");
		when(order1.getUser()).thenReturn(user);
		when(user.getLoginId()).thenReturn("user123");
		when(order1.getReceiverName()).thenReturn("수령자1");
		when(order1.getOrderState()).thenReturn(orderStateCompleted);
		when(orderStateCompleted.getState()).thenReturn(OrderStatus.COMPLETED);
		when(order1.getPayment()).thenReturn(payment);
		when(payment.getPaidAmount()).thenReturn(BigDecimal.valueOf(10000));
		when(payment.getPaymentDetail()).thenReturn(paymentDetail);
		when(paymentDetail.getPaymentType()).thenReturn(paymentType);
		when(paymentType.getMethod()).thenReturn("CARD");

		// order2 mocking (비회원 주문)
		when(order2.getId()).thenReturn(2L);
		when(order2.getOrderKey()).thenReturn("order_key_2");
		when(order2.getCreatedAt()).thenReturn(LocalDateTime.now().minusDays(1));
		when(order2.getOrdererName()).thenReturn("주문자2");
		when(order2.getUser()).thenReturn(null); // 비회원
		when(order2.getReceiverName()).thenReturn("수령자2");
		when(order2.getOrderState()).thenReturn(orderStatePending);
		when(orderStatePending.getState()).thenReturn(OrderStatus.PENDING);
		when(order2.getPayment()).thenReturn(payment); // 동일 payment mock 사용 가능 (재설정)
		when(payment.getPaidAmount()).thenReturn(BigDecimal.valueOf(5000)); // 값 재설정
		when(payment.getPaymentDetail()).thenReturn(paymentDetail); // 동일 mock
		when(paymentDetail.getPaymentType()).thenReturn(paymentType); // 동일 mock
		when(paymentType.getMethod()).thenReturn("BANK"); // 값 재설정

		Page<Order> orderPage = new PageImpl<>(List.of(order1, order2), pageable, 2);
		when(orderRepository.searchOrders(searchRequest, pageable)).thenReturn(orderPage);

		Page<AdminOrderListResponse> result = orderService.searchOrders(searchRequest, pageable);

		verify(orderRepository).searchOrders(searchRequest, pageable);

		verify(order1, times(2)).getUser();
		verify(order2).getUser(); // getUser() 호출 횟수 (null 체크)
		verify(order1, times(5)).getPayment();
		verify(order2, times(5)).getPayment();
		verify(paymentType, times(2)).getMethod(); // CARD, BANK 각각
	}

	@Test
	@DisplayName("convertToResponseDto 성공 - 회원 주문")
	void convertToResponseDto_MemberOrder_Success() {
		OrderServiceImpl spyOrderService = spy(orderService);

		when(order.getId()).thenReturn(1L);
		when(order.getOrderKey()).thenReturn("member_key");
		when(order.getCreatedAt()).thenReturn(LocalDateTime.now());
		when(order.getOrdererName()).thenReturn("회원주문자");
		when(order.getUser()).thenReturn(user);
		when(user.getLoginId()).thenReturn("memberId");
		when(order.getReceiverName()).thenReturn("회원수령자");
		when(order.getOrderState()).thenReturn(orderStateCompleted);
		when(orderStateCompleted.getState()).thenReturn(OrderStatus.COMPLETED);
		when(order.getPayment()).thenReturn(payment);
		when(payment.getPaidAmount()).thenReturn(BigDecimal.valueOf(50000));
		when(payment.getPaymentDetail()).thenReturn(paymentDetail);
		when(paymentDetail.getPaymentType()).thenReturn(paymentType);
		when(paymentType.getMethod()).thenReturn("CARD");

		// private 메서드를 리플렉션으로 호출
		AdminOrderListResponse response = null;
		try {
			java.lang.reflect.Method method = OrderServiceImpl.class.getDeclaredMethod("convertToResponseDto",
				Order.class);
			method.setAccessible(true);
			response = (AdminOrderListResponse)method.invoke(spyOrderService, order);
		} catch (Exception e) {
			fail("Exception occurred: " + e.getMessage());
		}

		verify(order).getId();
		verify(order).getOrderKey();
		verify(order).getCreatedAt();
		verify(order).getOrdererName();
		verify(order, times(2)).getUser();
		verify(user).getLoginId();
		verify(order).getReceiverName();
		verify(order, times(5)).getPayment();
		verify(payment).getPaidAmount();
		verify(payment, times(3)).getPaymentDetail();
		verify(paymentDetail, times(2)).getPaymentType();
		verify(paymentType).getMethod();
		verify(order).getOrderState();
		verify(orderStateCompleted).getState();
	}

	@Test
	@DisplayName("convertToResponseDto 성공 - 비회원 주문")
	void convertToResponseDto_NonMemberOrder_Success() {
		OrderServiceImpl spyOrderService = spy(orderService);

		when(order.getId()).thenReturn(2L);
		when(order.getOrderKey()).thenReturn("non_member_key");
		when(order.getCreatedAt()).thenReturn(LocalDateTime.now().minusDays(1));
		when(order.getOrdererName()).thenReturn("비회원주문자");
		when(order.getUser()).thenReturn(null); // 비회원
		when(order.getReceiverName()).thenReturn("비회원수령자");
		when(order.getOrderState()).thenReturn(orderStatePending);
		when(orderStatePending.getState()).thenReturn(OrderStatus.PENDING);
		when(order.getPayment()).thenReturn(payment);
		when(payment.getPaidAmount()).thenReturn(BigDecimal.valueOf(20000));
		when(payment.getPaymentDetail()).thenReturn(paymentDetail);
		when(paymentDetail.getPaymentType()).thenReturn(paymentType);
		when(paymentType.getMethod()).thenReturn("BANK");

		AdminOrderListResponse response = null;
		try {
			java.lang.reflect.Method method = OrderServiceImpl.class.getDeclaredMethod("convertToResponseDto",
				Order.class);
			method.setAccessible(true);
			response = (AdminOrderListResponse)method.invoke(spyOrderService, order);
		} catch (Exception e) {
			fail("Exception occurred: " + e.getMessage());
		}

		verify(order).getId();
		verify(order).getOrderKey();
		verify(order).getCreatedAt();
		verify(order).getOrdererName();
		verify(order).getUser();
		verify(user, never()).getLoginId(); // user.getLoginId는 호출 안됨
		verify(order).getReceiverName();
		verify(order, times(5)).getPayment();
		verify(payment).getPaidAmount();
		verify(payment, times(3)).getPaymentDetail();
		verify(paymentDetail, times(2)).getPaymentType();
		verify(paymentType).getMethod();
		verify(order).getOrderState();
		verify(orderStatePending).getState();
	}

	@Test
	@DisplayName("convertToResponseDto 성공 - 결제 정보 없음")
	void convertToResponseDto_NoPaymentInfo() {
		OrderServiceImpl spyOrderService = spy(orderService);

		when(order.getId()).thenReturn(3L);
		when(order.getOrderKey()).thenReturn("no_payment_key");
		when(order.getCreatedAt()).thenReturn(LocalDateTime.now().minusDays(2));
		when(order.getOrdererName()).thenReturn("결제정보없음");
		when(order.getUser()).thenReturn(user);
		when(user.getLoginId()).thenReturn("noPaymentUser");
		when(order.getReceiverName()).thenReturn("결제정보없음수령자");
		when(order.getOrderState()).thenReturn(orderStatePending);
		when(orderStatePending.getState()).thenReturn(OrderStatus.PENDING);
		when(order.getPayment()).thenReturn(null); // 결제 정보 없음

		AdminOrderListResponse response = null;
		try {
			java.lang.reflect.Method method = OrderServiceImpl.class.getDeclaredMethod("convertToResponseDto",
				Order.class);
			method.setAccessible(true);
			response = (AdminOrderListResponse)method.invoke(spyOrderService, order);
		} catch (Exception e) {
			fail("Exception occurred: " + e.getMessage());
		}

		verify(order).getId();
		verify(order).getOrderKey();
		verify(order).getCreatedAt();
		verify(order).getOrdererName();
		verify(order, times(2)).getUser();
		verify(user).getLoginId();
		verify(order).getReceiverName();
		verify(order, times(2)).getPayment();
		verify(payment, never()).getPaidAmount(); // payment가 null이므로 호출 안됨
		verify(payment, never()).getPaymentDetail();
		verify(paymentDetail, never()).getPaymentType();
		verify(paymentType, never()).getMethod();
		verify(order).getOrderState();
		verify(orderStatePending).getState();
	}

	@Test
	@DisplayName("mapToPackagingResponse 성공")
	void mapToPackagingResponse_Success() {
		Long packageId = 1L;
		String name = "기본 포장";
		BigDecimal price = BigDecimal.valueOf(1500);
		Integer quantity = 1;

		when(orderPackaging.getPackagingOption()).thenReturn(packagingOption);
		when(packagingOption.getId()).thenReturn(packageId);
		when(packagingOption.getName()).thenReturn(name);
		when(packagingOption.getPrice()).thenReturn(price);
		when(orderPackaging.getQuantity()).thenReturn(quantity);

		OrderServiceImpl spyOrderService = spy(orderService);

		OrderPackagingResponse response = null;
		try {
			java.lang.reflect.Method method = OrderServiceImpl.class.getDeclaredMethod("mapToPackagingResponse",
				OrderPackaging.class);
			method.setAccessible(true);
			response = (OrderPackagingResponse)method.invoke(spyOrderService, orderPackaging);
		} catch (Exception e) {
			fail("Exception occurred: " + e.getMessage());
		}

		assertNotNull(response);
		assertEquals(packageId, response.getPackageId());
		assertEquals(name, response.getName());
		assertEquals(price, response.getPrice());
		assertEquals(quantity, response.getQuantity());

		verify(orderPackaging).getPackagingOption();
		verify(packagingOption).getId();
		verify(packagingOption).getName();
		verify(packagingOption).getPrice();
		verify(orderPackaging).getQuantity();
	}

	@Test
	@DisplayName("mapToAppliedCouponResponse 성공")
	void mapToAppliedCouponResponse_Success() {
		Long userCouponBookOrderId = 10L;
		String couponName = "10% 할인 쿠폰";

		when(userCouponBookOrder.getId()).thenReturn(userCouponBookOrderId);
		when(userCouponBookOrder.getUserCoupon()).thenReturn(userCoupon);
		when(userCoupon.getCoupon()).thenReturn(coupon);
		when(coupon.getCouponName()).thenReturn(couponName);

		OrderServiceImpl spyOrderService = spy(orderService);

		AppliedCouponResponse response = null;
		try {
			java.lang.reflect.Method method = OrderServiceImpl.class.getDeclaredMethod("mapToAppliedCouponResponse",
				UserCouponBookOrder.class);
			method.setAccessible(true);
			response = (AppliedCouponResponse)method.invoke(spyOrderService, userCouponBookOrder);
		} catch (Exception e) {
			fail("Exception occurred: " + e.getMessage());
		}

		assertNotNull(response);
		assertEquals(userCouponBookOrderId, response.getUserCouponBookOrderId());
		assertEquals(couponName, response.getCouponName());

		verify(userCouponBookOrder).getId();
		verify(userCouponBookOrder).getUserCoupon();
		verify(userCoupon).getCoupon();
		verify(coupon).getCouponName();
	}

	@Test
	@DisplayName("getAdminOrderDetail 성공 - 회원 주문, 결제 및 환불 정보 포함")
	void getAdminOrderDetail_Success_MemberOrder_WithPaymentAndRefund() {
		Long orderId = 1L;
		// order mock setup
		when(order.getId()).thenReturn(orderId);
		when(order.getOrderKey()).thenReturn("admin_order_key_1");
		when(order.getCreatedAt()).thenReturn(LocalDateTime.now());
		when(order.getOrdererName()).thenReturn("관리자_주문자");
		when(order.getUser()).thenReturn(user);
		when(user.getLoginId()).thenReturn("adminUser123");
		when(order.getOrdererPhoneNumber()).thenReturn("010-1111-2222");
		when(order.getOrdererEmail()).thenReturn("admin@example.com");
		when(order.getReceiverName()).thenReturn("관리자_수령자");
		when(order.getReceiverPhoneNumber()).thenReturn("010-3333-4444");
		when(order.getReceiverEmail()).thenReturn("admin_receiver@example.com");
		when(order.getPostalCode()).thenReturn("12345");
		when(order.getAddress()).thenReturn("서울시");
		when(order.getDetailAddress()).thenReturn("어딘가");
		when(order.getOriginalAmount()).thenReturn(BigDecimal.valueOf(50000));
		when(order.getSaleDiscountAmount()).thenReturn(BigDecimal.valueOf(5000)); // productAmount = 45000
		when(order.getPointUseAmount()).thenReturn(BigDecimal.valueOf(1000));
		when(order.getCouponDiscountAmount()).thenReturn(BigDecimal.valueOf(2000));
		when(order.getDeliveryFee()).thenReturn(BigDecimal.valueOf(3000));
		when(order.getOrderState()).thenReturn(orderStateCompleted);
		when(orderStateCompleted.getState()).thenReturn(OrderStatus.COMPLETED);
		when(order.getTrackingNumber()).thenReturn("TRK789");
		when(order.getRefund()).thenReturn(refund); // 환불 정보 있음
		when(refund.getReason()).thenReturn(RefundReason.CHANGE_OF_MIND);
		when(refund.getReasonDetail()).thenReturn("단순 변심");

		// itemResponses setup (mapToItemResponse 호출을 위해)
		BookOrder bookOrder1 = mock(BookOrder.class);
		when(order.getBookOrders()).thenReturn(List.of(bookOrder1)); // 단일 아이템 가정
		// mapToItemResponse 내부에서 필요한 bookOrder 및 book mocking
		when(bookOrder1.getId()).thenReturn(100L);
		when(bookOrder1.getBook()).thenReturn(book);
		when(bookOrder1.getQuantity()).thenReturn(1);
		when(bookOrder1.getPrice()).thenReturn(BigDecimal.valueOf(45000));
		when(book.getId()).thenReturn(200L);
		when(book.getTitle()).thenReturn("테스트 책");
		when(book.getBookImgs()).thenReturn(List.of(bookImg));
		when(bookImg.isThumbnail()).thenReturn(true);
		when(bookImg.getImg()).thenReturn(img);
		when(img.getImgUrl()).thenReturn("http://thumb.url/book.jpg");
		when(bookOrder1.getOrderPackagings()).thenReturn(List.of(orderPackaging)); // 패키징 있음
		when(orderPackaging.getPackagingOption()).thenReturn(packagingOption);
		when(packagingOption.getId()).thenReturn(300L);
		when(packagingOption.getName()).thenReturn("선물포장");
		when(packagingOption.getPrice()).thenReturn(BigDecimal.valueOf(1000));
		when(orderPackaging.getQuantity()).thenReturn(1);
		when(bookOrder1.getUserCouponBookOrdersAssociatedWithThisBookOrder()).thenReturn(
			new ArrayList<>()); // 아이템 쿠폰 없음

		// payment setup
		when(order.getPayment()).thenReturn(payment);
		when(payment.getPaidAmount()).thenReturn(BigDecimal.valueOf(47000)); // 45000 + 1000 + 3000 - 2000
		when(payment.getPaymentDetail()).thenReturn(paymentDetail);
		when(paymentDetail.getPaymentType()).thenReturn(paymentType);
		when(paymentType.getMethod()).thenReturn("CARD");

		// Repository mocking
		when(orderRepository.findAdminOrderDetailsByOrderId(orderId)).thenReturn(Optional.of(order));

		OrderServiceImpl spyOrderService = spy(orderService);

		// When
		AdminOrderDetailResponse response = spyOrderService.getAdminOrderDetail(orderId);

		verify(orderRepository).findAdminOrderDetailsByOrderId(orderId);
		verify(order).getBookOrders();
		verify(order).getPayment();
		verify(order).getRefund();
	}

	@Test
	@DisplayName("getAdminOrderDetail 성공 - 비회원 주문, 결제 및 환불 정보 없음")
	void getAdminOrderDetail_Success_NonMemberOrder_NoPaymentNoRefund() {
		Long orderId = 2L;
		// order mock setup
		when(order.getId()).thenReturn(orderId);
		when(order.getOrderKey()).thenReturn("admin_order_key_2");
		when(order.getCreatedAt()).thenReturn(LocalDateTime.now().minusDays(1));
		when(order.getOrdererName()).thenReturn("비회원_주문자");
		when(order.getUser()).thenReturn(null); // 비회원
		when(order.getOrdererPhoneNumber()).thenReturn("010-5555-6666");
		when(order.getOrdererEmail()).thenReturn("nonmember@example.com");
		when(order.getReceiverName()).thenReturn("비회원_수령자");
		when(order.getReceiverPhoneNumber()).thenReturn("010-7777-8888");
		when(order.getReceiverEmail()).thenReturn("nonmember_receiver@example.com");
		when(order.getPostalCode()).thenReturn("54321");
		when(order.getAddress()).thenReturn("부산시");
		when(order.getDetailAddress()).thenReturn("어딘가");
		when(order.getOriginalAmount()).thenReturn(BigDecimal.valueOf(20000));
		when(order.getSaleDiscountAmount()).thenReturn(BigDecimal.ZERO);
		when(order.getPointUseAmount()).thenReturn(BigDecimal.ZERO);
		when(order.getCouponDiscountAmount()).thenReturn(BigDecimal.ZERO);
		when(order.getDeliveryFee()).thenReturn(BigDecimal.valueOf(2500));
		when(order.getOrderState()).thenReturn(orderStatePending);
		when(orderStatePending.getState()).thenReturn(OrderStatus.PENDING);
		when(order.getTrackingNumber()).thenReturn(null);
		when(order.getRefund()).thenReturn(null); // 환불 정보 없음

		// itemResponses setup
		BookOrder bookOrder1 = mock(BookOrder.class);
		when(order.getBookOrders()).thenReturn(List.of(bookOrder1)); // 단일 아이템 가정
		when(bookOrder1.getBook()).thenReturn(book);
		when(bookOrder1.getId()).thenReturn(101L);
		when(bookOrder1.getQuantity()).thenReturn(1);
		when(bookOrder1.getPrice()).thenReturn(BigDecimal.valueOf(20000));
		when(book.getId()).thenReturn(201L);
		when(book.getTitle()).thenReturn("비회원 책");
		when(book.getBookImgs()).thenReturn(List.of(bookImg));
		when(bookImg.isThumbnail()).thenReturn(true);
		when(bookImg.getImg()).thenReturn(img);
		when(img.getImgUrl()).thenReturn("http://thumb.url/nonmember_book.jpg");
		when(bookOrder1.getOrderPackagings()).thenReturn(new ArrayList<>()); // 패키징 없음
		when(bookOrder1.getUserCouponBookOrdersAssociatedWithThisBookOrder()).thenReturn(new ArrayList<>());

		// payment setup (payment, paymentDetail, paymentType 모두 null)
		when(order.getPayment()).thenReturn(null);

		// Repository mocking
		when(orderRepository.findAdminOrderDetailsByOrderId(orderId)).thenReturn(Optional.of(order));

		OrderServiceImpl spyOrderService = spy(orderService);

		// When
		AdminOrderDetailResponse response = spyOrderService.getAdminOrderDetail(orderId);

		verify(orderRepository).findAdminOrderDetailsByOrderId(orderId);
		verify(order).getBookOrders();
		verify(order).getPayment();
		verify(order).getRefund();
	}

	@Test
	@DisplayName("getAdminOrderDetail 실패 - 주문 없음")
	void getAdminOrderDetail_ThrowsOrderNotFoundException() {
		Long orderId = 999L;

		when(orderRepository.findAdminOrderDetailsByOrderId(orderId)).thenReturn(Optional.empty());

		OrderNotFoundException exception = assertThrows(OrderNotFoundException.class, () -> {
			orderService.getAdminOrderDetail(orderId);
		});

		assertEquals("주문을 찾을 수 없습니다.", exception.getMessage());
		verify(orderRepository).findAdminOrderDetailsByOrderId(orderId);
		verifyNoMoreInteractions(orderRepository);
	}

	@Test
	@DisplayName("updateOrderStatus 성공 - PENDING에서 SHIPPING으로 변경")
	void updateOrderStatus_Success_FromPendingToShipping() {
		Long orderId = 1L;
		OrderStatus newStatus = OrderStatus.SHIPPING;

		when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
		when(orderStateRepository.findByState(newStatus)).thenReturn(Optional.of(orderStateShipping));
		when(order.getOrderState()).thenReturn(orderStatePending);
		when(orderStatePending.getState()).thenReturn(OrderStatus.PENDING);

		orderService.updateOrderStatus(orderId, newStatus);

		verify(orderRepository).findById(orderId);
		verify(orderStateRepository).findByState(newStatus);
		verify(order, times(4)).getOrderState();
		verify(orderStatePending, times(4)).getState();
		verify(order).changeOrderState(orderStateShipping);
		verify(orderRepository).save(order);
		verify(order).changeShippedAt(any(LocalDateTime.class));
		verify(eventPublisher).publishEvent(any(OrderShippingMessage.class));
	}

	@Test
	@DisplayName("updateOrderStatus 성공 - SHIPPING에서 COMPLETED로 변경")
	void updateOrderStatus_Success_FromShippingToCompleted() {
		Long orderId = 1L;
		OrderStatus newStatus = OrderStatus.COMPLETED;

		when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
		when(orderStateRepository.findByState(newStatus)).thenReturn(Optional.of(orderStateCompleted));
		when(order.getOrderState()).thenReturn(orderStateShipping);
		when(orderStateShipping.getState()).thenReturn(OrderStatus.SHIPPING);

		orderService.updateOrderStatus(orderId, newStatus);

		verify(orderRepository).findById(orderId);
		verify(orderStateRepository).findByState(newStatus);
		verify(order, times(4)).getOrderState();
		verify(orderStateShipping, times(4)).getState();
		verify(order).changeOrderState(orderStateCompleted);
		verify(orderRepository).save(order);
		verify(order, never()).changeShippedAt(any(LocalDateTime.class)); // SHIPPING이 아니므로 호출 안됨
		verify(eventPublisher, never()).publishEvent(any(OrderShippingMessage.class)); // SHIPPING이 아니므로 호출 안됨
	}

	@Test
	@DisplayName("updateOrderStatus 실패 - 주문 없음")
	void updateOrderStatus_ThrowsOrderNotFoundException() {
		Long orderId = 999L;
		OrderStatus newStatus = OrderStatus.SHIPPING;

		when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

		OrderNotFoundException exception = assertThrows(OrderNotFoundException.class, () ->
			orderService.updateOrderStatus(orderId, newStatus)
		);

		assertEquals("주문을 찾을 수 없습니다.", exception.getMessage());
		verify(orderRepository).findById(orderId);
		verifyNoInteractions(orderStateRepository); // OrderStateRepository 호출 안됨
	}

	@Test
	@DisplayName("updateOrderStatus 실패 - 새 상태를 찾을 수 없음")
	void updateOrderStatus_ThrowsOrderStateNotFoundException() {
		Long orderId = 1L;
		OrderStatus newStatus = OrderStatus.SHIPPING;

		when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
		when(orderStateRepository.findByState(newStatus)).thenReturn(Optional.empty()); // 새 상태 없음

		OrderStateNotFoundException exception = assertThrows(OrderStateNotFoundException.class, () ->
			orderService.updateOrderStatus(orderId, newStatus)
		);

		verify(orderRepository).findById(orderId);
		verify(orderStateRepository).findByState(newStatus);
		verify(order, never()).changeOrderState(any()); // 상태 변경 호출 안됨
	}

	@Test
	@DisplayName("updateOrderStatus 실패 - CANCELED 주문 상태 변경 불가")
	void updateOrderStatus_ThrowsOrderInvalidStateException_CanceledOrder() {
		Long orderId = 1L;
		OrderStatus newStatus = OrderStatus.SHIPPING;

		when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
		when(orderStateRepository.findByState(newStatus)).thenReturn(Optional.of(orderStateShipping));
		when(order.getOrderState()).thenReturn(orderStateCanceled);
		when(orderStateCanceled.getState()).thenReturn(OrderStatus.CANCELED);

		OrderInvalidStateException exception = assertThrows(OrderInvalidStateException.class, () -> {
			orderService.updateOrderStatus(orderId, newStatus);
		});

		verify(orderRepository).findById(orderId);
		verify(orderStateRepository).findByState(newStatus);
		verify(order, times(2)).getOrderState();
		verify(orderStateCanceled, times(2)).getState();
		verify(order, never()).changeOrderState(any());
	}

	@Test
	@DisplayName("updateOrderStatus 실패 - RETURNED_REQUEST 주문 상태 변경 불가")
	void updateOrderStatus_ThrowsOrderInvalidStateException_ReturnedRequestOrder() {
		Long orderId = 1L;
		OrderStatus newStatus = OrderStatus.SHIPPING;

		when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
		when(orderStateRepository.findByState(newStatus)).thenReturn(Optional.of(orderStateShipping));
		when(order.getOrderState()).thenReturn(orderStateReturnedRequest);
		when(orderStateReturnedRequest.getState()).thenReturn(OrderStatus.RETURNED_REQUEST);

		OrderInvalidStateException exception = assertThrows(OrderInvalidStateException.class, () -> {
			orderService.updateOrderStatus(orderId, newStatus);
		});

		verify(orderRepository).findById(orderId);
		verify(orderStateRepository).findByState(newStatus);
		verify(order, times(3)).getOrderState();
		verify(orderStateReturnedRequest, times(3)).getState();

		verify(order, never()).changeOrderState(any());
	}

	@Test
	@DisplayName("updateOrderStatus 실패 - RETURNED 주문 상태 변경 불가")
	void updateOrderStatus_ThrowsOrderInvalidStateException_ReturnedOrder() {
		Long orderId = 1L;
		OrderStatus newStatus = OrderStatus.SHIPPING;

		when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
		when(orderStateRepository.findByState(newStatus)).thenReturn(Optional.of(orderStateShipping));
		when(order.getOrderState()).thenReturn(orderStateReturned);
		when(orderStateReturned.getState()).thenReturn(OrderStatus.RETURNED);

		OrderInvalidStateException exception = assertThrows(OrderInvalidStateException.class, () -> {
			orderService.updateOrderStatus(orderId, newStatus);
		});

		verify(orderRepository).findById(orderId);
		verify(orderStateRepository).findByState(newStatus);
		verify(order, times(4)).getOrderState();

		verify(order, never()).changeOrderState(any());
	}

	@Test
	@DisplayName("updateOrderStatus 실패 - 배송 중이 아닌 상태에서 배송완료 변경 불가")
	void updateOrderStatus_ThrowsOrderInvalidStateException_NotShippingToCompleted() {
		Long orderId = 1L;
		OrderStatus newStatus = OrderStatus.COMPLETED;

		when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
		when(orderStateRepository.findByState(newStatus)).thenReturn(Optional.of(orderStateCompleted));
		when(order.getOrderState()).thenReturn(orderStatePending); // PENDING 상태
		when(orderStatePending.getState()).thenReturn(OrderStatus.PENDING); // PENDING 상태 반환

		OrderInvalidStateException exception = assertThrows(OrderInvalidStateException.class, () ->
			orderService.updateOrderStatus(orderId, newStatus)
		);

		verify(orderRepository).findById(orderId);
		verify(orderStateRepository).findByState(newStatus);
		verify(order, times(4)).getOrderState();
		verify(orderStatePending, times(4)).getState();
		verify(order, never()).changeOrderState(any());
	}

	@Test
	@DisplayName("updateOrderTrackingNumber 성공")
	void updateOrderTrackingNumber_Success() {
		Long orderId = 1L;
		String trackingNumber = "NEW_TRACK_123";

		when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
		when(orderStateRepository.findByState(OrderStatus.SHIPPING)).thenReturn(Optional.of(orderStateShipping));
		when(order.getOrderState()).thenReturn(orderStatePending); // Assume current state allows change
		when(orderStatePending.getState()).thenReturn(OrderStatus.PENDING); // Mock for order.getOrderState().getState()

		orderService.updateOrderTrackingNumber(orderId, trackingNumber);

		verify(orderRepository).findById(orderId);
		verify(orderStateRepository).findByState(OrderStatus.SHIPPING);
		verify(order).getOrderState(); // Check current state
		verify(orderStatePending).getState(); // Get state value
		verify(order).changeOrderState(orderStateShipping); // Change to SHIPPING state
		verify(order).changeTrackingNumber(trackingNumber); // Set new tracking number
		verify(orderRepository).save(order); // Save the updated order
	}

	@Test
	@DisplayName("updateOrderTrackingNumber 실패 - 주문 없음")
	void updateOrderTrackingNumber_ThrowsOrderNotFoundException() {
		Long orderId = 999L;
		String trackingNumber = "NON_EXISTENT_TRACK";

		when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

		OrderNotFoundException exception = assertThrows(OrderNotFoundException.class, () ->
			orderService.updateOrderTrackingNumber(orderId, trackingNumber)
		);

		verify(orderRepository).findById(orderId);
		verifyNoInteractions(orderStateRepository); // No further interaction
	}

	@Test
	@DisplayName("updateOrderTrackingNumber 실패 - SHIPPING 상태를 찾을 수 없음")
	void updateOrderTrackingNumber_ThrowsOrderStateNotFoundException() {
		Long orderId = 1L;
		String trackingNumber = "TRACK_NO_STATE";

		when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
		when(orderStateRepository.findByState(OrderStatus.SHIPPING)).thenReturn(
			Optional.empty()); // SHIPPING state not found

		OrderStateNotFoundException exception = assertThrows(OrderStateNotFoundException.class, () ->
			orderService.updateOrderTrackingNumber(orderId, trackingNumber)
		);

		verify(orderRepository).findById(orderId);
		verify(orderStateRepository).findByState(OrderStatus.SHIPPING);
		verify(order, never()).changeOrderState(any()); // No state change
	}

	@Test
	@DisplayName("updateOrderTrackingNumber 실패 - CANCELED 주문 상태 변경 불가")
	void updateOrderTrackingNumber_ThrowsOrderInvalidStateException_CanceledOrder() {
		Long orderId = 1L;
		String trackingNumber = "TRACK_CANCELED";

		when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
		when(orderStateRepository.findByState(OrderStatus.SHIPPING)).thenReturn(Optional.of(orderStateShipping));
		when(order.getOrderState()).thenReturn(orderStateCanceled);
		when(orderStateCanceled.getState()).thenReturn(OrderStatus.CANCELED); // Current state is CANCELED

		OrderInvalidStateException exception = assertThrows(OrderInvalidStateException.class, () ->
			orderService.updateOrderTrackingNumber(orderId, trackingNumber)
		);

		verify(orderRepository).findById(orderId);
		verify(orderStateRepository).findByState(OrderStatus.SHIPPING);
		verify(order).getOrderState();
		verify(orderStateCanceled).getState();
		verify(order, never()).changeOrderState(any()); // No state change
	}
}