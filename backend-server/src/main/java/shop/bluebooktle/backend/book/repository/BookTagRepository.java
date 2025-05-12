package shop.bluebooktle.backend.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.book.entity.BookTag;

public interface BookTagRepository extends JpaRepository<BookTag, Long> {
}
