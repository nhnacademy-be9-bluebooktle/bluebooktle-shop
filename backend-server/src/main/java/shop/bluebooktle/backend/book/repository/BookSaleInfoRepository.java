package shop.bluebooktle.backend.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import shop.bluebooktle.backend.book.entity.BookSaleInfo;

@Repository
public interface BookSaleInfoRepository extends JpaRepository<BookSaleInfo,Long> {
}
