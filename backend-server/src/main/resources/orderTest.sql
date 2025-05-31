-- 기존 데이터 INSERT 문들은 여기에 그대로 위치합니다 (1번부터 9번까지) --
-- ... (생략) ...

-- 1. 테스트용 도서 (Books) - ID (101, 102) 사용 (기존과 동일)
INSERT INTO `book` (`book_id`, `title`, `description`, `isbn`, `publish_date`, `created_at`)
VALUES (101, '코어 자바스크립트 Deep Dive', '자바스크립트의 핵심 원리를 파헤쳐보자.', '9791100001018', NOW(), NOW()),
       (102, 'HTTP 완벽 가이드 초판', 'HTTP 프로토콜의 모든 것, 웹 통신의 기본.', '9791100001025', NOW(), NOW())
ON DUPLICATE KEY UPDATE title=VALUES(title);

-- 2. 테스트용 포장 옵션 (PackagingOptions) - ID (101, 102) 사용 (기존과 동일)
INSERT INTO `packaging_option` (`package_id`, `name`, `price`, `created_at`)
VALUES (101, '친환경 고급 포장', 2500.00, NOW()),
       (102, '스페셜 선물 포장 (리본)', 3000.00, NOW())
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- 3. BOOK 타겟 정액 쿠폰 (ID 103 - 코어JS 할인) - 기존 유지
INSERT INTO `coupon_type` (`coupon_type_id`, `name`, `target`, `minimum_payment`, `created_at`)
VALUES (103, '코어JS 전용 2000원 할인', 'BOOK', 20000.00, NOW())
ON DUPLICATE KEY UPDATE name=VALUES(name);
INSERT INTO `absolute_coupon` (`coupon_type_id`, `discount_price`, `created_at`)
VALUES (103, 2000.00, NOW())
ON DUPLICATE KEY UPDATE discount_price=VALUES(discount_price);
INSERT INTO `coupon` (`coupon_id`, `coupon_type_id`, `name`, `created_at`)
VALUES (103, 103, '코어 자바스크립트 특별 할인', NOW())
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- 4. ORDER 타겟 정률 쿠폰 (ID 104 - VIP 15% 할인) - 쿠폰 자체는 정의해두되, 이번 주문에는 적용 안 함 (다른 테스트용)
INSERT INTO `coupon_type` (`coupon_type_id`, `name`, `target`, `minimum_payment`, `created_at`)
VALUES (104, '정기 구독 15% 할인', 'ORDER', 30000.00, NOW())
ON DUPLICATE KEY UPDATE name=VALUES(name);
INSERT INTO `relative_coupon` (`coupon_type_id`, `maximum_discount_price`, `discount_percent`, `created_at`)
VALUES (104, 5000.00, 15, NOW())
ON DUPLICATE KEY UPDATE maximum_discount_price=VALUES(maximum_discount_price),
                        discount_percent=VALUES(discount_percent);
INSERT INTO `coupon` (`coupon_id`, `coupon_type_id`, `name`, `created_at`)
VALUES (104, 104, 'VIP 고객님께 드리는 15% 주문 할인', NOW())
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- 5. 새로운 BOOK 타겟 정률(비율) 할인 쿠폰 추가 (ID 105 사용)
INSERT INTO `coupon_type` (`coupon_type_id`, `name`, `target`, `minimum_payment`, `created_at`)
VALUES (105, 'HTTP 완벽가이드 특별 20% 할인', 'BOOK', 30000.00, NOW())
ON DUPLICATE KEY UPDATE name=VALUES(name);
INSERT INTO `relative_coupon` (`coupon_type_id`, `maximum_discount_price`, `discount_percent`, `created_at`)
VALUES (105, 10000.00, 20, NOW())
ON DUPLICATE KEY UPDATE maximum_discount_price=VALUES(maximum_discount_price),
                        discount_percent=VALUES(discount_percent);
INSERT INTO `coupon` (`coupon_id`, `coupon_type_id`, `name`, `created_at`)
VALUES (105, 105, 'HTTP 완벽가이드 정독 응원', NOW())
ON DUPLICATE KEY UPDATE name=VALUES(name);


-- 6. 사용자 쿠폰 할당 (UserCoupons)
INSERT INTO `user_coupon` (`user_coupon_id`, `coupon_id`, `user_id`, `available_start_at`, `available_end_at`,
                           `created_at`, `used_at`, `deleted_at`)
VALUES (101, 1, 1, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), NOW(), NULL, NULL)
ON DUPLICATE KEY UPDATE coupon_id=VALUES(coupon_id),
                        user_id=VALUES(user_id);
INSERT INTO `user_coupon` (`user_coupon_id`, `coupon_id`, `user_id`, `available_start_at`, `available_end_at`,
                           `created_at`, `used_at`, `deleted_at`)
