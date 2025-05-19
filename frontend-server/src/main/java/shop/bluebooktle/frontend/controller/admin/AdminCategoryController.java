package shop.bluebooktle.frontend.controller.admin;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

	@Getter
	@Setter
	@ToString
	static class CategoryDto {
		private Long id;
		private String name;
		private Long parentId;
		private String parentName;
		private String categoryPath;
		private int depth;
		private List<CategoryDto> children;
		private LocalDateTime createdAt;
		private LocalDateTime deletedAt;

		public boolean isActive() {
			return this.deletedAt == null;
		}

		public CategoryDto() {
			this.children = new ArrayList<>();
			this.createdAt = LocalDateTime.now();
			// ID는 이 생성자에서 설정하지 않음. 외부에서 반드시 설정해야 함.
			// ID가 null인 상태로 리스트에 추가되거나 정렬되면 안됨.
		}

		public CategoryDto(Long id, String name, Long parentId, String categoryPath, LocalDateTime createdAt,
			LocalDateTime deletedAt) {
			this();
			if (id == null) {
				// static 블록에서 데모 데이터 생성 시 ID 누락은 심각한 오류임.
				log.error("CRITICAL: CategoryDto created with NULL ID. Name: '{}', Path: '{}'", name, categoryPath);
				throw new IllegalArgumentException("Category ID cannot be null for demo data initialization.");
			}
			this.id = id;
			this.name = name;
			this.parentId = parentId;
			this.categoryPath = categoryPath;
			this.createdAt = (createdAt != null) ? createdAt : LocalDateTime.now();
			this.deletedAt = deletedAt;
		}

		// ID가 null이 아니라고 가정하고 equals/hashCode 구현 (Lombok이 생성하는 것과 유사)
		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;
			CategoryDto that = (CategoryDto)o;
			return id.equals(that.id); // id는 null이 아니어야 함을 전제
		}

		@Override
		public int hashCode() {
			return id.hashCode(); // id는 null이 아니어야 함을 전제
		}
	}

	private static final List<CategoryDto> allCategoriesForDemo = new ArrayList<>();
	// private static final AtomicLong demoIdCounter = new AtomicLong(100); // saveCategory에서 사용

	private static void flattenCategories(CategoryDto category, List<CategoryDto> flatList, int currentDepth,
		String currentParentName) {
		if (category == null) { // category 객체 자체가 null인 경우 방지
			log.warn("Flatten: Attempted to flatten a null category object.");
			return;
		}
		if (category.getId() == null) { // ID가 없는 DTO는 flatten 과정에 포함시키지 않음.
			log.warn("Flatten: Category ID is null for name '{}', skipping from flatten.", category.getName());
			return; // ID가 null이면 리스트에 추가하지 않고 건너뜀
		}

		category.setDepth(currentDepth);
		category.setParentName(currentParentName);

		// ID 기준으로 중복 추가 방지
		Long currentCatId = category.getId();
		if (flatList.stream().noneMatch(c -> c.getId() != null && c.getId().equals(currentCatId))) {
			flatList.add(category);
		} else {
			log.warn("Flatten: Category with ID {} already exists in flatList. Skipping duplicate.", currentCatId);
		}

		if (category.getChildren() != null && !category.getChildren().isEmpty()) {
			for (CategoryDto child : category.getChildren()) {
				if (child != null && child.getId() != null) { // 자식 객체 및 ID null 체크
					flattenCategories(child, flatList, currentDepth + 1, category.getName());
				} else {
					log.warn("Flatten: Child category or its ID is null. Parent: {}", category.getName());
				}
			}
		}
	}

	static {
		log.info("AdminCategoryController static block: Initializing demo data START");
		try {
			// 1. 원본 계층 구조 데이터 생성 (ID는 항상 값을 가져야 함)
			CategoryDto cat1 = new CategoryDto(1L, "국내도서", null, "/1", LocalDateTime.now().minusDays(10), null);
			CategoryDto cat1_1 = new CategoryDto(2L, "소설", 1L, "/1/2", LocalDateTime.now().minusDays(9), null);
			CategoryDto cat1_2 = new CategoryDto(3L, "경제/경영", 1L, "/1/3", LocalDateTime.now().minusDays(8), null);
			CategoryDto cat1_1_1 = new CategoryDto(6L, "현대소설", 2L, "/1/2/6", LocalDateTime.now().minusDays(7), null);

			cat1_1.getChildren().add(cat1_1_1);
			cat1.getChildren().add(cat1_1);
			cat1.getChildren().add(cat1_2);

			CategoryDto cat2 = new CategoryDto(4L, "해외도서", null, "/4", LocalDateTime.now().minusDays(6), null);
			CategoryDto cat3 = new CategoryDto(5L, "컴퓨터/IT", null, "/5", LocalDateTime.now().minusDays(5),
				LocalDateTime.now().minusDays(1));

			List<CategoryDto> hierarchicalRoots = Arrays.asList(cat1, cat2, cat3);

			for (CategoryDto rootCategory : hierarchicalRoots) {
				if (rootCategory != null && rootCategory.getId() != null) {
					flattenCategories(rootCategory, allCategoriesForDemo, 0, null);
				} else {
					log.error("CRITICAL in static init: A root category or its ID is null. Root: {}", rootCategory);
				}
			}

			// 3. 추가 플랫 데모 데이터 (ID는 항상 값을 가져야 함)
			for (long idVal = 7; idVal <= 30; idVal++) {
				Long currentId = idVal;
				Long parentDemoId = (idVal % 4 == 0 ? 1L : (idVal % 4 == 1 ? 2L : (idVal % 4 == 2 ? 4L : 1)));
				String tempCategoryPath = "/temp/" + currentId; // 초기 경로, 부모 설정 시 업데이트

				CategoryDto newCat = new CategoryDto(
					currentId, "추가 카테고리 " + currentId,
					parentDemoId,
					tempCategoryPath,
					LocalDateTime.now().minusDays(currentId),
					(currentId % 10 == 0) ? LocalDateTime.now().minusDays(currentId / 2) : null
				);

				String tempParentName = null;
				int tempDepth = 0;
				final Long finalParentDemoId = parentDemoId;
				Optional<CategoryDto> parentOpt = allCategoriesForDemo.stream()
					.filter(p -> p.getId() != null && p.getId().equals(finalParentDemoId))
					.findFirst();
				if (parentOpt.isPresent()) {
					CategoryDto p = parentOpt.get();
					tempParentName = p.getName();
					tempDepth = p.getDepth() + 1;
					newCat.setCategoryPath(
						(p.getCategoryPath() == null || p.getCategoryPath().equals("/") ? "" : p.getCategoryPath())
							+ "/" + newCat.getId());
				} else {
					newCat.setCategoryPath("/" + newCat.getId());
					log.warn("Parent with ID {} not found for demo category ID {}. Setting as top-level path.",
						parentDemoId, currentId);
				}
				newCat.setParentName(tempParentName);
				newCat.setDepth(tempDepth);

				if (newCat.getId() == null) { // 최종 방어
					log.error("CRITICAL: Demo category ID is null before adding to allCategoriesForDemo. Name: {}",
						newCat.getName());
					// 이 경우 리스트에 추가하지 않거나, 임시 ID라도 부여해야 함
					// newCat.setId(demoIdCounter.incrementAndGet()); // 예시: 임시 ID 부여
					continue; // ID가 null이면 추가하지 않음
				}
				allCategoriesForDemo.add(newCat);
			}

			// 정렬 전에 ID가 null인 객체가 있는지 한 번 더 철저히 확인
			long nullIdCount = allCategoriesForDemo.stream().filter(c -> c.getId() == null).count();
			if (nullIdCount > 0) {
				log.error("CRITICAL: {} CategoryDto(s) with null ID found in allCategoriesForDemo BEFORE sorting!",
					nullIdCount);
				allCategoriesForDemo.stream()
					.filter(c -> c.getId() == null)
					.forEach(c -> log.error("Category with null ID: {}", c));
				// 개발 중에는 여기서 예외를 발생시켜 문제를 즉시 인지하도록 하는 것이 좋음
				throw new IllegalStateException(
					"Found CategoryDto with null ID in demo data. Cannot proceed with sorting.");
			}

			// 이 부분이 146번째 줄이라면, 위에서 ID가 null인 객체가 없도록 보장되어야 함.
			if (!allCategoriesForDemo.isEmpty()) {
				// 모든 CategoryDto 객체의 ID가 null이 아님을 가정하고 정렬
				allCategoriesForDemo.sort(Comparator.comparing(CategoryDto::getId));
				log.info(
					"AdminCategoryController static block: Demo data sorted. Total categories after processing: {}",
					allCategoriesForDemo.size());
			} else {
				log.warn("AdminCategoryController static block: allCategoriesForDemo is empty after all processing.");
			}
			log.info("AdminCategoryController static block: Initializing demo data END. Final size: {}",
				allCategoriesForDemo.size());

		} catch (Throwable t) {
			log.error("CRITICAL ERROR during static initialization of AdminCategoryController", t);
			throw new ExceptionInInitializerError(t); // 원인 예외를 포함하여 다시 던짐
		}
	}

	// Controller 메소드들은 이전 답변의 수정된 내용 유지
	// ... (이하 코드 생략, 이전 답변 내용 참고) ...
	// listCategories, categoryForm, saveCategory, deleteCategory, activateCategory 메소드들은
	// currentPageZeroBased 변수명을 사용하고, CategoryDto의 id가 null이 아님을 전제로 동작하도록 유지.

	@GetMapping
	public String listCategories(Model model, HttpServletRequest request,
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "10") int size,
		@RequestParam(value = "searchKeyword", required = false) String searchKeyword,
		@RequestParam(value = "statusFilter", required = false) String statusFilter) {
		log.info("AdminCategoryController - listCategories: page={}, size={}, searchKeyword={}, statusFilter={}", page,
			size, searchKeyword, statusFilter);
		model.addAttribute("pageTitle", "카테고리 관리");
		model.addAttribute("currentURI", request.getRequestURI());

		List<CategoryDto> finalCategoriesToList;
		// static 블록에서 예외 발생 시 allCategoriesForDemo가 비어있거나 초기화 안될 수 있음
		if (allCategoriesForDemo == null || allCategoriesForDemo.isEmpty()) {
			log.warn("allCategoriesForDemo is null or empty in listCategories. Returning empty list.");
			finalCategoriesToList = Collections.emptyList();
		} else {
			finalCategoriesToList = allCategoriesForDemo.stream()
				.filter(cat -> cat.getId() != null)
				.filter(cat -> (searchKeyword == null || searchKeyword.trim().isEmpty()) ||
					(cat.getName() != null && cat.getName().toLowerCase().contains(searchKeyword.toLowerCase())) ||
					(cat.getCategoryPath() != null && cat.getCategoryPath().contains(searchKeyword))
				)
				.filter(cat -> (statusFilter == null || statusFilter.isEmpty()) ||
					("active".equals(statusFilter) && cat.isActive()) ||
					("inactive".equals(statusFilter) && !cat.isActive())
				)
				.collect(Collectors.toList());
		}

		Sort sort = Sort.by(Sort.Direction.ASC, "id");
		Pageable pageable = PageRequest.of(page, size, sort);

		int start = (int)pageable.getOffset();
		int end = Math.min((start + pageable.getPageSize()), finalCategoriesToList.size());
		List<CategoryDto> pageContent = (start >= finalCategoriesToList.size() || start > end) ? List.of() :
			finalCategoriesToList.subList(start, end);
		Page<CategoryDto> categoriesPage = new PageImpl<>(pageContent, pageable, finalCategoriesToList.size());

		model.addAttribute("categories", categoriesPage.getContent());
		model.addAttribute("currentPage", categoriesPage.getNumber());
		model.addAttribute("totalPages", categoriesPage.getTotalPages());
		model.addAttribute("currentSize", categoriesPage.getSize());
		model.addAttribute("totalElements", categoriesPage.getTotalElements());

		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath(request.getRequestURI())
			.queryParam("size", categoriesPage.getSize());
		if (searchKeyword != null && !searchKeyword.isEmpty())
			uriBuilder.queryParam("searchKeyword", searchKeyword);
		if (statusFilter != null && !statusFilter.isEmpty())
			uriBuilder.queryParam("statusFilter", statusFilter);

		String baseUrlWithParams = uriBuilder.toUriString();
		model.addAttribute("baseUrlWithParams", baseUrlWithParams);

		model.addAttribute("searchKeyword", searchKeyword);
		model.addAttribute("statusFilter", statusFilter);

		return "admin/category/category_list";
	}

	// categoryForm, saveCategory 등 나머지 메소드는 이전과 유사하게 유지하되,
	// CategoryDto의 id가 null일 수 있는 경우 (신규 등록 폼) 와 null이 아니어야 하는 경우 (수정 폼)를
	// 명확히 구분하고, ID가 없는 객체를 demo 리스트에 추가하지 않도록 주의합니다.
	// saveCategory에서 신규 등록 시 ID를 AtomicLong 등으로 생성하여 부여하는 것이 안전합니다.

	@GetMapping({"/new", "/{categoryId}/edit"})
	public String categoryForm(@PathVariable(value = "categoryId", required = false) Long categoryId,
		Model model, HttpServletRequest request) {
		log.info("어드민 카테고리 폼 페이지 요청. URI: {}, categoryId: {}", request.getRequestURI(), categoryId);
		model.addAttribute("currentURI", request.getRequestURI());

		CategoryDto categoryDto;
		String pageTitle;

		if (categoryId != null) {
			pageTitle = "카테고리 수정 (ID: " + categoryId + ")";
			final Long finalCategoryId = categoryId;
			categoryDto = allCategoriesForDemo.stream()
				.filter(c -> c.getId() != null && c.getId().equals(finalCategoryId)) // ID null 체크 추가
				.findFirst()
				.map(
					c -> new CategoryDto(c.getId(), c.getName(), c.getParentId(), c.getCategoryPath(), c.getCreatedAt(),
						c.getDeletedAt()))
				.orElseGet(() -> {
					CategoryDto dto = new CategoryDto(); // ID가 없는 상태로 생성
					dto.setId(finalCategoryId); // PathVariable ID 설정
					dto.setName("ID " + finalCategoryId + " 카테고리 없음"); // 임시 이름
					log.warn("Category with ID {} not found. Creating a placeholder DTO.", finalCategoryId);
					return dto;
				});
		} else {
			pageTitle = "새 카테고리 등록";
			categoryDto = new CategoryDto(); // ID가 null인 새 객체
			// categoryDto.setId(demoIdCounter.incrementAndGet()); // 신규 등록 시 임시 ID (선택적)
		}
		model.addAttribute("pageTitle", pageTitle);
		model.addAttribute("category", categoryDto);

		List<CategoryDto> parentCategoryOptions = new ArrayList<>();
		CategoryDto topLevelOption = new CategoryDto();
		topLevelOption.setId(null);
		topLevelOption.setName("== 최상위 카테고리 ==");
		parentCategoryOptions.add(topLevelOption);

		final String currentCategoryPath = categoryDto.getCategoryPath();
		final Long currentId = categoryDto.getId(); // 수정 시 null이 아니어야 함

		allCategoriesForDemo.stream()
			.filter(CategoryDto::isActive)
			.filter(cat -> cat.getId() != null) // ID null 체크
			.filter(cat -> !Objects.equals(cat.getId(), currentId))
			.filter(cat -> {
				if (currentId == null || currentCategoryPath == null || cat.getCategoryPath() == null)
					return true;
				if (cat.getCategoryPath().equals(currentCategoryPath) && Objects.equals(cat.getId(), currentId))
					return false;
				return !cat.getCategoryPath().startsWith(currentCategoryPath + "/");
			})
			.sorted(Comparator.comparing(CategoryDto::getCategoryPath, Comparator.nullsLast(String::compareTo)))
			.forEach(parentCategoryOptions::add);

		model.addAttribute("parentCategories", parentCategoryOptions);
		return "admin/category/category_form";
	}

	private static final AtomicLong categoryDemoIdGenerator = new AtomicLong(100); // CategoryDto 임시 ID 생성용

	@PostMapping("/save")
	public String saveCategory(@ModelAttribute("category") CategoryDto categoryDto,
		BindingResult bindingResult,
		RedirectAttributes redirectAttributes,
		HttpServletRequest request) {
		log.info("카테고리 저장 요청: {}", categoryDto);

		if (categoryDto.getName() == null || categoryDto.getName().trim().isEmpty()) {
			bindingResult.rejectValue("name", "NotEmpty", "카테고리 이름은 필수입니다.");
		}

		if (bindingResult.hasErrors()) {
			log.warn("카테고리 저장 폼 유효성 검증 에러: {}", bindingResult.getAllErrors());
			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.category",
				bindingResult);
			redirectAttributes.addFlashAttribute("category", categoryDto);
			redirectAttributes.addFlashAttribute("globalErrorMessage", "입력값을 확인해주세요.");
			if (categoryDto.getId() != null) {
				return "redirect:/admin/categories/" + categoryDto.getId() + "/edit";
			} else {
				return "redirect:/admin/categories/new";
			}
		}

		try {
			String action;
			boolean isNew = categoryDto.getId() == null || allCategoriesForDemo.stream()
				.noneMatch(c -> Objects.equals(c.getId(), categoryDto.getId()));

			if (isNew) {
				action = "등록";
				Long newId = categoryDemoIdGenerator.incrementAndGet(); // 새 ID 생성
				categoryDto.setId(newId);
				categoryDto.setCreatedAt(LocalDateTime.now());
				categoryDto.setDeletedAt(null);

				if (categoryDto.getParentId() != null) {
					final Long pId = categoryDto.getParentId();
					allCategoriesForDemo.stream()
						.filter(c -> c.getId() != null && c.getId().equals(pId))
						.findFirst()
						.ifPresent(pCat -> {
							categoryDto.setParentName(pCat.getName());
							categoryDto.setDepth(pCat.getDepth() + 1);
							categoryDto.setCategoryPath(
								(pCat.getCategoryPath() == null || pCat.getCategoryPath().equals("/") ? "" :
									pCat.getCategoryPath()) + "/" + categoryDto.getId());
						});
					if (categoryDto.getParentName() == null && pId != null) {
						categoryDto.setParentId(null);
						categoryDto.setDepth(0);
						categoryDto.setCategoryPath("/" + categoryDto.getId());
					}
				} else {
					categoryDto.setDepth(0);
					categoryDto.setCategoryPath("/" + categoryDto.getId());
				}
				// 추가 전에 ID가 할당되었는지 최종 확인
				if (categoryDto.getId() == null) {
					log.error("CRITICAL: Category ID is still null before adding new category. Name: {}",
						categoryDto.getName());
					throw new IllegalStateException("Failed to assign ID to new category.");
				}
				allCategoriesForDemo.add(categoryDto);
			} else {
				action = "수정";
				final Long idToUpdate = categoryDto.getId();
				allCategoriesForDemo.stream()
					.filter(c -> Objects.equals(c.getId(), idToUpdate))
					.findFirst()
					.ifPresent(existingCat -> {
						existingCat.setName(categoryDto.getName());
						Long oldParentId = existingCat.getParentId();
						existingCat.setParentId(categoryDto.getParentId());
						if (!Objects.equals(oldParentId, categoryDto.getParentId())) {
							if (categoryDto.getParentId() != null) {
								final Long pId = categoryDto.getParentId();
								Optional<CategoryDto> parentOpt = allCategoriesForDemo.stream()
									.filter(c -> c.getId() != null && c.getId().equals(pId))
									.findFirst();
								if (parentOpt.isPresent()) {
									CategoryDto pCat = parentOpt.get();
									existingCat.setParentName(pCat.getName());
									existingCat.setDepth(pCat.getDepth() + 1);
									existingCat.setCategoryPath(
										(pCat.getCategoryPath() == null || pCat.getCategoryPath().equals("/") ? "" :
											pCat.getCategoryPath()) + "/" + existingCat.getId());
								} else {
									existingCat.setParentId(oldParentId);
								}
							} else {
								existingCat.setParentName(null);
								existingCat.setDepth(0);
								existingCat.setCategoryPath("/" + existingCat.getId());
							}
						}
					});
			}
			allCategoriesForDemo.sort(Comparator.comparing(CategoryDto::getId));

			log.info("카테고리 {} 처리: {}", action, categoryDto.getName());
			redirectAttributes.addFlashAttribute("globalSuccessMessage",
				"카테고리 '" + categoryDto.getName() + "'가 성공적으로 " + action + "되었습니다.");
		} catch (Exception e) {
			log.error("카테고리 저장 중 오류 발생", e);
			redirectAttributes.addFlashAttribute("globalErrorMessage", "카테고리 저장 중 오류가 발생했습니다: " + e.getMessage());
			redirectAttributes.addFlashAttribute("category", categoryDto);
			if (categoryDto.getId() != null) {
				return "redirect:/admin/categories/" + categoryDto.getId() + "/edit";
			} else {
				return "redirect:/admin/categories/new";
			}
		}
		return "redirect:/admin/categories";
	}

	@PostMapping("/{categoryId}/delete")
	public String deleteCategory(@PathVariable Long categoryId, RedirectAttributes redirectAttributes) {
		log.info("카테고리 비활성화 요청: ID {}", categoryId);
		try {
			final Long finalCategoryId = categoryId;
			allCategoriesForDemo.stream()
				.filter(c -> c.getId() != null && c.getId().equals(finalCategoryId))
				.findFirst()
				.ifPresent(c -> c.setDeletedAt(LocalDateTime.now()));
			redirectAttributes.addFlashAttribute("globalSuccessMessage", "카테고리(ID: " + categoryId + ")가 비활성화되었습니다.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("globalErrorMessage", "카테고리 비활성화 중 오류 발생: " + e.getMessage());
		}
		return "redirect:/admin/categories";
	}

	@PostMapping("/{categoryId}/activate")
	public String activateCategory(@PathVariable Long categoryId, RedirectAttributes redirectAttributes) {
		log.info("카테고리 활성화 요청: ID {}", categoryId);
		try {
			final Long finalCategoryId = categoryId;
			allCategoriesForDemo.stream()
				.filter(c -> c.getId() != null && c.getId().equals(finalCategoryId))
				.findFirst()
				.ifPresent(c -> c.setDeletedAt(null));
			redirectAttributes.addFlashAttribute("globalSuccessMessage", "카테고리(ID: " + categoryId + ")가 활성화되었습니다.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("globalErrorMessage", "카테고리 활성화 중 오류 발생: " + e.getMessage());
		}
		return "redirect:/admin/categories";
	}
}