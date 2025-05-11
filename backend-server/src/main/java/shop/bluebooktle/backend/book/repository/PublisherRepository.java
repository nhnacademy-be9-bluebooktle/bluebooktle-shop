package shop.bluebooktle.backend.book.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.book.entity.Publisher;

public interface PublisherRepository extends JpaRepository<Publisher, Long> {
	Optional<Publisher> findByName(String name);
}