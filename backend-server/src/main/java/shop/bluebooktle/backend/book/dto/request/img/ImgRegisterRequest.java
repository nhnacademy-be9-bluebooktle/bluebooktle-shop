package shop.bluebooktle.backend.book.dto.request.img;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ImgRegisterRequest {
	String imgUrl;
}
