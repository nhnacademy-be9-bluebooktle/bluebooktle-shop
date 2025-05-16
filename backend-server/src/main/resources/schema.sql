CREATE TABLE `publisher`
(
    `publisher_id` bigint      NOT NULL AUTO_INCREMENT,
    `name`         varchar(20) NOT NULL,
    `created_at`   timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `deleted_at`   timestamp   NULL,

    CONSTRAINT `PK_PUBLISHER` PRIMARY KEY (`publisher_id`)
);

CREATE TABLE `author`
(
    `author_id`  bigint      NOT NULL AUTO_INCREMENT,
    `name`       varchar(50) NOT NULL,
    `created_at` timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `deleted_at` timestamp   NULL,

    CONSTRAINT `PK_AUTHOR` PRIMARY KEY (`author_id`)
);

CREATE TABLE `tag`
(
    `tag_id`     bigint      NOT NULL AUTO_INCREMENT,
    `name`       varchar(20) NOT NULL,
    `created_at` timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `deleted_at` timestamp   NULL,

    CONSTRAINT `PK_TAG` PRIMARY KEY (`tag_id`)
);

CREATE TABLE `delivery_rule`
(
    `delivery_rule_id` bigint         NOT NULL AUTO_INCREMENT,
    `name`             varchar(50)    NOT NULL,
    `price`            decimal(10, 2) NOT NULL,
    `delivery_fee`     decimal(10, 2) NOT NULL,
    `created_at`       timestamp      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `deleted_at`       timestamp      NULL,

    CONSTRAINT `PK_DELIVERY_RULE` PRIMARY KEY (`delivery_rule_id`)
);

CREATE TABLE `payment_type`
(
    `payment_type_id` bigint      NOT NULL AUTO_INCREMENT,
    `method`          varchar(50) NOT NULL,
    `created_at`      timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `deleted_at`      timestamp   NULL,

    CONSTRAINT `PK_PAYMENT_TYPE` PRIMARY KEY (`payment_type_id`)
);

CREATE TABLE `book`
(
    `book_id`      bigint       NOT NULL AUTO_INCREMENT,
    `title`        varchar(255) NOT NULL,
    `index`        text         NULL,
    `description`  text         NOT NULL,
    `publish_date` timestamp    NULL,
    `isbn`         varchar(13)  NOT NULL,
    `created_at`   timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `deleted_at`   timestamp    NULL,

    CONSTRAINT `PK_BOOK` PRIMARY KEY (`book_id`)
);

CREATE TABLE `coupon_type`
(
    `coupon_type_id`  bigint                 NOT NULL AUTO_INCREMENT,
    `name`            varchar(100)           NOT NULL COMMENT '구분자',
    `target`          ENUM ('ORDER', 'BOOK') NOT NULL,
    `minimum_payment` decimal(10, 2)         NOT NULL,
    `created_at`      timestamp              NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `deleted_at`      timestamp              NULL,

    CONSTRAINT `PK_COUPON_TYPE` PRIMARY KEY (`coupon_type_id`)
);

CREATE TABLE `membership_level`
(
    `membership_id` bigint         NOT NULL AUTO_INCREMENT,
    `name`          varchar(10)    NOT NULL,
    `rate`          int            NOT NULL,
    `min_net_spent` decimal(10, 2) NOT NULL,
    `max_net_spent` decimal(10, 2) NOT NULL,
    `created_at`    timestamp      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `deleted_at`    timestamp      NULL,

    CONSTRAINT `PK_MEMBERSHIP_LEVEL` PRIMARY KEY (`membership_id`)
);

CREATE TABLE `category`
(
    `category_id`        bigint      NOT NULL AUTO_INCREMENT,
    `parent_category_id` bigint      NULL,
    `name`               varchar(50) NOT NULL,
    `created_at`         timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `deleted_at`         timestamp   NULL,

    CONSTRAINT `PK_CATEGORY` PRIMARY KEY (`category_id`),
    CONSTRAINT `FK_category_parent_category_id_category`
        FOREIGN KEY (`parent_category_id`)
            REFERENCES `category` (`category_id`)
);

