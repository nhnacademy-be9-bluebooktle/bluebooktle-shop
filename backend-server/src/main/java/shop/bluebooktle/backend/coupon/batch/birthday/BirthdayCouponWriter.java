package shop.bluebooktle.backend.coupon.batch.birthday;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.coupon.entity.UserCoupon;
import shop.bluebooktle.backend.coupon.repository.UserCouponRepository;

@Component
@RequiredArgsConstructor
public class BirthdayCouponWriter implements ItemWriter<UserCoupon> {

	private final UserCouponRepository userCouponRepository;

	@Override
	public void write(Chunk<? extends UserCoupon> chunk) {
		userCouponRepository.saveAll(chunk.getItems());
	}
}