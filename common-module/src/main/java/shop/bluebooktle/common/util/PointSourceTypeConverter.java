package shop.bluebooktle.common.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import shop.bluebooktle.common.domain.point.PointSourceTypeEnum;

@Converter
public class PointSourceTypeConverter
	implements AttributeConverter<PointSourceTypeEnum, Long> {

	@Override
	public Long convertToDatabaseColumn(PointSourceTypeEnum attribute) {
		return attribute == null ? null : attribute.getId();
	}

	@Override
	public PointSourceTypeEnum convertToEntityAttribute(Long dbData) {
		return dbData == null ? null : PointSourceTypeEnum.fromId(dbData);
	}
}
