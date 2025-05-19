package shop.bluebooktle.backend.payment.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import shop.bluebooktle.backend.payment.dto.request.PaymentTypeRequest;
import shop.bluebooktle.backend.payment.dto.response.PaymentTypeResponse;
import shop.bluebooktle.backend.payment.entity.PaymentType;
import shop.bluebooktle.backend.payment.repository.PaymentTypeRepository;
import shop.bluebooktle.backend.payment.service.impl.PaymentTypeServiceImpl;
import shop.bluebooktle.common.exception.payment.PaymentTypeAlreadyExistException;
import shop.bluebooktle.common.exception.payment.PaymentTypeNotFoundException;

@ExtendWith(MockitoExtension.class)
class PaymentTypeServiceImplTest {

	@InjectMocks
	private PaymentTypeServiceImpl service;

	@Mock
	private PaymentTypeRepository repository;

	@Test
	@DisplayName("create(): 새 결제수단 정상 등록")
	void create_success() {
		// given
		PaymentTypeRequest req = new PaymentTypeRequest("CARD");
		given(repository.existsByMethod("CARD")).willReturn(false);

		// when
		service.create(req);

		// then
		ArgumentCaptor<PaymentType> captor = ArgumentCaptor.forClass(PaymentType.class);
		verify(repository).save(captor.capture());
		assertThat(captor.getValue().getMethod()).isEqualTo("CARD");
	}

	@Test
	@DisplayName("create(): 중복이면 PaymentTypeAlreadyExistException")
	void create_duplicate() {
		PaymentTypeRequest req = new PaymentTypeRequest("CARD");
		given(repository.existsByMethod("CARD")).willReturn(true);

		assertThatThrownBy(() -> service.create(req))
			.isInstanceOf(PaymentTypeAlreadyExistException.class);

		verify(repository, never()).save(any());
	}

	@Test
	@DisplayName("update(): method 변경 & 중복 없음 → 성공")
	void update_success_methodChanged() {
		// given
		PaymentType pt = mock(PaymentType.class);
		given(pt.getMethod()).willReturn("CARD");

		PaymentTypeRequest newReq = new PaymentTypeRequest("TOSS_PAY");
		given(repository.findById(1L)).willReturn(Optional.of(pt));
		given(repository.existsByMethod("TOSS_PAY")).willReturn(false);

		// when
		service.update(1L, newReq);

		// then
		verify(pt).changeMethod("TOSS_PAY");
		verify(repository).save(pt);
	}

	@Test
	@DisplayName("update(): ID 없으면 PaymentTypeNotFoundException")
	void update_notFound() {
		given(repository.findById(999L)).willReturn(Optional.empty());

		assertThatThrownBy(() -> service.update(999L, new PaymentTypeRequest("CARD")))
			.isInstanceOf(PaymentTypeNotFoundException.class);
	}

	@Test
	@DisplayName("update(): method 중복이면 PaymentTypeAlreadyExistException")
	void update_duplicateMethod() {
		PaymentType pt = mock(PaymentType.class);
		given(pt.getMethod()).willReturn("CARD");

		given(repository.findById(1L)).willReturn(Optional.of(pt));
		given(repository.existsByMethod("TOSS_PAY")).willReturn(true);

		assertThatThrownBy(() -> service.update(1L, new PaymentTypeRequest("TOSS_PAY")))
			.isInstanceOf(PaymentTypeAlreadyExistException.class);

		verify(pt, never()).changeMethod(any());
		verify(repository, never()).save(any());
	}

	@Test
	@DisplayName("delete(): 정상 삭제")
	void delete_success() {
		PaymentType pt = mock(PaymentType.class);
		given(repository.findById(3L)).willReturn(Optional.of(pt));

		service.delete(3L);

		verify(repository).delete(pt);
	}

	@Test
	@DisplayName("delete(): ID 없으면 PaymentTypeNotFoundException")
	void delete_notFound() {
		given(repository.findById(404L)).willReturn(Optional.empty());

		assertThatThrownBy(() -> service.delete(404L))
			.isInstanceOf(PaymentTypeNotFoundException.class);
	}

	@Test
	@DisplayName("get(): method로 조회 성공")
	void get_success() {
		PaymentType pt = mock(PaymentType.class);
		given(repository.findByMethod("CARD")).willReturn(Optional.of(pt));

		PaymentTypeResponse resp = service.get(new PaymentTypeRequest("CARD"));

		assertThat(resp).isNotNull();
		verify(repository).findByMethod("CARD");
	}

	@Test
	@DisplayName("get(): method 없으면 PaymentTypeNotFoundException")
	void get_notFound() {
		given(repository.findByMethod("KAKAO_PAY")).willReturn(Optional.empty());

		assertThatThrownBy(() -> service.get(new PaymentTypeRequest("KAKAO_PAY")))
			.isInstanceOf(PaymentTypeNotFoundException.class);
	}

	@Test
	@DisplayName("getAll(): 페이지 조회 성공")
	void getAll_success() {
		Page<PaymentType> page = new PageImpl<>(List.of(mock(PaymentType.class)));
		given(repository.findAll(PageRequest.of(0, 5))).willReturn(page);

		Page<PaymentTypeResponse> result = service.getAll(PageRequest.of(0, 5));

		assertThat(result.getTotalElements()).isEqualTo(1);
		verify(repository).findAll(PageRequest.of(0, 5));
	}
}
