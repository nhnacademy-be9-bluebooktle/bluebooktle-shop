package shop.bluebooktle.backend.book.repository;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.book.entity.Publisher;

public interface PublisherRepository extends JpaRepository<Publisher, Long> {

	// 출판사 전체 이름으로 조회 (정확하게 일치하는 이름)
	Optional<Publisher> findByName(String name);

	// 출판사 이름 부분 일치 조회
	List<Publisher> findByNameContaining(String name);

	// 삭제된 출판사만 조회
	List<Publisher> findByDeletedAtIsNotNull();

	// 삭제된 출판사 전체 이름으로 조회
	List<Publisher> findByNameAndDeletedAtIsNotNull(String name);

	// 삭제된 출판사 이름 부분 일치 조회
	List<Publisher> findByNameContainingAndDeletedAtIsNotNull(String name);

	// 삭제되지 않은 출판사 전체 조회
	List<Publisher> findByDeletedAtIsNull();

	// 삭제되지 않은 출판사 전체 이름으로 조회
	List<Publisher> findByNameAndDeletedAtIsNull(String name);

	// 삭제되지 않은 출판사 이름 부분 일치 조회
	List<Publisher> findByNameContainingAndDeletedAtIsNull(String name);
}