CREATE TABLE `order_state`
(
    `order_state_id` bigint    NOT NULL AUTO_INCREMENT,
    `state`          ENUM ('PENDING', 'SHIPPING', 'COMPLETED', 'RETURNED', 'CANCELED')
                               NOT NULL COMMENT '대기,배송중,완료,반품,주문취소',
    `created_at`     timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `deleted_at`     timestamp NULL,

    CONSTRAINT `PK_ORDER_STATE` PRIMARY KEY (`order_state_id`)
);

CREATE TABLE `point_source_type`
(
    `point_type_id` bigint               NOT NULL AUTO_INCREMENT,
    `action_type`   ENUM ('USE', 'EARN') NOT NULL,
    `source_type`   varchar(50)          NOT NULL COMMENT '로그인 적립, 회원가입 적립, 리뷰 적립 등 : 결제 사용',
    `created_at`    timestamp            NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `deleted_at`    timestamp            NULL,

    CONSTRAINT `PK_POINT_SOURCE_TYPE` PRIMARY KEY (`point_type_id`)
);

CREATE TABLE `img`
(
    `img_id`     bigint       NOT NULL AUTO_INCREMENT,
    `img_url`    varchar(255) NOT NULL,
    `created_at` timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `deleted_at` timestamp    NULL,

    CONSTRAINT `PK_IMG` PRIMARY KEY (`img_id`)
);

CREATE TABLE `packaging_option`
(
    `package_id` bigint         NOT NULL AUTO_INCREMENT,
    `name`       varchar(20)    NOT NULL,
    `price`      decimal(10, 2) NOT NULL,
    `created_at` timestamp      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `deleted_at` timestamp      NULL,

    CONSTRAINT `PK_PACKAGING_OPTION` PRIMARY KEY (`package_id`)
);

CREATE TABLE `payment_detail`
(
    `payment_detail_id` bigint       NOT NULL AUTO_INCREMENT,
    `payment_type_id`   bigint       NOT NULL,
    `key`               varchar(255) NULL,
    `created_at`        timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `deleted_at`        timestamp    NULL,

    CONSTRAINT `PK_PAYMENT_DETAIL` PRIMARY KEY (`payment_detail_id`),
    CONSTRAINT `FK_payment_detail_payment_type_id_payment_type`
        FOREIGN KEY (`payment_type_id`)
            REFERENCES `payment_type` (`payment_type_id`)
);

CREATE TABLE `book_author`
(
    `book_author_id` bigint NOT NULL AUTO_INCREMENT,
    `author_id`      bigint NOT NULL,
    `book_id`        bigint NOT NULL,

    CONSTRAINT `PK_BOOK_AUTHOR` PRIMARY KEY (`book_author_id`),
    CONSTRAINT `FK_book_author_author_id_author`
        FOREIGN KEY (`author_id`)
            REFERENCES `author` (`author_id`),
    CONSTRAINT `FK_book_author_book_id_book`
        FOREIGN KEY (`book_id`)
            REFERENCES `book` (`book_id`)
);

CREATE TABLE `book_sale_info`
(
    `book_sale_info_id` bigint                                                    NOT NULL AUTO_INCREMENT,
    `book_id`           bigint                                                    NOT NULL,
    `price`             decimal(10, 2)                                            NOT NULL,
    `sale_price`        decimal(10, 2)                                            NOT NULL,
    `stock`             int                                                       NOT NULL,
    `is_packable`       boolean                                                   NULL,
    `sale_percentage`   decimal(10, 2)                                            NOT NULL,
    `state`             ENUM ('AVAILABLE' , 'LOW_STOCK', 'SALE_ENDED', 'DELETED') NOT NULL DEFAULT 'AVAILABLE',
    `view_count`        bigint                                                    NOT NULL DEFAULT 0,
    `search_count`      bigint                                                    NOT NULL DEFAULT 0,
    `created_at`        timestamp                                                 NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `deleted_at`        timestamp                                                 NULL,

    CONSTRAINT `PK_BOOK_SALE_INFO` PRIMARY KEY (`book_sale_info_id`),
    CONSTRAINT `FK_book_sale_info_book_id_book`
        FOREIGN KEY (`book_id`)
            REFERENCES `book` (`book_id`)
);

