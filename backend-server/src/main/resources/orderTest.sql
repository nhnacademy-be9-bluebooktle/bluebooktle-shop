-- 이 스크립트를 실행하기 전에 다음의 기본 데이터가 있다고 가정합니다:
-- 1. `users` 테이블에 `user_id = 1`인 사용자가 존재해야 합니다.
-- 2. `membership_level` 테이블에 `membership_id` (users.membership_id 참조)가 존재해야 합니다.
-- 3. `order_state` 테이블에 `order_state_id = 1`인 상태가 존재해야 합니다.
-- 4. `delivery_rule` 테이블에 `delivery_rule_id = 1`인 규칙이 존재해야 합니다.
-- 5. `coupon_type` 테이블에 `coupon_id = 1` (user_coupon의 coupon_id=1 참조)에 해당하는 ORDER 타겟 쿠폰 타입이 존재해야 합니다.
-- 6. `coupon` 테이블에 `coupon_id = 1` ('생일 축하 쿠폰' 등으로 가정)이 존재해야 합니다.
-- 이 외에도 FK로 연결된 다른 테이블의 기본 데이터가 필요할 수 있습니다.

-- 1. 테스트용 도서 (Books) - ID (101, 102) 사용 (기존과 동일)
INSERT INTO `book` (`book_id`, `title`, `description`, `isbn`, `publish_date`, `created_at`)
VALUES (101, '코어 자바스크립트 Deep Dive', '자바스크립트의 핵심 원리를 파헤쳐보자.', '9791100001018', NOW(), NOW()),
       (102, 'HTTP 완벽 가이드 초판', 'HTTP 프로토콜의 모든 것, 웹 통신의 기본.', '9791100001025', NOW(), NOW())
ON DUPLICATE KEY UPDATE title=VALUES(title),
                        description=VALUES(description),
                        isbn=VALUES(isbn),
                        publish_date=VALUES(publish_date);

-- 2. 테스트용 포장 옵션 (PackagingOptions) - ID (101, 102) 사용 (기존과 동일)
INSERT INTO `packaging_option` (`package_id`, `name`, `price`, `created_at`)
VALUES (101, '친환경 고급 포장', 2500.00, NOW()),
       (102, '스페셜 선물 포장 (리본)', 3000.00, NOW())
ON DUPLICATE KEY UPDATE name=VALUES(name),
                        price=VALUES(price);

-- 3. BOOK 타겟 정액 쿠폰 (ID 103 - 코어JS 할인) - 기존 유지
INSERT INTO `coupon_type` (`coupon_type_id`, `name`, `target`, `minimum_payment`, `created_at`)
VALUES (103, '코어JS 전용 2000원 할인', 'BOOK', 20000.00, NOW())
ON DUPLICATE KEY UPDATE name=VALUES(name),
                        target=VALUES(target),
                        minimum_payment=VALUES(minimum_payment);
INSERT INTO `absolute_coupon` (`coupon_type_id`, `discount_price`, `created_at`)
VALUES (103, 2000.00, NOW())
ON DUPLICATE KEY UPDATE discount_price=VALUES(discount_price);
INSERT INTO `coupon` (`coupon_id`, `coupon_type_id`, `name`, `created_at`)
VALUES (103, 103, '코어 자바스크립트 특별 할인', NOW())
ON DUPLICATE KEY UPDATE coupon_type_id=VALUES(coupon_type_id),
                        name=VALUES(name);

-- 4. ORDER 타겟 정률 쿠폰 (ID 104 - VIP 15% 할인) - 쿠폰 자체는 정의해두되, 이번 주문에는 적용 안 함 (다른 테스트용)
INSERT INTO `coupon_type` (`coupon_type_id`, `name`, `target`, `minimum_payment`, `created_at`)
VALUES (104, '정기 구독 15% 할인', 'ORDER', 30000.00, NOW())
ON DUPLICATE KEY UPDATE name=VALUES(name),
                        target=VALUES(target),
                        minimum_payment=VALUES(minimum_payment);
INSERT INTO `relative_coupon` (`coupon_type_id`, `maximum_discount_price`, `discount_percent`, `created_at`)
VALUES (104, 5000.00, 15, NOW())
ON DUPLICATE KEY UPDATE maximum_discount_price=VALUES(maximum_discount_price),
                        discount_percent=VALUES(discount_percent);
INSERT INTO `coupon` (`coupon_id`, `coupon_type_id`, `name`, `created_at`)
VALUES (104, 104, 'VIP 고객님께 드리는 15% 주문 할인', NOW())
ON DUPLICATE KEY UPDATE coupon_type_id=VALUES(coupon_type_id),
                        name=VALUES(name);

-- 5. 새로운 BOOK 타겟 정률(비율) 할인 쿠폰 추가 (ID 105 사용)
INSERT INTO `coupon_type` (`coupon_type_id`, `name`, `target`, `minimum_payment`, `created_at`)
VALUES (105, 'HTTP 완벽가이드 특별 20% 할인', 'BOOK', 30000.00, NOW())
ON DUPLICATE KEY UPDATE name=VALUES(name),
                        target=VALUES(target),
                        minimum_payment=VALUES(minimum_payment);
