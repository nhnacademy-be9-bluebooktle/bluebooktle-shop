package shop.bluebooktle.backend.order.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import shop.bluebooktle.backend.book_order.entity.BookOrder;
import shop.bluebooktle.backend.book_order.entity.UserCouponBookOrder;
import shop.bluebooktle.backend.payment.entity.Payment;
import shop.bluebooktle.common.converter.ProfileAwareStringCryptoConverter;
import shop.bluebooktle.common.entity.BaseEntity;
import shop.bluebooktle.common.entity.auth.User;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders")
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString(exclude = {"orderState", "deliveryRule", "user", "bookOrders", "userCouponBookOrders", "payment"})
@SQLDelete(sql = "UPDATE orders SET deleted_at = CURRENT_TIMESTAMP WHERE order_id = ?")
@SQLRestriction("deleted_at IS NULL")
public class Order extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "order_id")
	private Long id;

	@Setter(AccessLevel.PRIVATE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_state_id", nullable = false)
	private OrderState orderState;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "delivery_rule_id", nullable = false)
	private DeliveryRule deliveryRule;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@Column(name = "order_name", nullable = false)
	private String orderName;

	@Column(name = "requested_delivery_date")
	private LocalDateTime requestedDeliveryDate;

	@Column(name = "shipped_at")
	private LocalDateTime shippedAt;

	@Column(name = "delivery_fee", nullable = false, precision = 10, scale = 2)
	private BigDecimal deliveryFee;

	@Convert(converter = ProfileAwareStringCryptoConverter.class)
	@Column(name = "orderer_name", nullable = false, length = 150)
	private String ordererName;

	@Convert(converter = ProfileAwareStringCryptoConverter.class)
	@Column(name = "orderer_email", nullable = false, length = 255)
	private String ordererEmail;

	@Convert(converter = ProfileAwareStringCryptoConverter.class)
	@Column(name = "orderer_phone_number", nullable = false, length = 100)
	private String ordererPhoneNumber;

	@Convert(converter = ProfileAwareStringCryptoConverter.class)
	@Column(name = "receiver_name", nullable = false, length = 150)
	private String receiverName;

	@Convert(converter = ProfileAwareStringCryptoConverter.class)
	@Column(name = "receiver_email", nullable = false, length = 200)
	private String receiverEmail;

	@Convert(converter = ProfileAwareStringCryptoConverter.class)
	@Column(name = "receiver_phone_number", nullable = false, length = 100)
	private String receiverPhoneNumber;

	@Column(name = "address", nullable = false, length = 255)
	private String address;

	@Column(name = "detail_address", nullable = false, length = 255)
	private String detailAddress;

	@Column(name = "postal_code", nullable = false, length = 5)
	private String postalCode;

	@Column(name = "tracking_number", length = 14)
	private String trackingNumber;

	@Column(name = "order_key", unique = true, nullable = false)
	private String orderKey;

	@Column(name = "coupon_discount_amount", precision = 10, scale = 2)
	private BigDecimal couponDiscountAmount;

	@Column(name = "point_discount_amount", precision = 10, scale = 2)
	private BigDecimal pointUseAmount;

	@Column(name = "original_amount", precision = 10, scale = 2, nullable = false)
	private BigDecimal originalAmount;

	@Column(name = "sale_discount_amount", precision = 10, scale = 2)
	private BigDecimal saleDiscountAmount;

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@BatchSize(size = 10)
	private List<BookOrder> bookOrders = new ArrayList<>();

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@BatchSize(size = 10)
	private List<UserCouponBookOrder> userCouponBookOrders = new ArrayList<>();

	@OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	private Payment payment;

	@Builder
	public Order(OrderState orderState, DeliveryRule deliveryRule, User user,
		String orderName, LocalDateTime requestedDeliveryDate, LocalDateTime shippedAt, BigDecimal deliveryFee,
		String ordererName, String ordererPhoneNumber, String receiverName, String receiverPhoneNumber,
		String address, String detailAddress, String postalCode, String trackingNumber, String orderKey,
		String ordererEmail, String receiverEmail, BigDecimal couponDiscountAmount, BigDecimal pointUseAmount,
		BigDecimal originalAmount, BigDecimal saleDiscountAmount) {
		this.orderState = orderState;
		this.deliveryRule = deliveryRule;
		this.user = user;
		this.orderName = orderName;
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
		this.ordererEmail = ordererEmail;
		this.receiverEmail = receiverEmail;
		this.couponDiscountAmount = couponDiscountAmount;
		this.pointUseAmount = pointUseAmount;
		this.originalAmount = originalAmount;
		this.saleDiscountAmount = saleDiscountAmount;
	}

	public void changeOrderState(OrderState newState) {
		this.orderState = newState;
	}

	public void changeShippedAt(LocalDateTime newShippedAt) {
		this.shippedAt = newShippedAt;
	}
}