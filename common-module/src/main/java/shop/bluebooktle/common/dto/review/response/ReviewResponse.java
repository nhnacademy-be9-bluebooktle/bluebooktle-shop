package shop.bluebooktle.common.dto.review.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponse {

	private Long reviewId;
	private Long userId;
	private Long bookOrderId;
	private String bookTitle;
	private String nickname;
	private String imgUrl;
	private Integer star;
	private String reviewContent;
	private Integer likes;
	private LocalDateTime createdAt;
}

