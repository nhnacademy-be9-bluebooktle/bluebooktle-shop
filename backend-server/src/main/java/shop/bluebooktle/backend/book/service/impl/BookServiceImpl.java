package shop.bluebooktle.backend.book.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.dto.request.BookRegisterRequest;
import shop.bluebooktle.backend.book.dto.request.BookUpdateRequest;
import shop.bluebooktle.backend.book.dto.response.BookAllResponse;
import shop.bluebooktle.backend.book.dto.response.BookRegisterResponse;
import shop.bluebooktle.backend.book.dto.response.BookResponse;
import shop.bluebooktle.backend.book.dto.response.BookUpdateResponse;
import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.BookSaleInfo;
import shop.bluebooktle.backend.book.repository.BookAuthorRepository;
import shop.bluebooktle.backend.book.repository.BookCategoryRepository;
import shop.bluebooktle.backend.book.repository.BookImgRepository;
import shop.bluebooktle.backend.book.repository.BookPublisherRepository;
import shop.bluebooktle.backend.book.repository.BookRepository;
import shop.bluebooktle.backend.book.repository.BookSaleInfoRepository;
import shop.bluebooktle.backend.book.repository.BookTagRepository;
import shop.bluebooktle.backend.book.service.BookService;
import shop.bluebooktle.common.exception.book.BookAlreadyExistsException;
import shop.bluebooktle.common.exception.book.BookNotFoundException;

@Service

