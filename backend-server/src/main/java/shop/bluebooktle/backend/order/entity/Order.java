package shop.bluebooktle.backend.order.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import shop.bluebooktle.common.entity.BaseEntity;
import shop.bluebooktle.common.entity.auth.User;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders")
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString(exclude = {"orderState", "deliveryRule"})
@SQLDelete(sql = "UPDATE orders SET deleted_at = CURRENT_TIMESTAMP WHERE order_id = ?")
@SQLRestriction("deleted_at IS NULL")
public class Order extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "order_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_state_id", nullable = false)
	private OrderState orderState;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "delivery_rule_id", nullable = false)
	private DeliveryRule deliveryRule;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "order_name", nullable = false)
	private String orderName;

	@Column(name = "requested_delivery_date", nullable = false)
	private LocalDateTime requestedDeliveryDate;

	@Column(name = "shipped_at")
	private LocalDateTime shippedAt;

	@Column(name = "delivery_fee", nullable = false, precision = 10, scale = 2)
	private BigDecimal deliveryFee;

	@Column(name = "orderer_name", nullable = false, length = 20)
	private String ordererName;

	@Column(name = "orderer_phone_number", nullable = false, length = 11)
	private String ordererPhoneNumber;

	@Column(name = "receiver_name", nullable = false, length = 20)
	private String receiverName;

	@Column(name = "receiver_phone_number", nullable = false, length = 11)
	private String receiverPhoneNumber;

	@Column(name = "address", nullable = false, length = 255)
	private String address;

	@Column(name = "detail_address", nullable = false, length = 255)
	private String detailAddress;

	@Column(name = "postal_code", nullable = false, length = 5)
	private String postalCode;

	@Column(name = "tracking_number", nullable = false, length = 14)
	private String trackingNumber;

	@Column(name = "order_key")
	private String orderKey;

	@Builder
	public Order(OrderState orderState, DeliveryRule deliveryRule, User user, LocalDateTime orderDate,
		LocalDateTime requestedDeliveryDate, LocalDateTime shippedAt, BigDecimal deliveryFee, String ordererName,
		String ordererPhoneNumber, String receiverName, String receiverPhoneNumber, String address,
		String detailAddress, String postalCode, String trackingNumber, String orderName, String orderKey) {
		this.orderState = orderState;
		this.deliveryRule = deliveryRule;
		this.user = user;
		this.ordererName = orderName;
		this.requestedDeliveryDate = requestedDeliveryDate;
		this.shippedAt = shippedAt;
		this.deliveryFee = deliveryFee;
		this.ordererName = ordererName;
		this.ordererPhoneNumber = ordererPhoneNumber;
		this.receiverName = receiverName;
		this.receiverPhoneNumber = receiverPhoneNumber;
		this.address = address;
		this.detailAddress = detailAddress;
		this.postalCode = postalCode;
		this.trackingNumber = trackingNumber;
		this.orderKey = orderKey;
	}

	public void changeOrderState(OrderState newState) {
		this.orderState = newState;
	}
}