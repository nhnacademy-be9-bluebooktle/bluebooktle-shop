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
import shop.bluebooktle.frontend.config.retry.RetryWithTokenRefresh;

@FeignClient(url = "${server.gateway-url}", name = "userAddressRepository", path = "/api/addresses", configuration = FeignGlobalConfig.class)
public interface UserAddressRepository {

	@GetMapping
	@RetryWithTokenRefresh
	List<AddressResponse> getUserAddresses();

	@PostMapping
	@RetryWithTokenRefresh
	AddressResponse createAddress(@RequestBody AddressRequest request);

	@PutMapping("/{addressId}")
	@RetryWithTokenRefresh
	AddressResponse updateAddress(
		@PathVariable("addressId") Long addressId,
		@RequestBody AddressRequest request);

	@DeleteMapping("/{addressId}")
	@RetryWithTokenRefresh
	void deleteAddress(@PathVariable("addressId") Long addressId);
}