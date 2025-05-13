package shop.bluebooktle.backend.book_order.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book_order.entity.BookOrder;
import shop.bluebooktle.backend.book_order.entity.OrderPackaging;
import shop.bluebooktle.backend.book_order.entity.PackagingOption;
import shop.bluebooktle.backend.book_order.repository.BookOrderRepository;
import shop.bluebooktle.backend.book_order.repository.OrderPackagingRepository;
import shop.bluebooktle.backend.book_order.repository.PackagingOptionRepository;
import shop.bluebooktle.backend.book_order.service.OrderPackagingService;
import shop.bluebooktle.common.dto.book_order.request.OrderPackagingRequest;
import shop.bluebooktle.common.dto.book_order.request.OrderPackagingUpdateRequest;
import shop.bluebooktle.common.dto.book_order.response.OrderPackagingResponse;
import shop.bluebooktle.common.exception.book_order.BookOrderNotFoundException;
import shop.bluebooktle.common.exception.book_order.OrderPackagingNotFoundException;
import shop.bluebooktle.common.exception.book_order.PackagingOptionNotFoundException;
import shop.bluebooktle.common.exception.book_order.PackagingQuantityExceedException;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderPackagingServiceImpl implements OrderPackagingService {
	private final OrderPackagingRepository orderPackagingRepository;
	private final BookOrderRepository bookOrderRepository;
	private final PackagingOptionRepository packagingOptionRepository;

	/** 도서 주문에 포장 옵션 추가 */
	@Override
	public OrderPackagingResponse addOrderPackaging(OrderPackagingRequest request) {
		BookOrder bookOrder = bookOrderRepository.findById(request.getBookOrderId())
			.orElseThrow(BookOrderNotFoundException::new);
		PackagingOption option = packagingOptionRepository.findById(request.getPackagingOptionId())
			.orElseThrow(PackagingOptionNotFoundException::new);

		if (request.getQuantity() > bookOrder.getQuantity()) { // 수량 검증 (주문 포장 개수 > 도서 주문 수량, 에러)
			throw new PackagingQuantityExceedException();
		}

		OrderPackaging orderPackaging = OrderPackaging.builder()
			.bookOrder(bookOrder)
			.packagingOption(option)
			.quantity(request.getQuantity())
			.build();

		OrderPackaging saved = orderPackagingRepository.save(orderPackaging);

		return OrderPackagingResponse.builder()
			.orderPackagingId(saved.getId())
			.bookOrderId(saved.getBookOrder().getId())
			.packagingOptionId(saved.getPackagingOption().getId())
			.quantity(saved.getQuantity())
			.createdAt(saved.getCreatedAt())
			.build();
	}

	/** 도서 주문 포장 옵션 단건 조회 */
	@Override
	@Transactional(readOnly = true)
	public OrderPackagingResponse getOrderPackaging(Long orderPackagingId) {
		OrderPackaging op = orderPackagingRepository.findById(orderPackagingId)
			.orElseThrow(OrderPackagingNotFoundException::new);

		return OrderPackagingResponse.builder()
			.orderPackagingId(op.getId())
			.bookOrderId(op.getBookOrder().getId())
			.packagingOptionId(op.getPackagingOption().getId())
			.quantity(op.getQuantity())
			.createdAt(op.getCreatedAt())
			.build();
	}

	/** 도서 주문 포장 옵션 수정 */
	@Override
	public OrderPackagingResponse updateOrderPackaging(Long orderPackagingId, OrderPackagingUpdateRequest request) {
		OrderPackaging op = orderPackagingRepository.findById(orderPackagingId)
			.orElseThrow(OrderPackagingNotFoundException::new);

		PackagingOption newOption = packagingOptionRepository.findById(request.getPackagingOptionId())
			.orElseThrow(PackagingOptionNotFoundException::new);

		op.setPackagingOption(newOption);
		op.setQuantity(request.getQuantity());

		return OrderPackagingResponse.builder()
			.orderPackagingId(op.getId())
			.bookOrderId(op.getBookOrder().getId())
			.packagingOptionId(op.getPackagingOption().getId())
			.quantity(op.getQuantity())
			.createdAt(op.getCreatedAt())
			.build();
	}

	/** 도서 주문 포장 옵션 삭제 */
	@Override
	public void deleteOrderPackaging(Long orderPackagingId) {
		OrderPackaging op = orderPackagingRepository.findById(orderPackagingId)
			.orElseThrow(OrderPackagingNotFoundException::new);

		orderPackagingRepository.delete(op);
	}
}
