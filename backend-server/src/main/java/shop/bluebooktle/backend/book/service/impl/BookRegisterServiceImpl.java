package shop.bluebooktle.backend.book.service.impl;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.dto.request.BookAllRegisterByAladinRequest;
import shop.bluebooktle.backend.book.dto.request.BookAllRegisterRequest;
import shop.bluebooktle.backend.book.dto.response.AladinBookResponse;
import shop.bluebooktle.backend.book.entity.Author;
import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.BookAuthor;
import shop.bluebooktle.backend.book.entity.BookCategory;
import shop.bluebooktle.backend.book.entity.BookImg;
import shop.bluebooktle.backend.book.entity.BookPublisher;
import shop.bluebooktle.backend.book.entity.BookSaleInfo;
import shop.bluebooktle.backend.book.entity.BookTag;
import shop.bluebooktle.backend.book.entity.Category;
import shop.bluebooktle.backend.book.entity.Img;
import shop.bluebooktle.backend.book.entity.Publisher;
import shop.bluebooktle.backend.book.entity.Tag;
import shop.bluebooktle.backend.book.repository.AuthorRepository;
import shop.bluebooktle.backend.book.repository.BookAuthorRepository;
import shop.bluebooktle.backend.book.repository.BookCategoryRepository;
import shop.bluebooktle.backend.book.repository.BookImgRepository;
import shop.bluebooktle.backend.book.repository.BookPublisherRepository;
import shop.bluebooktle.backend.book.repository.BookRepository;
import shop.bluebooktle.backend.book.repository.BookSaleInfoRepository;
import shop.bluebooktle.backend.book.repository.BookTagRepository;
import shop.bluebooktle.backend.book.repository.CategoryRepository;
import shop.bluebooktle.backend.book.repository.ImgRepository;
import shop.bluebooktle.backend.book.repository.PublisherRepository;
import shop.bluebooktle.backend.book.repository.TagRepository;
import shop.bluebooktle.backend.book.service.AladinBookService;
import shop.bluebooktle.backend.book.service.AladinBookService;
import shop.bluebooktle.backend.book.service.BookRegisterService;
import shop.bluebooktle.common.exception.book.AladinBookNotFoundException;
import shop.bluebooktle.common.exception.book.BookAlreadyExistsException;

@Service
@RequiredArgsConstructor
public class BookRegisterServiceImpl implements BookRegisterService {

	private final BookRepository bookRepository;
	private final BookSaleInfoRepository bookSaleInfoRepository;
	private final AladinBookService aladinBookService;

	private final AuthorRepository authorRepository;
	private final PublisherRepository publisherRepository;
	private final CategoryRepository categoryRepository;
	private final ImgRepository imgRepository;
	private final TagRepository tagRepository;

	private final BookAuthorRepository bookAuthorRepository;
	private final BookPublisherRepository bookPublisherRepository;
	private final BookCategoryRepository bookCategoryRepository;
	private final BookImgRepository bookImgRepository;
	private final BookTagRepository bookTagRepository;

	//연관테이블 완성되면 수정필요 일단기능구현만
	@Transactional
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

		//작가, 출판사, 태그, 이미지, 카테고리 - 수정필요
		// 연관테이블 완성되면 나중에 대략 이런식으로 수정
		// authorService.saveAuthors(request.getAuthor(), book);
		// publisherService.savePublisher(request.getPublisher(), book);
		// categoryService.saveCategories(request.getCategory(), book);
		// imageService.saveImages(request.getImageUrl(), book, false);
		// tagService.saveTags(request.getTag(), book);

		for (String authorName : request.getAuthor()) {
			Author author = authorRepository.findByName(authorName)
				.orElseGet(() -> authorRepository.save(new Author(authorName)));
			bookAuthorRepository.save(new BookAuthor(author, book));
		}

		Publisher publisher = publisherRepository.findByName(request.getPublisher())
			.orElseGet(() -> publisherRepository.save(new Publisher(request.getPublisher())));
		bookPublisherRepository.save(new BookPublisher(book, publisher));

		for (String categoryName : request.getCategory()) {
			Category category = Optional.ofNullable(
				categoryRepository.findByName(categoryName)
			).orElseGet(() -> categoryRepository.save(new Category(null, categoryName, "")));
			bookCategoryRepository.save(new BookCategory(book, category));
		}

		for (String imageUrl : request.getImageUrl()) {
			Img img = imgRepository.findByImgUrl(imageUrl)
				.orElseGet(() -> imgRepository.save(new Img(imageUrl)));
			bookImgRepository.save(new BookImg(book, img, false));
		}

		for (String tagName : request.getTag()) {
			Tag tag = tagRepository.findByName(tagName).stream()
				.findFirst()
				.orElseGet(() -> tagRepository.save(new Tag(tagName)));
			bookTagRepository.save(new BookTag(tag, book));
		}

		BookSaleInfo bookSaleInfo = BookSaleInfo.builder()
			.book(book)
			.price(request.getPrice())
			.salePrice(request.getSalePrice())
			.stock(request.getStock())
			.isPackable(request.getIsPackable() != null &&
				request.getIsPackable())
			.state(request.getState())
			.salePercentage(salePercentage)
			.build();
		bookSaleInfoRepository.save(bookSaleInfo);
	}

	//연관테이블 완성되면 수정필요 일단기능구현만
	//알라딘으로 도서저장할시 태그 추가해야함. 가져오는정보에 없음
	@Transactional
	@Override
	public void registerBookByAladin(BookAllRegisterByAladinRequest request) {
		Optional<Book> existBook = bookRepository.findByIsbn(request.getIsbn());
		if (existBook.isPresent()) {
			throw new BookAlreadyExistsException();
		}

		AladinBookResponse aladin = aladinBookService.getBookByIsbn(request.getIsbn());
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
		).orElseGet(() -> categoryRepository.save(new Category(null, aladin.getCategoryName(), "")));
		bookCategoryRepository.save(new BookCategory(book, category));

		Img img = imgRepository.findByImgUrl(aladin.getImageUrl())
			.orElseGet(() -> imgRepository.save(new Img(aladin.getImageUrl())));
		bookImgRepository.save(new BookImg(book, img, true));

		if (request.getTag() != null && !request.getTag().isEmpty()) {
			for (String tagName : request.getTag()) {
				Tag tag = tagRepository.findByName(tagName).stream()
					.findFirst()
					.orElseGet(() -> tagRepository.save(new Tag(tagName)));
				bookTagRepository.save(new BookTag(tag, book));
			}
		}

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
