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
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.exception.book.BookLikesAlreadyChecked;
import shop.bluebooktle.common.exception.book.BookNotFoundException;
import shop.bluebooktle.common.exception.user.UserNotFoundException;

@Service
@RequiredArgsConstructor
public class BookLikesImpl implements BookLikesService {
	private final BookLikesRepository bookLikesRepository;
	private final BookRepository bookRepository;
	private final UserRepository userRepository;

	/** 사용자가 도서 좋아요 누르기 */
	@Override
	@Transactional
	public void like(BookLikesRequest request) {
		if (bookLikesRepository.existsByUser_IdAndBook_Id(request.getUserId(), request.getBookId())) {
			throw new BookLikesAlreadyChecked(Long.valueOf(request.getBookId()).toString());
		}
		Book book = bookRepository.findById(request.getBookId())
			.orElseThrow(() -> new BookNotFoundException(Long.valueOf(request.getBookId()).toString()));
		User user = userRepository.findById(request.getUserId())
			.orElseThrow(() -> new UserNotFoundException(Long.valueOf(request.getUserId()).toString()));
		bookLikesRepository.save(new BookLikes(book, user));
	}

	/** 사용자가 누른 도서 좋아요 취소 */
	@Transactional
	@Override
	public void unlike(BookLikesRequest request) {
		BookLikes bookLikes = bookLikesRepository.findByUser_IdAndBook_Id(request.getUserId(),
			request.getBookId());
		if (bookLikes != null) {
			bookLikesRepository.delete(bookLikes);
		}
	}

	/** 도서 좋아요 여부 확인 */
	@Override
	@Transactional(readOnly = true)
	public BookLikesResponse isLiked(BookLikesRequest request) {
		boolean likedByUser = bookLikesRepository.existsByUser_IdAndBook_Id(request.getUserId(),
			request.getBookId()); // 로그인한 사용자가 이 책에 좋아요를 눌렀는지 판단
		int count = (int)bookLikesRepository.countByBook_Id(request.getBookId()); // 해당 도서가 받은 전체 좋아요 수
		return BookLikesResponse.builder()
			.bookId(request.getBookId()) // 어떤 책에 대한 정보인지
			.isLiked(likedByUser) // 이 사용자가 좋아요를 눌렀는지
			.countLikes(count)
			.build();
	}

	/** 도서 좋아요 수 확인 */
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

	/** 좋아요 누른 도서 조회 */
	@Override
	@Transactional(readOnly = true)
	public List<BookLikesResponse> getBooksLikedByUser(BookLikesRequest request) {
		List<Book> likedBooks = bookLikesRepository.findBooksLikedByUser(request.getUserId());
		return likedBooks.stream()
			.map(book -> BookLikesResponse.builder()
				.bookId(book.getId())
				.isLiked(true) // 사용자가 좋아요한 책이므로 true 고정
				.countLikes((int)bookLikesRepository.countByBook_Id(book.getId())) // 몇 명에게 좋아요를 받았는지 세기
				.build())
			.toList();
	}
}
