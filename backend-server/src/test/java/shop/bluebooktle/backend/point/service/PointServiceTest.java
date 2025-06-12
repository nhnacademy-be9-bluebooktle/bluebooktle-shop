package shop.bluebooktle.backend.point.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
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
import org.springframework.data.domain.Pageable;

import shop.bluebooktle.backend.payment.entity.Payment;
import shop.bluebooktle.backend.payment.repository.PaymentRepository;
import shop.bluebooktle.backend.point.entity.PaymentPointHistory;
import shop.bluebooktle.backend.point.repository.PaymentPointHistoryRepository;
import shop.bluebooktle.backend.point.repository.PointHistoryRepository;
import shop.bluebooktle.backend.point.repository.PointPolicyRepository;
import shop.bluebooktle.backend.point.repository.PointSourceTypeRepository;
import shop.bluebooktle.backend.point.service.impl.PointServiceImpl;
import shop.bluebooktle.backend.user.repository.UserRepository;
import shop.bluebooktle.common.domain.point.ActionType;
import shop.bluebooktle.common.domain.point.PointSourceTypeEnum;
import shop.bluebooktle.common.dto.point.request.PointAdjustmentRequest;
import shop.bluebooktle.common.dto.point.response.PointHistoryResponse;
import shop.bluebooktle.common.entity.auth.MembershipLevel;
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.entity.point.PointHistory;
import shop.bluebooktle.common.entity.point.PointPolicy;
import shop.bluebooktle.common.entity.point.PointSourceType;
import shop.bluebooktle.common.exception.auth.UserNotFoundException;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

	@InjectMocks
	private PointServiceImpl pointService;

	@Mock
	private PointHistoryRepository pointHistoryRepository;

	@Mock
	private UserRepository userRepository;
	@Mock
	private PointSourceTypeRepository pointSourceTypeRepository;

	@Mock
	private PointPolicyRepository pointPolicyRepository;

	@Mock
	private PaymentRepository paymentRepository;

	@Mock
	private PaymentPointHistoryRepository paymentPointHistoryRepository;

	private User user;
	private PointPolicy earnPolicy;
	private PointPolicy usePolicy;
	private MembershipLevel mockLevel;
	private Payment payment;
	private PointSourceType earnSourceType;
	private PointSourceType useSourceType;

	@BeforeEach
	void setUp() {
		user = mock(User.class);
		earnPolicy = mock(PointPolicy.class);
		usePolicy = mock(PointPolicy.class);
		earnSourceType = mock(PointSourceType.class);
		useSourceType = mock(PointSourceType.class);
		mockLevel = mock(MembershipLevel.class);
		payment = mock(Payment.class);
	}

	@Test
	@DisplayName("결제 적립 - 성공")
	void paymentEarn_success_flowOnly() {
		BigDecimal paymentAmount = new BigDecimal("10000");

		given(user.getMembershipLevel()).willReturn(mockLevel);
		given(mockLevel.getRate()).willReturn(1);
		given(earnSourceType.getActionType()).willReturn(ActionType.EARN);
		given(earnSourceType.getId()).willReturn(PointSourceTypeEnum.PAYMENT_EARN.getId());
		given(earnPolicy.getIsActive()).willReturn(true);
		given(earnPolicy.getValue()).willReturn(new BigDecimal("5"));

		given(userRepository.findUserById(anyLong())).willReturn(Optional.of(user));
		given(pointSourceTypeRepository.findById(anyLong())).willReturn(Optional.of(earnSourceType));
		given(pointPolicyRepository.findByPointSourceType(any(PointSourceType.class))).willReturn(
			Optional.of(earnPolicy));
		given(paymentRepository.findById(anyLong())).willReturn(Optional.of(payment));
		given(pointHistoryRepository.save(any(PointHistory.class))).willReturn(mock(PointHistory.class));

		pointService.adjustUserPointAndSavePointHistory(1L, PointSourceTypeEnum.PAYMENT_EARN, paymentAmount,
			1L);

		verify(user).addPoint(any(BigDecimal.class));

		verify(userRepository).save(any(User.class));

		verify(pointHistoryRepository).save(any(PointHistory.class));

		verify(paymentPointHistoryRepository).save(any(PaymentPointHistory.class));
	}

	@Test
	@DisplayName("리뷰 적립 - 성공")
	void reviewEarn_success_flowOnly() {
		Long userId = 1L;
		PointSourceTypeEnum sourceTypeEnum = PointSourceTypeEnum.REVIEW_EARN;
		given(userRepository.findUserById(anyLong())).willReturn(Optional.of(user));
		given(pointSourceTypeRepository.findById(anyLong())).willReturn(Optional.of(earnSourceType));
		given(pointPolicyRepository.findByPointSourceType(any(PointSourceType.class))).willReturn(
			Optional.of(earnPolicy));
		given(earnSourceType.getActionType()).willReturn(ActionType.EARN);
		given(earnPolicy.getIsActive()).willReturn(true);
		given(earnPolicy.getValue()).willReturn(new BigDecimal("500"));

		pointService.adjustUserPointAndSavePointHistory(userId, sourceTypeEnum, null, null);

		verify(user).addPoint(any(BigDecimal.class));
		verify(userRepository).save(any(User.class));
		verify(pointHistoryRepository).save(any(PointHistory.class));
		verify(paymentPointHistoryRepository, never()).save(any(PaymentPointHistory.class));
	}

	@Test
	@DisplayName("포인트 사용 - 성공")
	void paymentUse_success() {
		PointSourceTypeEnum sourceTypeEnum = PointSourceTypeEnum.PAYMENT_USE;
		BigDecimal useAmount = new BigDecimal("300");
		given(userRepository.findUserById(anyLong())).willReturn(Optional.of(user));
		given(useSourceType.getActionType()).willReturn(ActionType.USE);

		given(pointSourceTypeRepository.findById(sourceTypeEnum.getId())).willReturn(Optional.of(useSourceType));
		given(pointPolicyRepository.findByPointSourceType(useSourceType)).willReturn(Optional.of(usePolicy));

		given(usePolicy.getIsActive()).willReturn(true);

		pointService.adjustUserPointAndSavePointHistory(user.getId(), sourceTypeEnum, useAmount, null);

		verify(user).subtractPoint(useAmount);
		verify(pointHistoryRepository).save(any(PointHistory.class));
	}

	@Test
	@DisplayName("정책 - 비활성 상태")
	void policyIsInactive_shouldDoNothing() {

		PointSourceTypeEnum sourceTypeEnum = PointSourceTypeEnum.REVIEW_EARN;
		given(userRepository.findUserById(anyLong())).willReturn(Optional.of(user));

		given(earnPolicy.getIsActive()).willReturn(false);
		given(pointSourceTypeRepository.findById(sourceTypeEnum.getId())).willReturn(Optional.of(earnSourceType));
		given(pointPolicyRepository.findByPointSourceType(earnSourceType)).willReturn(Optional.of(earnPolicy));

		pointService.adjustUserPointAndSavePointHistory(user.getId(), sourceTypeEnum, null, null);

		verify(user, never()).addPoint(any());
		verify(user, never()).subtractPoint(any());
		verify(pointHistoryRepository, never()).save(any());
	}

	@Test
	@DisplayName(" 적립/사용 - null/0")
	void amountIsZeroOrNull_shouldDoNothing() {

		PointSourceTypeEnum sourceTypeEnum = PointSourceTypeEnum.PAYMENT_EARN;
		given(userRepository.findUserById(anyLong())).willReturn(Optional.of(user));
		given(pointSourceTypeRepository.findById(sourceTypeEnum.getId())).willReturn(Optional.of(earnSourceType));
		given(pointPolicyRepository.findByPointSourceType(earnSourceType)).willReturn(Optional.of(earnPolicy));

		pointService.adjustUserPointAndSavePointHistory(user.getId(), sourceTypeEnum, BigDecimal.ZERO, null);

		verify(user, never()).addPoint(any());
		verify(pointHistoryRepository, never()).save(any());
	}

	@Test
	@DisplayName("유저를 찾을 수 없음 - 실패")
	void userNotFound_fail() {
		given(userRepository.findUserById(anyLong())).willReturn(Optional.empty());

		assertThrows(UserNotFoundException.class, () ->
			pointService.adjustUserPointAndSavePointHistory(99L, PointSourceTypeEnum.REVIEW_EARN, null, null));
	}

	@Test
	@DisplayName("분기 테스트 - 결제 적립 시 금액 0이면 포인트를 적립하지 않음")
	void paymentEarn_whenAmountIsZero_shouldDoNothing() {
		// given
		PointSourceTypeEnum sourceTypeEnum = PointSourceTypeEnum.PAYMENT_EARN;
		BigDecimal zeroAmount = BigDecimal.ZERO;

		given(userRepository.findUserById(anyLong())).willReturn(Optional.of(user));
		given(pointSourceTypeRepository.findById(sourceTypeEnum.getId())).willReturn(Optional.of(earnSourceType));
		given(pointPolicyRepository.findByPointSourceType(earnSourceType)).willReturn(Optional.of(earnPolicy));
		given(earnPolicy.getIsActive()).willReturn(true);
		given(earnSourceType.getActionType()).willReturn(ActionType.EARN);
		given(earnSourceType.getId()).willReturn(PointSourceTypeEnum.PAYMENT_EARN.getId());

		pointService.adjustUserPointAndSavePointHistory(1L, sourceTypeEnum, zeroAmount, 1L);

		verify(user, never()).addPoint(any());
		verify(userRepository, never()).save(any());
		verify(pointHistoryRepository, never()).save(any());
	}

	@Test
	@DisplayName("분기 테스트 - 결제 적립 시 금액 null 이면 포인트를 적립하지 않음")
	void paymentEarn_whenAmountIsNull_shouldDoNothing() {
		// given
		PointSourceTypeEnum sourceTypeEnum = PointSourceTypeEnum.PAYMENT_EARN;
		BigDecimal zeroAmount = null;

		given(userRepository.findUserById(anyLong())).willReturn(Optional.of(user));
		given(pointSourceTypeRepository.findById(sourceTypeEnum.getId())).willReturn(Optional.of(earnSourceType));
		given(pointPolicyRepository.findByPointSourceType(earnSourceType)).willReturn(Optional.of(earnPolicy));
		given(earnPolicy.getIsActive()).willReturn(true);
		given(earnSourceType.getActionType()).willReturn(ActionType.EARN);
		given(earnSourceType.getId()).willReturn(PointSourceTypeEnum.PAYMENT_EARN.getId());

		pointService.adjustUserPointAndSavePointHistory(1L, sourceTypeEnum, zeroAmount, 1L);

		verify(user, never()).addPoint(any());
		verify(userRepository, never()).save(any());
		verify(pointHistoryRepository, never()).save(any());
	}

	@Test
	@DisplayName("분기 테스트 - 포인트 사용 시 금액 0이면 포인트를 적립하지 않음")
	void paymentUse_whenAmountIsZero_shouldDoNothing() {
		// given
		PointSourceTypeEnum sourceTypeEnum = PointSourceTypeEnum.PAYMENT_USE;
		BigDecimal zeroAmount = BigDecimal.ZERO;

		given(userRepository.findUserById(anyLong())).willReturn(Optional.of(user));
		given(pointSourceTypeRepository.findById(anyLong())).willReturn(Optional.of(useSourceType));
		given(pointPolicyRepository.findByPointSourceType(any(PointSourceType.class))).willReturn(
			Optional.of(usePolicy));
		given(usePolicy.getIsActive()).willReturn(true);
		given(useSourceType.getActionType()).willReturn(ActionType.USE);

		pointService.adjustUserPointAndSavePointHistory(1L, sourceTypeEnum, zeroAmount, 1L);

		verify(user, never()).addPoint(any());
		verify(userRepository, never()).save(any());
		verify(pointHistoryRepository, never()).save(any());
	}

	@Test
	@DisplayName("분기 테스트 - 포인트 사용 시 금액 Null 이면 포인트를 적립하지 않음")
	void paymentUse_whenAmountIsNull_shouldDoNothing() {
		PointSourceTypeEnum sourceTypeEnum = PointSourceTypeEnum.PAYMENT_USE;

		given(userRepository.findUserById(anyLong())).willReturn(Optional.of(user));
		given(pointSourceTypeRepository.findById(anyLong())).willReturn(Optional.of(useSourceType));
		given(pointPolicyRepository.findByPointSourceType(any(PointSourceType.class))).willReturn(
			Optional.of(usePolicy));
		given(usePolicy.getIsActive()).willReturn(true);

		pointService.adjustUserPointAndSavePointHistory(1L, sourceTypeEnum, null, 1L);

		verify(user, never()).addPoint(any());
		verify(userRepository, never()).save(any());
		verify(pointHistoryRepository, never()).save(any());
	}

	@Test
	@DisplayName("분기 테스트 - 계산된 포인트가 0이면 이력을 저장하지 않음")
	void whenCalculatedPointIsZero_shouldNotSaveHistory() {
		PointSourceTypeEnum sourceTypeEnum = PointSourceTypeEnum.PAYMENT_EARN;
		BigDecimal smallAmount = BigDecimal.ZERO;

		given(userRepository.findUserById(anyLong())).willReturn(Optional.of(user));
		given(pointSourceTypeRepository.findById(sourceTypeEnum.getId())).willReturn(Optional.of(earnSourceType));
		given(pointPolicyRepository.findByPointSourceType(earnSourceType)).willReturn(Optional.of(earnPolicy));
		given(earnPolicy.getIsActive()).willReturn(true);
		given(earnSourceType.getActionType()).willReturn(ActionType.EARN);
		given(earnSourceType.getId()).willReturn(PointSourceTypeEnum.PAYMENT_EARN.getId());

		pointService.adjustUserPointAndSavePointHistory(1L, sourceTypeEnum, smallAmount, 1L);

		verify(user, never()).addPoint(BigDecimal.ZERO);
		verify(userRepository, never()).save(user);

		verify(pointHistoryRepository, never()).save(any());
		verify(paymentPointHistoryRepository, never()).save(any());
	}

	@Test
	@DisplayName("분기 테스트 - 계산된 포인트가 null 이면 이력을 저장하지 않음")
	void whenCalculatedPointIsNull_shouldNotSaveHistory() {
		PointSourceTypeEnum sourceTypeEnum = PointSourceTypeEnum.PAYMENT_EARN;
		BigDecimal smallAmount = null;

		given(userRepository.findUserById(anyLong())).willReturn(Optional.of(user));
		given(pointSourceTypeRepository.findById(sourceTypeEnum.getId())).willReturn(Optional.of(earnSourceType));
		given(pointPolicyRepository.findByPointSourceType(earnSourceType)).willReturn(Optional.of(earnPolicy));
		given(earnPolicy.getIsActive()).willReturn(true);
		given(earnSourceType.getActionType()).willReturn(ActionType.EARN);
		given(earnSourceType.getId()).willReturn(PointSourceTypeEnum.PAYMENT_EARN.getId());

		pointService.adjustUserPointAndSavePointHistory(1L, sourceTypeEnum, smallAmount, 1L);

		verify(user, never()).addPoint(any());
		verify(userRepository, never()).save(user);

		verify(pointHistoryRepository, never()).save(any());
		verify(paymentPointHistoryRepository, never()).save(any());
	}

	@Test
	@DisplayName("포인트 이력 수동 저장 - 성공")
	void savePointHistory_success() {
		Long userId = 1L;
		PointAdjustmentRequest request = new PointAdjustmentRequest(PointSourceTypeEnum.LOGIN_EARN.getId(),
			new BigDecimal("1000"));

		given(userRepository.findById(userId)).willReturn(Optional.of(user));
		given(pointSourceTypeRepository.findById(request.pointSourceTypeId())).willReturn(Optional.of(earnSourceType));
		given(pointPolicyRepository.findByPointSourceType(earnSourceType)).willReturn(Optional.of(earnPolicy));
		given(earnPolicy.getIsActive()).willReturn(true);

		pointService.savePointHistory(userId, request);

		verify(pointHistoryRepository).save(any());
	}

	@Test
	@DisplayName("포인트 이력 수동 저장 - 정책이 비활성 상태이면 저장하지 않음")
	void savePointHistory_whenPolicyIsInactive_shouldDoNothing() {
		// given
		Long userId = 1L;
		PointAdjustmentRequest request = new PointAdjustmentRequest(PointSourceTypeEnum.REVIEW_EARN.getId(),
			new BigDecimal("1000"));

		given(userRepository.findById(userId)).willReturn(Optional.of(user));
		given(pointSourceTypeRepository.findById(request.pointSourceTypeId())).willReturn(Optional.of(earnSourceType));
		given(pointPolicyRepository.findByPointSourceType(earnSourceType)).willReturn(Optional.of(earnPolicy));
		given(earnPolicy.getIsActive()).willReturn(false);

		pointService.savePointHistory(userId, request);
		verify(pointHistoryRepository, never()).save(any());
	}

	@Test
	@DisplayName("유저 ID로 포인트 이력 조회 - 성공")
	void getPointHistories_success() {
		Pageable pageable = Pageable.ofSize(10);
		Long userId = 1L;

		PointHistory history1 = mock(PointHistory.class);
		PointSourceType pst1 = mock(PointSourceType.class);
		given(history1.getId()).willReturn(100L);
		given(history1.getValue()).willReturn(new BigDecimal("500"));
		given(history1.getCreatedAt()).willReturn(LocalDateTime.now());
		given(history1.getSourceType()).willReturn(PointSourceTypeEnum.LOGIN_EARN);

		Page<PointHistory> historyPage = new PageImpl<>(List.of(history1), pageable, 1);
		given(pointHistoryRepository.findByUser_IdOrderByCreatedAtDesc(userId, pageable)).willReturn(historyPage);

		Page<PointHistoryResponse> responsePage = pointService.getPointHistoriesByUserId(userId, pageable);

		assertThat(responsePage).isNotNull();
		assertThat(responsePage.getTotalElements()).isEqualTo(1);
		assertThat(responsePage.getContent().getFirst().id()).isEqualTo(100L);
		assertThat(responsePage.getContent().getFirst().pointSourceType().sourceType()).isEqualTo(
			PointSourceTypeEnum.LOGIN_EARN.getSourceType());
	}

	@Test
	@DisplayName("리뷰 포인트 지급 - 내부 메서드를 올바른 인자로 호출")
	void reviewPoint_shouldCallInternalMethodCorrectly() {
		Long userId = 1L;

		PointServiceImpl spiedService = spy(pointService);

		doNothing().when(spiedService).adjustUserPointAndSavePointHistory(anyLong(), any(), any(), any());

		spiedService.reviewPoint(userId);

		verify(spiedService).adjustUserPointAndSavePointHistory(
			userId,
			PointSourceTypeEnum.REVIEW_EARN,
			null,
			null
		);
	}
}