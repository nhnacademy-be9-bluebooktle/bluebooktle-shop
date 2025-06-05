package shop.bluebooktle.common.dto.order.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder(toBuilder = true)
public record OrderCreateRequest(
	Long userId,

	@FutureOrPresent(message = "요청 배송일은 현재 시각 이후여야 합니다.")
	LocalDate requestedDeliveryDate,

	@NotBlank(message = "주문명은 필수입니다.")
	@Size(max = 150, message = "주문명은 최대 150자까지 입력 가능합니다.")
	String orderName,

	@NotNull(message = "주문 아이템 목록을 제공해야 합니다.")
	@Size(min = 1, message = "최소 한 개 이상의 주문 아이템이 필요합니다.")
	@Valid
	List<OrderItemRequest> orderItems,

	@NotNull(message = "배송비는 필수입니다.")
	@DecimalMin(value = "0", inclusive = true, message = "배송비는 0원 이상이어야 합니다.")
	BigDecimal deliveryFee,

	@NotNull(message = "배송 정책 ID는 필수입니다.")
	Long deliveryRuleId,

	@NotBlank(message = "주문자 이름은 필수입니다.")
	@Size(max = 20, message = "주문자 이름은 최대 20자까지 입력 가능합니다.")
	String ordererName,

	@Size(max = 50, message = "주문자 이메일은 최대 50자 까지 입력 가능합니다.")
	String ordererEmail,

	@NotBlank(message = "주문자 전화번호는 필수입니다.")
	@Size(max = 11, message = "주문자 전화번호는 최대 11자까지 입력 가능합니다.")
	String ordererPhoneNumber,

	@NotBlank(message = "수령인 이름은 필수입니다.")
	@Size(max = 20, message = "수령인 이름은 최대 20자까지 입력 가능합니다.")
	String receiverName,

	@Email(message = "유효한 이메일 형식이 아닙니다.")
	@Size(max = 50, message = "수령인 이메일은 최대 50자까지 입력 가능합니다.")
	String receiverEmail,

	@NotBlank(message = "수령인 전화번호는 필수입니다.")
	@Size(max = 11, message = "수령인 전화번호는 최대 11자까지 입력 가능합니다.")
	String receiverPhoneNumber,

	@NotBlank(message = "우편번호는 필수입니다.")
	@Size(max = 5, message = "우편번호는 최대 5자까지 입력 가능합니다.")
	String postalCode,

	@NotBlank(message = "주소는 필수입니다.")
	@Size(max = 255, message = "주소는 최대 255자까지 입력 가능합니다.")
	String address,

	@NotBlank(message = "상세 주소는 필수입니다.")
	@Size(max = 255, message = "상세 주소는 최대 255자까지 입력 가능합니다.")
	String detailAddress,

	@DecimalMin(value = "0", inclusive = true, message = "쿠폰 할인액은 0원 이상이어야 합니다.")
	BigDecimal couponDiscountAmount,

	@DecimalMin(value = "0", inclusive = true, message = "포인트 사용액 0원 이상이어야 합니다.")
	BigDecimal pointUseAmount,

	@DecimalMin(value = "0", inclusive = true, message = "책기본할인금액은 0원 이상이어야 합니다.")
	BigDecimal saleDiscountAmount,

	@DecimalMin(value = "0", inclusive = true, message = "결제 원금은 0원 이상이어야 합니다.")
	BigDecimal originalAmount,

	String orderKey

) {
}