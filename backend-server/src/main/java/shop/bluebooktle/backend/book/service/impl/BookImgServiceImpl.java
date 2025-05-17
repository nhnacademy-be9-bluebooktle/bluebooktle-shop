package shop.bluebooktle.backend.book.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.dto.request.BookImgRegisterRequest;
import shop.bluebooktle.backend.book.dto.response.BookImgResponse;
import shop.bluebooktle.backend.book.dto.response.BookInfoResponse;
import shop.bluebooktle.backend.book.dto.response.img.ImgResponse;
import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.BookImg;
import shop.bluebooktle.backend.book.entity.Img;
import shop.bluebooktle.backend.book.repository.BookImgRepository;
import shop.bluebooktle.backend.book.repository.BookRepository;
import shop.bluebooktle.backend.book.repository.ImgRepository;
import shop.bluebooktle.backend.book.service.BookImgService;

// 수정 필요

@Service
@RequiredArgsConstructor
public class BookImgServiceImpl implements BookImgService {

	private final BookRepository bookRepository;
	private final ImgRepository imgRepository;
	private final BookImgRepository bookImgRepository;

	@Transactional
	@Override
	public void registerBookImg(Long bookId, BookImgRegisterRequest bookImgRegisterRequest) {

		if (bookId == null) {
			throw new RuntimeException(); // TODO #1
		}

		Img img = imgRepository.findById(bookImgRegisterRequest.getImgId()).orElseThrow(() -> new RuntimeException()); // TODO #2
		Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException()); // TODO #3

		if (bookImgRepository.existsByBookAndImg(book, img)) {
			throw new RuntimeException(); // TODO #4
		}
		BookImg bookImg = BookImg.builder()
			.book(book)
			.img(img)
			.isThumbnail(bookImgRegisterRequest.isThumbnail())
			.build();

		bookImgRepository.save(bookImg);
	}


	@Transactional(readOnly = true)
	@Override
	public List<ImgResponse> getImgByBookId(Long bookId) {
		if (bookId == null) {
			throw new RuntimeException(); // TODO #5
		}

		Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException()); // TODO #6

		List<Img> images = bookImgRepository.findImagesByBook(book);

		return images.stream()
			.map(img -> ImgResponse.builder()
				.id(img.getId())
				.imgUrl(img.getImgUrl())
				.createdAt(img.getCreatedAt())
				.build()
			)
			.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	@Override
	public List<BookInfoResponse> getBookByImgId(Long imgId) {
		if (imgId == null) {
			throw new RuntimeException(); // TODO #7
		}

		Img img = imgRepository.findById(imgId).orElseThrow(() -> new RuntimeException()); // TODO #8

		List<Book> books = bookImgRepository.findBooksByImg(img);
		return books.stream()
			.map(book -> new BookInfoResponse(book.getId()))
			.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	@Override
	public Optional<BookImgResponse> getThumbnailByBookId(Long bookId) {
		if (bookId == null) {
			throw new IllegalArgumentException("Book ID must not be null");
		}
		return bookImgRepository.findByBookId(bookId).stream()
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
			);
	}

	@Transactional
	@Override
	public void deleteBookImg(Long bookId, Long imgId) {
		if (bookId == null || imgId == null) {
			throw new RuntimeException(); // TODO
		}

		BookImg relation = bookImgRepository
			.findByBookIdAndImgId(bookId, imgId)
			.orElseThrow(RuntimeException::new); // TODO

		bookImgRepository.delete(relation);
	}
}
