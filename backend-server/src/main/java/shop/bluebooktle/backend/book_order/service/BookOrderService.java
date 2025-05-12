package shop.bluebooktle.backend.book_order.service;

import shop.bluebooktle.common.dto.book_order.request.BookOrderRequest;
import shop.bluebooktle.common.dto.book_order.response.BookOrderResponse;

public interface BookOrderService {
	/** 도서 주문 생성 */
	BookOrderResponse createBookOrder(BookOrderRequest request);

	/** 도서 주문 조회 */
	BookOrderResponse getBookOrder(Long bookOrderId);

	/** 도서 주문 수정 */
	BookOrderResponse updateBookOrder(BookOrderRequest request);

	/** 도서 주문 삭제 */
	void deleteBookOrder(Long bookOrderId);
}
