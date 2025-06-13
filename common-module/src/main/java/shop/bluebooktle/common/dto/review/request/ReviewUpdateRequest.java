package shop.bluebooktle.common.dto.review.request;

import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewUpdateRequest {
	@NotNull
	@Min(1)
	@Max(5)
	Integer star;

	@Size(min = 10, max = 255, message = "리뷰 내용은 10~255자 입니다")
	String reviewContent;

	List<String> imgUrls;
}
