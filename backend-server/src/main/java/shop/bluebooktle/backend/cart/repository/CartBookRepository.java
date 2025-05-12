package shop.bluebooktle.backend.cart.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.cart.entity.Cart;
import shop.bluebooktle.backend.cart.entity.CartBook;

public interface CartBookRepository extends JpaRepository<CartBook, Long> {

	Optional<CartBook> findByCartAndBook(Cart cart, Book book);

	@Modifying
	@Query("UPDATE CartBook cb SET cb.quantity = cb.quantity + :quantity WHERE cb.cart = :cart AND cb.book = :book")
	void addQuantity(@Param("cart") Cart cart, @Param("book") Book book, @Param("quantity") int quantity);
}
