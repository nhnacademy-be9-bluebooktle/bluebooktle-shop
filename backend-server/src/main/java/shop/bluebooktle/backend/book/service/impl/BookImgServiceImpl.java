package shop.bluebooktle.backend.book.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.BookImg;
import shop.bluebooktle.backend.book.entity.Img;
import shop.bluebooktle.backend.book.repository.BookImgRepository;
import shop.bluebooktle.backend.book.repository.BookRepository;
import shop.bluebooktle.backend.book.repository.ImgRepository;
import shop.bluebooktle.backend.book.service.BookImgService;
import shop.bluebooktle.backend.book.service.ImgService;
import shop.bluebooktle.common.dto.book.request.BookImgRegisterRequest;
import shop.bluebooktle.common.dto.book.request.img.ImgRegisterRequest;
import shop.bluebooktle.common.dto.book.response.BookImgResponse;
import shop.bluebooktle.common.dto.book.response.BookInfoResponse;
import shop.bluebooktle.common.dto.book.response.img.ImgResponse;
import shop.bluebooktle.common.exception.book.BookIdNullException;
import shop.bluebooktle.common.exception.book.BookImgAlreadyExistsException;
import shop.bluebooktle.common.exception.book.BookImgNotFoundException;
import shop.bluebooktle.common.exception.book.BookNotFoundException;
import shop.bluebooktle.common.exception.book.ImgIdNullException;
import shop.bluebooktle.common.exception.book.ImgNotFoundException;

// 수정 필요

@Service
@Transactional
@RequiredArgsConstructor
public class BookImgServiceImpl implements BookImgService {

	private final BookRepository bookRepository;
	private final ImgRepository imgRepository;
	private final BookImgRepository bookImgRepository;
	private final ImgService imgService;

	@Override
	public void registerBookImg(Long bookId, BookImgRegisterRequest bookImgRegisterRequest) {
		if (bookId == null) {
			throw new BookIdNullException();
		}
		imgService.registerImg(ImgRegisterRequest
			.builder()
			.imgUrl(bookImgRegisterRequest.getImgUrl())
			.build());

		Long imgId = bookImgRegisterRequest.getImgId();
		if (imgId == null) {
			throw new ImgIdNullException();
		}

		Img img = imgRepository.findById(bookImgRegisterRequest.getImgId())
			.orElseThrow(() -> new ImgNotFoundException());
		Book book = bookRepository.findById(bookId).orElseThrow(() -> new BookNotFoundException());

		if (bookImgRepository.existsByBookAndImg(book, img)) {
			throw new BookImgAlreadyExistsException(bookId, imgId);
		}
		BookImg bookImg = BookImg.builder()
			.book(book)
			.img(img)
			.isThumbnail(bookImgRegisterRequest.isThumbnail())
			.build();

		bookImgRepository.save(bookImg);
	}

	@Override
	public void registerBookImg(Long bookId, String imageUrl) {
		Book book = bookRepository.findById(bookId).orElseThrow(BookNotFoundException::new);
		Img img = imgRepository.save(Img.builder().imgUrl(imageUrl).build());

		BookImg bookImg = BookImg.builder()
			.book(book)
			.img(img)
			.isThumbnail(true)
			.build();
		bookImgRepository.save(bookImg);
	}

	@Transactional(readOnly = true)
	@Override
	public ImgResponse getImgByBookId(Long bookId) {
		if (bookId == null) {
			throw new BookIdNullException();
		}

		Book book = bookRepository.findById(bookId).orElseThrow(BookNotFoundException::new);

		BookImg bookImage = bookImgRepository.findBookImgByBook(book).orElseThrow(
			BookImgNotFoundException::new);

		return ImgResponse.builder()
			.id(bookImage.getImg().getId())
			.imgUrl(bookImage.getImg().getImgUrl())
			.createdAt(bookImage.getImg().getCreatedAt())
			.build();
	}

	@Transactional(readOnly = true)
	@Override
	public List<BookInfoResponse> getBookByImgId(Long imgId) {
		if (imgId == null) {
			throw new ImgIdNullException();
		}

		Img img = imgRepository.findById(imgId).orElseThrow(() -> new ImgNotFoundException());

		List<Book> books = bookImgRepository.findBooksByImg(img);
		/*return books.stream()
			.map(book -> new BookInfoResponse(book.getId()))
			.collect(Collectors.toList());*/
		return null;
	}

	@Transactional(readOnly = true)
	@Override
	public Optional<BookImgResponse> getThumbnailByBookId(Long bookId) {
		if (bookId == null) {
			throw new IllegalArgumentException("Book ID must not be null");
		}
		/*return bookImgRepository.findByBookId(bookId).stream()
			.filter(BookImg::isThumbnail)
			.findFirst()
			.map(rel -> BookImgResponse.builder()
				.imgResponse(ImgResponse.builder()
					.id(rel.getImg().getId())
					.imgUrl(rel.getImg().getImgUrl())
					.createdAt(rel.getImg().getCreatedAt())
					.build()
				)
				.bookInfoResponse(new BookInfoResponse(rel.getBook().getId()))
				.isThumbnail(true)
				.build()
			);*/
		return null;
	}

	@Override
	public void deleteBookImg(Long bookId, Long imgId) {
		if (bookId == null) {
			throw new BookIdNullException();
		}
		if (imgId == null) {
			throw new ImgIdNullException();
		}

		BookImg relation = bookImgRepository.findByBookIdAndImgId(bookId, imgId)
			.orElseThrow(() -> new BookImgNotFoundException(bookId, imgId));

		bookImgRepository.delete(relation);
	}

	@Override
	public void updateBookImg(Long bookId, String imageUrl) {
		// 도서에 기존 이미지 삭제
		List<BookImg> bookImgList = bookImgRepository.findByBookId(bookId);
		bookImgRepository.deleteAll(bookImgList);
		// 새로운 이미지 등록
		registerBookImg(bookId, imageUrl);
	}
}
