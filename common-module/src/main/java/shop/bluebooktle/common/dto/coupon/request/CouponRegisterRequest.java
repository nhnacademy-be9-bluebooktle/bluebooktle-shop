package shop.bluebooktle.common.dto.coupon.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CouponRegisterRequest {
	@NotNull(message = "쿠폰 정책 선택은 필수입니다.")
	Long couponTypeId;
	@NotBlank(message = "쿠폰 이름은 필수입니다.")
	@Size(max = 100, message = "쿠폰명은 100자 이하로 입력해주세요.")
	String name;

	Long bookId;
	Long categoryId;

}
