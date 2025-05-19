package shop.bluebooktle.backend.cart.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.repository.BookRepository;
import shop.bluebooktle.backend.cart.dto.response.CartItemResponse;
import shop.bluebooktle.backend.cart.entity.Cart;
import shop.bluebooktle.backend.cart.entity.CartBook;
import shop.bluebooktle.backend.cart.repository.CartBookRepository;
import shop.bluebooktle.backend.cart.repository.CartRepository;
import shop.bluebooktle.backend.cart.repository.redis.GuestCartRepository;
import shop.bluebooktle.backend.cart.service.impl.CartServiceImpl;
import shop.bluebooktle.common.domain.auth.UserStatus;
import shop.bluebooktle.common.domain.auth.UserType;
import shop.bluebooktle.common.entity.auth.MembershipLevel;
import shop.bluebooktle.common.entity.auth.User;

class CartServiceImplTest {

	@Mock
	private CartRepository cartRepository;

	@Mock
	private CartBookRepository cartBookRepository;

	@Mock
	private BookRepository bookRepository;

	@Mock
	private GuestCartRepository guestCartRepository;

	@InjectMocks
	private CartServiceImpl cartService;

	private User user;
	private Book book;
	private Cart cart;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		MembershipLevel level = new MembershipLevel("일반", 1, null, null);
		user = User.builder()
			.membershipLevel(level)
			.loginId("testUser")
			.encodedPassword("pw")
			.name("name")
			.email("email@test.com")
			.nickname("nick")
			.birth("19900101")
			.phoneNumber("01011112222")
			.type(UserType.USER)
			.status(UserStatus.ACTIVE)
			.build();

