package shop.bluebooktle.frontend.repository;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import shop.bluebooktle.common.dto.book.response.BookCartOrderResponse;
import shop.bluebooktle.common.dto.cart.request.BookIdListRequest;
import shop.bluebooktle.common.dto.cart.request.CartItemRequest;
import shop.bluebooktle.common.dto.cart.request.CartRemoveOneRequest;
import shop.bluebooktle.common.dto.cart.request.CartRemoveSelectedRequest;
import shop.bluebooktle.frontend.config.feign.FeignGlobalConfig;
import shop.bluebooktle.frontend.config.retry.RetryWithTokenRefresh;

@FeignClient(
	url = "${server.gateway-url}",
	path = "/api/cart",
	name = "cartRepository",
	configuration = FeignGlobalConfig.class
)
public interface CartRepository {

	// ✅ 공통 카트 기능

	@PostMapping
	@RetryWithTokenRefresh
	void addBookToCart(
		@RequestBody CartItemRequest request,
		@RequestHeader(name = "GUEST_ID", required = false) String guestId
	);

	@GetMapping
	List<BookCartOrderResponse> getCartItems(
		@RequestHeader(name = "GUEST_ID", required = false) String guestId
	);

	@PostMapping("/increase")
	void increaseQuantity(
		@RequestBody CartItemRequest request,
		@RequestHeader(name = "GUEST_ID", required = false) String guestId
	);

	@PostMapping("/decrease")
	void decreaseQuantity(
		@RequestBody CartItemRequest request,
		@RequestHeader(name = "GUEST_ID", required = false) String guestId
	);

	@DeleteMapping
	void removeBook(
		@RequestBody CartRemoveOneRequest request,
		@RequestHeader(name = "GUEST_ID", required = false) String guestId
	);

	@DeleteMapping("/selected")
	void removeSelectedBooks(
		@RequestBody CartRemoveSelectedRequest request,
		@RequestHeader(name = "GUEST_ID", required = false) String guestId
	);

	@PostMapping("/order")
	List<BookCartOrderResponse> getSelectedCartItemsForOrder(
		@RequestBody BookIdListRequest request,
		@RequestHeader(name = "GUEST_ID", required = false) String guestId
	);

	//  전환/병합
	@PostMapping("/convert/merge")
	void mergeOrConvertGuestCartToMember(
		@RequestHeader(name = "GUEST_ID", required = false) String guestId
	);

	@GetMapping("quantity")
	Long getCartQuantity(
		@RequestHeader(name = "GUEST_ID", required = false) String guestId
	);
}