package shop.bluebooktle.backend.book_order.entity;

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
import lombok.Setter;
import lombok.ToString;
import shop.bluebooktle.common.entity.BaseEntity;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "order_packaging")
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString(exclude = {"packagingOption", "bookOrder"})
@SQLDelete(sql = "UPDATE order_packaging SET deleted_at = CURRENT_TIMESTAMP WHERE order_packaging_id = ?")
@SQLRestriction("deleted_at IS NULL")
public class OrderPackaging extends BaseEntity {

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

	@Builder
	public OrderPackaging(PackagingOption packagingOption, BookOrder bookOrder, Integer quantity) {
		this.packagingOption = packagingOption;
		this.bookOrder = bookOrder;
		this.quantity = quantity;
	}
}
