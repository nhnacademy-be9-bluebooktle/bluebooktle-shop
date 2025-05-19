package shop.bluebooktle.backend.book.entity;

import java.util.ArrayList;
import java.util.List;

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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import shop.bluebooktle.common.entity.BaseEntity;

@Entity
@Table(name = "category")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString(exclude = {"parentCategory", "childCategories"})
@SQLDelete(sql = "UPDATE category SET deleted_at = CURRENT_TIMESTAMP WHERE category_id = ?")
@SQLRestriction("deleted_at IS NULL")
@EqualsAndHashCode(of = "id", callSuper = false)
public class Category extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "category_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_category_id")
	@Setter
	private Category parentCategory;

	@Setter
	@Column(name = "name", nullable = false, length = 50)
	private String name;

	@Column(name = "category_path", nullable = false)
	@Setter
	private String categoryPath;

	@OneToMany(
		mappedBy = "parentCategory",
		fetch = FetchType.LAZY
	)
	private List<Category> childCategories = new ArrayList<>();

	public void addChildCategory(Category child) {
		child.parentCategory = this;
		this.childCategories.add(child);
	}

	public Category(Category parentCategory, String name, String categoryPath) {
		this.parentCategory = parentCategory;
		this.name = name;
		this.categoryPath = categoryPath;
	}

	@Builder
	public Category(Category parentCategory, String name) {
		this.parentCategory = parentCategory;
		this.name = name;
		this.categoryPath = "default";
	}
}
