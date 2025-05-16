package shop.bluebooktle.backend.book.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.book.entity.Author;

public interface AuthorRepository extends JpaRepository<Author, Long> {

	// 작가 전체 이름으로 조회 (정확하게 일치하는 이름)
	Optional<Author> findByName(String name);

	// 작가 이름 부분 일치 조회
	List<Author> findByNameContaining(String name);

	// 삭제된 작가만 조회
	List<Author> findByDeletedAtIsNotNull();

	// 삭제된 작가 전체 이름으로 조회
	List<Author> findByNameAndDeletedAtIsNotNull(String name);

	// 삭제된 작가 이름 부분 일치 조회
	List<Author> findByNameContainingAndDeletedAtIsNotNull(String name);

	// 삭제되지 않은 작가 전체 조회
	List<Author> findByDeletedAtIsNull();

	// 삭제되지 않은 작가 전체 이름으로 조회
	List<Author> findByNameAndDeletedAtIsNull(String name);

	// 삭제되지 않은 작가 이름 부분 일치 조회
	List<Author> findByNameContainingAndDeletedAtIsNull(String name);
}
