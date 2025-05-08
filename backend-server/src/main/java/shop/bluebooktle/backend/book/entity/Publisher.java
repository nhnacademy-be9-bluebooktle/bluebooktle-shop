package shop.bluebooktle.backend.book.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.bluebooktle.common.entity.BaseEntity;

@Entity
@Table(name = "publisher")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "publisherId", callSuper = false)
public class Publisher extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long publisherId;

	@Column(name = "name", nullable = false, length = 20)
	private String name;

	public Publisher(String name) {
		this.name = name;
	}
}
