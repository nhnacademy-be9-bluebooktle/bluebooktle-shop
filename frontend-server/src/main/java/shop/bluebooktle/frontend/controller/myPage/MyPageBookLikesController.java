package shop.bluebooktle.frontend.controller.myPage;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.common.dto.book.response.BookLikesListResponse;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.frontend.service.MyPageBookLikesService;

@Controller
@RequestMapping("/mypage/likes")
@RequiredArgsConstructor
@Slf4j
public class MyPageBookLikesController {
	private final MyPageBookLikesService myPageBookLikesService;

	// 도서 좋아요 목록 조회
	@GetMapping
	public String listBookLikes(Model model,
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "10") int size) {
		log.info("도서 좋아요 목록 조회 경로");

		// 서비스 호출
		PaginationData<BookLikesListResponse> likesPage = myPageBookLikesService.getMyPageBookLikes(page, size);

		// 모델에 넣기
		model.addAttribute("likes", likesPage.getContent());
		model.addAttribute("pagination", likesPage.getPagination());
		return "mypage/likes_list";
	}

	// 도서 좋아요 삭제
	@PostMapping("/{bookId}/delete")
	public String deleteBookLikes(
		@PathVariable Long bookId,
		RedirectAttributes redirectAttributes) {
		log.info("도서 좋아요 삭제 경로");

		try {
			myPageBookLikesService.unlike(bookId);
			redirectAttributes.addFlashAttribute("globalSuccessMessage", "찜이 성공적으로 해제되었습니다.");
		} catch (Exception e) {
			log.error("찜 해제 중 오류", e);
			redirectAttributes.addFlashAttribute("globalErrorMessage", "찜 해제에 실패했습니다: " + e.getMessage());
		}
		// list 매핑이 /mypage/likes 이므로 다시 여기로 돌려 보내기
		return "redirect:/mypage/likes";
	}
}
