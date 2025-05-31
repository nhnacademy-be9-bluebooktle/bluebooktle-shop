package shop.bluebooktle.frontend.controller.myPage;

import java.util.List;

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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.common.dto.user.request.AddressRequest;
import shop.bluebooktle.common.dto.user.response.AddressResponse;
import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.frontend.service.AddressService;

@Slf4j
@Controller
@RequestMapping("/mypage/addresses")
@RequiredArgsConstructor
public class AddressController {

	private final AddressService addressService;

	@GetMapping()
	public String userAddressesPage(Model model, RedirectAttributes redirectAttributes) {
		try {
			List<AddressResponse> addresses = addressService.getAddresses();
			model.addAttribute("addresses", addresses);
			log.info("Loaded {} addresses for mypage.", addresses != null ? addresses.size() : 0);

			if (!model.containsAttribute("addressRequest")) {
				model.addAttribute("addressRequest", AddressRequest.builder().build());
			}
		} catch (ApplicationException e) {
			log.error("주소 페이지 로딩 중 오류: {}", e.getMessage());
			redirectAttributes.addFlashAttribute("globalErrorMessage", e.getMessage());
			return "redirect:/mypage/profile";
		} catch (Exception e) {
			log.error("주소 페이지 로딩 중 예상치 못한 오류:", e);
			redirectAttributes.addFlashAttribute("globalErrorMessage", "주소 정보를 불러오는 중 알 수 없는 오류가 발생했습니다.");
			return "redirect:/mypage/profile";
		}
		return "mypage/address_list";
	}

	@PostMapping("/save")
	public String saveAddress(@Valid @ModelAttribute("addressRequest") AddressRequest request,
		BindingResult bindingResult,
		@RequestParam(value = "addressId", required = false) Long addressId, // @RequestParam으로 받기
		RedirectAttributes redirectAttributes) {

		log.debug("Attempting to save address. ID: {}, Alias: {}", addressId, request.getAlias());

		if (bindingResult.hasErrors()) {
			log.warn("Address form validation failed: {}", bindingResult.getAllErrors());
			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.addressRequest",
				bindingResult);
			redirectAttributes.addFlashAttribute("addressRequest", request);
			if (addressId != null)
				redirectAttributes.addFlashAttribute("addressIdForModal", addressId); // 수정 모드 유지용 ID 전달
			redirectAttributes.addFlashAttribute("openModal", "addressFormModal");
			return "redirect:/mypage/addresses";
		}

		try {
			if (addressId != null) {
				log.info("Updating address with ID: {}", addressId);
				addressService.updateAddress(addressId, request);
				redirectAttributes.addFlashAttribute("globalSuccessMessage", "주소가 성공적으로 수정되었습니다.");
			} else {
				log.info("Creating new address with alias: {}", request.getAlias());
				addressService.createAddress(request);
				redirectAttributes.addFlashAttribute("globalSuccessMessage", "새 주소가 성공적으로 등록되었습니다.");
			}
		} catch (ApplicationException e) {
			log.error("주소 저장/수정 중 오류: {}", e.getMessage());
			redirectAttributes.addFlashAttribute("globalErrorMessage", e.getMessage());
			redirectAttributes.addFlashAttribute("addressRequest", request);
			if (addressId != null)
				redirectAttributes.addFlashAttribute("addressIdForModal", addressId);
			redirectAttributes.addFlashAttribute("openModal", "addressFormModal");
		} catch (Exception e) {
			log.error("주소 저장/수정 중 예상치 못한 오류:", e);
			redirectAttributes.addFlashAttribute("globalErrorMessage", "주소 처리 중 알 수 없는 오류가 발생했습니다.");
			redirectAttributes.addFlashAttribute("addressRequest", request);
			if (addressId != null)
				redirectAttributes.addFlashAttribute("addressIdForModal", addressId);
			redirectAttributes.addFlashAttribute("openModal", "addressFormModal");
		}

		return "redirect:/mypage/addresses";
	}

	@PostMapping("/delete/{id}")
	public String deleteAddress(@PathVariable("id") Long addressId, RedirectAttributes redirectAttributes) {
		try {
			addressService.deleteAddress(addressId);
			redirectAttributes.addFlashAttribute("globalSuccessMessage", "주소가 삭제되었습니다.");
		} catch (ApplicationException e) {
			log.error("주소 삭제 중 오류: {}", e.getMessage());
			redirectAttributes.addFlashAttribute("globalErrorMessage", e.getMessage());
		} catch (Exception e) {
			log.error("주소 삭제 중 예상치 못한 오류:", e);
			redirectAttributes.addFlashAttribute("globalErrorMessage", "주소 삭제 중 알 수 없는 오류가 발생했습니다.");
		}
		return "redirect:/mypage/addresses";
	}
}