CREATE TABLE `book_publisher`
(
    `book_publisher_id` bigint NOT NULL AUTO_INCREMENT,
    `book_id`           bigint NOT NULL,
    `publisher_id`      bigint NOT NULL,

    CONSTRAINT `PK_BOOK_PUBLISHER` PRIMARY KEY (`book_publisher_id`),
    CONSTRAINT `FK_book_publisher_book_id_book`
        FOREIGN KEY (`book_id`)
            REFERENCES `book` (`book_id`),
    CONSTRAINT `FK_book_publisher_publisher_id_publisher`
        FOREIGN KEY (`publisher_id`)
            REFERENCES `publisher` (`publisher_id`)
);

CREATE TABLE `book_tag`
(
    `book_tag_id` bigint NOT NULL AUTO_INCREMENT,
    `tag_id`      bigint NOT NULL,
    `book_id`     bigint NOT NULL,

    CONSTRAINT `PK_BOOK_TAG` PRIMARY KEY (`book_tag_id`),
    CONSTRAINT `FK_book_tag_tag_id_tag`
        FOREIGN KEY (`tag_id`)
            REFERENCES `tag` (`tag_id`),
    CONSTRAINT `FK_book_tag_book_id_book`
        FOREIGN KEY (`book_id`)
            REFERENCES `book` (`book_id`)
);

CREATE TABLE `absolute_coupon`
(
    `coupon_type_id` bigint         NOT NULL,
    `discount_price` decimal(10, 2) NOT NULL,
    `created_at`     timestamp      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `deleted_at`     timestamp      NULL,

    CONSTRAINT `PK_ABSOLUTE_COUPON` PRIMARY KEY (`coupon_type_id`),
    CONSTRAINT `FK_absolute_coupon_coupon_type_id_coupon_type`
        FOREIGN KEY (`coupon_type_id`)
            REFERENCES `coupon_type` (`coupon_type_id`)
);

CREATE TABLE `relative_coupon`
(
    `coupon_type_id`         bigint         NOT NULL,
    `maximum_discount_price` decimal(10, 2) NOT NULL,
    `discount_percent`       int            NOT NULL,
    `created_at`             timestamp      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `deleted_at`             timestamp      NULL,

    CONSTRAINT `PK_RELATIVE_COUPON` PRIMARY KEY (`coupon_type_id`),
    CONSTRAINT `FK_relative_coupon_coupon_type_id_coupon_type`
        FOREIGN KEY (`coupon_type_id`)
            REFERENCES `coupon_type` (`coupon_type_id`)
);

CREATE TABLE `coupon`
(
    `coupon_id`      bigint       NOT NULL AUTO_INCREMENT,
    `coupon_type_id` bigint       NOT NULL,
    `name`           varchar(100) NOT NULL,
    `created_at`     timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `deleted_at`     timestamp    NULL,

    CONSTRAINT `PK_COUPON` PRIMARY KEY (`coupon_id`),
    CONSTRAINT `FK_coupon_coupon_type_id_coupon_type`
        FOREIGN KEY (`coupon_type_id`)
            REFERENCES `coupon_type` (`coupon_type_id`)
);

