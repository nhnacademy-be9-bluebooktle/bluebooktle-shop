package shop.bluebooktle.backend.cart.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.cart.entity.Cart;
import shop.bluebooktle.common.entity.auth.User;

public interface CartRepository extends JpaRepository<Cart, Long> {

	// 유저 기준으로 장바구니 조회
	@EntityGraph(attributePaths = {"cartBooks", "cartBooks.book"})
	Optional<Cart> findByUser(User user);

	// Cart 저장
	Cart save(Cart cart);
}