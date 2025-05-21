package shop.bluebooktle.backend.book.service.impl;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.BookSaleInfo;
import shop.bluebooktle.backend.book.repository.BookRepository;
import shop.bluebooktle.backend.book.repository.BookSaleInfoRepository;
import shop.bluebooktle.backend.book.service.BookSaleInfoService;
import shop.bluebooktle.common.dto.book.emuns.State;
import shop.bluebooktle.common.dto.book.request.BookSaleInfoRegisterRequest;
import shop.bluebooktle.common.dto.book.request.BookSaleInfoUpdateRequest;
import shop.bluebooktle.common.dto.book.response.BookSaleInfoRegisterResponse;
import shop.bluebooktle.common.dto.book.response.BookSaleInfoResponse;
import shop.bluebooktle.common.dto.book.response.BookSaleInfoUpdateResponse;
import shop.bluebooktle.common.exception.book.BookNotFoundException;
import shop.bluebooktle.common.exception.book.BookSaleInfoNotFoundException;

@Service
@Transactional
@RequiredArgsConstructor
public class BookSaleInfoServiceImpl implements BookSaleInfoService {

	private final BookRepository bookRepository;
	private final BookSaleInfoRepository bookSaleInfoRepository;

	@Override
	public BookSaleInfoRegisterResponse save(BookSaleInfoRegisterRequest request) {
		Book book = bookRepository.findById(request.getBookId())
			.orElseThrow(BookNotFoundException::new);

		BookSaleInfo bookSaleInfo = BookSaleInfo.builder()
			.book(book)
			.price(request.getPrice())
			.salePrice(request.getSalePrice())
			.stock(request.getStock())
			.isPackable(request.getIsPackable())
			.salePercentage(calculateSalePercentage(request.getPrice(), request.getSalePrice()))
			.state(State.valueOf(request.getState().name()))
			.build();

		bookSaleInfoRepository.save(bookSaleInfo);

		return BookSaleInfoRegisterResponse.builder()
			.id(bookSaleInfo.getId())
			.title(book.getTitle())
			.price(bookSaleInfo.getPrice())
			.salePrice(bookSaleInfo.getSalePrice())
			.salePercentage(bookSaleInfo.getSalePercentage())
			.stock(bookSaleInfo.getStock())
			.isPackable(bookSaleInfo.isPackable())
			.state(bookSaleInfo.getState().name())
			.build();
	}

	@Override
	public BookSaleInfoUpdateResponse update(Long id, BookSaleInfoUpdateRequest request) {
		BookSaleInfo bookSaleInfo = bookSaleInfoRepository.findById(id)
			.orElseThrow(BookSaleInfoNotFoundException::new);

		bookSaleInfo = bookSaleInfo.toBuilder()
			.price(request.getPrice())
			.salePrice(request.getSalePrice())
			.stock(request.getStock())
			.isPackable(request.getIsPackable())
			.salePercentage(calculateSalePercentage(request.getPrice(), request.getSalePrice()))
			.state(State.valueOf(request.getState().name()))
			.build();

		bookSaleInfoRepository.save(bookSaleInfo);

		return BookSaleInfoUpdateResponse.builder()
			.id(bookSaleInfo.getId())
			.title(bookSaleInfo.getBook().getTitle())
			.price(bookSaleInfo.getPrice())
			.salePrice(bookSaleInfo.getSalePrice())
			.salePercentage(bookSaleInfo.getSalePercentage())
			.stock(bookSaleInfo.getStock())
			.isPackable(bookSaleInfo.isPackable())
			.state(bookSaleInfo.getState().name())
			.build();
	}

	@Override
	@Transactional(readOnly = true)
	public BookSaleInfoResponse findById(Long id) {
		BookSaleInfo bookSaleInfo = bookSaleInfoRepository.findById(id)
			.orElseThrow(BookSaleInfoNotFoundException::new);

		return BookSaleInfoResponse.builder()
			.id(bookSaleInfo.getId())
			.title(bookSaleInfo.getBook().getTitle())
			.price(bookSaleInfo.getPrice())
			.salePrice(bookSaleInfo.getSalePrice())
			.salePercentage(bookSaleInfo.getSalePercentage())
			.stock(bookSaleInfo.getStock())
			.isPackable(bookSaleInfo.isPackable())
			.state(bookSaleInfo.getState().name())
			.build();
	}

	@Override
	public void deleteById(Long id) {
		BookSaleInfo bookSaleInfo = bookSaleInfoRepository.findById(id)
			.orElseThrow(BookSaleInfoNotFoundException::new);
		bookSaleInfoRepository.delete(bookSaleInfo);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<BookSaleInfo> findByBook(Book book) {
		if (book == null) {
			throw new BookNotFoundException();
		}
		return bookSaleInfoRepository.findByBook(book);
	}

	// 할인율 계산 메서드
	private BigDecimal calculateSalePercentage(BigDecimal price, BigDecimal salePrice) {
		if (price == null || salePrice == null || price.compareTo(BigDecimal.ZERO) == 0) {
			return BigDecimal.ZERO;
		}
		return salePrice.divide(price, 2, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100));
	}
}