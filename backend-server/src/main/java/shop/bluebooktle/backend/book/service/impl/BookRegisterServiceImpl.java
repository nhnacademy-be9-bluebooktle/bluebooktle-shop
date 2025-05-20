package shop.bluebooktle.backend.book.service.impl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.BookSaleInfo;
import shop.bluebooktle.backend.book.repository.BookRepository;
import shop.bluebooktle.backend.book.repository.BookSaleInfoRepository;
import shop.bluebooktle.backend.book.service.AladinBookService;
import shop.bluebooktle.backend.book.service.AuthorService;
import shop.bluebooktle.backend.book.service.BookAuthorService;
import shop.bluebooktle.backend.book.service.BookCategoryService;
import shop.bluebooktle.backend.book.service.BookImgService;
import shop.bluebooktle.backend.book.service.BookPublisherService;
import shop.bluebooktle.backend.book.service.BookRegisterService;
import shop.bluebooktle.backend.book.service.BookTagService;
import shop.bluebooktle.backend.book.service.PublisherService;
import shop.bluebooktle.common.dto.book.request.BookAllRegisterByAladinRequest;
import shop.bluebooktle.common.dto.book.request.BookAllRegisterRequest;
import shop.bluebooktle.common.dto.book.response.AladinBookResponse;
import shop.bluebooktle.common.dto.book.response.PublisherInfoResponse;
import shop.bluebooktle.common.dto.book.response.author.AuthorResponse;
import shop.bluebooktle.common.exception.book.AladinBookNotFoundException;
import shop.bluebooktle.common.exception.book.BookAlreadyExistsException;

@Service
@Transactional
@RequiredArgsConstructor
public class BookRegisterServiceImpl implements BookRegisterService {

	private final BookRepository bookRepository;
	private final BookSaleInfoRepository bookSaleInfoRepository;
	private final AladinBookService aladinBookService;

	private final BookPublisherService bookPublisherService;
	private final BookCategoryService bookCategoryService;
	private final BookTagService bookTagService;
	private final BookImgService bookImgService;
	private final BookAuthorService bookAuthorService;
	private final AuthorService authorService;
	private final PublisherService publisherService;

	@Override
	public void registerBook(BookAllRegisterRequest request) {
		Optional<Book> existBook = bookRepository.findByIsbn(request.getIsbn());
		if (existBook.isPresent()) {
			throw new BookAlreadyExistsException();
		}
		Book book = Book.builder()
			.title(request.getTitle())
			.isbn(request.getIsbn())
			.description(request.getDescription())
			.publishDate(request.getPublishDate() != null ?
				request.getPublishDate().atStartOfDay() : null)
			.build();
		bookRepository.save(book);

		//할인율 계산 따로 뺄것
		BigDecimal salePercentage = request.getPrice().subtract(request.getSalePrice())
			.divide(request.getPrice(), 2, BigDecimal.ROUND_HALF_UP)
			.multiply(BigDecimal.valueOf(100));

		bookAuthorService.registerBookAuthor(book.getId(), request.getAuthorIdList());

		bookPublisherService.registerBookPublisher(book.getId(), request.getAuthorIdList());

		bookCategoryService.registerBookCategory(book.getId(), request.getCategoryIdList());

		bookTagService.registerBookTag(book.getId(), request.getTagIdList());

		// TODO 일단 이미지를 url을 받아와서 저장되도록(이미지 테이블에 저장 및 도서이미지에 저장)
		// TODO 이미지 파일 이미지 서버(MINIO)에 저장 로직 구현
		bookImgService.registerBookImg(book.getId(), request.getThumbnailUrl());

		BookSaleInfo bookSaleInfo = BookSaleInfo.builder()
			.book(book)
			.price(request.getPrice())
			.salePrice(request.getSalePrice())
			.stock(request.getStock())
			.isPackable(request.getIsPackable() != null &&
				request.getIsPackable())
			.state(toStateOrThrow(request.getState()))
			.salePercentage(salePercentage)
			.build();
		bookSaleInfoRepository.save(bookSaleInfo);
	}

	@Override
	public void registerBookByAladin(BookAllRegisterByAladinRequest request) {
		// 이미 등록된 도서인 경우(중복 isbn 처리)
		if (bookRepository.existsByIsbn(request.getIsbn())) {
			throw new BookAlreadyExistsException();
		}
		AladinBookResponse aladinBook = aladinBookService.getBookByIsbn(request.getIsbn());
		if (aladinBook == null) {
			throw new AladinBookNotFoundException("알라딘 API에서 해당 ISBN의 도서를 찾을 수 없습니다.");
		}

		//book 정보 저장
		Book book = Book.builder()
			.title(aladinBook.getTitle())
			.description(aladinBook.getDescription())
			.isbn(aladinBook.getIsbn())
			.publishDate(aladinBook.getPublishDate().toLocalDate().atStartOfDay())
			.build();
		bookRepository.save(book);

		List<String> authorsName = parseAuthors(aladinBook.getAuthor());
		for (String authorName : authorsName) {
			// 도서에 작가 등록 (작가 없으면 작가 테이블에 등록)
			AuthorResponse author = authorService.registerAuthorByName(authorName);
			bookAuthorService.registerBookAuthor(book.getId(), author.getId());
		}
		// 도서에 출판사 등록 (출판사 없으면 출판사 테이블에 등록)
		PublisherInfoResponse publisher = publisherService.registerPublisherByName(aladinBook.getPublisher());
		bookPublisherService.registerBookPublisher(book.getId(), publisher.getId());

		// TODO 이미지 파일 이미지 서버(MINIO)에 저장 로직 구현
		bookImgService.registerBookImg(book.getId(), aladinBook.getImageUrl());

		for (Long categoryId : request.getCategoryIdList()) {
			bookCategoryService.registerBookCategory(book.getId(), categoryId);
		}

		for (Long tagId : request.getTagIdList()) {
			bookTagService.registerBookTag(book.getId(), tagId);
		}

		BookSaleInfo saleInfo = BookSaleInfo.builder()
			.book(book)
			.price(aladinBook.getPrice())
			.salePrice(aladinBook.getSalePrice())
			.salePercentage(aladinBook.getSalePercentage())
			.stock(request.getStock())
			.isPackable(request.getIsPackable())
			.state(toStateOrThrow(request.getState()))
			.build();
		bookSaleInfoRepository.save(saleInfo);
	}

	private List<String> parseAuthors(String author) {
		int index = author.indexOf("(지은이)");
		String authorsStr = author.substring(0, index);
		return Arrays.stream(authorsStr.split(","))
			.map(String::trim)
			.toList();
	}

	public BookSaleInfo.State toStateOrThrow(String stateStr) {
		if (stateStr == null) {
			throw new IllegalArgumentException("State 값이 null입니다.");
		}
		try {
			return BookSaleInfo.State.valueOf(stateStr);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("유효하지 않은 상태 값입니다: " + stateStr);
		}
	}
}
