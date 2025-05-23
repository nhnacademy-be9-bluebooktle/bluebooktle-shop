package shop.bluebooktle.backend.book.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.anyLong;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book.entity.BookSaleInfo;
import shop.bluebooktle.backend.book.repository.BookRepository;
import shop.bluebooktle.backend.book.repository.BookSaleInfoRepository;
import shop.bluebooktle.backend.book.service.impl.BookSaleInfoServiceImpl;
import shop.bluebooktle.common.dto.book.request.BookSaleInfoRegisterRequest;
import shop.bluebooktle.common.dto.book.request.BookSaleInfoUpdateRequest;
import shop.bluebooktle.common.dto.book.response.BookSaleInfoRegisterResponse;
import shop.bluebooktle.common.dto.book.response.BookSaleInfoUpdateResponse;
import shop.bluebooktle.common.exception.book.BookNotFoundException;
import shop.bluebooktle.common.exception.book.BookSaleInfoNotFoundException;

@ExtendWith(MockitoExtension.class)
class BookSaleInfoServiceTest {

	@InjectMocks
	private BookSaleInfoServiceImpl bookSaleInfoService;

	@Mock
	private BookRepository bookRepository;

	@Mock
	private BookSaleInfoRepository bookSaleInfoRepository;

	@Test
	@DisplayName("도서 판매 정보 등록 성공")
	void saveBookSaleInfo_Success() {
		// given
		Book book = Book.builder().id(1L).title("Book Title").build();
		BookSaleInfoRegisterRequest request = BookSaleInfoRegisterRequest.builder()
			.bookId(1L)
			.price(BigDecimal.valueOf(100))
			.salePrice(BigDecimal.valueOf(80))
			.stock(10)
			.isPackable(true)
			.build();

		when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
		when(bookSaleInfoRepository.save(any(BookSaleInfo.class))).thenAnswer(invocation -> invocation.getArgument(0));

		// when
		BookSaleInfoRegisterResponse response = bookSaleInfoService.save(request);

		// then
		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Book Title");
		assertThat(response.getPrice()).isEqualTo(BigDecimal.valueOf(100));
		assertThat(response.getSalePrice()).isEqualTo(BigDecimal.valueOf(80));
		verify(bookSaleInfoRepository, times(1)).save(any(BookSaleInfo.class));
	}

	@Test
	@DisplayName("도서 판매 정보 등록 실패 - 도서를 찾을 수 없음")
	void saveBookSaleInfo_BookNotFound_ShouldThrowException() {
		// given
		BookSaleInfoRegisterRequest request = BookSaleInfoRegisterRequest.builder()
			.bookId(99L)
			.price(BigDecimal.valueOf(100))
			.salePrice(BigDecimal.valueOf(80))
			.stock(10)
			.isPackable(true)
			.build();

		when(bookRepository.findById(99L)).thenReturn(Optional.empty());

		// when / then
		assertThatThrownBy(() -> bookSaleInfoService.save(request))
			.isInstanceOf(BookNotFoundException.class);

		verify(bookSaleInfoRepository, never()).save(any(BookSaleInfo.class));
	}

	@Test
	@DisplayName("도서 판매 정보 수정 성공")
	void updateBookSaleInfo_Success() {
		// given
		Book book = Book.builder().id(1L).title("Book Title").build();
		BookSaleInfo bookSaleInfo = BookSaleInfo.builder()
			.id(1L)
			.book(book)
			.price(BigDecimal.valueOf(100))
			.salePrice(BigDecimal.valueOf(80))
			.stock(10)
			.build();

		BookSaleInfoUpdateRequest request = BookSaleInfoUpdateRequest.builder()
			.bookId(1L)
			.price(BigDecimal.valueOf(120))
			.salePrice(BigDecimal.valueOf(100))
			.stock(15)
			.isPackable(false)
			.build();

		when(bookSaleInfoRepository.findById(1L)).thenReturn(Optional.of(bookSaleInfo));
		when(bookSaleInfoRepository.save(any(BookSaleInfo.class))).thenAnswer(invocation -> invocation.getArgument(0));

		// when
		BookSaleInfoUpdateResponse response = bookSaleInfoService.update(1L, request);

		// then
		assertThat(response).isNotNull();
		assertThat(response.getPrice()).isEqualTo(BigDecimal.valueOf(120));
		assertThat(response.getSalePrice()).isEqualTo(BigDecimal.valueOf(100));
		assertThat(response.getStock()).isEqualTo(15);
		assertThat(response.getIsPackable()).isFalse();
		verify(bookSaleInfoRepository, times(1)).save(bookSaleInfo);
	}

	@Test
	@DisplayName("도서 판매 정보 수정 실패 - 판매 정보를 찾을 수 없음")
	void updateBookSaleInfo_NotFound_ShouldThrowException() {
		// given
		BookSaleInfoUpdateRequest request = BookSaleInfoUpdateRequest.builder()
			.bookId(1L)
			.price(BigDecimal.valueOf(120))
			.salePrice(BigDecimal.valueOf(100))
			.stock(15)
			.isPackable(false)
			.build();

		when(bookSaleInfoRepository.findById(1L)).thenReturn(Optional.empty());

		// when / then
		assertThatThrownBy(() -> bookSaleInfoService.update(1L, request))
			.isInstanceOf(BookSaleInfoNotFoundException.class);

		verify(bookSaleInfoRepository, never()).save(any(BookSaleInfo.class));
	}

	@Test
	@DisplayName("도서 판매 정보 삭제 성공")
	void deleteBookSaleInfo_Success() {
		// given
		BookSaleInfo bookSaleInfo = BookSaleInfo.builder().id(1L).build();
		when(bookSaleInfoRepository.findById(1L)).thenReturn(Optional.of(bookSaleInfo));

		// when
		bookSaleInfoService.deleteById(1L);

		// then
		verify(bookSaleInfoRepository, times(1)).delete(bookSaleInfo);
	}

	@Test
	@DisplayName("도서 판매 정보 삭제 실패 - 판매 정보를 찾을 수 없음")
	void deleteBookSaleInfo_NotFound_ShouldThrowException() {
		// given
		when(bookSaleInfoRepository.findById(1L)).thenReturn(Optional.empty());

		// when / then
		assertThatThrownBy(() -> bookSaleInfoService.deleteById(1L))
			.isInstanceOf(BookSaleInfoNotFoundException.class);

		verify(bookSaleInfoRepository, never()).deleteById(anyLong());
	}
}