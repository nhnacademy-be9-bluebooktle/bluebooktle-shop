package shop.bluebooktle.backend.user.service;

import java.util.List;

import shop.bluebooktle.common.dto.user.request.AddressRequest;
import shop.bluebooktle.common.dto.user.response.AddressResponse;

public interface AddressService {
	List<AddressResponse> findAddressesByUserId(Long userId);

	AddressResponse createAddress(Long userId, AddressRequest request);

	AddressResponse updateAddress(Long userId, Long addressId, AddressRequest request);

	void deleteAddress(Long userId, Long addressId);
}