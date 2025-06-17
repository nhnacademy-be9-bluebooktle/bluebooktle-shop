package shop.bluebooktle.common.exception;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import shop.bluebooktle.common.exception.book_order.BookOrderNotFoundException;
import shop.bluebooktle.common.exception.book_order.OrderPackagingNotFoundException;
import shop.bluebooktle.common.exception.book_order.PackagingOptionAlreadyExistsException;
import shop.bluebooktle.common.exception.book_order.PackagingOptionNotFoundException;
import shop.bluebooktle.common.exception.book_order.PackagingQuantityExceedException;

class BookOrderExceptionCombinedTest {

	@Test
	@DisplayName("BookOrderNotFoundException 테스트")
	void bookOrderNotFoundException() {
		BookOrderNotFoundException exception = new BookOrderNotFoundException();

		assertThat(exception)
			.isInstanceOf(BookOrderNotFoundException.class)
			.isInstanceOf(ApplicationException.class);

		assertThat(exception.getErrorCode())
			.isEqualTo(ErrorCode.G_BOOK_ORDER_NOT_FOUND);
	}

	@Test
	@DisplayName("PackagingQuantityExceedException 테스트")
	void packagingQuantityExceedException() {
		PackagingQuantityExceedException exception = new PackagingQuantityExceedException();

		assertThat(exception)
			.isInstanceOf(PackagingQuantityExceedException.class)
			.isInstanceOf(ApplicationException.class);

		assertThat(exception.getErrorCode())
			.isEqualTo(ErrorCode.G_PACKAGING_QUANTITY_EXCEEDS_BOOK_ORDER);
	}

	@Test
	@DisplayName("PackagingOptionAlreadyExistsException 테스트")
	void packagingOptionAlreadyExistsException() {
		PackagingOptionAlreadyExistsException exception = new PackagingOptionAlreadyExistsException();

		assertThat(exception)
			.isInstanceOf(PackagingOptionAlreadyExistsException.class)
			.isInstanceOf(ApplicationException.class);

		assertThat(exception.getErrorCode())
			.isEqualTo(ErrorCode.G_ORDER_PACKAGING_OPTION_ALREADY_EXITS);
	}

	@Test
	@DisplayName("OrderPackagingNotFoundException 테스트")
	void orderPackagingNotFoundException() {
		OrderPackagingNotFoundException exception = new OrderPackagingNotFoundException();

		assertThat(exception)
			.isInstanceOf(OrderPackagingNotFoundException.class)
			.isInstanceOf(ApplicationException.class);

		assertThat(exception.getErrorCode())
			.isEqualTo(ErrorCode.G_ORDER_PACKAGING_NOT_FOUND);
	}

	@Test
	@DisplayName("PackagingOptionNotFoundException 테스트")
	void packagingOptionNotFoundException() {
		PackagingOptionNotFoundException exception = new PackagingOptionNotFoundException();

		assertThat(exception)
			.isInstanceOf(PackagingOptionNotFoundException.class)
			.isInstanceOf(ApplicationException.class);

		assertThat(exception.getErrorCode())
			.isEqualTo(ErrorCode.G_ORDER_PACKAGING_OPTION_NOT_FOUND);
	}

}
