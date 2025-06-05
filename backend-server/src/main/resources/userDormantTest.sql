INSERT INTO `users` (`membership_id`, `login_id`, `password`, `name`, `email`, `nickname`, `birth`, `phone_number`,
                     `point_balance`, `provider`, `type`, `status`, `last_login_at`, `created_at`, `deleted_at`)
VALUES (1, 'testuser1', '$2a$10$abcdefghijklmnopqrstuv', '김테스트', 'test1@example.com', '테스터1', '19900101', '01012345671',
        1000.00, 'BLUEBOOKTLE', 'USER', 'ACTIVE', NOW(), NOW(), NULL),
       (1, 'dormantuser1', '$2a$10$abcdefghijklmnopqrstuv', '박휴면', 'dormant1@example.com', '휴면테스터1', '19850315',
        '01012345672',
        500.00, 'BLUEBOOKTLE', 'USER', 'ACTIVE', DATE_SUB(NOW(), INTERVAL 4 MONTH), NOW(), NULL),
       (2, 'adminuser1', '$2a$10$abcdefghijklmnopqrstuv', '이관리', 'admin1@example.com', '관리자1', '19921105',
        '01012345673',
        0.00, 'BLUEBOOKTLE', 'ADMIN', 'ACTIVE', NOW(), NOW(), NULL),
       (1, 'paycouser1', '$2a$10$abcdefghijklmnopqrstuv', '최페이코', 'payco1@example.com', '페이코연동', '20000720',
        '01012345674',
        100.50, 'PAYCO', 'USER', 'ACTIVE', DATE_SUB(NOW(), INTERVAL 1 MONTH), NOW(), NULL),
       (1, 'withdrawnuser1', '$2a$10$abcdefghijklmnopqrstuv', '강탈퇴', 'withdrawn1@example.com', '탈퇴한유저1', '19950505',
        '01012345675',
        0.00, 'BLUEBOOKTLE', 'USER', 'WITHDRAWN', DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 2 MONTH),
        NOW());

-- 추가 데이터 예시 (휴면 대상이 될 수 있는 사용자)
INSERT INTO `users` (`membership_id`, `login_id`, `password`, `name`, `email`, `nickname`, `birth`, `phone_number`,
                     `point_balance`, `provider`, `type`, `status`, `last_login_at`, `created_at`, `deleted_at`)
VALUES (1, 'testuser2', '$2a$10$abcdefghijklmnopqrstuv', '정활성', 'test2@example.com', '활성사용자2', '19930220',
        '01022345671',
        2500.00, 'BLUEBOOKTLE', 'USER', 'ACTIVE', DATE_SUB(NOW(), INTERVAL 2 MONTH), NOW(), NULL),
       (2, 'testuser3', '$2a$10$abcdefghijklmnopqrstuv', '조오래전', 'test3@example.com', '휴면임박3', '19880810',
        '01032345671',
        120.00, 'BLUEBOOKTLE', 'USER', 'ACTIVE',
        DATE_SUB(DATE_SUB(NOW(), INTERVAL 3 MONTH), INTERVAL 1 DAY), -- 수정된 부분
        NOW(),
        NULL);