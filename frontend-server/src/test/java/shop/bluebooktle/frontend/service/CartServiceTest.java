package shop.bluebooktle.frontend.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import shop.bluebooktle.common.dto.book.response.BookCartOrderResponse;
import shop.bluebooktle.common.dto.cart.request.BookIdListRequest;
import shop.bluebooktle.common.dto.cart.request.CartItemRequest;
import shop.bluebooktle.common.dto.cart.request.CartRemoveOneRequest;
import shop.bluebooktle.common.dto.cart.request.CartRemoveSelectedRequest;
import shop.bluebooktle.frontend.repository.CartRepository;
import shop.bluebooktle.frontend.service.impl.CartServiceImpl;

class CartServiceTest {

	private CartServiceImpl cartService;

	@Mock
	private CartRepository cartRepository;

	private final String guestId = "guest-123";
	private final Long bookId = 1L;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		cartService = new CartServiceImpl(cartRepository);
	}

	@Test
	@DisplayName("장바구니에 도서 추가")
	void addToCartTest() {
		cartService.addToCart(guestId, bookId, 2);
		verify(cartRepository).addBookToCart(new CartItemRequest(bookId, 2), guestId);
	}

	@Test
	@DisplayName("장바구니 항목 조회")
	void getCartItemsTest() {
		List<BookCartOrderResponse> expected = List.of(mock(BookCartOrderResponse.class));
		when(cartRepository.getCartItems(guestId)).thenReturn(expected);

		List<BookCartOrderResponse> result = cartService.getCartItems(guestId);

		assertThat(result).isEqualTo(expected);
		verify(cartRepository).getCartItems(guestId);
	}

	@Test
	@DisplayName("수량 증가")
	void increaseQuantityTest() {
		cartService.increaseQuantity(guestId, bookId);
		verify(cartRepository).increaseQuantity(new CartItemRequest(bookId, 1), guestId);
	}

	@Test
	@DisplayName("수량 감소")
	void decreaseQuantityTest() {
		cartService.decreaseQuantity(guestId, bookId);
		verify(cartRepository).decreaseQuantity(new CartItemRequest(bookId, 1), guestId);
	}

	@Test
	@DisplayName("도서 1개 삭제")
	void removeOneTest() {
		cartService.removeOne(guestId, bookId);
		verify(cartRepository).removeBook(new CartRemoveOneRequest(guestId, bookId), guestId);
	}

	@Test
	@DisplayName("선택한 도서들 삭제")
	void removeSelectedTest() {
		List<Long> bookIds = List.of(1L, 2L);
		cartService.removeSelected(guestId, bookIds);
		verify(cartRepository).removeSelectedBooks(new CartRemoveSelectedRequest(guestId, bookIds), guestId);
	}

	@Test
	@DisplayName("선택한 도서 조회 (주문용)")
	void getSelectedCartItemsForOrderTest() {
		List<Long> bookIds = List.of(1L, 2L);
		List<BookCartOrderResponse> expected = List.of(mock(BookCartOrderResponse.class));
		when(cartRepository.getSelectedCartItemsForOrder(new BookIdListRequest(bookIds), guestId)).thenReturn(expected);

		List<BookCartOrderResponse> result = cartService.getSelectedCartItemsForOrder(guestId, bookIds);

		assertThat(result).isEqualTo(expected);
	}

	@Test
	@DisplayName("게스트 장바구니 병합 또는 전환")
	void mergeOrConvertGuestCartTest() {
		cartService.mergeOrConvertGuestCart(guestId);
		verify(cartRepository).mergeOrConvertGuestCartToMember(guestId);
	}

	@Test
	@DisplayName("장바구니 전체 수량 조회")
	void getCartSizeTest() {
		when(cartRepository.getCartQuantity(guestId)).thenReturn(5L);

		Long result = cartService.getCartSize(guestId);

		assertThat(result).isEqualTo(5L);
		verify(cartRepository).getCartQuantity(guestId);
	}
}