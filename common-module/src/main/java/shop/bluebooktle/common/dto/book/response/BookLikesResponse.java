package shop.bluebooktle.common.dto.book.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor   // Jackson용 기본 생성자
public class BookLikesResponse {
	private Long bookId;
	private boolean isLiked;
	private int countLikes;

	@Builder
	public BookLikesResponse(Long bookId, boolean isLiked, int countLikes) {
		this.bookId = bookId;
		this.isLiked = isLiked;
		this.countLikes = countLikes;
	}
}