VALUES (102, 103, 1, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), NOW(), NULL, NULL)
ON DUPLICATE KEY UPDATE coupon_id=VALUES(coupon_id),
                        user_id=VALUES(user_id);
INSERT INTO `user_coupon` (`user_coupon_id`, `coupon_id`, `user_id`, `available_start_at`, `available_end_at`,
                           `created_at`, `used_at`, `deleted_at`)
VALUES (103, 104, 1, NOW(), DATE_ADD(NOW(), INTERVAL 60 DAY), NOW(), NULL, NULL)
ON DUPLICATE KEY UPDATE coupon_id=VALUES(coupon_id),
                        user_id=VALUES(user_id);
INSERT INTO `user_coupon` (`user_coupon_id`, `coupon_id`, `user_id`, `available_start_at`, `available_end_at`,
                           `created_at`, `used_at`, `deleted_at`)
VALUES (104, 105, 1, NOW(), DATE_ADD(NOW(), INTERVAL 45 DAY), NOW(), NULL, NULL)
ON DUPLICATE KEY UPDATE coupon_id=VALUES(coupon_id),
                        user_id=VALUES(user_id);


-- 7. 주문 생성 (Order) - order_id (101) 사용
INSERT INTO `orders` (`order_id`, `user_id`, `order_state_id`, `delivery_rule_id`, `order_name`,
                      `requested_delivery_date`, `delivery_fee`, `orderer_name`, `orderer_phone_number`,
                      `receiver_name`, `receiver_phone_number`, `address`, `detail_address`, `postal_code`,
                      `tracking_number`, `created_at`, `deleted_at`, `order_key`)
VALUES (101, 1, 1, 1, '코어 자바스크립트 Deep Dive 외 1건',
        DATE_ADD(NOW(), INTERVAL 5 DAY), 5000.00, '테스트유저일', '01011112222',
        '수령인A', '01022223333', '서울특별시 서초구 신반포로 321', '101동 1001호 (반포자이)', '06543',
        'TEMPTRACK12345', NOW(), NULL, UUID())
ON DUPLICATE KEY UPDATE order_name=VALUES(order_name);

-- 8. 주문 상품 목록 (BookOrder) - book_order_id (101, 102) 사용
INSERT INTO `book_order` (`book_order_id`, `order_id`, `book_id`, `quantity`, `price`, `created_at`, `deleted_at`)
VALUES (101, 101, 101, 1, 32000.00, NOW(), NULL),
       (102, 101, 102, 2, 45000.00, NOW(), NULL)
ON DUPLICATE KEY UPDATE quantity=VALUES(quantity);

-- 9. 포장 주문 내역 (OrderPackaging)
INSERT INTO `order_packaging` (`order_packaging_id`, `book_order_id`, `package_id`, `quantity`, `created_at`,
                               `deleted_at`)
VALUES (101, 101, 101, 1, NOW(), NULL),
       (102, 102, 102, 1, NOW(), NULL)
ON DUPLICATE KEY UPDATE quantity=VALUES(quantity);

-- 10. 쿠폰 사용 내역 (UserCouponBookOrder) - 수정된 시나리오 반영
-- '생일 축하 쿠폰'(user_coupon_id=101, ORDER 타겟)을 전체 주문(order_id=101)에 적용 (book_order_id는 NULL로 설정)
INSERT INTO `user_coupon_book_order` (`user_coupon_book_order_id`, `user_coupon_id`, `book_order_id`, `order_id`,
                                      `user_id`)
VALUES (101, 101, NULL, 101, 1) -- << 여기! book_order_id를 NULL로 변경했습니다.
ON DUPLICATE KEY UPDATE user_coupon_id=VALUES(user_coupon_id),
                        book_order_id=VALUES(book_order_id);
-- ON DUPLICATE 절에도 반영

-- '코어 자바스크립트 특별 할인 쿠폰'(user_coupon_id=102, BOOK 타겟 정액)을 book_order_id=101에 적용
INSERT INTO `user_coupon_book_order` (`user_coupon_book_order_id`, `user_coupon_id`, `book_order_id`, `order_id`,
                                      `user_id`)
VALUES (102, 102, 101, 101, 1)
ON DUPLICATE KEY UPDATE user_coupon_id=VALUES(user_coupon_id);

-- 새로 추가한 'HTTP 완벽가이드 정독 응원 쿠폰'(user_coupon_id=104, BOOK 타겟 정률)을
-- 'HTTP 완벽 가이드 초판'(book_order_id=102)에 적용 (user_coupon_book_order_id=104 사용)
INSERT INTO `user_coupon_book_order` (`user_coupon_book_order_id`, `user_coupon_id`, `book_order_id`, `order_id`,
                                      `user_id`)
VALUES (104, 104, 102, 101, 1)
ON DUPLICATE KEY UPDATE user_coupon_id=VALUES(user_coupon_id);