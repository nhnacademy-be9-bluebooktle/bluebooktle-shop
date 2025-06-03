package shop.bluebooktle.backend.book.entity;

import java.math.BigDecimal;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.bluebooktle.common.dto.book.BookSaleInfoState;
import shop.bluebooktle.common.entity.BaseEntity;

@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "book_sale_info")
@EqualsAndHashCode(of = "id", callSuper = false)
@SQLDelete(sql = "UPDATE book_sale_info SET deleted_at = CURRENT_TIMESTAMP WHERE book_sale_info_id = ?")
@SQLRestriction("deleted_at IS NULL")
public class BookSaleInfo extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "book_sale_info_id")
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "book_id", nullable = false, unique = true)
	private Book book;

	@Column(name = "price", nullable = false, precision = 10, scale = 2)
	private BigDecimal price;

	@Column(name = "sale_price", nullable = false, precision = 10, scale = 2)
	private BigDecimal salePrice;

	@Column(name = "stock", nullable = false)
	private Integer stock;

	@Column(name = "is_packable")
	private boolean isPackable;

	@Setter
	@Column(name = "sale_percentage", nullable = false, precision = 10, scale = 2)
	private BigDecimal salePercentage;

	@Builder.Default
	@Column(name = "state")
	@Enumerated(EnumType.STRING)
	private BookSaleInfoState bookSaleInfoState = BookSaleInfoState.AVAILABLE;

	@Builder.Default
	@Column(name = "view_count", nullable = false, columnDefinition = "BIGINT DEFAULT 0")
	private Long viewCount = 0L;

	@Builder.Default
	@Column(name = "search_count", nullable = false, columnDefinition = "BIGINT DEFAULT 0")
	private Long searchCount = 0L;

	@Builder.Default
	@Column(name = "star", nullable = false, columnDefinition = "DECIMAL(2,1) DEFAULT 0.0")
	private BigDecimal star = BigDecimal.valueOf(0.0);

	@Builder.Default
	@Column(name = "review_count", nullable = false, columnDefinition = "BIGINT DEFAULT 0")
	private Long reviewCount = 0L;
}
