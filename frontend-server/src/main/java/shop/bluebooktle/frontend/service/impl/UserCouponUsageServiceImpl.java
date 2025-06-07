package shop.bluebooktle.frontend.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.common.dto.book_order.request.UserCouponBookOrderRequest;
import shop.bluebooktle.frontend.repository.UserCouponBookOrderRepository;
import shop.bluebooktle.frontend.service.UserCouponUsageService;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserCouponUsageServiceImpl implements UserCouponUsageService {

	private final UserCouponBookOrderRepository userCouponBookOrderRepository;

	@Override
	public void saveCouponUsage(UserCouponBookOrderRequest request) {
		userCouponBookOrderRepository.saveUsage(request);
		log.info("쿠폰 사용 처리 완료: orderId= {}, userId = {} 사용 쿠폰 수= {}", request.getOrderId(), request.getUserId(),
			request.getUsages().size());
	}

	@Override
	public void deleteCouponUsage(Long orderId, List<Long> userCouponIds) {
		userCouponBookOrderRepository.deleteUsage(orderId, userCouponIds);
		log.info("쿠폰 사용 취소 완료: orderId={}, userCouponIds={}", orderId, userCouponIds);
	}
}
