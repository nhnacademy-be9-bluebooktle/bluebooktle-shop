package shop.bluebooktle.auth.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import shop.bluebooktle.auth.repository.UserRepository;
import shop.bluebooktle.auth.repository.point.PointHistoryRepository;
import shop.bluebooktle.auth.repository.point.PointPolicyRepository;
import shop.bluebooktle.auth.repository.point.PointSourceTypeRepository;
import shop.bluebooktle.auth.service.impl.PointServiceImpl;
import shop.bluebooktle.common.domain.auth.UserStatus;
import shop.bluebooktle.common.domain.auth.UserType;
import shop.bluebooktle.common.domain.point.ActionType;
import shop.bluebooktle.common.domain.point.PointSourceTypeEnum;
import shop.bluebooktle.common.domain.point.PolicyType;
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.entity.point.PointHistory;
import shop.bluebooktle.common.entity.point.PointPolicy;
import shop.bluebooktle.common.entity.point.PointSourceType;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

	@InjectMocks
	private PointServiceImpl pointService;

	@Mock
	private UserRepository userRepository;
	@Mock
	private PointSourceTypeRepository pointSourceTypeRepository;
	@Mock
	private PointPolicyRepository pointPolicyRepository;
	@Mock
	private PointHistoryRepository pointHistoryRepository;

	private User testUser;

	@BeforeEach
	void setUp() {
		testUser = User.builder()
			.id(1L)
			.loginId("loginId")
			.nickname("nick")
			.status(UserStatus.ACTIVE)
			.type(UserType.USER)
			.pointBalance(BigDecimal.ZERO)
			.build();
	}

	@Test
	@DisplayName("adjustUserPoint - EARN 타입이면 포인트 적립")
	void adjustUserPointAndSaveHistory_earn() {
		PointSourceType sourceType = PointSourceType.builder()
			.actionType(ActionType.EARN)
			.sourceType("회원가입")
			.build();

		PointPolicy policy = PointPolicy.builder()
			.pointSourceType(sourceType)
			.value(BigDecimal.valueOf(5000))
			.policyType(PolicyType.AMOUNT)
			.build();
		policy.changeIsActive(true);

		when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
		when(pointSourceTypeRepository.findById(PointSourceTypeEnum.SIGNUP_EARN.getId()))
			.thenReturn(Optional.of(sourceType));
		when(pointPolicyRepository.findByPointSourceType(sourceType)).thenReturn(Optional.of(policy));
		when(userRepository.save(any())).thenReturn(testUser);
		when(pointHistoryRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0)); // ✔️ 누락되었을 가능성 있음

		// 실행
		pointService.adjustUserPointAndSavePointHistory(1L, PointSourceTypeEnum.SIGNUP_EARN);

		// 검증
		assertThat(testUser.getPointBalance()).isEqualByComparingTo("5000");
		verify(pointHistoryRepository).save(any(PointHistory.class)); // ✔️ verify는 반드시 실행 후
	}

	@Test
	@DisplayName("adjustUserPoint - 정책이 비활성화인 경우 아무 동작 안함")
	void adjustUserPoint_inactivePolicy() {
		PointSourceType sourceType = PointSourceType.builder()
			.actionType(ActionType.EARN)
			.sourceType("회원가입")
			.build();

		PointPolicy policy = PointPolicy.builder()
			.pointSourceType(sourceType)
			.policyType(PolicyType.AMOUNT)
			.value(BigDecimal.valueOf(5000))
			.build(); // 기본값으로 isActive == false

		when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

		when(pointSourceTypeRepository.findById(PointSourceTypeEnum.SIGNUP_EARN.getId()))
			.thenReturn(Optional.of(sourceType));
		when(pointPolicyRepository.findByPointSourceType(sourceType))
			.thenReturn(Optional.of(policy));

		pointService.adjustUserPointAndSavePointHistory(1L, PointSourceTypeEnum.SIGNUP_EARN);

		assertThat(testUser.getPointBalance()).isEqualByComparingTo("0");
		verify(pointHistoryRepository, never()).save(any());
	}

	@Test
	@DisplayName("loginPoint - 오늘 이미 적립했다면 skip")
	void loginPoint_alreadyEarnedToday() {
		PointHistory existing = PointHistory.builder()
			.sourceType(PointSourceTypeEnum.LOGIN_EARN)
			.user(testUser)
			.value(BigDecimal.valueOf(100))
			.build();

		// createdAt을 수동으로 설정
		ReflectionTestUtils.setField(existing, "createdAt", LocalDateTime.now());

		when(
			pointHistoryRepository.findTopByUserIdAndSourceTypeOrderByCreatedAtDesc(1L, PointSourceTypeEnum.LOGIN_EARN))
			.thenReturn(Optional.of(existing));

		pointService.loginPoint(1L);

		verify(userRepository, never()).findById(any());
	}

	@Test
	@DisplayName("loginPoint - 오늘 적립한 이력이 없으면 적립")
	void loginPoint_earn() {
		Long loginSourceTypeId = PointSourceTypeEnum.LOGIN_EARN.getId(); // 정확한 ID 사용

		when(
			pointHistoryRepository.findTopByUserIdAndSourceTypeOrderByCreatedAtDesc(1L, PointSourceTypeEnum.LOGIN_EARN))
			.thenReturn(Optional.empty());

		PointSourceType sourceType = PointSourceType.builder()
			.actionType(ActionType.EARN)
			.sourceType("로그인")
			.build();

		PointPolicy policy = PointPolicy.builder()
			.pointSourceType(sourceType)
			.policyType(PolicyType.AMOUNT)
			.value(BigDecimal.valueOf(100))
			.build();
		policy.changeIsActive(true);

		when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
		when(pointSourceTypeRepository.findById(loginSourceTypeId)).thenReturn(Optional.of(sourceType));
		when(pointPolicyRepository.findByPointSourceType(sourceType)).thenReturn(Optional.of(policy));
		when(userRepository.save(any())).thenReturn(testUser);
		when(pointHistoryRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

		pointService.loginPoint(1L);

		assertThat(testUser.getPointBalance()).isEqualByComparingTo("100");
		verify(pointHistoryRepository).save(any(PointHistory.class));
	}

	@Test
	@DisplayName("signUpPoint - 회원가입 시 포인트 적립")
	void signUpPoint_success() {
		PointSourceType sourceType = PointSourceType.builder()
			.actionType(ActionType.EARN)
			.sourceType("회원가입")
			.build();

		PointPolicy policy = PointPolicy.builder()
			.pointSourceType(sourceType)
			.policyType(PolicyType.AMOUNT)
			.value(BigDecimal.valueOf(5000))
			.build();
		policy.changeIsActive(true);

		when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
		when(pointSourceTypeRepository.findById(PointSourceTypeEnum.SIGNUP_EARN.getId())).thenReturn(
			Optional.of(sourceType));
		when(pointPolicyRepository.findByPointSourceType(sourceType)).thenReturn(Optional.of(policy));
		when(userRepository.save(any())).thenReturn(testUser);
		when(pointHistoryRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

		pointService.signUpPoint(1L);

		assertThat(testUser.getPointBalance()).isEqualByComparingTo("5000");
		verify(pointHistoryRepository).save(any(PointHistory.class));
	}

}
