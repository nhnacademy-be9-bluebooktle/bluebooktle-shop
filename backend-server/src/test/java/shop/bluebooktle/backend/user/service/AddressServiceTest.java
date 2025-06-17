package shop.bluebooktle.backend.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import shop.bluebooktle.backend.user.repository.AddressRepository;
import shop.bluebooktle.backend.user.repository.UserRepository;
import shop.bluebooktle.backend.user.service.impl.AddressServiceImpl;
import shop.bluebooktle.common.dto.user.request.AddressRequest;
import shop.bluebooktle.common.dto.user.response.AddressResponse;
import shop.bluebooktle.common.entity.auth.Address;
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.exception.auth.UserNotFoundException;
import shop.bluebooktle.common.exception.user.AddressLimitExceededException;
import shop.bluebooktle.common.exception.user.AddressNotFoundException;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private AddressRepository addressRepository;

	@InjectMocks
	private AddressServiceImpl addressService;

	private static final int MAX_ADDRESS_COUNT = 10;

	@Test
	@DisplayName("사용자 아이디로 주소 조회 성공")
	void findAddressesByUserId_success() {

		Long userId = 1L;

		Address address1 = Address.builder()
			.alias("address1")
			.roadAddress("roadAddress1")
			.detailAddress("detailAddress1")
			.postalCode("postalCode1")
			.build();

		Address address2 = Address.builder()
			.alias("address2")
			.roadAddress("roadAddress2")
			.detailAddress("detailAddress2")
			.postalCode("postalCode2")
			.build();

		List<Address> addresses = List.of(address1, address2);

		when(userRepository.existsById(userId)).thenReturn(true);
		when(addressRepository.findByUser_Id(userId)).thenReturn(addresses);

		List<AddressResponse> result = addressService.findAddressesByUserId(userId);

		for (int i = 0; i < result.size(); i++) {
			Address address = addresses.get(i);
			AddressResponse addressResponse = result.get(i);

			assertEquals(address.getAlias(), addressResponse.getAlias());
			assertEquals(address.getRoadAddress(), addressResponse.getRoadAddress());
			assertEquals(address.getDetailAddress(), addressResponse.getDetailAddress());
			assertEquals(address.getPostalCode(), addressResponse.getPostalCode());
		}
		verify(userRepository).existsById(userId);
		verify(addressRepository).findByUser_Id(userId);
	}

	@Test
	@DisplayName("사용자 아이디로 주소 조회 실패 - 유효한 사용자 아이디가 아닌 경우")
	void findAddressesByUserId_fail_invalidUserId() {

		when(userRepository.existsById(anyLong())).thenReturn(false);
		assertThrows(UserNotFoundException.class, () -> {
			addressService.findAddressesByUserId(999L);
		});

		verify(userRepository).existsById(anyLong());
		verify(addressRepository, never()).findByUser_Id(anyLong());
	}

	@Test
	@DisplayName("주소 생성 성공")
	void createAddress_success() {

		Long userId = 1L;

		User user = User.builder()
			.id(userId)
			.build();

		AddressRequest addressRequest = AddressRequest.builder()
			.alias("alias")
			.roadAddress("roadAddress")
			.detailAddress("detailAddress")
			.postalCode("postalCode")
			.build();

		Address address = addressRequest.toEntity();

		when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
		when(addressRepository.countByUser_Id(userId)).thenReturn(0L);
		when(addressRepository.save(address)).thenReturn(address);

		AddressResponse addressResponse = addressService.createAddress(userId, addressRequest);

		verify(userRepository).findById(userId);
		verify(addressRepository).countByUser_Id(userId);
		verify(addressRepository).save(address);

		assertNotNull(addressResponse);
		assertEquals(address.getAlias(), addressResponse.getAlias());
		assertEquals(address.getRoadAddress(), addressResponse.getRoadAddress());
		assertEquals(address.getDetailAddress(), addressResponse.getDetailAddress());
		assertEquals(address.getPostalCode(), addressResponse.getPostalCode());
	}

	@Test
	@DisplayName("주소 생성 실패 - 사용자 아이디가 유효하지 않은 경우")
	void createAddress_fail_invalidUserId() {

		Long userId = 1L;

		when(userRepository.findById(userId)).thenReturn(java.util.Optional.empty());

		assertThrows(UserNotFoundException.class, () -> {
			addressService.createAddress(userId, AddressRequest.builder().build());
		});
	}

	@Test
	@DisplayName("주소 생성 실패 - 이미 최대치의 주소를 갖고 있는 경우")
	void createAddress_fail_maxAddressCount() {

		Long userId = 1L;

		User user = User.builder()
			.id(userId)
			.build();

		when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
		when(addressRepository.countByUser_Id(userId)).thenReturn(10L);

		assertThrows(AddressLimitExceededException.class, () -> {
			addressService.createAddress(userId, AddressRequest.builder().build());
		});
	}

	@Test
	@DisplayName("주소 수정 성공")
	void updateAddress_success() {

		Long userId = 1L;

		Long addressId = 1L;

		Address address = Address.builder()
			.alias("alias")
			.roadAddress("roadAddress")
			.detailAddress("detailAddress")
			.postalCode("postalCode")
			.build();

		AddressRequest addressRequest = AddressRequest.builder()
			.alias("alias_updated")
			.roadAddress("roadAddress_updated")
			.detailAddress("detailAddress_updated")
			.postalCode("postalCode_updated")
			.build();

		when(addressRepository.findByIdAndUser_Id(addressId, userId)).thenReturn(Optional.of(address));
		when(addressRepository.save(address)).thenReturn(address);

		AddressResponse addressResponse = addressService.updateAddress(userId, addressId, addressRequest);

		verify(addressRepository).findByIdAndUser_Id(addressId, userId);
		verify(addressRepository).save(address);

		assertNotNull(addressResponse);
		assertEquals(addressRequest.getAlias(), addressResponse.getAlias());
		assertEquals(addressRequest.getRoadAddress(), addressResponse.getRoadAddress());
		assertEquals(addressRequest.getDetailAddress(), addressResponse.getDetailAddress());
		assertEquals(addressRequest.getPostalCode(), addressResponse.getPostalCode());
	}

	@Test
	@DisplayName("주소 수정 실패 - 주소 아이디가 유효하지 않거나 권한이 없는 경우")
	void updateAddress_fail_invalidId() {
		Long userId = 1L;
		Long addressId = 1L;

		when(addressRepository.findByIdAndUser_Id(addressId, userId)).thenReturn(Optional.empty());

		assertThrows(AddressNotFoundException.class, () -> {
			addressService.updateAddress(userId, addressId, AddressRequest.builder().build());
		});

		verify(addressRepository, never()).save(any(Address.class));
	}

	@Test
	@DisplayName("주소 삭제 성공")
	void deleteAddress_success() {
		Long userId = 1L;
		Long addressId = 1L;

		Address address = Address.builder()
			.user(User.builder().id(userId).build())
			.build();
		ReflectionTestUtils.setField(address, "id", addressId);

		when(addressRepository.findByIdAndUser_Id(addressId, userId)).thenReturn(Optional.of(address));

		addressService.deleteAddress(userId, addressId);

		verify(addressRepository).delete(address);
	}

	@Test
	@DisplayName("주소 삭제 실패 - 주소 아이디가 유효하지 않거나 권한이 없는 경우")
	void deleteAddress_fail_invalidId() {
		Long userId = 1L;
		Long addressId = 1L;

		when(addressRepository.findByIdAndUser_Id(addressId, userId)).thenReturn(Optional.empty());

		assertThrows(AddressNotFoundException.class, () -> {
			addressService.deleteAddress(userId, addressId);
		});

		verify(addressRepository, never()).delete(any(Address.class));
	}


}
