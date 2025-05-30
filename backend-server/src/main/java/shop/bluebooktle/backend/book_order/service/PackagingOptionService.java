package shop.bluebooktle.backend.book_order.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.bluebooktle.common.dto.book_order.request.PackagingOptionRequest;
import shop.bluebooktle.common.dto.book_order.request.PackagingOptionUpdateRequest;
import shop.bluebooktle.common.dto.book_order.response.PackagingOptionResponse;

public interface PackagingOptionService {

	/** 포장 옵션 등록 */
	PackagingOptionResponse createPackagingOption(PackagingOptionRequest request);

	/** 포장 옵션 전체 조회 */
	Page<PackagingOptionResponse> getPackagingOption(Pageable pageable);

	/** 포장 옵션 수정 */
	PackagingOptionResponse updatePackagingOption(Long packagingOptionId, PackagingOptionUpdateRequest request);

	/** 포장 옵션 삭제 */
	void deletePackagingOption(Long packagingOptionId);
}
