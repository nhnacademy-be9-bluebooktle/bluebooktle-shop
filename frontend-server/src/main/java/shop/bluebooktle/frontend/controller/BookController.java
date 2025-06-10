package shop.bluebooktle.frontend.controller;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
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
import shop.bluebooktle.common.domain.point.PointSourceTypeEnum;
import shop.bluebooktle.common.dto.book.BookSortType;
import shop.bluebooktle.common.dto.book.response.BookDetailResponse;
import shop.bluebooktle.common.dto.book.response.BookInfoResponse;
import shop.bluebooktle.common.dto.book.response.CategoryResponse;
import shop.bluebooktle.common.dto.order.response.DeliveryRuleResponse;
import shop.bluebooktle.common.dto.point.response.PointRuleResponse;
import shop.bluebooktle.frontend.service.AdminPointService;
import shop.bluebooktle.frontend.service.BookService;
import shop.bluebooktle.frontend.service.DeliveryRuleService;

@Controller
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class BookController {

	private final BookService bookService;
	private final DeliveryRuleService deliveryRuleService;
	private final AdminPointService adminPointService;

	@GetMapping("/books")
	public String bookListPage(
		Model model,
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "20") int size,
		@RequestParam(value = "searchKeyword", required = false) String searchKeyword,
		@RequestParam(defaultValue = "POPULARITY") BookSortType bookSortType
	) {

		Page<BookInfoResponse> pagedBooks = bookService.getPagedBooks(page, size, searchKeyword, bookSortType);
		log.info("pagedBooks: {}", pagedBooks);
		log.info("pagedBooks: {}", pagedBooks.getNumber());
		log.info("pagedBooks: {}", pagedBooks.getTotalElements());
		log.info("pagedBooks: {}", pagedBooks.getNumberOfElements());
		log.info("pagedBooks: {}", pagedBooks.getContent());

		model.addAttribute("sortTypes", BookSortType.values());
		model.addAttribute("bookSortType", bookSortType);
		model.addAttribute("searchKeyword", searchKeyword);
		model.addAttribute("size", size);
		model.addAttribute("filterCount", BookSortType.values().length);
		model.addAttribute("pagedBooks", pagedBooks);

		return "book/book_list";
	}

	@GetMapping("/categories/{categoryId}")
	public String booksByCategoryPage(
		Model model,
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "20") int size,
		@RequestParam(defaultValue = "POPULARITY") BookSortType bookSortType,
		@PathVariable Long categoryId
	) {
		log.info("요청된 카테고리 ID: {}", categoryId);
		Page<BookInfoResponse> pagedBooksByCategory = bookService.getPagedBooksByCategoryId(page, size, bookSortType,
			categoryId);
		CategoryResponse category = bookService.getCategoryById(categoryId);
		log.info("pagedBooksByCategory: {}", pagedBooksByCategory);
		log.info("category: {}", category);

		model.addAttribute("sortTypes", BookSortType.values());
		model.addAttribute("bookSortType", bookSortType);
		model.addAttribute("category", category);
		model.addAttribute("size", size);
		model.addAttribute("filterCount", BookSortType.values().length);
		model.addAttribute("pagedBooks", pagedBooksByCategory);

		return "book/book_list";
	}

	// 도서 상세 페이지
	@GetMapping("/books/{bookId}")
	public String bookDetailPage(@PathVariable Long bookId,
		Model model,
		RedirectAttributes redirectAttributes) {
		log.info("도서 상세 조회 요청");
		try {

			// 도서 정보 가져오기
			BookDetailResponse book = bookService.getBookDetail(bookId);
			model.addAttribute("book", book);
			model.addAttribute("bookId", bookId);

			// 배송 정책 들고오기
			DeliveryRuleResponse deliveryRule = deliveryRuleService.getDefaultDeliveryRule();
			model.addAttribute("deliveryRule", deliveryRule);

			// 로그인 여부 확인
			Object rawIsLoggedIn = model.asMap().get("isLoggedIn");
			boolean isLoggedIn = (rawIsLoggedIn instanceof Boolean) ? (Boolean)rawIsLoggedIn : false;

			// 도서의 총 좋아요 개수 가져오기
			int likeCount = bookService.countLikes(bookId);
			model.addAttribute("likeCount", likeCount);

			// 로그인한 경우에만 좋아요와 적립될 포인트 계산
			if (isLoggedIn) {
				// 현재 로그인한 사용자가 해당 도서를 좋아요 했는지 확인
				boolean isLiked = bookService.isLiked(bookId);
				model.addAttribute("isLiked", isLiked);

				// 적립될 포인트 계산
				PointRuleResponse ruleResponse = adminPointService.getRuleByType(PointSourceTypeEnum.PAYMENT_EARN);
				log.info("ruleResponse: {}", ruleResponse);
				if (ruleResponse == null) {
					log.warn("PointRuleResponse가 없거나 value가 null 입니다.");
					model.addAttribute("earnedPoints", 0);
				} else {
					BigDecimal earnPercent = ruleResponse.value();
					long salePrice = book.getSalePrice().longValue();
					int earnPercentage = earnPercent.intValue();
					int earnedPoints = (int)(earnPercentage * salePrice / 100.0);
					model.addAttribute("earnedPoints", earnedPoints);
				}
			}
			return "book/book_detail";
		} catch (Exception e) {
			log.error("도서 상세 조회 실패");
			redirectAttributes.addFlashAttribute("globalErrorMessage", "도서 정보 조회에 실패했습니다: " + e.getMessage());
			return "redirect:/books";
		}
	}

	// 도서 찜 등록
	@GetMapping("/books/{bookId}/likes")
	public String likeBook(@PathVariable Long bookId, RedirectAttributes redirectAttributes) {
		try {
			bookService.like(bookId);
			redirectAttributes.addFlashAttribute("globalSuccessMessage", "찜 완료!");
		} catch (Exception e) {
			log.error("도서 찜 실패");
			redirectAttributes.addFlashAttribute("globalErrorMessage", "찜에 실패했습니다: " + e.getMessage());
		}
		return "redirect:/books/" + bookId;
	}

	// 도서 찜 취소
	@PostMapping("/books/{bookId}/unlikes")
	public String unlikeBook(@PathVariable Long bookId, RedirectAttributes redirectAttributes) {
		try {
			bookService.unlike(bookId);
			redirectAttributes.addFlashAttribute("globalSuccessMessage", "찜 취소!");
		} catch (Exception e) {
			log.error("도서 찜 취소 실패");
			redirectAttributes.addFlashAttribute("globalErrorMessage", "찜 취소에 실패했습니다: " + e.getMessage());
		}
		return "redirect:/books/" + bookId;
	}
}
