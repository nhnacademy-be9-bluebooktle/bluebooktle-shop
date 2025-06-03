package shop.bluebooktle.backend.book.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.BookImg;
import shop.bluebooktle.backend.book.entity.Img;
import shop.bluebooktle.backend.book.repository.BookImgRepository;
import shop.bluebooktle.backend.book.repository.BookRepository;
import shop.bluebooktle.backend.book.repository.ImgRepository;
import shop.bluebooktle.backend.book.service.impl.BookImgServiceImpl;
import shop.bluebooktle.common.dto.book.request.BookImgRegisterRequest;
import shop.bluebooktle.common.dto.book.response.BookImgResponse;
import shop.bluebooktle.common.dto.book.response.BookInfoResponse;
import shop.bluebooktle.common.dto.book.response.img.ImgResponse;
import shop.bluebooktle.common.exception.book.BookIdNullException;
import shop.bluebooktle.common.exception.book.BookImgAlreadyExistsException;
import shop.bluebooktle.common.exception.book.BookImgNotFoundException;
import shop.bluebooktle.common.exception.book.BookNotFoundException;
import shop.bluebooktle.common.exception.book.ImgIdNullException;
import shop.bluebooktle.common.exception.book.ImgNotFoundException;

@ExtendWith(MockitoExtension.class)
public class BookImgServiceTest {

	@Mock
	private BookRepository bookRepository;

	@Mock
	private ImgRepository imgRepository;

	@Mock
	private BookImgRepository bookImgRepository;

	@Mock
	private ImgService imgService;

	@InjectMocks
	private BookImgServiceImpl bookImgService;

	// registerBookImg(Long bookId, BookImgRegisterRequest bookImgRegisterRequest) : 오버로딩 메서드 1

	@Test
	@DisplayName("도서 이미지 연결 성공")
	void registerBookImg_success() {

		Book book = Book.builder()
			.id(1L)
			.build();

		BookImgRegisterRequest bookImgRegisterRequest = BookImgRegisterRequest.builder()
			.imgId(1L)
			.imgUrl("testImgUrl")
			.build();

		Img img = Img.builder()
			.imgUrl(bookImgRegisterRequest.getImgUrl())
			.build();
		ReflectionTestUtils.setField(img, "id", bookImgRegisterRequest.getImgId() );

		when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
		when(imgRepository.findById(1L)).thenReturn(Optional.of(img));
		when(bookImgRepository.existsByBookAndImg(any(Book.class), any(Img.class))).thenReturn(false);

		bookImgService.registerBookImg(1L, bookImgRegisterRequest);

		verify(bookImgRepository, times(1)).save(any(BookImg.class));
	}

	@Test
	@DisplayName("도서 이미지 연결 시 도서 아이디가 null인 경우")
	void registerBookImg_fail_bookId_null() {

		Book book = Book.builder()
			.build();

		BookImgRegisterRequest bookImgRegisterRequest = BookImgRegisterRequest.builder()
			.imgUrl("testImgUrl")
			.build();

		assertThrows(BookIdNullException.class, () -> bookImgService.registerBookImg(book.getId(), bookImgRegisterRequest));
	}

	@Test
	@DisplayName("도서 이미지 연결 시 이미지 아이디가 null인 경우")
	void registerBookImg_fail_imgId_null() {

		Book book = Book.builder()
			.id(1L)
			.build();

		BookImgRegisterRequest bookImgRegisterRequest = BookImgRegisterRequest.builder()
			.imgUrl("testImgUrl")
			.build();

		Img img = Img.builder()
			.imgUrl(bookImgRegisterRequest.getImgUrl())
			.build();

		assertThrows(ImgIdNullException.class, () -> bookImgService.registerBookImg(book.getId(), bookImgRegisterRequest));
	}

