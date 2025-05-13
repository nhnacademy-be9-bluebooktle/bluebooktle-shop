package shop.bluebooktle.backend.book.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

@AllArgsConstructor
@Getter
@Value
@Builder
public class BookLikesRequest {
	@NotNull(message = "도서 ID는 필수입니다.")
	@Positive(message = "도서 ID는 1 이상의 양수이어야 합니다.")
	Long bookId;
	@NotNull(message = "회원 ID(user_id)는 필수입니다.")
	@Positive(message = "회원 ID(user_id)는 1 이상의 양수이어야 합니다.")
	Long userId;
}