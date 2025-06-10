package shop.bluebooktle.backend.payment.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record TossApiPaymentCancelSuccessResponse(
	String mId, // 상점 아이디
	String version, // API 버전
	String paymentKey, // 결제의 키 값
	String orderId, // 주문 ID
	String orderName, // 주문명
	String currency, // 통화 (KRW 등)
	String method, // 결제 수단 (e.g., "간편결제")
	BigDecimal totalAmount, // 총 결제 금액
	BigDecimal balanceAmount, // 취소 후 남은 금액
	String status, // 결제 상태 (e.g., CANCELED)
	OffsetDateTime requestedAt, // 결제 요청 시각
	OffsetDateTime approvedAt, // 결제 승인 시각
	boolean useEscrow, // 에스크로 사용 여부
	String lastTransactionKey, // 마지막 거래의 키
	BigDecimal suppliedAmount, // 공급가액
	BigDecimal vat, // 부가세
	BigDecimal taxFreeAmount, // 면세액
	boolean isPartialCancelable, // 부분 취소 가능 여부
	List<Cancel> cancels, // 취소 이력
	EasyPay easyPay, // 간편결제 정보
	String country, // 국가 코드 (e.g., "KR")
	Receipt receipt, // 영수증 정보
	Failure failure // 실패 정보
) {

	/**
	 * 취소 이력 상세 정보
	 */
	public record Cancel(
		BigDecimal cancelAmount, // 취소된 금액
		String cancelReason, // 취소 사유
		BigDecimal taxFreeAmount, // 취소된 금액 중 면세 금액
		BigDecimal taxExemptionAmount, // 과세 제외 금액
		BigDecimal refundableAmount, // 환불 가능한 잔액
		OffsetDateTime canceledAt, // 취소된 시각
		String transactionKey, // 취소 거래 키
		String receiptKey, // 취소 영수증 키
		String cancelStatus // 취소 상태 (e.g., DONE)
	) {
	}

	/**
	 * 간편결제 정보
	 */
	public record EasyPay(
		String provider, // 간편결제 제공자 (e.g., "토스페이")
		BigDecimal amount, // 결제 금액
		BigDecimal discountAmount // 할인 금액
	) {
	}

	/**
	 * 영수증 정보
	 */
	public record Receipt(
		String url // 영수증 URL
	) {
	}

	public record Failure(
		String code, // 오류 코드
		String message // 오류 메시지
	) {
	}
}