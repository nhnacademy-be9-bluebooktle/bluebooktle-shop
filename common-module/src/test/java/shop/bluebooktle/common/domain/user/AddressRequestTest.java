package shop.bluebooktle.common.domain.user;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import shop.bluebooktle.common.dto.user.request.AddressRequest;
import shop.bluebooktle.common.entity.auth.Address;

class AddressRequestTest {

	@Test
	@DisplayName("Builder를 통한 AddressRequest 생성 및 필드 확인")
	void builderTest() {
		// given
		String alias = "집";
		String roadAddress = "서울특별시 강남구 테헤란로 123";
		String detailAddress = "101동 202호";
		String postalCode = "12345";

		// when
		AddressRequest request = AddressRequest.builder()
			.alias(alias)
			.roadAddress(roadAddress)
			.detailAddress(detailAddress)
			.postalCode(postalCode)
			.build();

		// then
		assertThat(request.getAlias()).isEqualTo(alias);
		assertThat(request.getRoadAddress()).isEqualTo(roadAddress);
		assertThat(request.getDetailAddress()).isEqualTo(detailAddress);
		assertThat(request.getPostalCode()).isEqualTo(postalCode);
	}

	@Test
	@DisplayName("toEntity() 메서드로 Address 엔티티 변환 테스트")
	void toEntityTest() {
		// given
		AddressRequest request = AddressRequest.builder()
			.alias("회사")
			.roadAddress("서울시 종로구 종로1길 1")
			.detailAddress("A동 5층")
			.postalCode("54321")
			.build();

		// when
		Address address = request.toEntity();

		// then
		assertThat(address.getAlias()).isEqualTo(request.getAlias());
		assertThat(address.getRoadAddress()).isEqualTo(request.getRoadAddress());
		assertThat(address.getDetailAddress()).isEqualTo(request.getDetailAddress());
		assertThat(address.getPostalCode()).isEqualTo(request.getPostalCode());
	}

}