CREATE TABLE `users`
(
    `user_id`       bigint                                  NOT NULL AUTO_INCREMENT,
    `membership_id` bigint                                  NOT NULL,
    `login_id`      varchar(50)                             NOT NULL,
    `password`      varchar(255)                            NOT NULL,
    `name`          varchar(20)                             NOT NULL,
    `email`         varchar(50)                             NOT NULL,
    `birth`         varchar(8)                              NOT NULL,
    `phone_number`  varchar(11)                             NOT NULL,
    `point_balance` decimal(10, 2)                          NOT NULL,
    `type`          ENUM ('USER', 'ADMIN')                  NOT NULL DEFAULT 'USER',
    `status`        ENUM ('ACTIVE', 'DORMANT', 'WITHDRAWN') NOT NULL DEFAULT 'ACTIVE' COMMENT '일반,휴면, 탈퇴',
    `last_login_at` timestamp                               NULL,
    `created_at`    timestamp                               NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `deleted_at`    timestamp                               NULL,

    CONSTRAINT `PK_USER` PRIMARY KEY (`user_id`),
    CONSTRAINT `FK_user_membership_id_membership_level`
        FOREIGN KEY (`membership_id`)
            REFERENCES `membership_level` (`membership_id`)
);

CREATE TABLE `book_category`
(
    `book_category_id` bigint NOT NULL AUTO_INCREMENT,
    `book_id`          bigint NOT NULL,
    `category_id`      bigint NOT NULL,

    CONSTRAINT `PK_BOOK_CATEGORY` PRIMARY KEY (`book_category_id`),
    UNIQUE KEY `UK_book_category_book_category` (`book_id`, `category_id`),
    CONSTRAINT `FK_book_category_book_id_book`
        FOREIGN KEY (`book_id`)
            REFERENCES `book` (`book_id`),
    CONSTRAINT `FK_book_category_category_id_category`
        FOREIGN KEY (`category_id`)
            REFERENCES `category` (`category_id`)
);

CREATE TABLE `book_img`
(
    `book_img_id`  bigint  NOT NULL AUTO_INCREMENT,
    `book_id`      bigint  NOT NULL,
    `img_id`       bigint  NOT NULL,
    `is_thumbnail` boolean NOT NULL,

    CONSTRAINT `PK_BOOK_IMG` PRIMARY KEY (`book_img_id`),
    CONSTRAINT `FK_book_img_book_id_book`
        FOREIGN KEY (`book_id`)
            REFERENCES `book` (`book_id`),
    CONSTRAINT `FK_book_img_img_id_img`
        FOREIGN KEY (`img_id`)
            REFERENCES `img` (`img_id`)
);

