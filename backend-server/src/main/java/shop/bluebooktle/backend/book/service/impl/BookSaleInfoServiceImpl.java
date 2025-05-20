package shop.bluebooktle.backend.book.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.dto.response.BookSaleInfoRegisterResponse;
import shop.bluebooktle.backend.book.dto.response.BookSaleInfoUpdateResponse;
import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.BookSaleInfo;
import shop.bluebooktle.backend.book.repository.BookRepository;
import shop.bluebooktle.backend.book.repository.BookSaleInfoRepository;
import shop.bluebooktle.backend.book.service.BookSaleInfoService;
import shop.bluebooktle.common.dto.book.request.BookSaleInfoRegisterRequest;
import shop.bluebooktle.common.dto.book.request.BookSaleInfoUpdateRequest;
import shop.bluebooktle.common.exception.InvalidInputValueException;
import shop.bluebooktle.common.exception.book.BookNotFoundException;
import shop.bluebooktle.common.exception.book.BookSaleInfoAlreadyExistsException;
import shop.bluebooktle.common.exception.book.BookSaleInfoNotFoundException;

@Service
@Transactional
@RequiredArgsConstructor
public class BookSaleInfoServiceImpl implements BookSaleInfoService {

	private final BookRepository bookRepository;
	private final BookSaleInfoRepository bookSaleInfoRepository;

	@Override
	public BookSaleInfoRegisterResponse save(BookSaleInfoRegisterRequest request) {
		//도서가 있어야 도서 판매정보를 등록할수있음 bookId로 조회
		Book book = bookRepository.findById(request.getBookId())
			.orElseThrow(BookNotFoundException::new);

		if (bookSaleInfoRepository.findByBook(book).isPresent()) {
			throw new BookSaleInfoAlreadyExistsException();
		}

		BigDecimal salePercentage = calculateSalePercentage(request.getPrice(), request.getSalePrice());

		BookSaleInfo bookSaleInfo = request.toEntity(book).toBuilder()
			.salePercentage(salePercentage)
			.build();

		BookSaleInfo savedEntity = bookSaleInfoRepository.save(bookSaleInfo);

		return BookSaleInfoRegisterResponse.fromEntity(savedEntity);

	}

	@Override
	@Transactional(readOnly = true)
	public BookSaleInfo findById(Long id) {
		return bookSaleInfoRepository.findById(id)
			.orElseThrow(BookSaleInfoNotFoundException::new);
	}

	@Override
	public BookSaleInfoUpdateResponse update(Long id, BookSaleInfoUpdateRequest request) {
		BookSaleInfo existingEntity = bookSaleInfoRepository.findById(id)
			.orElseThrow(BookSaleInfoNotFoundException::new);

		Book book = bookRepository.findById(request.getBookId())
			.orElseThrow(BookNotFoundException::new);

		BookSaleInfo updatedEntity = request.toEntity(existingEntity.toBuilder()
			.book(book)
			.build());

		BigDecimal salePercentage = calculateSalePercentage(updatedEntity.getPrice(), updatedEntity.getSalePrice());

		updatedEntity.setSalePercentage(salePercentage);

		BookSaleInfo savedEntity = bookSaleInfoRepository.save(updatedEntity);

		return BookSaleInfoUpdateResponse.fromEntity(savedEntity);
	}

	@Override
	public void deleteById(Long id) {
		if (!bookSaleInfoRepository.existsById(id)) {
			throw new BookSaleInfoNotFoundException();
		}
		bookSaleInfoRepository.deleteById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<BookSaleInfo> findByBook(Book book) {
		if (book == null || book.getId() == null) {
			throw new BookNotFoundException();
		}
		return bookSaleInfoRepository.findByBook(book);
	}

	//할인율 계산
	private BigDecimal calculateSalePercentage(BigDecimal price, BigDecimal salePrice) {
		if (price == null || salePrice == null || price.compareTo(BigDecimal.ZERO) <= 0) {
			throw new InvalidInputValueException();
		}

		return price.subtract(salePrice)
			.divide(price, 2, RoundingMode.HALF_UP)
			.multiply(BigDecimal.valueOf(100));
	}
}