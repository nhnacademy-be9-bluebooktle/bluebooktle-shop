package shop.bluebooktle.frontend.controller.myPage;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.anyLong;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import shop.bluebooktle.common.dto.user.request.AddressRequest;
import shop.bluebooktle.common.dto.user.response.AddressResponse;
import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;
import shop.bluebooktle.frontend.service.AddressService;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class AddressControllerTest {

	@InjectMocks
	private AddressController addressController;

	@Mock
	private AddressService addressService;

	@Mock
	private Model model;

	private RedirectAttributes redirectAttributes;

	@BeforeEach
	void setUp() {
		// RedirectAttributes의 실제 구현체를 사용하여 플래시 속성을 검증합니다.
		redirectAttributes = new RedirectAttributesModelMap();
	}

	@Test
	@DisplayName("userAddressesPage: 주소 목록 로드 성공")
	void userAddressesPage_success() {
		// Given
		List<AddressResponse> mockAddresses = Arrays.asList(
			AddressResponse.builder()
				.addressId(1L)
				.alias("집")
				.roadAddress("서울시 강남구")
				.detailAddress("101호")
				.postalCode("06130")
				.build(),
			AddressResponse.builder()
				.addressId(2L)
				.alias("회사")
				.roadAddress("대전광역시 유성구")
				.detailAddress("연구동")
				.postalCode("34139")
				.build()
		);
		when(addressService.getAddresses()).thenReturn(mockAddresses);
		when(model.containsAttribute("addressRequest")).thenReturn(false); // addressRequest가 모델에 없다고 가정

		// When
		String viewName = addressController.userAddressesPage(model, redirectAttributes);

		// Then
		assertThat(viewName).isEqualTo("mypage/address_list");
		verify(model).addAttribute("addresses", mockAddresses);
		verify(model).addAttribute(eq("addressRequest"), any(AddressRequest.class)); // addressRequest가 추가되었는지 확인
		assertThat(redirectAttributes.getFlashAttributes()).isEmpty(); // 성공 시 플래시 속성이 없어야 함
	}

	@Test
	@DisplayName("userAddressesPage: 주소 목록이 비어있는 경우에도 성공")
	void userAddressesPage_success_emptyAddresses() {
		// Given
		when(addressService.getAddresses()).thenReturn(Collections.emptyList());
		when(model.containsAttribute("addressRequest")).thenReturn(true); // addressRequest가 이미 모델에 있다고 가정

		// When
		String viewName = addressController.userAddressesPage(model, redirectAttributes);

		// Then
		assertThat(viewName).isEqualTo("mypage/address_list");
		verify(model).addAttribute("addresses", Collections.emptyList());
		verify(model, never()).addAttribute(eq("addressRequest"), any(AddressRequest.class)); // 이미 있으므로 추가되지 않아야 함
		assertThat(redirectAttributes.getFlashAttributes()).isEmpty();
	}

	@Test
	@DisplayName("userAddressesPage: ApplicationException 발생 시 리다이렉트 및 에러 메시지")
	void userAddressesPage_applicationException() {
		// Given
		String errorMessage = "삭제할 주소를 찾을 수 없거나 권한이 없습니다.";
		when(addressService.getAddresses()).thenThrow(new ApplicationException(ErrorCode.AUTH_ADDRESS_NOT_FOUND));

		// When
		String viewName = addressController.userAddressesPage(model, redirectAttributes);

		// Then
		assertThat(viewName).isEqualTo("redirect:/mypage/profile");
		assertThat(redirectAttributes.getFlashAttributes()).containsKey("globalErrorMessage");
		assertThat(redirectAttributes.getFlashAttributes().get("globalErrorMessage")).isEqualTo(errorMessage);
		verify(model, never()).addAttribute(anyString(), any()); // 모델에 데이터가 추가되지 않아야 함
	}

	@Test
	@DisplayName("userAddressesPage: 예상치 못한 Exception 발생 시 리다이렉트 및 일반 에러 메시지")
	void userAddressesPage_unexpectedException() {
		// Given
		when(addressService.getAddresses()).thenThrow(new RuntimeException("데이터베이스 연결 오류"));

		// When
		String viewName = addressController.userAddressesPage(model, redirectAttributes);

		// Then
		assertThat(viewName).isEqualTo("redirect:/mypage/profile");
		assertThat(redirectAttributes.getFlashAttributes()).containsKey("globalErrorMessage");
		assertThat(redirectAttributes.getFlashAttributes().get("globalErrorMessage")).isEqualTo(
			"주소 정보를 불러오는 중 알 수 없는 오류가 발생했습니다.");
		verify(model, never()).addAttribute(anyString(), any());
	}

	@Test
	@DisplayName("saveAddress: 새 주소 생성 성공")
	void saveAddress_createSuccess() {
		// Given
		AddressRequest request = AddressRequest.builder()
			.alias("새로운 집")
			.roadAddress("새로운 도로명")
			.detailAddress("새로운 상세주소")
			.postalCode("00000")
			.build();
		BindingResult bindingResult = mock(BindingResult.class);
		when(bindingResult.hasErrors()).thenReturn(false);

		// When
		String viewName = addressController.saveAddress(request, bindingResult, null, redirectAttributes);

		// Then
		assertThat(viewName).isEqualTo("redirect:/mypage/addresses");
		verify(addressService).createAddress(request); // createAddress가 호출되었는지 확인
		verify(addressService, never()).updateAddress(anyLong(), any(AddressRequest.class)); // update는 호출되지 않아야 함
		assertThat(redirectAttributes.getFlashAttributes()).containsKey("globalSuccessMessage");
		assertThat(redirectAttributes.getFlashAttributes().get("globalSuccessMessage")).isEqualTo(
			"새 주소가 성공적으로 등록되었습니다.");
	}

	@Test
	@DisplayName("saveAddress: 기존 주소 수정 성공")
	void saveAddress_updateSuccess() {
		// Given
		Long existingAddressId = 1L;
		AddressRequest request = AddressRequest.builder()
			.alias("수정된 집")
			.roadAddress("수정된 도로명")
			.detailAddress("수정된 상세주소")
			.postalCode("11111")
			.build();
		BindingResult bindingResult = mock(BindingResult.class);
		when(bindingResult.hasErrors()).thenReturn(false);

		// When
		String viewName = addressController.saveAddress(request, bindingResult, existingAddressId, redirectAttributes);

		// Then
		assertThat(viewName).isEqualTo("redirect:/mypage/addresses");
		verify(addressService).updateAddress(existingAddressId, request); // updateAddress가 호출되었는지 확인
		verify(addressService, never()).createAddress(any(AddressRequest.class)); // create는 호출되지 않아야 함
		assertThat(redirectAttributes.getFlashAttributes()).containsKey("globalSuccessMessage");
		assertThat(redirectAttributes.getFlashAttributes().get("globalSuccessMessage")).isEqualTo("주소가 성공적으로 수정되었습니다.");
	}

	@Test
	@DisplayName("saveAddress: 유효성 검사 실패 시 리다이렉트 및 에러 정보 전달")
	void saveAddress_validationFailure() {
		// Given
		AddressRequest invalidRequest = AddressRequest.builder().alias("").build(); // 별칭이 비어있어 유효성 검사 실패
		BindingResult bindingResult = mock(BindingResult.class);
		when(bindingResult.hasErrors()).thenReturn(true);
		// BindingResult에 필드 에러 추가 (실제 상황을 모방)
		when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(
			new FieldError("addressRequest", "alias", "별칭은 필수입니다.")
		));

		// When
		String viewName = addressController.saveAddress(invalidRequest, bindingResult, null, redirectAttributes);

		// Then
		assertThat(viewName).isEqualTo("redirect:/mypage/addresses");
		assertThat(redirectAttributes.getFlashAttributes()).containsKey(
			"org.springframework.validation.BindingResult.addressRequest");
		assertThat(redirectAttributes.getFlashAttributes().get("addressRequest")).isEqualTo(invalidRequest);
		assertThat(redirectAttributes.getFlashAttributes()).containsKey("openModal");
		assertThat(redirectAttributes.getFlashAttributes().get("openModal")).isEqualTo("addressFormModal");
		verify(addressService, never()).createAddress(any(AddressRequest.class)); // 서비스는 호출되지 않아야 함
		verify(addressService, never()).updateAddress(anyLong(), any(AddressRequest.class));
	}

	@Test
	@DisplayName("saveAddress: 유효성 검사 실패 시 수정 모드일 때 addressIdForModal 유지")
	void saveAddress_validationFailure_withAddressId() {
		// Given
		Long addressIdForEdit = 5L;
		AddressRequest invalidRequest = AddressRequest.builder().alias("").build();
		BindingResult bindingResult = mock(BindingResult.class);
		when(bindingResult.hasErrors()).thenReturn(true);
		when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(
			new FieldError("addressRequest", "alias", "별칭은 필수입니다.")
		));

		// When
		String viewName = addressController.saveAddress(invalidRequest, bindingResult, addressIdForEdit,
			redirectAttributes);

		// Then
		assertThat(viewName).isEqualTo("redirect:/mypage/addresses");
		assertThat(redirectAttributes.getFlashAttributes()).containsKey("addressIdForModal");
		assertThat(redirectAttributes.getFlashAttributes().get("addressIdForModal")).isEqualTo(addressIdForEdit);
		assertThat(redirectAttributes.getFlashAttributes()).containsKey("openModal");
	}

	@Test
	@DisplayName("saveAddress: 저장 중 ApplicationException 발생 시 에러 메시지 및 폼 유지")
	void saveAddress_applicationException_onSave() {
		// Given
		AddressRequest request = AddressRequest.builder()
			.alias("중복 별칭")
			.roadAddress("도로")
			.detailAddress("상세")
			.postalCode("12345")
			.build();
		BindingResult bindingResult = mock(BindingResult.class);
		when(bindingResult.hasErrors()).thenReturn(false);
		String errorMessage = "유효하지 않은 주소 정보입니다.";
		doThrow(new ApplicationException(ErrorCode.AUTH_INVALID_ADDRESS)).when(addressService).createAddress(request);

		// When
		String viewName = addressController.saveAddress(request, bindingResult, null, redirectAttributes);

		// Then
		assertThat(viewName).isEqualTo("redirect:/mypage/addresses");
		assertThat(redirectAttributes.getFlashAttributes()).containsKey("globalErrorMessage");
		assertThat(redirectAttributes.getFlashAttributes().get("globalErrorMessage")).isEqualTo(errorMessage);
		assertThat(redirectAttributes.getFlashAttributes().get("addressRequest")).isEqualTo(request);
		assertThat(redirectAttributes.getFlashAttributes()).containsKey("openModal");
	}

	@Test
	@DisplayName("saveAddress: 수정 중 에러메세지")
	void saveAddress_applicationException_onUpdate() {
		// Given
		Long addressId = 1L;
		AddressRequest request = AddressRequest.builder()
			.alias("존재하지 않는 ID")
			.roadAddress("도로")
			.detailAddress("상세")
			.postalCode("12345")
			.build();
		BindingResult bindingResult = mock(BindingResult.class);
		when(bindingResult.hasErrors()).thenReturn(false);
		String errorMessage = "삭제할 주소를 찾을 수 없거나 권한이 없습니다.";
		doThrow(new ApplicationException(ErrorCode.AUTH_ADDRESS_NOT_FOUND)).when(addressService)
			.updateAddress(addressId, request);

		// When
		String viewName = addressController.saveAddress(request, bindingResult, addressId, redirectAttributes);

		// Then
		assertThat(viewName).isEqualTo("redirect:/mypage/addresses");
		assertThat(redirectAttributes.getFlashAttributes()).containsKey("globalErrorMessage");
		assertThat(redirectAttributes.getFlashAttributes().get("globalErrorMessage")).isEqualTo(errorMessage);
		assertThat(redirectAttributes.getFlashAttributes().get("addressRequest")).isEqualTo(request);
		assertThat(redirectAttributes.getFlashAttributes().get("addressIdForModal")).isEqualTo(addressId);
		assertThat(redirectAttributes.getFlashAttributes()).containsKey("openModal");
	}

	@Test
	@DisplayName("saveAddress: 예상치 못한 Exception 발생 시 일반 에러 메시지 및 폼 유지")
	void saveAddress_unexpectedException() {
		// Given
		AddressRequest request = AddressRequest.builder()
			.alias("예외 발생")
			.roadAddress("도로")
			.detailAddress("상세")
			.postalCode("12345")
			.build();
		BindingResult bindingResult = mock(BindingResult.class);
		when(bindingResult.hasErrors()).thenReturn(false);
		doThrow(new RuntimeException("알 수 없는 DB 오류")).when(addressService).createAddress(request);

		// When
		String viewName = addressController.saveAddress(request, bindingResult, null, redirectAttributes);

		// Then
		assertThat(viewName).isEqualTo("redirect:/mypage/addresses");
		assertThat(redirectAttributes.getFlashAttributes()).containsKey("globalErrorMessage");
		assertThat(redirectAttributes.getFlashAttributes().get("globalErrorMessage")).isEqualTo(
			"주소 처리 중 알 수 없는 오류가 발생했습니다.");
		assertThat(redirectAttributes.getFlashAttributes().get("addressRequest")).isEqualTo(request);
		assertThat(redirectAttributes.getFlashAttributes()).containsKey("openModal");
	}

	@Test
	@DisplayName("deleteAddress: 주소 삭제 성공")
	void deleteAddress_success() {
		// Given
		Long addressIdToDelete = 1L;
		doNothing().when(addressService).deleteAddress(addressIdToDelete);

		// When
		String viewName = addressController.deleteAddress(addressIdToDelete, redirectAttributes);

		// Then
		assertThat(viewName).isEqualTo("redirect:/mypage/addresses");
		verify(addressService).deleteAddress(addressIdToDelete);
		assertThat(redirectAttributes.getFlashAttributes()).containsKey("globalSuccessMessage");
		assertThat(redirectAttributes.getFlashAttributes().get("globalSuccessMessage")).isEqualTo("주소가 삭제되었습니다.");
	}

	@Test
	@DisplayName("deleteAddress: ApplicationException 발생 시 에러 메시지")
	void deleteAddress_applicationException() {
		// Given
		Long addressIdToDelete = 1L;
		String errorMessage = "삭제할 주소를 찾을 수 없거나 권한이 없습니다.";
		doThrow(new ApplicationException(ErrorCode.AUTH_ADDRESS_NOT_FOUND)).when(addressService)
			.deleteAddress(addressIdToDelete);

		// When
		String viewName = addressController.deleteAddress(addressIdToDelete, redirectAttributes);

		// Then
		assertThat(viewName).isEqualTo("redirect:/mypage/addresses");
		assertThat(redirectAttributes.getFlashAttributes()).containsKey("globalErrorMessage");
		assertThat(redirectAttributes.getFlashAttributes().get("globalErrorMessage")).isEqualTo(errorMessage);
	}

	@Test
	@DisplayName("deleteAddress: 예상치 못한 Exception 발생 시 일반 에러 메시지")
	void deleteAddress_unexpectedException() {
		// Given
		Long addressIdToDelete = 1L;
		doThrow(new RuntimeException("알 수 없는 시스템 오류")).when(addressService).deleteAddress(addressIdToDelete);

		// When
		String viewName = addressController.deleteAddress(addressIdToDelete, redirectAttributes);

		// Then
		assertThat(viewName).isEqualTo("redirect:/mypage/addresses");
		assertThat(redirectAttributes.getFlashAttributes()).containsKey("globalErrorMessage");
		assertThat(redirectAttributes.getFlashAttributes().get("globalErrorMessage")).isEqualTo(
			"주소 삭제 중 알 수 없는 오류가 발생했습니다.");
	}
}