package shop.bluebooktle.backend.book.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.BookPublisher;
import shop.bluebooktle.backend.book.entity.Publisher;
import shop.bluebooktle.backend.book.repository.BookPublisherRepository;
import shop.bluebooktle.backend.book.repository.BookRepository;
import shop.bluebooktle.backend.book.repository.PublisherRepository;
import shop.bluebooktle.backend.book.service.impl.BookPublisherServiceImpl;
import shop.bluebooktle.common.dto.book.response.PublisherInfoResponse;
import shop.bluebooktle.common.exception.book.BookNotFoundException;
import shop.bluebooktle.common.exception.book.BookPublisherAlreadyExistsException;
import shop.bluebooktle.common.exception.book.PublisherNotFoundException;

@ExtendWith(MockitoExtension.class)
public class BookPublisherServiceTest {

	@Mock
	private PublisherRepository publisherRepository;

	@Mock
	private BookPublisherRepository bookPublisherRepository;

	@Mock
	private BookRepository bookRepository;

	@InjectMocks
	private BookPublisherServiceImpl bookPublisherService;

	private final Long BOOK_ID = 1L;
	private final Long PUBLISHER_ID1 = 10L;
	private final Long PUBLISHER_ID2 = 20L;

	private Book sampleBook() {
		return Book.builder()
			.id(BOOK_ID)
			.title("멀쩡한 어른 되긴 글렀군")
			.description("내 일상에 브레이크를 거는 짱구의 삐딱한 인생 기술")
			.index("")
			.publishDate(LocalDateTime.now())
			.isbn("1234567890123")
			.build();
	}

	private Publisher samplePublisher(Long id) {
		return Publisher.builder()
			.id(id)
			.name("Pubblisher" + id)
			.build();
	}

	@Test
	@DisplayName("단일 출판사 등록 - 성공")
	void registerBookPublisher_success() {
		Book book = sampleBook();
		Publisher publisher = samplePublisher(PUBLISHER_ID1);
		when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.of(book));
		when(publisherRepository.findById(PUBLISHER_ID1)).thenReturn(Optional.of(publisher));
		when(bookPublisherRepository.existsByBookAndPublisher(book, publisher)).thenReturn(false);

		PublisherInfoResponse response = bookPublisherService.registerBookPublisher(BOOK_ID, PUBLISHER_ID1);

		assertEquals(PUBLISHER_ID1, response.getId());
		verify(bookPublisherRepository, times(1)).save(any(BookPublisher.class));
	}

	@Test
	@DisplayName("출판사 등록 - 등록 실패: 이미 등록된 출판사")
	void registerBookPublisher_fail_alreadyExists() {
		Book book = sampleBook();
		Publisher publisher = samplePublisher(PUBLISHER_ID1);
		when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.of(book));
		when(publisherRepository.findById(PUBLISHER_ID1)).thenReturn(Optional.of(publisher));
		when(bookPublisherRepository.existsByBookAndPublisher(book, publisher)).thenReturn(true);

		assertThrows(BookPublisherAlreadyExistsException.class,
			() -> bookPublisherService.registerBookPublisher(BOOK_ID, PUBLISHER_ID1));
	}

	@Test
	@DisplayName("출판사 등록 - 등록 실패: 도서 없음")
	void registerBookPublisher_fail_bookNotFound() {
		when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.empty());

		assertThrows(BookNotFoundException.class,
			() -> bookPublisherService.registerBookPublisher(BOOK_ID, PUBLISHER_ID1));
	}

	@Test
	@DisplayName("복수 출판사 등록 - 성공")
	void registerBookPublishers_success() {
		Book book = sampleBook();
		Publisher publisher1 = samplePublisher(PUBLISHER_ID1);
		Publisher publisher2 = samplePublisher(PUBLISHER_ID2);
		when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.of(book));
		when(publisherRepository.findById(PUBLISHER_ID1)).thenReturn(Optional.of(publisher1));
		when(publisherRepository.findById(PUBLISHER_ID2)).thenReturn(Optional.of(publisher2));
		when(bookPublisherRepository.existsByBookAndPublisher(book, publisher1)).thenReturn(false);
		when(bookPublisherRepository.existsByBookAndPublisher(book, publisher2)).thenReturn(false);

		List<PublisherInfoResponse> responses = bookPublisherService.registerBookPublisher(BOOK_ID,
			Arrays.asList(PUBLISHER_ID1, PUBLISHER_ID2));

		assertEquals(2, responses.size());
		verify(bookPublisherRepository, times(2)).save(any(BookPublisher.class));
	}

	@Test
	@DisplayName("출판사 수정 - 성공")
	void updateBookPublisher_success() {
		Book book = sampleBook();
		BookPublisher existing = BookPublisher.builder()
			.book(book)
			.publisher(samplePublisher(PUBLISHER_ID1))
			.build();

		when(bookPublisherRepository.findByBookId(BOOK_ID))
			.thenReturn(Collections.singletonList(existing));
		when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.of(book));
		Publisher pub2 = samplePublisher(PUBLISHER_ID2);
		when(publisherRepository.findById(PUBLISHER_ID2)).thenReturn(Optional.of(pub2));
		when(bookPublisherRepository.existsByBookAndPublisher(book, pub2)).thenReturn(false);

		List<PublisherInfoResponse> updated = bookPublisherService.updateBookPublisher(
			BOOK_ID, Collections.singletonList(PUBLISHER_ID2));

		verify(bookPublisherRepository, times(1))
			.deleteAll(Collections.singletonList(existing));
		verify(bookPublisherRepository, times(1))
			.save(any(BookPublisher.class));
		assertEquals(1, updated.size());
		assertEquals(PUBLISHER_ID2, updated.get(0).getId());
	}

	@Test
	@DisplayName("출판사 수정 - 실패: 도서 없음")
	void updateBookPublisher_Fail_BookNotFound() {
		when(bookPublisherRepository.findByBookId(BOOK_ID))
			.thenReturn(Collections.emptyList());
		when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.empty());

		assertThrows(BookNotFoundException.class,
			() -> bookPublisherService.updateBookPublisher(
				BOOK_ID, Collections.singletonList(PUBLISHER_ID1)));
	}

	@Test
	@DisplayName("출판사 수정 - 실패: 출판사 없음")
	void updateBookPublisher_Fail_PublisherNotFound() {
		Book book = sampleBook();
		when(bookPublisherRepository.findByBookId(BOOK_ID))
			.thenReturn(Collections.emptyList());
		when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.of(book));
		when(publisherRepository.findById(PUBLISHER_ID1)).thenReturn(Optional.empty());

		assertThrows(PublisherNotFoundException.class,
			() -> bookPublisherService.updateBookPublisher(
				BOOK_ID, Collections.singletonList(PUBLISHER_ID1)));
	}
}
