package shop.bluebooktle.common.dto.book.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewRequest {

	private Long imgId;

	@NotNull
	@Positive
	@Max(5)
	private Integer star;

	@Size(min = 1, max = 255, message = "리뷰 내용은 1~255자 입니다")
	private String reviewContent;
}
