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
		private Category parent;
		private Category child;
		private Category target;

		@BeforeEach
		void setUpUpdate() {
			// 부모 카테고리 준비
			parent = Category.builder()
				.name("상위 카테고리")
				.build();
			ReflectionTestUtils.setField(parent, "id", 1L);

			// 수정 대상 카테고리 준비(detail)
			target = Category.builder()
				.name("기존 이름")
				.parentCategory(parent)
				.build();
			ReflectionTestUtils.setField(target, "id", 2L);

			// 기존 부모에 등록된 자식 카테고리
			child = Category.builder()
				.name("중복 이름")
				.build();
			ReflectionTestUtils.setField(parent, "id", 3L);
		}

		@Test
		@DisplayName("성공: 이름 변경 후 save() 1회 호출")
		void success() {
			// repository.findById -> target
			when(categoryRepository.findById(2L))
				.thenReturn(Optional.of(target));

			CategoryUpdateRequest request = new CategoryUpdateRequest("새 이름");

			// save() stub
			when(categoryRepository.save(any(Category.class)))
				.thenAnswer(inv -> inv.getArgument(0));

			categoryService.updateCategory(2L, request);

			// save 호출 검증
			verify(categoryRepository, times(1)).save(target);
			// name 이 정상 변경되었는지 검증
			assertThat(target.getName()).isEqualTo("새 이름");
		}

		@Test
		@DisplayName("실패: 부모 카테고리의 하위 카테고리 중복 이름 예외")
		void duplicateNameThrows() {
			when(categoryRepository.findById(2L))
				.thenReturn(Optional.of(target));

			CategoryUpdateRequest request = new CategoryUpdateRequest("중복 이름");

			when(categoryRepository.getAllDescendantCategories(any()))
				.thenReturn(List.of(child));

			assertThatThrownBy(() -> categoryService.updateCategory(2L, request))
				.isInstanceOf(CategoryAlreadyExistsException.class)
				.hasMessageContaining("이미 존재하는 카테고리명입니다.");

			verify(categoryRepository, never()).save(any());
		}

		@Test
		@DisplayName("성공: 부모 카테고리의 하위 카테고리 중복 이름 예외")
		void successRootCategory() {
			when(categoryRepository.findById(2L))
				.thenReturn(Optional.of(target));

			CategoryUpdateRequest request = new CategoryUpdateRequest("중복 이름");

			when(categoryRepository.getAllDescendantCategories(any()))
				.thenReturn(List.of(child));

			assertThatThrownBy(() -> categoryService.updateCategory(2L, request))
				.isInstanceOf(CategoryAlreadyExistsException.class)
				.hasMessageContaining("이미 존재하는 카테고리명입니다.");

			verify(categoryRepository, never()).save(any());
		}

	}

	@Nested
	@DisplayName("카테고리 삭제")
	class DeleteCategoryTests {

		@Test
		@DisplayName("실패: 해당 카테고리에 BookCategory 존재 시 예외")
		void throwsWhenBookExistsOnCategory() {
			// given: parent-child 세팅
			Category parent = Category.builder().name("루트").build();
			ReflectionTestUtils.setField(parent, "id", 1L);
			Category child = Category.builder().name("삭제대상").parentCategory(parent).build();
			ReflectionTestUtils.setField(child, "id", 2L);
			parent.addChildCategory(child);

			when(categoryRepository.findById(2L)).thenReturn(Optional.of(child));
			when(bookCategoryRepository.existsByCategory(child)).thenReturn(true);

			// when / then
			assertThatThrownBy(() -> categoryService.deleteCategory(2L))
				.isInstanceOf(CategoryCannotDeleteRootException.class);

			verify(bookCategoryRepository).existsByCategory(child);
			verify(categoryRepository, never()).delete(child);
		}
	}

	@Nested
	@DisplayName("getCategories & searchCategories")
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
			Category cat = Category.builder().name("Java").build();
			ReflectionTestUtils.setField(cat, "id", 5L);
			ReflectionTestUtils.setField(cat, "categoryPath", "/5");
			Pageable pageable = PageRequest.of(0, 5);
			Page<Category> page = new PageImpl<>(List.of(cat), pageable, 1);
			given(categoryRepository.searchByNameContaining("ja", pageable)).willReturn(page);

			// when
			Page<CategoryResponse> result = categoryService.searchCategories("ja", pageable);

			// then
			assertThat(result.getTotalElements()).isEqualTo(1);
			CategoryResponse resp = result.getContent().getFirst();
			assertThat(resp.categoryId()).isEqualTo(5L);
			assertThat(resp.name()).isEqualTo("Java");
			assertThat(resp.parentName()).isEqualTo("-");
			assertThat(resp.categoryPath()).isEqualTo("/5");
		}
	}

}
