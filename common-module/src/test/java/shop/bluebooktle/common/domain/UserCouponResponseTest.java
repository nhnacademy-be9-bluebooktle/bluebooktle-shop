package shop.bluebooktle.common.domain;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import shop.bluebooktle.common.domain.coupon.CouponTypeTarget;
import shop.bluebooktle.common.dto.coupon.response.UserCouponResponse;

class UserCouponResponseTest {

	@Test
	@DisplayName("QueryProjection 생성자 테스트")
	void queryProjectionConstructorTest() {
		// given
		Long id = 1L;
		LocalDateTime createdAt = LocalDateTime.of(2024, 6, 1, 10, 0);
		String couponName = "첫 구매 할인";
		String couponTypeName = "정액할인";
		CouponTypeTarget target = CouponTypeTarget.BOOK;
		LocalDateTime availableStartAt = LocalDateTime.of(2024, 6, 2, 0, 0);
		LocalDateTime availableEndAt = LocalDateTime.of(2024, 6, 30, 23, 59);
		LocalDateTime usedAt = LocalDateTime.of(2024, 6, 15, 12, 30);
		String categoryName = "문학";
		String bookName = "이방인";

		// when
		UserCouponResponse response = new UserCouponResponse(
			id, createdAt, couponName, couponTypeName, target,
			availableStartAt, availableEndAt, usedAt, categoryName, bookName
		);

		// then
		assertThat(response.getId()).isEqualTo(id);
		assertThat(response.getCreatedAt()).isEqualTo(createdAt);
		assertThat(response.getCouponName()).isEqualTo(couponName);
		assertThat(response.getCouponTypeName()).isEqualTo(couponTypeName);
		assertThat(response.getTarget()).isEqualTo(target);
		assertThat(response.getAvailableStartAt()).isEqualTo(availableStartAt);
		assertThat(response.getAvailableEndAt()).isEqualTo(availableEndAt);
		assertThat(response.getUsedAt()).isEqualTo(usedAt);
		assertThat(response.getCategoryName()).isEqualTo(categoryName);
		assertThat(response.getBookName()).isEqualTo(bookName);
	}

	@Test
	@DisplayName("기본 생성자 테스트")
	void noArgsConstructorTest() {
		// when
		UserCouponResponse response = new UserCouponResponse();

		// then
		assertThat(response).isNotNull();
		assertThat(response.getId()).isNull();
	}

}
