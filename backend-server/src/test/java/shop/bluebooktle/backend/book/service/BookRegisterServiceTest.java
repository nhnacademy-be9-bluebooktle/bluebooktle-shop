package shop.bluebooktle.backend.book.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.BookSaleInfo;
import shop.bluebooktle.backend.book.repository.BookRepository;
import shop.bluebooktle.backend.book.repository.BookSaleInfoRepository;
import shop.bluebooktle.backend.book.service.impl.BookRegisterServiceImpl;
import shop.bluebooktle.backend.elasticsearch.service.BookElasticSearchService;
import shop.bluebooktle.common.dto.book.BookSaleInfoState;
import shop.bluebooktle.common.dto.book.request.BookAllRegisterByAladinRequest;
import shop.bluebooktle.common.dto.book.request.BookAllRegisterRequest;
import shop.bluebooktle.common.dto.book.response.AladinBookResponse;
import shop.bluebooktle.common.dto.book.response.PublisherInfoResponse;
import shop.bluebooktle.common.dto.book.response.TagInfoResponse;
import shop.bluebooktle.common.dto.book.response.author.AuthorResponse;
import shop.bluebooktle.common.exception.book.AladinBookNotFoundException;
import shop.bluebooktle.common.exception.book.BookAlreadyExistsException;

@ExtendWith(MockitoExtension.class)
class BookRegisterServiceTest {

	@Mock
	private BookRepository bookRepository;
	@Mock
	private BookSaleInfoRepository bookSaleInfoRepository;
	@Mock
	private AladinBookService aladinBookService;
	@Mock
	private BookPublisherService bookPublisherService;
	@Mock
	private BookCategoryService bookCategoryService;
	@Mock
	private BookTagService bookTagService;
	@Mock
	private BookImgService bookImgService;
	@Mock
	private BookAuthorService bookAuthorService;
	@Mock
	private AuthorService authorService;
	@Mock
	private PublisherService publisherService;
	@Mock
	private BookElasticSearchService bookElasticSearchService;

	@InjectMocks
	private BookRegisterServiceImpl bookRegisterService;

	@Test
	@DisplayName("도서 직접 등록 성공 - 모든 필드 포함")
	void registerBook_Success_AllFields() {
		BookAllRegisterRequest request = BookAllRegisterRequest.builder()
			.isbn("9791160508488")
			.title("테스트 도서")
			.description("테스트 도서 설명입니다!!")
			.publishDate(LocalDate.of(2023, 1, 1))
			.index("테스트 목차")
			.price(new BigDecimal("25000.00"))
			.salePrice(new BigDecimal("20000.00"))
			.stock(100)
			.isPackable(true)
			.state(BookSaleInfoState.AVAILABLE)
			.authorIdList(List.of(1L))
			.publisherIdList(List.of(1L))
			.categoryIdList(List.of(1L))
			.tagIdList(List.of(1L))
			.imgUrl("http://example.com/cover.jpg")
			.build();

		final Long expectedBookId = 1L;

		AuthorResponse authorResponse = AuthorResponse.builder().id(1L).name("테스트 작가").build();
		PublisherInfoResponse publisherResponse = PublisherInfoResponse.builder().id(1L).name("테스트 출판사").build();
		TagInfoResponse tagResponse = TagInfoResponse.builder().id(1L).name("테스트 태그").build();

		when(bookRepository.findByIsbn(anyString())).thenReturn(Optional.empty());

		when(bookRepository.save(any(Book.class))).thenAnswer(new Answer<Book>() {
			@Override
			public Book answer(InvocationOnMock invocation) throws Throwable {
				Book bookToSave = invocation.getArgument(0);
				java.lang.reflect.Field idField = Book.class.getDeclaredField("id");
				idField.setAccessible(true);
				idField.set(bookToSave, expectedBookId);
				return bookToSave;
			}
		});

		when(bookSaleInfoRepository.save(any(BookSaleInfo.class))).thenAnswer(new Answer<BookSaleInfo>() {
			@Override
			public BookSaleInfo answer(InvocationOnMock invocation) throws Throwable {
				BookSaleInfo saleInfoToSave = invocation.getArgument(0);
				java.lang.reflect.Field idField = BookSaleInfo.class.getDeclaredField("id");
				idField.setAccessible(true);
				idField.set(saleInfoToSave, 1L);
				return saleInfoToSave;
			}
		});

		when(bookAuthorService.registerBookAuthor(eq(expectedBookId), anyList()))
			.thenReturn(Collections.singletonList(authorResponse));
		when(bookPublisherService.registerBookPublisher(eq(expectedBookId), anyList()))
			.thenReturn(Collections.singletonList(publisherResponse));
		doNothing().when(bookCategoryService).registerBookCategory(eq(expectedBookId), anyList());
		when(bookTagService.registerBookTag(eq(expectedBookId), anyList()))
			.thenReturn(Collections.singletonList(tagResponse));
		doNothing().when(bookImgService).registerBookImg(eq(expectedBookId), anyString());
		doNothing().when(bookElasticSearchService).registerBook(any());

		bookRegisterService.registerBook(request);

		verify(bookRepository, times(1)).findByIsbn(request.getIsbn());
		verify(bookRepository, times(1)).save(any(Book.class));
		verify(bookSaleInfoRepository, times(1)).save(any(BookSaleInfo.class));
		verify(bookAuthorService, times(1)).registerBookAuthor(expectedBookId, request.getAuthorIdList());
		verify(bookPublisherService, times(1)).registerBookPublisher((expectedBookId),
			eq(request.getPublisherIdList()));
		verify(bookCategoryService, times(1)).registerBookCategory(expectedBookId, request.getCategoryIdList());
		verify(bookTagService, times(1)).registerBookTag(expectedBookId, request.getTagIdList());
		verify(bookImgService, times(1)).registerBookImg(expectedBookId, request.getImgUrl());
		verify(bookElasticSearchService, times(1)).registerBook(any());
	}