CREATE TABLE `book_coupon`
(
    `coupon_id`  bigint    NOT NULL,
    `book_id`    bigint    NOT NULL,
    `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `deleted_at` timestamp NULL,

    CONSTRAINT `PK_BOOK_COUPON` PRIMARY KEY (`coupon_id`),
    CONSTRAINT `FK_book_coupon_coupon_id_coupon`
        FOREIGN KEY (`coupon_id`)
            REFERENCES `coupon` (`coupon_id`),
    CONSTRAINT `FK_book_coupon_book_id_book`
        FOREIGN KEY (`book_id`)
            REFERENCES `book` (`book_id`)
);

CREATE TABLE `category_coupon`
(
    `coupon_id`   bigint    NOT NULL,
    `category_id` bigint    NOT NULL,
    `created_at`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `deleted_at`  timestamp NULL,

    CONSTRAINT `PK_CATEGORY_COUPON` PRIMARY KEY (`coupon_id`),
    CONSTRAINT `FK_category_coupon_coupon_id_coupon`
        FOREIGN KEY (`coupon_id`)
            REFERENCES `coupon` (`coupon_id`),
    CONSTRAINT `FK_category_coupon_category_id_category`
        FOREIGN KEY (`category_id`)
            REFERENCES `category` (`category_id`)
);

CREATE TABLE `user_coupon`
(
    `user_coupon_id`     bigint    NOT NULL AUTO_INCREMENT,
    `coupon_id`          bigint    NOT NULL,
    `created_at`         timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `used_at`            timestamp NULL,
    `available_start_at` timestamp NOT NULL,
    `available_end_at`   timestamp NOT NULL,
    `deleted_at`         timestamp NULL,
    `user_id`            bigint    NOT NULL,

    CONSTRAINT `PK_USER_COUPON` PRIMARY KEY (`user_coupon_id`),
    CONSTRAINT `FK_user_coupon_coupon_id_coupon`
        FOREIGN KEY (`coupon_id`)
            REFERENCES `coupon` (`coupon_id`),
    CONSTRAINT `FK_user_coupon_user_id_user`
        FOREIGN KEY (`user_id`)
            REFERENCES `users` (`user_id`)
);

CREATE TABLE `point_history`
(
    `point_id`      bigint         NOT NULL AUTO_INCREMENT,
    `point_type_id` bigint         NOT NULL,
    `user_id`       bigint         NOT NULL,
    `value`         decimal(10, 2) NOT NULL,
    `created_at`    timestamp      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `deleted_at`    timestamp      NULL,

    CONSTRAINT `PK_POINT_HISTORY` PRIMARY KEY (`point_id`),
    CONSTRAINT `FK_point_history_point_type_id_point_source_type`
        FOREIGN KEY (`point_type_id`)
            REFERENCES `point_source_type` (`point_type_id`),
    CONSTRAINT `FK_point_history_user_id_user`
        FOREIGN KEY (`user_id`)
            REFERENCES `users` (`user_id`)
);

CREATE TABLE `address`
(
    `address_id`     bigint       NOT NULL AUTO_INCREMENT,
    `user_id`        bigint       NOT NULL,
    `alias`          varchar(50)  NOT NULL,
    `road_address`   varchar(255) NOT NULL,
    `detail_address` varchar(255) NOT NULL,
    `postal_code`    varchar(5)   NOT NULL,
    `created_at`     timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `deleted_at`     timestamp    NULL,

    CONSTRAINT `PK_ADDRESS` PRIMARY KEY (`address_id`),
    CONSTRAINT `FK_address_user_id_user`
        FOREIGN KEY (`user_id`)
            REFERENCES `users` (`user_id`)
);

CREATE TABLE `book_likes`
(
    `book_id` bigint NOT NULL,
    `user_id` bigint NOT NULL,

    CONSTRAINT `PK_BOOK_LIKES` PRIMARY KEY (`book_id`, `user_id`),
    CONSTRAINT `FK_book_likes_book_id_book`
        FOREIGN KEY (`book_id`)
            REFERENCES `book` (`book_id`),
    CONSTRAINT `FK_book_likes_user_id_user`
        FOREIGN KEY (`user_id`)
            REFERENCES `users` (`user_id`)
);

CREATE TABLE `cart`
(
    `cart_id`    bigint    NOT NULL AUTO_INCREMENT,
    `user_id`    bigint    NOT NULL,
    `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `deleted_at` timestamp NULL,

    CONSTRAINT `PK_CART` PRIMARY KEY (`cart_id`),
    CONSTRAINT `FK_cart_user_id_user`
        FOREIGN KEY (`user_id`)
            REFERENCES `users` (`user_id`)
);

CREATE TABLE `orders`
(
    `order_id`                bigint         NOT NULL AUTO_INCREMENT,
    `order_state_id`          bigint         NOT NULL,
    `delivery_rule_id`        bigint         NOT NULL,
    `user_id`                 bigint         NULL,
    `order_date`              timestamp      NOT NULL,
    `requested_delivery_date` timestamp      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `shipped_at`              timestamp      NULL,
    `delivery_fee`            decimal(10, 2) NOT NULL,
    `orderer_name`            varchar(20)    NOT NULL,
    `orderer_phone_number`    varchar(11)    NOT NULL,
    `receiver_name`           varchar(20)    NOT NULL,
    `receiver_phone_number`   varchar(11)    NOT NULL,
    `address`                 varchar(255)   NOT NULL,
    `detail_address`          varchar(255)   NOT NULL,
    `postal_code`             varchar(5)     NOT NULL,
    `tracking_number`         varchar(14)    NOT NULL,
    `deleted_at`              timestamp      NULL,
    `order_key`               varchar(36)    NULL COMMENT '비회원이 주문을 확인하기 위해 사용하는 비밀번호입니다.',

    CONSTRAINT `PK_ORDERS` PRIMARY KEY (`order_id`),
    CONSTRAINT `FK_order_order_state_id_order_state`
        FOREIGN KEY (`order_state_id`)
            REFERENCES `order_state` (`order_state_id`),
    CONSTRAINT `FK_order_delivery_rule_id_delivery_rule`
        FOREIGN KEY (`delivery_rule_id`)
            REFERENCES `delivery_rule` (`delivery_rule_id`),
    CONSTRAINT `FK_order_user_id_user`
        FOREIGN KEY (`user_id`)
            REFERENCES `users` (`user_id`)
);

