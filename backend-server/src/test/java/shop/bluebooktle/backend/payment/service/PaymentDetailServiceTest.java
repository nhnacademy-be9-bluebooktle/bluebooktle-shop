// package shop.bluebooktle.backend.payment.service;
//
// import static org.assertj.core.api.AssertionsForClassTypes.*;
// import static org.mockito.BDDMockito.*;
//
// import java.util.Optional;
//
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
//
// import shop.bluebooktle.backend.payment.dto.request.PaymentDetailRequest;
// import shop.bluebooktle.backend.payment.entity.Payment;
// import shop.bluebooktle.backend.payment.entity.PaymentDetail;
// import shop.bluebooktle.backend.payment.entity.PaymentType;
// import shop.bluebooktle.backend.payment.repository.PaymentDetailRepository;
// import shop.bluebooktle.backend.payment.repository.PaymentRepository;
// import shop.bluebooktle.backend.payment.repository.PaymentTypeRepository;
// import shop.bluebooktle.backend.payment.service.impl.PaymentDetailServiceImpl;
// import shop.bluebooktle.common.domain.payment.PaymentStatus;
// import shop.bluebooktle.common.exception.payment.PaymentDetailNotFoundException;
// import shop.bluebooktle.common.exception.payment.PaymentTypeNotFoundException;
//
// @ExtendWith(MockitoExtension.class)
// public class PaymentDetailServiceTest {
//
// 	@InjectMocks
// 	private PaymentDetailServiceImpl service;
//
// 	@Mock
// 	private PaymentTypeRepository typeRepo;
//
// 	@Mock
// 	private PaymentRepository paymentRepo;
//
// 	@Mock
// 	private PaymentDetailRepository detailRepo;
//
// 	@Test
// 	@DisplayName("create(): 결제 상세 정상 생성")
// 	void create_success() {
// 		// given
// 		Long paymentId = 100L;
// 		Long typeId = 200L;
// 		Payment payment = new Payment();
// 		payment.setId(paymentId);
// 		PaymentType type = new PaymentType();
// 		type.setId(typeId);
//
// 		PaymentDetailRequest req = new PaymentDetailRequest(
// 			paymentId,
// 			typeId,
// 			"key-123",
// 			PaymentStatus.READY
// 		);
//
// 		given(typeRepo.findById(typeId)).willReturn(Optional.of(type));
// 		given(paymentRepo.findById(paymentId)).willReturn(Optional.of(payment));
//
// 		// when
// 		service.create(req);
//
// 		// then
// 		verify(typeRepo).findById(typeId);
// 		verify(paymentRepo).findById(paymentId);
// 		verify(detailRepo).save(any(PaymentDetail.class));
// 	}
//
// 	@Test
// 	@DisplayName("create(): 결제 타입 없으면 PaymentTypeNotFoundException")
// 	void create_typeNotFound() {
// 		Long paymentId = 1L;
// 		Long typeId = 2L;
// 		PaymentDetailRequest req = new PaymentDetailRequest(paymentId, typeId, "k", PaymentStatus.READY);
//
// 		given(typeRepo.findById(typeId)).willReturn(Optional.empty());
//
// 		assertThatThrownBy(() -> service.create(req))
// 			.isInstanceOf(PaymentTypeNotFoundException.class);
//
// 		then(paymentRepo).shouldHaveNoInteractions();
// 		then(detailRepo).shouldHaveNoInteractions();
// 	}
//
// 	@Test
// 	@DisplayName("create(): 결제 정보 없으면 PaymentDetailNotFoundException")
// 	void create_paymentNotFound() {
// 		Long paymentId = 10L;
// 		Long typeId = 20L;
// 		PaymentType type = new PaymentType();
// 		type.setId(typeId);
// 		PaymentDetailRequest req = new PaymentDetailRequest(paymentId, typeId, "k", PaymentStatus.READY);
//
// 		given(typeRepo.findById(typeId)).willReturn(Optional.of(type));
// 		given(paymentRepo.findById(paymentId)).willReturn(Optional.empty());
//
// 		assertThatThrownBy(() -> service.create(req))
// 			.isInstanceOf(PaymentDetailNotFoundException.class);
//
// 		verify(typeRepo).findById(typeId);
// 		verify(paymentRepo).findById(paymentId);
// 		then(detailRepo).shouldHaveNoInteractions();
// 	}
//
// 	@Test
// 	@DisplayName("delete(): 결제 상세 정상 삭제")
// 	void delete_success() {
// 		Long detailId = 50L;
// 		PaymentDetail detail = new PaymentDetail();
// 		detail.setId(detailId);
//
// 		given(detailRepo.findById(detailId)).willReturn(Optional.of(detail));
//
// 		service.delete(detailId);
//
// 		verify(detailRepo).findById(detailId);
// 		verify(detailRepo).delete(detail);
// 	}
//
// 	@Test
// 	@DisplayName("delete(): 정보 없으면 PaymentDetailNotFoundException")
// 	void delete_notFound() {
// 		given(detailRepo.findById(999L)).willReturn(Optional.empty());
//
// 		assertThatThrownBy(() -> service.delete(999L))
// 			.isInstanceOf(PaymentDetailNotFoundException.class);
//
// 		verify(detailRepo).findById(999L);
// 	}
//
// 	@Test
// 	@DisplayName("get(): 결제 상세 정상 조회")
// 	void get_success() {
// 		Long detailId = 77L;
// 		Payment payment = new Payment();
// 		payment.setId(1L);
// 		PaymentType type = new PaymentType();
// 		type.setId(2L);
// 		PaymentDetail detail = PaymentDetail.builder()
// 			.id(detailId)         // 빌더에 id 세팅이 가능하다면
// 			.payment(payment)
// 			.paymentType(type)
// 			.paymentKey("xyz")
// 			.paymentStatus(PaymentStatus.IN_PROGRESS)
// 			.build();
//
// 		given(detailRepo.findById(detailId)).willReturn(Optional.of(detail));
//
// 		var resp = service.get(detailId);
// 		assertThat(resp).isNotNull();
// 		assertThat(resp.id()).isEqualTo(detailId);
// 		assertThat(resp.paymentKey()).isEqualTo("xyz");
//
// 		verify(detailRepo).findById(detailId);
// 	}
//
// 	@Test
// 	@DisplayName("get(): 정보 없으면 PaymentDetailNotFoundException")
// 	void get_notFound() {
// 		given(detailRepo.findById(1234L)).willReturn(Optional.empty());
//
// 		assertThatThrownBy(() -> service.get(1234L))
// 			.isInstanceOf(PaymentDetailNotFoundException.class);
//
// 		verify(detailRepo).findById(1234L);
// 	}
// }
