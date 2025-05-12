package shop.bluebooktle.backend.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.book.entity.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {
}
