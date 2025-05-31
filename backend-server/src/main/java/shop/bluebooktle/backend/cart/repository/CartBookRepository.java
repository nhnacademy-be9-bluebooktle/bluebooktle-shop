package shop.bluebooktle.backend.cart.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.cart.entity.Cart;
import shop.bluebooktle.backend.cart.entity.CartBook;

public interface CartBookRepository extends JpaRepository<CartBook, Long> {

	// 특정 장바구니 안에 특정 도서가 있는지 조회
	Optional<CartBook> findByCartAndBook(Cart cart, Book book);

	// 특정 장바구니에 담긴 모든 항목 조회
	List<CartBook> findAllByCart(Cart cart);

	// 특정 장바구니에서 bookId 리스트로 항목 일괄 조회
	List<CartBook> findAllByCartAndBookIdIn(Cart cart, List<Long> bookIds);

	// 저장 (수량 증가 등)
	CartBook save(CartBook cartBook);
}
