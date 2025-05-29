INSERT INTO delivery_rule
VALUES (1, '기본 배송 정책', 30000.00, 5000.00, 'ALL', true, now(), null);

INSERT INTO order_state
VALUES (1, 'PENDING', now(), null);
INSERT INTO order_state
VALUES (2, 'SHIPPING', now(), null);
INSERT INTO order_state
VALUES (3, 'COMPLETED', now(), null);
INSERT INTO order_state
VALUES (4, 'RETURNED', now(), null);
INSERT INTO order_state
VALUES (5, 'CANCELED', now(), null);



INSERT INTO point_source_type
VALUES (1, 'EARN', '로그인 적립', now(), null);

INSERT INTO point_source_type
VALUES (2, 'EARN', '회원 가입', now(), null);

INSERT INTO point_source_type
VALUES (3, 'EARN', '리뷰 적립', now(), null);

INSERT INTO point_source_type
VALUES (4, 'EARN', '결제 적립', now(), null);

INSERT INTO point_source_type
VALUES (5, 'USE', '결제 사용', now(), null);

INSERT INTO point_policy
VALUES (1, 1, 'AMOUNT', 500, true, now(), null);

INSERT INTO point_policy
VALUES (2, 2, 'AMOUNT', 5000, true, now(), null);

INSERT INTO point_policy
VALUES (3, 3, 'AMOUNT', 500, true, now(), null);

INSERT INTO point_policy
VALUES (4, 4, 'PERCENTAGE', 10, true, now(), null);


-- 일반 등급 (0원 이상 ~ 10만원 미만)
INSERT INTO membership_level (name, rate, min_net_spent, max_net_spent)
VALUES ('일반', 1, 0.00, 99999.99);

-- 로얄 등급 (10만원 이상 ~ 20만원 미만)
INSERT INTO membership_level (name, rate, min_net_spent, max_net_spent)
VALUES ('로얄', 2, 100000.00, 199999.99);

-- 골드 등급 (20만원 이상 ~ 30만원 미만) - 적립률 2%로 가정
INSERT INTO membership_level (name, rate, min_net_spent, max_net_spent)
VALUES ('골드', 2, 200000.00, 299999.99);

-- 플래티넘 등급 (30만원 이상)
INSERT INTO membership_level (name, rate, min_net_spent, max_net_spent)
VALUES ('플래티넘', 3, 300000.00, 99999999.99);

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