INSERT INTO `relative_coupon` (`coupon_type_id`, `maximum_discount_price`, `discount_percent`, `created_at`)
VALUES (105, 10000.00, 20, NOW())
ON DUPLICATE KEY UPDATE maximum_discount_price=VALUES(maximum_discount_price),
                        discount_percent=VALUES(discount_percent);
INSERT INTO `coupon` (`coupon_id`, `coupon_type_id`, `name`, `created_at`)
VALUES (105, 105, 'HTTP 완벽가이드 정독 응원', NOW())
ON DUPLICATE KEY UPDATE coupon_type_id=VALUES(coupon_type_id),
                        name=VALUES(name);


-- 6. 사용자 쿠폰 할당 (UserCoupons)
-- user_coupon_id = 101 은 coupon_id = 1 (ORDER 타겟 '생일 축하 쿠폰'으로 가정)을 user_id = 1에게 할당합니다.
INSERT INTO `user_coupon` (`user_coupon_id`, `coupon_id`, `user_id`, `available_start_at`, `available_end_at`,
                           `created_at`)
VALUES (101, 1, 1, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), NOW())
ON DUPLICATE KEY UPDATE coupon_id=VALUES(coupon_id),
                        user_id=VALUES(user_id),
                        available_start_at=VALUES(available_start_at),
                        available_end_at=VALUES(available_end_at),
                        used_at=NULL,
                        deleted_at=NULL;

-- user_coupon_id = 102 은 coupon_id = 103 (BOOK 타겟 '코어JS 전용 2000원 할인')을 user_id = 1에게 할당합니다.
INSERT INTO `user_coupon` (`user_coupon_id`, `coupon_id`, `user_id`, `available_start_at`, `available_end_at`,
                           `created_at`)
VALUES (102, 103, 1, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), NOW())
ON DUPLICATE KEY UPDATE coupon_id=VALUES(coupon_id),
                        user_id=VALUES(user_id),
                        available_start_at=VALUES(available_start_at),
                        available_end_at=VALUES(available_end_at),
                        used_at=NULL,
                        deleted_at=NULL;

-- user_coupon_id = 103 은 coupon_id = 104 (ORDER 타겟 '정기 구독 15% 할인')을 user_id = 1에게 할당합니다. (이번 테스트 주문에는 미사용)
INSERT INTO `user_coupon` (`user_coupon_id`, `coupon_id`, `user_id`, `available_start_at`, `available_end_at`,
                           `created_at`)
VALUES (103, 104, 1, NOW(), DATE_ADD(NOW(), INTERVAL 60 DAY), NOW())
ON DUPLICATE KEY UPDATE coupon_id=VALUES(coupon_id),
                        user_id=VALUES(user_id),
                        available_start_at=VALUES(available_start_at),
                        available_end_at=VALUES(available_end_at),
                        used_at=NULL,
                        deleted_at=NULL;

-- user_coupon_id = 104 은 coupon_id = 105 (BOOK 타겟 'HTTP 완벽가이드 특별 20% 할인')을 user_id = 1에게 할당합니다.
INSERT INTO `user_coupon` (`user_coupon_id`, `coupon_id`, `user_id`, `available_start_at`, `available_end_at`,
                           `created_at`)
VALUES (104, 105, 1, NOW(), DATE_ADD(NOW(), INTERVAL 45 DAY), NOW())
ON DUPLICATE KEY UPDATE coupon_id=VALUES(coupon_id),
                        user_id=VALUES(user_id),
                        available_start_at=VALUES(available_start_at),
                        available_end_at=VALUES(available_end_at),
                        used_at=NULL,
                        deleted_at=NULL;


-- 7. 주문 생성 (Orders) - order_id (101) 사용
INSERT INTO `orders` (`order_id`, `user_id`, `order_state_id`, `delivery_rule_id`, `order_name`,
                      `requested_delivery_date`, `shipped_at`, `delivery_fee`, `orderer_name`, `orderer_email`,
                      `orderer_phone_number`, `receiver_name`, `receiver_email`, `receiver_phone_number`,
                      `address`, `detail_address`, `postal_code`, `tracking_number`, `order_key`,
                      `coupon_discount_amount`, `point_discount_amount`, `sale_discount_amount`,
                      `original_amount`, -- 필드 추가
                      `created_at`, `deleted_at`)
VALUES (101, 1, 1, 1, '코어 자바스크립트 Deep Dive 외 1건',
        DATE_ADD(NOW(), INTERVAL 5 DAY), NULL, 5000.00, '테스트유저일', NULL,
        '01011112222', '수령인A', NULL, '01022223333',
        '서울특별시 서초구 신반포로 321', '101동 1001호 (반포자이)', '06543',
        'TEMPTRACK12345', UUID(),
        NULL, NULL, NULL, -- 할인 금액들은 우선 NULL로 설정 (필요시 값 지정)
        122000.00, -- 값 추가 (예: 32000*1 + 45000*2)
        NOW(), NULL)
