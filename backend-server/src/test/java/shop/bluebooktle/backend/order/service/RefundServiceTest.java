package shop.bluebooktle.backend.order.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import shop.bluebooktle.backend.order.entity.DeliveryRule;
import shop.bluebooktle.backend.order.entity.Order;
import shop.bluebooktle.backend.order.entity.OrderState;
import shop.bluebooktle.backend.order.entity.Refund;
import shop.bluebooktle.backend.order.repository.OrderRepository;
import shop.bluebooktle.backend.order.repository.OrderStateRepository;
import shop.bluebooktle.backend.order.repository.RefundRepository;
import shop.bluebooktle.backend.order.service.impl.RefundServiceImpl;
import shop.bluebooktle.backend.payment.entity.Payment;
import shop.bluebooktle.backend.point.repository.PointHistoryRepository;
import shop.bluebooktle.common.domain.order.OrderStatus;
import shop.bluebooktle.common.domain.refund.RefundReason;
import shop.bluebooktle.common.domain.refund.RefundStatus;
import shop.bluebooktle.common.dto.refund.request.RefundCreateRequest;
import shop.bluebooktle.common.dto.refund.request.RefundSearchRequest;
import shop.bluebooktle.common.dto.refund.request.RefundUpdateRequest;
import shop.bluebooktle.common.dto.refund.response.AdminRefundDetailResponse;
import shop.bluebooktle.common.dto.refund.response.RefundListResponse;
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.entity.point.PointHistory;
import shop.bluebooktle.common.exception.refund.RefundAlreadyProcessedException;
import shop.bluebooktle.common.exception.refund.RefundNotFoundException;
import shop.bluebooktle.common.exception.refund.RefundNotPossibleException;

@ExtendWith(MockitoExtension.class)
class RefundServiceTest {

	@InjectMocks
	private RefundServiceImpl refundService;

	@Mock
	private RefundRepository refundRepository;
	@Mock
	private OrderRepository orderRepository;
	@Mock
	private PointHistoryRepository pointHistoryRepository;
	@Mock
	private OrderStateRepository orderStateRepository;

	private User mockUser;
	private Order mockOrder;
	private Payment mockPayment;
	private Refund mockRefund; // 추가

	@BeforeEach
	void setUp() {
		mockUser = mock(User.class);
		mockOrder = mock(Order.class);
		mockPayment = mock(Payment.class);
		mockRefund = mock(Refund.class); // 추가
	}

	@Test
	@DisplayName("환불 요청 - 성공")
	void requestRefund_success() {
		Long userId = 1L;
		RefundCreateRequest request = new RefundCreateRequest("orderKey123", RefundReason.CHANGE_OF_MIND, "");
		OrderState returnedRequestState = new OrderState(OrderStatus.RETURNED_REQUEST);

		given(mockOrder.getUser()).willReturn(mockUser);
		given(mockUser.getId()).willReturn(userId);
		given(mockOrder.getShippedAt()).willReturn(LocalDateTime.now().minusDays(5));
		given(orderRepository.getOrderByOrderKey(request.orderKey())).willReturn(Optional.of(mockOrder));
		given(orderStateRepository.findByState(OrderStatus.RETURNED_REQUEST)).willReturn(
			Optional.of(returnedRequestState));

		given(mockOrder.getPayment()).willReturn(mockPayment);
		given(mockPayment.getPaidAmount()).willReturn(BigDecimal.TEN);

		refundService.requestRefund(userId, request);

		verify(refundRepository).save(any(Refund.class));
		verify(mockOrder).changeOrderState(returnedRequestState);
	}

	@Test
	@DisplayName("환불 요청 실패 - 주문 소유주 불일치")
	void requestRefund_fail_whenUserMismatched() {
		Long requestUserId = 1L;
		Long orderOwnerId = 2L;
		RefundCreateRequest request = new RefundCreateRequest("orderKey123", RefundReason.CHANGE_OF_MIND, "");

		given(mockOrder.getUser()).willReturn(mockUser);
		given(mockUser.getId()).willReturn(orderOwnerId);
		given(orderRepository.getOrderByOrderKey(request.orderKey())).willReturn(Optional.of(mockOrder));
		given(mockOrder.getPayment()).willReturn(mockPayment);
		given(mockPayment.getPaidAmount()).willReturn(BigDecimal.TEN);

		assertThatThrownBy(() -> refundService.requestRefund(requestUserId, request))
			.isInstanceOf(RefundNotPossibleException.class);
	}

	@Test
	@DisplayName("환불 요청 실패 - 환불 가능 기간 만료")
	void requestRefund_fail_whenPeriodExpired() {
		Long userId = 1L;
		RefundCreateRequest request = new RefundCreateRequest("orderKey123", RefundReason.CHANGE_OF_MIND, "");

		given(mockOrder.getUser()).willReturn(mockUser);
		given(mockUser.getId()).willReturn(userId);
		given(mockOrder.getShippedAt()).willReturn(LocalDateTime.now().minusDays(15));
		given(orderRepository.getOrderByOrderKey(request.orderKey())).willReturn(Optional.of(mockOrder));
		given(mockOrder.getPayment()).willReturn(mockPayment);
		given(mockPayment.getPaidAmount()).willReturn(BigDecimal.TEN);

		assertThatThrownBy(() -> refundService.requestRefund(userId, request))
			.isInstanceOf(RefundNotPossibleException.class);
	}

