package shop.bluebooktle.backend.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.book.entity.BookAuthor;

public interface BookAuthorRepository extends JpaRepository<BookAuthor, Long> {
}