@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

	private final BookRepository bookRepository;
	private final BookSaleInfoRepository bookSaleInfoRepository;
	private final BookAuthorRepository bookAuthorRepository;
	private final BookPublisherRepository bookPublisherRepository;
	private final BookCategoryRepository bookCategoryRepository;
	private final BookTagRepository bookTagRepository;
	private final BookImgRepository bookImgRepository;

	@Transactional
	@Override
	public BookRegisterResponse registerBook(BookRegisterRequest request) {
		if (bookRepository.existsByIsbn(request.getIsbn())) {
			throw new BookAlreadyExistsException("이미 도서가 존재합니다 ISBN: " + request.getIsbn());
		}
		Book book = toEntity(request);
		bookRepository.save(book);

		return BookRegisterResponse.builder()
			.title(request.getTitle())
			.description(request.getDescription())
			.publishDate(request.getPublishDate())
			.isbn(request.getIsbn())
			.build();
	}

	@Transactional(readOnly = true)
	@Override
	public BookResponse findBookById(Long bookId) {
		Book book = bookRepository.findById(bookId)
			.orElseThrow(() -> new BookNotFoundException("도서를 찾을 수 없습니다 ID: " + bookId));

		return BookResponse.builder()
			.title(book.getTitle())
			.description(book.getDescription())
			.publishDate(LocalDate.from(book.getPublishDate()))
			.isbn(book.getIsbn())
			.build();
	}

	@Transactional
	@Override
	public BookUpdateResponse updateBook(Long bookId, BookUpdateRequest request) {
		Book book = bookRepository.findById(bookId)
			.orElseThrow(() -> new BookNotFoundException("도서를 찾을 수 없습니다 ID: " + bookId));

		Book updatedBook = Book.builder()
			.id(book.getId()) // ID는 기존 데이터 유지
			.title(request.getTitle()) // 수정된 값
			.description(request.getDescription()) // 수정된 값
			.publishDate(book.getPublishDate()) // 기존 데이터 유지
			.isbn(book.getIsbn()) // 기존 데이터 유지
			.build();

		bookRepository.save(updatedBook);

		return BookUpdateResponse.builder()
			.title(updatedBook.getTitle())
			.description(updatedBook.getDescription())
			.build();
	}

	@Transactional
	@Override
	public void deleteBook(Long bookId) {
		// Book 존재 여부 확인
		Book book = bookRepository.findById(bookId)
			.orElseThrow(() -> new BookNotFoundException("도서를 찾을 수 없습니다. ID: " + bookId));

		// book 삭제시 관련 BookSaleInfo 함께 삭제
		bookSaleInfoRepository.findByBook(book).ifPresent(bookSaleInfo -> {
			bookSaleInfoRepository.delete(bookSaleInfo);
		});

		bookRepository.delete(book);
	}

	//도서 연관 데이터 한번에 id로조회
	@Transactional(readOnly = true)
	@Override
	public BookAllResponse findBookAllById(Long id) {
		Book book = bookRepository.findById(id)
			.orElseThrow(() -> new BookNotFoundException("해당 도서를 찾을 수 없습니다. ID: " + id));
		BookSaleInfo saleInfo = getBookSaleInfoByBookId(book.getId());

		return BookAllResponse.builder()
			.id(book.getId())                             // 책 ID
			.title(book.getTitle())                       // 책 제목
			.description(book.getDescription())           // 책 설명
			.publishDate(book.getPublishDate())           // 출판일
			.isbn(book.getIsbn())                         // ISBN
			.price(saleInfo.getPrice())                   // 정가
			.salePrice(saleInfo.getSalePrice())           // 할인가
			.stock(saleInfo.getStock())                   // 재고
			.salePercentage(saleInfo.getSalePercentage()) // 할인율
			.thumbnailUrl(getThumbnailUrlByBookId(book.getId()))    // 썸네일 URL
			.authors(getAuthorsByBookId(book.getId()))               // 저자 리스트
			.publisher(getPublisherByBookId(book.getId()))           // 출판사 이름
			.categories(getCategoriesByBookId(book.getId()))         // 카테고리 리스트
			.tags(getTagsByBookId(book.getId()))                     // 태그 리스트
			.state(saleInfo.getState())
			.viewCount(saleInfo.getViewCount())                      // 조회수
			.searchCount(saleInfo.getSearchCount())                  // 검색수
			.build();
	}

	//도서 연관 데이터 한번에 제목으로 조회
	@Transactional(readOnly = true)
	@Override
	public List<BookAllResponse> getBookAllByTitle(String title) {
		List<Book> books = bookRepository.findAllByTitle(title);

		return books.stream()
			.map(book -> {
				BookSaleInfo saleInfo = getBookSaleInfoByBookId(book.getId());

				return BookAllResponse.builder()
					.id(book.getId())                             // 책 ID
					.title(book.getTitle())                       // 책 제목
					.description(book.getDescription())           // 책 설명
					.publishDate(book.getPublishDate())           // 출판일
					.isbn(book.getIsbn())                         // ISBN
					.price(saleInfo.getPrice())                   // 정가
					.salePrice(saleInfo.getSalePrice())           // 할인가
					.stock(saleInfo.getStock())                   // 재고
					.salePercentage(saleInfo.getSalePercentage()) // 할인율
					.thumbnailUrl(getThumbnailUrlByBookId(book.getId()))    // 썸네일 URL
					.authors(getAuthorsByBookId(book.getId()))               // 저자 리스트
					.publisher(getPublisherByBookId(book.getId()))           // 출판사 이름
					.categories(getCategoriesByBookId(book.getId()))         // 카테고리 리스트
					.tags(getTagsByBookId(book.getId()))                     // 태그 리스트
					.state(saleInfo.getState())
					.viewCount(saleInfo.getViewCount())                      // 조회수
					.searchCount(saleInfo.getSearchCount())                  // 검색수
					.build();
			})
			.toList();
	}

	@Transactional(readOnly = true)
	@Override
	public Book getBookById(Long bookId) {
		return bookRepository.findById(bookId)
			.orElseThrow(() -> new BookNotFoundException("해당 ID를 가진 책을 찾을 수 없습니다: " + bookId));
	}

	private List<String> getAuthorsByBookId(Long bookId) {
		return bookAuthorRepository.findByBookId(bookId)
			.stream()
			.map(bookAuthor -> bookAuthor.getAuthor().getName())
			.toList();
	}

	private String getPublisherByBookId(Long bookId) {
		return bookPublisherRepository.findByBookId(bookId)
			.stream()
			.map(bookPublisher -> bookPublisher.getPublisher().getName())
			.findFirst()
			.orElse(""); // 없으면 기본값으로 빈 문자열 반환
	}

	private List<String> getCategoriesByBookId(Long bookId) {
		return bookCategoryRepository.findByBook_Id(bookId)
			.stream()
			.map(bookCategory -> bookCategory.getCategory().getName())
			.toList();
	}

	private List<String> getTagsByBookId(Long bookId) {
		return bookTagRepository.findByBookId(bookId)
			.stream()
			.map(bookTag -> bookTag.getTag().getName())
			.toList();
	}

	private String getThumbnailUrlByBookId(Long bookId) {
		return bookImgRepository.findByBookId(bookId)
			.stream()
			.map(bookImg -> bookImg.getImg().getImgUrl())
			.findFirst()
			.orElse("기본 썸네일url"); // 없으면 기본 썸네일 URL 반환
	}

	private BookSaleInfo getBookSaleInfoByBookId(Long bookId) {
		return bookSaleInfoRepository.findByBookId(bookId)
			.orElseThrow(() -> new BookNotFoundException("도서 판매정보가 필요합니다. ID: " + bookId));
	}

	@Override
	public boolean existsBookById(Long id) {
		return bookRepository.existsById(id);
	}

	private Book toEntity(BookRegisterRequest request) {
		return Book.builder()
			.title(request.getTitle())
			.description(request.getDescription())
			.isbn(request.getIsbn())
			.publishDate(request.getPublishDate() != null ? request.getPublishDate().atStartOfDay() : null)
			.build();
	}
}
