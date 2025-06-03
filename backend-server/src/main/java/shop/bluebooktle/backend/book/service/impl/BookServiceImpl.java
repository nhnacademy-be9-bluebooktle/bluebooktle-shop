package shop.bluebooktle.backend.book.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.BookSaleInfo;
import shop.bluebooktle.backend.book.repository.BookAuthorRepository;
import shop.bluebooktle.backend.book.repository.BookCategoryRepository;
import shop.bluebooktle.backend.book.repository.BookImgRepository;
import shop.bluebooktle.backend.book.repository.BookPublisherRepository;
import shop.bluebooktle.backend.book.repository.BookRepository;
import shop.bluebooktle.backend.book.repository.BookSaleInfoRepository;
import shop.bluebooktle.backend.book.repository.BookTagRepository;
import shop.bluebooktle.backend.book.service.AuthorService;
import shop.bluebooktle.backend.book.service.BookAuthorService;
import shop.bluebooktle.backend.book.service.BookCategoryService;
import shop.bluebooktle.backend.book.service.BookPublisherService;
import shop.bluebooktle.backend.book.service.BookService;
import shop.bluebooktle.backend.book.service.BookTagService;
import shop.bluebooktle.backend.book.service.PublisherService;
import shop.bluebooktle.common.dto.book.request.BookRegisterRequest;
import shop.bluebooktle.common.dto.book.request.BookUpdateRequest;
import shop.bluebooktle.common.dto.book.response.BookAllResponse;
import shop.bluebooktle.common.dto.book.response.BookCartOrderResponse;
import shop.bluebooktle.common.dto.book.response.BookInfoResponse;
import shop.bluebooktle.common.dto.book.response.BookResponse;
import shop.bluebooktle.common.exception.book.BookNotFoundException;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class BookServiceImpl implements BookService {

	private final BookRepository bookRepository;
	private final BookSaleInfoRepository bookSaleInfoRepository;
	private final BookAuthorRepository bookAuthorRepository;
	private final BookPublisherRepository bookPublisherRepository;
	private final BookCategoryRepository bookCategoryRepository;
	private final BookTagRepository bookTagRepository;
	private final BookImgRepository bookImgRepository;

	private final BookPublisherService bookPublisherService;
	private final BookCategoryService bookCategoryService;
	private final BookAuthorService bookAuthorService;
	private final AuthorService authorService;
	private final PublisherService publisherService;
	private final BookTagService bookTagService;

	@Override
	@Transactional(readOnly = true)
	public BookResponse findBookById(Long bookId) {
		Book book = bookRepository.findById(bookId)
			.orElseThrow(BookNotFoundException::new);

		return BookResponse.builder()
			.title(book.getTitle())
			.description(book.getDescription())
			.index(book.getIndex())
			.publishDate(LocalDate.from(book.getPublishDate()))
			.isbn(book.getIsbn())
			.build();
	}

	@Override
	public void updateBook(Long bookId, BookUpdateRequest request) {

		Book book = bookRepository.findById(bookId)
			.orElseThrow(BookNotFoundException::new);

		BigDecimal salePercentage = request.getPrice().subtract(request.getSalePrice())
			.divide(request.getPrice(), 2, BigDecimal.ROUND_HALF_UP)
			.multiply(BigDecimal.valueOf(100));

		Book updatedBook = Book.builder()
			.id(book.getId())
			.title(request.getTitle())
			.description(request.getDescription())
			.publishDate(request.getPublishDate() != null ?
				request.getPublishDate().atStartOfDay() : null)
			.isbn(book.getIsbn()) // isbn은 수정 불가
			.build();

		bookRepository.save(updatedBook);

		BookSaleInfo bookSaleInfo = bookSaleInfoRepository.findByBook(book).orElseThrow(BookNotFoundException::new);
		BookSaleInfo updatedBookSaleInfo = BookSaleInfo.builder()
			.id(bookSaleInfo.getId())
			.book(updatedBook)
			.price(request.getPrice())
			.salePrice(request.getSalePrice())
			.stock(request.getStock())
			.isPackable(request.getIsPackable())
			.salePercentage(salePercentage)
			.bookSaleInfoState(request.getState())
			.viewCount(bookSaleInfo.getViewCount())
			.searchCount(bookSaleInfo.getSearchCount())
			.star(bookSaleInfo.getStar())
			.reviewCount(bookSaleInfo.getReviewCount())
			.build();
		bookSaleInfoRepository.save(updatedBookSaleInfo);

		bookAuthorService.updateBookAuthor(bookId, request.getAuthorIdList()); // 작가
		bookPublisherService.updateBookPublisher(bookId, request.getPublisherIdList()); // 출판사
		bookCategoryService.updateBookCategory(bookId, request.getCategoryIdList()); // 카테고리
		bookTagService.updateBookTag(bookId, request.getTagIdList()); // 태그

	}

	@Override
	public void deleteBook(Long bookId) {
		// Book 존재 여부 확인
		Book book = bookRepository.findById(bookId)
			.orElseThrow(BookNotFoundException::new);

		// book 삭제시 관련 BookSaleInfo 함께 삭제
		bookSaleInfoRepository.findByBook(book).ifPresent(bookSaleInfoRepository::delete);

		bookRepository.delete(book);
	}

	//도서 연관 데이터 한번에 id로조회
	@Override
	@Transactional(readOnly = true)
	public BookAllResponse findBookAllById(Long id) {
		Book book = bookRepository.findById(id)
			.orElseThrow(BookNotFoundException::new);
		BookSaleInfo saleInfo = getBookSaleInfoByBookId(book.getId());

		return BookAllResponse.builder()
			.id(book.getId())                             // 책 ID
			.title(book.getTitle())                       // 책 제목
			.description(book.getDescription())           // 책 설명
			.index(book.getIndex())
			.publishDate(book.getPublishDate())           // 출판일
			.isbn(book.getIsbn())                         // ISBN
			.price(saleInfo.getPrice())                   // 정가
			.salePrice(saleInfo.getSalePrice())           // 할인가
			.stock(saleInfo.getStock())                   // 재고
			.salePercentage(saleInfo.getSalePercentage()) // 할인율
			.imgUrl(getThumbnailUrlByBookId(book.getId()))    // 썸네일 URL
			.authors(getAuthorsByBookId(book.getId()))               // 저자 리스트
			.publishers(getPublisherByBookId(book.getId()))           // 출판사 이름
			.categories(getCategoriesByBookId(book.getId()))         // 카테고리 리스트
			.tags(getTagsByBookId(book.getId()))                     // 태그 리스트
			.bookSaleInfoState(saleInfo.getBookSaleInfoState())
			.viewCount(saleInfo.getViewCount())                      // 조회수
			.searchCount(saleInfo.getSearchCount())                  // 검색수
			.build();
	}

	//도서 연관 데이터 한번에 제목으로 조회
	@Override
	@Transactional(readOnly = true)
	public List<BookAllResponse> getBookAllByTitle(String title) {
		List<Book> books = bookRepository.findAllByTitle(title);

		return books.stream()
			.map(book -> {
				BookSaleInfo saleInfo = getBookSaleInfoByBookId(book.getId());

				return BookAllResponse.builder()
					.id(book.getId())                             // 책 ID
					.title(book.getTitle())                       // 책 제목
					.description(book.getDescription())           // 책 설명
					.index(book.getIndex())
					.publishDate(book.getPublishDate())           // 출판일
					.isbn(book.getIsbn())                         // ISBN
					.price(saleInfo.getPrice())                   // 정가
					.salePrice(saleInfo.getSalePrice())           // 할인가
					.stock(saleInfo.getStock())                   // 재고
					.salePercentage(saleInfo.getSalePercentage()) // 할인율
					.imgUrl(getThumbnailUrlByBookId(book.getId()))    // 썸네일 URL
					.authors(getAuthorsByBookId(book.getId()))               // 저자 리스트
					.publishers(getPublisherByBookId(book.getId()))           // 출판사 이름
					.categories(getCategoriesByBookId(book.getId()))         // 카테고리 리스트
					.tags(getTagsByBookId(book.getId()))                     // 태그 리스트
					.bookSaleInfoState(saleInfo.getBookSaleInfoState())
					.viewCount(saleInfo.getViewCount())                      // 조회수
					.searchCount(saleInfo.getSearchCount())                  // 검색수
					.build();
			})
			.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public Page<BookInfoResponse> findAllBooks(int page, int size, String searchKeyword) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
		Page<Book> bookPage;

		if (StringUtils.hasText(searchKeyword)) {
			bookPage = bookRepository.findByTitleContainingIgnoreCase(searchKeyword, pageable);
		} else {
			bookPage = bookRepository.findAll(pageable);
			log.info("{}", bookPage);
		}

		List<BookInfoResponse> content = bookPage.getContent().stream()
			.map(book -> {
				BookSaleInfo bookSaleInfo = bookSaleInfoRepository.findByBook(book)
					.orElseThrow(BookNotFoundException::new);

				List<String> authorNameList = bookAuthorRepository.findByBookId(book.getId()).stream()
					.map(bookAuthor -> bookAuthor.getAuthor().getName())
					.toList();

				String imgUrl = bookImgRepository.findByBook(book).getImg().getImgUrl();
				return new BookInfoResponse(
					book.getId(),
					book.getTitle(),
					authorNameList,
					bookSaleInfo.getSalePrice(),
					bookSaleInfo.getPrice(),
					imgUrl,
					book.getCreatedAt(),
					bookSaleInfo.getStar(),
					bookSaleInfo.getReviewCount(),
					bookSaleInfo.getViewCount()
				);
			})
			.toList();

		return new PageImpl<>(content, pageable, bookPage.getTotalElements());
	}

	@Override
	@Transactional(readOnly = true)
	public BookCartOrderResponse getBookCartOrder(Long bookId, int quantity) {
		Book book = bookRepository.findById(bookId).orElseThrow(BookNotFoundException::new);
		BookSaleInfo saleInfo = getBookSaleInfoByBookId(book.getId());

		return new BookCartOrderResponse(
			bookId,
			book.getTitle(),
			saleInfo.getPrice(),
			saleInfo.getSalePrice(),
			getThumbnailUrlByBookId(bookId),
			getCategoriesByBookId(book.getId()),
			saleInfo.isPackable(),
			quantity);
	}

	// //메인페이지에 표시될 정보(id, title, author, price, salePrice, imgUrl) 조회
	// @Override
	// @Transactional(readOnly = true)
	// public Page<BookInfoResponse> getBooksForMainPage(Long bookId, Pageable pageable) {
	// 	return bookRepository.findBooksForMainPage(bookId, pageable);
	// }
	//
	// //제목으로 검색하여 표시될 정보(id, title, author, price, salePrice, imgUrl) 조회
	// @Override
	// @Transactional(readOnly = true)
	// public Page<BookInfoResponse> searchBooksByTitle(String title, Pageable pageable) {
	// 	return bookRepository.findBooksForSearchPageBytitle(title, pageable);
	// }

	private List<String> getAuthorsByBookId(Long bookId) {
		return bookAuthorRepository.findByBookId(bookId)
			.stream()
			.map(bookAuthor -> bookAuthor.getAuthor().getName())
			.toList();
	}

	private List<String> getPublisherByBookId(Long bookId) {
		return bookPublisherRepository.findByBookId(bookId)
			.stream()
			.map(bookPublisher -> bookPublisher.getPublisher().getName())
			.toList();
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
			.orElseThrow(BookNotFoundException::new);
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
