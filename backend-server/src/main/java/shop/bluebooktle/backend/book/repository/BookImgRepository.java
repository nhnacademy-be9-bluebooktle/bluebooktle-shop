package shop.bluebooktle.backend.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.book.entity.BookImg;

public interface BookImgRepository extends JpaRepository<BookImg, Long> {
}
