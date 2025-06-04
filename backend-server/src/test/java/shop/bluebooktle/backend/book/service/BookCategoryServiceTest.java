package shop.bluebooktle.backend.book.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.BookCategory;
import shop.bluebooktle.backend.book.entity.Category;
import shop.bluebooktle.backend.book.repository.BookCategoryRepository;
import shop.bluebooktle.backend.book.repository.BookRepository;
import shop.bluebooktle.backend.book.repository.CategoryRepository;
import shop.bluebooktle.backend.book.service.impl.BookCategoryServiceImpl;
import shop.bluebooktle.common.dto.book.request.BookInfoRequest;
import shop.bluebooktle.common.dto.book.response.BookInfoResponse;
import shop.bluebooktle.common.dto.book.response.CategoryResponse;
import shop.bluebooktle.common.exception.book.BookCategoryAlreadyExistsException;
import shop.bluebooktle.common.exception.book.BookCategoryLimitExceededException;
import shop.bluebooktle.common.exception.book.BookCategoryNotFoundException;
import shop.bluebooktle.common.exception.book.BookCategoryRequiredException;
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

	@InjectMocks
	private BookCategoryServiceImpl bookCategoryService;

	// registerBookCategory(Long bookId, Long categoryId) : 오버로딩 메서드 1

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
	@DisplayName("도서 카테고리 삭제 성공")
	void deleteBookCategory_success() {

		Book book = Book.builder().id(1L).build();

		Category category = Category.builder().build();
		ReflectionTestUtils.setField(category, "id", 1L);

		BookCategory bookCategory = BookCategory.builder()
			.book(book)
			.category(category)
			.build();

		when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
		when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
		when(bookCategoryRepository.findByBookAndCategory(book, category)).thenReturn(Optional.of(bookCategory));
		when(bookCategoryRepository.countByBook(book)).thenReturn(5L);

		bookCategoryService.deleteBookCategory(book.getId(), category.getId());

		ArgumentCaptor<BookCategory> captor = ArgumentCaptor.forClass(BookCategory.class);
		verify(bookCategoryRepository, times(1)).delete(captor.capture());
		BookCategory deletedBookCategory = captor.getValue();
		assertEquals(book, deletedBookCategory.getBook());
		assertEquals(category, deletedBookCategory.getCategory());
		assertEquals(bookCategory, deletedBookCategory);
	}

	@Test
	@DisplayName("도서 카테고리 삭제 시 도서 아이디가 유효하지 않아 실패하는 경우")
	void deleteBookCategory_fail_book_id_invalid() {

		when(bookRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(BookNotFoundException.class, () -> {
			bookCategoryService.deleteBookCategory(1L, 1L);
		});
	}

	@Test
	@DisplayName("도서 카테고리 삭제 시 카테고리 아이디가 유효하지 않아 실패하는 경우")
	void deleteBookCategory_fail_category_id_invalid() {

		Book book = Book.builder().id(1L).build();

		when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
		when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(CategoryNotFoundException.class, () -> {
			bookCategoryService.deleteBookCategory(book.getId(), 1L);
		});
	}

	@Test
	@DisplayName("도서 카테고리 삭제 시 도서 카테고리 관계가 존재하지 않아 실패하는 경우")
	void deleteBookCategory_fail_not_found() {

		Book book = Book.builder().id(1L).build();

		Category category = Category.builder().build();
		ReflectionTestUtils.setField(category, "id", 1L);

		when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
		when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
		when(bookCategoryRepository.findByBookAndCategory(book, category)).thenReturn(Optional.empty());

		assertThrows(BookCategoryNotFoundException.class, () -> {
			bookCategoryService.deleteBookCategory(book.getId(), category.getId());
		});

	}

	@Test
	@DisplayName("해당 도서 카테고리 관계가 도서의 유일한 카테고리여서 실패하는 경우")
	void deleteBookCategory_fail_last_category() {
		Book book = Book.builder().id(1L).build();

		Category category = Category.builder().build();
		ReflectionTestUtils.setField(category, "id", 1L);

		BookCategory bookCategory = BookCategory.builder()
			.book(book)
			.category(category)
			.build();

		when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
		when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
		when(bookCategoryRepository.findByBookAndCategory(book, category)).thenReturn(Optional.of(bookCategory));
		when(bookCategoryRepository.countByBook(book)).thenReturn(1L);

		assertThrows(BookCategoryRequiredException.class, () -> {
			bookCategoryService.deleteBookCategory(book.getId(), category.getId());
		});
	}

	@Test
	@DisplayName("도서의 특정 카테고리 수정 성공 (A -> B)")
	void updateBookCategoryByBookCategoryId_success() {

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

		when(bookCategoryRepository.findById(bookCategory.getId())).thenReturn(Optional.of(bookCategory));
		when(categoryRepository.findById(updatedCategory.getId())).thenReturn(Optional.of(updatedCategory));
		when(bookCategoryRepository.existsByBookAndCategory(book, updatedCategory)).thenReturn(false);

		bookCategoryService.updateBookCategoryByBookCategoryId(updatedCategory.getId(), bookCategory.getId());

		assertEquals(updatedCategory, bookCategory.getCategory());
	}

	@Test
	@DisplayName("도서의 특정 카테고리 수정 시 도서 (교체될 : A) 카테고리 관계가 존재하지 않아 실패하는 경우")
	void updateBookCategoryByBookCategoryId_fail_not_found() {

		Category updatedCategory = Category.builder().build();
		ReflectionTestUtils.setField(updatedCategory, "id", 2L);

		when(bookCategoryRepository.findById(999L)).thenReturn(Optional.empty());

		assertThrows(BookCategoryNotFoundException.class, () -> {
			bookCategoryService.updateBookCategoryByBookCategoryId(updatedCategory.getId(), 999L);
		});
	}

	@Test
	@DisplayName("도서의 특정 카테고리 수정 시 (교체할 : B) 카테고리의 아이디가 유효하지 않아 실패하는 경우")
	void updateBookCategoryByBookCategoryId_fail_category_id_invalid() {
		Book book = Book.builder().id(1L).build();

		Category originalCategory = Category.builder().build();
		ReflectionTestUtils.setField(originalCategory, "id", 1L);

		BookCategory bookCategory = BookCategory.builder()
			.book(book)
			.category(originalCategory)
			.build();
		ReflectionTestUtils.setField(bookCategory, "id", 1L);

		when(bookCategoryRepository.findById(bookCategory.getId())).thenReturn(Optional.of(bookCategory));
		when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

		assertThrows(CategoryNotFoundException.class, () -> {
			bookCategoryService.updateBookCategoryByBookCategoryId(999L, bookCategory.getId());
		});
	}

	@Test
	@DisplayName("도서의 특정 카테고리 수정 시 (교체할 : B) 카테고리와 도서의 관계가 이미 존재해 실패하는 경우")
	void updateBookCategoryByBookCategoryId_fail_already_registered() {
		Book book = Book.builder().id(1L).build();

		Category originalCategory = Category.builder().build();
		ReflectionTestUtils.setField(originalCategory, "id", 1L);

		Category updatedCategory = Category.builder().build();
		ReflectionTestUtils.setField(updatedCategory, "id", 2L);

		BookCategory bookCategory1 = BookCategory.builder()
			.book(book)
			.category(originalCategory)
			.build();
		ReflectionTestUtils.setField(bookCategory1, "id", 1L);

		BookCategory bookCategory2 = BookCategory.builder()
			.book(book)
			.category(updatedCategory)
			.build();
		ReflectionTestUtils.setField(bookCategory2, "id", 2L);

		when(bookCategoryRepository.findById(bookCategory1.getId())).thenReturn(Optional.of(bookCategory1));
		when(categoryRepository.findById(updatedCategory.getId())).thenReturn(Optional.of(updatedCategory));
		when(bookCategoryRepository.existsByBookAndCategory(book, updatedCategory)).thenReturn(true);

		assertThrows(BookCategoryAlreadyExistsException.class, () -> {
			bookCategoryService.updateBookCategoryByBookCategoryId(updatedCategory.getId(), bookCategory1.getId());
		});
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

		when(categoryRepository.findById(originalCategory.getId())).thenReturn(Optional.of(originalCategory));
		when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
		when(bookCategoryRepository.findByBookAndCategory(book, originalCategory)).thenReturn(Optional.of(bookCategory));
		when(categoryRepository.findById(updatedCategory.getId())).thenReturn(Optional.of(updatedCategory));
		when(bookCategoryRepository.existsByBookAndCategory(book, updatedCategory)).thenReturn(false);

		bookCategoryService.updateBookCategory(updatedCategory.getId(), originalCategory.getId(), book.getId());

		assertEquals(updatedCategory, bookCategory.getCategory());
	}

	@Test
	@DisplayName("도서의 특정 카테고리 수정 시 (교체될 : A) 카테고리의 아이디가 유효하지 않아 실패하는 경우")
	void updateBookCategory_fail_original_category_id_invalid() {

		Book book = Book.builder().id(1L).build();

		Category updatedCategory = Category.builder().build();
		ReflectionTestUtils.setField(updatedCategory, "id", 2L);

		when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

		assertThrows(CategoryNotFoundException.class, () -> {
			bookCategoryService.updateBookCategory(updatedCategory.getId(), 999L, book.getId());
		});
	}

	@Test
	@DisplayName("도서의 특정 카테고리 수정 시 도서 아이디가 유효하지 않아 실패하는 경우")
	void updateBookCategory_fail_book_id_invalid() {

		Category originalCategory = Category.builder().build();
		ReflectionTestUtils.setField(originalCategory, "id", 1L);

		Category updatedCategory = Category.builder().build();
		ReflectionTestUtils.setField(updatedCategory, "id", 2L);

		when(categoryRepository.findById(originalCategory.getId())).thenReturn(Optional.of(originalCategory));
		when(bookRepository.findById(999L)).thenReturn(Optional.empty());

		assertThrows(BookNotFoundException.class, () -> {
			bookCategoryService.updateBookCategory(updatedCategory.getId(), originalCategory.getId(), 999L);
		});
	}

	@Test
	@DisplayName("도서의 특정 카테고리 수정 시 도서 (교체될 : A) 카테고리 관계가 존재하지 않아 실패하는 경우")
	void updateBookCategory_fail_book_category_not_found() {

		Book book = Book.builder().id(1L).build();

		Category originalCategory = Category.builder().build();
		ReflectionTestUtils.setField(originalCategory, "id", 1L);

		Category updatedCategory = Category.builder().build();
		ReflectionTestUtils.setField(updatedCategory, "id", 2L);

		when(categoryRepository.findById(originalCategory.getId())).thenReturn(Optional.of(originalCategory));
		when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
		when(bookCategoryRepository.findByBookAndCategory(book, originalCategory)).thenReturn(Optional.empty());

		assertThrows(BookCategoryNotFoundException.class, () -> {
			bookCategoryService.updateBookCategory(updatedCategory.getId(), originalCategory.getId(), book.getId());
		});
	}

	@Test
	@DisplayName("도서의 특정 카테고리 수정 시 (교체할 : B) 카테고리의 아이디가 유효하지 않아 실패하는 경우")
	void updateBookCategory_fail_updated_category_id_invalid() {

		Book book = Book.builder().id(1L).build();

		Category originalCategory = Category.builder().build();
		ReflectionTestUtils.setField(originalCategory, "id", 1L);

		BookCategory bookCategory = BookCategory.builder()
			.book(book)
			.category(originalCategory)
			.build();
		ReflectionTestUtils.setField(bookCategory, "id", 1L);

		when(categoryRepository.findById(originalCategory.getId())).thenReturn(Optional.of(originalCategory));
		when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
		when(bookCategoryRepository.findByBookAndCategory(book, originalCategory)).thenReturn(Optional.of(bookCategory));
		when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

		assertThrows(CategoryNotFoundException.class, () -> {
			bookCategoryService.updateBookCategory(999L, originalCategory.getId(), book.getId());
		});
	}

	@Test
	@DisplayName("도서의 특정 카테고리 수정 시 (교체할 : B) 카테고리와 도서의 관계가 이미 존재해 실패하는 경우")
	void updateBookCategory_fail_updated_category_already_exists() {

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

		BookCategory bookUpdatedCategory = BookCategory.builder()
			.book(book)
			.category(updatedCategory)
			.build();
		ReflectionTestUtils.setField(bookUpdatedCategory, "id", 2L);

		when(categoryRepository.findById(originalCategory.getId())).thenReturn(Optional.of(originalCategory));
		when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
		when(bookCategoryRepository.findByBookAndCategory(book, originalCategory)).thenReturn(Optional.of(bookCategory));
		when(categoryRepository.findById(updatedCategory.getId())).thenReturn(Optional.of(updatedCategory));
		when(bookCategoryRepository.existsByBookAndCategory(book, updatedCategory)).thenReturn(true);

		assertThrows(BookCategoryAlreadyExistsException.class, () -> {
			bookCategoryService.updateBookCategory(updatedCategory.getId(), originalCategory.getId(), book.getId());
		});
	}

	@Test
	@DisplayName("도서 아이디로 카테고리 조회 성공")
	void getCategoryByBookId_success() {

		Book book = Book.builder().id(1L).build();

		BookInfoRequest bookInfoRequest = new BookInfoRequest(book.getId());

		Category parentCategory = Category.builder().build();
		ReflectionTestUtils.setField(parentCategory, "id", 999L);
		ReflectionTestUtils.setField(parentCategory, "name", "상위 카테고리");

		Category category1 = Category.builder().build();
		ReflectionTestUtils.setField(category1, "id", 1L);
		ReflectionTestUtils.setField(category1, "name", "카테고리 1");
		ReflectionTestUtils.setField(category1, "parentCategory", parentCategory);

		Category category2 = Category.builder().build();
		ReflectionTestUtils.setField(category2, "id", 2L);
		ReflectionTestUtils.setField(category2, "name", "카테고리 2");
		ReflectionTestUtils.setField(category2, "parentCategory", parentCategory);

		BookCategory bookCategory1 = BookCategory.builder()
			.book(book)
			.category(category1)
			.build();

		BookCategory bookCategory2 = BookCategory.builder()
			.book(book)
			.category(category2)
			.build();

		List<BookCategory> bookCategories = List.of(bookCategory1, bookCategory2);

		when(bookRepository.findById(bookInfoRequest.bookId())).thenReturn(Optional.of(book));
		when(bookCategoryRepository.findByBook(book)).thenReturn(bookCategories);

		List<CategoryResponse> categoryResponses = bookCategoryService.getCategoryByBookId(bookInfoRequest);

		assertEquals(2, categoryResponses.size());
		assertEquals(category1.getId(), categoryResponses.get(0).categoryId());
		assertEquals(category2.getId(), categoryResponses.get(1).categoryId());
	}

	@Test
	@DisplayName("도서 아이디로 카테고리 조회 시 도서 아이디가 유효하지 않아 실패하는 경우")
	void getCategoryByBookId_fail_book_id_invalid() {

		BookInfoRequest bookInfoRequest = new BookInfoRequest(1L);

		when(bookRepository.findById(bookInfoRequest.bookId())).thenReturn(Optional.empty());

		assertThrows(BookNotFoundException.class, () ->{
			bookCategoryService.getCategoryByBookId(bookInfoRequest);
		});
	}

	@Test
	@DisplayName("카테고리 아이디로 도서 조회 성공")
	void searchBooksByCategory_success() {
		Book book1 = Book.builder().id(1L).build();
		Book book2 = Book.builder().id(2L).build();

		Category category = Category.builder().build();
		ReflectionTestUtils.setField(category, "id", 1L);

		Pageable pageable = PageRequest.of(0, 10);

		BookCategory bookCategory1 = BookCategory.builder()
			.book(book1)
			.category(category)
			.build();

		BookCategory bookCategory2 = BookCategory.builder()
			.book(book2)
			.category(category)
			.build();

		Page<BookCategory> page = new PageImpl<>(List.of(bookCategory1, bookCategory2), pageable, 2);

		when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
		when(bookCategoryRepository.findAllByCategory(category, pageable)).thenReturn(page);

		Page<BookInfoResponse> bookInfoResponses = bookCategoryService.searchBooksByCategory(category.getId(), pageable);

		assertEquals(2, bookInfoResponses.getTotalElements());
		assertEquals(book1.getId(), bookInfoResponses.getContent().get(0).bookId());
		assertEquals(book2.getId(), bookInfoResponses.getContent().get(1).bookId());
		assertEquals(pageable, bookInfoResponses.getPageable());
	}

	@Test
	@DisplayName("카테고리 아이디로 도서 조회 시 카테고리 아이디가 유효하지 않아 실패하는 경우")
	void searchBooksByCategory_fail_category_id_invalid() {

		when(categoryRepository.findById(999L)).thenReturn(Optional.empty());
		assertThrows(CategoryNotFoundException.class, () -> {
			bookCategoryService.searchBooksByCategory(999L, PageRequest.of(0, 10));
		});
	}

}