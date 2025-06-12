package shop.bluebooktle.backend.book.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import shop.bluebooktle.backend.book.entity.Author;
import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.BookAuthor;
import shop.bluebooktle.backend.book.entity.BookCategory;
import shop.bluebooktle.backend.book.entity.BookImg;
import shop.bluebooktle.backend.book.entity.BookPublisher;
import shop.bluebooktle.backend.book.entity.BookSaleInfo;
import shop.bluebooktle.backend.book.entity.BookTag;
import shop.bluebooktle.backend.book.entity.Category;
import shop.bluebooktle.backend.book.entity.Img;
import shop.bluebooktle.backend.book.entity.Publisher;
import shop.bluebooktle.backend.book.entity.Tag;
import shop.bluebooktle.backend.book.repository.BookAuthorRepository;
import shop.bluebooktle.backend.book.repository.BookCategoryRepository;
import shop.bluebooktle.backend.book.repository.BookImgRepository;
import shop.bluebooktle.backend.book.repository.BookPublisherRepository;
import shop.bluebooktle.backend.book.repository.BookRepository;
import shop.bluebooktle.backend.book.repository.BookSaleInfoRepository;
import shop.bluebooktle.backend.book.repository.BookTagRepository;
import shop.bluebooktle.backend.book.service.impl.BookServiceImpl;
import shop.bluebooktle.backend.elasticsearch.service.BookElasticSearchService;
import shop.bluebooktle.common.dto.book.BookSaleInfoState;
import shop.bluebooktle.common.dto.book.BookSortType;
import shop.bluebooktle.common.dto.book.request.BookUpdateServiceRequest;
import shop.bluebooktle.common.dto.book.response.AdminBookResponse;
import shop.bluebooktle.common.dto.book.response.BookDetailResponse;
import shop.bluebooktle.common.dto.book.response.BookInfoResponse;
import shop.bluebooktle.common.exception.book.BookNotFoundException;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

	@Mock
	private BookRepository bookRepository;
	@Mock
	private BookSaleInfoRepository bookSaleInfoRepository;
	@Mock
	private BookAuthorRepository bookAuthorRepository;
	@Mock
	private BookPublisherRepository bookPublisherRepository;
	@Mock
	private BookCategoryRepository bookCategoryRepository;
	@Mock
	private BookTagRepository bookTagRepository;
	@Mock
	private BookImgRepository bookImgRepository;
	@Mock
	private BookPublisherService bookPublisherService;
	@Mock
	private BookCategoryService bookCategoryService;
	@Mock
	private BookAuthorService bookAuthorService;
	@Mock
	private BookTagService bookTagService;
	@Mock
	private BookImgService bookImgService;
	@Mock
	private BookElasticSearchService bookElasticSearchService;

	@InjectMocks
	private BookServiceImpl bookService;

	private Book book;
	private BookSaleInfo bookSaleInfo;
	private Img img;
	private BookImg bookImg;
	private Author author;
	private BookAuthor bookAuthor;
	private Publisher publisher;
	private BookPublisher bookPublisher;
	private Category category;
	private BookCategory bookCategory;
	private Tag tag;
	private BookTag bookTag;

	private void setId(Object entity, Long idValue) throws NoSuchFieldException, IllegalAccessException {
		Field idField = entity.getClass().getDeclaredField("id");
		idField.setAccessible(true);
		idField.set(entity, idValue);
	}

	@BeforeEach
	void setUp() throws NoSuchFieldException, IllegalAccessException {
		// Img 엔티티 (가장 먼저 초기화)
		img = Img.builder()
			.imgUrl("http://example.com/thumbnail.jpg")
			.build();
		setId(img, 1L); // Img의 id 설정

		// Book 엔티티 (Img 다음으로 초기화)
		book = Book.builder()
			.title("테스트 책")
			.description("이것은 테스트 책입니다.")
			.isbn("1234567890123")
			.publishDate(LocalDateTime.of(2023, 1, 1, 0, 0))
			.index("목차")
			.build();
		setId(book, 1L);

		bookImg = BookImg.builder()
			.book(book)
			.img(img)
			.isThumbnail(true)
			.build();
		setId(bookImg, 1L);

		Field bookImgsField = Book.class.getDeclaredField("bookImgs");
		bookImgsField.setAccessible(true);
		bookImgsField.set(book, Collections.singletonList(bookImg));

		// BookSaleInfo 엔티티
		bookSaleInfo = BookSaleInfo.builder()
			.book(book)
			.price(new BigDecimal("20000.00"))
			.salePrice(new BigDecimal("18000.00"))
			.stock(100)
			.isPackable(true)
			.salePercentage(new BigDecimal("10.00"))
			.bookSaleInfoState(BookSaleInfoState.AVAILABLE)
			.viewCount(50L)
			.searchCount(20L)
			.star(new BigDecimal("4.5"))
			.reviewCount(10L)
			.build();
		setId(bookSaleInfo, 1L);

		// Author 엔티티
		author = Author.builder()
			.name("테스트 작가")
			.build();
		setId(author, 1L);

		// BookAuthor 엔티티
		bookAuthor = BookAuthor.builder()
			.book(book)
			.author(author)
			.build();
		setId(bookAuthor, 1L);

		// Publisher 엔티티
		publisher = Publisher.builder()
			.name("테스트 출판사")
			.build();
		setId(publisher, 1L);

		// BookPublisher 엔티티
		bookPublisher = BookPublisher.builder()
			.book(book)
			.publisher(publisher)
			.build();
		setId(bookPublisher, 1L);

		// Category 엔티티
		category = Category.builder()
			.name("소설")
			.build();
		setId(category, 1L);

		// BookCategory 엔티티
		bookCategory = BookCategory.builder()
			.book(book)
			.category(category)
			.build();
		setId(bookCategory, 1L);

		// Tag 엔티티
		tag = Tag.builder()
			.name("판타지")
			.build();
		setId(tag, 1L);

		// BookTag 엔티티
		bookTag = BookTag.builder()
			.book(book)
			.tag(tag)
			.build();
		setId(bookTag, 1L);
	}

	@Test
	@DisplayName("도서 상세조회 성공")
	void findBookById_success() {
		// Given
		Long bookId = 1L;

		when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
		when(bookSaleInfoRepository.findByBookId(bookId)).thenReturn(Optional.of(bookSaleInfo));
		when(bookAuthorRepository.findByBook_Id(bookId)).thenReturn(Collections.singletonList(bookAuthor));
		when(bookPublisherRepository.findByBook_Id(bookId)).thenReturn(Collections.singletonList(bookPublisher));

		// When
		BookDetailResponse response = bookService.findBookById(bookId);

		// Then
		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo(book.getTitle());
		assertThat(response.getIsbn()).isEqualTo(book.getIsbn());
		assertThat(response.getAuthors()).containsExactly(author.getName());
		assertThat(response.getPublishers()).containsExactly(publisher.getName());
		assertThat(response.getPrice()).isEqualTo(bookSaleInfo.getPrice());
		assertThat(response.getSalePrice()).isEqualTo(bookSaleInfo.getSalePrice());
		assertThat(response.getSalePercentage()).isEqualTo(bookSaleInfo.getSalePercentage().intValue());
		assertThat(response.getDescription()).isEqualTo(book.getDescription());
		assertThat(response.getIndex()).isEqualTo(book.getIndex());
		assertThat(response.getImgUrl()).isEqualTo(img.getImgUrl());
		assertThat(response.getSaleState()).isEqualTo(bookSaleInfo.getBookSaleInfoState());

		verify(bookRepository, times(1)).findById(bookId);
		verify(bookSaleInfoRepository, times(1)).findByBookId(bookId);
		verify(bookAuthorRepository, times(1)).findByBook_Id(bookId);
		verify(bookPublisherRepository, times(1)).findByBook_Id(bookId);
		verify(bookElasticSearchService, times(1)).updateViewCount(book);
	}

	@Test
	@DisplayName("도서 상세 조회 실패 - 도서 없음")
	void findBookById_BookNotFoundException() {
		// Given
		Long nonExistentBookId = 99L;
		when(bookRepository.findById(nonExistentBookId)).thenReturn(Optional.empty());

		// When & Then
		assertThatThrownBy(() -> bookService.findBookById(nonExistentBookId))
			.isInstanceOf(BookNotFoundException.class);

		verify(bookRepository, times(1)).findById(nonExistentBookId);
	}

	@Test
	@DisplayName("도서 업데이트 실패 - 도서없음")
	void updateBook_BookNotFoundException() {
		// Given
		Long nonExistentBookId = 99L;
		BookUpdateServiceRequest request = BookUpdateServiceRequest.builder().build();
		when(bookRepository.findById(nonExistentBookId)).thenReturn(Optional.empty());

		// When & Then
		assertThatThrownBy(() -> bookService.updateBook(nonExistentBookId, request))
			.isInstanceOf(BookNotFoundException.class);
		verify(bookRepository, times(1)).findById(nonExistentBookId);
		verify(bookRepository, never()).save(any(Book.class));
		verify(bookElasticSearchService, never()).updateBook(
			any(shop.bluebooktle.common.dto.elasticsearch.BookElasticSearchUpdateRequest.class));
	}

	@Test
	@DisplayName("도서 삭제 성공")
	void deleteBook_success() {
		// Given
		Long bookId = 1L;
		when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
		when(bookSaleInfoRepository.findByBook(book)).thenReturn(Optional.of(bookSaleInfo));

		// When
		bookService.deleteBook(bookId);

		// Then
		verify(bookRepository, times(1)).findById(bookId);
		verify(bookSaleInfoRepository, times(1)).findByBook(book);
		verify(bookSaleInfoRepository, times(1)).delete(bookSaleInfo);
		verify(bookRepository, times(1)).delete(book);
		verify(bookElasticSearchService, times(1)).deleteBook(book);
	}

	@Test
	@DisplayName("도서 삭제 실패 - 도서없음")
	void deleteBook_BookNotFoundException() {
		// Given
		Long nonExistentBookId = 99L;
		when(bookRepository.findById(nonExistentBookId)).thenReturn(Optional.empty());

		// When & Then
		assertThatThrownBy(() -> bookService.deleteBook(nonExistentBookId))
			.isInstanceOf(BookNotFoundException.class);
		verify(bookRepository, times(1)).findById(nonExistentBookId);
		verify(bookRepository, never()).delete(any(Book.class));
		verify(bookElasticSearchService, never()).deleteBook(any(Book.class));
	}

	@Test
	@DisplayName("모든 도서 조회 성공")
	void findAllBooks_success() {
		// Given
		int page = 0;
		int size = 10;
		String searchKeyword = "";
		BookSortType sortType = BookSortType.NEWEST;

		List<Book> books = Collections.singletonList(book);
		org.springframework.data.domain.Page<Book> bookPage = new org.springframework.data.domain.PageImpl<>(books,
			org.springframework.data.domain.PageRequest.of(page, size), 1);

		when(bookElasticSearchService.searchBooksBySortOnly(sortType, page, size)).thenReturn(bookPage);
		when(bookSaleInfoRepository.findByBook(book)).thenReturn(Optional.of(bookSaleInfo));
		when(bookAuthorRepository.findByBookId(book.getId())).thenReturn(Collections.singletonList(bookAuthor));
		when(bookImgRepository.findByBook(book)).thenReturn(bookImg);

		// When
		org.springframework.data.domain.Page<shop.bluebooktle.common.dto.book.response.BookInfoResponse> responsePage = bookService.findAllBooks(
			page, size, searchKeyword, sortType);

		// Then
		assertThat(responsePage).isNotNull();
		assertThat(responsePage.getTotalElements()).isEqualTo(1);
		assertThat(responsePage.getContent()).hasSize(1);
		BookInfoResponse response = responsePage.getContent().get(0);
		assertThat(response.title()).isEqualTo(book.getTitle());
		assertThat(response.authors()).containsExactly(author.getName());
		assertThat(response.salePrice()).isEqualTo(bookSaleInfo.getSalePrice());
		assertThat(response.price()).isEqualTo(bookSaleInfo.getPrice());
		assertThat(response.imgUrl()).isEqualTo(img.getImgUrl());
		assertThat(response.star()).isEqualTo(bookSaleInfo.getStar());

		verify(bookElasticSearchService, times(1)).searchBooksBySortOnly(sortType, page, size);
		verify(bookElasticSearchService, never()).updateSearchCount(anyList());
		verify(bookElasticSearchService, never()).searchBooksByKeywordAndSort(anyString(),
			any(shop.bluebooktle.common.dto.book.BookSortType.class), anyInt(), anyInt());
		verify(bookSaleInfoRepository, times(1)).findByBook(book);
		verify(bookAuthorRepository, times(1)).findByBookId(book.getId());
		verify(bookImgRepository, times(1)).findByBook(book);
	}

	@Test
	@DisplayName("검색 키워드로 모든 도서 조회 성공")
	void findAllBooks_withSearchKeyword_success() {
		// Given
		int page = 0;
		int size = 10;
		String searchKeyword = "테스트";
		shop.bluebooktle.common.dto.book.BookSortType sortType = BookSortType.NEWEST;

		List<Book> books = Collections.singletonList(book);
		org.springframework.data.domain.Page<Book> bookPage = new org.springframework.data.domain.PageImpl<>(books,
			org.springframework.data.domain.PageRequest.of(page, size), 1);

		when(bookElasticSearchService.searchBooksByKeywordAndSort(searchKeyword, sortType, page, size)).thenReturn(
			bookPage);

		when(bookSaleInfoRepository.findByBook(book)).thenReturn(Optional.of(bookSaleInfo));
		when(bookAuthorRepository.findByBookId(book.getId())).thenReturn(Collections.singletonList(bookAuthor));
		when(bookImgRepository.findByBook(book)).thenReturn(bookImg);

		// When
		Page<BookInfoResponse> responsePage = bookService.findAllBooks(page, size, searchKeyword, sortType);

		// Then
		assertThat(responsePage).isNotNull();
		assertThat(responsePage.getTotalElements()).isEqualTo(1);
		assertThat(responsePage.getContent()).hasSize(1);
		BookInfoResponse response = responsePage.getContent().get(0);
		assertThat(response.title()).isEqualTo(book.getTitle());

		verify(bookElasticSearchService, times(1)).searchBooksByKeywordAndSort(searchKeyword, sortType, page, size);
		verify(bookElasticSearchService, times(1)).updateSearchCount(books);
		verify(bookElasticSearchService, never()).searchBooksBySortOnly(
			any(shop.bluebooktle.common.dto.book.BookSortType.class), anyInt(), anyInt());
		verify(bookSaleInfoRepository, times(1)).findByBook(book);
		verify(bookAuthorRepository, times(1)).findByBookId(book.getId());
		verify(bookImgRepository, times(1)).findByBook(book);
	}

	@Test
	@DisplayName("관리자용 모든 도서 조회 성공")
	void findAllBooksByAdmin_success() {
		// Given
		int page = 0;
		int size = 10;
		String searchKeyword = "";

		List<Book> books = Collections.singletonList(book);
		org.springframework.data.domain.Page<Book> bookPage = new org.springframework.data.domain.PageImpl<>(books,
			org.springframework.data.domain.PageRequest.of(page, size), 1);

		when(bookRepository.findAll(any(org.springframework.data.domain.Pageable.class))).thenReturn(bookPage);
		when(bookSaleInfoRepository.findByBook(book)).thenReturn(Optional.of(bookSaleInfo));
		when(bookAuthorRepository.findByBookId(book.getId())).thenReturn(Collections.singletonList(bookAuthor));
		when(bookPublisherRepository.findByBookId(book.getId())).thenReturn(Collections.singletonList(bookPublisher));

		// When
		Page<AdminBookResponse> responsePage = bookService.findAllBooksByAdmin(
			page, size, searchKeyword);

		// Then
		assertThat(responsePage).isNotNull();
		assertThat(responsePage.getTotalElements()).isEqualTo(1);
		assertThat(responsePage.getContent()).hasSize(1);
		shop.bluebooktle.common.dto.book.response.AdminBookResponse response = responsePage.getContent().get(0);
		assertThat(response.getTitle()).isEqualTo(book.getTitle());
		assertThat(response.getAuthors()).containsExactly(author.getName());
		assertThat(response.getPublishers()).containsExactly(publisher.getName());
		assertThat(response.getSalePrice()).isEqualTo(bookSaleInfo.getSalePrice());
		assertThat(response.getStock()).isEqualTo(bookSaleInfo.getStock());
		assertThat(response.getPublishDate()).isEqualTo(book.getPublishDate());

		// Verify interactions
		verify(bookRepository, times(1)).findAll(any(org.springframework.data.domain.Pageable.class));
		verify(bookElasticSearchService, never()).searchBooksByKeyword(anyString(), anyInt(), anyInt());
		verify(bookSaleInfoRepository, times(1)).findByBook(book);
		verify(bookAuthorRepository, times(1)).findByBookId(book.getId());
		verify(bookPublisherRepository, times(1)).findByBookId(book.getId());
	}

	@Test
	@DisplayName("도서 정보 전체 조회 실패 - 도서 없음")
	void findBookAllById_BookNotFoundException() {
		// Given
		Long nonExistentBookId = 99L;
		when(bookRepository.findById(nonExistentBookId)).thenReturn(Optional.empty());

		// When & Then
		assertThatThrownBy(() -> bookService.findBookAllById(nonExistentBookId))
			.isInstanceOf(BookNotFoundException.class);

		verify(bookRepository, times(1)).findById(nonExistentBookId);
		verify(bookSaleInfoRepository, never()).findByBookId(anyLong());
	}

}