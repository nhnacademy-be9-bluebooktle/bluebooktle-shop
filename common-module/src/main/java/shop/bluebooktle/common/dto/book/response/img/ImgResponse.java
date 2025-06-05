package shop.bluebooktle.common.dto.book.response.img;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImgResponse {
	Long id;
	String imgUrl;
	LocalDateTime createdAt;
}
