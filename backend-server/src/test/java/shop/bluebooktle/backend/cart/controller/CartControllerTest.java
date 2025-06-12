package shop.bluebooktle.backend.cart.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import shop.bluebooktle.backend.cart.service.CartService;
import shop.bluebooktle.common.domain.auth.UserStatus;
import shop.bluebooktle.common.domain.auth.UserType;
import shop.bluebooktle.common.dto.auth.UserDto;
import shop.bluebooktle.common.dto.book.response.BookCartOrderResponse;
import shop.bluebooktle.common.dto.cart.request.BookIdListRequest;
import shop.bluebooktle.common.dto.cart.request.CartItemRequest;
import shop.bluebooktle.common.dto.cart.request.CartRemoveOneRequest;
import shop.bluebooktle.common.dto.cart.request.CartRemoveSelectedRequest;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.security.UserPrincipal;

@ExtendWith(MockitoExtension.class)
class CartControllerGuestTest {

	@Mock
	CartService cartService;

	@InjectMocks
	CartController cartController;

	final String guestId = "guest-1234";

	UserDto userDto = UserDto.builder()
		.id(1L)
		.loginId("loginId")
		.nickname("nickname")
		.type(UserType.USER)
		.status(UserStatus.ACTIVE)
		.build();

	UserPrincipal principal = new UserPrincipal(userDto);
	User user;

	@BeforeEach
	void setup() {
		UserDto userDto = UserDto.builder()
			.id(1L)
			.loginId("loginId")
			.nickname("nickname")
			.type(UserType.USER)
			.status(UserStatus.ACTIVE)
			.build();

		principal = new UserPrincipal(userDto);
		user = mock(User.class);

		// 불필요한 stubbing 예외 방지
		lenient().when(cartService.findUserEntityById(userDto.getId())).thenReturn(user);
	}

	@Test
	@DisplayName("비회원 장바구니 담기 - bookId, quantity 전달 시 201 반환")
	void addBookToCart_guest_success() {
		CartItemRequest request = new CartItemRequest(42L, 3);

		ResponseEntity<JsendResponse<Void>> response = cartController.addBookToCart(
			null, guestId, request
		);

		assertThat(response.getStatusCodeValue()).isEqualTo(201);
		JsendResponse<Void> body = response.getBody();
		assertThat(body).isNotNull();
		assertThat(body.status()).isEqualTo("success");
		assertThat(body.data()).isNull();
		assertThat(body.message()).isNull();
		assertThat(body.code()).isNull();
	}

	@Test
	@DisplayName("비회원 장바구니 목록 조회 - guestId로 조회 성공")
	void getCartItems_guest() {
		List<BookCartOrderResponse> mockResponse = List.of();
		when(cartService.getGuestCartItems(guestId)).thenReturn(mockResponse);

		ResponseEntity<JsendResponse<List<BookCartOrderResponse>>> response =
			cartController.getCartItems(null, guestId);

		assertThat(response.getStatusCodeValue()).isEqualTo(200);
		assertThat(response.getBody().status()).isEqualTo("success");
		assertThat(response.getBody().data()).isEqualTo(mockResponse);
		verify(cartService).getGuestCartItems(guestId);
	}

	@Test
	@DisplayName("비회원 장바구니 수량 증가 - bookId, quantity 전달 시 성공")
	void increaseQuantity_guest() {
		CartItemRequest req = new CartItemRequest(1L, 2);
		ResponseEntity<JsendResponse<Void>> response = cartController.increaseQuantity(null, guestId, req);
		assertThat(response.getStatusCodeValue()).isEqualTo(200);
		verify(cartService).increaseGuestQuantity(guestId, 1L, 2);
	}

	@Test
	@DisplayName("비회원 장바구니 수량 감소 - bookId, quantity 전달 시 성공")
	void decreaseQuantity_guest() {
		CartItemRequest req = new CartItemRequest(1L, 1);
		ResponseEntity<JsendResponse<Void>> response = cartController.decreaseQuantity(null, guestId, req);
		assertThat(response.getStatusCodeValue()).isEqualTo(200);
		verify(cartService).decreaseGuestQuantity(guestId, 1L, 1);
	}

	@Test
	@DisplayName("비회원 장바구니 도서 1개 삭제 - bookId 전달 시 성공")
	void removeBook_guest() {
		CartRemoveOneRequest req = new CartRemoveOneRequest("some-id", 1L);

		ResponseEntity<JsendResponse<Void>> response =
			cartController.removeBook(null, guestId, req);

		assertThat(response.getStatusCodeValue()).isEqualTo(200);
		verify(cartService).removeBookFromGuestCart(guestId, 1L);
	}

	@Test
	@DisplayName("비회원 장바구니 선택 삭제 - bookIds 리스트로 삭제 성공")
	void removeSelectedBooks_guest() {
		List<Long> ids = List.of(1L, 2L);
		CartRemoveSelectedRequest req = new CartRemoveSelectedRequest("dummy-id", ids);

		ResponseEntity<JsendResponse<Void>> response =
			cartController.removeSelectedBooks(null, guestId, req);

		assertThat(response.getStatusCodeValue()).isEqualTo(200);
		verify(cartService).removeSelectedBooksFromGuestCart(guestId, ids);
	}

