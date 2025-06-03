package shop.bluebooktle.common.dto.coupon.response;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UsableUserCouponMapResponse {
	Map<Long, List<UsableUserCouponResponse>> usableUserCouponMap;
}