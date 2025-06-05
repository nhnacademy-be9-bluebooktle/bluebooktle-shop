package shop.bluebooktle.common.dto.book.response.author;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
@NoArgsConstructor        // 파라미터 없는 생성자
@AllArgsConstructor
public class AuthorResponse {
	Long id;
	String name;
	LocalDateTime createdAt;
}
