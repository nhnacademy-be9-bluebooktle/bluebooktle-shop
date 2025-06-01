package shop.bluebooktle.common.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public interface StringAttributeCryptoConverter extends AttributeConverter<String, String> {
}
