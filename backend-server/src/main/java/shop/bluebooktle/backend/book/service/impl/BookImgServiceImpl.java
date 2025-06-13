package shop.bluebooktle.backend.book.service.impl;

import java.util.List;

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
import shop.bluebooktle.common.dto.book.response.img.ImgResponse;
import shop.bluebooktle.common.exception.book.BookIdNullException;
import shop.bluebooktle.common.exception.book.BookImgNotFoundException;
import shop.bluebooktle.common.exception.book.BookNotFoundException;

@Service
@Transactional
@RequiredArgsConstructor
public class BookImgServiceImpl implements BookImgService {

	private final BookRepository bookRepository;
	private final ImgRepository imgRepository;
	private final BookImgRepository bookImgRepository;

	@Override
	public void registerBookImg(Long bookId, String imageUrl) {

		if (bookId == null || imageUrl == null || imageUrl.trim().isEmpty()) {
			throw new IllegalArgumentException();
		}

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

	@Override
	public void updateBookImg(Long bookId, String imageUrl) {
		// 도서에 기존 이미지 삭제

		List<BookImg> bookImgList = bookImgRepository.findByBookId(bookId);

		if (bookImgList.isEmpty()) {
			throw new BookImgNotFoundException();
		}

		bookImgRepository.deleteAll(bookImgList);
		// 새로운 이미지 등록
		registerBookImg(bookId, imageUrl);
	}
}
