package shop.bluebooktle.backend.book.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import shop.bluebooktle.backend.book.entity.Book;

public interface BookRepository extends JpaRepository<Book, Long> {
	@Query("SELECT b FROM Book b WHERE b.deletedAt IS NULL AND b.title LIKE %:title%")
	List<Book> findAllByTitle(@Param("title") String title);

	Optional<Book> findByIsbn(String isbn);

	boolean existsByIsbn(String isbn);

}
