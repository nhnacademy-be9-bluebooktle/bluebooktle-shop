package shop.bluebooktle.common.dto.user.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.bluebooktle.common.entity.auth.Address;

@Getter
@Setter
@NoArgsConstructor
public class AddressRequest {

	@NotBlank(message = "별칭을 입력해주세요.")
	@Size(max = 50, message = "별칭은 최대 50자까지 입력 가능합니다.")
	private String alias;

	@NotBlank(message = "도로명 주소를 입력해주세요.")
	@Size(max = 255, message = "도로명 주소는 최대 255자까지 입력 가능합니다.")
	private String roadAddress;

	@NotBlank(message = "상세 주소를 입력해주세요.")
	@Size(max = 255, message = "상세 주소는 최대 255자까지 입력 가능합니다.")
	private String detailAddress;

	@NotBlank(message = "우편번호를 입력해주세요.")
	@Size(min = 5, max = 5, message = "우편번호는 5자리여야 합니다.")
	private String postalCode;

	public Address toEntity() {
		return Address.builder()
			.alias(this.alias)
			.roadAddress(this.roadAddress)
			.detailAddress(this.detailAddress)
			.postalCode(this.postalCode)
			.build();
	}

	@Builder
	public AddressRequest(String alias, String roadAddress, String detailAddress, String postalCode) {
		this.alias = alias;
		this.roadAddress = roadAddress;
		this.detailAddress = detailAddress;
		this.postalCode = postalCode;
	}
}