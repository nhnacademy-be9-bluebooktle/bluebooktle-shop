package shop.bluebooktle.backend.book_order.service;

import shop.bluebooktle.common.dto.book_order.request.BookOrderRegisterRequest;
import shop.bluebooktle.common.dto.book_order.request.BookOrderUpdateRequest;
import shop.bluebooktle.common.dto.book_order.response.BookOrderResponse;

public interface BookOrderService {
	/** 도서 주문 생성 */
	BookOrderResponse createBookOrder(BookOrderRegisterRequest request);

	/** 도서 주문 조회 */
	BookOrderResponse getBookOrder(Long bookOrderId);

	/** 도서 주문 수정 */
	BookOrderResponse updateBookOrder(Long bookOrderId, BookOrderUpdateRequest request);

	/** 도서 주문 삭제 */
	void deleteBookOrder(Long bookOrderId);
}
