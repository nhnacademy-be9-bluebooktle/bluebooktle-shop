package shop.bluebooktle.backend.payment.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import shop.bluebooktle.backend.payment.dto.request.PaymentTypeRequest;
import shop.bluebooktle.backend.payment.entity.PaymentType;
import shop.bluebooktle.backend.payment.repository.PaymentTypeRepository;
import shop.bluebooktle.backend.payment.service.impl.PaymentTypeServiceImpl;
import shop.bluebooktle.common.exception.payment.PaymentTypeAlreadyExistException;
import shop.bluebooktle.common.exception.payment.PaymentTypeNotFoundException;

@ExtendWith(MockitoExtension.class)
class PaymentTypeServiceTest {

	@InjectMocks
	private PaymentTypeServiceImpl paymentTypeService;

	@Mock
	private PaymentTypeRepository paymentTypeRepository;

	@Test
	@DisplayName("결제 수단 생성 성공")
	void create_success() {
		// given
		String newMethod = "NEW_PAYMENT_METHOD";
		PaymentTypeRequest request = new PaymentTypeRequest(newMethod);

		// 해당 이름의 결제 수단이 아직 없다고 설정
		when(paymentTypeRepository.existsByMethod(newMethod)).thenReturn(false);

		// when
		paymentTypeService.create(request);

		// then
		ArgumentCaptor<PaymentType> captor = ArgumentCaptor.forClass(PaymentType.class);
		verify(paymentTypeRepository).save(captor.capture());
		assertEquals(newMethod, captor.getValue().getMethod());
	}

	@Test
	@DisplayName("결제 수단 생성 실패 - 이미 존재하는 경우")
	void create_fail_alreadyExists() {
		// given
		String existingMethod = "EXISTING_PAYMENT_METHOD";
		PaymentTypeRequest request = new PaymentTypeRequest(existingMethod);

		// 해당 이름의 결제 수단이 이미 존재한다고 설정
		when(paymentTypeRepository.existsByMethod(existingMethod)).thenReturn(true);

		// when & then
		assertThrows(PaymentTypeAlreadyExistException.class, () -> {
			paymentTypeService.create(request);
		});

		// 예외가 발생했으므로 save 메소드는 호출되지 않아야 함
		verify(paymentTypeRepository, never()).save(any());
	}

	@Test
	@DisplayName("결제 수단 수정 성공")
	void update_success() {
		// given
		Long existingId = 1L;
		String oldMethod = "OLD_METHOD";
		String newMethod = "NEW_METHOD";
		PaymentTypeRequest request = new PaymentTypeRequest(newMethod);
		PaymentType existingPaymentType = new PaymentType(oldMethod);

		// findById 호출 시 기존 객체를 반환하도록 설정
		when(paymentTypeRepository.findById(existingId)).thenReturn(Optional.of(existingPaymentType));
		// 새로운 이름은 중복되지 않았다고 설정
		when(paymentTypeRepository.existsByMethod(newMethod)).thenReturn(false);

		// when
		paymentTypeService.update(existingId, request);

		// then
		verify(paymentTypeRepository, times(1)).save(existingPaymentType);
		assertEquals(newMethod, existingPaymentType.getMethod());
	}

	@Test
	@DisplayName("결제 수단 수정 실패 - 존재하지 않는 ID")
	void update_fail_notFound() {
		// given
		Long notFoundId = 99L;
		PaymentTypeRequest request = new PaymentTypeRequest("ANY_METHOD");

		// findById 호출 시 비어있는 Optional을 반환하도록 설정
		when(paymentTypeRepository.findById(notFoundId)).thenReturn(Optional.empty());

		// when & then
		assertThrows(PaymentTypeNotFoundException.class, () -> {
			paymentTypeService.update(notFoundId, request);
		});

		// 객체를 찾지 못했으므로 save는 호출되지 않아야 함
		verify(paymentTypeRepository, never()).save(any());
	}

	@Test
	@DisplayName("결제 수단 수정 실패 - 이미 존재하는 이름")
	void update_fail_alreadyExists() {
		// given
		Long existingId = 1L;
		String oldMethod = "OLD_METHOD";
		String newMethod = "ALREADY_EXISTING_METHOD"; // 다른 결제 수단이 이미 사용 중인 이름
		PaymentTypeRequest request = new PaymentTypeRequest(newMethod);
		PaymentType existingPaymentType = new PaymentType(oldMethod);

		// findById 호출 시 기존 객체를 반환하도록 설정
		when(paymentTypeRepository.findById(existingId)).thenReturn(Optional.of(existingPaymentType));
		// 새로운 이름이 이미 존재한다고 설정
		when(paymentTypeRepository.existsByMethod(newMethod)).thenReturn(true);

		// when & then
		assertThrows(PaymentTypeAlreadyExistException.class, () -> {
			paymentTypeService.update(existingId, request);
		});

		// 이름이 중복되어 예외가 발생했으므로 save는 호출되지 않아야 함
		verify(paymentTypeRepository, never()).save(any());
	}
}