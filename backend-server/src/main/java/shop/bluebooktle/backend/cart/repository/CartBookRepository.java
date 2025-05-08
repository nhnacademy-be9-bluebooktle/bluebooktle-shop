package shop.bluebooktle.backend.cart.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.cart.entity.Cart;
import shop.bluebooktle.backend.cart.entity.CartBook;

public interface CartBookRepository extends JpaRepository<CartBook, Long> {

	List<CartBook> findByCart(Cart cart);

	Optional<CartBook> findByCartAndBook(Cart cart, Book book);

	boolean existsByCartAndBook(Cart cart, Book book);

	@Modifying
	@Query("UPDATE CartBook cb SET cb.quantity = cb.quantity + :amount WHERE cb.cart = :cart AND cb.book = :book")
	int addQuantity(Cart cart, Book book, int amount);

}