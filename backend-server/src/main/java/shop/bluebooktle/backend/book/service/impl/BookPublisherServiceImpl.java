package shop.bluebooktle.backend.book.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
		Book book = findBookOrThrow(bookId);
		Publisher publisher = findPublisherOrThrow(publisherId);
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
		Book book = findBookOrThrow(bookId);
		Publisher publisher = findPublisherOrThrow(publisherId);
		if (!bookPublisherRepository.existsByBookAndPublisher(book, publisher)) {
			throw new BookPublisherNotFoundException(bookId, publisherId);
		}
		BookPublisher bookPublisher = BookPublisher.builder()
			.book(book)
			.publisher(publisher)
			.build();
		bookPublisherRepository.delete(bookPublisher);
	}

	@Override
	@Transactional(readOnly = true)
	public List<PublisherInfoResponse> getPublishersByBookId(Long bookId) {
		Book book = findBookOrThrow(bookId);
		List<BookPublisher> publisherListByBook = bookPublisherRepository.findByBook(book);
		return publisherListByBook.stream()
			.map(p -> new PublisherInfoResponse(
				p.getPublisher().getId(),
				p.getPublisher().getName()))
			.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public Page<BookInfoResponse> searchBooksByPublisher(Long publisherId, Pageable pageable) {
		Publisher publisher = findPublisherOrThrow(publisherId);
		Page<BookPublisher> bookPublisherPage = bookPublisherRepository.findAllByPublisher(publisher, pageable);
		return bookPublisherPage.map(bp -> new BookInfoResponse(bp.getBook().getId()));
	}

	private Book findBookOrThrow(Long bookId) {
		return bookRepository.findById(bookId)
			.orElseThrow(BookNotFoundException::new);
	}

	private Publisher findPublisherOrThrow(Long publihserId) {
		return publisherRepository.findById(publihserId)
			.orElseThrow(() -> new PublisherNotFoundException(publihserId));
	}
}
