package shop.bluebooktle.backend.book.dto.response.img;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ImgResponse {
	Long id;
	String imgUrl;
	LocalDateTime createdAt;
}
