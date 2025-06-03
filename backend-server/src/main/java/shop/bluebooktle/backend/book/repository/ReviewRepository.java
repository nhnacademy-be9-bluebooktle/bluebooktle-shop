package shop.bluebooktle.backend.book.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.book.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

	//내가 쓴 리뷰 목록
	Page<Review> findAllByUserId(Long userId, Pageable pageable);
}
