package shop.bluebooktle.backend.book.jpa;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import shop.bluebooktle.backend.book_order.repository.BookOrderRepository;
import shop.bluebooktle.backend.config.JpaAuditingConfiguration;
import shop.bluebooktle.backend.config.QueryDslConfig;
import shop.bluebooktle.backend.order.repository.OrderRepository;

//TODO 도서 구현된 후에 구현 예정
@DataJpaTest
@ActiveProfiles("test")
@Import({QueryDslConfig.class, JpaAuditingConfiguration.class})
public class BookOrderRepositoryTest { //custom Repository Test 진행
	@Autowired
	private BookOrderRepository bookOrderRepository;

	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private OrderRepository orderRepository;

	@Test
	@DisplayName("삭제되지 않은 도서 주문 조회 - 성공")
	void findByIdAndDeletedAtIsNull_success() {
		// given

		// when

		// then
	}

	@Test
	@DisplayName("삭제된 도서 주문 조회 누락 - 성공")
	void findByIdAndDeletedAtIsNull_deleted() {
		// given

		// when

		// then
	}
}
