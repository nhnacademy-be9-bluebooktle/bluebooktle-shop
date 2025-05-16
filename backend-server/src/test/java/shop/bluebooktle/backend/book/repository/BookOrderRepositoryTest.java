package shop.bluebooktle.backend.book.repository;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest

public class BookOrderRepositoryTest {
	// @Autowired
	// private BookOrderRepository bookOrderRepository;
	//
	// @Autowired
	// private BookRepository bookRepository;
	//
	// @Autowired
	// private OrderRepository orderRepository;
	//
	// @Test
	// @DisplayName("삭제되지 않은 도서 주문 조회 성공")
	// void findByIdAndDeletedAtIsNull_success() {
	// 	// given
	// 	Book book = bookRepository.save(Book.builder()
	// 		.title("테스트 도서")
	// 		.price(BigDecimal.valueOf(12000))
	// 		.build());
	//
	// 	Order order = orderRepository.save(Order.builder()
	// 		.orderDate(LocalDateTime.now())
	// 		.requestedDeliveryDate(LocalDateTime.now().plusDays(2))
	// 		.deliveryFee(BigDecimal.valueOf(3000))
	// 		.ordererName("홍길동")
	// 		.ordererPhoneNumber("01012345678")
	// 		.receiverName("김길동")
	// 		.receiverPhoneNumber("01087654321")
	// 		.address("서울시 강남구")
	// 		.detailAddress("어디빌딩 101호")
	// 		.postalCode("12345")
	// 		.trackingNumber("TRACK12345678")
	// 		.build());
	//
	// 	BookOrder saved = bookOrderRepository.save(BookOrder.builder()
	// 		.book(book)
	// 		.order(order)
	// 		.quantity(2)
	// 		.price(BigDecimal.valueOf(24000))
	// 		.build());
	//
	// 	// when
	// 	Optional<BookOrder> result = bookOrderRepository.findByIdAndDeletedAtIsNull(saved.getId());
	//
	// 	// then
	// 	assertThat(result).isPresent();
	// 	assertThat(result.get().getQuantity()).isEqualTo(2);
	// }
	//
	// @Test
	// @DisplayName("삭제된 도서 주문은 조회되지 않음")
	// void findByIdAndDeletedAtIsNull_deleted() {
	// 	// given
	// 	Book book = bookRepository.save(Book.builder().title("삭제도서").price(BigDecimal.valueOf(10000)).build());
	// 	Order order = orderRepository.save(Order.builder()
	// 		.orderDate(LocalDateTime.now())
	// 		.requestedDeliveryDate(LocalDateTime.now().plusDays(1))
	// 		.deliveryFee(BigDecimal.valueOf(2000))
	// 		.ordererName("테스트")
	// 		.ordererPhoneNumber("01011112222")
	// 		.receiverName("수신자")
	// 		.receiverPhoneNumber("01099998888")
	// 		.address("서울시")
	// 		.detailAddress("주소")
	// 		.postalCode("54321")
	// 		.trackingNumber("DEL999999")
	// 		.build());
	//
	// 	BookOrder saved = bookOrderRepository.save(BookOrder.builder()
	// 		.book(book)
	// 		.order(order)
	// 		.quantity(1)
	// 		.price(BigDecimal.valueOf(10000))
	// 		.build());
	//
	// 	saved.setDeletedAt(LocalDateTime.now());
	//
	// 	// when
	// 	Optional<BookOrder> result = bookOrderRepository.findByIdAndDeletedAtIsNull(saved.getId());
	//
	// 	// then
	// 	assertThat(result).isEmpty();
	// }
}