CREATE TABLE `cart_book`
(
    `cart_book_id` bigint    NOT NULL AUTO_INCREMENT,
    `book_id`      bigint    NOT NULL,
    `cart_id`      bigint    NOT NULL,
    `quantity`     int       NULL,
    `created_at`   timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `deleted_at`   timestamp NULL,

    CONSTRAINT `PK_CART_BOOK` PRIMARY KEY (`cart_book_id`),
    CONSTRAINT `FK_cart_book_book_id_book`
        FOREIGN KEY (`book_id`)
            REFERENCES `book` (`book_id`),
    CONSTRAINT `FK_cart_book_cart_id_cart`
        FOREIGN KEY (`cart_id`)
            REFERENCES `cart` (`cart_id`)
);

CREATE TABLE `payment`
(
    `payment_id`        bigint         NOT NULL AUTO_INCREMENT,
    `order_id`          bigint         NOT NULL,
    `payment_detail_id` bigint         NOT NULL,
    `price`             decimal(10, 2) NOT NULL,
    `created_at`        timestamp      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `deleted_at`        timestamp      NULL,

    CONSTRAINT `PK_PAYMENT` PRIMARY KEY (`payment_id`),
    CONSTRAINT `FK_payment_order_id_order`
        FOREIGN KEY (`order_id`)
            REFERENCES `orders` (`order_id`),
    CONSTRAINT `FK_payment_payment_detail_id_payment_detail`
        FOREIGN KEY (`payment_detail_id`)
            REFERENCES `payment_detail` (`payment_detail_id`)
);

CREATE TABLE `book_order`
(
    `book_order_id` bigint         NOT NULL AUTO_INCREMENT,
    `order_id`      bigint         NOT NULL,
    `book_id`       bigint         NOT NULL,
    `quantity`      int            NOT NULL,
    `price`         decimal(10, 2) NOT NULL,
    `created_at`    timestamp      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `deleted_at`    timestamp      NULL,

    CONSTRAINT `PK_BOOK_ORDER` PRIMARY KEY (`book_order_id`),
    CONSTRAINT `FK_book_order_order_id_order`
        FOREIGN KEY (`order_id`)
            REFERENCES `orders` (`order_id`),
    CONSTRAINT `FK_book_order_book_id_book`
        FOREIGN KEY (`book_id`)
            REFERENCES `book` (`book_id`)
);

CREATE TABLE `refund`
(
    `refund_id`     bigint                                                                  NOT NULL AUTO_INCREMENT,
    `order_id`      bigint                                                                  NOT NULL,
    `date`          datetime                                                                NOT NULL,
    `reason`        ENUM ('CHANGE_OF_MIND', 'DEFECT', 'DAMAGED', 'WRONG_DELIVERY', 'OTHER') NOT NULL COMMENT '단순 변심, 결함, 파손, 오배송',
    `reason_detail` text                                                                    NOT NULL,
    `status`        ENUM ('PENDING', 'PROGRESS', 'COMPLETE', 'CANCELED')                    NOT NULL COMMENT '요청, 진행중, 완료, 취소',
    `price`         decimal(10, 2)                                                          NOT NULL,
    `created_at`    timestamp                                                               NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `deleted_at`    timestamp                                                               NULL,

    CONSTRAINT `PK_REFUND` PRIMARY KEY (`refund_id`),
    CONSTRAINT `FK_refund_order_id_order`
        FOREIGN KEY (`order_id`)
            REFERENCES `orders` (`order_id`)
);

