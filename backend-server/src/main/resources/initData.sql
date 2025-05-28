INSERT INTO delivery_rule
VALUES (1, '기본 배송 정책', 30000.00, 5000.00, now());

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