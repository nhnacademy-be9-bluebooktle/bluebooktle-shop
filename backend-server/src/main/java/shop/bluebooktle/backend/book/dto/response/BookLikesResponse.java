package shop.bluebooktle.backend.book.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

@AllArgsConstructor
@Getter
@Value
@Builder
public class BookLikesResponse {
	Long bookId;
	boolean isLiked;
	int countLikes;
}
