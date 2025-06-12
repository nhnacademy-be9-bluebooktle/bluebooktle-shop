package shop.bluebooktle.backend.cart.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.BookCategory;
import shop.bluebooktle.backend.book.entity.BookImg;
import shop.bluebooktle.backend.book.entity.BookSaleInfo;
import shop.bluebooktle.backend.book.entity.Category;
import shop.bluebooktle.backend.book.entity.Img;
import shop.bluebooktle.backend.book.repository.BookCategoryRepository;
import shop.bluebooktle.backend.book.repository.BookImgRepository;
import shop.bluebooktle.backend.book.repository.BookRepository;
import shop.bluebooktle.backend.book.repository.BookSaleInfoRepository;
import shop.bluebooktle.backend.cart.entity.Cart;
import shop.bluebooktle.backend.cart.entity.CartBook;
import shop.bluebooktle.backend.cart.repository.CartBookRepository;
import shop.bluebooktle.backend.cart.repository.CartRepository;
import shop.bluebooktle.backend.cart.repository.redis.GuestCartRepository;
import shop.bluebooktle.backend.cart.service.impl.CartServiceImpl;
import shop.bluebooktle.backend.user.repository.UserRepository;
import shop.bluebooktle.common.dto.book.response.BookCartOrderResponse;
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.exception.auth.UserNotFoundException;
import shop.bluebooktle.common.exception.book.BookNotFoundException;
import shop.bluebooktle.common.exception.cart.CartNotFoundException;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private CartRepository cartRepository;
	@Mock
	private CartBookRepository cartBookRepository;
	@Mock
	private BookRepository bookRepository;
	@Mock
	private BookSaleInfoRepository bookSaleInfoRepository;
	@Mock
	private BookCategoryRepository bookCategoryRepository;
	@Mock
	private BookImgRepository bookImgRepository;
	@Mock
	private GuestCartRepository guestCartRepository;

	@InjectMocks
	private CartServiceImpl cartService;

	static class TestCartFactory {
		static User createUser(Long id) {
			return User.builder().id(id).loginId("user" + id).email("user" + id + "@test.com").build();
		}

		static Book createBook(Long id, String title) {
			return Book.builder().id(id).title(title).build();
		}

		static Cart createCart(User user) {
			return Cart.builder().user(user).cartBooks(new ArrayList<>()).build();
		}

		static CartBook createCartBook(Book book, Cart cart, int quantity) {
			CartBook cb = CartBook.of(book, cart, quantity);
			cart.getCartBooks().add(cb);
			return cb;
		}

		static BookSaleInfo createBookSaleInfo(int price, int salePrice, boolean packable) {
			return BookSaleInfo.builder()
				.price(BigDecimal.valueOf(price))
				.salePrice(BigDecimal.valueOf(salePrice))
				.isPackable(packable)
				.build();
		}

		static BookImg createBookImg(String url) {
			Img img = mock(Img.class);
			when(img.getImgUrl()).thenReturn(url);
			BookImg bi = mock(BookImg.class);
			when(bi.getImg()).thenReturn(img);
			return bi;
		}

		public static List<BookCategory> createBookCategories(Book book, String... categoryNames) {
			List<BookCategory> result = new ArrayList<>();
			for (String name : categoryNames) {
				Category category = Category.builder().name(name).build();
				BookCategory bookCategory = new BookCategory(book, category);
				result.add(bookCategory);
			}
			return result;
		}

	}

	@Test
	@DisplayName("유저 조회 성공")
	void findUserEntityById_success() {
		// given
		Long userId = 1L;
		User user = User.builder().id(userId).build();

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));

		// when
		User result = cartService.findUserEntityById(userId);

		// then
		assertThat(result).isEqualTo(user);
	}

	@Test
	@DisplayName("유저 조회 실패 - 예외 발생")
	void findUserEntityById_fail() {
		// given
		Long userId = 999L;
		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> cartService.findUserEntityById(userId))
			.isInstanceOf(UserNotFoundException.class);
	}

	@Test
	@DisplayName("기존에 있는 책이면 수량만 증가")
	void addBookToCart_existingBook_increaseQuantity() {
		// given
		Long userId = 1L;
		Long bookId = 10L;
		int quantity = 3;

		User user = User.builder().id(userId).build();
		Cart cart = Cart.builder().id(100L).user(user).build();
		Book book = Book.builder().id(bookId).build();

		CartBook cartBook = spy(CartBook.of(book, cart, 2));

		when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
		when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
		when(cartBookRepository.findByCartAndBook(cart, book)).thenReturn(Optional.of(cartBook));

		// when
		cartService.addBookToUserCart(user, bookId, quantity);

		// then
		verify(cartBook, times(1)).increaseQuantity(quantity);
		verify(cartBookRepository, never()).save(any());
	}

	@Test
	@DisplayName("새로운 책이면 새로 저장")
	void addBookToCart_newBook_savesNewCartBook() {
		// given
		Long userId = 2L;
		Long bookId = 20L;
		int quantity = 1;

		User user = User.builder().id(userId).build();
		Cart cart = Cart.builder().id(200L).user(user).build();
		Book book = Book.builder().id(bookId).build();

		when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
		when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
		when(cartBookRepository.findByCartAndBook(cart, book)).thenReturn(Optional.empty());

		// when
		cartService.addBookToUserCart(user, bookId, quantity);

		// then
		verify(cartBookRepository).save(argThat(saved ->
			saved.getBook().equals(book) &&
				saved.getCart().equals(cart) &&
				saved.getQuantity() == quantity
		));
	}

	@Test
	@DisplayName("유저 카트 아이템 불러오기")
	void testGetUserCartItems() {
		// given
		User user = TestCartFactory.createUser(1L);
		Book book = TestCartFactory.createBook(100L, "테스트 도서");
		Cart cart = TestCartFactory.createCart(user);
		CartBook cartBook = TestCartFactory.createCartBook(book, cart, 2);

		BookSaleInfo saleInfo = TestCartFactory.createBookSaleInfo(20000, 15000, true);
		List<BookCategory> bookCategories = TestCartFactory.createBookCategories(book, "소설", "문학");

		when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
		when(bookSaleInfoRepository.findByBookId(book.getId())).thenReturn(Optional.of(saleInfo));
		when(bookCategoryRepository.findByBook_Id(book.getId())).thenReturn(bookCategories);
		when(bookImgRepository.findByBookId(book.getId())).thenReturn(List.of());

		// when
		List<BookCartOrderResponse> result = cartService.getUserCartItems(user);

		// then
		assertThat(result).hasSize(1);
		BookCartOrderResponse dto = result.get(0);
		assertThat(dto.bookId()).isEqualTo(book.getId());
		assertThat(dto.title()).isEqualTo(book.getTitle());
		assertThat(dto.price()).isEqualTo(saleInfo.getPrice());
		assertThat(dto.salePrice()).isEqualTo(saleInfo.getSalePrice());
		assertThat(dto.quantity()).isEqualTo(2);
		assertThat(dto.thumbnailUrl()).isEqualTo("기본 썸네일url");
		assertThat(dto.categories()).containsExactly("소설", "문학");
		assertThat(dto.isPackable()).isTrue();
	}

	@Test
	@DisplayName("회원 장바구니 수량 증가 - 기존 책이 있는 경우")
	void increaseUserQuantity_existingBook_increasesQuantity() {
		// given
		User user = TestCartFactory.createUser(1L);
		Book book = TestCartFactory.createBook(10L, "테스트 도서");
		Cart cart = TestCartFactory.createCart(user);
		CartBook cartBook = spy(TestCartFactory.createCartBook(book, cart, 2));

		int addedQuantity = 3;

		when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
		when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
		when(cartBookRepository.findByCartAndBook(cart, book)).thenReturn(Optional.of(cartBook));

		// when
		cartService.increaseUserQuantity(user, book.getId(), addedQuantity);

		// then
		verify(cartBook).increaseQuantity(addedQuantity);
		verify(cartBookRepository, never()).save(any());
	}

	@Test
	@DisplayName("회원 장바구니 수량 증가 - 새로운 책일 경우 저장")
	void increaseUserQuantity_newBook_savesNewCartBook() {
		// given
		User user = TestCartFactory.createUser(2L);
		Book book = TestCartFactory.createBook(20L, "신규 도서");
		Cart cart = TestCartFactory.createCart(user);

		int quantity = 1;

		when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
		when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
		when(cartBookRepository.findByCartAndBook(cart, book)).thenReturn(Optional.empty());

		// when
		cartService.increaseUserQuantity(user, book.getId(), quantity);

		// then
		verify(cartBookRepository).save(argThat(saved ->
			saved.getBook().equals(book) &&
				saved.getCart().equals(cart) &&
				saved.getQuantity() == quantity
		));
	}

	@Test
	@DisplayName("회원 장바구니 수량 감소 - 기존 책이 있을 경우 감소")
	void decreaseUserQuantity_existingBook_decreasesQuantity() {
		// given
		User user = TestCartFactory.createUser(3L);
		Book book = TestCartFactory.createBook(30L, "감소 테스트 도서");
		Cart cart = TestCartFactory.createCart(user);
		CartBook cartBook = spy(TestCartFactory.createCartBook(book, cart, 5));

		int decreaseAmount = 2;

		when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
		when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
		when(cartBookRepository.findByCartAndBook(cart, book)).thenReturn(Optional.of(cartBook));

		// when
		cartService.decreaseUserQuantity(user, book.getId(), decreaseAmount);

		// then
		verify(cartBook).decreaseQuantity(decreaseAmount);
	}

	@Test
	@DisplayName("회원 장바구니 단일 항목 삭제")
	void removeBookFromUserCart_success() {
		// given
		User user = TestCartFactory.createUser(1L);
		Book book = TestCartFactory.createBook(10L, "테스트 도서");
		Cart cart = TestCartFactory.createCart(user);
		CartBook cartBook = spy(TestCartFactory.createCartBook(book, cart, 1));

		when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
		when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
		when(cartBookRepository.findByCartAndBook(cart, book)).thenReturn(Optional.of(cartBook));

		// when
		cartService.removeBookFromUserCart(user, book.getId());

		// then
		verify(cartBook).setDeletedAt();
	}

	@Test
	@DisplayName("회원 장바구니 여러 항목 삭제")
	void removeSelectedBooksFromUserCart_success() {
		// given
		User user = TestCartFactory.createUser(2L);
		Book book1 = TestCartFactory.createBook(100L, "도서 1");
		Book book2 = TestCartFactory.createBook(200L, "도서 2");
		Cart cart = TestCartFactory.createCart(user);
		CartBook cartBook1 = spy(TestCartFactory.createCartBook(book1, cart, 1));
		CartBook cartBook2 = spy(TestCartFactory.createCartBook(book2, cart, 2));

		List<Long> bookIds = List.of(book1.getId(), book2.getId());
		List<CartBook> cartBooks = List.of(cartBook1, cartBook2);

		when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
		when(cartBookRepository.findAllByCartAndBookIdIn(cart, bookIds)).thenReturn(cartBooks);

		// when
		cartService.removeSelectedBooksFromUserCart(user, bookIds);

		// then
		verify(cartBook1).setDeletedAt();
		verify(cartBook2).setDeletedAt();
	}

	@Test
	@DisplayName("회원 장바구니 단일 항목 삭제 실패 - 카트 없음")
	void removeBookFromUserCart_fail_cartNotFound() {
		// given
		User user = TestCartFactory.createUser(1L);
		Long bookId = 10L;

		when(cartRepository.findByUser(user)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> cartService.removeBookFromUserCart(user, bookId))
			.isInstanceOf(CartNotFoundException.class);
	}

	@Test
	@DisplayName("회원 장바구니 단일 항목 삭제 실패 - 도서 없음")
	void removeBookFromUserCart_fail_bookNotFound() {
		// given
		User user = TestCartFactory.createUser(2L);
		Long bookId = 20L;
		Cart cart = TestCartFactory.createCart(user);

		when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
		when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> cartService.removeBookFromUserCart(user, bookId))
			.isInstanceOf(BookNotFoundException.class);
	}

	@Test
	@DisplayName("회원 장바구니 단일 항목 삭제 실패 - 장바구니 도서 없음")
	void removeBookFromUserCart_fail_cartBookNotFound() {
		// given
		User user = TestCartFactory.createUser(3L);
		Book book = TestCartFactory.createBook(30L, "없는 도서");
		Cart cart = TestCartFactory.createCart(user);

		when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
		when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
		when(cartBookRepository.findByCartAndBook(cart, book)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> cartService.removeBookFromUserCart(user, book.getId()))
			.isInstanceOf(RuntimeException.class)
			.hasMessage("CartBook not found");
	}

	@Test
	@DisplayName("선택한 도서만 주문용 DTO로 반환")
	void sendSelectedCartItemsToOrder_success() {
		// given
		User user = TestCartFactory.createUser(1L);
		Book book1 = TestCartFactory.createBook(101L, "도서1");
		Book book2 = TestCartFactory.createBook(102L, "도서2");
		Cart cart = TestCartFactory.createCart(user);
		TestCartFactory.createCartBook(book1, cart, 2);
		TestCartFactory.createCartBook(book2, cart, 3);

		// 선택한 bookId는 도서1만
		List<Long> selectedBookIds = List.of(book1.getId());

		// Stub 설정
		when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
		when(bookSaleInfoRepository.findByBookId(book1.getId()))
			.thenReturn(Optional.of(TestCartFactory.createBookSaleInfo(10000, 8000, true)));
		when(bookCategoryRepository.findByBook_Id(book1.getId()))
			.thenReturn(TestCartFactory.createBookCategories(book1, "소설"));
		when(bookImgRepository.findByBookId(book1.getId()))
			.thenReturn(List.of()); // fallback URL 유도

		// when
		List<BookCartOrderResponse> result = cartService.sendSelectedCartItemsToOrder(user, selectedBookIds);

		// then
		assertThat(result).hasSize(1);
		BookCartOrderResponse dto = result.get(0);
		assertThat(dto.bookId()).isEqualTo(book1.getId());
		assertThat(dto.title()).isEqualTo(book1.getTitle());
		assertThat(dto.quantity()).isEqualTo(2);
		assertThat(dto.thumbnailUrl()).isEqualTo("기본 썸네일url");
		assertThat(dto.categories()).containsExactly("소설");
		assertThat(dto.price()).isEqualTo(BigDecimal.valueOf(10000));
		assertThat(dto.salePrice()).isEqualTo(BigDecimal.valueOf(8000));
		assertThat(dto.isPackable()).isTrue();
	}

	@Test
	@DisplayName("선택 도서 주문 - 카트 없음으로 실패")
	void sendSelectedCartItemsToOrder_fail_cartNotFound() {
		// given
		User user = TestCartFactory.createUser(999L);
		List<Long> selectedBookIds = List.of(101L);

		when(cartRepository.findByUser(user)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> cartService.sendSelectedCartItemsToOrder(user, selectedBookIds))
			.isInstanceOf(UserNotFoundException.class);
	}

	@Test
	@DisplayName("회원 장바구니 도서 수량 조회")
	void getCartSize_success() {
		// given
		User user = TestCartFactory.createUser(1L);
		Cart cart = TestCartFactory.createCart(user);

		when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
		when(cartBookRepository.countByCart(cart)).thenReturn(5L);

		// when
		Long result = cartService.getCartSize(user);

		// then
		assertThat(result).isEqualTo(5L);
	}

	@Test
	@DisplayName("회원 장바구니가 없을 때 수량은 0")
	void getCartSize_noCart_returnsZero() {
		// given
		User user = TestCartFactory.createUser(2L);
		when(cartRepository.findByUser(user)).thenReturn(Optional.empty());

		// when
		Long result = cartService.getCartSize(user);

		// then
		assertThat(result).isEqualTo(0L);
	}

	@Test
	@DisplayName("비회원 장바구니에 도서 추가")
	void addBookToGuestCart_success() {
		// given
		String guestId = "guest-123";
		Long bookId = 101L;
		int quantity = 2;

		// when
		cartService.addBookToGuestCart(guestId, bookId, quantity);

		// then
		verify(guestCartRepository).addBook(guestId, bookId, quantity);
	}

	@Test
	@DisplayName("비회원 장바구니 도서 목록 조회")
	void getGuestCartItems_success() {
		// given
		String guestId = "guest-abc";
		Long bookId = 101L;
		int quantity = 3;

		Map<Long, Integer> guestCart = Map.of(bookId, quantity);

		Book book = TestCartFactory.createBook(bookId, "도서A");
		BookSaleInfo saleInfo = TestCartFactory.createBookSaleInfo(12000, 9000, true);
		List<BookCategory> categories = TestCartFactory.createBookCategories(book, "소설", "에세이");
		BookImg thumbnail = TestCartFactory.createBookImg("http://img.url");

		when(guestCartRepository.getCart(guestId)).thenReturn(guestCart);
		when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
		when(bookSaleInfoRepository.findByBookId(bookId)).thenReturn(Optional.of(saleInfo));
		when(bookCategoryRepository.findByBook_Id(bookId)).thenReturn(categories);
		when(bookImgRepository.findByBookId(bookId)).thenReturn(List.of(thumbnail));

		// when
		List<BookCartOrderResponse> result = cartService.getGuestCartItems(guestId);

		// then
		assertThat(result).hasSize(1);
		BookCartOrderResponse dto = result.get(0);
		assertThat(dto.bookId()).isEqualTo(bookId);
		assertThat(dto.title()).isEqualTo("도서A");
		assertThat(dto.quantity()).isEqualTo(quantity);
		assertThat(dto.price()).isEqualTo(saleInfo.getPrice());
		assertThat(dto.salePrice()).isEqualTo(saleInfo.getSalePrice());
		assertThat(dto.isPackable()).isTrue();
		assertThat(dto.thumbnailUrl()).isEqualTo("http://img.url");
		assertThat(dto.categories()).containsExactly("소설", "에세이");
	}

	@Test
	@DisplayName("비회원 장바구니 도서 수량 증가")
	void increaseGuestQuantity_success() {
		// given
		String guestId = "guest-456";
		Long bookId = 202L;
		int quantity = 2;

		// when
		cartService.increaseGuestQuantity(guestId, bookId, quantity);

		// then
		verify(guestCartRepository).increaseQuantity(guestId, bookId, quantity);
	}

	@Test
	@DisplayName("비회원 장바구니 도서 수량 감소")
	void decreaseGuestQuantity_success() {
		// given
		String guestId = "guest-789";
		Long bookId = 303L;
		int quantity = 1;

		// when
		cartService.decreaseGuestQuantity(guestId, bookId, quantity);

		// then
		verify(guestCartRepository).decreaseQuantity(guestId, bookId, quantity);
	}

	@Test
	@DisplayName("비회원 장바구니 단일 도서 삭제")
	void removeBookFromGuestCart_success() {
		// given
		String guestId = "guest-999";
		Long bookId = 404L;

		// when
		cartService.removeBookFromGuestCart(guestId, bookId);

		// then
		verify(guestCartRepository).removeBook(guestId, bookId);
	}

	@Test
	@DisplayName("비회원 장바구니에서 선택한 도서들 삭제")
	void removeSelectedBooksFromGuestCart_success() {
		// given
		String guestId = "guest-abc";
		List<Long> bookIds = List.of(101L, 102L, 103L);

		// when
		cartService.removeSelectedBooksFromGuestCart(guestId, bookIds);

		// then
		verify(guestCartRepository).removeSelectedBooks(guestId, bookIds);
	}

	@Test
	@DisplayName("비회원 장바구니 선택 도서 주문용 DTO 반환")
	void sendSelectedGuestCartItemsToOrder_success() {
		// given
		String guestId = "guest-xyz";
		Long bookId1 = 100L;
		Long bookId2 = 200L;

		// 장바구니에는 두 권이 있지만 bookId1만 선택
		Map<Long, Integer> guestCart = Map.of(
			bookId1, 2,
			bookId2, 1
		);

		Book book1 = TestCartFactory.createBook(bookId1, "선택 도서");
		BookSaleInfo saleInfo = TestCartFactory.createBookSaleInfo(10000, 8000, true);
		List<BookCategory> categories = TestCartFactory.createBookCategories(book1, "소설");
		BookImg thumbnail = TestCartFactory.createBookImg("http://img.url");

		when(guestCartRepository.getCart(guestId)).thenReturn(guestCart);
		when(bookRepository.findById(bookId1)).thenReturn(Optional.of(book1));
		when(bookSaleInfoRepository.findByBookId(bookId1)).thenReturn(Optional.of(saleInfo));
		when(bookCategoryRepository.findByBook_Id(bookId1)).thenReturn(categories);
		when(bookImgRepository.findByBookId(bookId1)).thenReturn(List.of(thumbnail));

		// when
		List<BookCartOrderResponse> result = cartService.sendSelectedGuestCartItemsToOrder(guestId, List.of(bookId1));

		// then
		assertThat(result).hasSize(1);
		BookCartOrderResponse dto = result.get(0);
		assertThat(dto.bookId()).isEqualTo(bookId1);
		assertThat(dto.title()).isEqualTo("선택 도서");
		assertThat(dto.quantity()).isEqualTo(2);
		assertThat(dto.price()).isEqualTo(BigDecimal.valueOf(10000));
		assertThat(dto.salePrice()).isEqualTo(BigDecimal.valueOf(8000));
		assertThat(dto.thumbnailUrl()).isEqualTo("http://img.url");
		assertThat(dto.categories()).containsExactly("소설");
		assertThat(dto.isPackable()).isTrue();
	}

	@Test
	@DisplayName("비회원 장바구니 수량 조회")
	void getGuestCartSize_success() {
		// given
		String guestId = "guest-1234";
		long expectedSize = 4L;

		when(guestCartRepository.getCartSize(guestId)).thenReturn(expectedSize);

		// when
		Long result = cartService.getGuestCartSize(guestId);

		// then
		assertThat(result).isEqualTo(expectedSize);
		verify(guestCartRepository).getCartSize(guestId);
	}

	@Test
	@DisplayName("회원의 장바구니가 존재하면 그대로 반환")
	void getOrCreateCart_existingCart() {
		// given
		User user = TestCartFactory.createUser(1L);
		Cart existingCart = TestCartFactory.createCart(user);

		when(cartRepository.findByUser(user)).thenReturn(Optional.of(existingCart));
		when(cartBookRepository.countByCart(existingCart)).thenReturn(0L); // getCartSize 테스트용

		// when
		Long size = cartService.getCartSize(user); // 내부적으로 getOrCreateCart 호출

		// then
		assertThat(size).isEqualTo(0L);
		verify(cartRepository, never()).save(any()); // 새로 저장되지 않음
	}

	@Test
	@DisplayName("회원의 장바구니가 없으면 새로 생성 후 반환")
	void getOrCreateCart_createNewCart() {
		// given
		User user = TestCartFactory.createUser(2L);
		Cart newCart = TestCartFactory.createCart(user);

		when(cartRepository.findByUser(user)).thenReturn(Optional.empty());
		when(cartRepository.save(any(Cart.class))).thenReturn(newCart);
		when(cartBookRepository.countByCart(newCart)).thenReturn(0L); // getCartSize 테스트용

		// when
		Long size = cartService.getCartSize(user); // 내부적으로 getOrCreateCart 호출

		// then
		assertThat(size).isEqualTo(0L);
		verify(cartRepository).save(any(Cart.class)); // 새로 저장됨
	}

	@Test
	@DisplayName("비회원 장바구니가 비어 있으면 아무 작업도 하지 않음")
	void mergeOrConvertGuestCartToMemberCart_emptyCart_doesNothing() {
		// given
		String guestId = "guest-empty";
		User user = TestCartFactory.createUser(1L);

		when(guestCartRepository.getCart(guestId)).thenReturn(Map.of());

		// when
		cartService.mergeOrConvertGuestCartToMemberCart(guestId, user);

		// then
		verify(cartRepository, never()).findByUser(any());
		verify(cartBookRepository, never()).saveAll(any());
		verify(guestCartRepository, never()).removeSelectedBooks(any(), any());
	}

	@Test
	@DisplayName("회원 장바구니가 없으면 생성 후 비회원 장바구니 내용 추가")
	void mergeOrConvertGuestCartToMemberCart_convertNewCart() {
		// given
		String guestId = "guest-new";
		User user = TestCartFactory.createUser(2L);
		Long bookId = 100L;

		Map<Long, Integer> guestCart = Map.of(bookId, 3);
		Book book = TestCartFactory.createBook(bookId, "책");

		when(guestCartRepository.getCart(guestId)).thenReturn(guestCart);
		when(cartRepository.findByUser(user)).thenReturn(Optional.empty());

		Cart newCart = TestCartFactory.createCart(user);
		when(cartRepository.save(any(Cart.class))).thenReturn(newCart);
		when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

		// when
		cartService.mergeOrConvertGuestCartToMemberCart(guestId, user);

		// then
		verify(cartRepository).save(any(Cart.class));
		verify(cartBookRepository).saveAll(any());
		verify(guestCartRepository).removeSelectedBooks(eq(guestId), eq(List.of(bookId)));
	}

	@Test
	@DisplayName("회원 장바구니가 이미 있으면 수량 증가 또는 새 항목 추가")
	void mergeOrConvertGuestCartToMemberCart_mergeWithExistingCart() {
		// given
		String guestId = "guest-merge";
		User user = TestCartFactory.createUser(3L);
		Long bookId = 200L;

		Map<Long, Integer> guestCart = Map.of(bookId, 2);
		Book book = TestCartFactory.createBook(bookId, "책");
		Cart existingCart = TestCartFactory.createCart(user);
		CartBook existingCartBook = spy(TestCartFactory.createCartBook(book, existingCart, 1));

		when(guestCartRepository.getCart(guestId)).thenReturn(guestCart);
		when(cartRepository.findByUser(user)).thenReturn(Optional.of(existingCart));
		when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
		when(cartBookRepository.findByCartAndBook(existingCart, book)).thenReturn(Optional.of(existingCartBook));

		// when
		cartService.mergeOrConvertGuestCartToMemberCart(guestId, user);

		// then
		verify(existingCartBook).increaseQuantity(2);
		verify(cartBookRepository, never()).save(any()); // 수량 증가만, 저장은 안함
		verify(guestCartRepository).removeSelectedBooks(eq(guestId), eq(List.of(bookId)));
	}

}