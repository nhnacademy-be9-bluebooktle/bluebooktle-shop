-- src/main/resources/data.sql

-- MembershipLevel 데이터 삽입 (MERGE INTO 사용)
MERGE INTO membership_level (membership_id, name, rate, min_net_spent, max_net_spent, created_at, deleted_at)
    KEY (name) -- 중복 검사 기준 컬럼 (또는 KEY(membership_id) 사용 가능)
    VALUES (1, '일반', 1, 0.00, 99999.99, NOW(), NULL);

MERGE INTO membership_level (membership_id, name, rate, min_net_spent, max_net_spent, created_at, deleted_at)
    KEY (name)
    VALUES (2, '로얄', 2, 100000.00, 199999.99, NOW(), NULL);

MERGE INTO membership_level (membership_id, name, rate, min_net_spent, max_net_spent, created_at, deleted_at)
    KEY (name)
    VALUES (3, '골드', 3, 200000.00, 299999.99, NOW(), NULL); -- 임의의 rate 값, 확인 필요

MERGE INTO membership_level (membership_id, name, rate, min_net_spent, max_net_spent, created_at, deleted_at)
    KEY (name)
    VALUES (4, '플래티넘', 4, 300000.00, 99999999.99, NOW(), NULL); -- 임의의 rate 값, 확인 필요

MERGE INTO membership_level (membership_id, name, rate, min_net_spent, max_net_spent, created_at, deleted_at)
    KEY (name)
    VALUES (5, '일반회원', 1, 0.00, 99999.99, NOW(), NULL);