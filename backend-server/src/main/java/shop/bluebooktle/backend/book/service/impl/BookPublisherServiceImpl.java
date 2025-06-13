package shop.bluebooktle.backend.book.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.BookPublisher;
import shop.bluebooktle.backend.book.entity.Publisher;
import shop.bluebooktle.backend.book.repository.BookPublisherRepository;
import shop.bluebooktle.backend.book.repository.BookRepository;
import shop.bluebooktle.backend.book.repository.PublisherRepository;
import shop.bluebooktle.backend.book.service.BookPublisherService;
import shop.bluebooktle.common.dto.book.response.PublisherInfoResponse;
import shop.bluebooktle.common.exception.book.BookNotFoundException;
import shop.bluebooktle.common.exception.book.BookPublisherAlreadyExistsException;
import shop.bluebooktle.common.exception.book.PublisherNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional
public class BookPublisherServiceImpl implements BookPublisherService {

	private final BookPublisherRepository bookPublisherRepository;
	private final BookRepository bookRepository;
	private final PublisherRepository publisherRepository;

	@Override
	public PublisherInfoResponse registerBookPublisher(Long bookId, Long publisherId) {
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
		return PublisherInfoResponse.builder()
			.id(publisher.getId())
			.name(publisher.getName())
			.createdAt(publisher.getCreatedAt())
			.build();
	}

	@Override
	public List<PublisherInfoResponse> registerBookPublisher(Long bookId, List<Long> publisherIdList) {
		List<PublisherInfoResponse> responses = new ArrayList<>();
		for (Long publisherId : publisherIdList) {
			PublisherInfoResponse response = registerBookPublisher(bookId, publisherId);
			responses.add(response);
		}
		return responses;
	}

	@Override
	public List<PublisherInfoResponse> updateBookPublisher(Long bookId, List<Long> publisherIdList) {
		// 도서에 등록되었던 기존 출판사 삭제
		List<BookPublisher> bookPublisherList = bookPublisherRepository.findByBookId(bookId);
		bookPublisherRepository.deleteAll(bookPublisherList);
		// 다시 새롭게 등록
		return registerBookPublisher(bookId, publisherIdList);
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
