package shop.bluebooktle.backend.payment.event.type;

import java.math.BigDecimal;

public record PaymentPointEarnEvent(Long userId, BigDecimal amount, Long paymentId) {
}