package shop.bluebooktle.backend.book_order.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "order_packaging")
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString(exclude = {"packagingOption", "bookOrder"})
public class OrderPackaging {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "order_packaging_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "package_id", nullable = false)
	private PackagingOption packagingOption;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "book_order_id", nullable = false)
	private BookOrder bookOrder;

	@Column(name = "quantity", nullable = false)
	private Integer quantity;
	
}
