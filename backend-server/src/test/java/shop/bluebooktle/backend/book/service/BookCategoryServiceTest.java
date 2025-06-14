package shop.bluebooktle.backend.book.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import shop.bluebooktle.backend.book.entity.Author;
import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.BookAuthor;
import shop.bluebooktle.backend.book.entity.BookCategory;
import shop.bluebooktle.backend.book.entity.BookImg;
import shop.bluebooktle.backend.book.entity.BookSaleInfo;
import shop.bluebooktle.backend.book.entity.Category;
import shop.bluebooktle.backend.book.entity.Img;
import shop.bluebooktle.backend.book.repository.BookAuthorRepository;
import shop.bluebooktle.backend.book.repository.BookCategoryRepository;
import shop.bluebooktle.backend.book.repository.BookImgRepository;
import shop.bluebooktle.backend.book.repository.BookRepository;
import shop.bluebooktle.backend.book.repository.BookSaleInfoRepository;
import shop.bluebooktle.backend.book.repository.CategoryRepository;
import shop.bluebooktle.backend.book.service.impl.BookCategoryServiceImpl;
import shop.bluebooktle.backend.elasticsearch.service.BookElasticSearchService;
import shop.bluebooktle.common.dto.book.BookSortType;
import shop.bluebooktle.common.dto.book.response.BookInfoResponse;
import shop.bluebooktle.common.exception.book.BookCategoryAlreadyExistsException;
import shop.bluebooktle.common.exception.book.BookCategoryLimitExceededException;
import shop.bluebooktle.common.exception.book.BookNotFoundException;
import shop.bluebooktle.common.exception.book.CategoryNotFoundException;

@ExtendWith(MockitoExtension.class)
public class BookCategoryServiceTest {

	@Mock
	private BookRepository bookRepository;

	@Mock
	private CategoryRepository categoryRepository;

	@Mock
	private BookCategoryRepository bookCategoryRepository;

	@Mock
	private BookAuthorRepository bookAuthorRepository;

	@Mock
	private BookSaleInfoRepository bookSaleInfoRepository;

	@Mock
	private BookImgRepository bookImgRepository;

	@Mock
	private BookElasticSearchService bookElasticSearchService;

	@InjectMocks
	private BookCategoryServiceImpl bookCategoryService;


	@Test
	@DisplayName("도서 카테고리 등록 성공")
	void registerBookCategory_success() {

		Book book = Book.builder().id(1L).build();

		Category category = Category.builder().build();
		ReflectionTestUtils.setField(category, "id", 1L);

		when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
		when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
		when(bookCategoryRepository.existsByBookAndCategory(book, category)).thenReturn(false);
		when(bookCategoryRepository.countByBook(book)).thenReturn(0L);

		bookCategoryService.registerBookCategory(book.getId(), category.getId());

		ArgumentCaptor<BookCategory> captor = ArgumentCaptor.forClass(BookCategory.class);
		verify(bookCategoryRepository, times(1)).save(captor.capture());
		BookCategory bookCategory = captor.getValue();
		assertEquals(book, bookCategory.getBook());
		assertEquals(category, bookCategory.getCategory());
	}

	@Test
	@DisplayName("도서 카테고리 등록 시 도서 아이디가 유효하지 않아 실패하는 경우")
	void registerBookCategory_fail_book_id_invalid() {
		when(bookRepository.findById(1L)).thenReturn(Optional.empty());
		assertThrows(BookNotFoundException.class, () -> {
			bookCategoryService.registerBookCategory(1L, 1L);
		});

		verify(bookCategoryRepository, never()).save(any(BookCategory.class));
	}

	@Test
	@DisplayName("도서 카테고리 등록 시 카테고리 아이디가 유효하지 않아 실패하는 경우")
	void registerBookCategory_fail_category_id_invalid() {
		Book book = Book.builder().id(1L).build();

		when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
		when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(CategoryNotFoundException.class, () -> {
			bookCategoryService.registerBookCategory(book.getId(), 1L);
		});

		verify(bookCategoryRepository, never()).save(any(BookCategory.class));
	}

	@Test
	@DisplayName("도서 카테고리 등록 시 이미 등록된 카테고리라 실패하는 경우")
	void registerBookCategory_fail_already_registered() {
		Book book = Book.builder().id(1L).build();
		Category category = Category.builder().build();
		ReflectionTestUtils.setField(category, "id", 1L);

		when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
		when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
		when(bookCategoryRepository.existsByBookAndCategory(book, category)).thenReturn(true);

		assertThrows(BookCategoryAlreadyExistsException.class, () -> {
			bookCategoryService.registerBookCategory(book.getId(), category.getId());
		});

		verify(bookCategoryRepository, never()).save(any(BookCategory.class));
	}

