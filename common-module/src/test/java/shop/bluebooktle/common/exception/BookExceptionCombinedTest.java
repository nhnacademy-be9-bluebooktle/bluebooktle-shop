package shop.bluebooktle.common.exception;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import shop.bluebooktle.common.exception.book.AladinBookNotFoundException;
import shop.bluebooktle.common.exception.book.AuthorAlreadyExistsException;
import shop.bluebooktle.common.exception.book.AuthorCannotDeleteException;
import shop.bluebooktle.common.exception.book.AuthorFieldNullException;
import shop.bluebooktle.common.exception.book.AuthorIdNullException;
import shop.bluebooktle.common.exception.book.AuthorNotFoundException;
import shop.bluebooktle.common.exception.book.AuthorUpdateFieldMissingException;
import shop.bluebooktle.common.exception.book.BookAlreadyExistsException;
import shop.bluebooktle.common.exception.book.BookAuthorAlreadyExistsException;
import shop.bluebooktle.common.exception.book.BookAuthorNotFoundException;
import shop.bluebooktle.common.exception.book.BookCategoryAlreadyExistsException;
import shop.bluebooktle.common.exception.book.BookCategoryLimitExceededException;
import shop.bluebooktle.common.exception.book.BookCategoryNotFoundException;
import shop.bluebooktle.common.exception.book.BookCategoryRequiredException;
import shop.bluebooktle.common.exception.book.BookIdNullException;
import shop.bluebooktle.common.exception.book.BookImgAlreadyExistsException;
import shop.bluebooktle.common.exception.book.BookImgNotFoundException;
import shop.bluebooktle.common.exception.book.BookLikesAlreadyChecked;
import shop.bluebooktle.common.exception.book.BookNotFoundException;
import shop.bluebooktle.common.exception.book.BookPublisherAlreadyExistsException;
import shop.bluebooktle.common.exception.book.BookPublisherNotFoundException;
import shop.bluebooktle.common.exception.book.BookSaleInfoAlreadyExistsException;
import shop.bluebooktle.common.exception.book.BookSaleInfoNotFoundException;
import shop.bluebooktle.common.exception.book.BookTagAlreadyExistsException;
import shop.bluebooktle.common.exception.book.BookTagNotFoundException;
import shop.bluebooktle.common.exception.book.CategoryAlreadyExistsException;
import shop.bluebooktle.common.exception.book.CategoryCannotDeleteException;
import shop.bluebooktle.common.exception.book.CategoryCannotDeleteRootException;
import shop.bluebooktle.common.exception.book.CategoryNotFoundException;
import shop.bluebooktle.common.exception.book.ImgAlreadyExistsException;
import shop.bluebooktle.common.exception.book.ImgIdNullException;
import shop.bluebooktle.common.exception.book.ImgNotFoundException;
import shop.bluebooktle.common.exception.book.ImgUrlEmptyException;
import shop.bluebooktle.common.exception.book.PublisherAlreadyExistsException;
import shop.bluebooktle.common.exception.book.PublisherCannotDeleteException;
import shop.bluebooktle.common.exception.book.PublisherCreateException;
import shop.bluebooktle.common.exception.book.PublisherDeleteException;
import shop.bluebooktle.common.exception.book.PublisherListFetchException;
import shop.bluebooktle.common.exception.book.PublisherNotFoundException;
import shop.bluebooktle.common.exception.book.PublisherUpdateException;
import shop.bluebooktle.common.exception.book.ReviewAuthorizationException;
import shop.bluebooktle.common.exception.book.TagAlreadyExistsException;
import shop.bluebooktle.common.exception.book.TagCreateException;
import shop.bluebooktle.common.exception.book.TagDeleteException;
import shop.bluebooktle.common.exception.book.TagListFetchException;
import shop.bluebooktle.common.exception.book.TagNotFoundException;
import shop.bluebooktle.common.exception.book.TagUpdateException;

class BookExceptionCombinedTest {

	@Test
	@DisplayName("AladinBookNotFoundException - message only")
	void testAladinBookNotFoundException() {
		String message = "Book not found in Aladin";
		AladinBookNotFoundException exception = new AladinBookNotFoundException(message);

		assertThat(exception.getMessage()).isEqualTo(message);
	}

