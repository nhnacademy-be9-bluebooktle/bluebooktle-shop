package shop.bluebooktle.common.dto.membership;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MembershipLevelDto {
	private Long id;
	private String name;
}