CREATE TABLE `payment_point_history`
(
    `payment_point_history_id` bigint NOT NULL AUTO_INCREMENT,
    `payment_id`               bigint NOT NULL,
    `point_id`                 bigint NOT NULL,
    `user_id`                  bigint NOT NULL,

    CONSTRAINT `PK_PAYMENT_POINT_HISTORY` PRIMARY KEY (`payment_point_history_id`),
    CONSTRAINT `FK_payment_point_history_payment_id_payment`
        FOREIGN KEY (`payment_id`)
            REFERENCES `payment` (`payment_id`),
    CONSTRAINT `FK_payment_point_history_point_id_point_history`
        FOREIGN KEY (`point_id`)
            REFERENCES `point_history` (`point_id`),
    CONSTRAINT `FK_payment_point_history_user_id_point_history`
        FOREIGN KEY (`user_id`)
            REFERENCES `point_history` (`user_id`)
);

CREATE TABLE `review`
(
    `review_id`      bigint    NOT NULL AUTO_INCREMENT,
    `user_id`        bigint    NOT NULL,
    `book_order_id`  bigint    NOT NULL,
    `img_id`         bigint    NULL,
    `star`           int       NOT NULL,
    `review_content` text      NULL,
    `likes`          int       NULL,
    `deleted_at`     timestamp NULL,
    `created_at`     timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT `PK_REVIEW` PRIMARY KEY (`review_id`),
    CONSTRAINT `FK_review_user_id_user`
        FOREIGN KEY (`user_id`)
            REFERENCES `users` (`user_id`),
    CONSTRAINT `FK_review_book_order_id_book_order`
        FOREIGN KEY (`book_order_id`)
            REFERENCES `book_order` (`book_order_id`),
    CONSTRAINT `FK_review_img_id_img`
        FOREIGN KEY (`img_id`)
            REFERENCES `img` (`img_id`)
);

CREATE TABLE `user_coupon_book_order`
(
    `user_coupon_book_order_id` bigint NOT NULL AUTO_INCREMENT,
    `user_coupon_id`            bigint NOT NULL,
    `book_order_id`             bigint NOT NULL,
    `order_id`                  bigint NOT NULL,
    `user_id`                   bigint NOT NULL,

    CONSTRAINT `PK_USER_COUPON_BOOK_ORDER` PRIMARY KEY (`user_coupon_book_order_id`),
    CONSTRAINT `FK_user_coupon_book_order_user_coupon_id_user_coupon`
        FOREIGN KEY (`user_coupon_id`)
            REFERENCES `user_coupon` (`user_coupon_id`),
    CONSTRAINT `FK_user_coupon_book_order_book_order_id_book_order`
        FOREIGN KEY (`book_order_id`)
            REFERENCES `book_order` (`book_order_id`),
    CONSTRAINT `FK_user_coupon_book_order_order_id_order`
        FOREIGN KEY (`order_id`)
            REFERENCES `orders` (`order_id`),
    CONSTRAINT `FK_user_coupon_book_order_user_id_user`
        FOREIGN KEY (`user_id`)
            REFERENCES `users` (`user_id`)
);

CREATE TABLE `order_packaging`
(
    `order_packaging_id` bigint    NOT NULL AUTO_INCREMENT,
    `package_id`         bigint    NOT NULL,
    `book_order_id`      bigint    NOT NULL,
    `quantity`           int       NOT NULL,
    `created_at`         timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `deleted_at`         timestamp NULL,

    CONSTRAINT `PK_ORDER_PACKAGING` PRIMARY KEY (`order_packaging_id`),
    CONSTRAINT `FK_order_packaging_package_id_packaging_option`
        FOREIGN KEY (`package_id`)
            REFERENCES `packaging_option` (`package_id`),
    CONSTRAINT `FK_order_packaging_book_order_id_book_order`
        FOREIGN KEY (`book_order_id`)
            REFERENCES `book_order` (`book_order_id`)
);