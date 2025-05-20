package shop.bluebooktle.frontend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.bluebooktle.common.dto.coupon.request.CouponRegisterRequest;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponRegisterFormDto {

	@NotNull(message = "쿠폰 정책 선택은 필수입니다.")
	private Long couponTypeId;
	@NotBlank(message = "쿠폰 이름은 필수입니다.")
	@Size(max = 100, message = "쿠폰명은 100자 이하로 입력해주세요.")
	private String name;

	private Long bookId;      // 도서 관련 정책일 때만 사용
	private Long categoryId;  // 카테고리 관련 정책일 때만 사용

	public CouponRegisterRequest toRequest() {
		return CouponRegisterRequest.builder()
			.name(this.name)
			.couponTypeId(this.couponTypeId)
			.bookId(this.bookId)
			.categoryId(this.categoryId)
			.build();
	}
}