		book = Book.builder().id(1L).title("테스트 도서").isbn("123456").build();
		cart = Cart.builder().user(user).build();
	}

	// ----------------- 회원용 -----------------

	@Test
	@DisplayName("회원 장바구니에 도서 추가 - 기존 도서가 없어 새로 추가")
	void addBookToUserCart_whenBookNotExists() {
		// given
		given(cartRepository.findByUser(user)).willReturn(Optional.of(cart));
		given(bookRepository.getReferenceById(1L)).willReturn(book);
		given(cartBookRepository.findByCartAndBook(cart, book)).willReturn(Optional.empty());

		// when
		cartService.addBookToUserCart(user, 1L, 3);

		// then
		then(cartBookRepository).should(times(1)).save(any(CartBook.class));
	}

	@Test
	@DisplayName("회원 장바구니에 도서 추가 - 기존 도서가 있어 수량만 증가")
	void addBookToUserCart_whenBookAlreadyExists() {
		// given
		CartBook cartBook = CartBook.of(book, cart, 1);
		given(cartRepository.findByUser(user)).willReturn(Optional.of(cart));
		given(bookRepository.getReferenceById(1L)).willReturn(book);
		given(cartBookRepository.findByCartAndBook(cart, book)).willReturn(Optional.of(cartBook));

		// when
		cartService.addBookToUserCart(user, 1L, 2);

		// then
		assertThat(cartBook.getQuantity()).isEqualTo(3);
		then(cartBookRepository).should(never()).save(any());
	}

	@Test
	@DisplayName("회원 장바구니 조회 - 여러 도서 포함")
	void getUserCartItems_success() {
		// given
		Book book1 = Book.builder().id(1L).title("도서1").isbn("111").build();
		Book book2 = Book.builder().id(2L).title("도서2").isbn("222").build();

		CartBook cartBook1 = CartBook.of(book1, cart, 2);
		CartBook cartBook2 = CartBook.of(book2, cart, 5);

		List<CartBook> cartBooks = List.of(cartBook1, cartBook2);
		ReflectionTestUtils.setField(cart, "cartBooks", cartBooks);

		given(cartRepository.findByUser(user)).willReturn(Optional.of(cart));

		// when
		List<CartItemResponse> result = cartService.getUserCartItems(user);

		// then
		assertThat(result).hasSize(2);
		assertThat(result).extracting(CartItemResponse::bookId)
			.containsExactlyInAnyOrder(1L, 2L);
		assertThat(result).extracting(CartItemResponse::quantity)
			.containsExactlyInAnyOrder(2, 5);
	}

	@Test
	@DisplayName("회원 장바구니 수량 증가 - 기존 도서 존재")
	void increaseUserQuantity_success() {
		// given
		CartBook cartBook = CartBook.of(book, cart, 2);
		given(cartRepository.findByUser(user)).willReturn(Optional.of(cart));
		given(bookRepository.getReferenceById(book.getId())).willReturn(book);
		given(cartBookRepository.findByCartAndBook(cart, book)).willReturn(Optional.of(cartBook));

		// when
		cartService.increaseUserQuantity(user, book.getId(), 3);

		// then
		assertThat(cartBook.getQuantity()).isEqualTo(5);
	}

	@Test
	@DisplayName("회원 장바구니 수량 감소 - 최소 수량 1 보장")
	void decreaseUserQuantity_success() {
		// given
		CartBook cartBook = CartBook.of(book, cart, 3); // 초기 수량 3
		given(cartRepository.findByUser(user)).willReturn(Optional.of(cart));
		given(bookRepository.getReferenceById(book.getId())).willReturn(book);
		given(cartBookRepository.findByCartAndBook(cart, book)).willReturn(Optional.of(cartBook));

		// when
		cartService.decreaseUserQuantity(user, book.getId(), 2); // → 수량 1
		cartService.decreaseUserQuantity(user, book.getId(), 5); // → 수량 유지 (최소 1)

		// then
		assertThat(cartBook.getQuantity()).isEqualTo(1);
	}

	@Test
	@DisplayName("회원 장바구니 도서 삭제 - 해당 도서 존재 시")
	void removeBookFromUserCart_success() {
		// given
		CartBook cartBook = CartBook.of(book, cart, 2);
		given(cartRepository.findByUser(user)).willReturn(Optional.of(cart));
		given(bookRepository.getReferenceById(book.getId())).willReturn(book);
		given(cartBookRepository.findByCartAndBook(cart, book)).willReturn(Optional.of(cartBook));

		// when
		cartService.removeBookFromUserCart(user, book.getId());

		// then
		then(cartBookRepository).should(times(1)).delete(cartBook);
	}

	@Test
	@DisplayName("회원 장바구니 도서들 일괄 삭제 - 선택한 도서만 삭제")
	void removeSelectedBooksFromUserCart_success() {
		// given
		Book book1 = Book.builder().id(1L).title("도서1").isbn("111").build();
		Book book2 = Book.builder().id(2L).title("도서2").isbn("222").build();

		CartBook cb1 = CartBook.of(book1, cart, 2);
		CartBook cb2 = CartBook.of(book2, cart, 1);
		List<CartBook> cartBooks = List.of(cb1, cb2);

		List<Long> bookIds = List.of(1L, 2L);

		given(cartRepository.findByUser(user)).willReturn(Optional.of(cart));
		given(cartBookRepository.findAllByCartAndBookIdIn(cart, bookIds)).willReturn(cartBooks);

		// when
		cartService.removeSelectedBooksFromUserCart(user, bookIds);

		// then
		then(cartBookRepository).should().deleteAll(cartBooks);
	}

	// ----------------- 비회원용 -----------------

	@Test
	@DisplayName("비회원 장바구니 도서 추가 - GuestCartRepository 위임")
	void addBookToGuestCart_success() {
		// given
		String guestId = "guest-abc";
		Long bookId = 10L;
		int quantity = 3;

		// when
		cartService.addBookToGuestCart(guestId, bookId, quantity);

		// then
		then(guestCartRepository).should().addBook(guestId, bookId, quantity);
	}

	@Test
	@DisplayName("비회원 장바구니 조회 - GuestCartRepository에서 데이터 조회")
	void getGuestCartItems_success() {
		// given
		String guestId = "guest-abc";
		Map<Long, Integer> guestCart = Map.of(
			100L, 2,
			200L, 5
		);
		given(guestCartRepository.getCart(guestId)).willReturn(guestCart);

		// when
		List<CartItemResponse> result = cartService.getGuestCartItems(guestId);

		// then
		assertThat(result).hasSize(2);
		assertThat(result).extracting(CartItemResponse::bookId).containsExactlyInAnyOrder(100L, 200L);
		assertThat(result).extracting(CartItemResponse::quantity).containsExactlyInAnyOrder(2, 5);
	}

	@Test
	@DisplayName("비회원 장바구니 수량 증가 - GuestCartRepository 위임")
	void increaseGuestQuantity_success() {
		// given
		String guestId = "guest-abc";
		Long bookId = 100L;
		int quantity = 2;

		// when
		cartService.increaseGuestQuantity(guestId, bookId, quantity);

		// then
		then(guestCartRepository).should().increaseQuantity(guestId, bookId, quantity);
	}

	@Test
	@DisplayName("비회원 장바구니 수량 감소 - GuestCartRepository 위임")
	void decreaseGuestQuantity_success() {
		// given
		String guestId = "guest-abc";
		Long bookId = 100L;
		int quantity = 3;

		// when
		cartService.decreaseGuestQuantity(guestId, bookId, quantity);

		// then
		then(guestCartRepository).should().decreaseQuantity(guestId, bookId, quantity);
	}

	@Test
	@DisplayName("비회원 장바구니 도서 삭제 - GuestCartRepository 위임")
	void removeBookFromGuestCart_success() {
		// given
		String guestId = "guest-abc";
		Long bookId = 100L;

		// when
		cartService.removeBookFromGuestCart(guestId, bookId);

		// then
		then(guestCartRepository).should().removeBook(guestId, bookId);
	}

	@Test
	@DisplayName("비회원 장바구니 선택 도서 일괄 삭제 - GuestCartRepository 위임")
	void removeSelectedBooksFromGuestCart_success() {
		// given
		String guestId = "guest-abc";
		List<Long> bookIds = List.of(1L, 2L, 3L);

		// when
		cartService.removeSelectedBooksFromGuestCart(guestId, bookIds);

		// then
		then(guestCartRepository).should().removeSelectedBooks(guestId, bookIds);
	}

	// ----------------- 전환용 -----------------

	@Test
	@DisplayName("비회원 → 회원 장바구니 전환 - 기존 Cart 없음, Redis에서 가져와 저장")
	void convertGuestCartToMemberCart_success() {
		// given
		String guestId = "guest-abc";
		Map<Long, Integer> guestCart = Map.of(10L, 2, 20L, 3);
		Book book1 = Book.builder().id(10L).title("도서1").isbn("isbn1").build();
		Book book2 = Book.builder().id(20L).title("도서2").isbn("isbn2").build();

		given(guestCartRepository.getCart(guestId)).willReturn(guestCart);
		given(bookRepository.getReferenceById(10L)).willReturn(book1);
		given(bookRepository.getReferenceById(20L)).willReturn(book2);
		given(cartRepository.save(any(Cart.class))).willReturn(cart);

		// when
		cartService.convertGuestCartToMemberCart(guestId, user);

		// then
		then(cartRepository).should().save(any(Cart.class));
		then(cartBookRepository).should().saveAll(anyList());
		then(guestCartRepository).should().removeSelectedBooks(eq(guestId), anyList());
	}

	@Test
	@DisplayName("비회원 → 회원 장바구니 병합 - 기존 장바구니에 수량 합산 또는 새로 추가")
	void mergeGuestCartToMemberCart_success() {
		// given
		String guestId = "guest-abc";
		Map<Long, Integer> guestCart = Map.of(10L, 2, 20L, 1);
		Book book1 = Book.builder().id(10L).title("도서1").isbn("isbn1").build();
		Book book2 = Book.builder().id(20L).title("도서2").isbn("isbn2").build();

		CartBook existing = CartBook.of(book1, cart, 3); // 기존 장바구니에 이미 있음
		given(guestCartRepository.getCart(guestId)).willReturn(guestCart);
		given(cartRepository.findByUser(user)).willReturn(Optional.of(cart));
		given(bookRepository.getReferenceById(10L)).willReturn(book1);
		given(bookRepository.getReferenceById(20L)).willReturn(book2);
		given(cartBookRepository.findByCartAndBook(cart, book1)).willReturn(Optional.of(existing));
		given(cartBookRepository.findByCartAndBook(cart, book2)).willReturn(Optional.empty());

		// when
		cartService.mergeGuestCartToMemberCart(guestId, user);

		// then
		// 수량 합산 확인
		assertThat(existing.getQuantity()).isEqualTo(5);
		// 새로운 항목 저장 확인
		then(cartBookRepository).should().save(argThat(cb ->
			cb.getBook().getId().equals(20L) &&
				cb.getCart().equals(cart) &&
				cb.getQuantity() == 1
		));
		// Redis 제거 확인
		then(guestCartRepository).should().removeSelectedBooks(eq(guestId), anyList());
	}

}