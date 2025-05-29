package shop.bluebooktle.frontend.service;

import java.util.List;

import shop.bluebooktle.common.dto.user.request.AddressRequest;
import shop.bluebooktle.common.dto.user.response.AddressResponse;

public interface AddressService {

	List<AddressResponse> getAddresses();

	AddressResponse createAddress(AddressRequest request);

	AddressResponse updateAddress(Long addressId, AddressRequest request);

	void deleteAddress(Long addressId);
}