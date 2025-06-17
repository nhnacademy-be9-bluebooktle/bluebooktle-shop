package shop.bluebooktle.backend.point.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import shop.bluebooktle.backend.payment.entity.Payment;
import shop.bluebooktle.backend.payment.repository.PaymentRepository;
import shop.bluebooktle.backend.point.entity.PaymentPointHistory;
import shop.bluebooktle.backend.point.repository.PaymentPointHistoryRepository;
import shop.bluebooktle.backend.point.repository.PointHistoryRepository;
import shop.bluebooktle.backend.point.service.impl.PaymentPointHistoryServiceImpl;
import shop.bluebooktle.common.entity.point.PointHistory;
import shop.bluebooktle.common.exception.payment.PaymentNotFoundException;
import shop.bluebooktle.common.exception.point.PointHistoryNotFoundException;

@ExtendWith(MockitoExtension.class)
class PaymentPointHistoryServiceTest {

	@InjectMocks
	private PaymentPointHistoryServiceImpl service;

	@Mock
	private PaymentPointHistoryRepository paymentPointHistoryRepository;

	@Mock
	private PaymentRepository paymentRepository;

	@Mock
	private PointHistoryRepository pointHistoryRepository;

	@Test
	@DisplayName("결제-포인트 이력 저장 - 성공")
	void save_success() {
		Long paymentId = 1L;
		Long pointHistoryId = 100L;

		Payment payment = Payment.builder().build();
		PointHistory pointHistory = PointHistory.builder().build();

		given(paymentRepository.findById(paymentId)).willReturn(Optional.of(payment));
		given(pointHistoryRepository.findById(pointHistoryId)).willReturn(Optional.of(pointHistory));

		service.save(paymentId, pointHistoryId);

		then(paymentPointHistoryRepository).should().save(any(PaymentPointHistory.class));
	}

	@Test
	@DisplayName("결제-포인트 이력 저장 실패 - 결제 정보 없음")
	void save_paymentNotFound_fail() {
		Long paymentId = 1L;
		Long pointHistoryId = 100L;

		given(paymentRepository.findById(paymentId)).willReturn(Optional.empty());

		assertThatThrownBy(() -> service.save(paymentId, pointHistoryId))
			.isInstanceOf(PaymentNotFoundException.class);

		verify(pointHistoryRepository, never()).save(any(PointHistory.class));
		verify(paymentPointHistoryRepository, never()).save(any(PaymentPointHistory.class));

	}

	@Test
	@DisplayName("결제-포인트 이력 저장 실패 - 포인트 이력 없음")
	void save_pointHistoryNotFound_fail() {
		Long paymentId = 1L;
		Long pointHistoryId = 100L;

		Payment payment = Payment.builder().build();

		given(paymentRepository.findById(paymentId)).willReturn(Optional.of(payment));
		given(pointHistoryRepository.findById(pointHistoryId)).willReturn(Optional.empty());

		assertThrows(PointHistoryNotFoundException.class, () -> service.save(paymentId, pointHistoryId));

		verify(paymentPointHistoryRepository, never()).save(any(PaymentPointHistory.class));
	}
}