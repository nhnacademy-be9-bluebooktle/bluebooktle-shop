package shop.bluebooktle.frontend.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.common.dto.book_order.response.PackagingOptionInfoResponse;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.frontend.repository.AdminPackagingOptionRepository;
import shop.bluebooktle.frontend.service.AdminPackagingOptionService;

@Service
@RequiredArgsConstructor
public class AdminPackagingOptionServiceImpl implements AdminPackagingOptionService {
	private final AdminPackagingOptionRepository packagingOptionRepository;

	@Override
	public Page<PackagingOptionInfoResponse> getPackagingOptions(int page, int size, String searchKeyword) {
		Pageable pageable = PageRequest.of(page, size);

		String keyword = null;
		if (searchKeyword != null && !searchKeyword.isBlank()) {
			keyword = searchKeyword;
		}

		PaginationData<PackagingOptionInfoResponse> data = packagingOptionRepository.getPackagingOptions(page, size,
			keyword);
		return new PageImpl<>(data.getContent(), pageable, data.getTotalElements());
	}

	@Override
	public PackagingOptionInfoResponse getPackagingOption(Long id) {
		return packagingOptionRepository.getPackagingOption(id);
	}

	@Override
	public void deletePackingOption(Long id) {
		packagingOptionRepository.deletePackagingOption(id);
	}

	@Override
	public void updatePackingOption(Long id, PackagingOptionInfoResponse request) {
		packagingOptionRepository.updatePackagingOption(id, request);
	}

	@Override
	public void createPackingOption(PackagingOptionInfoResponse request) {
		packagingOptionRepository.createPackagingOption(request);
	}
}
