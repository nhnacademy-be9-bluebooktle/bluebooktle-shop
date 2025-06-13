package shop.bluebooktle.backend.book.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.BookImg;
import shop.bluebooktle.backend.book.entity.Img;
import shop.bluebooktle.backend.book.repository.BookImgRepository;
import shop.bluebooktle.backend.book.repository.BookRepository;
import shop.bluebooktle.backend.book.repository.ImgRepository;
import shop.bluebooktle.backend.book.service.impl.BookImgServiceImpl;
import shop.bluebooktle.common.dto.book.response.img.ImgResponse;
import shop.bluebooktle.common.exception.book.BookIdNullException;
import shop.bluebooktle.common.exception.book.BookImgNotFoundException;
import shop.bluebooktle.common.exception.book.BookNotFoundException;

@ExtendWith(MockitoExtension.class)
public class BookImgServiceTest {

	@Mock
	private BookRepository bookRepository;

	@Mock
	private ImgRepository imgRepository;

	@Mock
	private BookImgRepository bookImgRepository;

	@InjectMocks
	private BookImgServiceImpl bookImgService;

	@Test
	@DisplayName("도서 이미지 연결 성공")
	void registerBookImg_success() {

		Long bookId = 1L;
		String imageUrl = "imageUrl";

		Book book = Book.builder().id(bookId).build();
		Img img = Img.builder().imgUrl(imageUrl).build();

		BookImg bookImg = BookImg.builder().book(book).img(img).build();

		when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

		bookImgService.registerBookImg(bookId, imageUrl);

		verify(imgRepository, times(1)).save(img);
		verify(bookImgRepository, times(1)).save(bookImg);
	}

	@Test
	@DisplayName("도서 이미지 연결 실패 - 도서 아이디와 이미지 url이 null인 경우")
	void registerBookImg_fail_bookId_imageUrl_null() {

		Long bookId = null;
		String imageUrl = null;

		assertThrows(IllegalArgumentException.class,
			() -> bookImgService.registerBookImg(bookId, imageUrl)
		);

		verify(imgRepository, never()).save(any(Img.class));
		verify(bookImgRepository, never()).save(any(BookImg.class));

	}

	@Test
	@DisplayName("도서 이미지 연결 실패 - 이미지 url이 빈 칸인 경우")
	void registerBookImg_fail_imageUrl_isBlank() {

		Long bookId = 1L;
		String imageUrl = "";

		assertThrows(IllegalArgumentException.class,
			() -> bookImgService.registerBookImg(bookId, imageUrl)
		);

		verify(imgRepository, never()).save(any(Img.class));
		verify(bookImgRepository, never()).save(any(BookImg.class));

	}

	@Test
	@DisplayName("도서 이미지 연결 실패 - 이미지 url이 null인 경우")
	void registerBookImg_fail_bookId_null_imageUrl_isBlank() {

		Long bookId = 1L;
		String imageUrl = null;

		assertThrows(IllegalArgumentException.class,
			() -> bookImgService.registerBookImg(bookId, imageUrl)
		);

		verify(imgRepository, never()).save(any(Img.class));
		verify(bookImgRepository, never()).save(any(BookImg.class));

	}

	@Test
	@DisplayName("도서 이미지 연결 실패 - 도서 아이디가 유효하지 않은 경우")
	void registerBookImg_fail_bookId_invalid() {

		Long bookId = 1L;
		String imageUrl = "imageUrl";

		when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

		assertThrows(BookNotFoundException.class,
			() -> bookImgService.registerBookImg(bookId, imageUrl)
		);

		verify(imgRepository, never()).save(any(Img.class));
		verify(bookImgRepository, never()).save(any(BookImg.class));
	}

