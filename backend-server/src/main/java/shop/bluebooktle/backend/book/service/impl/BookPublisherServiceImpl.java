package shop.bluebooktle.backend.book.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.dto.response.BookInfoResponse;
import shop.bluebooktle.backend.book.dto.response.PublisherInfoResponse;
import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.BookPublisher;
import shop.bluebooktle.backend.book.entity.Publisher;
import shop.bluebooktle.backend.book.repository.BookPublisherRepository;
import shop.bluebooktle.backend.book.repository.BookRepository;
import shop.bluebooktle.backend.book.repository.PublisherRepository;
import shop.bluebooktle.backend.book.service.BookPublisherService;
import shop.bluebooktle.common.exception.book.BookNotFoundException;
import shop.bluebooktle.common.exception.book.BookPublisherAlreadyExistsException;
import shop.bluebooktle.common.exception.book.BookPublisherNotFoundException;
import shop.bluebooktle.common.exception.book.PublisherNotFoundException;

@Service
@RequiredArgsConstructor
public class BookPublisherServiceImpl implements BookPublisherService {

	private final BookPublisherRepository bookPublisherRepository;
	private final BookRepository bookRepository;
	private final PublisherRepository publisherRepository;

	@Override
	public void registerBookPublisher(Long bookId, Long publisherId) {
		Book book = bookRepository.findById(bookId).orElseThrow(BookNotFoundException::new);
		Publisher publisher = publisherRepository.findById(publisherId)
			.orElseThrow(() -> new PublisherNotFoundException(publisherId));
		// 이미 도서에 등록된 출판사인 경우
		if (bookPublisherRepository.existsByBookAndPublisher(book, publisher)) {
			throw new BookPublisherAlreadyExistsException(bookId, publisherId);
		}
		BookPublisher bookPublisher = BookPublisher.builder()
			.book(book)
			.publisher(publisher)
			.build();
		bookPublisherRepository.save(bookPublisher);
	}

	@Override
	public void deleteBookPublisher(Long bookId, Long publisherId) {
		Book book = bookRepository.findById(bookId).orElseThrow(BookNotFoundException::new);
		Publisher publisher = publisherRepository.findById(publisherId)
			.orElseThrow(() -> new PublisherNotFoundException(publisherId));
		if (!bookPublisherRepository.existsByBookAndPublisher(book, publisher)) {
			throw new BookPublisherNotFoundException(bookId, publisherId);
		}
	}

	@Override
	public List<PublisherInfoResponse> getPublishersByBookId(Long bookId) {
		Book book = bookRepository.findById(bookId).orElseThrow(BookNotFoundException::new);
		List<BookPublisher> publisherListByBook = bookPublisherRepository.findByBook(book);
		return publisherListByBook.stream()
			.map(p -> new PublisherInfoResponse(
				p.getPublisher().getId(),
				p.getPublisher().getName()))
			.toList();
	}

	@Override
	public Page<BookInfoResponse> searchBooksByPublisher(Long publisherId, Pageable pageable) {
		Publisher publisher = publisherRepository.findById(publisherId)
			.orElseThrow(() -> new PublisherNotFoundException(publisherId));
		Page<BookPublisher> bookPublisherPage = bookPublisherRepository.findAllByPublisher(publisher, pageable);
		return bookPublisherPage
			.map(bp -> new BookInfoResponse(
				bp.getBook().getId()));
	}
}
