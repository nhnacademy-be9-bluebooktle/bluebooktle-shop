package shop.bluebooktle.backend.payment.gateway.impl;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.payment.client.TossPaymentClient;
import shop.bluebooktle.backend.payment.dto.request.TossApiPaymentConfirmRequest;
import shop.bluebooktle.backend.payment.dto.response.GenericPaymentConfirmResponse;
import shop.bluebooktle.backend.payment.dto.response.GenericPaymentConfirmResponse.PaymentStatus;
import shop.bluebooktle.backend.payment.dto.response.TossApiPaymentConfirmSuccessResponse;
import shop.bluebooktle.backend.payment.gateway.PaymentGateway;
import shop.bluebooktle.common.dto.payment.request.PaymentConfirmRequest;

@Component("TOSS")
@RequiredArgsConstructor
public class TossPaymentGatewayImpl implements PaymentGateway {

	@Value("${toss.secret-key}")
	private String secretKey;

	private static final Logger log = LoggerFactory.getLogger(TossPaymentGatewayImpl.class);
	private final TossPaymentClient tossPaymentClient;

	@Override
	public String getGatewayName() {
		return "TOSS";
	}

	@Override
	public GenericPaymentConfirmResponse confirmPayment(PaymentConfirmRequest commonRequest) {
		TossApiPaymentConfirmRequest tossSpecificRequest = new TossApiPaymentConfirmRequest(
			commonRequest.paymentKey(),
			commonRequest.orderId(),
			commonRequest.amount()
		);

		try {
			String authorization = "Basic " + Base64.getEncoder().encodeToString((secretKey + ":").getBytes());
			TossApiPaymentConfirmSuccessResponse tossResponse = tossPaymentClient.confirmPayment(
				authorization,
				tossSpecificRequest
			);
			BigDecimal confirmedAmount = tossResponse.totalAmount() != null
				? BigDecimal.valueOf(tossResponse.totalAmount())
				: BigDecimal.ZERO;

			PaymentStatus paymentStatus = "DONE".equalsIgnoreCase(tossResponse.status())
				? PaymentStatus.SUCCESS
				: PaymentStatus.FAILURE;

			Map<String, Object> additionalData = new HashMap<>();
			additionalData.put("tossPaymentKey", tossResponse.paymentKey());
			additionalData.put("tossPaymentStatus", tossResponse.status());

			return new GenericPaymentConfirmResponse(
				tossResponse.paymentKey(),
				tossResponse.orderId(),
				confirmedAmount,
				paymentStatus,
				tossResponse.method(),
				additionalData
			);

		} catch (FeignException e) {
			Map<String, Object> errorAdditionalData = new HashMap<>();
			errorAdditionalData.put("tossApiErrorStatus", e.status());
			errorAdditionalData.put("tossApiErrorMessage", e.contentUTF8());
			return new GenericPaymentConfirmResponse(
				null,
				commonRequest.orderId(),
				BigDecimal.valueOf(commonRequest.amount()),
				PaymentStatus.FAILURE,
				"TOSS_API_ERROR",
				errorAdditionalData
			);
		} catch (Exception e) {
			log.error("TossPaymentGateway 실행 중 에러 발생: {}", commonRequest.orderId(), e);
			return new GenericPaymentConfirmResponse(
				null,
				commonRequest.orderId(),
				BigDecimal.valueOf(commonRequest.amount()),
				PaymentStatus.FAILURE,
				"UNKNOWN_ERROR",
				Map.of("errorMessage", e.getMessage())
			);
		}
	}
}