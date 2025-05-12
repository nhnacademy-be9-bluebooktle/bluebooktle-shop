package shop.bluebooktle.backend.book_order.service;

import shop.bluebooktle.common.dto.book_order.request.OrderPackagingRequest;
import shop.bluebooktle.common.dto.book_order.response.OrderPackagingResponse;

public interface OrderPackagingService {
	/** 도서 주문에 포장 옵션 추가 */
	OrderPackagingResponse addOrderPackaging(OrderPackagingRequest request);

	/** 도서 주문 포장 옵션 단건 조회 */
	OrderPackagingResponse getOrderPackaging(Long orderPackagingId);

	/** 도서 주문 포장 옵션 수정 */
	OrderPackagingResponse updateOrderPackaging(Long orderPackagingId, OrderPackagingRequest request);

	/** 도서 주문 포장 옵션 삭제 */
	void deleteOrderPackaging(Long orderPackagingId);
}