ON DUPLICATE KEY UPDATE user_id=VALUES(user_id),
                        order_state_id=VALUES(order_state_id),
                        delivery_rule_id=VALUES(delivery_rule_id),
                        order_name=VALUES(order_name),
                        requested_delivery_date=VALUES(requested_delivery_date),
                        shipped_at=VALUES(shipped_at),
                        delivery_fee=VALUES(delivery_fee),
                        orderer_name=VALUES(orderer_name),
                        orderer_email=VALUES(orderer_email),
                        orderer_phone_number=VALUES(orderer_phone_number),
                        receiver_name=VALUES(receiver_name),
                        receiver_email=VALUES(receiver_email),
                        receiver_phone_number=VALUES(receiver_phone_number),
                        address=VALUES(address),
                        detail_address=VALUES(detail_address),
                        postal_code=VALUES(postal_code),
                        tracking_number=VALUES(tracking_number),
                        order_key=VALUES(order_key),
                        coupon_discount_amount=VALUES(coupon_discount_amount),
                        point_discount_amount=VALUES(point_discount_amount),
                        sale_discount_amount=VALUES(sale_discount_amount),
                        original_amount=VALUES(original_amount), -- 업데이트 절에도 추가
                        created_at=VALUES(created_at),           -- 생성 시간도 업데이트 절에 추가
                        deleted_at=NULL;
-- 중복 실행 시 deleted_at을 NULL로 초기화

-- 8. 주문 상품 목록 (BookOrder) - book_order_id (101, 102) 사용
INSERT INTO `book_order` (`book_order_id`, `order_id`, `book_id`, `quantity`, `price`, `created_at`)
VALUES (101, 101, 101, 1, 32000.00, NOW()),
       (102, 101, 102, 2, 45000.00, NOW())
ON DUPLICATE KEY UPDATE order_id=VALUES(order_id),
                        book_id=VALUES(book_id),
                        quantity=VALUES(quantity),
                        price=VALUES(price),
                        deleted_at=NULL;

-- 9. 포장 주문 내역 (OrderPackaging)
INSERT INTO `order_packaging` (`order_packaging_id`, `book_order_id`, `package_id`, `quantity`, `created_at`)
VALUES (101, 101, 101, 1, NOW()),
       (102, 102, 102, 1, NOW())
ON DUPLICATE KEY UPDATE book_order_id=VALUES(book_order_id),
                        package_id=VALUES(package_id),
                        quantity=VALUES(quantity),
                        deleted_at=NULL;

-- 10. 쿠폰 사용 내역 (UserCouponBookOrder) - 수정된 시나리오 반영

-- '생일 축하 쿠폰'(user_coupon_id=101, ORDER 타겟)을 전체 주문(order_id=101)에 적용
-- user_coupon_book_order_id = 101
INSERT INTO `user_coupon_book_order` (`user_coupon_book_order_id`, `user_coupon_id`, `book_order_id`, `order_id`,
                                      `user_id`)
VALUES (101, 101, NULL, 101, 1) -- book_order_id를 NULL로 설정하여 주문 전체에 적용
ON DUPLICATE KEY UPDATE user_coupon_id=VALUES(user_coupon_id),
                        book_order_id=VALUES(book_order_id), -- NULL로 업데이트
                        order_id=VALUES(order_id),
                        user_id=VALUES(user_id);

-- '코어 자바스크립트 특별 할인 쿠폰'(user_coupon_id=102, BOOK 타겟 정액)을 '코어 자바스크립트 Deep Dive'(book_order_id=101)에 적용
-- user_coupon_book_order_id = 102
INSERT INTO `user_coupon_book_order` (`user_coupon_book_order_id`, `user_coupon_id`, `book_order_id`, `order_id`,
                                      `user_id`)
VALUES (102, 102, 101, 101, 1)
ON DUPLICATE KEY UPDATE user_coupon_id=VALUES(user_coupon_id),
                        book_order_id=VALUES(book_order_id),
                        order_id=VALUES(order_id),
                        user_id=VALUES(user_id);

-- 새로 추가한 'HTTP 완벽가이드 정독 응원 쿠폰'(user_coupon_id=104, BOOK 타겟 정률)을
-- 'HTTP 완벽 가이드 초판'(book_order_id=102)에 적용
-- user_coupon_book_order_id = 104 (ID 103은 다른 용도로 예약되어 있거나 건너뛴 것으로 가정)
INSERT INTO `user_coupon_book_order` (`user_coupon_book_order_id`, `user_coupon_id`, `book_order_id`, `order_id`,
                                      `user_id`)
VALUES (104, 104, 102, 101, 1)
ON DUPLICATE KEY UPDATE user_coupon_id=VALUES(user_coupon_id),
                        book_order_id=VALUES(book_order_id),
                        order_id=VALUES(order_id),
                        user_id=VALUES(user_id);