	@Test
	@DisplayName("환불 상태 업데이트 성공 - 환불 완료 (구매자 귀책)")
	void updateRefund_success_userFault() {
		Refund mockRefund = mock(Refund.class);
		DeliveryRule mockDeliveryRule = mock(DeliveryRule.class);
		RefundUpdateRequest request = new RefundUpdateRequest(1L, RefundStatus.COMPLETE);

		given(mockRefund.getOrder()).willReturn(mockOrder);
		given(mockOrder.getUser()).willReturn(mockUser);
		given(mockOrder.getDeliveryRule()).willReturn(mockDeliveryRule);

		given(refundRepository.findRefundDetailsById(request.refundId())).willReturn(Optional.of(mockRefund));
		given(mockRefund.getStatus()).willReturn(RefundStatus.PENDING);
		given(mockRefund.getReason()).willReturn(RefundReason.CHANGE_OF_MIND);
		given(mockDeliveryRule.getDeliveryFee()).willReturn(new BigDecimal("3000"));
		given(mockOrder.getOriginalAmount()).willReturn(new BigDecimal("20000"));
		given(mockOrder.getSaleDiscountAmount()).willReturn(new BigDecimal("1000"));
		given(mockOrder.getPointUseAmount()).willReturn(new BigDecimal("500"));
		given(orderStateRepository.findByState(OrderStatus.RETURNED)).willReturn(
			Optional.of(new OrderState(OrderStatus.RETURNED)));

		// when
		refundService.updateRefund(request);

		// then
		verify(mockUser).addPoint(any(BigDecimal.class));
		verify(pointHistoryRepository).save(any(PointHistory.class));
		verify(mockOrder).changeOrderState(any(OrderState.class));
		verify(mockRefund).changeStatus(RefundStatus.COMPLETE);
	}

	@Test
	@DisplayName("환불 상태 업데이트 실패 - 이미 처리된 환불 건")
	void updateRefund_fail_whenAlreadyCompleted() {
		Refund mockRefund = mock(Refund.class);
		RefundUpdateRequest request = new RefundUpdateRequest(1L, RefundStatus.COMPLETE);

		given(mockRefund.getStatus()).willReturn(RefundStatus.COMPLETE);

		given(mockOrder.getDeliveryRule()).willReturn(mock(DeliveryRule.class));

		given(refundRepository.findRefundDetailsById(request.refundId())).willReturn(Optional.of(mockRefund));
		given(mockRefund.getOrder()).willReturn(mockOrder);

		// when & then
		assertThatThrownBy(() -> refundService.updateRefund(request))
			.isInstanceOf(RefundAlreadyProcessedException.class);
	}

	@Test
	@DisplayName("환불 목록 조회 시 리포지토리 호출 검증")
	void getRefundList_ShouldCallRepositoryMethod() {
		// given
		RefundSearchRequest request = new RefundSearchRequest(null, null, null, null, null);
		Pageable pageable = PageRequest.of(0, 10);

		// mapToRefundListResponse 내부에서 사용하는 필드들에 대한 stubbing
		given(mockRefund.getOrder()).willReturn(mockOrder);
		given(mockRefund.getId()).willReturn(1L);
		given(mockRefund.getDate()).willReturn(LocalDateTime.now());
		given(mockRefund.getPrice()).willReturn(BigDecimal.TEN);
		given(mockRefund.getReason()).willReturn(RefundReason.DAMAGED);
		given(mockRefund.getStatus()).willReturn(RefundStatus.PENDING);
		given(mockOrder.getId()).willReturn(10L);
		given(mockOrder.getOrderKey()).willReturn("testOrderKey");
		given(mockOrder.getOrdererName()).willReturn("테스트 주문자");

		Page<Refund> refundPage = new PageImpl<>(List.of(mockRefund));
		given(refundRepository.searchRefunds(any(RefundSearchRequest.class), any(Pageable.class)))
			.willReturn(refundPage);

		// when
		refundService.getRefundList(request, pageable);

		// then
		verify(refundRepository).searchRefunds(request, pageable);
	}

