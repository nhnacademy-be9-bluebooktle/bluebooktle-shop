package shop.bluebooktle.frontend.repository;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import shop.bluebooktle.common.dto.user.request.AddressRequest;
import shop.bluebooktle.common.dto.user.response.AddressResponse;
import shop.bluebooktle.frontend.config.feign.FeignGlobalConfig;
 
@FeignClient(url = "${server.gateway-url}", name = "userAddressRepository", path = "/api/addresses", configuration = FeignGlobalConfig.class)
public interface UserAddressRepository {

	@GetMapping
	List<AddressResponse> getUserAddresses();

	@PostMapping
	AddressResponse createAddress(@RequestBody AddressRequest request);

	@PutMapping("/{addressId}")
	AddressResponse updateAddress(
		@PathVariable("addressId") Long addressId,
		@RequestBody AddressRequest request);

	@DeleteMapping("/{addressId}")
	void deleteAddress(@PathVariable("addressId") Long addressId);
}