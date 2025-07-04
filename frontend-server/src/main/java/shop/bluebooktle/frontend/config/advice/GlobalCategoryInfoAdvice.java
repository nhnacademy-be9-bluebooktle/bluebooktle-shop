package shop.bluebooktle.frontend.config.advice;

import java.util.List;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import shop.bluebooktle.common.dto.book.response.CategoryTreeResponse;
import shop.bluebooktle.frontend.service.CategoryService;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalCategoryInfoAdvice {

	private final CategoryService categoryService;

	private static final String CATEGORY_TREE = "categoryTree";

	@ModelAttribute
	public void addCategoryInfoToModel(Model model, HttpServletRequest request) {

		String uri = request.getRequestURI();
		if (uri.startsWith("/admin")) {
			return; // 관리자 페이지는 제외
		}

		List<CategoryTreeResponse> tree = categoryService.getCategoryTreeCached(); // 캐싱 작업
		model.addAttribute(CATEGORY_TREE, tree);
	}
}
