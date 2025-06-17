package shop.bluebooktle.backend.book.service.impl;

import java.util.ArrayList;
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
	public TagInfoResponse registerBookTag(Long bookId, Long tagId) {
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

		return TagInfoResponse.builder()
			.id(tag.getId())
			.name(tag.getName())
			.createdAt(tag.getCreatedAt())
			.build();

	}

	@Override
	public List<TagInfoResponse> registerBookTag(Long bookId, List<Long> tagIdList) {
		List<TagInfoResponse> responses = new ArrayList<>();
		for (Long tagId : tagIdList) {
			TagInfoResponse response = registerBookTag(bookId, tagId);
			responses.add(response);
		}
		return responses;
	}

	@Override
	public List<TagInfoResponse> updateBookTag(Long bookId, List<Long> tagIdList) {
		List<BookTag> bookTagList = bookTagRepository.findByBookId(bookId);
		bookTagRepository.deleteAll(bookTagList);
		return registerBookTag(bookId, tagIdList);
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
