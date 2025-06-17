package shop.bluebooktle.common.domain.user;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import shop.bluebooktle.common.dto.user.response.AddressResponse;

class AddressResponseTest {

	@Test
	@DisplayName("Builder를 통한 AddressResponse 생성 테스트")
	void builderTest() {
		// given
		Long addressId = 1L;
		String alias = "집";
		String roadAddress = "서울 강남구 테헤란로 123";
		String detailAddress = "101동 202호";
		String postalCode = "12345";

		// when
		AddressResponse response = AddressResponse.builder()
			.addressId(addressId)
			.alias(alias)
			.roadAddress(roadAddress)
			.detailAddress(detailAddress)
			.postalCode(postalCode)
			.build();

		// then
		assertThat(response.getAddressId()).isEqualTo(addressId);
		assertThat(response.getAlias()).isEqualTo(alias);
		assertThat(response.getRoadAddress()).isEqualTo(roadAddress);
		assertThat(response.getDetailAddress()).isEqualTo(detailAddress);
		assertThat(response.getPostalCode()).isEqualTo(postalCode);
	}

}
