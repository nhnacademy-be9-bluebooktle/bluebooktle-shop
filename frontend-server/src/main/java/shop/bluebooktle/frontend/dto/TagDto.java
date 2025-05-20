package shop.bluebooktle.frontend.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TagDto {
	private Long id; // tag_id
	private String name;
	private LocalDateTime createdAt;
	private LocalDateTime deletedAt;

	public TagDto() { // 기본 생성자
	}

	public TagDto(Long id, String name, LocalDateTime createdAt, LocalDateTime deletedAt) {
		this.id = id;
		this.name = name;
		this.createdAt = createdAt;
		this.deletedAt = deletedAt;
	}
}