	@Test
	@DisplayName("도서 아이디로 이미지 조회 성공")
	void getImgByBookId_success() {

		Long bookId = 1L;

		Book book = Book.builder().id(bookId).build();
		Img img = Img.builder().imgUrl("imgUrl").build();
		BookImg bookImg = BookImg.builder().book(book).img(img).build();

		when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
		when(bookImgRepository.findBookImgByBook(book)).thenReturn(Optional.of(bookImg));

		ImgResponse imgResponse = bookImgService.getImgByBookId(bookId);

		assertNotNull(imgResponse);
		assertEquals(img.getImgUrl(), imgResponse.getImgUrl());
	}

	@Test
	@DisplayName("도서 아이디로 이미지 조회 실패 - 도서 아이디가 null인 경우")
	void getImgByBookId_fail_bookId_null() {

		Long bookId = null;

		assertThrows(BookIdNullException.class, () -> {
			bookImgService.getImgByBookId(bookId);
		});

		verify(bookRepository, never()).findById(bookId);
		verify(bookImgRepository, never()).findBookImgByBook(any(Book.class));
	}

	@Test
	@DisplayName("도서 아이디로 이미지 조회 실패 - 도서 아이디가 유효하지 않은 경우")
	void getImgByBookId_fail_bookId_invalid() {

		Long bookId = 1L;

		assertThrows(BookNotFoundException.class, () -> {
			bookImgService.getImgByBookId(bookId);
		});

		verify(bookRepository, times(1)).findById(bookId);
		verify(bookImgRepository, never()).findBookImgByBook(any(Book.class));
	}

	@Test
	@DisplayName("도서 아이디로 이미지 조회 실패 - 도서와 연관된 이미지가 존재하지 않은 경우")
	void getImgByBookId_fail_bookId_noBookImg() {

		Long bookId = 1L;

		Book book = Book.builder().id(bookId).build();

		when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

		assertThrows(BookImgNotFoundException.class, () -> {
			bookImgService.getImgByBookId(bookId);
		});

		verify(bookRepository, times(1)).findById(bookId);
		verify(bookImgRepository, times(1)).findBookImgByBook(book);
	}

	@Test
	@DisplayName("도서 이미지 교체 성공")
	void updateBookImg_success() {

		Long bookId = 1L;
		String oldImageUrl = "oldImageUrl";
		String newImageUrl = "newImageUrl";

		Book book = Book.builder().id(bookId).build();
		Img oldImg = Img.builder().imgUrl(oldImageUrl).build();

		BookImg bookImg = BookImg.builder().book(book).img(oldImg).build();

		List<BookImg> bookImgList = List.of(bookImg);

		when(bookImgRepository.findByBookId(bookId)).thenReturn(bookImgList);
		when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

		Img newImg = Img.builder().imgUrl(newImageUrl).build();

		bookImgService.updateBookImg(bookId, newImageUrl);

		ArgumentCaptor<Img> imgCaptor = ArgumentCaptor.forClass(Img.class);
		ArgumentCaptor<BookImg> bookImgCaptor = ArgumentCaptor.forClass(BookImg.class);

		verify(imgRepository, times(1)).save(imgCaptor.capture());
		Img savedImg = imgCaptor.getValue();
		assertEquals(newImageUrl, savedImg.getImgUrl());

		verify(bookImgRepository, times(1)).deleteAll(bookImgList);
		verify(bookImgRepository, times(1)).save(bookImgCaptor.capture());
		BookImg savedBookImg = bookImgCaptor.getValue();
		assertEquals(bookId, savedBookImg.getBook().getId());
	}

	@Test
	@DisplayName("도서 이미지 교체 실패 - 해당 도서에 연관된 이미지가 없는 경우")
	void updateBookImg_fail_bookId_noBookImg() {

		Long bookId = 1L;
		String imageUrl = "imageUrl";

		List<BookImg> bookImgList = List.of();

		when(bookImgRepository.findByBookId(bookId)).thenReturn(bookImgList);

		assertThrows(BookImgNotFoundException.class,
			() -> bookImgService.updateBookImg(bookId, imageUrl)
		);
	}

}
