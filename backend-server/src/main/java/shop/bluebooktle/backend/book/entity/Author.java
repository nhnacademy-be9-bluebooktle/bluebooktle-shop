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
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.bluebooktle.common.entity.BaseEntity;

@Entity
@Table(name = "author")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
@SQLDelete(sql = "UPDATE author SET deleted_at = CURRENT_TIMESTAMP WHERE author_id = ?")
@SQLRestriction("deleted_at IS NULL")
public class Author extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "author_id")
	private Long id;

	@Setter
	@Column(name = "name", nullable = false, length = 50) //공동, 외국인작가 이름 고려해서 수정해서 테스트중 원래 length10
	private String name;

	@Setter
	@Column(name = "description", nullable = false, columnDefinition = "TEXT")
	private String description;

	@Setter
	@Column(name = "author_key", nullable = false, length = 50)
	private String authorKey;

	@Builder
	public Author(String name, String description) {
		this.name = name;
		this.description = description;
	}
}
