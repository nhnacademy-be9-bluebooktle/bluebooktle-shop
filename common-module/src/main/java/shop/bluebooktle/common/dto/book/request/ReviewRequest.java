package shop.bluebooktle.common.dto.book.request;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewRequest {

	@NotNull
	@Min(1)
	@Max(5)
	private Integer star;

	@Size(min = 10, max = 255, message = "리뷰 내용은 10~255자 입니다")
	private String reviewContent;

	private List<MultipartFile> imageFiles;
}
