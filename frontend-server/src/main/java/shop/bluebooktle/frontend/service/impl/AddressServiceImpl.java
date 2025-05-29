package shop.bluebooktle.frontend.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.common.dto.user.request.AddressRequest;
import shop.bluebooktle.common.dto.user.response.AddressResponse;
import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;
import shop.bluebooktle.frontend.repository.UserAddressRepository;
import shop.bluebooktle.frontend.service.AddressService;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

	private final UserAddressRepository userAddressRepository;

	@Override
	public List<AddressResponse> getAddresses() {
		log.debug("주소 목록 조회를 시작합니다.");
		try {
			List<AddressResponse> addresses = userAddressRepository.getUserAddresses();
			log.info("주소 {}개를 성공적으로 조회했습니다.", addresses != null ? addresses.size() : 0);
			return addresses;
		} catch (ApplicationException e) {
			log.error("주소 목록 조회 중 ApplicationException 발생: {}", e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error("주소 목록 조회 중 예상치 못한 오류 발생", e);
			throw new ApplicationException(ErrorCode.INTERNAL_SERVER_ERROR, "주소 조회 중 시스템 오류가 발생했습니다.", e);
		}
	}

	@Override
	public AddressResponse createAddress(AddressRequest request) {
		log.debug("새 주소 등록을 시작합니다. 별칭: {}", request.getAlias());
		try {
			AddressResponse createdAddress = userAddressRepository.createAddress(request);
			log.info("새 주소(ID: {})를 성공적으로 등록했습니다.", createdAddress.getAddressId());
			return createdAddress;
		} catch (ApplicationException e) {
			log.error("주소 등록 중 ApplicationException 발생: {}", e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error("주소 등록 중 예상치 못한 오류 발생", e);
			throw new ApplicationException(ErrorCode.INTERNAL_SERVER_ERROR, "주소 등록 중 시스템 오류가 발생했습니다.", e);
		}
	}

	@Override
	public AddressResponse updateAddress(Long addressId, AddressRequest request) {
		log.debug("주소 수정을 시작합니다. ID: {}", addressId);
		try {
			AddressResponse updatedAddress = userAddressRepository.updateAddress(addressId, request);
			log.info("주소(ID: {})를 성공적으로 수정했습니다.", addressId);
			return updatedAddress;
		} catch (ApplicationException e) {
			log.error("주소(ID: {}) 수정 중 ApplicationException 발생: {}", addressId, e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error("주소(ID: {}) 수정 중 예상치 못한 오류 발생", addressId, e);
			throw new ApplicationException(ErrorCode.INTERNAL_SERVER_ERROR, "주소 수정 중 시스템 오류가 발생했습니다.", e);
		}
	}

	@Override
	public void deleteAddress(Long addressId) {
		log.debug("주소 삭제를 시작합니다. ID: {}", addressId);
		try {
			userAddressRepository.deleteAddress(addressId);
			log.info("주소(ID: {})를 성공적으로 삭제했습니다.", addressId);
		} catch (ApplicationException e) {
			log.error("주소(ID: {}) 삭제 중 ApplicationException 발생: {}", addressId, e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error("주소(ID: {}) 삭제 중 예상치 못한 오류 발생", addressId, e);
			throw new ApplicationException(ErrorCode.INTERNAL_SERVER_ERROR, "주소 삭제 중 시스템 오류가 발생했습니다.", e);
		}
	}
}