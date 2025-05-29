package shop.bluebooktle.common.dto.user.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.bluebooktle.common.entity.auth.Address;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressResponse {
	private Long addressId;
	private String alias;
	private String roadAddress;
	private String detailAddress;
	private String postalCode;

	public static AddressResponse fromEntity(Address address) {
		return AddressResponse.builder()
			.addressId(address.getId())
			.alias(address.getAlias())
			.roadAddress(address.getRoadAddress())
			.detailAddress(address.getDetailAddress())
			.postalCode(address.getPostalCode())
			.build();
	}
}