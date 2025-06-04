package shop.bluebooktle.backend.book.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.BookTag;
import shop.bluebooktle.backend.book.entity.Tag;
import shop.bluebooktle.backend.book.repository.BookRepository;
import shop.bluebooktle.backend.book.repository.BookTagRepository;
import shop.bluebooktle.backend.book.repository.TagRepository;
import shop.bluebooktle.backend.book.service.BookTagService;
import shop.bluebooktle.common.dto.book.response.BookInfoResponse;
import shop.bluebooktle.common.dto.book.response.TagInfoResponse;
import shop.bluebooktle.common.exception.book.BookNotFoundException;
import shop.bluebooktle.common.exception.book.BookTagAlreadyExistsException;
import shop.bluebooktle.common.exception.book.BookTagNotFoundException;
import shop.bluebooktle.common.exception.book.TagNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional
public class BookTagServiceImpl implements BookTagService {

	private final BookTagRepository bookTagRepository;
	private final BookRepository bookRepository;
	private final TagRepository tagRepository;

	@Override
	public void registerBookTag(Long bookId, Long tagId) {
		Book book = findBookOrThrow(bookId);
		Tag tag = findTagOrThrow(tagId);

		if (bookTagRepository.existsByBookAndTag(book, tag)) {
			throw new BookTagAlreadyExistsException(bookId, tagId);
		}
		BookTag bookTag = BookTag.builder()
			.tag(tag)
			.book(book)
			.build();
		bookTagRepository.save(bookTag);
	}

	@Override
	public void registerBookTag(Long bookId, List<Long> tagIdList) {
		for (Long tagId : tagIdList) {
			registerBookTag(bookId, tagId);
		}
	}

	@Override
	public void deleteBookTag(Long tagId, Long bookId) {
		Tag tag = findTagOrThrow(tagId);
		Book book = findBookOrThrow(bookId);

		BookTag bookTag = bookTagRepository.findByBookAndTag(book, tag)
			.orElseThrow(() -> new BookTagNotFoundException(bookId, tagId));
		bookTagRepository.delete(bookTag);
	}

	@Override
	@Transactional(readOnly = true)
	public List<TagInfoResponse> getTagsByBookId(Long bookId) {
		Book book = findBookOrThrow(bookId);
		List<BookTag> bookTagsByBook = bookTagRepository.findByBook(book);
		return bookTagsByBook.stream()
			.map(t -> new TagInfoResponse(
				t.getTag().getId(),
				t.getTag().getName(),
				t.getTag().getCreatedAt()))
			.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public Page<BookInfoResponse> searchBooksByTag(Long tagId, Pageable pageable) {
		Tag tag = findTagOrThrow(tagId);
		Page<BookTag> bookTagPage = bookTagRepository.findAllByTag(tag, pageable);
		// return bookTagPage.map(bt -> new BookInfoResponse(bt.getBook().getId()));
		return null;
	}

	private Book findBookOrThrow(Long bookId) {
		return bookRepository.findById(bookId)
			.orElseThrow(BookNotFoundException::new);
	}

	private Tag findTagOrThrow(Long tagId) {
		return tagRepository.findById(tagId)
			.orElseThrow(() -> new TagNotFoundException(tagId));
	}

}
