package shop.bluebooktle.common.dto.book.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

@AllArgsConstructor
@Getter
@Value
@Builder
public class BookLikesResponse {
	private Long bookId;
	private boolean isLiked;
	private int countLikes;
}