	@Test
	@DisplayName("도서 카테고리 등록 시 이미 최대치의 카테고리를 갖고 있어 실패하는 경우")
	void registerBookCategory_fail_already_max_category() {
		Book book = Book.builder().id(1L).build();
		Category category = Category.builder().build();
		ReflectionTestUtils.setField(category, "id", 1L);

		when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
		when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
		when(bookCategoryRepository.existsByBookAndCategory(book, category)).thenReturn(false);
		when(bookCategoryRepository.countByBook(book)).thenReturn(10L);

		assertThrows(BookCategoryLimitExceededException.class, () -> {
			bookCategoryService.registerBookCategory(book.getId(), category.getId());
		});

		verify(bookCategoryRepository, never()).save(any(BookCategory.class));
	}

	// registerBookCategory(Long bookId, List<Long> categoryIdList) : 오버로딩 메서드 2

	@Test
	@DisplayName("도서에 카테고리 등록 성공")
	void registerBookCategory_with_categoryIdList_success() {

		Book book = Book.builder().id(1L).build();

		Category category1 = Category.builder().build();
		ReflectionTestUtils.setField(category1, "id", 1L);

		Category category2 = Category.builder().build();
		ReflectionTestUtils.setField(category2, "id", 2L);

		List<Long> categoryIdList = new ArrayList<>();
		categoryIdList.add(1L);
		categoryIdList.add(2L);

		when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
		when(categoryRepository.findById(category1.getId())).thenReturn(Optional.of(category1));
		when(categoryRepository.findById(category2.getId())).thenReturn(Optional.of(category2));
		when(bookCategoryRepository.existsByBookAndCategory(book, category1)).thenReturn(false);
		when(bookCategoryRepository.existsByBookAndCategory(book, category2)).thenReturn(false);
		when(bookCategoryRepository.countByBook(book)).thenReturn(0L, 1L);

		bookCategoryService.registerBookCategory(book.getId(), categoryIdList);

		ArgumentCaptor<BookCategory> captor = ArgumentCaptor.forClass(BookCategory.class);
		verify(bookCategoryRepository, times(2)).save(captor.capture());
		List<BookCategory> bookCategories = captor.getAllValues();
		assertEquals(book, bookCategories.get(0).getBook());
		assertEquals(category1, bookCategories.get(0).getCategory());
		assertEquals(book, bookCategories.get(1).getBook());
		assertEquals(category2, bookCategories.get(1).getCategory());
	}

	@Test
	@DisplayName("도서에 카테고리 등록 시 도서 아이디가 유효하지 않아 실패하는 경우")
	void registerBookCategory_with_categoryIdList_fail_book_id_invalid() {

		when(bookRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(BookNotFoundException.class, () -> {
			bookCategoryService.registerBookCategory(1L, List.of(1L));
		});

		verify(bookCategoryRepository, never()).save(any(BookCategory.class));
	}

	@Test
	@DisplayName("도서에 카테고리 등록 시 일부 카테고리 아이디가 유효하지 않아 실패하는 경우")
	void registerBookCategory_with_categoryIdList_fail_category_id_invalid() {

		Book book = Book.builder().id(1L).build();

		Category category1 = Category.builder().build();
		ReflectionTestUtils.setField(category1, "id", 1L);

		when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
		when(categoryRepository.findById(1L)).thenReturn(Optional.of(category1));
		when(categoryRepository.findById(2L)).thenReturn(Optional.empty());
		when(bookCategoryRepository.countByBook(book)).thenReturn(0L);
		when(bookCategoryRepository.existsByBookAndCategory(book, category1)).thenReturn(false);

		assertThrows(CategoryNotFoundException.class, () -> {
			bookCategoryService.registerBookCategory(book.getId(), List.of(category1.getId(), 2L));
		});

		verify(bookCategoryRepository, times(1)).save(any(BookCategory.class));
	}

	@Test
	@DisplayName("도서에 카테고리 등록 시 일부 카테고리가 이미 등록된 경우")
	void registerBookCategory_with_categoryIdList_fail_already_registered() {
		Book book = Book.builder().id(1L).build();
		Category category1 = Category.builder().build();
		ReflectionTestUtils.setField(category1, "id", 1L);

		Category category2 = Category.builder().build();
		ReflectionTestUtils.setField(category2, "id", 2L);

		when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
		when(categoryRepository.findById(1L)).thenReturn(Optional.of(category1));
		when(categoryRepository.findById(2L)).thenReturn(Optional.of(category2));
		when(bookCategoryRepository.existsByBookAndCategory(book, category1)).thenReturn(false);
		when(bookCategoryRepository.existsByBookAndCategory(book, category2)).thenReturn(true);
		when(bookCategoryRepository.countByBook(book)).thenReturn(0L);

		assertThrows(BookCategoryAlreadyExistsException.class, () -> {
			bookCategoryService.registerBookCategory(book.getId(), List.of(category1.getId(), category2.getId()));
		});

		verify(bookCategoryRepository, times(1)).save(any(BookCategory.class));
	}

	@Test
	@DisplayName("도서에 카테고리 등록 시 카테고리 최대치 한계로 인해 일부만 등록되는 경우")
	void registerBookCategory_with_categoryIdList_fail_already_max_category() {
		Book book = Book.builder().id(1L).build();

		Category category1 = Category.builder().build();
		ReflectionTestUtils.setField(category1, "id", 1L);

		Category category2 = Category.builder().build();
		ReflectionTestUtils.setField(category2, "id", 2L);

		when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
		when(categoryRepository.findById(1L)).thenReturn(Optional.of(category1));
		when(categoryRepository.findById(2L)).thenReturn(Optional.of(category2));
		when(bookCategoryRepository.countByBook(book)).thenReturn(9L, 10L);
		when(bookCategoryRepository.existsByBookAndCategory(book, category1)).thenReturn(false);
		when(bookCategoryRepository.existsByBookAndCategory(book, category2)).thenReturn(false);

		assertThrows(BookCategoryLimitExceededException.class, () -> {
			bookCategoryService.registerBookCategory(book.getId(), List.of(category1.getId(), 2L));
		});

		verify(bookCategoryRepository, times(1)).save(any(BookCategory.class));
	}


	@Test
	@DisplayName("도서의 특정 카테고리 수정 성공 (A -> B)")
	void updateBookCategory_success() {
		Book book = Book.builder().id(1L).build();

		Category originalCategory = Category.builder().build();
		ReflectionTestUtils.setField(originalCategory, "id", 1L);

		Category updatedCategory = Category.builder().build();
		ReflectionTestUtils.setField(updatedCategory, "id", 2L);

		BookCategory bookCategory = BookCategory.builder()
			.book(book)
			.category(originalCategory)
			.build();
		ReflectionTestUtils.setField(bookCategory, "id", 1L);

		when(bookCategoryRepository.findByBook_Id(book.getId())).thenReturn(List.of(bookCategory));
		doNothing().when(bookCategoryRepository).deleteAll(anyList());

		when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));

