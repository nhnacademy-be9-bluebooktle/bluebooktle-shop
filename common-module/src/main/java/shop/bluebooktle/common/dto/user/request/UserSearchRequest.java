package shop.bluebooktle.common.dto.user.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserSearchRequest {
	private String searchField;
	private String searchKeyword;
	private String userTypeFilter;
	private String userStatusFilter;
}