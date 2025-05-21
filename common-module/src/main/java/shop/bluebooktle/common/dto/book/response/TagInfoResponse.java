package shop.bluebooktle.common.dto.book.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TagInfoResponse {
	private Long id;
	private String name;
	private LocalDateTime createdAt;
}
