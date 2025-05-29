package shop.bluebooktle.backend.user.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.user.repository.AddressRepository;
import shop.bluebooktle.backend.user.repository.UserRepository;
import shop.bluebooktle.backend.user.service.AddressService;
import shop.bluebooktle.common.dto.user.request.AddressRequest;
import shop.bluebooktle.common.dto.user.response.AddressResponse;
import shop.bluebooktle.common.entity.auth.Address;
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.exception.auth.UserNotFoundException;
import shop.bluebooktle.common.exception.user.AddressLimitExceededException;
import shop.bluebooktle.common.exception.user.AddressNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AddressServiceImpl implements AddressService {

	private final UserRepository userRepository;
	private final AddressRepository addressRepository;

	private static final int MAX_ADDRESS_COUNT = 10;

	@Override
	public List<AddressResponse> findAddressesByUserId(Long userId) {
		if (!userRepository.existsById(userId)) {
			throw new UserNotFoundException();
		}
		return addressRepository.findByUser_Id(userId).stream()
			.map(AddressResponse::fromEntity)
			.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public AddressResponse createAddress(Long userId, AddressRequest request) {
		User user = userRepository.findById(userId)
			.orElseThrow(UserNotFoundException::new);

		if (addressRepository.countByUser_Id(userId) >= MAX_ADDRESS_COUNT) {
			throw new AddressLimitExceededException("주소는 최대 " + MAX_ADDRESS_COUNT + "개까지 등록할 수 있습니다.");
		}

		Address address = request.toEntity();
		user.addAddress(address);

		Address savedAddress = addressRepository.save(address); // 주소 저장
		return AddressResponse.fromEntity(savedAddress);
	}

	@Override
	@Transactional
	public AddressResponse updateAddress(Long userId, Long addressId, AddressRequest request) {
		Address address = addressRepository.findByIdAndUser_Id(addressId, userId)
			.orElseThrow(() -> new AddressNotFoundException("수정할 주소를 찾을 수 없거나 권한이 없습니다."));

		address.update(
			request.getAlias(),
			request.getRoadAddress(),
			request.getDetailAddress(),
			request.getPostalCode()
		);

		Address updatedAddress = addressRepository.save(address);
		return AddressResponse.fromEntity(updatedAddress);
	}

	@Override
	@Transactional
	public void deleteAddress(Long userId, Long addressId) {
		Address address = addressRepository.findByIdAndUser_Id(addressId, userId)
			.orElseThrow(() -> new AddressNotFoundException("삭제할 주소를 찾을 수 없거나 권한이 없습니다."));

		addressRepository.delete(address);
	}
}