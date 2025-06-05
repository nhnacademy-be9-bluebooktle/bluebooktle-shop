package shop.bluebooktle.backend.cart.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.BookSaleInfo;
import shop.bluebooktle.backend.book.repository.BookCategoryRepository;
import shop.bluebooktle.backend.book.repository.BookImgRepository;
import shop.bluebooktle.backend.book.repository.BookRepository;
import shop.bluebooktle.backend.book.repository.BookSaleInfoRepository;
import shop.bluebooktle.backend.cart.entity.Cart;
import shop.bluebooktle.backend.cart.entity.CartBook;
import shop.bluebooktle.backend.cart.repository.CartBookRepository;
import shop.bluebooktle.backend.cart.repository.CartRepository;
import shop.bluebooktle.backend.cart.repository.redis.GuestCartRepository;
import shop.bluebooktle.backend.cart.service.CartService;
import shop.bluebooktle.backend.user.repository.UserRepository;
import shop.bluebooktle.common.dto.book.response.BookCartOrderResponse;
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.exception.auth.UserNotFoundException;
import shop.bluebooktle.common.exception.book.BookNotFoundException;
import shop.bluebooktle.common.exception.cart.CartNotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

	private final CartRepository cartRepository;
	private final CartBookRepository cartBookRepository;
	private final BookRepository bookRepository;
	private final GuestCartRepository guestCartRepository;
	private final UserRepository userRepository;
	private final BookSaleInfoRepository bookSaleInfoRepository;
	private final BookCategoryRepository bookCategoryRepository;
	private final BookImgRepository bookImgRepository;

	// ----------------- 회원용 -----------------

	// 전달받은 UserId로 User 객체로 반환하기 위한 메서드
	@Override
	@Transactional
	public User findUserEntityById(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(UserNotFoundException::new);
	}

	// User 카트로 Book과 Quantity 정보 담기
	@Override
	@Transactional
	public void addBookToUserCart(User user, Long bookId, int quantity) {
		Cart cart = getOrCreateCart(user);
		Book book = bookRepository.findById(bookId)
			.orElseThrow(BookNotFoundException::new);

		cartBookRepository.findByCartAndBook(cart, book)
			.ifPresentOrElse(
				cartBook -> cartBook.increaseQuantity(quantity),
				() -> cartBookRepository.save(CartBook.of(book, cart, quantity))
			);
	}

	// User의 카트에 있는 모든 책의 정보를 반환 (View에 필요한 모든 정보를 담은 DTO로 반환)
	@Override
	@Transactional(readOnly = true)
	public List<BookCartOrderResponse> getUserCartItems(User user) {
		Cart cart = getOrCreateCart(user);

		return cart.getCartBooks().stream()
			.map(cartBook -> {
				Book book = cartBook.getBook();
				BookSaleInfo saleInfo = getBookSaleInfoByBookId(book.getId());

				return BookCartOrderResponse.builder()
					.bookId(book.getId())
					.title(book.getTitle())
					.price(saleInfo.getPrice())
					.salePrice(saleInfo.getSalePrice())
					.thumbnailUrl(getThumbnailUrlByBookId(book.getId()))
					.categories(getCategoriesByBookId(book.getId()))
					.isPackable(saleInfo.isPackable())
					.quantity(cartBook.getQuantity())
					.build();
			})
			.toList();
	}

	// 책의 수량 증가하는 메서드
	@Override
	@Transactional
	public void increaseUserQuantity(User user, Long bookId, int quantity) {
		Cart cart = cartRepository.findByUser(user)
			.orElseGet(() -> cartRepository.save(new Cart(user)));
		Book book = bookRepository.findById(bookId)
			.orElseThrow(BookNotFoundException::new);

		cartBookRepository.findByCartAndBook(cart, book)
			.ifPresentOrElse(
				cartBook -> cartBook.increaseQuantity(quantity), // ← 객체 메서드 호출 (OK)
				() -> cartBookRepository.save(CartBook.of(book, cart, quantity))
			);
	}

	// 책의 수량 감소하는 메서드
	@Override
	public void decreaseUserQuantity(User user, Long bookId, int quantity) {
		Cart cart = cartRepository.findByUser(user)
			.orElseThrow(CartNotFoundException::new);
		Book book = bookRepository.findById(bookId)
			.orElseThrow(BookNotFoundException::new);

		cartBookRepository.findByCartAndBook(cart, book)
			.ifPresent(cartBook -> cartBook.decreaseQuantity(quantity));
	}

	// User 카트에서 단일 책 삭제
	@Override
	public void removeBookFromUserCart(User user, Long bookId) {
		Cart cart = cartRepository.findByUser(user)
			.orElseThrow(CartNotFoundException::new);
		Book book = bookRepository.findById(bookId)
			.orElseThrow(BookNotFoundException::new);

		CartBook cartBook = cartBookRepository.findByCartAndBook(cart, book)
			.orElseThrow(() -> new RuntimeException("CartBook not found"));

		cartBook.setDeletedAt();
	}

	// User 카트에서 요청된 List에 담겨있는 책들 삭제
	@Override
	@Transactional
	public void removeSelectedBooksFromUserCart(User user, List<Long> bookIds) {
		Cart cart = cartRepository.findByUser(user)
			.orElseThrow(CartNotFoundException::new);

		List<CartBook> cartBooks = cartBookRepository.findAllByCartAndBookIdIn(cart, bookIds);

		cartBooks.forEach(CartBook::setDeletedAt);
	}

	// 선택된 도서 주문으로 넘기기 메서드
	@Override
	@Transactional(readOnly = true)
	public List<BookCartOrderResponse> sendSelectedCartItemsToOrder(User user, List<Long> bookIds) {
		Cart cart = cartRepository.findByUser(user)
			.orElseThrow(UserNotFoundException::new);

		return cart.getCartBooks().stream()
			.filter(cartBook -> bookIds.contains(cartBook.getBook().getId()))
			.map(cartBook -> {
				Book book = cartBook.getBook();
				BookSaleInfo saleInfo = getBookSaleInfoByBookId(book.getId());

				return BookCartOrderResponse.builder()
					.bookId(book.getId())
					.title(book.getTitle())
					.price(saleInfo.getPrice())
					.salePrice(saleInfo.getSalePrice())
					.thumbnailUrl(getThumbnailUrlByBookId(book.getId()))
					.categories(getCategoriesByBookId(book.getId()))
					.isPackable(saleInfo.isPackable())
					.quantity(cartBook.getQuantity())
					.build();
			})
			.toList();
	}

	@Override
	public Long getCartSize(User user) {
		Cart cart = getOrCreateCart(user);
		if (cart == null) {
			return 0L;
		}
		return cartBookRepository.countByCart(cart);
	}

	// ----------------- 비회원용 -----------------

	// 비회원 카트에 책 담기
	@Override
	@Transactional
	public void addBookToGuestCart(String guestId, Long bookId, int quantity) {
		guestCartRepository.addBook(guestId, bookId, quantity);
	}

	// 비회원 카트에 담겨있는 모든 책 정보 반환 (View에 필요한 모든 정보 담은 dto로 반환)
	@Override
	@Transactional(readOnly = true)
	public List<BookCartOrderResponse> getGuestCartItems(String guestId) {
		Map<Long, Integer> guestCart = guestCartRepository.getCart(guestId);

		return guestCart.entrySet().stream()
			.map(entry -> {
				Long bookId = entry.getKey();
				int quantity = entry.getValue();

				Book book = bookRepository.findById(bookId)
					.orElseThrow(BookNotFoundException::new);
				BookSaleInfo saleInfo = getBookSaleInfoByBookId(bookId);

				return BookCartOrderResponse.builder()
					.bookId(book.getId())
					.title(book.getTitle())
					.price(saleInfo.getPrice())
					.salePrice(saleInfo.getSalePrice())
					.thumbnailUrl(getThumbnailUrlByBookId(bookId))
					.categories(getCategoriesByBookId(bookId))
					.isPackable(saleInfo.isPackable())
					.quantity(quantity)
					.build();
			})
			.toList();
	}

	// 책 수량 증가 메서드
	@Override
	@Transactional
	public void increaseGuestQuantity(String guestId, Long bookId, int quantity) {
		guestCartRepository.increaseQuantity(guestId, bookId, quantity);
	}

	// 책 수량 감소 메서드
	@Override
	@Transactional
	public void decreaseGuestQuantity(String guestId, Long bookId, int quantity) {
		guestCartRepository.decreaseQuantity(guestId, bookId, quantity);
	}

	// 카트에서 단일 책 삭제 메서드
	@Override
	@Transactional
	public void removeBookFromGuestCart(String guestId, Long bookId) {
		guestCartRepository.removeBook(guestId, bookId);
	}

	// 카트에서 요청된 List에 있는 책들 삭제
	@Override
	@Transactional
	public void removeSelectedBooksFromGuestCart(String guestId, List<Long> bookIds) {
		guestCartRepository.removeSelectedBooks(guestId, bookIds);
	}

	// 선택된 도서 주문으로 넘기기 메서드
	@Override
	@Transactional(readOnly = true)
	public List<BookCartOrderResponse> sendSelectedGuestCartItemsToOrder(String guestId, List<Long> bookIds) {
		Map<Long, Integer> guestCart = guestCartRepository.getCart(guestId);

		return guestCart.entrySet().stream()
			.filter(entry -> bookIds.contains(entry.getKey()))
			.map(entry -> {
				Long bookId = entry.getKey();
				int quantity = entry.getValue();
				Book book = bookRepository.findById(bookId)
					.orElseThrow(BookNotFoundException::new);
				BookSaleInfo saleInfo = getBookSaleInfoByBookId(bookId);

				return BookCartOrderResponse.builder()
					.bookId(bookId)
					.title(book.getTitle())
					.price(saleInfo.getPrice())
					.salePrice(saleInfo.getSalePrice())
					.thumbnailUrl(getThumbnailUrlByBookId(bookId))
					.categories(getCategoriesByBookId(bookId))
					.isPackable(saleInfo.isPackable())
					.quantity(quantity)
					.build();
			})
			.toList();
	}

	@Override
	public Long getGuestCartSize(String guestId) {
		return guestCartRepository.getCartSize(guestId);
	}

	// ----------------- 공통 -----------------

	// User의 카트가 존재하면 Cart반환, 없으면 생성
	private Cart getOrCreateCart(User user) {
		return cartRepository.findByUser(user)
			.orElseGet(() -> cartRepository.save(
				Cart.builder()
					.user(user)
					.build()
			));
	}

	// 비회원 카트에서 회원카트로 정보 이전 메서드
	@Override
	@Transactional
	public void mergeOrConvertGuestCartToMemberCart(String guestId, User user) {
		Map<Long, Integer> guestCart = guestCartRepository.getCart(guestId);
		if (guestCart.isEmpty()) {
			return;
		}

		Cart cart = cartRepository.findByUser(user).orElse(null);

		// 장바구니 없으면 -> 생성 후 전환 (convert)
		if (cart == null) {
			cart = cartRepository.save(Cart.builder().user(user).build());

			List<CartBook> cartBooks = new ArrayList<>();
			for (Map.Entry<Long, Integer> entry : guestCart.entrySet()) {
				Long bookId = entry.getKey();
				int quantity = entry.getValue();
				Book book = bookRepository.findById(bookId)
					.orElseThrow(BookNotFoundException::new);
				cartBooks.add(CartBook.of(book, cart, quantity));
			}
			cartBookRepository.saveAll(cartBooks);
		}

		// 장바구니 있으면 -> 병합 (merge)
		else {
			for (Map.Entry<Long, Integer> entry : guestCart.entrySet()) {
				Long bookId = entry.getKey();
				int guestQuantity = entry.getValue();
				Book book = bookRepository.findById(bookId)
					.orElseThrow(BookNotFoundException::new);

				Optional<CartBook> optionalCartBook = cartBookRepository.findByCartAndBook(cart, book);
				if (optionalCartBook.isPresent()) {
					CartBook existing = optionalCartBook.get();
					existing.increaseQuantity(guestQuantity);
				} else {
					cartBookRepository.save(CartBook.of(book, cart, guestQuantity));
				}
			}
		}

		// Redis 비우기 (공통 처리)
		guestCartRepository.removeSelectedBooks(guestId, new ArrayList<>(guestCart.keySet()));
	}

	// 책의 카테고리 정보 가져오기 메서드
	private List<String> getCategoriesByBookId(Long bookId) {
		return bookCategoryRepository.findByBook_Id(bookId)
			.stream()
			.map(bookCategory -> bookCategory.getCategory().getName())
			.toList();
	}

	// 책의 썸네일 주소 가져오기 메서드
	private String getThumbnailUrlByBookId(Long bookId) {
		return bookImgRepository.findByBookId(bookId)
			.stream()
			.map(bookImg -> bookImg.getImg().getImgUrl())
			.findFirst()
			.orElse("기본 썸네일url"); // 없으면 기본 썸네일 URL 반환
	}

	// 책의 판매정보 객체를 가져오는 메서드
	private BookSaleInfo getBookSaleInfoByBookId(Long bookId) {
		return bookSaleInfoRepository.findByBookId(bookId)
			.orElseThrow(BookNotFoundException::new);
	}

}