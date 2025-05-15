INSERT INTO deliver_rule
VALUES (1, '기본 배송 정책', 30000.00, 5000.00, now())

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