	@Test
	@DisplayName("도서 직접 등록 성공 - 필수 필드만 포함 (isPackable, tagIdList null/empty)")
	void registerBook_Success_RequiredFieldsOnly() {
		BookAllRegisterRequest request = BookAllRegisterRequest.builder()
			.isbn("9791160508489")
			.title("최소 필드 도서")
			.price(new BigDecimal("10000.00"))
			.salePrice(new BigDecimal("9000.00"))
			.stock(10)
			.state(BookSaleInfoState.AVAILABLE)
			.authorIdList(List.of(1L))
			.publisherIdList(List.of(1L))
			.categoryIdList(List.of(1L))
			.imgUrl("http://example.com/cover2.jpg")
			.build();

		final Long expectedBookId = 2L;

		AuthorResponse authorResponse = AuthorResponse.builder().id(1L).name("테스트 작가").build();
		PublisherInfoResponse publisherResponse = PublisherInfoResponse.builder().id(1L).name("테스트 출판사").build();

		when(bookRepository.findByIsbn(anyString())).thenReturn(Optional.empty());

		when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> {
			Book bookToSave = invocation.getArgument(0);
			try {
				java.lang.reflect.Field idField = Book.class.getDeclaredField("id");
				idField.setAccessible(true);
				idField.set(bookToSave, expectedBookId);
			} catch (NoSuchFieldException | IllegalAccessException e) {
				fail("Failed to set book ID via reflection: " + e.getMessage());
			}
			return bookToSave;
		});

		when(bookSaleInfoRepository.save(any(BookSaleInfo.class))).thenAnswer(invocation -> {
			BookSaleInfo saleInfoToSave = invocation.getArgument(0);
			try {
				java.lang.reflect.Field idField = BookSaleInfo.class.getDeclaredField("id");
				idField.setAccessible(true);
				idField.set(saleInfoToSave, 2L);
			} catch (NoSuchFieldException | IllegalAccessException e) {
				fail("Failed to set BookSaleInfo ID via reflection: " + e.getMessage());
			}
			return saleInfoToSave;
		});

		when(bookAuthorService.registerBookAuthor(eq(expectedBookId), anyList()))
			.thenReturn(Collections.singletonList(authorResponse));
		when(bookPublisherService.registerBookPublisher(eq(expectedBookId), anyList()))
			.thenReturn(Collections.singletonList(publisherResponse));
		doNothing().when(bookCategoryService).registerBookCategory(eq(expectedBookId), anyList());
		doNothing().when(bookImgService).registerBookImg(eq(expectedBookId), anyString());
		doNothing().when(bookElasticSearchService).registerBook(any());

