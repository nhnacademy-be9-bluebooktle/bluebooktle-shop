package shop.bluebooktle.frontend.controller.admin;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
@RequestMapping("/admin/publishers")
@RequiredArgsConstructor
public class AdminPublisherController {

	@Getter
	@Setter
	@ToString
	static class PublisherDto {
		private Long id;
		private String name;
		private LocalDateTime createdAt;
		private LocalDateTime deletedAt;
		// UI 편의를 위해 isActive 추가
		private boolean isActive;

		public PublisherDto() {
			this.isActive = true; // 기본값
		}

		public PublisherDto(Long id, String name, LocalDateTime createdAt, LocalDateTime deletedAt) {
			this.id = id;
			this.name = name;
			this.createdAt = createdAt;
			this.deletedAt = deletedAt;
			this.isActive = (deletedAt == null);
		}
	}

	// 임시 출판사 데이터 목록
	private static final List<PublisherDto> allPublishersForDemo = new ArrayList<>();

	static {
		allPublishersForDemo.add(new PublisherDto(1L, "블루출판사", LocalDateTime.now().minusDays(30), null));
		allPublishersForDemo.add(new PublisherDto(2L, "북틀컴퍼니", LocalDateTime.now().minusDays(15), null));
		allPublishersForDemo.add(
			new PublisherDto(3L, "학문사", LocalDateTime.now().minusDays(60), LocalDateTime.now().minusDays(5)));
		for (int i = 4; i <= 28; i++) { // 데이터 추가
			allPublishersForDemo.add(new PublisherDto((long)i, "출판사 넘버 " + i, LocalDateTime.now().minusDays(i * 5),
				(i % 6 == 0) ? LocalDateTime.now().minusDays(i) : null));
		}
	}

	@GetMapping
	public String listPublishers(Model model, HttpServletRequest request,
		@RequestParam(value = "page", defaultValue = "0") int page, // 0-based
		@RequestParam(value = "size", defaultValue = "10") int size,
		@RequestParam(value = "searchKeyword", required = false) String searchKeyword,
		@RequestParam(value = "statusFilter", required = false) String statusFilter) { // 상태 필터 추가
		log.info("AdminPublisherController - listPublishers: page={}, size={}, searchKeyword={}, statusFilter={}", page,
			size, searchKeyword, statusFilter);
		model.addAttribute("pageTitle", "출판사 관리");
		model.addAttribute("currentURI", request.getRequestURI());

		List<PublisherDto> filteredPublishers = allPublishersForDemo.stream()
			.filter(pub -> (searchKeyword == null || searchKeyword.trim().isEmpty()) ||
				(pub.getName() != null && pub.getName().toLowerCase().contains(searchKeyword.toLowerCase()))
			)
			.filter(pub -> (statusFilter == null || statusFilter.isEmpty()) ||
				("active".equals(statusFilter) && pub.getDeletedAt() == null) ||
				("inactive".equals(statusFilter) && pub.getDeletedAt() != null)
			)
			.collect(Collectors.toList());

		Sort sort = Sort.by(Sort.Direction.DESC, "createdAt"); // 기본 정렬
		Pageable pageable = PageRequest.of(page, size, sort);

		int start = (int)pageable.getOffset();
		int end = Math.min((start + pageable.getPageSize()), filteredPublishers.size());
		List<PublisherDto> pageContent =
			(start >= filteredPublishers.size() || start > end) ? List.of() : filteredPublishers.subList(start, end);
		Page<PublisherDto> publishersPage = new PageImpl<>(pageContent, pageable, filteredPublishers.size());

		log.info("Publisher Pagination Data: TotalElements={}, TotalPages={}, CurrentPage(0-based)={}, PageSize={}",
			publishersPage.getTotalElements(), publishersPage.getTotalPages(), publishersPage.getNumber(),
			publishersPage.getSize());

		model.addAttribute("publishers", publishersPage.getContent());
		model.addAttribute("currentPage", publishersPage.getNumber());
		model.addAttribute("totalPages", publishersPage.getTotalPages());
		model.addAttribute("currentSize", publishersPage.getSize());
		model.addAttribute("totalElements", publishersPage.getTotalElements());

		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath(request.getRequestURI())
			.queryParam("size", publishersPage.getSize());
		if (searchKeyword != null && !searchKeyword.isEmpty())
			uriBuilder.queryParam("searchKeyword", searchKeyword);
		if (statusFilter != null && !statusFilter.isEmpty())
			uriBuilder.queryParam("statusFilter", statusFilter);

		String baseUrlWithParams = uriBuilder.toUriString();
		log.info("Base URL for publisher pagination (excluding 'page' param): {}", baseUrlWithParams);
		model.addAttribute("baseUrlWithParams", baseUrlWithParams);

		model.addAttribute("searchKeyword", searchKeyword);
		model.addAttribute("statusFilter", statusFilter); // 상태 필터 값 유지
		model.addAttribute("allPublishersForDemo", allPublishersForDemo); // 총 건수 표시용

		return "admin/publisher/publisher_list";
	}