	@Test
	@DisplayName("AuthorAlreadyExistsException - default constructor")
	void testAuthorAlreadyExistsException_default() {
		AuthorAlreadyExistsException exception = new AuthorAlreadyExistsException();

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AUTHOR_ALREADY_EXISTS);
		assertThat(exception.getMessage()).isEqualTo(ErrorCode.AUTHOR_ALREADY_EXISTS.getMessage());
	}

	@Test
	@DisplayName("AuthorAlreadyExistsException - message constructor")
	void testAuthorAlreadyExistsException_message() {
		String name = "홍길동";
		AuthorAlreadyExistsException exception = new AuthorAlreadyExistsException(name);

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AUTHOR_ALREADY_EXISTS);
		assertThat(exception.getMessage()).contains("Author already exists with name: " + name);
	}

	@Test
	@DisplayName("AuthorAlreadyExistsException - cause constructor")
	void testAuthorAlreadyExistsException_cause() {
		Throwable cause = new RuntimeException("DB Error");
		AuthorAlreadyExistsException exception = new AuthorAlreadyExistsException(cause);

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AUTHOR_ALREADY_EXISTS);
		assertThat(exception.getCause()).isEqualTo(cause);
	}

	@Test
	@DisplayName("AuthorAlreadyExistsException - message + cause constructor")
	void testAuthorAlreadyExistsException_messageAndCause() {
		String message = "중복";
		Throwable cause = new RuntimeException("중복 원인");
		AuthorAlreadyExistsException exception = new AuthorAlreadyExistsException(message, cause);

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AUTHOR_ALREADY_EXISTS);
		assertThat(exception.getMessage()).contains(message);
		assertThat(exception.getCause()).isEqualTo(cause);
	}

	@Test
	@DisplayName("AuthorCannotDeleteException - all constructors")
	void testAuthorCannotDeleteException() {
		AuthorCannotDeleteException ex1 = new AuthorCannotDeleteException();
		assertThat(ex1.getErrorCode()).isEqualTo(ErrorCode.AUTHOR_DELETE_NOT_AVAILABLE);

		String message = "삭제 불가";
		AuthorCannotDeleteException ex2 = new AuthorCannotDeleteException(message);
		assertThat(ex2.getMessage()).contains(message);

		Throwable cause = new RuntimeException("참조 중");
		AuthorCannotDeleteException ex3 = new AuthorCannotDeleteException(cause);
		assertThat(ex3.getCause()).isEqualTo(cause);

		AuthorCannotDeleteException ex4 = new AuthorCannotDeleteException(message, cause);
		assertThat(ex4.getMessage()).contains(message);
		assertThat(ex4.getCause()).isEqualTo(cause);
	}

	@Test
	@DisplayName("AuthorFieldNullException - default only")
	void testAuthorFieldNullException() {
		AuthorFieldNullException exception = new AuthorFieldNullException();

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AUTHOR_FIELD_NULL);
		assertThat(exception.getMessage()).contains("must not be null");
	}

	@Test
	@DisplayName("AuthorIdNullException - default only")
	void testAuthorIdNullException() {
		AuthorIdNullException exception = new AuthorIdNullException();

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AUTHOR_ID_NULL);
		assertThat(exception.getMessage()).contains("must not be null");
	}

	@Test
	@DisplayName("AuthorNotFoundException - default constructor")
	void testAuthorNotFoundException_default() {
		AuthorNotFoundException exception = new AuthorNotFoundException();

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AUTHOR_NOT_FOUND);
		assertThat(exception.getMessage()).isEqualTo(ErrorCode.AUTHOR_NOT_FOUND.getMessage());
	}

	@Test
	@DisplayName("AuthorNotFoundException - ID constructor")
	void testAuthorNotFoundException_withId() {
		Long id = 42L;
		AuthorNotFoundException exception = new AuthorNotFoundException(id);

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AUTHOR_NOT_FOUND);
		assertThat(exception.getMessage()).contains("Author not found with ID: " + id);
	}

	@Test
	@DisplayName("AuthorUpdateFieldMissingException - default only")
	void testAuthorUpdateFieldMissingException() {
		AuthorUpdateFieldMissingException exception = new AuthorUpdateFieldMissingException();

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AUTHOR_UPDATE_FIELD_MISSING);
		assertThat(exception.getMessage()).contains("must be provided to update author");
	}

	@Test
	@DisplayName("BookAlreadyExistsException - 모든 생성자")
	void testBookAlreadyExistsException() {
		BookAlreadyExistsException ex1 = new BookAlreadyExistsException();
		assertThat(ex1.getErrorCode()).isEqualTo(ErrorCode.BOOK_ALREADY_EXISTS_EXCEPTION);

		String message = "Book already exists!";
		BookAlreadyExistsException ex2 = new BookAlreadyExistsException(message);
		assertThat(ex2.getMessage()).contains(message);

		Throwable cause = new RuntimeException("DB Error");
		BookAlreadyExistsException ex3 = new BookAlreadyExistsException(cause);
		assertThat(ex3.getCause()).isEqualTo(cause);

		BookAlreadyExistsException ex4 = new BookAlreadyExistsException(message, cause);
		assertThat(ex4.getMessage()).contains(message);
		assertThat(ex4.getCause()).isEqualTo(cause);
	}

	@Test
	@DisplayName("BookAuthorAlreadyExistsException - ID 기반 메시지 생성자")
	void testBookAuthorAlreadyExistsException() {
		BookAuthorAlreadyExistsException exception = new BookAuthorAlreadyExistsException(1L, 2L);

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.BOOK_AUTHOR_ALREADY_EXISTS);
		assertThat(exception.getMessage()).contains("Book(id=1) is already linked to Author(id=2)");
	}

	@Test
	@DisplayName("BookAuthorNotFoundException - 모든 생성자")
	void testBookAuthorNotFoundException() {
		BookAuthorNotFoundException ex1 = new BookAuthorNotFoundException();
		assertThat(ex1.getErrorCode()).isEqualTo(ErrorCode.BOOK_AUTHOR_NOT_FOUND);

		String msg = "No mapping";
		BookAuthorNotFoundException ex2 = new BookAuthorNotFoundException(msg);
		assertThat(ex2.getMessage()).contains(msg);

		Throwable cause = new RuntimeException("not found");
		BookAuthorNotFoundException ex3 = new BookAuthorNotFoundException(cause);
		assertThat(ex3.getCause()).isEqualTo(cause);

		BookAuthorNotFoundException ex4 = new BookAuthorNotFoundException(1L, 2L);
		assertThat(ex4.getMessage()).contains("No link found between Book(id=1) and Author(id=2)");
	}

	@Test
	@DisplayName("BookCategoryAlreadyExistsException - 모든 생성자")
	void testBookCategoryAlreadyExistsException() {
		BookCategoryAlreadyExistsException ex1 = new BookCategoryAlreadyExistsException();
		assertThat(ex1.getErrorCode()).isEqualTo(ErrorCode.BOOK_CATEGORY_ALREADY_EXISTS);

		BookCategoryAlreadyExistsException ex2 = new BookCategoryAlreadyExistsException("중복입니다");
		assertThat(ex2.getMessage()).contains("중복입니다");

		Throwable cause = new RuntimeException("중복");
		BookCategoryAlreadyExistsException ex3 = new BookCategoryAlreadyExistsException(cause);
		assertThat(ex3.getCause()).isEqualTo(cause);

		BookCategoryAlreadyExistsException ex4 = new BookCategoryAlreadyExistsException("중복", cause);
		assertThat(ex4.getMessage()).contains("중복");
		assertThat(ex4.getCause()).isEqualTo(cause);

		BookCategoryAlreadyExistsException ex5 = new BookCategoryAlreadyExistsException(1L, 10L);
		assertThat(ex5.getMessage()).contains("Book category with id 1 already exists in category 10");
	}

	@Test
	@DisplayName("BookCategoryLimitExceededException - 모든 생성자")
	void testBookCategoryLimitExceededException() {
		BookCategoryLimitExceededException ex1 = new BookCategoryLimitExceededException();
		assertThat(ex1.getErrorCode()).isEqualTo(ErrorCode.BOOK_CATEGORY_LIMIT_EXCEEDED);

		BookCategoryLimitExceededException ex2 = new BookCategoryLimitExceededException("Too many categories");
		assertThat(ex2.getMessage()).contains("Too many");

		Throwable cause = new RuntimeException("Limit error");
		BookCategoryLimitExceededException ex3 = new BookCategoryLimitExceededException(cause);
		assertThat(ex3.getCause()).isEqualTo(cause);

		BookCategoryLimitExceededException ex4 = new BookCategoryLimitExceededException("Limit exceeded", cause);
		assertThat(ex4.getMessage()).contains("Limit exceeded");
		assertThat(ex4.getCause()).isEqualTo(cause);

		BookCategoryLimitExceededException ex5 = new BookCategoryLimitExceededException(5L);
		assertThat(ex5.getMessage()).contains("Book(id=5) can have at most 10 categories.");
	}

	@Test
	@DisplayName("BookCategoryNotFoundException - 모든 생성자")
	void testBookCategoryNotFoundException() {
		BookCategoryNotFoundException ex1 = new BookCategoryNotFoundException();
		assertThat(ex1.getErrorCode()).isEqualTo(ErrorCode.BOOK_CATEGORY_NOT_FOUND);

		BookCategoryNotFoundException ex2 = new BookCategoryNotFoundException("없음");
		assertThat(ex2.getMessage()).contains("없음");

		Throwable cause = new RuntimeException("No mapping");
		BookCategoryNotFoundException ex3 = new BookCategoryNotFoundException(cause);
		assertThat(ex3.getCause()).isEqualTo(cause);

		BookCategoryNotFoundException ex4 = new BookCategoryNotFoundException("없음", cause);
		assertThat(ex4.getMessage()).contains("없음");
		assertThat(ex4.getCause()).isEqualTo(cause);

		BookCategoryNotFoundException ex5 = new BookCategoryNotFoundException(3L, 7L);
		assertThat(ex5.getMessage()).contains("Book category with id 3 does not exist in category 7");
	}

	@Test
	@DisplayName("BookCategoryRequiredException - 생성자들")
	void testBookCategoryRequiredException() {
		BookCategoryRequiredException ex1 = new BookCategoryRequiredException();
		assertThat(ex1.getErrorCode()).isEqualTo(ErrorCode.BOOK_CATEGORY_REQUIRED);

		BookCategoryRequiredException ex2 = new BookCategoryRequiredException("Required category");
		assertThat(ex2.getMessage()).contains("Required");

		Throwable cause = new RuntimeException("Missing category");
		BookCategoryRequiredException ex3 = new BookCategoryRequiredException(cause);
		assertThat(ex3.getCause()).isEqualTo(cause);

		BookCategoryRequiredException ex4 = new BookCategoryRequiredException(99L);
		assertThat(ex4.getMessage()).contains("Book (id=99) must belong to at least one category.");
	}

	@Test
	@DisplayName("BookIdNullException - 기본 생성자")
	void testBookIdNullException() {
		BookIdNullException ex = new BookIdNullException();
		assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.BOOK_ID_NULL);
		assertThat(ex.getMessage()).contains(ErrorCode.BOOK_ID_NULL.getMessage());
	}

	@Test
	@DisplayName("BookImgAlreadyExistsException - bookId/imgId 메시지")
	void testBookImgAlreadyExistsException() {
		BookImgAlreadyExistsException ex = new BookImgAlreadyExistsException(1L, 2L);
		assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.BOOK_IMG_ALREADY_EXISTS);
		assertThat(ex.getMessage()).contains("Book(id=1) and Img(id=2) mapping already exists");
	}

	@Test
	@DisplayName("BookImgNotFoundException - 모든 생성자")
	void testBookImgNotFoundException() {
		BookImgNotFoundException ex1 = new BookImgNotFoundException();
		assertThat(ex1.getErrorCode()).isEqualTo(ErrorCode.BOOK_IMG_NOT_FOUND);

		BookImgNotFoundException ex2 = new BookImgNotFoundException("img not found");
		assertThat(ex2.getMessage()).contains("img not found");

		Throwable cause = new RuntimeException("missing");
		BookImgNotFoundException ex3 = new BookImgNotFoundException(cause);
		assertThat(ex3.getCause()).isEqualTo(cause);

		BookImgNotFoundException ex4 = new BookImgNotFoundException(5L, 6L);
		assertThat(ex4.getMessage()).contains("Book(id=5) and Img(id=6) mapping not found");
	}

	@Test
	@DisplayName("BookLikesAlreadyChecked - 모든 생성자")
	void testBookLikesAlreadyChecked() {
		BookLikesAlreadyChecked ex1 = new BookLikesAlreadyChecked();
		assertThat(ex1.getErrorCode()).isEqualTo(ErrorCode.BOOK_ALREADY_LIKED);

		BookLikesAlreadyChecked ex2 = new BookLikesAlreadyChecked("Already liked");
		assertThat(ex2.getMessage()).contains("Already liked");

		Throwable cause = new RuntimeException("Liked");
		BookLikesAlreadyChecked ex3 = new BookLikesAlreadyChecked(cause);
		assertThat(ex3.getCause()).isEqualTo(cause);

		BookLikesAlreadyChecked ex4 = new BookLikesAlreadyChecked("Again", cause);
		assertThat(ex4.getMessage()).contains("Again");
		assertThat(ex4.getCause()).isEqualTo(cause);
	}

	@Test
	@DisplayName("BookNotFoundException - 모든 생성자")
	void testBookNotFoundException() {
		BookNotFoundException ex1 = new BookNotFoundException();
		assertThat(ex1.getErrorCode()).isEqualTo(ErrorCode.BOOK_NOT_FOUND);

		BookNotFoundException ex2 = new BookNotFoundException("book missing");
		assertThat(ex2.getMessage()).contains("book missing");

		Throwable cause = new RuntimeException("none");
		BookNotFoundException ex3 = new BookNotFoundException(cause);
		assertThat(ex3.getCause()).isEqualTo(cause);

		BookNotFoundException ex4 = new BookNotFoundException("again", cause);
		assertThat(ex4.getMessage()).contains("again");
		assertThat(ex4.getCause()).isEqualTo(cause);
	}

	@Test
	@DisplayName("BookPublisherAlreadyExistsException - 모든 생성자")
	void testBookPublisherAlreadyExistsException() {
		BookPublisherAlreadyExistsException ex1 = new BookPublisherAlreadyExistsException();
		assertThat(ex1.getErrorCode()).isEqualTo(ErrorCode.BOOK_PUBLISHER_ALREADY_EXISTS);

		BookPublisherAlreadyExistsException ex2 = new BookPublisherAlreadyExistsException("exists");
		assertThat(ex2.getMessage()).contains("exists");

		Throwable cause = new RuntimeException("conflict");
		BookPublisherAlreadyExistsException ex3 = new BookPublisherAlreadyExistsException(cause);
		assertThat(ex3.getCause()).isEqualTo(cause);

		BookPublisherAlreadyExistsException ex4 = new BookPublisherAlreadyExistsException("double", cause);
		assertThat(ex4.getMessage()).contains("double");
		assertThat(ex4.getCause()).isEqualTo(cause);

		BookPublisherAlreadyExistsException ex5 = new BookPublisherAlreadyExistsException(10L, 20L);
		assertThat(ex5.getMessage()).contains("bookId=10, publisherId=20");
	}

	@Test
	@DisplayName("BookPublisherNotFoundException - 모든 생성자")
	void testBookPublisherNotFoundException() {
		BookPublisherNotFoundException ex1 = new BookPublisherNotFoundException();
		assertThat(ex1.getErrorCode()).isEqualTo(ErrorCode.BOOK_PUBLISHER_ALREADY_EXISTS);

		BookPublisherNotFoundException ex2 = new BookPublisherNotFoundException("not found");
		assertThat(ex2.getMessage()).contains("not found");

		Throwable cause = new RuntimeException("no relation");
		BookPublisherNotFoundException ex3 = new BookPublisherNotFoundException(cause);
		assertThat(ex3.getCause()).isEqualTo(cause);

		BookPublisherNotFoundException ex4 = new BookPublisherNotFoundException("missing", cause);
		assertThat(ex4.getMessage()).contains("missing");

		BookPublisherNotFoundException ex5 = new BookPublisherNotFoundException(100L, 200L);
		assertThat(ex5.getMessage()).contains("bookId=100, publisherId=200");
	}

	@Test
	@DisplayName("BookSaleInfoAlreadyExistsException - 모든 생성자")
	void testBookSaleInfoAlreadyExistsException() {
		BookSaleInfoAlreadyExistsException ex1 = new BookSaleInfoAlreadyExistsException();
		assertThat(ex1.getErrorCode()).isEqualTo(ErrorCode.BOOK_SALE_INFO_ALREADY_EXISTS);

		BookSaleInfoAlreadyExistsException ex2 = new BookSaleInfoAlreadyExistsException("sale exists");
		assertThat(ex2.getMessage()).contains("sale exists");

		Throwable cause = new RuntimeException("sale error");
		BookSaleInfoAlreadyExistsException ex3 = new BookSaleInfoAlreadyExistsException(cause);
		assertThat(ex3.getCause()).isEqualTo(cause);

		BookSaleInfoAlreadyExistsException ex4 = new BookSaleInfoAlreadyExistsException("dup", cause);
		assertThat(ex4.getMessage()).contains("dup");
		assertThat(ex4.getCause()).isEqualTo(cause);
	}

	@Test
	@DisplayName("BookSaleInfoNotFoundException - 모든 생성자")
	void testBookSaleInfoNotFoundException() {
		BookSaleInfoNotFoundException ex1 = new BookSaleInfoNotFoundException();
		assertThat(ex1.getErrorCode()).isEqualTo(ErrorCode.BOOK_SALE_INFO_NOT_FOUND);

		BookSaleInfoNotFoundException ex2 = new BookSaleInfoNotFoundException("sale not found");
		assertThat(ex2.getMessage()).contains("sale not found");

		Throwable cause = new RuntimeException("not there");
		BookSaleInfoNotFoundException ex3 = new BookSaleInfoNotFoundException(cause);
		assertThat(ex3.getCause()).isEqualTo(cause);

		BookSaleInfoNotFoundException ex4 = new BookSaleInfoNotFoundException("again", cause);
		assertThat(ex4.getMessage()).contains("again");
		assertThat(ex4.getCause()).isEqualTo(cause);
	}

	@Test
	@DisplayName("BookTagAlreadyExistsException - 모든 생성자")
	void testBookTagAlreadyExistsException() {
		BookTagAlreadyExistsException ex1 = new BookTagAlreadyExistsException();
		assertThat(ex1.getErrorCode()).isEqualTo(ErrorCode.BOOK_TAG_ALREADY_EXISTS);

		BookTagAlreadyExistsException ex2 = new BookTagAlreadyExistsException("exists");
		assertThat(ex2.getMessage()).contains("exists");

		Throwable cause = new RuntimeException("tag error");
		BookTagAlreadyExistsException ex3 = new BookTagAlreadyExistsException(cause);
		assertThat(ex3.getCause()).isEqualTo(cause);

		BookTagAlreadyExistsException ex4 = new BookTagAlreadyExistsException("msg", cause);
		assertThat(ex4.getMessage()).contains("msg");

		BookTagAlreadyExistsException ex5 = new BookTagAlreadyExistsException(111L, 222L);
		assertThat(ex5.getMessage()).contains("bookId=111, tagId=222");
	}

	@Test
	@DisplayName("BookTagNotFoundException - 모든 생성자")
	void testBookTagNotFoundException() {
		BookTagNotFoundException ex1 = new BookTagNotFoundException();
		assertThat(ex1.getErrorCode()).isEqualTo(ErrorCode.BOOK_TAG_NOT_FOUND);

		BookTagNotFoundException ex2 = new BookTagNotFoundException("없음");
		assertThat(ex2.getMessage()).contains("없음");

		Throwable cause = new RuntimeException("없음");
		BookTagNotFoundException ex3 = new BookTagNotFoundException(cause);
		assertThat(ex3.getCause()).isEqualTo(cause);

		BookTagNotFoundException ex4 = new BookTagNotFoundException("x", cause);
		assertThat(ex4.getMessage()).contains("x");

		BookTagNotFoundException ex5 = new BookTagNotFoundException(300L, 301L);
		assertThat(ex5.getMessage()).contains("bookId=300, publisherId=301");
	}

	@Test
	@DisplayName("CategoryAlreadyExistsException - 모든 생성자")
	void testCategoryAlreadyExistsException() {
		CategoryAlreadyExistsException ex1 = new CategoryAlreadyExistsException();
		assertThat(ex1.getErrorCode()).isEqualTo(ErrorCode.CATEGORY_NAME_ALREADY_EXISTS);

		CategoryAlreadyExistsException ex2 = new CategoryAlreadyExistsException("중복 이름");
		assertThat(ex2.getMessage()).contains("중복");

		Throwable cause = new RuntimeException("중복");
		CategoryAlreadyExistsException ex3 = new CategoryAlreadyExistsException(cause);
		assertThat(ex3.getCause()).isEqualTo(cause);

		CategoryAlreadyExistsException ex4 = new CategoryAlreadyExistsException("중복", cause);
		assertThat(ex4.getMessage()).contains("중복");
		assertThat(ex4.getCause()).isEqualTo(cause);
	}

	@Test
	@DisplayName("CategoryCannotDeleteException - 모든 생성자")
	void testCategoryCannotDeleteException() {
		CategoryCannotDeleteException ex1 = new CategoryCannotDeleteException();
		assertThat(ex1.getErrorCode()).isEqualTo(ErrorCode.CATEGORY_REQUIRED);

		CategoryCannotDeleteException ex2 = new CategoryCannotDeleteException("필수 카테고리");
		assertThat(ex2.getMessage()).contains("필수");

		Throwable cause = new RuntimeException("삭제 불가");
		CategoryCannotDeleteException ex3 = new CategoryCannotDeleteException(cause);
		assertThat(ex3.getCause()).isEqualTo(cause);

		CategoryCannotDeleteException ex4 = new CategoryCannotDeleteException("불가", cause);
		assertThat(ex4.getMessage()).contains("불가");
		assertThat(ex4.getCause()).isEqualTo(cause);
	}

	@Test
	@DisplayName("CategoryCannotDeleteRootException - 모든 생성자")
	void testCategoryCannotDeleteRootException() {
		CategoryCannotDeleteRootException ex1 = new CategoryCannotDeleteRootException();
		assertThat(ex1.getErrorCode()).isEqualTo(ErrorCode.CATEGORY_DELETE_NOT_ALLOWED);

		CategoryCannotDeleteRootException ex2 = new CategoryCannotDeleteRootException("root");
		assertThat(ex2.getMessage()).contains(ErrorCode.CATEGORY_DELETE_NOT_ALLOWED.getMessage()).contains("root");

		Throwable cause = new RuntimeException("최상위");
		CategoryCannotDeleteRootException ex3 = new CategoryCannotDeleteRootException(cause);
		assertThat(ex3.getCause()).isEqualTo(cause);

		CategoryCannotDeleteRootException ex4 = new CategoryCannotDeleteRootException("메시지", cause);
		assertThat(ex4.getMessage()).contains("메시지");
		assertThat(ex4.getCause()).isEqualTo(cause);
	}

	@Test
	@DisplayName("CategoryNotFoundException - 모든 생성자")
	void testCategoryNotFoundException() {
		CategoryNotFoundException ex1 = new CategoryNotFoundException();
		assertThat(ex1.getErrorCode()).isEqualTo(ErrorCode.CATEGORY_NOT_FOUND);

		CategoryNotFoundException ex2 = new CategoryNotFoundException("not found");
		assertThat(ex2.getMessage()).contains("not found");

		Throwable cause = new RuntimeException("실패");
		CategoryNotFoundException ex3 = new CategoryNotFoundException(cause);
		assertThat(ex3.getCause()).isEqualTo(cause);

		CategoryNotFoundException ex4 = new CategoryNotFoundException("없음", cause);
		assertThat(ex4.getMessage()).contains("없음");

		CategoryNotFoundException ex5 = new CategoryNotFoundException(99L);
		assertThat(ex5.getMessage()).contains("Category with id 99 does not exist");
	}

	@Test
	@DisplayName("ImgAlreadyExistsException - url 기반 메시지")
	void testImgAlreadyExistsException() {
		ImgAlreadyExistsException ex = new ImgAlreadyExistsException("http://image.com/a.jpg");
		assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.IMAGE_ALREADY_EXISTS);
		assertThat(ex.getMessage()).contains("http://image.com/a.jpg");
	}

	@Test
	@DisplayName("ImgIdNullException - 모든 생성자")
	void testImgIdNullException() {
		ImgIdNullException ex1 = new ImgIdNullException();
		assertThat(ex1.getErrorCode()).isEqualTo(ErrorCode.IMAGE_ID_NULL);

		ImgIdNullException ex2 = new ImgIdNullException("이미지 ID 누락");
		assertThat(ex2.getMessage()).contains("누락");
	}

	@Test
	@DisplayName("ImgNotFoundException - 기본 생성자")
	void testImgNotFoundException() {
		ImgNotFoundException ex = new ImgNotFoundException();
		assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.IMAGE_NOT_FOUND);
		assertThat(ex.getMessage()).isEqualTo(ErrorCode.IMAGE_NOT_FOUND.getMessage());
	}

	@Test
	@DisplayName("ImgUrlEmptyException - 생성자들")
	void testImgUrlEmptyException() {
		ImgUrlEmptyException ex1 = new ImgUrlEmptyException();
		assertThat(ex1.getErrorCode()).isEqualTo(ErrorCode.IMAGE_URL_EMPTY);

		ImgUrlEmptyException ex2 = new ImgUrlEmptyException("URL 없음");
		assertThat(ex2.getMessage()).contains("URL 없음");
	}

	@Test
	@DisplayName("PublisherAlreadyExistsException - 모든 생성자")
	void testPublisherAlreadyExistsException() {
		PublisherAlreadyExistsException ex1 = new PublisherAlreadyExistsException();
		assertThat(ex1.getErrorCode()).isEqualTo(ErrorCode.PUBLISHER_ALREADY_EXISTS);

		PublisherAlreadyExistsException ex2 = new PublisherAlreadyExistsException("중복");
		assertThat(ex2.getMessage()).contains(ErrorCode.PUBLISHER_ALREADY_EXISTS.getMessage()).contains("중복");

		Throwable cause = new RuntimeException("에러");
		PublisherAlreadyExistsException ex3 = new PublisherAlreadyExistsException(cause);
		assertThat(ex3.getCause()).isEqualTo(cause);

		PublisherAlreadyExistsException ex4 = new PublisherAlreadyExistsException("중복", cause);
		assertThat(ex4.getMessage()).contains("중복");
		assertThat(ex4.getCause()).isEqualTo(cause);
	}

	@Test
	@DisplayName("PublisherCannotDeleteException - 모든 생성자")
	void testPublisherCannotDeleteException() {
		PublisherCannotDeleteException ex1 = new PublisherCannotDeleteException();
		assertThat(ex1.getErrorCode()).isEqualTo(ErrorCode.PUBLISHER_DELETE_NOT_AVAILABLE);

		PublisherCannotDeleteException ex2 = new PublisherCannotDeleteException("삭제불가");
		assertThat(ex2.getMessage()).contains("삭제불가");

		Throwable cause = new RuntimeException("연결됨");
		PublisherCannotDeleteException ex3 = new PublisherCannotDeleteException(cause);
		assertThat(ex3.getCause()).isEqualTo(cause);

		PublisherCannotDeleteException ex4 = new PublisherCannotDeleteException("메시지", cause);
		assertThat(ex4.getMessage()).contains("메시지");
		assertThat(ex4.getCause()).isEqualTo(cause);
	}

	@Test
	@DisplayName("PublisherCreateException - 모든 생성자")
	void testPublisherCreateException() {
		PublisherCreateException ex1 = new PublisherCreateException();
		assertThat(ex1.getErrorCode()).isEqualTo(ErrorCode.PUBLISHER_CREATE_FAILED);

		PublisherCreateException ex2 = new PublisherCreateException("생성 실패");
		assertThat(ex2.getMessage()).contains(ErrorCode.PUBLISHER_CREATE_FAILED.getMessage()).contains("생성");

		Throwable cause = new RuntimeException("DB 오류");
		PublisherCreateException ex3 = new PublisherCreateException(cause);
		assertThat(ex3.getCause()).isEqualTo(cause);

		PublisherCreateException ex4 = new PublisherCreateException("문제", cause);
		assertThat(ex4.getMessage()).contains("문제");
		assertThat(ex4.getCause()).isEqualTo(cause);
	}

	@Test
	@DisplayName("PublisherDeleteException - 모든 생성자")
	void testPublisherDeleteException() {
		PublisherDeleteException ex1 = new PublisherDeleteException();
		assertThat(ex1.getErrorCode()).isEqualTo(ErrorCode.PUBLISHER_DELETE_FAILED);

		PublisherDeleteException ex2 = new PublisherDeleteException("삭제 실패");
		assertThat(ex2.getMessage()).contains(ErrorCode.PUBLISHER_DELETE_FAILED.getMessage()).contains("삭제");

		Throwable cause = new RuntimeException("삭제 오류");
		PublisherDeleteException ex3 = new PublisherDeleteException(cause);
		assertThat(ex3.getCause()).isEqualTo(cause);

		PublisherDeleteException ex4 = new PublisherDeleteException("삭제 중", cause);
		assertThat(ex4.getMessage()).contains("삭제 중");
		assertThat(ex4.getCause()).isEqualTo(cause);
	}

	@Test
	@DisplayName("PublisherListFetchException - 모든 생성자")
	void testPublisherListFetchException() {
		PublisherListFetchException ex1 = new PublisherListFetchException();
		assertThat(ex1.getErrorCode()).isEqualTo(ErrorCode.PUBLISHER_LIST_FETCH_ERROR);

		PublisherListFetchException ex2 = new PublisherListFetchException("리스트 조회 실패");
		assertThat(ex2.getMessage()).contains(ErrorCode.PUBLISHER_LIST_FETCH_ERROR.getMessage()).contains("리스트");

		Throwable cause = new RuntimeException("네트워크 오류");
		PublisherListFetchException ex3 = new PublisherListFetchException(cause);
		assertThat(ex3.getCause()).isEqualTo(cause);

		PublisherListFetchException ex4 = new PublisherListFetchException("실패", cause);
		assertThat(ex4.getMessage()).contains("실패");
		assertThat(ex4.getCause()).isEqualTo(cause);
	}

	@Test
	@DisplayName("PublisherNotFoundException - 모든 생성자")
	void testPublisherNotFoundException() {
		PublisherNotFoundException ex1 = new PublisherNotFoundException();
		assertThat(ex1.getErrorCode()).isEqualTo(ErrorCode.PUBLISHER_NOT_FOUND);

		PublisherNotFoundException ex2 = new PublisherNotFoundException("없음");
		assertThat(ex2.getMessage()).contains("없음");

		Throwable cause = new RuntimeException("null");
		PublisherNotFoundException ex3 = new PublisherNotFoundException(cause);
		assertThat(ex3.getCause()).isEqualTo(cause);

		PublisherNotFoundException ex4 = new PublisherNotFoundException("없는 출판사", cause);
		assertThat(ex4.getMessage()).contains("없는 출판사");
		assertThat(ex4.getCause()).isEqualTo(cause);

		PublisherNotFoundException ex5 = new PublisherNotFoundException(123L);
		assertThat(ex5.getMessage()).contains("Publisher with id 123 not found");
	}

	@Test
	@DisplayName("PublisherUpdateException - 모든 생성자")
	void testPublisherUpdateException() {
		PublisherUpdateException ex1 = new PublisherUpdateException();
		assertThat(ex1.getErrorCode()).isEqualTo(ErrorCode.PUBLISHER_UPDATE_FAILED);

		PublisherUpdateException ex2 = new PublisherUpdateException("업데이트 실패");
		assertThat(ex2.getMessage()).contains(ErrorCode.PUBLISHER_UPDATE_FAILED.getMessage()).contains("업데이트");

		Throwable cause = new RuntimeException("업데이트 오류");
		PublisherUpdateException ex3 = new PublisherUpdateException(cause);
		assertThat(ex3.getCause()).isEqualTo(cause);

		PublisherUpdateException ex4 = new PublisherUpdateException("오류", cause);
		assertThat(ex4.getMessage()).contains("오류");
		assertThat(ex4.getCause()).isEqualTo(cause);
	}

	@Test
	@DisplayName("ReviewAuthorizationException - 모든 생성자")
	void testReviewAuthorizationException() {
		ReviewAuthorizationException ex1 = new ReviewAuthorizationException();
		assertThat(ex1.getErrorCode()).isEqualTo(ErrorCode.REVIEW_AUTHORIZATION_FAILED);

		ReviewAuthorizationException ex2 = new ReviewAuthorizationException("권한 없음");
		assertThat(ex2.getMessage()).contains(ErrorCode.REVIEW_AUTHORIZATION_FAILED.getMessage()).contains("권한 없음");

		Throwable cause = new RuntimeException("불가");
		ReviewAuthorizationException ex3 = new ReviewAuthorizationException(cause);
		assertThat(ex3.getCause()).isEqualTo(cause);

		ReviewAuthorizationException ex4 = new ReviewAuthorizationException("실패", cause);
		assertThat(ex4.getMessage()).contains("실패");
		assertThat(ex4.getCause()).isEqualTo(cause);

		ReviewAuthorizationException ex5 = new ReviewAuthorizationException(ErrorCode.REVIEW_AUTHORIZATION_FAILED);
		assertThat(ex5.getErrorCode()).isEqualTo(ErrorCode.REVIEW_AUTHORIZATION_FAILED);
	}

	@Test
	@DisplayName("TagAlreadyExistsException - 모든 생성자")
	void testTagAlreadyExistsException() {
		TagAlreadyExistsException ex1 = new TagAlreadyExistsException();
		assertThat(ex1.getErrorCode()).isEqualTo(ErrorCode.TAG_ALREADY_EXISTS);

		TagAlreadyExistsException ex2 = new TagAlreadyExistsException("중복");
		assertThat(ex2.getMessage()).contains(ErrorCode.TAG_ALREADY_EXISTS.getMessage()).contains("중복");

		Throwable cause = new RuntimeException("에러");
		TagAlreadyExistsException ex3 = new TagAlreadyExistsException(cause);
		assertThat(ex3.getCause()).isEqualTo(cause);

		TagAlreadyExistsException ex4 = new TagAlreadyExistsException("중복", cause);
		assertThat(ex4.getMessage()).contains("중복");
		assertThat(ex4.getCause()).isEqualTo(cause);
	}

	@Test
	@DisplayName("TagCreateException - 모든 생성자")
	void testTagCreateException() {
		TagCreateException ex1 = new TagCreateException();
		assertThat(ex1.getErrorCode()).isEqualTo(ErrorCode.TAG_CREATE_FAILED);

		TagCreateException ex2 = new TagCreateException("생성 실패");
		assertThat(ex2.getMessage()).contains(ErrorCode.TAG_CREATE_FAILED.getMessage()).contains("생성");

		Throwable cause = new RuntimeException("실패");
		TagCreateException ex3 = new TagCreateException(cause);
		assertThat(ex3.getCause()).isEqualTo(cause);

		TagCreateException ex4 = new TagCreateException("에러", cause);
		assertThat(ex4.getMessage()).contains("에러");
		assertThat(ex4.getCause()).isEqualTo(cause);
	}

	@Test
	@DisplayName("TagDeleteException - 모든 생성자")
	void testTagDeleteException() {
		TagDeleteException ex1 = new TagDeleteException();
		assertThat(ex1.getErrorCode()).isEqualTo(ErrorCode.TAG_DELETE_FAILED);

		TagDeleteException ex2 = new TagDeleteException("삭제 실패");
		assertThat(ex2.getMessage()).contains(ErrorCode.TAG_DELETE_FAILED.getMessage()).contains("삭제");

		Throwable cause = new RuntimeException("실패");
		TagDeleteException ex3 = new TagDeleteException(cause);
		assertThat(ex3.getCause()).isEqualTo(cause);

		TagDeleteException ex4 = new TagDeleteException("에러", cause);
		assertThat(ex4.getMessage()).contains("에러");
		assertThat(ex4.getCause()).isEqualTo(cause);
	}

	@Test
	@DisplayName("TagListFetchException - 모든 생성자")
	void testTagListFetchException() {
		TagListFetchException ex1 = new TagListFetchException();
		assertThat(ex1.getErrorCode()).isEqualTo(ErrorCode.TAG_LIST_FETCH_ERROR);

		TagListFetchException ex2 = new TagListFetchException("조회 실패");
		assertThat(ex2.getMessage()).contains(ErrorCode.TAG_LIST_FETCH_ERROR.getMessage()).contains("조회");

		Throwable cause = new RuntimeException("조회 실패");
		TagListFetchException ex3 = new TagListFetchException(cause);
		assertThat(ex3.getCause()).isEqualTo(cause);

		TagListFetchException ex4 = new TagListFetchException("에러", cause);
		assertThat(ex4.getMessage()).contains("에러");
		assertThat(ex4.getCause()).isEqualTo(cause);
	}

	@Test
	@DisplayName("TagNotFoundException - 모든 생성자")
	void testTagNotFoundException() {
		TagNotFoundException ex1 = new TagNotFoundException();
		assertThat(ex1.getErrorCode()).isEqualTo(ErrorCode.TAG_NOT_FOUND);

		TagNotFoundException ex2 = new TagNotFoundException("태그 없음");
		assertThat(ex2.getMessage()).contains("태그 없음");

		Throwable cause = new RuntimeException("없음");
		TagNotFoundException ex3 = new TagNotFoundException(cause);
		assertThat(ex3.getCause()).isEqualTo(cause);

		TagNotFoundException ex4 = new TagNotFoundException("에러", cause);
		assertThat(ex4.getMessage()).contains("에러");
		assertThat(ex4.getCause()).isEqualTo(cause);

		TagNotFoundException ex5 = new TagNotFoundException(123L);
		assertThat(ex5.getMessage()).contains("Tag with id 123 not found");
	}

	@Test
	@DisplayName("TagUpdateException - 모든 생성자")
	void testTagUpdateException() {
		TagUpdateException ex1 = new TagUpdateException();
		assertThat(ex1.getErrorCode()).isEqualTo(ErrorCode.TAG_UPDATE_FAILED);

		TagUpdateException ex2 = new TagUpdateException("업데이트 실패");
		assertThat(ex2.getMessage()).contains(ErrorCode.TAG_UPDATE_FAILED.getMessage()).contains("업데이트");

		Throwable cause = new RuntimeException("업데이트 오류");
		TagUpdateException ex3 = new TagUpdateException(cause);
		assertThat(ex3.getCause()).isEqualTo(cause);

		TagUpdateException ex4 = new TagUpdateException("에러", cause);
		assertThat(ex4.getMessage()).contains("에러");
		assertThat(ex4.getCause()).isEqualTo(cause);
	}
}