	@Test
	@DisplayName("mapToRefundListResponse - Refund 엔티티를 DTO로 정확하게 변환한다")
	void mapToRefundListResponse_CorrectlyMapsToDto() {
		// given
		given(mockRefund.getOrder()).willReturn(mockOrder);
		given(mockRefund.getId()).willReturn(1L);
		given(mockRefund.getDate()).willReturn(LocalDateTime.now());
		given(mockRefund.getPrice()).willReturn(BigDecimal.TEN);
		given(mockRefund.getReason()).willReturn(RefundReason.DAMAGED);
		given(mockRefund.getStatus()).willReturn(RefundStatus.PENDING);
		given(mockOrder.getId()).willReturn(10L);
		given(mockOrder.getOrderKey()).willReturn("testOrderKey");
		given(mockOrder.getOrdererName()).willReturn("테스트 주문자");

		Page<Refund> refundPage = new PageImpl<>(List.of(mockRefund));
		given(refundRepository.searchRefunds(any(), any())).willReturn(refundPage);

		Page<RefundListResponse> resultPage = refundService.getRefundList(
			new RefundSearchRequest(null, null, null, null, null),
			PageRequest.of(0, 10));

		assertThat(resultPage.getContent()).hasSize(1);
		RefundListResponse resultDto = resultPage.getContent().get(0);

		assertThat(resultDto.refundId()).isEqualTo(mockRefund.getId());
		assertThat(resultDto.orderId()).isEqualTo(mockOrder.getId());
		assertThat(resultDto.orderKey()).isEqualTo(mockOrder.getOrderKey());
		assertThat(resultDto.ordererName()).isEqualTo(mockOrder.getOrdererName());
		assertThat(resultDto.requestDate()).isEqualTo(mockRefund.getDate());
		assertThat(resultDto.refundPrice()).isEqualTo(mockRefund.getPrice());
		assertThat(resultDto.reason()).isEqualTo(mockRefund.getReason());
		assertThat(resultDto.status()).isEqualTo(mockRefund.getStatus());
	}

	@Test
	@DisplayName("관리자 환불 상세 조회 - 성공 (회원)")
	void getAdminRefundDetail_success_member() {
		// given
		Long refundId = 1L;
		given(mockRefund.getOrder()).willReturn(mockOrder);
		given(mockOrder.getUser()).willReturn(mockUser); // 회원이므로 user 객체 반환
		given(mockRefund.getId()).willReturn(refundId);
		given(mockRefund.getDate()).willReturn(LocalDateTime.now());
		given(mockRefund.getStatus()).willReturn(RefundStatus.PENDING);
		given(mockRefund.getPrice()).willReturn(BigDecimal.valueOf(15000));
		given(mockRefund.getReason()).willReturn(RefundReason.CHANGE_OF_MIND);
		given(mockRefund.getReasonDetail()).willReturn("단순 변심");
		given(mockOrder.getId()).willReturn(100L);
		given(mockOrder.getOrderKey()).willReturn("adminOrderKey");
		given(mockOrder.getOrdererName()).willReturn("관리자 테스트");
		given(mockUser.getLoginId()).willReturn("testUser");

		given(refundRepository.findRefundDetailsById(refundId)).willReturn(Optional.of(mockRefund));

		// when
		AdminRefundDetailResponse response = refundService.getAdminRefundDetail(refundId);

		// then
		assertThat(response.refundId()).isEqualTo(refundId);
		assertThat(response.orderKey()).isEqualTo("adminOrderKey");
		assertThat(response.userLoginId()).isEqualTo("testUser");
		verify(refundRepository).findRefundDetailsById(refundId);
	}

	@Test
	@DisplayName("관리자 환불 상세 조회 - 성공 (비회원)")
	void getAdminRefundDetail_success_nonMember() {
		// given
		Long refundId = 2L;
		given(mockRefund.getOrder()).willReturn(mockOrder);
		given(mockOrder.getUser()).willReturn(null); // 비회원이므로 null 반환
		given(mockRefund.getId()).willReturn(refundId);
		given(mockRefund.getDate()).willReturn(LocalDateTime.now());
		given(mockRefund.getStatus()).willReturn(RefundStatus.PENDING);
		given(mockRefund.getPrice()).willReturn(BigDecimal.valueOf(15000));
		given(mockRefund.getReason()).willReturn(RefundReason.CHANGE_OF_MIND);
		given(mockRefund.getReasonDetail()).willReturn("단순 변심");
		given(mockOrder.getId()).willReturn(101L);
		given(mockOrder.getOrderKey()).willReturn("adminOrderKeyNonMember");
		given(mockOrder.getOrdererName()).willReturn("비회원 주문자");

		given(refundRepository.findRefundDetailsById(refundId)).willReturn(Optional.of(mockRefund));

		// when
		AdminRefundDetailResponse response = refundService.getAdminRefundDetail(refundId);

		// then
		assertThat(response.refundId()).isEqualTo(refundId);
		assertThat(response.orderKey()).isEqualTo("adminOrderKeyNonMember");
		assertThat(response.userLoginId()).isEqualTo("비회원"); // 비회원일 경우 "비회원" 반환
		verify(refundRepository).findRefundDetailsById(refundId);
	}

	@Test
	@DisplayName("관리자 환불 상세 조회 실패 - 환불 정보를 찾을 수 없음")
	void getAdminRefundDetail_fail_refundNotFound() {
		// given
		Long refundId = 99L;
		given(refundRepository.findRefundDetailsById(refundId)).willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> refundService.getAdminRefundDetail(refundId))
			.isInstanceOf(RefundNotFoundException.class);
		verify(refundRepository).findRefundDetailsById(refundId);
	}
}