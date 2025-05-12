package shop.bluebooktle.backend.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.book.entity.BookPublisher;

public interface BookPublisherRepository extends JpaRepository<BookPublisher, Long> {
}
