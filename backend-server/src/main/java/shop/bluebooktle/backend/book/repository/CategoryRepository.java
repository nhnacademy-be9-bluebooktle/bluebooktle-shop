package shop.bluebooktle.backend.book.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.book.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long>, CategoryQueryRepository {
	
	// 최상위 카테고리들(대분류) 조회 (parentCategory가 null인 경우)
	List<Category> findByParentCategoryIsNull();

	// 카테고리 이름으로 정확히 일치하는 항목 조회
	Category findByName(String name);

	// 특정 이름의 카테고리가 존재 유무 확인
	boolean existsByName(String name);

	// 최상위 카테고리인지 여부 판단
	boolean existsByIdAndParentCategoryIsNull(Long id);

	Category findParentCategoryById(Long id);

}
