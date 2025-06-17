package shop.bluebooktle.common.exception;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import shop.bluebooktle.common.exception.order.BookNotOrderableException;
import shop.bluebooktle.common.exception.order.OrderNotFoundException;
import shop.bluebooktle.common.exception.order.StockNotEnoughException;
import shop.bluebooktle.common.exception.order.delivery_rule.CannotDeleteDefaultPolicyException;
import shop.bluebooktle.common.exception.order.delivery_rule.DefaultDeliveryRuleNotFoundException;
import shop.bluebooktle.common.exception.order.delivery_rule.DeliveryRuleAlreadyExistsException;
import shop.bluebooktle.common.exception.order.delivery_rule.DeliveryRuleNotFoundException;
import shop.bluebooktle.common.exception.order.order_state.OrderInvalidStateException;
import shop.bluebooktle.common.exception.order.order_state.OrderStateNotFoundException;

public class OrderExceptionCombinedTest {

	// ── delivery_rule ─────────────────────────────────────────────
	@Nested
	class DeliveryRuleExceptions {

		@Test
		@DisplayName("CannotDeleteDefaultPolicyException 테스트")
		void cannotDeleteDefaultPolicyException() {
			CannotDeleteDefaultPolicyException exception = new CannotDeleteDefaultPolicyException();

			assertThat(exception).isInstanceOf(ApplicationException.class);
			assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.DELIVERY_RULE_CANNOT_DELETE_DEFAULT);
		}

		@Test
		@DisplayName("DefaultDeliveryRuleNotFoundException 테스트")
		void defaultDeliveryRuleNotFoundException() {
			DefaultDeliveryRuleNotFoundException exception = new DefaultDeliveryRuleNotFoundException();

			assertThat(exception).isInstanceOf(ApplicationException.class);
			assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.DEFAULT_DELIVERY_RULE_NOT_FOUND);
		}

		@Test
		@DisplayName("DeliveryRuleAlreadyExistsException 테스트")
		void deliveryRuleAlreadyExistsException() {
			String ruleName = "기본배송정책";
			DeliveryRuleAlreadyExistsException exception = new DeliveryRuleAlreadyExistsException(ruleName);

			assertThat(exception).isInstanceOf(ApplicationException.class);
			assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.DELIVERY_RULE_ALREADY_EXISTS);
			assertThat(exception.getMessage()).contains(ruleName);
		}

		@Test
		@DisplayName("DeliveryRuleNotFoundException 테스트")
		void deliveryRuleNotFoundException() {
			DeliveryRuleNotFoundException exception = new DeliveryRuleNotFoundException();

			assertThat(exception).isInstanceOf(ApplicationException.class);
			assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.DELIVERY_RULE_NOT_FOUND);
		}
	}

	// ── order_state ────────────────────────────────────────────────
	@Nested
	class OrderStateExceptions {

		@Test
		@DisplayName("OrderInvalidStateException 테스트 (기본 생성자)")
		void orderInvalidStateException_default() {
			OrderInvalidStateException exception = new OrderInvalidStateException();

			assertThat(exception).isInstanceOf(ApplicationException.class);
			assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ORDER_INVALID_STATE);
		}

		@Test
		@DisplayName("OrderInvalidStateException 테스트 (이름 전달)")
		void orderInvalidStateException_named() {
			OrderInvalidStateException exception = new OrderInvalidStateException("SHIPPED");

			assertThat(exception).isInstanceOf(ApplicationException.class);
			assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ORDER_INVALID_STATE);
		}

		@Test
		@DisplayName("OrderStateNotFoundException 테스트 (기본 생성자)")
		void orderStateNotFoundException_default() {
			OrderStateNotFoundException exception = new OrderStateNotFoundException();

			assertThat(exception).isInstanceOf(ApplicationException.class);
			assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ORDER_STATE_NOT_FOUND);
		}

		@Test
		@DisplayName("OrderStateNotFoundException 테스트 (이름 전달)")
		void orderStateNotFoundException_named() {
			String name = "CANCELLED";
			OrderStateNotFoundException exception = new OrderStateNotFoundException(name);

			assertThat(exception).isInstanceOf(ApplicationException.class);
			assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ORDER_STATE_NOT_FOUND);
			assertThat(exception.getMessage()).contains(name);
		}
	}

	// ── order ─────────────────────────────────────────────────────
	@Nested
	class OrderExceptions {

		@Test
		@DisplayName("BookNotOrderableException 테스트")
		void bookNotOrderableException() {
			String reason = "품절 도서";
			BookNotOrderableException exception = new BookNotOrderableException(reason);

			assertThat(exception).isInstanceOf(ApplicationException.class);
			assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ORDER_BOOK_NOT_ORDERABLE);
			assertThat(exception.getMessage()).contains(reason);
		}

		@Test
		@DisplayName("OrderNotFoundException 테스트 (기본 생성자)")
		void orderNotFoundException_default() {
			OrderNotFoundException exception = new OrderNotFoundException();

			assertThat(exception).isInstanceOf(ApplicationException.class);
			assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ORDER_NOT_FOUND);
		}

		@Test
		@DisplayName("OrderNotFoundException 테스트 (메시지 전달)")
		void orderNotFoundException_message() {
			String message = "주문 ID 1234가 존재하지 않음";
			OrderNotFoundException exception = new OrderNotFoundException(message);

			assertThat(exception).isInstanceOf(ApplicationException.class);
			assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ORDER_NOT_FOUND);
			assertThat(exception.getMessage()).contains(message);
		}

		@Test
		@DisplayName("StockNotEnoughException 테스트")
		void stockNotEnoughException() {
			String message = "남은 재고가 부족합니다.";
			StockNotEnoughException exception = new StockNotEnoughException(message);

			assertThat(exception).isInstanceOf(ApplicationException.class);
			assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ORDER_STOCK_NOT_ENOUGH);
			assertThat(exception.getMessage()).contains(message);
		}
	}
}
