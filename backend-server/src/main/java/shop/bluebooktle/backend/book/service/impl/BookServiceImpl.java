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
import shop.bluebooktle.backend.book.entity.BookAuthor;
import shop.bluebooktle.backend.book.entity.BookPublisher;
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
import shop.bluebooktle.backend.book.service.BookImgService;
import shop.bluebooktle.backend.book.service.BookPublisherService;
import shop.bluebooktle.backend.book.service.BookService;
import shop.bluebooktle.backend.book.service.BookTagService;
import shop.bluebooktle.backend.book.service.PublisherService;
import shop.bluebooktle.common.dto.book.request.BookRegisterRequest;
import shop.bluebooktle.common.dto.book.request.BookUpdateServiceRequest;
import shop.bluebooktle.common.dto.book.response.AdminBookResponse;
import shop.bluebooktle.common.dto.book.response.BookAllResponse;
import shop.bluebooktle.common.dto.book.response.BookCartOrderResponse;
import shop.bluebooktle.common.dto.book.response.BookInfoResponse;
import shop.bluebooktle.common.dto.book.response.BookResponse;
import shop.bluebooktle.common.dto.book.response.CategoryResponse;
import shop.bluebooktle.common.dto.book.response.PublisherInfoResponse;
import shop.bluebooktle.common.dto.book.response.TagInfoResponse;
import shop.bluebooktle.common.dto.book.response.author.AuthorResponse;
import shop.bluebooktle.common.dto.book.response.BookDetailResponse;
import shop.bluebooktle.common.dto.book.response.BookInfoResponse;
import shop.bluebooktle.common.dto.book.response.BookRegisterResponse;
import shop.bluebooktle.common.dto.book.response.BookUpdateResponse;
import shop.bluebooktle.common.exception.book.BookAlreadyExistsException;
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
	private final BookImgService bookImgService;

	// 도서 상세 조회를 위한 메소드
	@Override
	@Transactional(readOnly = true)
	public BookDetailResponse findBookById(Long bookId) {
		// 도서 조회
		Book book = bookRepository.findById(bookId)
			.orElseThrow(BookNotFoundException::new);

		// 도서 판매 정보 조회
		BookSaleInfo saleInfo = bookSaleInfoRepository.findByBook(book)
			.orElseThrow(BookNotFoundException::new);

		// 작가 목록 조회
		List<BookAuthor> bookAuthors = bookAuthorRepository.findByBook_Id(bookId);
		List<String> authors = bookAuthors.stream()
			.map(bookAuthor -> bookAuthor.getAuthor().getName())
			.toList();

		// 출판사 목록 조회
		List<BookPublisher> bookPublishers = bookPublisherRepository.findByBook_Id(bookId);
		List<String> publisher = bookPublishers.stream()
			.map(bookPublisher -> bookPublisher.getPublisher().getName())
			.toList();

		// 썸네일 URL 가져오기
		String imgUrl = book.getBookImgs().stream()
			.filter(b1 -> b1.isThumbnail())
			.findFirst()
			.map(b1 -> b1.getImg().getImgUrl())
			.orElse("");

		return BookDetailResponse.builder()
			.isbn(book.getIsbn())
			.title(book.getTitle())
			.authors(authors)
			.publishers(publisher)
			.price(saleInfo.getPrice())
			.salePrice(saleInfo.getSalePrice())
			.salePercentage(saleInfo.getSalePercentage().intValue())
			.description(book.getDescription())
			.index(book.getIndex())
			.imgUrl(imgUrl)
			.saleState(saleInfo.getBookSaleInfoState())
			.build();
	}

	@Override
	public void updateBook(Long bookId, BookUpdateServiceRequest request) {

		Book book = bookRepository.findById(bookId)
			.orElseThrow(BookNotFoundException::new);

		BigDecimal salePercentage = request.getPrice().subtract(request.getSalePrice())
			.divide(request.getPrice(), 2, BigDecimal.ROUND_HALF_UP)
			.multiply(BigDecimal.valueOf(100));
		book.setTitle(request.getTitle());
		book.setDescription(request.getDescription());
		book.setIndex(request.getIndex());
		book.setPublishDate(request.getPublishDate() != null ?
			request.getPublishDate().atStartOfDay() : null);

		bookRepository.save(book);

		BookSaleInfo bookSaleInfo = bookSaleInfoRepository.findByBook(book).orElseThrow(BookNotFoundException::new);
		BookSaleInfo updatedBookSaleInfo = BookSaleInfo.builder()
			.id(bookSaleInfo.getId())
			.book(book)
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
		bookCategoryService.updateBookCategory(bookId, request.getCategoryIdList());// 카테고리
		if (request.getTagIdList() != null && !request.getTagIdList().isEmpty()) {
			bookTagService.updateBookTag(bookId, request.getTagIdList()); // 태그
		}
		if (request.getImgUrl() != null && !request.getImgUrl().isBlank()) {
			bookImgService.updateBookImg(bookId, request.getImgUrl()); // 이미지
		}

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
			.publishDate(book.getPublishDate())           // 출판일
			.index(book.getIndex())
			.isbn(book.getIsbn())                         // ISBN
			.price(saleInfo.getPrice())                   // 정가
			.salePrice(saleInfo.getSalePrice())           // 할인가
			.stock(saleInfo.getStock())                   // 재고
			.salePercentage(saleInfo.getSalePercentage()) // 할인율
			.imgUrl(getThumbnailUrlByBookId(book.getId()))    // 썸네일 URL
			.isPackable(saleInfo.isPackable())
			.authors(getAuthorsByBookId(book.getId()))               // 저자 리스트
			.publishers(getPublisherByBookId(book.getId()))           // 출판사 이름
			.categories(getCategoriesByBookId(book.getId()))         // 카테고리 리스트
			.tags(getTagsByBookId(book.getId()))                     // 태그 리스트
			.bookSaleInfoState(saleInfo.getBookSaleInfoState())
			.viewCount(saleInfo.getViewCount())                      // 조회수
			.searchCount(saleInfo.getSearchCount())// 검색수
			.reviewCount(saleInfo.getReviewCount())
			.star(saleInfo.getStar())
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
	public Page<AdminBookResponse> findAllBooksByAdmin(int page, int size, String searchKeyword) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
		Page<Book> bookPage;

		if (StringUtils.hasText(searchKeyword)) {
			bookPage = bookRepository.findByTitleContainingIgnoreCase(searchKeyword, pageable);
		} else {
			bookPage = bookRepository.findAll(pageable);
			log.info("{}", bookPage);
		}

		List<AdminBookResponse> content = bookPage.getContent().stream()
			.map(book -> {
				BookSaleInfo bookSaleInfo = bookSaleInfoRepository.findByBook(book)
					.orElseThrow(BookNotFoundException::new);

				List<String> authorNameList = bookAuthorRepository.findByBookId(book.getId()).stream()
					.map(bookAuthor -> bookAuthor.getAuthor().getName())
					.toList();

				List<String> publisherNameList = bookPublisherRepository.findByBookId(book.getId()).stream()
					.map(bookPublisher -> bookPublisher.getPublisher().getName())
					.toList();

				return new AdminBookResponse(
					book.getId(),
					book.getIsbn(),
					book.getTitle(),
					authorNameList,
					publisherNameList,
					bookSaleInfo.getBookSaleInfoState(),
					bookSaleInfo.getSalePrice(),
					bookSaleInfo.getStock(),
					book.getPublishDate()
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
			getCategorieNameByBookId(book.getId()),
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

	private List<AuthorResponse> getAuthorsByBookId(Long bookId) {
		return bookAuthorRepository.findByBookId(bookId)
			.stream()
			.map(bookAuthor -> new AuthorResponse(
				bookAuthor.getAuthor().getId(),
				bookAuthor.getAuthor().getName(),
				bookAuthor.getAuthor().getCreatedAt()
			))
			.toList();
	}

	private List<PublisherInfoResponse> getPublisherByBookId(Long bookId) {
		return bookPublisherRepository.findByBookId(bookId)
			.stream()
			.map(bookPublisher -> new PublisherInfoResponse(
					bookPublisher.getPublisher().getId(),
					bookPublisher.getPublisher().getName(),
					bookPublisher.getPublisher().getCreatedAt()
				)
			)
			.toList();
	}

	private List<CategoryResponse> getCategoriesByBookId(Long bookId) {
		return bookCategoryRepository.findByBook_Id(bookId)
			.stream()
			.map(bookCategory -> new CategoryResponse(
					bookCategory.getCategory().getId(),
					bookCategory.getCategory().getName(),
					bookCategory.getCategory().getParentCategory().getName(),
					bookCategory.getCategory().getCategoryPath()
				)
			)
			.toList();
	}

	private List<String> getCategorieNameByBookId(Long bookId) {
		return bookCategoryRepository.findByBook_Id(bookId)
			.stream()
			.map(bookCategory -> bookCategory.getCategory().getName())
			.toList();
	}

	private List<TagInfoResponse> getTagsByBookId(Long bookId) {
		return bookTagRepository.findByBookId(bookId)
			.stream()
			.map(bookTag -> new TagInfoResponse(
					bookTag.getTag().getId(),
					bookTag.getTag().getName(),
					bookTag.getTag().getCreatedAt()
				)
			)
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
