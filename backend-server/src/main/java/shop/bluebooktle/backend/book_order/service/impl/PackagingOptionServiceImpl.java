package shop.bluebooktle.backend.book_order.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book_order.entity.PackagingOption;
import shop.bluebooktle.backend.book_order.repository.PackagingOptionRepository;
import shop.bluebooktle.backend.book_order.service.PackagingOptionService;
import shop.bluebooktle.common.dto.book_order.request.PackagingOptionRequest;
import shop.bluebooktle.common.dto.book_order.response.PackagingOptionInfoResponse;
import shop.bluebooktle.common.exception.book_order.PackagingOptionNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional
public class PackagingOptionServiceImpl implements PackagingOptionService {
	private final PackagingOptionRepository packagingOptionRepository;

	/** 포장 옵션 등록 */
	@Override
	public PackagingOptionInfoResponse createPackagingOption(PackagingOptionRequest request) {
		if (packagingOptionRepository.existsByName(request.getName())) {
			throw new PackagingOptionNotFoundException(); // 이미 등록된 포장 옵션 이름이라면 에러 발생
		}
		PackagingOption option = PackagingOption.builder()
			.name(request.getName())
			.price(request.getPrice())
			.build();
		PackagingOption saved = packagingOptionRepository.save(option);

		return PackagingOptionInfoResponse.builder()
			.packagingOptionId(saved.getId())
			.name(saved.getName())
			.price(saved.getPrice())
			.build();
	}

	/** 포장 옵션 전체 조회 */
	@Override
	public Page<PackagingOptionInfoResponse> getPackagingOption(Pageable pageable) {
		Page<PackagingOption> page = packagingOptionRepository.findAllByDeletedAtIsNull(pageable);
		return page.map(o -> PackagingOptionInfoResponse.builder()
			.packagingOptionId(o.getId())
			.name(o.getName())
			.price(o.getPrice())
			.build());
	}

	/** 포장 옵션 수정 */
	@Override
	public PackagingOptionInfoResponse updatePackagingOption(Long packagingOptionId,
		PackagingOptionRequest request) {
		PackagingOption option = packagingOptionRepository.findByIdAndDeletedAtIsNull(packagingOptionId)
			.orElseThrow(PackagingOptionNotFoundException::new);

		option.setName(request.getName());
		option.setPrice(request.getPrice());

		return PackagingOptionInfoResponse.builder()
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
