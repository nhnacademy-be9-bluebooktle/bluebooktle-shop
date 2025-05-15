package shop.bluebooktle.backend.book.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.dto.request.BookSaleInfoRegisterRequest;
import shop.bluebooktle.backend.book.dto.request.BookSaleInfoUpdateRequest;
import shop.bluebooktle.backend.book.dto.response.BookSaleInfoRegisterResponse;
import shop.bluebooktle.backend.book.dto.response.BookSaleInfoUpdateResponse;
import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.BookSaleInfo;
import shop.bluebooktle.backend.book.repository.BookRepository;
import shop.bluebooktle.backend.book.repository.BookSaleInfoRepository;
import shop.bluebooktle.backend.book.service.BookSaleInfoService;
import shop.bluebooktle.common.exception.book.BookNotFoundException;
import shop.bluebooktle.common.exception.book.BookSaleInfoAlreadyExistsException;
import shop.bluebooktle.common.exception.book.BookSaleInfoNotFoundException;

@Service
@RequiredArgsConstructor
public class BookSaleInfoServiceImpl implements BookSaleInfoService {

	private final BookRepository bookRepository;
	private final BookSaleInfoRepository bookSaleInfoRepository;

	@Override
	@Transactional
	public BookSaleInfoRegisterResponse save(BookSaleInfoRegisterRequest request) {
		//도서가 있어야 도서 판매정보를 등록할수있음 bookId로 조회
		Book book = bookRepository.findById(request.getBookId())
			.orElseThrow(() -> new BookNotFoundException("도서를 찾을 수 없습니다. ID: " + request.getBookId()));

		if (bookSaleInfoRepository.findByBook(book).isPresent()) {
			throw new BookSaleInfoAlreadyExistsException("해당 도서는 이미 판매 정보가 등록되어 있습니다");
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
			.orElseThrow(() -> new BookSaleInfoNotFoundException("도서 판매 정보를 찾을 수 없습니다."));
	}

	@Override
	@Transactional
	public BookSaleInfoUpdateResponse update(Long id, BookSaleInfoUpdateRequest request) {
		BookSaleInfo existingEntity = bookSaleInfoRepository.findById(id)
			.orElseThrow(() -> new BookSaleInfoNotFoundException("도서 판매 정보 ID: " + id + "를 찾을 수 없습니다."));

		Book book = bookRepository.findById(request.getBookId())
			.orElseThrow(() -> new BookNotFoundException("해당 도서 ID: " + request.getBookId() + "를 찾을 수 없습니다."));

		BookSaleInfo updatedEntity = request.toEntity(existingEntity.toBuilder()
			.book(book)
			.build());

		BigDecimal salePercentage = calculateSalePercentage(updatedEntity.getPrice(), updatedEntity.getSalePrice());

		updatedEntity.setSalePercentage(salePercentage);

		BookSaleInfo savedEntity = bookSaleInfoRepository.save(updatedEntity);

		return BookSaleInfoUpdateResponse.fromEntity(savedEntity);
	}

	@Override
	@Transactional
	public void deleteById(Long id) {
		if (!bookSaleInfoRepository.existsById(id)) {
			throw new BookSaleInfoNotFoundException("도서 판매 정보를 찾을 수 없습니다.");
		}
		bookSaleInfoRepository.deleteById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<BookSaleInfo> findByBook(Book book) {
		if (book == null || book.getId() == null) {
			throw new BookNotFoundException("도서를 찾을 수 없습니다.");
		}
		return bookSaleInfoRepository.findByBook(book);
	}

	//할인율 계산
	private BigDecimal calculateSalePercentage(BigDecimal price, BigDecimal salePrice) {
		if (price == null || salePrice == null || price.compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("가격과 할인가격은 0보다 커야 합니다.");
		}

		return price.subtract(salePrice)
			.divide(price, 2, RoundingMode.HALF_UP)
			.multiply(BigDecimal.valueOf(100));
	}
}