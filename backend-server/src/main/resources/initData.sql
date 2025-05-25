-- INSERT INTO deliver_rule
-- VALUES (1, '기본 배송 정책', 30000.00, 5000.00, now());
-- INSERT INTO order_state
-- VALUES (1, 'PENDING', now(), null);
-- INSERT INTO order_state
-- VALUES (2, 'SHIPPING', now(), null);
-- INSERT INTO order_state
-- VALUES (3, 'COMPLETED', now(), null);
-- INSERT INTO order_state
-- VALUES (4, 'RETURNED', now(), null);
-- INSERT INTO order_state
-- VALUES (5, 'CANCELED', now(), null);

-- 생일 쿠폰 정책 생성
INSERT INTO coupon_type (coupon_type_id, name, target, minimum_payment, created_at)
VALUES (1, '생일 쿠폰', 'ORDER', 50000.00, CURRENT_TIMESTAMP);
INSERT INTO absolute_coupon (coupon_type_id, discount_price, created_at)
VALUES (1, 10000.00, CURRENT_TIMESTAMP);
-- 생일 쿠폰 생성
INSERT INTO coupon (coupon_id, coupon_type_id, name, created_at)
VALUES (1, 1, '생일 축하 쿠폰', CURRENT_TIMESTAMP);

-- 웰컴 쿠폰 정책 생성
INSERT INTO coupon_type (coupon_type_id, name, target, minimum_payment, created_at)
VALUES (2, '웰컴 쿠폰', 'ORDER', 50000.00, CURRENT_TIMESTAMP);
INSERT INTO absolute_coupon (coupon_type_id, discount_price, created_at)
VALUES (2, 10000.00, CURRENT_TIMESTAMP);
-- 웰컴 쿠폰 생성
INSERT INTO coupon (coupon_id, coupon_type_id, name, created_at)
VALUES (2, 2, '회원가입 축하 쿠폰', CURRENT_TIMESTAMP);