package shop.bluebooktle.backend.book.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.book.entity.Book;

public interface BookRepository extends JpaRepository<Book, Long> {
	List<Book> findAllByTitle(String title);
}
