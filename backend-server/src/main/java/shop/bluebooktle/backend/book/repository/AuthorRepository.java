package shop.bluebooktle.backend.book.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.book.entity.Author;

public interface AuthorRepository extends JpaRepository<Author, Long>, AuthorQueryRepository {

	// (작가 이름으로) 작가 조회
	Optional<Author> findByName(String name);

	boolean existsByName(String name);

}
