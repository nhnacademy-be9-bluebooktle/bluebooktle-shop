package shop.bluebooktle.backend.book.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.dto.request.BookRegisterByAladinRequest;
import shop.bluebooktle.backend.book.dto.request.BookRegisterRequest;
import shop.bluebooktle.backend.book.dto.response.AladinBookResponseDto;
import shop.bluebooktle.backend.book.entity.Author;
import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.BookAuthor;
import shop.bluebooktle.backend.book.entity.BookCategory;
import shop.bluebooktle.backend.book.entity.BookImg;
import shop.bluebooktle.backend.book.entity.BookPublisher;
import shop.bluebooktle.backend.book.entity.BookSaleInfo;
import shop.bluebooktle.backend.book.entity.Category;
import shop.bluebooktle.backend.book.entity.Img;
import shop.bluebooktle.backend.book.entity.Publisher;
import shop.bluebooktle.backend.book.repository.AuthorRepository;
import shop.bluebooktle.backend.book.repository.BookAuthorRepository;
import shop.bluebooktle.backend.book.repository.BookCategoryRepository;
import shop.bluebooktle.backend.book.repository.BookImgRepository;
import shop.bluebooktle.backend.book.repository.BookPublisherRepository;
import shop.bluebooktle.backend.book.repository.BookRepository;
import shop.bluebooktle.backend.book.repository.BookSaleInfoRepository;
import shop.bluebooktle.backend.book.repository.CategoryRepository;
import shop.bluebooktle.backend.book.repository.ImgRepository;
import shop.bluebooktle.backend.book.repository.PublisherRepository;
import shop.bluebooktle.backend.book.service.BookRegisterService;
import shop.bluebooktle.common.exception.BookAlreadyExistsException;
import shop.bluebooktle.common.exception.book.AladinBookNotFoundException;

@Service
@RequiredArgsConstructor
public class BookRegisterServiceImpl implements BookRegisterService {

	private final BookRepository bookRepository;
	private final BookSaleInfoRepository bookSaleInfoRepository;
	private final AladinBookServiceImpl aladinBookService;

	private final AuthorRepository authorRepository;
	private final PublisherRepository publisherRepository;
	private final CategoryRepository categoryRepository;
	private final ImgRepository imgRepository;

	private final BookAuthorRepository bookAuthorRepository;
	private final BookPublisherRepository bookPublisherRepository;
	private final BookCategoryRepository bookCategoryRepository;
	private final BookImgRepository bookImgRepository;

	@Transactional
	@Override
	public void registerBook(BookRegisterRequest request) {
		//직접등록 구현해야함
	}

	@Transactional
	@Override
	public void registerBookByAladin(BookRegisterByAladinRequest request) {
		Optional<Book> existBook = bookRepository.findByIsbn(request.getIsbn());
		if (existBook.isPresent()) {
			throw new BookAlreadyExistsException();
		}

		AladinBookResponseDto aladin = aladinBookService.getBookByIsbn(request.getIsbn());
		if (aladin == null) {
			throw new AladinBookNotFoundException("알라딘 API에서 해당 ISBN의 도서를 찾을 수 없습니다.");
		}

		//book 정보 저장
		Book book = Book.builder()
			.title(aladin.getTitle())
			.description(aladin.getDescription())
			.isbn(aladin.getIsbn())
			.publishDate(aladin.getPublishDate().toLocalDate().atStartOfDay())
			.build();
		bookRepository.save(book);

		Author author = authorRepository.findByName(aladin.getAuthor())
			.orElseGet(() -> authorRepository.save(new Author(aladin.getAuthor())));
		bookAuthorRepository.save(new BookAuthor(author, book));

		Publisher publisher = publisherRepository.findByName(aladin.getPublisher())
			.orElseGet(() -> publisherRepository.save(new Publisher(aladin.getPublisher())));
		bookPublisherRepository.save(new BookPublisher(book, publisher));

		Category category = Optional.ofNullable(
			categoryRepository.findByName(aladin.getCategoryName())
		).orElseGet(() -> categoryRepository.save(new Category(null, aladin.getCategoryName())));
		bookCategoryRepository.save(new BookCategory(book, category));

		Img img = imgRepository.findByImgUrl(aladin.getImageUrl())
			.orElseGet(() -> imgRepository.save(new Img(aladin.getImageUrl())));
		bookImgRepository.save(new BookImg(book, img, true));

		//booksaleinfo 정보 저장
		BookSaleInfo saleInfo = BookSaleInfo.builder()
			.book(book)
			.price(aladin.getPrice())
			.salePrice(aladin.getSalePrice())
			.salePercentage(aladin.getSalePercentage())
			.stock(request.getStock())
			.isPackable(request.getIsPackable())
			.state(request.getState())
			.build();
		bookSaleInfoRepository.save(saleInfo);
	}
}