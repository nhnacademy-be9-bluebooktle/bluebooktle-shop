package shop.bluebooktle.frontend.config.advice;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import shop.bluebooktle.common.dto.book.response.CategoryTreeResponse;
import shop.bluebooktle.frontend.service.AdminCategoryService;

@ControllerAdvice
public class GlobalCategoryInfoAdvice {
	
	private static final String CATEGORY_TREE = "categoryTree";

	@Autowired
	private AdminCategoryService adminCategoryService;

	@ModelAttribute
	public void addCategoryInfoToModel(Model model) {

		// TODO 레디스에 존재하지 않으면 DB에서 불러오고 레디스에 저장 후 레디스에서 조회
		// 레디스에 존재하면 레디스에서 조회
		List<CategoryTreeResponse> tree = adminCategoryService.searchAllCategoriesTree();
		model.addAttribute(CATEGORY_TREE, tree);
	}
}
