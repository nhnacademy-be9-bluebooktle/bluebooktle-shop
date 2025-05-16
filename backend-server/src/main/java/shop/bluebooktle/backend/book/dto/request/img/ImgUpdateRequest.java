package shop.bluebooktle.backend.book.dto.request.img;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ImgUpdateRequest {
	@NotBlank
	String imgUrl;
}
