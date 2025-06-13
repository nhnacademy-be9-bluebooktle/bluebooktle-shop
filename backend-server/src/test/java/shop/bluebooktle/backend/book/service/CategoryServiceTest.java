package shop.bluebooktle.backend.book.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.book.entity.Category;
import shop.bluebooktle.backend.book.repository.BookCategoryRepository;
import shop.bluebooktle.backend.book.repository.CategoryRepository;
import shop.bluebooktle.backend.book.service.impl.CategoryServiceImpl;
import shop.bluebooktle.common.dto.book.request.CategoryRegisterRequest;
import shop.bluebooktle.common.dto.book.request.CategoryUpdateRequest;
import shop.bluebooktle.common.dto.book.request.RootCategoryRegisterRequest;
import shop.bluebooktle.common.dto.book.response.CategoryResponse;
import shop.bluebooktle.common.dto.book.response.CategoryTreeResponse;
import shop.bluebooktle.common.exception.book.CategoryAlreadyExistsException;
import shop.bluebooktle.common.exception.book.CategoryCannotDeleteException;
import shop.bluebooktle.common.exception.book.CategoryCannotDeleteRootException;
import shop.bluebooktle.common.exception.book.CategoryNotFoundException;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class CategoryServiceTest {

	@InjectMocks
	private CategoryServiceImpl categoryService;

	@Mock
	private CategoryRepository categoryRepository;

	@Mock
	private BookCategoryRepository bookCategoryRepository;

	@Nested
	@DisplayName("루트 카테고리 등록")
	class RegisterRootCategoryTests {

		@BeforeEach
		void setUpRoot() {
			// 기존 루트 카테고리 조회만 stub
			when(categoryRepository.findByParentCategoryIsNull())
				.thenReturn(List.of(new Category(null, "중복 이름", "/1")));
		}

		@Test
		@DisplayName("최상위 카테고리 저장 성공")
		void successRegisterRootCategory() {

			when(categoryRepository.save(any(Category.class)))
				.thenAnswer(inv -> {
					Category c = inv.getArgument(0);
					long id = (c.getParentCategory() == null ? 2L : 3L);
					ReflectionTestUtils.setField(c, "id", id);
					return c;
				});

			RootCategoryRegisterRequest req = new RootCategoryRegisterRequest("부모 카테고리", "자식 카테고리");
			categoryService.registerRootCategory(req);

			verify(categoryRepository, times(2)).save(any(Category.class));
		}

		@Test
		@DisplayName("실패: 중복 이름 예외")
		void duplicateNameThrows() {
			RootCategoryRegisterRequest req = new RootCategoryRegisterRequest("중복 이름", "자식 카테고리");
			assertThatThrownBy(() -> categoryService.registerRootCategory(req))
				.isInstanceOf(CategoryAlreadyExistsException.class)
				.hasMessageContaining("이미 존재하는 카테고리명입니다");

			// 예외 상황에서는 save() 가 호출되지 않아야 함
			verify(categoryRepository, never()).save(any());
		}
	}

	@Nested
	@DisplayName("하위 카테고리 등록")
	class RegisterSubCategoryTests {

		private Category parent;
		private Category child;

		@BeforeEach
		void setUpSub() {
			// 부모 조회만 stub
			parent = Category.builder().name("부모 카테고리").build();
			ReflectionTestUtils.setField(parent, "id", 1L);
			when(categoryRepository.findById(1L))
				.thenReturn(Optional.of(parent));
		}

		@Test
		@DisplayName("하위 카테고리 등록 성공")
		void success() {
			// 성공 케이스에서만 save() stub
			when(categoryRepository.save(any(Category.class)))
				.thenAnswer(inv -> {
					Category c = inv.getArgument(0);
					ReflectionTestUtils.setField(c, "id", 3L);
					return c;
				});

			CategoryRegisterRequest request = new CategoryRegisterRequest("자식 카테고리");
			categoryService.registerCategory(1L, request);

			verify(categoryRepository, times(3)).save(any());
		}

		@Test
		@DisplayName("하위 카테고리 실패 : 부모 카테고리의 하위 카테고리 중복 이름 예외")
		void duplicateNameThrows() {

			child = Category.builder().name("자식 카테고리 이름 중복").build();
			ReflectionTestUtils.setField(child, "id", 2L);
			when(categoryRepository.getAllDescendantCategories(any()))
				.thenReturn(List.of(child));

			CategoryRegisterRequest request = new CategoryRegisterRequest("자식 카테고리 이름 중복");

			assertThatThrownBy(() -> categoryService.registerCategory(1L, request))
				.isInstanceOf(CategoryAlreadyExistsException.class)
				.hasMessageContaining("이미 존재하는 카테고리명입니다.");

			verify(categoryRepository, never()).save(any());
		}
	}

	@Nested
	@DisplayName("카테고리 수정")
	class UpdateCategoryTests {
		private Category root;
		private Category child;
		private Category target;

		@BeforeEach
		void setUpUpdate() {
			// 부모 카테고리 준비
			root = Category.builder()
				.name("상위 카테고리")
				.build();
			ReflectionTestUtils.setField(root, "id", 1L);

			// 기존 부모에 등록된 자식 카테고리
			child = Category.builder()
				.name("중복 이름")
				.build();
			ReflectionTestUtils.setField(child, "id", 2L);
		}

		@Test
		@DisplayName("성공: 중간 카테고리 수정")
		void success() {
			// 수정 대상 카테고리
			target = Category.builder()
				.name("기존 이름")
				.parentCategory(root)
				.build();
			ReflectionTestUtils.setField(target, "id", 3L);

			// repository.findById -> target
			when(categoryRepository.findById(target.getId()))
				.thenReturn(Optional.of(target));

			CategoryUpdateRequest request = new CategoryUpdateRequest("새 이름");

			when(categoryRepository.getAllDescendantCategories(any())).thenReturn(List.of(child));

			categoryService.updateCategory(target.getId(), request);

			// save 호출 검증
			verify(categoryRepository, times(1)).save(target);
			// name 이 정상 변경되었는지 검증
			assertThat(target.getName()).isEqualTo("새 이름");
		}

		@Test
		@DisplayName("성공: 최상위 카테고리 수정")
		void successRootCategory() {
			// 수정 대상 카테고리
			target = Category.builder()
				.name("기존 이름")
				.parentCategory(null)
				.build();
			ReflectionTestUtils.setField(target, "id", 3L);

			when(categoryRepository.findById(3L))
				.thenReturn(Optional.of(target));

			CategoryUpdateRequest request = new CategoryUpdateRequest("새 이름");

			when(categoryRepository.findByParentCategoryIsNull())
				.thenReturn(List.of(root));

			categoryService.updateCategory(target.getId(), request);

			verify(categoryRepository, times(1)).save(any());
			assertThat(target.getName()).isEqualTo("새 이름");
		}

		@Test
		@DisplayName("실패: 부모 카테고리의 하위 카테고리 중복 이름 예외")
		void duplicateNameThrows() {
			target = Category.builder()
				.name("기존 이름")
				.parentCategory(root)
				.build();
			ReflectionTestUtils.setField(target, "id", 3L);

			when(categoryRepository.findById(3L))
				.thenReturn(Optional.of(target));

			CategoryUpdateRequest request = new CategoryUpdateRequest("중복 이름");

			when(categoryRepository.getAllDescendantCategories(any()))
				.thenReturn(List.of(child));

			assertThatThrownBy(() -> categoryService.updateCategory(target.getId(), request))
				.isInstanceOf(CategoryAlreadyExistsException.class)
				.hasMessageContaining("이미 존재하는 카테고리명입니다.");

			verify(categoryRepository, never()).save(any());
		}

		@Test
		@DisplayName("실패: 최상위 카테고리 수정의 경우 최상위 카테고리명 중복 예외")
		void duplicateNameByRootCategoryThrows() {
			target = Category.builder()
				.name("기존 이름")
				.parentCategory(null)
				.build();
			ReflectionTestUtils.setField(target, "id", 3L);

			when(categoryRepository.findById(3L))
				.thenReturn(Optional.of(target));

			CategoryUpdateRequest request = new CategoryUpdateRequest("상위 카테고리");

			when(categoryRepository.findByParentCategoryIsNull())
				.thenReturn(List.of(root));

			assertThatThrownBy(() -> categoryService.updateCategory(target.getId(), request))
				.isInstanceOf(CategoryAlreadyExistsException.class)
				.hasMessageContaining("이미 존재하는 카테고리명입니다.");

			verify(categoryRepository, never()).save(any());
		}
	}

	@Nested
	@DisplayName("카테고리 삭제")
	class DeleteCategoryTests {

		private Category root;
		private Category child;
		private Category target;

		@BeforeEach
		void setUpUpdate() {
			// 부모 카테고리 준비
			root = Category.builder()
				.name("최상위 카테고리")
				.build();
			ReflectionTestUtils.setField(root, "id", 1L);

			// 기존 부모에 등록된 자식 카테고리
			child = Category.builder()
				.name("자식 카테고리")
				.parentCategory(root)
				.build();
			ReflectionTestUtils.setField(child, "id", 2L);
			root.addChildCategory(child);
		}

		@Test
		@DisplayName("성공 : 리프 카테고리를 삭제할 경우")
		void success() {
			target = Category.builder()
				.name("삭제 대상")
				.parentCategory(root)
				.build();
			ReflectionTestUtils.setField(target, "id", 3L);
			root.addChildCategory(target);

			when(categoryRepository.findById(target.getId())).thenReturn(Optional.of(target));
			when(categoryRepository.findById(root.getId())).thenReturn(Optional.of(root));
			when(categoryRepository.getAllDescendantCategories(target)).thenReturn(List.of());
			when(bookCategoryRepository.existsByCategory(target)).thenReturn(false);
			when(categoryRepository.existsByIdAndParentCategoryIsNull(target.getParentCategory().getId())).thenReturn(true);
			categoryService.deleteCategory(target.getId());

			verify(categoryRepository, times(1)).delete(target);
		}

		@Test
		@DisplayName("성공 : 삭제 카테고리에 하위 카테고리가 존재하는 경우")
		void successWithChildCategory() {
			target = Category.builder()
				.name("삭제 대상")
				.parentCategory(root)
				.build();
			ReflectionTestUtils.setField(target, "id", 3L);

			Category targetChild = Category.builder()
				.name("삭제 대상 하위 카테고리")
				.parentCategory(target)
				.build();
			ReflectionTestUtils.setField(targetChild, "id", 4L);
			target.addChildCategory(targetChild);

			when(categoryRepository.findById(target.getId())).thenReturn(Optional.of(target));
			when(bookCategoryRepository.existsByCategory(target)).thenReturn(false);
			when(categoryRepository.getAllDescendantCategories(any())).thenReturn(List.of(targetChild));
			when(bookCategoryRepository.existsByCategory(targetChild)).thenReturn(false);
			categoryService.deleteCategory(target.getId());

			verify(categoryRepository, times(2)).delete(any());
		}

		@Test
		@DisplayName("성공: 부모 카테고리가 null인 경우 (조건문 진입 안 함)")
		void successWhenParentIsNull() {
			when(categoryRepository.findById(1L)).thenReturn(Optional.of(root));
			when(bookCategoryRepository.existsByCategory(any())).thenReturn(false);
			when(categoryRepository.getAllDescendantCategories(root)).thenReturn(List.of());

			// when
			categoryService.deleteCategory(1L);

			// then
			verify(categoryRepository).delete(root);
		}


		@Test
		@DisplayName("실패: 해당 카테고리에 BookCategory 존재 시 예외")
		void throwsWhenBookExistsOnCategory() {
			// given: parent-child 세팅
			target = Category.builder()
					.name("삭제 대상")
					.parentCategory(root)
					.build();
			ReflectionTestUtils.setField(target, "id", 3L);
			root.addChildCategory(target);

			when(categoryRepository.findById(target.getId())).thenReturn(Optional.of(target));
			when(bookCategoryRepository.existsByCategory(target)).thenReturn(true);

			// when / then
			assertThatThrownBy(() -> categoryService.deleteCategory(target.getId()))
				.isInstanceOf(CategoryCannotDeleteRootException.class);

			verify(bookCategoryRepository).existsByCategory(target);
			verify(categoryRepository, never()).delete(target);
		}

		@Test
		@DisplayName("실패: 하위 카테고리에 BookCategory 존재 시 예외")
		void throwsWhenBookExistsOnSubCategory() {
			// given: parent-child 세팅
			target = Category.builder()
				.name("삭제 대상")
				.parentCategory(root)
				.build();
			ReflectionTestUtils.setField(target, "id", 3L);

			Category targetChild = Category.builder()
				.name("삭제 대상 하위 카테고리")
				.parentCategory(target)
				.build();
			ReflectionTestUtils.setField(targetChild, "id", 4L);
			target.addChildCategory(targetChild);

			when(categoryRepository.findById(target.getId())).thenReturn(Optional.of(target));
			when(bookCategoryRepository.existsByCategory(target)).thenReturn(false);
			when(categoryRepository.getAllDescendantCategories(any())).thenReturn(List.of(targetChild));
			when(bookCategoryRepository.existsByCategory(targetChild)).thenReturn(true);

			// when / then
			assertThatThrownBy(() -> categoryService.deleteCategory(target.getId()))
				.isInstanceOf(CategoryCannotDeleteRootException.class)
				.hasMessageContaining("(도서가 등록된 하위 카테고리 존재시 삭제 불가)");
			verify(bookCategoryRepository, times(1)).existsByCategory(targetChild);
			verify(categoryRepository, never()).delete(target);
		}

		@Test
		@DisplayName("실패: 루트 카테고리에 단 하나의 하위 카테고리만 존재할 경우 삭제 불가")
		void failsToDelete_whenOnlyOneSubCategoryUnderRoot() {
			when(categoryRepository.findById(child.getId())).thenReturn(Optional.of(child));
			when(bookCategoryRepository.existsByCategory(child)).thenReturn(false);
			when(categoryRepository.getAllDescendantCategories(child)).thenReturn(List.of());
			when(categoryRepository.existsByIdAndParentCategoryIsNull(child.getParentCategory().getId())).thenReturn(true);
			when(categoryRepository.findById(root.getId())).thenReturn(Optional.of(root));
			// when / then
			assertThatThrownBy(() -> categoryService.deleteCategory(child.getId()))
				.isInstanceOf(CategoryCannotDeleteException.class)
				.hasMessageContaining("(카테고리는 최소 2단계 카테고리 유지)");
			verify(categoryRepository, never()).delete(target);
		}


		@Test
		@DisplayName("실패: 해당 카테고리 찾을 수 없음")
		void throwsWhenCategoryNotFound() {
			when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

			assertThatThrownBy(() -> categoryService.deleteCategory(99L))
				.isInstanceOf(CategoryNotFoundException.class);
		}
	}

	@Nested
	@DisplayName("카테고리 조회")
	class PageQueryTests {

		@Test
		@DisplayName("getCategories → Pageable 매핑 후 CategoryResponse 반환")
		void getCategories_success() {
			// given
			Category parent = Category.builder().name("Parent").build();
			ReflectionTestUtils.setField(parent, "id", 1L);
			ReflectionTestUtils.setField(parent, "categoryPath", "/1");

			Category child = Category.builder()
				.name("Child")
				.parentCategory(parent)
				.build();
			ReflectionTestUtils.setField(child, "id", 2L);
			ReflectionTestUtils.setField(child, "categoryPath", "/1/2");

			Pageable pageable = PageRequest.of(0, 10);
			Page<Category> page = new PageImpl<>(List.of(parent, child), pageable, 2);
			given(categoryRepository.findAll(pageable)).willReturn(page);

			// when
			Page<CategoryResponse> result = categoryService.getCategories(pageable);

			// then
			assertThat(result.getTotalElements()).isEqualTo(2);
			List<CategoryResponse> list = result.getContent();
			assertThat(list).extracting("categoryId", "name", "parentName", "categoryPath")
				.containsExactly(
					tuple(1L, "Parent", "-", "/1"),
					tuple(2L, "Child", "Parent", "/1/2")
				);
		}

		@Test
		@DisplayName("searchCategories → 검색어+Pageable 매핑 후 CategoryResponse 반환")
		void searchCategories_success() {
			// given
			Category category = Category.builder()
				.name("JAVA")
				.build();
			ReflectionTestUtils.setField(category, "id", 5L);
			ReflectionTestUtils.setField(category, "categoryPath", "/5");
			Pageable pageable = PageRequest.of(0, 5);

			Category categoryChild = Category.builder()
				.parentCategory(category)
				.name("java")
				.build();
			ReflectionTestUtils.setField(categoryChild, "id", 6L);
			ReflectionTestUtils.setField(categoryChild, "categoryPath", "/6");

			Page<Category> page = new PageImpl<>(List.of(category, categoryChild), pageable, 1);
			given(categoryRepository.searchByNameContaining("ja", pageable)).willReturn(page);

			// when
			Page<CategoryResponse> result = categoryService.searchCategories("ja", pageable);

			// then
			assertThat(result.getTotalElements()).isEqualTo(2);
			CategoryResponse resp = result.getContent().getFirst();
			assertThat(resp.categoryId()).isEqualTo(5L);
			assertThat(resp.name()).isEqualTo("JAVA");
			assertThat(resp.parentName()).isEqualTo("-");
			assertThat(resp.categoryPath()).isEqualTo("/5");

			CategoryResponse resp2 = result.getContent().getLast();
			assertThat(resp2.categoryId()).isEqualTo(6L);
			assertThat(resp2.name()).isEqualTo("java");
			assertThat(resp2.parentName()).isEqualTo("JAVA");
			assertThat(resp2.categoryPath()).isEqualTo("/6");
		}

		@Test
		@DisplayName("성공: 부모 카테고리가 존재할 경우")
		void successWithParent() {
			// given
			Category parent = Category.builder()
				.name("부모")
				.build();
			ReflectionTestUtils.setField(parent, "id", 1L);

			Category child = Category.builder()
				.name("자식")
				.parentCategory(parent)
				.build();
			ReflectionTestUtils.setField(child, "categoryPath", "/1/2");
			ReflectionTestUtils.setField(child, "id", 2L);

			when(categoryRepository.findByName("자식")).thenReturn(child);

			// when
			CategoryResponse response = categoryService.getCategoryByName("자식");

			// then
			assertThat(response.categoryId()).isEqualTo(2L);
			assertThat(response.name()).isEqualTo("자식");
			assertThat(response.parentName()).isEqualTo("부모");
			assertThat(response.categoryPath()).isEqualTo("/1/2");
		}

		@Test
		@DisplayName("성공: 부모 카테고리가 없을 경우 (최상위)")
		void successWithoutParent() {
			// given
			Category root = Category.builder()
				.name("루트")
				.parentCategory(null)
				.build();
			ReflectionTestUtils.setField(root, "id", 1L);
			ReflectionTestUtils.setField(root, "categoryPath", "/1");

			when(categoryRepository.findByName("루트")).thenReturn(root);

			// when
			CategoryResponse response = categoryService.getCategoryByName("루트");

			// then
			assertThat(response.categoryId()).isEqualTo(1L);
			assertThat(response.name()).isEqualTo("루트");
			assertThat(response.parentName()).isEqualTo("-");
			assertThat(response.categoryPath()).isEqualTo("/1");
		}

		@Test
		@DisplayName("성공: 부모 카테고리가 존재할 경우")
		void successGetCategoryByIdWithParent() {
			// given
			Category parent = Category.builder()
				.name("부모")
				.build();
			ReflectionTestUtils.setField(parent, "id", 1L);

			Category child = Category.builder()
				.name("자식")
				.parentCategory(parent)
				.build();
			ReflectionTestUtils.setField(child, "categoryPath", "/1/2");
			ReflectionTestUtils.setField(child, "id", 2L);
			parent.addChildCategory(child);

			when(categoryRepository.findById(child.getId())).thenReturn(Optional.of(child));

			// when
			CategoryResponse response = categoryService.getCategory(child.getId());

			// then
			assertThat(response.categoryId()).isEqualTo(2L);
			assertThat(response.name()).isEqualTo("자식");
			assertThat(response.parentName()).isEqualTo("부모");
			assertThat(response.categoryPath()).isEqualTo("/1/2");
		}

		@Test
		@DisplayName("성공: 부모 카테고리가 존재 하지 않을 경우")
		void successGetCategoryByIdWithoutParent() {
			// given
			Category root = Category.builder()
				.name("루트")
				.parentCategory(null)
				.build();
			ReflectionTestUtils.setField(root, "id", 1L);
			ReflectionTestUtils.setField(root, "categoryPath", "/1");

			when(categoryRepository.findById(root.getId())).thenReturn(Optional.of(root));

			// when
			CategoryResponse response = categoryService.getCategory(root.getId());

			// then
			assertThat(response.categoryId()).isEqualTo(1L);
			assertThat(response.name()).isEqualTo("루트");
			assertThat(response.parentName()).isEqualTo("-");
			assertThat(response.categoryPath()).isEqualTo("/1");
		}

		@Test
		@DisplayName("성공: 카테고리 데이터가 없을 경우 빈 리스트 반환")
		void successWhenNoCategory() {
			when(categoryRepository.findAll()).thenReturn(List.of());

			List<CategoryTreeResponse> result = categoryService.getCategoryTree();

			assertThat(result).isEmpty();
		}

		@Test
		@DisplayName("성공: 최상위 카테고리만 있을 경우 단일 노드 트리 반환")
		void successWhenOnlyRoot() {
			Category root = Category.builder().name("루트").build();
			ReflectionTestUtils.setField(root, "id", 1L);

			when(categoryRepository.findAll()).thenReturn(List.of(root));

			List<CategoryTreeResponse> result = categoryService.getCategoryTree();

			assertThat(result).hasSize(1);
			assertThat(result.get(0).name()).isEqualTo("루트");
			assertThat(result.get(0).children()).isEmpty();
		}

		@Test
		@DisplayName("성공: 루트와 자식 1단계가 있는 경우 트리 반환")
		void successWhenRootWithChild() {
			Category root = Category.builder().name("루트").build();
			ReflectionTestUtils.setField(root, "id", 1L);

			Category child = Category.builder().name("자식").parentCategory(root).build();
			ReflectionTestUtils.setField(child, "id", 2L);

			when(categoryRepository.findAll()).thenReturn(List.of(root, child));

			List<CategoryTreeResponse> result = categoryService.getCategoryTree();

			assertThat(result).hasSize(1);
			CategoryTreeResponse rootNode = result.get(0);
			assertThat(rootNode.name()).isEqualTo("루트");
			assertThat(rootNode.children()).hasSize(1);
			assertThat(rootNode.children().get(0).name()).isEqualTo("자식");
		}

		@Test
		@DisplayName("성공: 루트-자식-손자 3단계 트리 구성")
		void successWithThreeLevelTree() {
			Category root = Category.builder().name("루트").build();
			ReflectionTestUtils.setField(root, "id", 1L);

			Category child = Category.builder().name("자식").parentCategory(root).build();
			ReflectionTestUtils.setField(child, "id", 2L);

			Category grandchild = Category.builder().name("손자").parentCategory(child).build();
			ReflectionTestUtils.setField(grandchild, "id", 3L);

			when(categoryRepository.findAll()).thenReturn(List.of(root, child, grandchild));

			List<CategoryTreeResponse> result = categoryService.getCategoryTree();

			assertThat(result).hasSize(1);
			assertThat(result.get(0).children().get(0).children().get(0).name()).isEqualTo("손자");
		}

		@Test
		@DisplayName("성공: 하나의 루트가 여러 자식을 가질 때 sibling도 포함")
		void successWithMultipleChildren() {
			Category root = Category.builder().name("루트").build();
			ReflectionTestUtils.setField(root, "id", 1L);

			Category child1 = Category.builder().name("자식1").parentCategory(root).build();
			ReflectionTestUtils.setField(child1, "id", 2L);

			Category child2 = Category.builder().name("자식2").parentCategory(root).build();
			ReflectionTestUtils.setField(child2, "id", 3L);

			when(categoryRepository.findAll()).thenReturn(List.of(root, child1, child2));

			List<CategoryTreeResponse> result = categoryService.getCategoryTree();

			assertThat(result).hasSize(1);
			assertThat(result.get(0).children()).hasSize(2);
			assertThat(result.get(0).children())
				.extracting(CategoryTreeResponse::name)
				.containsExactlyInAnyOrder("자식1", "자식2");
		}
	}
}
