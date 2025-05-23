package shop.bluebooktle.frontend.service;

import org.springframework.data.domain.Page;

import shop.bluebooktle.common.dto.book_order.response.PackagingOptionInfoResponse;

public interface AdminPackagingOptionService {
	// 포장 옵션 목록 조회
	Page<PackagingOptionInfoResponse> getPackagingOptions(int page, int size, String searchKeyword);

	// 단일 포장 옵션 조회
	PackagingOptionInfoResponse getPackagingOption(Long id);

	// 포장 옵션 등록
	void createPackingOption(PackagingOptionInfoResponse request);

	// 포장 옵션 수정
	void updatePackingOption(Long id, PackagingOptionInfoResponse request);

	// 포장 옵션 삭제
	void deletePackingOption(Long id);
}