		bookRegisterService.registerBook(request);

		verify(bookRepository, times(1)).findByIsbn(request.getIsbn());
		verify(bookRepository, times(1)).save(any(Book.class));
		verify(bookSaleInfoRepository, times(1)).save(any(BookSaleInfo.class));
		verify(bookAuthorService, times(1)).registerBookAuthor(expectedBookId, request.getAuthorIdList());
		verify(bookPublisherService, times(1)).registerBookPublisher(expectedBookId,
			request.getPublisherIdList());
		verify(bookCategoryService, times(1)).registerBookCategory(expectedBookId,
			request.getCategoryIdList());
		verify(bookTagService, never()).registerBookTag(anyLong(), anyList());
		verify(bookImgService, times(1)).registerBookImg(expectedBookId, request.getImgUrl());
		verify(bookElasticSearchService, times(1)).registerBook(any());
	}

	@Test
	@DisplayName("도서 직접 등록 실패 - 이미 존재하는 ISBN")
	void registerBook_Fail_BookAlreadyExists() {
		BookAllRegisterRequest request = BookAllRegisterRequest.builder()
			.isbn("9791160508488")
			.title("기존 도서")
			.build();

		when(bookRepository.findByIsbn(anyString())).thenReturn(Optional.of(new Book()));

		assertThrows(BookAlreadyExistsException.class, () -> bookRegisterService.registerBook(request));

		verify(bookRepository, times(1)).findByIsbn(request.getIsbn());
		verify(bookRepository, never()).save(any(Book.class));
		verify(bookSaleInfoRepository, never()).save(any(BookSaleInfo.class));
		verify(bookAuthorService, never()).registerBookAuthor(anyLong(), anyList());
		verify(bookPublisherService, never()).registerBookPublisher(anyLong(), anyList());
		verify(bookCategoryService, never()).registerBookCategory(anyLong(), anyList());
		verify(bookTagService, never()).registerBookTag(anyLong(), anyList());
		verify(bookImgService, never()).registerBookImg(anyLong(), anyString());
		verify(bookElasticSearchService, never()).registerBook(any());
	}

	@Test
	@DisplayName("알라딘 API 도서 등록 성공 - 모든 필드 포함")
	void registerBookByAladin_Success_AllFields() {
		BookAllRegisterByAladinRequest request = BookAllRegisterByAladinRequest.builder()
			.isbn("978-0134685991")
			.index("알라딘 목차")
			.stock(50)
			.isPackable(true)
			.state(BookSaleInfoState.AVAILABLE)
			.categoryIdList(List.of(10L, 11L))
			.tagIdList(List.of(100L, 101L))
			.build();

		AladinBookResponse aladinResponse = AladinBookResponse.builder()
			.title("Effective Java")
			.author("Joshua Bloch(지은이)")
			.description("A must-read for Java developers.")
			.publishDate(LocalDateTime.of(2018, 1, 1, 0, 0, 0))
			.isbn("978-0134685991")
			.price(BigDecimal.valueOf(30000))
			.salePrice(BigDecimal.valueOf(27000))
			.salePercentage(BigDecimal.valueOf(10))
			.publisher("Addison-Wesley")
			.categoryName("Programming")
			.imgUrl("http://image.aladin.co.kr/cover/cover200/K123456789_1.jpg")
			.build();

		AuthorResponse authorResponse = AuthorResponse.builder().id(2L).name("Joshua Bloch").build();
		PublisherInfoResponse publisherResponse = PublisherInfoResponse.builder().id(2L).name("Addison-Wesley").build();
		TagInfoResponse tagResponse1 = TagInfoResponse.builder().id(100L).name("알라딘 태그1").build();
		TagInfoResponse tagResponse2 = TagInfoResponse.builder().id(101L).name("알라딘 태그2").build();

		when(bookRepository.existsByIsbn(anyString())).thenReturn(false);
		when(aladinBookService.getBookByIsbn(anyString())).thenReturn(aladinResponse);

		final Long expectedBookId = 3L;
		when(bookRepository.save(any(Book.class))).thenAnswer(new Answer<Book>() {
			@Override
			public Book answer(InvocationOnMock invocation) throws Throwable {
				Book bookToSave = invocation.getArgument(0);

				java.lang.reflect.Field idField = Book.class.getDeclaredField("id");
				idField.setAccessible(true);
				idField.set(bookToSave, expectedBookId);
				return bookToSave;
			}
		});

		final Long expectedSaleInfoId = 3L;
		when(bookSaleInfoRepository.save(any(BookSaleInfo.class))).thenAnswer(new Answer<BookSaleInfo>() {
			@Override
			public BookSaleInfo answer(InvocationOnMock invocation) throws Throwable {
				BookSaleInfo saleInfoToSave = invocation.getArgument(0);
				java.lang.reflect.Field idField = BookSaleInfo.class.getDeclaredField("id");
				idField.setAccessible(true);
				idField.set(saleInfoToSave, expectedSaleInfoId);
				return saleInfoToSave;
			}
		});

		when(authorService.registerAuthorByName(anyString())).thenReturn(authorResponse);
		when(bookAuthorService.registerBookAuthor(anyLong(), anyLong())).thenReturn(null);
		when(publisherService.registerPublisherByName(anyString())).thenReturn(
			publisherResponse);
		when(bookPublisherService.registerBookPublisher(anyLong(), anyLong())).thenReturn(null);
		doNothing().when(bookImgService).registerBookImg(anyLong(), anyString());
		doNothing().when(bookCategoryService).registerBookCategory(anyLong(), anyLong());
		when(bookTagService.registerBookTag(anyLong(), anyList()))
			.thenReturn(List.of(tagResponse1, tagResponse2));

		doNothing().when(bookElasticSearchService).registerBook(any());

		bookRegisterService.registerBookByAladin(request);

		verify(bookRepository, times(1)).existsByIsbn(request.getIsbn());
		verify(aladinBookService, times(1)).getBookByIsbn(request.getIsbn());
		verify(bookRepository, times(1)).save(any(Book.class));
		verify(bookSaleInfoRepository, times(1)).save(any(BookSaleInfo.class));
		verify(authorService, times(1)).registerAuthorByName("Joshua Bloch");
		verify(bookAuthorService, times(1)).registerBookAuthor(expectedBookId,
			eq(authorResponse.getId()));
		verify(publisherService, times(1)).registerPublisherByName("Addison-Wesley");
		verify(bookPublisherService, times(1)).registerBookPublisher(expectedBookId,
			eq(publisherResponse.getId()));
		verify(bookImgService, times(1)).registerBookImg(expectedBookId, endsWith("/cover500/K123456789_1.jpg"));
		verify(bookCategoryService, times(request.getCategoryIdList().size())).registerBookCategory(
			eq(expectedBookId), anyLong());
		verify(bookTagService, times(1)).registerBookTag(expectedBookId, request.getTagIdList());
		verify(bookElasticSearchService, times(1)).registerBook(any());
	}

	@Test
	@DisplayName("알라딘 API 도서 등록 성공 - 태그 목록이 없는 경우")
	void registerBookByAladin_Success_NoTags() {
		BookAllRegisterByAladinRequest request = BookAllRegisterByAladinRequest.builder()
			.isbn("978-0134685992")
			.index("알라딘 목차")
			.stock(50)
			.isPackable(false)
			.state(BookSaleInfoState.AVAILABLE)
			.categoryIdList(List.of(10L))
			.tagIdList(Collections.emptyList())
			.build();

		AladinBookResponse aladinResponse = AladinBookResponse.builder()
			.title("Another Book")
			.author("Some Author(지은이)")
			.description("Description.")
			.publishDate(LocalDateTime.of(2020, 5, 1, 0, 0, 0))
			.isbn("978-0134685992")
			.price(BigDecimal.valueOf(20000))
			.salePrice(BigDecimal.valueOf(18000))
			.salePercentage(BigDecimal.valueOf(10))
			.publisher("Publisher A")
			.imgUrl("http://image.aladin.co.kr/cover/cover200/K987654321_1.jpg")
			.build();

		final Long expectedBookId = 4L;
		AuthorResponse authorResponse = AuthorResponse.builder().id(3L).name("Some Author").build();
		PublisherInfoResponse publisherResponse = PublisherInfoResponse.builder().id(3L).name("Publisher A").build();

		when(bookRepository.existsByIsbn(anyString())).thenReturn(false);
		when(aladinBookService.getBookByIsbn(anyString())).thenReturn(aladinResponse);

		when(bookRepository.save(any(Book.class))).thenAnswer(new Answer<Book>() {
			@Override
			public Book answer(InvocationOnMock invocation) throws Throwable {
				Book bookToSave = invocation.getArgument(0);
				java.lang.reflect.Field idField = Book.class.getDeclaredField("id");
				idField.setAccessible(true);
				idField.set(bookToSave, expectedBookId);
				return bookToSave;
			}
		});

		when(bookSaleInfoRepository.save(any(BookSaleInfo.class))).thenAnswer(new Answer<BookSaleInfo>() {
			@Override
			public BookSaleInfo answer(InvocationOnMock invocation) throws Throwable {
				BookSaleInfo saleInfoToSave = invocation.getArgument(0);
				java.lang.reflect.Field idField = BookSaleInfo.class.getDeclaredField("id");
				idField.setAccessible(true);
				idField.set(saleInfoToSave, 4L);
				return saleInfoToSave;
			}
		});

		when(authorService.registerAuthorByName(anyString())).thenReturn(authorResponse);
		when(bookAuthorService.registerBookAuthor(anyLong(), anyLong())).thenReturn(null);
		when(publisherService.registerPublisherByName(anyString())).thenReturn(publisherResponse);
		when(bookPublisherService.registerBookPublisher(anyLong(), anyLong())).thenReturn(null);
		doNothing().when(bookImgService).registerBookImg(anyLong(), anyString());
		doNothing().when(bookCategoryService).registerBookCategory(anyLong(), anyLong());
		doNothing().when(bookElasticSearchService).registerBook(any());

		bookRegisterService.registerBookByAladin(request);

		verify(bookRepository, times(1)).existsByIsbn(request.getIsbn());
		verify(aladinBookService, times(1)).getBookByIsbn(request.getIsbn());
		verify(bookRepository, times(1)).save(any(Book.class));
		verify(bookSaleInfoRepository, times(1)).save(any(BookSaleInfo.class));
		verify(bookTagService, never()).registerBookTag(anyLong(), anyList());
		verify(bookElasticSearchService, times(1)).registerBook(any());
	}

	@Test
	@DisplayName("알라딘 API 도서 등록 실패 - 이미 존재하는 ISBN")
	void registerBookByAladin_Fail_BookAlreadyExists() {
		BookAllRegisterByAladinRequest request = BookAllRegisterByAladinRequest.builder()
			.isbn("978-0134685991")
			.build();

		when(bookRepository.existsByIsbn(anyString())).thenReturn(true);

		assertThrows(BookAlreadyExistsException.class, () -> bookRegisterService.registerBookByAladin(request));

		verify(bookRepository, times(1)).existsByIsbn(request.getIsbn());
		verify(aladinBookService, never()).getBookByIsbn(anyString());
		verify(bookRepository, never()).save(any(Book.class));
	}

	@Test
	@DisplayName("알라딘 API 도서 등록 실패 - 알라딘에서 도서를 찾을 수 없음")
	void registerBookByAladin_Fail_AladinNotFound() {
		BookAllRegisterByAladinRequest request = BookAllRegisterByAladinRequest.builder()
			.isbn("999-9999999999")
			.build();

		when(bookRepository.existsByIsbn(anyString())).thenReturn(false);
		when(aladinBookService.getBookByIsbn(anyString())).thenReturn(null);

		AladinBookNotFoundException thrown = assertThrows(AladinBookNotFoundException.class,
			() -> bookRegisterService.registerBookByAladin(request));

		assertEquals("알라딘 API에서 해당 ISBN의 도서를 찾을 수 없습니다.", thrown.getMessage());

		verify(bookRepository, times(1)).existsByIsbn(request.getIsbn());
		verify(aladinBookService, times(1)).getBookByIsbn(request.getIsbn());
		verify(bookRepository, never()).save(any(Book.class));
	}
}