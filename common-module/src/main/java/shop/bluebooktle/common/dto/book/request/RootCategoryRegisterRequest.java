package shop.bluebooktle.common.dto.book.request;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor        // 파라미터 없는 생성자
@AllArgsConstructor
public class RootCategoryRegisterRequest {
	@NotBlank
	@Length(max = 50)
	String rootCategoryName;
	@NotBlank
	@Length(max = 50)
	String childCategoryName;
}
