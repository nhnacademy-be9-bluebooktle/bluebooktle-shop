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
@Table(name = "img")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
@SQLDelete(sql = "UPDATE img SET deleted_at = CURRENT_TIMESTAMP WHERE img_id = ?")
@SQLRestriction("deleted_at IS NULL")
public class Img extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "img_id")
	private Long id;

	@Setter
	@Column(name = "img_url", nullable = false, length = 255)
	private String imgUrl;

	@Builder
	public Img(String imgUrl) {
		this.imgUrl = imgUrl;
	}
}