		when(categoryRepository.findById(updatedCategory.getId())).thenReturn(Optional.of(updatedCategory));
		when(bookCategoryRepository.existsByBookAndCategory(book, updatedCategory)).thenReturn(false);
		when(bookCategoryRepository.countByBook(book)).thenReturn(0L);

		bookCategoryService.updateBookCategory(book.getId(), List.of(updatedCategory.getId()));
		verify(bookCategoryRepository, times(1)).save(any(BookCategory.class));
	}

	@Test
	@DisplayName("카테고리 아이디로 도서 조회 시 카테고리 아이디가 유효하지 않아 실패하는 경우")
	void searchBooksByCategory_fail_category_id_invalid() {

		when(categoryRepository.findById(999L)).thenReturn(Optional.empty());
		assertThrows(CategoryNotFoundException.class, () -> {
			bookCategoryService.searchBooksByCategory(999L, PageRequest.of(0, 10), BookSortType.NEWEST);
		});
	}


	@Test
	@DisplayName("카테고리 ID로 도서 조회 성공")
	void searchBooksByCategory_success() {
		// Given
		Long categoryId = 1L;

		Category category = Category.builder().build();
		ReflectionTestUtils.setField(category, "id", categoryId);

		when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
		when(categoryRepository.findUnderCategory(category)).thenReturn(new ArrayList<>(List.of(2L, 3L)));

		Book book = Book.builder()
			.id(10L)
			.title("테스트 도서")
			.build();

		BookSaleInfo saleInfo = BookSaleInfo.builder()
			.salePrice(BigDecimal.valueOf(10000))
			.price(BigDecimal.valueOf(12000))
			.star(BigDecimal.valueOf(4.5))
			.reviewCount(12L)
			.viewCount(300L)
			.build();

		when(bookSaleInfoRepository.findByBook(book)).thenReturn(Optional.of(saleInfo));
		when(bookAuthorRepository.findByBookId(book.getId())).thenReturn(List.of(
			BookAuthor.builder().author(Author.builder().name("홍길동").build()).build()
		));

		BookImg img = BookImg.builder()
			.img(Img.builder().imgUrl("https://image.com/book.jpg").build())
			.build();

		when(bookImgRepository.findByBook(book)).thenReturn(img);

		Page<Book> bookPage = new PageImpl<>(List.of(book), PageRequest.of(0, 10), 1);
		when(bookElasticSearchService.searchBooksByCategoryAndSort(anyList(), any(), anyInt(), anyInt()))
			.thenReturn(bookPage);

		// When
		Page<BookInfoResponse> response = bookCategoryService.searchBooksByCategory(
			categoryId, PageRequest.of(0, 10), BookSortType.NEWEST);

		// Then
		assertEquals(1, response.getTotalElements());
		BookInfoResponse bookInfo = response.getContent().get(0);
		assertEquals("테스트 도서", bookInfo.title());
		assertEquals("홍길동", bookInfo.authors().get(0));
		assertEquals("https://image.com/book.jpg", bookInfo.imgUrl());
		assertEquals(BigDecimal.valueOf(10000), bookInfo.salePrice());
		assertEquals(BigDecimal.valueOf(12000), bookInfo.price());
		assertEquals(BigDecimal.valueOf(4.5), bookInfo.star());
		assertEquals(12, bookInfo.reviewCount());
		assertEquals(300, bookInfo.viewCount());
	}


}
