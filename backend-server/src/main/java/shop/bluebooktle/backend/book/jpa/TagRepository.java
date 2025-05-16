package shop.bluebooktle.backend.book.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.book.entity.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {

	// 태그 전체 이름으로 조회 (정확하게 일치하는 이름)
	List<Tag> findByName(String name);

	// 태그 이름 부분 일치 조회
	List<Tag> findByNameContaining(String name);

	// 삭제된 태그만 조회
	List<Tag> findByDeletedAtIsNotNull();

	// 삭제된 태그 전체 이름으로 조회
	List<Tag> findByNameAndDeletedAtIsNotNull(String name);

	// 삭제된 태그 이름 부분 일치 조회
	List<Tag> findByNameContainingAndDeletedAtIsNotNull(String name);

	// 삭제되지 않은 태그 전체 조회
	List<Tag> findByDeletedAtIsNull();

	// 삭제되지 않은 태그 전체 이름으로 조회
	List<Tag> findByNameAndDeletedAtIsNull(String name);

	// 삭제되지 않은 태그 이름 부분 일치 조회
	List<Tag> findByNameContainingAndDeletedAtIsNull(String name);

	boolean existsByName(String name);
}
