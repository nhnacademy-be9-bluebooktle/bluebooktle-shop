package shop.bluebooktle.backend.book_order.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book_order.entity.PackagingOption;
import shop.bluebooktle.backend.book_order.repository.PackagingOptionRepository;
import shop.bluebooktle.backend.book_order.service.PackagingOptionService;
import shop.bluebooktle.common.dto.book_order.request.PackagingOptionRequest;
import shop.bluebooktle.common.dto.book_order.request.PackagingOptionUpdateRequest;
import shop.bluebooktle.common.dto.book_order.response.PackagingOptionResponse;
import shop.bluebooktle.common.exception.book_order.PackagingOptionNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional
public class PackagingOptionServiceImpl implements PackagingOptionService {
	private final PackagingOptionRepository packagingOptionRepository;

	/** 포장 옵션 등록 */
	@Override
	public PackagingOptionResponse createPackagingOption(PackagingOptionRequest request) {
		PackagingOption option = PackagingOption.builder()
			.name(request.getName())
			.price(request.getPrice())
			.build();
		PackagingOption saved = packagingOptionRepository.save(option);

		return PackagingOptionResponse.builder()
			.packagingOptionId(saved.getId())
			.name(saved.getName())
			.price(saved.getPrice())
			.build();
	}

	/** 포장 옵션 전체 조회 */
	@Override
	@Transactional(readOnly = true)
	public Page<PackagingOptionResponse> getPackagingOption(Pageable pageable) {
		List<PackagingOption> options = packagingOptionRepository.findAllByDeletedAtIsNull();
		List<PackagingOptionResponse> responses = options.stream()
			.map(o -> PackagingOptionResponse.builder()
				.packagingOptionId(o.getId())
				.name(o.getName())
				.price(o.getPrice())
				.build())
			.collect(Collectors.toList());
		return new PageImpl<>(responses, pageable, responses.size());
	}

	/** 포장 옵션 수정 */
	@Override
	public PackagingOptionResponse updatePackagingOption(PackagingOptionUpdateRequest request) {
		PackagingOption option = packagingOptionRepository.findByIdAndDeletedAtIsNull(request.getPackagingOptionId())
			.orElseThrow(PackagingOptionNotFoundException::new);

		option.setName(request.getName());
		option.setPrice(request.getPrice());

		return PackagingOptionResponse.builder()
			.packagingOptionId(option.getId())
			.name(option.getName())
			.price(option.getPrice())
			.build();
	}

	/** 포장 옵션 삭제 */
	@Override
	public void deletePackagingOption(Long packagingOptionId) {
		PackagingOption option = packagingOptionRepository.findById(packagingOptionId)
			.orElseThrow(PackagingOptionNotFoundException::new);
		packagingOptionRepository.delete(option);
	}
}
