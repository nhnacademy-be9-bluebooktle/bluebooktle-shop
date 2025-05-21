package shop.bluebooktle.backend.book.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import shop.bluebooktle.backend.book.dto.request.BookSaleInfoRegisterRequest;
import shop.bluebooktle.backend.book.dto.request.BookSaleInfoUpdateRequest;
import shop.bluebooktle.backend.book.dto.response.BookSaleInfoRegisterResponse;
import shop.bluebooktle.backend.book.dto.response.BookSaleInfoUpdateResponse;
import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.BookSaleInfo;
import shop.bluebooktle.backend.book.repository.BookRepository;
import shop.bluebooktle.backend.book.repository.BookSaleInfoRepository;
import shop.bluebooktle.backend.book.service.impl.BookSaleInfoServiceImpl;
import shop.bluebooktle.common.exception.book.BookSaleInfoAlreadyExistsException;
import shop.bluebooktle.common.exception.book.BookSaleInfoNotFoundException;

@ExtendWith(MockitoExtension.class)
class BookSaleInfoServiceTest {

	@Mock
	private BookRepository bookRepository;

	@Mock
	private BookSaleInfoRepository bookSaleInfoRepository;

	@InjectMocks
	private BookSaleInfoServiceImpl bookSaleInfoService;

	@Test
	@DisplayName("도서 판매 정보 저장 성공")
	void saveBookSaleInfo_Success() {
		// given
		Long bookId = 1L;
		Book book = Book.builder().id(bookId).title("Test Book").build();

		BookSaleInfoRegisterRequest request = BookSaleInfoRegisterRequest.builder()
			.bookId(bookId)
			.price(new BigDecimal("20000"))
			.salePrice(new BigDecimal("15000"))
			.stock(50)
			.isPackable(true)
			.state(BookSaleInfo.State.AVAILABLE)
			.build();

		BookSaleInfo bookSaleInfo = request.toEntity(book);
		when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
		when(bookSaleInfoRepository.findByBook(book)).thenReturn(Optional.empty());
		when(bookSaleInfoRepository.save(any(BookSaleInfo.class))).thenReturn(bookSaleInfo);

		// when
		BookSaleInfoRegisterResponse response = bookSaleInfoService.save(request);

		// then
		assertThat(response).isNotNull();
		assertThat(response.getPrice()).isEqualTo(bookSaleInfo.getPrice());
		assertThat(response.getState()).isEqualTo(bookSaleInfo.getState().name());

		verify(bookRepository, times(1)).findById(bookId);
		verify(bookSaleInfoRepository, times(1)).findByBook(book);
		verify(bookSaleInfoRepository, times(1)).save(any(BookSaleInfo.class));
	}

	@Test
	@DisplayName("도서 판매 정보 저장 실패 - 중복된 도서판매정보")
	void saveBookSaleInfo_Fail_DuplicateInfo() {
		// given
		Long bookId = 1L;
		Book book = Book.builder().id(bookId).title("Test Book").build();

		BookSaleInfoRegisterRequest request = BookSaleInfoRegisterRequest.builder()
			.bookId(bookId)
			.price(new BigDecimal("20000"))
			.salePrice(new BigDecimal("15000"))
			.stock(50)
			.isPackable(true)
			.state(BookSaleInfo.State.AVAILABLE)
			.build();

		when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
		when(bookSaleInfoRepository.findByBook(book)).thenReturn(Optional.of(BookSaleInfo.builder().build()));

		// when & then
		assertThatThrownBy(() -> bookSaleInfoService.save(request))
			.isInstanceOf(BookSaleInfoAlreadyExistsException.class);

		verify(bookRepository, times(1)).findById(bookId);
		verify(bookSaleInfoRepository, times(1)).findByBook(book);
		verify(bookSaleInfoRepository, never()).save(any());
	}

	@Test
	@DisplayName("도서 판매 정보 수정 성공")
	void updateBookSaleInfo_Success() {
		// given
		Long saleInfoId = 1L;
		Long bookId = 2L;
		Book existingBook = Book.builder().id(bookId).title("Existing Book").build();
		Book newBook = Book.builder().id(bookId).title("New Book").build();

		BookSaleInfo existingEntity = BookSaleInfo.builder()
			.id(saleInfoId)
			.book(existingBook)
			.price(new BigDecimal("30000"))
			.salePrice(new BigDecimal("25000"))
			.stock(20)
			.build();

		BookSaleInfoUpdateRequest request = BookSaleInfoUpdateRequest.builder()
			.bookId(bookId)
			.price(new BigDecimal("35000"))
			.salePrice(new BigDecimal("30000"))
			.stock(30)
			.isPackable(false)
			.state(BookSaleInfo.State.LOW_STOCK)
			.build();

		BookSaleInfo updatedEntity = request.toEntity(existingEntity.toBuilder().build());

		when(bookSaleInfoRepository.findById(saleInfoId)).thenReturn(Optional.of(existingEntity));
		when(bookRepository.findById(bookId)).thenReturn(Optional.of(newBook));
		when(bookSaleInfoRepository.save(any(BookSaleInfo.class))).thenReturn(updatedEntity);

		// when
		BookSaleInfoUpdateResponse response = bookSaleInfoService.update(saleInfoId, request);

		// then
		assertThat(response).isNotNull();
		assertThat(response.getId()).isEqualTo(saleInfoId);
		assertThat(response.getState()).isEqualTo(BookSaleInfo.State.LOW_STOCK.name());

		verify(bookSaleInfoRepository, times(1)).findById(saleInfoId);
		verify(bookRepository, times(1)).findById(bookId);
		verify(bookSaleInfoRepository, times(1)).save(any(BookSaleInfo.class));
	}

	@Test
	@DisplayName("도서 판매 정보 삭제 성공")
	void deleteBookSaleInfoById_Success() {
		// given
		Long saleInfoId = 1L;

		when(bookSaleInfoRepository.existsById(saleInfoId)).thenReturn(true);

		// when
		bookSaleInfoService.deleteById(saleInfoId);

		// then
		verify(bookSaleInfoRepository, times(1)).existsById(saleInfoId);
		verify(bookSaleInfoRepository, times(1)).deleteById(saleInfoId);
	}

	@Test
	@DisplayName("도서 판매 정보 삭제 실패 - 존재하지 않는 도서판매정보")
	void deleteBookSaleInfoById_Fail_NotFound() {
		// given
		Long saleInfoId = 1L;

		when(bookSaleInfoRepository.existsById(saleInfoId)).thenReturn(false);

		// when & then
		assertThatThrownBy(() -> bookSaleInfoService.deleteById(saleInfoId))
			.isInstanceOf(BookSaleInfoNotFoundException.class);

		verify(bookSaleInfoRepository, times(1)).existsById(saleInfoId);
		verify(bookSaleInfoRepository, never()).deleteById(any());
	}
}