	@Test
	@DisplayName("비회원 장바구니 수량 조회 - guestId로 수량 조회 성공")
	void getQuantity_guest() {
		when(cartService.getGuestCartSize(guestId)).thenReturn(5L);
		ResponseEntity<JsendResponse<Long>> response = cartController.getQuantity(null, guestId);
		assertThat(response.getBody().data()).isEqualTo(5L);
		verify(cartService).getGuestCartSize(guestId);
	}

	@Test
	@DisplayName("회원 장바구니 담기 - bookId, quantity 전달 시 201 반환")
	void addBookToCart_member_success() {
		CartItemRequest request = new CartItemRequest(10L, 1);

		ResponseEntity<JsendResponse<Void>> response =
			cartController.addBookToCart(principal, null, request);

		assertThat(response.getStatusCodeValue()).isEqualTo(201);
		verify(cartService).addBookToUserCart(user, 10L, 1);
	}

	@Test
	@DisplayName("회원 장바구니 조회 - 장바구니 항목 리스트 반환")
	void getCartItems_member() {
		List<BookCartOrderResponse> mockItems = List.of();
		when(cartService.getUserCartItems(user)).thenReturn(mockItems);

		ResponseEntity<JsendResponse<List<BookCartOrderResponse>>> response =
			cartController.getCartItems(principal, null);

		assertThat(response.getBody().data()).isEqualTo(mockItems);
		verify(cartService).getUserCartItems(user);
	}

	@Test
	@DisplayName("회원 장바구니 수량 증가 - 정상 처리")
	void increaseQuantity_member() {
		CartItemRequest req = new CartItemRequest(1L, 3);
		ResponseEntity<JsendResponse<Void>> response =
			cartController.increaseQuantity(principal, null, req);
		assertThat(response.getStatusCodeValue()).isEqualTo(200);
		verify(cartService).increaseUserQuantity(user, 1L, 3);
	}

	@Test
	@DisplayName("회원 장바구니 수량 감소 - 정상 처리")
	void decreaseQuantity_member() {
		CartItemRequest req = new CartItemRequest(1L, 2);
		ResponseEntity<JsendResponse<Void>> response =
			cartController.decreaseQuantity(principal, null, req);
		assertThat(response.getStatusCodeValue()).isEqualTo(200);
		verify(cartService).decreaseUserQuantity(user, 1L, 2);
	}

	@Test
	@DisplayName("회원 장바구니 도서 1개 삭제")
	void removeBook_member() {
		CartRemoveOneRequest req = new CartRemoveOneRequest("any", 1L);
		ResponseEntity<JsendResponse<Void>> response =
			cartController.removeBook(principal, null, req);
		assertThat(response.getStatusCodeValue()).isEqualTo(200);
		verify(cartService).removeBookFromUserCart(user, 1L);
	}

	@Test
	@DisplayName("회원 장바구니 선택 삭제")
	void removeSelectedBooks_member() {
		List<Long> ids = List.of(1L, 2L);
		CartRemoveSelectedRequest req = new CartRemoveSelectedRequest("any", ids);
		ResponseEntity<JsendResponse<Void>> response =
			cartController.removeSelectedBooks(principal, null, req);
		assertThat(response.getStatusCodeValue()).isEqualTo(200);
		verify(cartService).removeSelectedBooksFromUserCart(user, ids);
	}

	@Test
	@DisplayName("회원 장바구니 주문용 선택 항목 조회")
	void getSelectedCartItemsForOrder_member() {
		List<Long> bookIds = List.of(10L, 20L);
		List<BookCartOrderResponse> expected = List.of();
		when(cartService.sendSelectedCartItemsToOrder(user, bookIds)).thenReturn(expected);

		ResponseEntity<JsendResponse<List<BookCartOrderResponse>>> response =
			cartController.getSelectedCartItemsForOrder(principal, null, new BookIdListRequest(bookIds));

		assertThat(response.getBody().data()).isEqualTo(expected);
		verify(cartService).sendSelectedCartItemsToOrder(user, bookIds);
	}

	@Test
	@DisplayName("회원 장바구니 수량 조회")
	void getQuantity_member() {
		when(cartService.getCartSize(user)).thenReturn(7L);

		ResponseEntity<JsendResponse<Long>> response = cartController.getQuantity(principal, null);

		assertThat(response.getBody().data()).isEqualTo(7L);
		verify(cartService).getCartSize(user);
	}

	@Test
	@DisplayName("회원 장바구니 병합 또는 전환 - guestId로 병합 요청 성공")
	void mergeOrConvertGuestCart_member() {
		// given
		String guestId = "guest-xyz";

		// when
		ResponseEntity<JsendResponse<Void>> response =
			cartController.mergeOrConvertGuestCart(principal, guestId);

		// then
		assertThat(response.getStatusCodeValue()).isEqualTo(200);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().status()).isEqualTo("success");

		verify(cartService).mergeOrConvertGuestCartToMemberCart(guestId, user);
	}

}
