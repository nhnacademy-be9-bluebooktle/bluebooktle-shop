package shop.bluebooktle.backend.cart.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.cart.entity.Cart;
import shop.bluebooktle.backend.user.entity.User;

public interface CartRepository extends JpaRepository<Cart, Long> {

	@EntityGraph(attributePaths = {"cartBooks", "cartBooks.book"})
	Optional<Cart> findByUser(User user);

	boolean existsByUser(User user);

}
