package shop.bluebooktle.backend.book.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.book.entity.Author;

public interface AuthorRepository extends JpaRepository<Author, Long> {
	Optional<Author> findByName(String name);
}
