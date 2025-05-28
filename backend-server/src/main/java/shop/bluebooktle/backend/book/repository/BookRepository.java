package shop.bluebooktle.backend.book.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import shop.bluebooktle.backend.book.entity.Book;

public interface BookRepository extends JpaRepository<Book, Long> {
	//제목으로 모든 도서 조회
	//쿼리 impl로 빼기!!
	@Query("SELECT b FROM Book b WHERE b.deletedAt IS NULL AND b.title LIKE %:title%")
	List<Book> findAllByTitle(@Param("title") String title);

	Optional<Book> findByIsbn(String isbn);

	boolean existsByIsbn(String isbn);

	Page<Book> findByTitleContainingIgnoreCase(String title, Pageable pageable);

	//메인페이지에 표시될 정보(id, title, author, price, salePrice, imgUrl) 조회
	//쿼리 impl로 빼기!!
	// @Query("SELECT new shop.bluebooktle.common.dto.book.response.book.BookInfoResponse( " +
	// 	"b.id, b.title, " +
	// 	"a.name, " +                // 작가 이름
	// 	"bs.price, " +              // 정가
	// 	"bs.salePrice, " +          // 할인가
	// 	"bi.img.imgUrl) " +         // 썸네일 URL
	// 	"FROM Book b " +
	// 	"JOIN BookAuthor ba ON ba.book.id = b.id " +   // BookAuthor 매핑을 통해 작가 연결
	// 	"JOIN Author a ON ba.author.id = a.id " +     // Author 매핑
	// 	"JOIN BookSaleInfo bs ON bs.book.id = b.id " + // BookSaleInfo 연결
	// 	"JOIN BookImg bi ON bi.book.id = b.id AND bi.isThumbnail = true")
	// Page<BookInfoResponse> findBooksForMainPage(@Param("bookId") Long bookId, Pageable pageable);

	//제목으로 검색하여 표시될 정보(id, title, author, price, salePrice, imgUrl) 조회
	//쿼리 impl로 빼기!!
	// @Query("SELECT new shop.bluebooktle.common.dto.book.response.book.BookInfoResponse(" +
	// 	"b.id, b.title, a.name, bs.price, bs.salePrice, bi.img.imgUrl) " +
	// 	"FROM Book b " +
	// 	"JOIN BookAuthor ba ON ba.book.id = b.id " +
	// 	"JOIN Author a ON ba.author.id = a.id " +
	// 	"JOIN BookSaleInfo bs ON bs.book.id = b.id " +
	// 	"JOIN BookImg bi ON bi.book.id = b.id AND bi.isThumbnail = true " +
	// 	"WHERE b.deletedAt IS NULL AND b.title LIKE %:title%")
	// Page<BookInfoResponse> findBooksForSearchPageBytitle(@Param("title") String title, Pageable pageable);
}
