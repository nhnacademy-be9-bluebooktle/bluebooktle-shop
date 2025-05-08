package shop.bluebooktle.backend.book.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

@AllArgsConstructor
@Getter
@Value
@Builder
public class BookLikesRequest {
	long bookId;
	long userId;
}