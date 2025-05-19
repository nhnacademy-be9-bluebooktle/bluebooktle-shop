package shop.bluebooktle.common.dto.auth;

import java.io.Serial;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.bluebooktle.common.domain.auth.UserStatus;
import shop.bluebooktle.common.domain.auth.UserType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	private Long id;
	private String loginId;
	private String nickname;
	private UserType type;
	private UserStatus status;
}