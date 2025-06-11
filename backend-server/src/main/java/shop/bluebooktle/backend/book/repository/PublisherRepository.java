package shop.bluebooktle.backend.book.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.book.entity.Publisher;

public interface PublisherRepository extends JpaRepository<Publisher, Long>, PublisherQueryRepository {

	// 출판사 전체 이름으로 조회 (정확하게 일치하는 이름)
	Optional<Publisher> findByName(String name);

	boolean existsByName(String name);
}
