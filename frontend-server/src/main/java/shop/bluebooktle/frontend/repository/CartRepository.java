package shop.bluebooktle.frontend.repository;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import shop.bluebooktle.common.dto.cart.request.GuestCartItemRequest;
import shop.bluebooktle.common.dto.cart.request.GuestCartRemoveOneRequest;
import shop.bluebooktle.common.dto.cart.request.GuestCartRemoveSelectedRequest;
import shop.bluebooktle.common.dto.cart.request.MemberCartItemRequest;
import shop.bluebooktle.common.dto.cart.request.MemberCartRemoveOneRequest;
import shop.bluebooktle.common.dto.cart.request.MemberCartRemoveSelectedRequest;
import shop.bluebooktle.common.dto.cart.response.CartItemResponse;
import shop.bluebooktle.frontend.config.feign.FeignGlobalConfig;

@FeignClient(
	url = "${server.gateway-url}",
	path = "/api/cart",
	name = "cartRepository",
	configuration = FeignGlobalConfig.class
)
public interface CartRepository {

	// === [비회원] ===

	@PostMapping("/guest")
	void addBookToGuestCart(@RequestBody GuestCartItemRequest request);

	@GetMapping("/guest")
	List<CartItemResponse> getGuestCartItems(@RequestParam("guestId") String guestId);

	@PatchMapping("/guest/increase")
	void increaseGuestQuantity(@RequestBody GuestCartItemRequest request);

	@PatchMapping("/guest/decrease")
	void decreaseGuestQuantity(@RequestBody GuestCartItemRequest request);

	@DeleteMapping("/guest")
	void removeBookFromGuestCart(@RequestBody GuestCartRemoveOneRequest request);

	@DeleteMapping("/guest/selected")
	void removeSelectedBooksFromGuestCart(@RequestBody GuestCartRemoveSelectedRequest request);

	// === [회원] ===

	@PostMapping("/member")
	Void addBookToMemberCart(@RequestBody MemberCartItemRequest request);

	@GetMapping("/member")
	List<CartItemResponse> getMemberCartItems();

	@PatchMapping("/member/increase")
	Void increaseMemberQuantity(@RequestBody MemberCartItemRequest request);

	@PatchMapping("/member/decrease")
	Void decreaseMemberQuantity(@RequestBody MemberCartItemRequest request);

	@DeleteMapping("/member")
	Void removeBookFromMemberCart(@RequestBody MemberCartRemoveOneRequest request);

	@DeleteMapping("/member/selected")
	Void removeSelectedBooksFromMemberCart(@RequestBody MemberCartRemoveSelectedRequest request);

	// === [전환 및 병합] ===

	@PostMapping("/convert/to-member")
	Void convertGuestCartToMember(@RequestBody String guestId);

	@PatchMapping("/convert/merge")
	Void mergeGuestCartToMember(@RequestBody String guestId);
}