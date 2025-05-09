package shop.bluebooktle.backend.book.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import shop.bluebooktle.backend.book.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
	// 특정 부모 카테고리의 자식 카테고리 조회
	List<Category> findByParentCategory(Category parent);

	// 최상위 카테고리들(대분류) 조회 (parentCategory가 null인 경우)
	List<Category> findByParentCategoryIsNull();

	// 카테고리 이름으로 정확히 일치하는 항목 조회
	Category findByName(String name);

	// 특정 이름의 카테고리가 존재 유무 확인
	boolean existsByName(String name);

	// 같은 이름을 가진 카테고리가 다른 ID로 있는지 확인 (중복 방지용, 같은 카테고리명으로 변경 시 확인 가능)
	boolean existsByNameAndIdNot(String name, Long id);

	// 여러 ID로 카테고리들 한 번에 조회
	List<Category> findByIdIn(List<Long> ids);

	// 부모 ID 기준으로 자식 카테고리를 이름 순으로 정렬
	List<Category> findByParentCategoryOrderByNameAsc(Category parent);

	// 최상위 카테고리인지 여부 판단
	boolean existsByIdAndParentCategoryIsNull(Long id);

	@Query("select distinct c.parentCategory from Category c where c.id in :ids and c.parentCategory is not null")
	List<Category> findParentCategoriesByChildIds(@Param("ids") List<Long> ids);

	@Query("select c.parentCategory from Category c where c.id = :id")
	Category findParentCategoryById(@Param("id") Long id);
}
