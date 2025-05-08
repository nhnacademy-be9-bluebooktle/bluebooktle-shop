package shop.bluebooktle.backend.book.service.Impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.dto.request.BookLikesRequest;
import shop.bluebooktle.backend.book.dto.response.BookLikesResponse;
import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.BookLikes;
import shop.bluebooktle.backend.book.repository.BookLikesRepository;
import shop.bluebooktle.backend.book.repository.BookRepository;
import shop.bluebooktle.backend.book.service.BookLikesService;
import shop.bluebooktle.backend.user.repository.UserRepository;
import shop.bluebooktle.common.entity.User;
import shop.bluebooktle.common.exception.BookLikesAlreadyChecked;
import shop.bluebooktle.common.exception.BookNotFoundException;
import shop.bluebooktle.common.exception.UserNotFoundException;

@Service
@RequiredArgsConstructor
public class BookLikesServiceImpl implements BookLikesService {
	private final BookLikesRepository bookLikesRepository;
	private final BookRepository bookRepository;
	private final UserRepository userRepository;

	@Override
	@Transactional
	public void like(BookLikesRequest request) {
		if (bookLikesRepository.existsByUser_IdAndBook_Id(request.getUserId(), request.getBookId())) {
			throw new BookLikesAlreadyChecked("Already checked book likes with bookId: " + request.getBookId());
		}
		Book book = bookRepository.findById(request.getBookId())
			.orElseThrow(() -> new BookNotFoundException("Book not found with id: " + request.getUserId()));
		User user = userRepository.findById(request.getUserId())
			.orElseThrow(() -> new UserNotFoundException("User not found with id: " + request.getUserId()));
		bookLikesRepository.save(new BookLikes(book, user));
	}

	@Transactional
	@Override
	public void unlike(BookLikesRequest request) {
		BookLikes bookLikes = bookLikesRepository.findByUser_IdAndBook_Id(request.getUserId(),
			request.getBookId());
		if (bookLikes != null) {
			bookLikesRepository.delete(bookLikes);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public BookLikesResponse isLiked(BookLikesRequest request) {
		boolean likedByUser = bookLikesRepository.existsByUser_IdAndBook_Id(request.getUserId(), request.getBookId());
		int count = (int)bookLikesRepository.countByBook_Id(request.getBookId());
		return BookLikesResponse.builder()
			.bookId(request.getBookId())
			.isLiked(likedByUser)
			.countLikes(count)
			.build();
	}

	@Override
	@Transactional(readOnly = true)
	public BookLikesResponse countLikes(BookLikesRequest request) {
		int count = (int)bookLikesRepository.countByBook_Id(request.getBookId());
		return BookLikesResponse.builder()
			.bookId(request.getBookId())
			.isLiked(false) // 비로그인 사용자인 경우, 좋아요 여부 판단 불가 → false로 고정
			.countLikes(count)
			.build();
	}

	@Override
	@Transactional(readOnly = true)
	public List<BookLikesResponse> getBooksLikedByUser(BookLikesRequest request) {
		List<Book> likedBooks = bookLikesRepository.findBooksLikedByUser(request.getUserId());
		return likedBooks.stream()
			.map(book -> BookLikesResponse.builder()
				.bookId(book.getId())
				.isLiked(true)
				.countLikes((int)bookLikesRepository.countByBook_Id(book.getId()))
				.build())
			.toList();
	}
}
