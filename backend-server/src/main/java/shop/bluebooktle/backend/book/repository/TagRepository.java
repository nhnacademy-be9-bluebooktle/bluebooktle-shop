package shop.bluebooktle.backend.book.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.book.entity.Tag;

public interface TagRepository extends JpaRepository<Tag, Long>, TagQueryRepository {

	// 태그 전체 이름으로 조회 (정확하게 일치하는 이름)
	List<Tag> findByName(String name);

	boolean existsByName(String name);

	// 페이징 전체 조회 (deletedAt이 NULL인 것만)
	Page<Tag> findAllByDeletedAtIsNull(Pageable pageable);
}
