// 3. Service Interface
package shop.bluebooktle.backend.payment.service;

import shop.bluebooktle.backend.payment.dto.request.PaymentDetailRequest;
import shop.bluebooktle.backend.payment.dto.response.PaymentDetailResponse;

public interface PaymentDetailService {
	void create(PaymentDetailRequest req);

	void delete(Long id);

	PaymentDetailResponse get(Long id);
}
