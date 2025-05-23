package shop.bluebooktle.frontend.controller.admin;

import java.util.List;

import org.springframework.data.domain.Page;
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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.common.dto.book.request.CategoryRegisterRequest;
import shop.bluebooktle.common.dto.book.request.CategoryUpdateRequest;
import shop.bluebooktle.common.dto.book.request.RootCategoryRegisterRequest;
import shop.bluebooktle.common.dto.book.request.category.CategoryInfoRequest;
import shop.bluebooktle.common.dto.book.request.category.CategoryRegisterFormRequest;
import shop.bluebooktle.common.dto.book.response.CategoryResponse;
import shop.bluebooktle.common.dto.book.response.CategoryTreeResponse;
import shop.bluebooktle.frontend.service.AdminCategoryService;

@Slf4j
@Controller
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

	private final AdminCategoryService adminCategoryService;

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

		// 카테고리 목록 보여주기
		Page<CategoryResponse> categoriesPage = adminCategoryService.getCategories(page, size, searchKeyword);
		log.info("pagenumber, totalPage, size, totalElemnts: {}, {}, {}, {}", categoriesPage.getNumber(),
			categoriesPage.getTotalPages(), categoriesPage.getSize(), categoriesPage.getTotalElements());
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

		// Tree 구조의 카테고리
		List<CategoryTreeResponse> tree = adminCategoryService.searchAllCategoriesTree();
		model.addAttribute("categoryTreeListJson", tree);
		return "admin/category/category_list";
	}

	@GetMapping({"/new"})
	public String categoryForm(Model model, HttpServletRequest request) {
		log.info("어드민 카테고리 폼 페이지 요청. URI: {}", request.getRequestURI());
		model.addAttribute("currentURI", request.getRequestURI());

		String pageTitle = "최상위 카테고리 등록";
		CategoryRegisterFormRequest form = new CategoryRegisterFormRequest();
		// 카테고리 목록 조회
		model.addAttribute("pageTitle", pageTitle);
		model.addAttribute("category", form);
		return "admin/category/root_category_form";
	}

	@GetMapping({"/{categoryId}/new"})
	public String categoryForm(@PathVariable(value = "categoryId", required = false) Long categoryId,
		Model model, HttpServletRequest request) {
		log.info("어드민 카테고리 폼 페이지 요청. URI: {}", request.getRequestURI());
		model.addAttribute("currentURI", request.getRequestURI());

		String pageTitle = "하위 카테고리 등록";
		CategoryRegisterFormRequest form = new CategoryRegisterFormRequest();
		form.setId(categoryId);
		model.addAttribute("pageTitle", pageTitle);
		model.addAttribute("category", form);
		return "admin/category/category_form";
	}

	@GetMapping({"/{categoryId}/edit"})
	public String categoryEditForm(@PathVariable(value = "categoryId", required = false) Long categoryId,
		Model model, HttpServletRequest request) {
		log.info("어드민 카테고리 수정 폼 페이지 요청. URI: {}, categoryId: {}", request.getRequestURI(), categoryId);
		model.addAttribute("currentURI", request.getRequestURI());
		String pageTitle;

		pageTitle = "카테고리 수정 (ID: " + categoryId + ")";
		// 카테고리 조회
		CategoryResponse category = adminCategoryService.getCategory(categoryId);

		model.addAttribute("pageTitle", pageTitle);
		model.addAttribute("category", category);

		return "admin/category/category_edit_form";
	}

	@PostMapping("/update")
	public String updateCategory(@Valid @ModelAttribute("category") CategoryInfoRequest categoryInfoRequest,
		BindingResult bindingResult,
		RedirectAttributes redirectAttributes,
		HttpServletRequest request) {
		if (bindingResult.hasErrors()) {
			log.warn("카테고리 저장 폼 유효성 검증 에러: {}", bindingResult.getAllErrors());
			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.category",
				bindingResult);
			redirectAttributes.addFlashAttribute("category", categoryInfoRequest);
			redirectAttributes.addFlashAttribute("globalErrorMessage", "입력값을 확인해주세요.");
			return "redirect:/admin/categories/" + categoryInfoRequest.categoryId() + "/edit";
		}
		try {
			adminCategoryService.updateCategory(categoryInfoRequest.categoryId(),
				new CategoryUpdateRequest(categoryInfoRequest.name()));
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("globalErrorMessage", "카테고리 수정 중 오류 발생: " + e.getMessage());
			return "redirect:/admin/categories/" + categoryInfoRequest.categoryId() + "/edit";
		}
		return "redirect:/admin/categories";
	}

	@PostMapping("/save")
	public String saveCategory(
		@Valid @ModelAttribute("category") RootCategoryRegisterRequest rootCategoryRegisterRequest,
		BindingResult bindingResult,
		RedirectAttributes redirectAttributes,
		HttpServletRequest request) {
		log.info("카테고리 저장 요청: {}", rootCategoryRegisterRequest);
		if (bindingResult.hasErrors()) {
			log.warn("카테고리 저장 폼 유효성 검증 에러: {}", bindingResult.getAllErrors());
			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.category",
				bindingResult);
			redirectAttributes.addFlashAttribute("category", rootCategoryRegisterRequest);
			redirectAttributes.addFlashAttribute("globalErrorMessage", "입력값을 확인해주세요.");
			return "redirect:/admin/categories/new";
		}
		try {
			adminCategoryService.addRootCategory(rootCategoryRegisterRequest);
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("globalErrorMessage", "카테고리 등록 중 오류 발생: " + e.getMessage());
			return "redirect:/admin/categories/new";
		}

		return "redirect:/admin/categories";
	}

	@PostMapping("/{categoryId}/save")
	public String saveCategory(
		@Valid @ModelAttribute("category") CategoryRegisterRequest categoryRegisterRequest,
		@PathVariable Long categoryId,
		BindingResult bindingResult,
		RedirectAttributes redirectAttributes,
		HttpServletRequest request) {
		log.info("카테고리 저장 요청: {}", categoryRegisterRequest);
		if (bindingResult.hasErrors()) {
			log.warn("카테고리 저장 폼 유효성 검증 에러: {}", bindingResult.getAllErrors());
			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.category",
				bindingResult);
			redirectAttributes.addFlashAttribute("category", categoryRegisterRequest);
			redirectAttributes.addFlashAttribute("globalErrorMessage", "입력값을 확인해주세요.");
			return "redirect:/admin/categories/" + categoryId + "/new";
		}
		try {
			adminCategoryService.addCategory(categoryId, categoryRegisterRequest);
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("globalErrorMessage", "카테고리 등록 중 오류 발생: " + e.getMessage());
			return "redirect:/admin/categories/" + categoryId + "/new";
		}

		return "redirect:/admin/categories";
	}

	@PostMapping("/{categoryId}/delete")
	public String deleteCategory(@PathVariable Long categoryId, RedirectAttributes redirectAttributes) {
		log.info("카테고리 비활성화 요청: ID {}", categoryId);
		try {
			adminCategoryService.deleteCategory(categoryId);
			redirectAttributes.addFlashAttribute("globalSuccessMessage", "카테고리(ID: " + categoryId + ")가 비활성화되었습니다.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("globalErrorMessage", "카테고리 비활성화 중 오류 발생: " + e.getMessage());
		}
		return "redirect:/admin/categories";
	}
}