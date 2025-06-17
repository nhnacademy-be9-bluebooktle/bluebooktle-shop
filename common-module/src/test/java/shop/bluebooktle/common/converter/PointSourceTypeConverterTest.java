package shop.bluebooktle.common.converter;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import shop.bluebooktle.common.domain.point.PointSourceTypeEnum;

class PointSourceTypeConverterTest {

	private final PointSourceTypeConverter converter = new PointSourceTypeConverter();

	@Test
	@DisplayName("Enum을 DB 값(Long)으로 변환")
	void convertToDatabaseColumn() {
		// given
		PointSourceTypeEnum sourceType = PointSourceTypeEnum.SIGNUP_EARN;

		// when
		Long dbValue = converter.convertToDatabaseColumn(sourceType);

		// then
		assertThat(dbValue).isEqualTo(sourceType.getId());
	}

	@Test
	@DisplayName("null Enum 변환 시 null 반환")
	void convertToDatabaseColumn_null() {
		assertThat(converter.convertToDatabaseColumn(null)).isNull();
	}

	@Test
	@DisplayName("DB 값(Long)을 Enum으로 변환")
	void convertToEntityAttribute() {
		// given
		Long id = PointSourceTypeEnum.SIGNUP_EARN.getId();

		// when
		PointSourceTypeEnum sourceType = converter.convertToEntityAttribute(id);

		// then
		assertThat(sourceType).isEqualTo(PointSourceTypeEnum.SIGNUP_EARN);
	}

	@Test
	@DisplayName("null DB 값 변환 시 null 반환")
	void convertToEntityAttribute_null() {
		assertThat(converter.convertToEntityAttribute(null)).isNull();
	}

}
