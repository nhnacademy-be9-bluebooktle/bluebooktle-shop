package shop.bluebooktle.backend.book.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.dto.response.BookLikesResponse;
import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.BookLikes;
import shop.bluebooktle.backend.book.jpa.BookLikesRepository;
import shop.bluebooktle.backend.book.jpa.BookRepository;
import shop.bluebooktle.backend.book.service.BookLikesService;
import shop.bluebooktle.backend.user.repository.UserRepository;
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.exception.auth.UserNotFoundException;
import shop.bluebooktle.common.exception.book.BookLikesAlreadyChecked;
import shop.bluebooktle.common.exception.book.BookNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional
public class BookLikesServiceImpl implements BookLikesService {
	private final BookLikesRepository bookLikesRepository;
	private final BookRepository bookRepository;
	private final UserRepository userRepository;

	/** 사용자가 도서 좋아요 누르기 */
	@Override
	public void like(Long bookId, Long userId) {
		if (bookLikesRepository.existsByUser_IdAndBook_Id(userId, bookId)) {
			throw new BookLikesAlreadyChecked();
		}
		Book book = bookRepository.findById(bookId)
			.orElseThrow(BookNotFoundException::new);
		User user = userRepository.findById(userId)
			.orElseThrow(UserNotFoundException::new);
		bookLikesRepository.save(new BookLikes(book, user));
	}

	/** 사용자가 누른 도서 좋아요 취소 */
	@Override
	public void unlike(Long bookId, Long userId) {
		BookLikes bookLikes = bookLikesRepository.findByUser_IdAndBook_Id(userId, bookId);
		if (bookLikes != null) {
			bookLikesRepository.delete(bookLikes);
		}
	}

	/** 도서 좋아요 여부 확인 */
	@Override
	@Transactional(readOnly = true)
	public BookLikesResponse isLiked(Long bookId, Long userId) {
		boolean likedByUser = bookLikesRepository.existsByUser_IdAndBook_Id(userId,
			bookId); // 로그인한 사용자가 이 책에 좋아요를 눌렀는지 판단
		int count = (int)bookLikesRepository.countByBook_Id(bookId); // 해당 도서가 받은 전체 좋아요 수
		return BookLikesResponse.builder()
			.bookId(bookId) // 어떤 책에 대한 정보인지
			.isLiked(likedByUser) // 이 사용자가 좋아요를 눌렀는지
			.countLikes(count)
			.build();
	}

	/** 도서 좋아요 수 확인 */
	@Override
	@Transactional(readOnly = true)
	public BookLikesResponse countLikes(Long bookId) {
		int count = (int)bookLikesRepository.countByBook_Id(bookId);
		return BookLikesResponse.builder()
			.bookId(bookId)
			.isLiked(false) // 비로그인 사용자인 경우, 좋아요 여부 판단 불가 → false로 고정
			.countLikes(count)
			.build();
	}

	/** 좋아요 누른 도서 조회 */
	@Override
	@Transactional(readOnly = true)
	public List<BookLikesResponse> getBooksLikedByUser(Long userId) {
		List<BookLikes> likedBooks = bookLikesRepository.findAllByUser_Id(userId);
		return likedBooks.stream()
			.map(bl -> BookLikesResponse.builder()
				.bookId(bl.getBook().getId())
				.isLiked(true)
				.countLikes((int)bookLikesRepository.countByBook_Id(bl.getBook().getId()))
				.build())
			.filter(BookLikesResponse::isLiked) // 좋아요 누른 도서만 조회
			.toList();
	}
}