	@Test
	@DisplayName("도서 이미지 연결 시 이미지 아이디가 유효하지 않은 경우")
	void registerBookImg_fail_imgId_invalid() {

		BookImgRegisterRequest bookImgRegisterRequest = BookImgRegisterRequest.builder()
			.imgId(1L)
			.imgUrl("testImgUrl")
			.build();

		when(imgRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(ImgNotFoundException.class, () -> bookImgService.registerBookImg(1L, bookImgRegisterRequest));
	}

	@Test
	@DisplayName("도서 이미지 연결 시 도서 아이디가 유효하지 않은 경우")
	void registerBookImg_fail_bookId_invalid() {

		BookImgRegisterRequest bookImgRegisterRequest = BookImgRegisterRequest.builder()
			.imgId(1L)
			.imgUrl("testImgUrl")
			.build();

		Img img = Img.builder()
			.imgUrl(bookImgRegisterRequest.getImgUrl())
			.build();
		ReflectionTestUtils.setField(img, "id", bookImgRegisterRequest.getImgId());

		when(imgRepository.findById(1L)).thenReturn(Optional.of(img));
		when(bookRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(BookNotFoundException.class, () -> bookImgService.registerBookImg(1L, bookImgRegisterRequest));
	}

	@Test
	@DisplayName("도서 이미지 연결 시 도서 이미지 관계가 이미 존재하는 경우")
	void registerBookImg_fail_bookImgAlreadyExist() {

		Book book = Book.builder()
			.id(1L)
			.build();

		BookImgRegisterRequest bookImgRegisterRequest = BookImgRegisterRequest.builder()
			.imgId(1L)
			.imgUrl("testImgUrl")
			.build();

		Img img = Img.builder()
			.imgUrl(bookImgRegisterRequest.getImgUrl())
			.build();
		ReflectionTestUtils.setField(img, "id", bookImgRegisterRequest.getImgId());

		when(imgRepository.findById(1L)).thenReturn(Optional.of(img));
		when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
		when(bookImgRepository.existsByBookAndImg(any(Book.class), any(Img.class))).thenReturn(true);

		assertThrows(BookImgAlreadyExistsException.class, () -> bookImgService.registerBookImg(book.getId(), bookImgRegisterRequest));
	}

	// registerBookImg(Long bookId, String imageUrl) : 오버로딩 메서드 2

	@Test
	@DisplayName("도서 이미지 연결 성공")
	void registerBookImg_with_imgUrl_success() {

		Book book = Book.builder()
			.id(1L)
			.build();

		Img img = Img.builder()
			.imgUrl("testImgUrl")
			.build();

		when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
		when(imgRepository.save(any(Img.class))).thenReturn(Img.builder().imgUrl("testImgUrl").build());

		bookImgService.registerBookImg(book.getId(), img.getImgUrl());

		verify(imgRepository, times(1)).save(any(Img.class));
		verify(bookImgRepository, times(1)).save(any(BookImg.class));
	}

	@Test
	@DisplayName("도서 이미지 연결 시 도서 아이디가 유효하지 않을 때")
	void registerBookImg_with_imgUrl_fail_bookId_invalid() {
		when(bookRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(BookNotFoundException.class, () -> bookImgService.registerBookImg(1L, "testImgUrl"));
	}

	@Test
	@DisplayName("도서 아이디로 이미지 조회 성공")
	void getImgByBookId_success() {

		Long bookId = 1L;

		Book book = Book.builder().id(bookId).build();

		List<Img> images = List.of(Img.builder().imgUrl("testImgUrl").build());

		when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
		when(bookImgRepository.findImagesByBook(book)).thenReturn(images);

		List<ImgResponse> imgResponses = bookImgService.getImgByBookId(bookId);

		assertNotNull(imgResponses);
		assertEquals(1, imgResponses.size());
		assertEquals("testImgUrl", imgResponses.getFirst().getImgUrl());
	}

	@Test
	@DisplayName("도서 아이디로 이미지 조회 시 도서 아이디 null인 경우")
	void getImgByBookId_fail_bookId_null() {
		assertThrows(BookIdNullException.class, () -> bookImgService.getImgByBookId(null));
	}

	@Test
	@DisplayName("도서 아이디로 이미지 조회 시 유효하지 않은 도서 아이디인 경우")
	void getImgByBookId_fail_bookId_invalid() {
		when(bookRepository.findById(1L)).thenReturn(Optional.empty());
		assertThrows(BookNotFoundException.class, () -> bookImgService.getImgByBookId(1L));
	}

	@Test
	@DisplayName("이미지 아이디로 도서 조회 성공")
	void getBookByImgId_success() {

		Long imgId = 1L;

		Img img = Img.builder()
			.imgUrl("testImgUrl")
			.build();
		ReflectionTestUtils.setField(img, "id", imgId);

		Book book = Book.builder()
			.id(1L)
			.build();

		when(imgRepository.findById(imgId)).thenReturn(Optional.of(img));
		when(bookImgRepository.findBooksByImg(img)).thenReturn(List.of(book));

		List<BookInfoResponse> bookInfoResponses = bookImgService.getBookByImgId(imgId);

		assertNotNull(bookInfoResponses);
		assertEquals(1, bookInfoResponses.size());
		assertEquals(1L, bookInfoResponses.getFirst().bookId());
	}

	@Test
	@DisplayName("이미지 아이디로 도서 조회 시 이미지 아이디가 null인 경우")
	void getBookByImgId_fail_imgId_null() {
		assertThrows(ImgIdNullException.class, () -> bookImgService.getBookByImgId(null));
	}

	@Test
	@DisplayName("이미지 아이디로 도서 조회 시 유효하지 않은 이미지 아이디인 경우")
	void getBookByImgId_fail_imgId_invalid() {
		when(imgRepository.findById(1L)).thenReturn(Optional.empty());
		assertThrows(ImgNotFoundException.class, () -> bookImgService.getBookByImgId(1L));
	}

	@Test
	@DisplayName("도서 아이디로 썸네일 조회 성공")
	void getThumbnailByBookId_success() {

		Long bookId = 1L;

		Book book = Book.builder().id(bookId).build();

		Img img = Img.builder()
			.imgUrl("testImgUrl")
			.build();
		ReflectionTestUtils.setField(img, "id", 1L);
		ReflectionTestUtils.setField(img, "createdAt", LocalDateTime.of(2025, 6, 3, 12, 0));

		BookImg bookImg = BookImg.builder()
			.book(book)
			.img(img)
			.isThumbnail(true)
			.build();

		when(bookImgRepository.findByBookId(bookId)).thenReturn(List.of(bookImg));

		Optional<BookImgResponse> bookImgResponse = bookImgService.getThumbnailByBookId(bookId);

		assertTrue(bookImgResponse.isPresent());
		assertEquals(bookImgResponse.get().getImgResponse().getId(), 1L);
		assertEquals(bookImgResponse.get().getImgResponse().getImgUrl(), "testImgUrl");
		assertEquals(bookImgResponse.get().getImgResponse().getCreatedAt(), LocalDateTime.of(2025, 6, 3, 12, 0));
		assertEquals(bookImgResponse.get().getBookInfoResponse().bookId(), bookId);
		assertEquals(bookImgResponse.get().getIsThumbnail(), true);

	}

	@Test
	@DisplayName("도서 아이디로 썸네일 조회 시 도서 아이디가 null인 경우")
	void getThumbnailByBookId_fail_bookId_null() {
		assertThrows(BookIdNullException.class, () -> bookImgService.getThumbnailByBookId(null));
	}

	@Test
	@DisplayName("도서 이미지 관계 삭제 성공")
	void deleteBookImg_success() {
		Long bookId = 1L;
		Long imgId = 1L;

		Book book = Book.builder().id(bookId).build();
		Img img = Img.builder().build();
		ReflectionTestUtils.setField(img, "id", imgId);

		BookImg bookImg = BookImg.builder()
			.book(book)
			.img(img)
			.build();

		when(bookImgRepository.findByBookIdAndImgId(bookId, imgId)).thenReturn(Optional.of(bookImg));

		bookImgService.deleteBookImg(bookId, imgId);

		verify(bookImgRepository, times(1)).delete(bookImg);
	}

	@Test
	@DisplayName("도서 이미지 관계 삭제 시 도서 아이디가 null인 경우")
	void deleteBookImg_fail_bookId_null() {
		assertThrows(BookIdNullException.class, () -> bookImgService.deleteBookImg(null, 1L));
	}

	@Test
	@DisplayName("도서 이미지 관계 삭제 시 이미지 아이디가 null인 경우")
	void deleteBookImg_fail_imgId_null() {
		assertThrows(ImgIdNullException.class, () -> bookImgService.deleteBookImg(1L, null));
	}

	@Test
	@DisplayName("도서 이미지 관계 삭제 시 도서 이미지 관계가 유효하지 않은 경우")
	void deleteBookImg_fail_bookImgNotExist() {
		Long bookId = 1L;
		Long imgId = 1L;

		Book book = Book.builder().id(bookId).build();
		Img img = Img.builder().build();
		ReflectionTestUtils.setField(img, "id", imgId);

		when(bookImgRepository.findByBookIdAndImgId(bookId, imgId)).thenReturn(Optional.empty());

		assertThrows(BookImgNotFoundException.class, () -> bookImgService.deleteBookImg(bookId, imgId));
	}
}
