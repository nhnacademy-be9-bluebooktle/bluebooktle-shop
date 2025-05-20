package shop.bluebooktle.common.dto.book.request.img;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ImgRegisterRequest {
	@NotBlank
	String imgUrl;
}
