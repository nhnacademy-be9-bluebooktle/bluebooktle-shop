package shop.bluebooktle.backend.point.service;

public interface PaymentPointHistoryService {
	void save(Long paymentId, Long pointHistoryId);
}