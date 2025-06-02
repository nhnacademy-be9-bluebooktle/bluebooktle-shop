package shop.bluebooktle.backend.book.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.entity.Author;
import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.BookLikes;
import shop.bluebooktle.backend.book.entity.BookSaleInfo;
import shop.bluebooktle.backend.book.entity.Img;
import shop.bluebooktle.backend.book.repository.AuthorRepository;
import shop.bluebooktle.backend.book.repository.BookAuthorRepository;
import shop.bluebooktle.backend.book.repository.BookImgRepository;
import shop.bluebooktle.backend.book.repository.BookLikesRepository;
import shop.bluebooktle.backend.book.repository.BookRepository;
import shop.bluebooktle.backend.book.repository.BookSaleInfoRepository;
import shop.bluebooktle.backend.book.repository.ImgRepository;
import shop.bluebooktle.backend.book.service.BookLikesService;
import shop.bluebooktle.backend.user.repository.UserRepository;
import shop.bluebooktle.common.dto.book.response.BookLikesListResponse;
import shop.bluebooktle.common.dto.book.response.BookLikesResponse;
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.exception.auth.UserNotFoundException;
import shop.bluebooktle.common.exception.book.BookLikesAlreadyChecked;
import shop.bluebooktle.common.exception.book.BookNotFoundException;
import shop.bluebooktle.common.exception.book.BookSaleInfoNotFoundException;
import shop.bluebooktle.common.exception.book.ImgNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional
public class BookLikesServiceImpl implements BookLikesService {
	private final BookLikesRepository bookLikesRepository;
	private final BookRepository bookRepository;
	private final UserRepository userRepository;
	private final BookImgRepository bookImgRepository;
	private final ImgRepository imgRepository;
	private final AuthorRepository authorRepository;
	private final BookAuthorRepository bookAuthorRepository;
	private final BookSaleInfoRepository bookSaleInfoRepository;

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
	public List<BookLikesListResponse> getBooksLikedByUser(Long userId) {
		// 유저가 좋아요 누른 BookLikes 리스트 조회
		List<BookLikes> likedBooks = bookLikesRepository.findAllByUser_Id(userId);

		List<BookLikesListResponse> result = new ArrayList<>(likedBooks.size());
		for (BookLikes bl : likedBooks) {
			Book book = bl.getBook();
			Long bookId = bl.getBook().getId();

			// 모든 저자 가져오기
			List<String> authorName = bookAuthorRepository
				.findAuthorsByBook(book)
				.stream()
				.map(Author::getName)
				.toList();

			// 썸네일 가져오기
			String imgUrl = bookImgRepository
				.findFirstByBookIdAndIsThumbnailTrueOrderByIdAsc(bookId)
				.flatMap(bookImg -> imgRepository.findById(bookImg.getImg().getId()))
				.map(Img::getImgUrl)
				.orElseThrow(ImgNotFoundException::new);

			// 도서 가격 가져오기
			BigDecimal price = bookSaleInfoRepository
				.findByBookId(bookId)
				.map(BookSaleInfo::getPrice)
				.orElseThrow(BookSaleInfoNotFoundException::new);

			// DTO 생성
			BookLikesListResponse dto = BookLikesListResponse.builder()
				.bookId(bookId)
				.bookName(book.getTitle())
				.authorName(authorName)
				.imgUrl(imgUrl)
				.price(price)
				.build();

			result.add(dto);
		}
		return result;
	}
}
