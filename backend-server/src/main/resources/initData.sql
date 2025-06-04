INSERT INTO delivery_rule
VALUES (1, '기본 배송', 30000.00, 5000, 'ALL', true, now(), null);

INSERT INTO delivery_rule
VALUES (2, '제주도', null, 6000, 'JEJU', true, now(), null);

INSERT INTO delivery_rule
VALUES (3, '도서산간', null, 10000, 'MOUNTAINOUS_AREA', true, now(), null);

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

INSERT INTO packaging_option
VALUES (1, '프리미엄 포장', 3000, now(), null);

INSERT INTO packaging_option
VALUES (2, '반짝이 포장', 1000, now(), null);

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

INSERT INTO category (category_id,
                      parent_category_id,
                      name,
                      category_path,
                      created_at,
                      deleted_at)
VALUES (1, NULL, '문학', '/1', '2025-05-25 17:48:16', NULL),
       (2, 1, '소설', '/1/2', '2025-05-25 17:48:16', NULL),
       (3, 1, '시·희곡', '/1/3', '2025-05-25 17:48:33', NULL),
       (4, 1, '에세이·전집', '/1/4', '2025-05-25 17:48:51', NULL),
       (5, 2, '일반 소설', '/1/2/5', '2025-05-25 17:49:04', NULL),
       (6, 2, '장르 소설', '/1/2/6', '2025-05-25 17:49:14', NULL),
       (7, 5, '국내 현대 소설', '/1/2/5/7', '2025-05-25 17:49:22', NULL),
       (8, 5, '해외 현대 소설', '/1/2/5/8', '2025-05-25 17:49:30', NULL),
       (9, 6, '판타지·무협·라이트노벨', '/1/2/6/9', '2025-05-25 17:49:48', NULL),
       (10, 6, '스릴러·추리·공포', '/1/2/6/10', '2025-05-25 17:50:02', NULL),
       (11, 6, '로맨스', '/1/2/5/7/11', '2025-05-25 17:50:07', NULL),
       (12, 3, '시', '/1/3/12', '2025-05-25 17:50:15', NULL),
       (13, 3, '희곡', '/1/3/13', '2025-05-25 17:50:22', NULL),
       (14, 4, '에세이·산문', '/1/4/14', '2025-05-25 17:50:46', NULL),
       (15, 4, '전집·문고', '/1/4/15', '2025-05-25 17:50:55', NULL),
       (16, NULL, '교양', '/16', '2025-05-25 17:51:27', NULL),
       (17, 16, '인문학', '/16/17', '2025-05-25 17:51:27', NULL),
       (18, 16, '사회과학', '/16/18', '2025-05-25 17:51:37', NULL),
       (19, 16, '자연과학', '/16/19', '2025-05-25 17:51:56', NULL),
       (20, 16, '역사·종교', '/16/20', '2025-05-25 17:52:11', NULL),
       (21, 17, '철학·사상', '/16/17/21', '2025-05-25 17:52:55', NULL),
       (22, 17, '문명·문화', '/16/17/22', '2025-05-25 17:53:17', NULL),
       (23, 18, '정치·국제관계', '/16/18/23', '2025-05-25 17:53:32', NULL),
       (24, 18, '사회학·인류학', '/16/18/24', '2025-05-25 17:54:17', NULL),
       (25, 19, '이론 과학 (물리·화학·생물)', '/16/19/25', '2025-05-25 17:54:49', NULL),
       (26, 19, '응용 과학 (공학·의학·IT)', '/16/19/26', '2025-05-25 17:55:08', NULL),
       (27, 20, '한국사', '/16/20/27', '2025-05-25 17:55:17', NULL),
       (28, 20, '세계사', '/16/20/28', '2025-05-25 17:55:25', NULL),
       (29, 20, '종교·역학·명상', '/16/20/29', '2025-05-25 17:55:36', NULL),
       (30, NULL, '비즈니스', '/30', '2025-05-25 17:56:03', NULL),
       (31, 30, '경제·경영', '/30/31', '2025-05-25 17:56:03', NULL),
       (32, 31, '기업경영·마케팅', '/30/31/32', '2025-05-25 17:56:21', NULL),
       (33, 31, '제테크·주식·부동산', '/30/31/33', '2025-05-25 17:56:38', NULL),
       (34, 30, '자기계발', '/30/34', '2025-05-25 17:56:58', NULL),
       (35, 34, '리더십·커리어', '/30/34/35', '2025-05-25 17:57:09', NULL),
       (36, 34, '커뮤니케이션·인간관계', '/30/34/36', '2025-05-25 17:57:21', NULL),
       (37, NULL, '학습', '/37', '2025-05-25 17:57:44', NULL),
       (38, 37, '어학', '/37/38', '2025-05-25 17:57:44', NULL),
       (39, 37, '수험서·자격증', '/37/39', '2025-05-25 17:58:09', NULL),
       (40, 38, '영어 (ELT·토익·토플)', '/37/38/40', '2025-05-25 17:58:31', NULL),
       (41, 38, '제2외국어 (일본어·중국어·기타)', '/37/38/41', '2025-05-25 17:59:19', NULL),
       (42, 37, '실무·전문서', '/37/42', '2025-05-25 17:59:59', NULL),
       (43, 37, '참고서·교재', '/37/43', '2025-05-25 18:00:15', NULL),
       (44, 39, '공무원 수험서', '/37/39/44', '2025-05-25 18:00:39', NULL),
       (45, 39, '국가 자격증', '/37/39/45', '2025-05-25 18:01:05', NULL),
       (46, 45, '회계·세무', '/37/39/45/46', '2025-05-25 18:01:23', NULL),
       (47, 45, '컴퓨터·IT', '/37/39/45/47', '2025-05-25 18:01:44', NULL),
       (48, 45, '기타', '/37/39/45/48', '2025-05-25 18:02:01', NULL),
       (49, 42, '프로그래밍·IT 실무', '/37/42/49', '2025-05-25 18:02:20', NULL),
       (50, 42, '디자인·크리에이티브', '/37/42/50', '2025-05-25 18:02:36', NULL),
       (51, 43, '초등 참고서', '/37/43/51', '2025-05-25 18:02:52', NULL),
       (52, 43, '중·고등 참고서', '/37/43/52', '2025-05-25 18:03:14', NULL),
       (53, 43, '대학교재', '/37/43/53', '2025-05-25 18:03:45', NULL),
       (54, NULL, '라이프', '/54', '2025-05-25 18:03:57', NULL),
       (55, 54, '건강·웰빙', '/54/55', '2025-05-25 18:03:57', NULL),
       (56, 55, '운동·다이어트', '/54/55/56', '2025-05-25 18:04:08', NULL),
       (57, 55, '명상·심리', '/54/55/57', '2025-05-25 18:04:25', NULL),
       (58, 54, '취미·여가', '/54/58', '2025-05-25 18:04:56', NULL),
       (59, 58, '여행·가이드', '/54/58/59', '2025-05-25 18:05:09', NULL),
       (60, 58, '요리·베이킹', '/54/58/60', '2025-05-25 18:05:23', NULL),
       (61, 58, '음악·예술', '/54/58/61', '2025-05-25 18:05:37', NULL),
       (62, 54, '가정·원예', '/54/62', '2025-05-25 18:06:02', NULL),
       (63, 62, '육아·부모', '/54/62/63', '2025-05-25 18:06:23', NULL),
       (64, 62, '인테리어·DIY', '/54/62/64', '2025-05-25 18:06:39', NULL)
;