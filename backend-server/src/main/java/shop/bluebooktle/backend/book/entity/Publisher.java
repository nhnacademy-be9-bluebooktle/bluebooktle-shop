package shop.bluebooktle.backend.book.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.bluebooktle.common.entity.BaseEntity;

@Entity
@Table(name = "publisher")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(of = "id", callSuper = false)
@SQLDelete(sql = "UPDATE publisher SET deleted_at = CURRENT_TIMESTAMP WHERE publisher_id = ?")
@SQLRestriction("deleted_at IS NULL")
public class Publisher extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "publisher_id")
	private Long id;

	@Column(name = "name", nullable = false, length = 20)
	@Setter
	private String name;

	public Publisher(String name) {
		this.name = name;
	}
}
