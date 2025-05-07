package shop.bluebooktle.backend.book_order.entity;

import java.math.BigDecimal;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.order.entity.Order;
import shop.bluebooktle.common.entity.BaseEntity;

@Entity
@Table(name = "book_order")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString(exclude = {"order", "book"})
@SQLDelete(sql = "UPDATE book_order SET deleted_at = CURRENT_TIMESTAMP WHERE book_order_id = ?")
@SQLRestriction("deleted_at IS NULL")
public class BookOrder extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "book_order_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", nullable = false)
	private Order order;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "book_id", nullable = false)
	private Book book;

	@Column(name = "quantity", nullable = false)
	private Integer quantity;

	@Column(name = "price", precision = 10, scale = 2, nullable = false)
	private BigDecimal price;

	@Builder
	public BookOrder(Order order, Book book, Integer quantity, BigDecimal price) {
		this.order = order;
		this.book = book;
		this.quantity = quantity;
		this.price = price;
	}

}