	@GetMapping({"/new", "/{publisherId}/edit"})
	public String publisherForm(@PathVariable(value = "publisherId", required = false) Long publisherId,
		Model model, HttpServletRequest request) {
		log.info("어드민 출판사 폼 페이지 요청. URI: {}, publisherId: {}", request.getRequestURI(), publisherId);
		model.addAttribute("currentURI", request.getRequestURI());

		PublisherDto publisherDto;
		String pageTitle;

		if (publisherId != null) {
			pageTitle = "출판사 정보 수정 (ID: " + publisherId + ")";
			final Long finalPublisherId = publisherId;
			publisherDto = allPublishersForDemo.stream()
				.filter(p -> p.getId().equals(finalPublisherId))
				.findFirst()
				.orElse(new PublisherDto());
			if (publisherDto.getId() == null && publisherId != null) {
				publisherDto.setId(publisherId);
				log.warn("Publisher with ID {} not found in demo data for edit form.", publisherId);
			}
		} else {
			pageTitle = "새 출판사 등록";
			publisherDto = new PublisherDto();
		}
		model.addAttribute("pageTitle", pageTitle);
		model.addAttribute("publisher", publisherDto);

		return "admin/publisher/publisher_form";
	}

	@PostMapping("/save")
	public String savePublisher(@ModelAttribute("publisher") PublisherDto publisherDto,
		BindingResult bindingResult,
		RedirectAttributes redirectAttributes,
		HttpServletRequest request) {
		log.info("출판사 저장 요청: {}", publisherDto);

		if (publisherDto.getName() == null || publisherDto.getName().trim().isEmpty()) {
			bindingResult.rejectValue("name", "NotEmpty", "출판사 이름은 필수입니다.");
		}

		if (bindingResult.hasErrors()) {
			log.warn("출판사 저장 폼 유효성 검증 에러: {}", bindingResult.getAllErrors());
			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.publisher",
				bindingResult);
			redirectAttributes.addFlashAttribute("publisher", publisherDto);
			redirectAttributes.addFlashAttribute("globalErrorMessage", "입력값을 확인해주세요.");
			if (publisherDto.getId() != null) {
				return "redirect:/admin/publishers/" + publisherDto.getId() + "/edit";
			} else {
				return "redirect:/admin/publishers/new";
			}
		}

		try {
			// TODO: 실제 서비스 로직
			String action = (publisherDto.getId() == null) ? "등록" : "수정";
			log.info("출판사 {} 처리 (임시): Name={}, DeletedAt={}", action, publisherDto.getName(),
				publisherDto.getDeletedAt());
			redirectAttributes.addFlashAttribute("globalSuccessMessage",
				"출판사 '" + publisherDto.getName() + "' 정보가 성공적으로 " + action + "되었습니다.");
		} catch (Exception e) {
			log.error("출판사 저장 중 오류 발생", e);
			redirectAttributes.addFlashAttribute("globalErrorMessage", "출판사 저장 중 오류가 발생했습니다: " + e.getMessage());
			redirectAttributes.addFlashAttribute("publisher", publisherDto);
			if (publisherDto.getId() != null) {
				return "redirect:/admin/publishers/" + publisherDto.getId() + "/edit";
			} else {
				return "redirect:/admin/publishers/new";
			}
		}
		return "redirect:/admin/publishers";
	}

	@PostMapping("/{publisherId}/delete") // 비활성화
	public String deletePublisher(@PathVariable Long publisherId, RedirectAttributes redirectAttributes,
		HttpServletRequest request) {
		log.info("출판사 비활성화 요청: ID {}", publisherId);
		try {
			// TODO: 실제 서비스 로직 - deleted_at 설정
			allPublishersForDemo.stream()
				.filter(p -> p.getId().equals(publisherId))
				.findFirst()
				.ifPresent(p -> p.setDeletedAt(LocalDateTime.now()));
			log.info("임시 출판사 비활성화 성공 처리: ID {}", publisherId);
			redirectAttributes.addFlashAttribute("globalSuccessMessage",
				"출판사(ID: " + publisherId + ")가 성공적으로 비활성화 처리되었습니다.");
		} catch (Exception e) {
			log.error("출판사 비활성화 중 오류 발생", e);
			redirectAttributes.addFlashAttribute("globalErrorMessage", "출판사 비활성화 중 오류가 발생했습니다: " + e.getMessage());
		}
		return "redirect:/admin/publishers";
	}

	@PostMapping("/{publisherId}/activate") // 활성화
	public String activatePublisher(@PathVariable Long publisherId, RedirectAttributes redirectAttributes,
		HttpServletRequest request) {
		log.info("출판사 활성화 요청: ID {}", publisherId);
		try {
			// TODO: 실제 서비스 로직 - deleted_at을 NULL로 설정
			allPublishersForDemo.stream()
				.filter(p -> p.getId().equals(publisherId))
				.findFirst()
				.ifPresent(p -> p.setDeletedAt(null));
			log.info("임시 출판사 활성화 성공 처리: ID {}", publisherId);
			redirectAttributes.addFlashAttribute("globalSuccessMessage",
				"출판사(ID: " + publisherId + ")가 성공적으로 활성화 처리되었습니다.");
		} catch (Exception e) {
			log.error("출판사 활성화 중 오류 발생", e);
			redirectAttributes.addFlashAttribute("globalErrorMessage", "출판사 활성화 중 오류가 발생했습니다: " + e.getMessage());
		}
		return "redirect:/admin/publishers";
	}
}