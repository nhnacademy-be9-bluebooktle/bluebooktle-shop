CREATE TABLE `membership_level`
(
    `membership_id` bigint         NOT NULL,
    `name`          varchar(10)    NOT NULL,
    `rate`          int            NOT NULL,
    `min_net_spent` decimal(10, 2) NOT NULL,
    `max_net_spent` decimal(10, 2) NOT NULL,
    `created_at`    timestamp      NOT NULL DEFAULT current_timestamp,
    `deleted_at`    timestamp      NULL
);

CREATE TABLE `absolute_coupon`
(
    `coupon_type_id` bigint         NOT NULL,
    `discount_price` decimal(10, 2) NOT NULL,
    `created_at`     timestamp      NOT NULL DEFAULT current_timestamp,
    `deleted_at`     timestamp      NULL
);

CREATE TABLE `payment_point_history`
(
    `payment_point_history_id` bigint NOT NULL,
    `payment_id`               bigint NOT NULL,
    `point_id`                 bigint NOT NULL,
    `user_id`                  bigint NOT NULL
);

CREATE TABLE `review`
(
    `review_id`      bigint       NOT NULL,
    `user_id`        bigint       NOT NULL,
    `book_order_id`  bigint       NOT NULL,
    `created_at`     timestamp    NOT NULL DEFAULT current_timestamp,
    `star`           int          NOT NULL,
    `img_url`        varchar(255) NULL,
    `review_content` text         NULL,
    `likes`          int          NULL,
    `deleted_at`     timestamp    NULL,
    `img_id`         bigint       NULL
);

CREATE TABLE `publisher`
(
    `publisher_id` bigint      NOT NULL,
    `name`         varchar(20) NOT NULL,
    `created_at`   timestamp   NOT NULL DEFAULT current_timestamp,
    `deleted_at`   timestamp   NULL
);

CREATE TABLE `delivery_rule`
(
    `delivery_rule_id` bigint         NOT NULL,
    `name`             varchar(50)    NOT NULL,
    `price`            decimal(10, 2) NOT NULL,
    `delivery_fee`     decimal(10, 2) NOT NULL,
    `created_at`       timestamp      NOT NULL DEFAULT current_timestamp,
    `deleted_at`       timestamp      NULL
);

CREATE TABLE `user_coupon`
(
    `user_coupon_id` bigint    NOT NULL,
    `coupon_id`      bigint    NOT NULL,
    `created_at`     timestamp NOT NULL DEFAULT current_timestamp,
    `used_at`        timestamp NULL,
    `deleted_at`     timestamp NULL,
    `user_id`        bigint    NOT NULL
);

CREATE TABLE `book_author`
(
    `book_author_id` bigint NOT NULL,
    `author_id`      bigint NOT NULL,
    `book_id`        bigint NOT NULL
);

CREATE TABLE `point_history`
(
    `point_id`      bigint         NOT NULL,
    `point_type_id` bigint         NOT NULL,
    `user_id`       bigint         NOT NULL,
    `value`         decimal(10, 2) NOT NULL,
    `created_at`    timestamp      NOT NULL DEFAULT current_timestamp,
    `deleted_at`    timestamp      NULL
);

CREATE TABLE `img`
(
    `img_id`     bigint       NOT NULL,
    `img_url`    varchar(255) NOT NULL,
    `created_at` timestamp    NOT NULL DEFAULT current_timestamp,
    `deleted_at` timestamp    NULL
);

CREATE TABLE `book_sale_info`
(
    `book_sale_info_id` bigint                                                NOT NULL,
    `book_id`           bigint                                                NOT NULL,
    `price`             decimal(10, 2)                                        NOT NULL,
    `sale_price`        decimal(10, 2)                                        NOT NULL,
    `stock`             int                                                   NOT NULL,
    `is_packable`       boolean                                               NULL,
    `sale_percentage`   decimal(10, 2)                                        NOT NULL,
    `state`             enum ('AVAILABLE','LOW_STOCK','SALE_ENDED','DELETED') NOT NULL DEFAULT 'AVAILABLE',
    `view_count`        bigint                                                NOT NULL DEFAULT 0,
    `search_count`      bigint                                                NOT NULL DEFAULT 0,
    `created_at`        timestamp                                             NOT NULL DEFAULT current_timestamp,
    `deleted_at`        timestamp                                             NULL
);

CREATE TABLE `relative_coupon`
(
    `coupon_type_id`         bigint         NOT NULL,
    `maximum_discount_price` decimal(10, 2) NOT NULL,
    `discount_percent`       int            NOT NULL,
    `created_at`             timestamp      NOT NULL DEFAULT current_timestamp,
    `deleted_at`             timestamp      NULL
);

CREATE TABLE `book_coupon`
(
    `coupon_id`  bigint    NOT NULL,
    `book_id`    bigint    NOT NULL,
    `created_at` timestamp NOT NULL DEFAULT current_timestamp,
    `deleted_at` timestamp NULL
);

CREATE TABLE `category`
(
    `category_id`        bigint      NOT NULL,
    `parent_category_id` bigint      NULL,
    `name`               varchar(50) NOT NULL,
    `created_at`         timestamp   NOT NULL DEFAULT current_timestamp,
    `deleted_at`         timestamp   NULL
);

CREATE TABLE `point_source_type`
(
    `point_type_id` bigint               NOT NULL,
    `action_type`   enum ('USE', 'EARN') NOT NULL,
    `source_type`   varchar(50)          NOT NULL COMMENT '로그인 적립, 회원가입 적립, 리뷰 적립 등 : 결제 사용',
    `created_at`    timestamp            NOT NULL DEFAULT current_timestamp,
    `deleted_at`    timestamp            NULL
);

CREATE TABLE `payment`
(
    `payment_id`        bigint         NOT NULL,
    `order_id`          bigint         NOT NULL,
    `payment_detail_id` bigint         NOT NULL,
    `price`             decimal(10, 2) NOT NULL,
    `created_at`        timestamp      NOT NULL DEFAULT current_timestamp,
    `deleted_at`        timestamp      NULL
);

CREATE TABLE `author`
(
    `author_id`  bigint      NOT NULL,
    `name`       varchar(10) NOT NULL,
    `created_at` timestamp   NOT NULL DEFAULT current_timestamp,
    `deleted_at` timestamp   NULL
);

CREATE TABLE `book_order`
(
    `book_order_id` bigint         NOT NULL,
    `order_id`      bigint         NOT NULL,
    `book_id`       bigint         NOT NULL,
    `quantity`      int            NOT NULL,
    `price`         decimal(10, 2) NOT NULL,
    `created_at`    timestamp      NOT NULL DEFAULT current_timestamp,
    `deleted_at`    timestamp      NULL
);

CREATE TABLE `packaging_option`
(
    `package_id` bigint         NOT NULL,
    `name`       varchar(20)    NOT NULL,
    `price`      decimal(10, 2) NOT NULL,
    `created_at` timestamp      NOT NULL DEFAULT current_timestamp,
    `deleted_at` timestamp      NULL
);

CREATE TABLE `category_coupon`
(
    `coupon_id`   bigint    NOT NULL,
    `category_id` bigint    NOT NULL,
    `created_at`  timestamp NOT NULL DEFAULT current_timestamp,
    `deleted_at`  timestamp NULL
);

CREATE TABLE `address`
(
    `address_id`     bigint       NOT NULL,
    `user_id`        bigint       NOT NULL,
    `alias`          varchar(50)  NOT NULL,
    `road_address`   varchar(255) NOT NULL,
    `detail_address` varchar(255) NOT NULL,
    `postal_code`    varchar(5)   NOT NULL,
    `created_at`     timestamp    NOT NULL DEFAULT current_timestamp,
    `deleted_at`     timestamp    NULL
);

CREATE TABLE `book_category`
(
    `book_category_id` bigint NOT NULL,
    `book_id`          bigint NOT NULL,
    `category_id`      bigint NOT NULL
);

CREATE TABLE `cart_book`
(
    `cart_book_id` bigint    NOT NULL,
    `book_id`      bigint    NOT NULL,
    `cart_id`      bigint    NOT NULL,
    `quantity`     int       NULL,
    `created_at`   timestamp NOT NULL DEFAULT current_timestamp,
    `deleted_at`   timestamp NULL
);

CREATE TABLE `coupon_type`
(
    `coupon_type_id` bigint                 NOT NULL,
    `name`           varchar(100)           NOT NULL COMMENT '구분자',
    `target`         enum ('ORDER', 'BOOK') NOT NULL,
    `created_at`     timestamp              NOT NULL DEFAULT current_timestamp,
    `deleted_at`     timestamp              NULL
);

CREATE TABLE `book_publisher`
(
    `book_publisher_id` bigint NOT NULL,
    `book_id`           bigint NOT NULL,
    `publisher_id`      bigint NOT NULL
);

CREATE TABLE `user`
(
    `user_id`       bigint                                  NOT NULL,
    `membership_id` bigint                                  NOT NULL,
    `login_id`      varchar(50)                             NOT NULL,
    `password`      varchar(255)                            NOT NULL,
    `name`          varchar(20)                             NOT NULL,
    `email`         varchar(50)                             NOT NULL,
    `birth`         timestamp                               NOT NULL,
    `phone_number`  varchar(11)                             NOT NULL,
    `point_balance` decimal(10, 2)                          NOT NULL,
    `type`          enum ('USER', 'ADMIN')                  NOT NULL DEFAULT 'USER',
    `status`        enum ('ACTIVE', 'DORMANT', 'WITHDRAWN') NOT NULL DEFAULT 'ACTIVE' COMMENT '일반,휴면, 탈퇴',
    `last_login_at` timestamp                               NULL,
    `created_at`    timestamp                               NOT NULL DEFAULT current_timestamp,
    `deleted_at`    timestamp                               NULL
);

CREATE TABLE `user_coupon_book_order`
(
    `user_coupon_book_order_id` bigint NOT NULL,
    `user_coupon_id`            bigint NOT NULL,
    `book_order_id`             bigint NOT NULL,
    `order_id`                  bigint NOT NULL,
    `user_id`                   bigint NOT NULL
);

CREATE TABLE `book`
(
    `book_id`      bigint       NOT NULL,
    `title`        varchar(255) NOT NULL,
    `index`        text         NULL,
    `description`  text         NOT NULL,
    `publish_date` timestamp    NULL,
    `isbn`         varchar(13)  NOT NULL,
    `created_at`   timestamp    NOT NULL DEFAULT current_timestamp,
    `deleted_at`   timestamp    NULL
);

CREATE TABLE `book_likes`
(
    `book_id` bigint NOT NULL,
    `user_id` bigint NOT NULL
);

CREATE TABLE `order_packaging`
(
    `order_packaging_id` bigint    NOT NULL,
    `package_id`         bigint    NOT NULL,
    `book_order_id`      bigint    NOT NULL,
    `quantity`           int       NOT NULL,
    `created_at`         timestamp NOT NULL DEFAULT current_timestamp,
    `deleted_at`         timestamp NULL
);

CREATE TABLE `tag`
(
    `tag_id`     bigint      NOT NULL,
    `name`       varchar(20) NOT NULL,
    `created_at` timestamp   NOT NULL DEFAULT current_timestamp,
    `deleted_at` timestamp   NULL
);

CREATE TABLE `refund`
(
    `refund_id`     bigint                                                                  NOT NULL,
    `order_id`      bigint                                                                  NOT NULL,
    `date`          datetime                                                                NOT NULL,
    `reason`        enum ('CHANGE_OF_MIND', 'DEFECT', 'DAMAGED', 'WRONG_DELIVERY', 'OTHER') NOT NULL COMMENT '단순 변심, 결함, 파손, 오배송',
    `reason_detail` text                                                                    NOT NULL,
    `status`        enum ('PENDING', 'PROGRESS', 'COMPLETE', 'CANCELED')                    NOT NULL COMMENT '요청, 진행중, 완료, 취소',
    `price`         decimal(10, 2)                                                          NOT NULL,
    `created_at`    timestamp                                                               NOT NULL DEFAULT current_timestamp,
    `deleted_at`    timestamp                                                               NULL
);

CREATE TABLE `coupon`
(
    `coupon_id`          bigint         NOT NULL,
    `coupon_type_id`     bigint         NOT NULL,
    `name`               varchar(100)   NOT NULL,
    `available_start_at` timestamp      NOT NULL,
    `available_end_at`   timestamp      NOT NULL,
    `minimum_payment`    decimal(10, 2) NOT NULL,
    `created_at`         timestamp      NOT NULL DEFAULT current_timestamp,
    `deleted_at`         timestamp      NULL
);

CREATE TABLE `cart`
(
    `cart_id`    bigint    NOT NULL,
    `user_id`    bigint    NOT NULL,
    `created_at` timestamp NOT NULL DEFAULT current_timestamp,
    `deleted_at` timestamp NULL
);

CREATE TABLE `book_img`
(
    `book_img_id` bigint  NOT NULL,
    `book_id`     bigint  NOT NULL,
    `img_id`      bigint  NOT NULL,
    `isThumbnail` boolean NOT NULL
);

CREATE TABLE `payment_detail`
(
    `payment_detail_id` bigint       NOT NULL,
    `payment_type_id`   bigint       NOT NULL,
    `key`               varchar(255) NULL,
    `created_at`        timestamp    NOT NULL DEFAULT current_timestamp,
    `deleted_at`        timestamp    NULL
);

CREATE TABLE `order_state`
(
    `order_state_id` bigint                                                            NOT NULL,
    `status`         enum ('PENDING', 'SHIPPING', 'COMPLETED', 'RETURNED', 'CANCELED') NOT NULL COMMENT '대기,배송중,완료,반품,주문취소',
    `created_at`     timestamp                                                         NOT NULL DEFAULT current_timestamp,
    `deleted_at`     timestamp                                                         NULL
);

CREATE TABLE `book_tag`
(
    `book_tag_id` bigint NOT NULL,
    `tag_id`      bigint NOT NULL,
    `book_id`     bigint NOT NULL
);

CREATE TABLE `order`
(
    `order_id`                bigint         NOT NULL,
    `order_state_id`          bigint         NOT NULL,
    `delivery_rule_id`        bigint         NOT NULL,
    `user_id`                 bigint         NULL,
    `order_date`              timestamp      NOT NULL,
    `requested_delivery_date` timestamp      NOT NULL DEFAULT current_timestamp,
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
    `order_key`               uuid           NULL COMMENT '비회원이 주문을 확인하기 위해 사용하는 비밀번호입니다.'
);

CREATE TABLE `payment_type`
(
    `payment_type_id` bigint      NOT NULL,
    `method`          varchar(50) NOT NULL,
    `created_at`      timestamp   NOT NULL DEFAULT current_timestamp,
    `deleted_at`      timestamp   NULL
);

ALTER TABLE `membership_level`
    ADD CONSTRAINT `PK_MEMBERSHIP_LEVEL` PRIMARY KEY (
                                                      `membership_id`
        );

ALTER TABLE `absolute_coupon`
    ADD CONSTRAINT `PK_ABSOLUTE_COUPON` PRIMARY KEY (
                                                     `coupon_type_id`
        );

ALTER TABLE `payment_point_history`
    ADD CONSTRAINT `PK_PAYMENT_POINT_HISTORY` PRIMARY KEY (
                                                           `payment_point_history_id`
        );

ALTER TABLE `review`
    ADD CONSTRAINT `PK_REVIEW` PRIMARY KEY (
                                            `review_id`
        );

ALTER TABLE `publisher`
    ADD CONSTRAINT `PK_PUBLISHER` PRIMARY KEY (
                                               `publisher_id`
        );

ALTER TABLE `delivery_rule`
    ADD CONSTRAINT `PK_DELIVERY_RULE` PRIMARY KEY (
                                                   `delivery_rule_id`
        );

ALTER TABLE `user_coupon`
    ADD CONSTRAINT `PK_USER_COUPON` PRIMARY KEY (
                                                 `user_coupon_id`
        );

ALTER TABLE `book_author`
    ADD CONSTRAINT `PK_BOOK_AUTHOR` PRIMARY KEY (
                                                 `book_author_id`
        );

ALTER TABLE `point_history`
    ADD CONSTRAINT `PK_POINT_HISTORY` PRIMARY KEY (
                                                   `point_id`
        );

ALTER TABLE `img`
    ADD CONSTRAINT `PK_IMG` PRIMARY KEY (
                                         `img_id`
        );

ALTER TABLE `book_sale_info`
    ADD CONSTRAINT `PK_BOOK_SALE_INFO` PRIMARY KEY (
                                                    `book_sale_info_id`
        );

ALTER TABLE `relative_coupon`
    ADD CONSTRAINT `PK_RELATIVE_COUPON` PRIMARY KEY (
                                                     `coupon_type_id`
        );

ALTER TABLE `book_coupon`
    ADD CONSTRAINT `PK_BOOK_COUPON` PRIMARY KEY (
                                                 `coupon_id`
        );

ALTER TABLE `category`
    ADD CONSTRAINT `PK_CATEGORY` PRIMARY KEY (
                                              `category_id`
        );

ALTER TABLE `point_source_type`
    ADD CONSTRAINT `PK_POINT_SOURCE_TYPE` PRIMARY KEY (
                                                       `point_type_id`
        );

ALTER TABLE `payment`
    ADD CONSTRAINT `PK_PAYMENT` PRIMARY KEY (
                                             `payment_id`
        );

ALTER TABLE `author`
    ADD CONSTRAINT `PK_AUTHOR` PRIMARY KEY (
                                            `author_id`
        );

ALTER TABLE `book_order`
    ADD CONSTRAINT `PK_BOOK_ORDER` PRIMARY KEY (
                                                `book_order_id`
        );

ALTER TABLE `packaging_option`
    ADD CONSTRAINT `PK_PACKAGING_OPTION` PRIMARY KEY (
                                                      `package_id`
        );

ALTER TABLE `category_coupon`
    ADD CONSTRAINT `PK_CATEGORY_COUPON` PRIMARY KEY (
                                                     `coupon_id`
        );

ALTER TABLE `address`
    ADD CONSTRAINT `PK_ADDRESS` PRIMARY KEY (
                                             `address_id`
        );

ALTER TABLE `book_category`
    ADD CONSTRAINT `PK_BOOK_CATEGORY` PRIMARY KEY (
                                                   `book_category_id`
        );

ALTER TABLE `cart_book`
    ADD CONSTRAINT `PK_CART_BOOK` PRIMARY KEY (
                                               `cart_book_id`
        );

ALTER TABLE `coupon_type`
    ADD CONSTRAINT `PK_COUPON_TYPE` PRIMARY KEY (
                                                 `coupon_type_id`
        );

ALTER TABLE `book_publisher`
    ADD CONSTRAINT `PK_BOOK_PUBLISHER` PRIMARY KEY (
                                                    `book_publisher_id`
        );

ALTER TABLE `user`
    ADD CONSTRAINT `PK_USER` PRIMARY KEY (
                                          `user_id`
        );

ALTER TABLE `user_coupon_book_order`
    ADD CONSTRAINT `PK_USER_COUPON_BOOK_ORDER` PRIMARY KEY (
                                                            `user_coupon_book_order_id`
        );

ALTER TABLE `book`
    ADD CONSTRAINT `PK_BOOK` PRIMARY KEY (
                                          `book_id`
        );

ALTER TABLE `book_likes`
    ADD CONSTRAINT `PK_BOOK_LIKES` PRIMARY KEY (
                                                `book_id`,
                                                `user_id`
        );

ALTER TABLE `order_packaging`
    ADD CONSTRAINT `PK_ORDER_PACKAGING` PRIMARY KEY (
                                                     `order_packaging_id`
        );

ALTER TABLE `tag`
    ADD CONSTRAINT `PK_TAG` PRIMARY KEY (
                                         `tag_id`
        );

ALTER TABLE `refund`
    ADD CONSTRAINT `PK_REFUND` PRIMARY KEY (
                                            `refund_id`
        );

ALTER TABLE `coupon`
    ADD CONSTRAINT `PK_COUPON` PRIMARY KEY (
                                            `coupon_id`
        );

ALTER TABLE `cart`
    ADD CONSTRAINT `PK_CART` PRIMARY KEY (
                                          `cart_id`
        );

ALTER TABLE `book_img`
    ADD CONSTRAINT `PK_BOOK_IMG` PRIMARY KEY (
                                              `book_img_id`
        );

ALTER TABLE `payment_detail`
    ADD CONSTRAINT `PK_PAYMENT_DETAIL` PRIMARY KEY (
                                                    `payment_detail_id`
        );

ALTER TABLE `order_state`
    ADD CONSTRAINT `PK_ORDER_STATE` PRIMARY KEY (
                                                 `order_state_id`
        );

ALTER TABLE `book_tag`
    ADD CONSTRAINT `PK_BOOK_TAG` PRIMARY KEY (
                                              `book_tag_id`
        );

ALTER TABLE `order`
    ADD CONSTRAINT `PK_ORDER` PRIMARY KEY (
                                           `order_id`
        );

ALTER TABLE `payment_type`
    ADD CONSTRAINT `PK_PAYMENT_TYPE` PRIMARY KEY (
                                                  `payment_type_id`
        );

ALTER TABLE `absolute_coupon`
    ADD CONSTRAINT `FK_coupon_type_TO_absolute_coupon_1` FOREIGN KEY (
                                                                      `coupon_type_id`
        )
        REFERENCES `coupon_type` (
                                  `coupon_type_id`
            );

ALTER TABLE `payment_point_history`
    ADD CONSTRAINT `FK_payment_TO_payment_point_history_1` FOREIGN KEY (
                                                                        `payment_id`
        )
        REFERENCES `payment` (
                              `payment_id`
            );

ALTER TABLE `payment_point_history`
    ADD CONSTRAINT `FK_point_history_TO_payment_point_history_1` FOREIGN KEY (
                                                                              `point_id`
        )
        REFERENCES `point_history` (
                                    `point_id`
            );

ALTER TABLE `payment_point_history`
    ADD CONSTRAINT `FK_point_history_TO_payment_point_history_2` FOREIGN KEY (
                                                                              `user_id`
        )
        REFERENCES `point_history` (
                                    `user_id`
            );

ALTER TABLE `review`
    ADD CONSTRAINT `FK_user_TO_review_1` FOREIGN KEY (
                                                      `user_id`
        )
        REFERENCES `user` (
                           `user_id`
            );

ALTER TABLE `review`
    ADD CONSTRAINT `FK_book_order_TO_review_1` FOREIGN KEY (
                                                            `book_order_id`
        )
        REFERENCES `book_order` (
                                 `book_order_id`
            );

ALTER TABLE `review`
    ADD CONSTRAINT `FK_img_TO_review_1` FOREIGN KEY (
                                                     `img_id`
        )
        REFERENCES `img` (
                          `img_id`
            );

ALTER TABLE `user_coupon`
    ADD CONSTRAINT `FK_coupon_TO_user_coupon_1` FOREIGN KEY (
                                                             `coupon_id`
        )
        REFERENCES `coupon` (
                             `coupon_id`
            );

ALTER TABLE `user_coupon`
    ADD CONSTRAINT `FK_user_TO_user_coupon_1` FOREIGN KEY (
                                                           `user_id`
        )
        REFERENCES `user` (
                           `user_id`
            );

ALTER TABLE `book_author`
    ADD CONSTRAINT `FK_author_TO_book_author_1` FOREIGN KEY (
                                                             `author_id`
        )
        REFERENCES `author` (
                             `author_id`
            );

ALTER TABLE `book_author`
    ADD CONSTRAINT `FK_book_TO_book_author_1` FOREIGN KEY (
                                                           `book_id`
        )
        REFERENCES `book` (
                           `book_id`
            );

ALTER TABLE `point_history`
    ADD CONSTRAINT `FK_point_source_type_TO_point_history_1` FOREIGN KEY (
                                                                          `point_type_id`
        )
        REFERENCES `point_source_type` (
                                        `point_type_id`
            );

ALTER TABLE `point_history`
    ADD CONSTRAINT `FK_user_TO_point_history_1` FOREIGN KEY (
                                                             `user_id`
        )
        REFERENCES `user` (
                           `user_id`
            );

ALTER TABLE `book_sale_info`
    ADD CONSTRAINT `FK_book_TO_book_sale_info_1` FOREIGN KEY (
                                                              `book_id`
        )
        REFERENCES `book` (
                           `book_id`
            );

ALTER TABLE `relative_coupon`
    ADD CONSTRAINT `FK_coupon_type_TO_relative_coupon_1` FOREIGN KEY (
                                                                      `coupon_type_id`
        )
        REFERENCES `coupon_type` (
                                  `coupon_type_id`
            );

ALTER TABLE `book_coupon`
    ADD CONSTRAINT `FK_coupon_TO_book_coupon_1` FOREIGN KEY (
                                                             `coupon_id`
        )
        REFERENCES `coupon` (
                             `coupon_id`
            );

ALTER TABLE `book_coupon`
    ADD CONSTRAINT `FK_book_TO_book_coupon_1` FOREIGN KEY (
                                                           `book_id`
        )
        REFERENCES `book` (
                           `book_id`
            );

ALTER TABLE `category`
    ADD CONSTRAINT `FK_category_TO_category_1` FOREIGN KEY (
                                                            `parent_category_id`
        )
        REFERENCES `category` (
                               `category_id`
            );

ALTER TABLE `payment`
    ADD CONSTRAINT `FK_order_TO_payment_1` FOREIGN KEY (
                                                        `order_id`
        )
        REFERENCES `order` (
                            `order_id`
            );

ALTER TABLE `payment`
    ADD CONSTRAINT `FK_payment_detail_TO_payment_1` FOREIGN KEY (
                                                                 `payment_detail_id`
        )
        REFERENCES `payment_detail` (
                                     `payment_detail_id`
            );

ALTER TABLE `book_order`
    ADD CONSTRAINT `FK_order_TO_book_order_1` FOREIGN KEY (
                                                           `order_id`
        )
        REFERENCES `order` (
                            `order_id`
            );

ALTER TABLE `book_order`
    ADD CONSTRAINT `FK_book_TO_book_order_1` FOREIGN KEY (
                                                          `book_id`
        )
        REFERENCES `book` (
                           `book_id`
            );

ALTER TABLE `category_coupon`
    ADD CONSTRAINT `FK_coupon_TO_category_coupon_1` FOREIGN KEY (
                                                                 `coupon_id`
        )
        REFERENCES `coupon` (
                             `coupon_id`
            );

ALTER TABLE `category_coupon`
    ADD CONSTRAINT `FK_category_TO_category_coupon_1` FOREIGN KEY (
                                                                   `category_id`
        )
        REFERENCES `category` (
                               `category_id`
            );

ALTER TABLE `address`
    ADD CONSTRAINT `FK_user_TO_address_1` FOREIGN KEY (
                                                       `user_id`
        )
        REFERENCES `user` (
                           `user_id`
            );

ALTER TABLE `book_category`
    ADD CONSTRAINT `FK_book_TO_book_category_1` FOREIGN KEY (
                                                             `book_id`
        )
        REFERENCES `book` (
                           `book_id`
            );

ALTER TABLE `book_category`
    ADD CONSTRAINT `FK_category_TO_book_category_1` FOREIGN KEY (
                                                                 `category_id`
        )
        REFERENCES `category` (
                               `category_id`
            );

ALTER TABLE `cart_book`
    ADD CONSTRAINT `FK_book_TO_cart_book_1` FOREIGN KEY (
                                                         `book_id`
        )
        REFERENCES `book` (
                           `book_id`
            );

ALTER TABLE `cart_book`
    ADD CONSTRAINT `FK_cart_TO_cart_book_1` FOREIGN KEY (
                                                         `cart_id`
        )
        REFERENCES `cart` (
                           `cart_id`
            );

ALTER TABLE `book_publisher`
    ADD CONSTRAINT `FK_book_TO_book_publisher_1` FOREIGN KEY (
                                                              `book_id`
        )
        REFERENCES `book` (
                           `book_id`
            );

ALTER TABLE `book_publisher`
    ADD CONSTRAINT `FK_publisher_TO_book_publisher_1` FOREIGN KEY (
                                                                   `publisher_id`
        )
        REFERENCES `publisher` (
                                `publisher_id`
            );

ALTER TABLE `user`
    ADD CONSTRAINT `FK_membership_level_TO_user_1` FOREIGN KEY (
                                                                `membership_id`
        )
        REFERENCES `membership_level` (
                                       `membership_id`
            );

ALTER TABLE `user_coupon_book_order`
    ADD CONSTRAINT `FK_user_coupon_TO_user_coupon_book_order_1` FOREIGN KEY (
                                                                             `user_coupon_id`
        )
        REFERENCES `user_coupon` (
                                  `user_coupon_id`
            );

ALTER TABLE `user_coupon_book_order`
    ADD CONSTRAINT `FK_book_order_TO_user_coupon_book_order_1` FOREIGN KEY (
                                                                            `book_order_id`
        )
        REFERENCES `book_order` (
                                 `book_order_id`
            );

ALTER TABLE `user_coupon_book_order`
    ADD CONSTRAINT `FK_order_TO_user_coupon_book_order_1` FOREIGN KEY (
                                                                       `order_id`
        )
        REFERENCES `order` (
                            `order_id`
            );

ALTER TABLE `user_coupon_book_order`
    ADD CONSTRAINT `FK_user_TO_user_coupon_book_order_1` FOREIGN KEY (
                                                                      `user_id`
        )
        REFERENCES `user` (
                           `user_id`
            );

ALTER TABLE `book_likes`
    ADD CONSTRAINT `FK_book_TO_book_likes_1` FOREIGN KEY (
                                                          `book_id`
        )
        REFERENCES `book` (
                           `book_id`
            );

ALTER TABLE `book_likes`
    ADD CONSTRAINT `FK_user_TO_book_likes_1` FOREIGN KEY (
                                                          `user_id`
        )
        REFERENCES `user` (
                           `user_id`
            );

ALTER TABLE `order_packaging`
    ADD CONSTRAINT `FK_packaging_option_TO_order_packaging_1` FOREIGN KEY (
                                                                           `package_id`
        )
        REFERENCES `packaging_option` (
                                       `package_id`
            );

ALTER TABLE `order_packaging`
    ADD CONSTRAINT `FK_book_order_TO_order_packaging_1` FOREIGN KEY (
                                                                     `book_order_id`
        )
        REFERENCES `book_order` (
                                 `book_order_id`
            );

ALTER TABLE `refund`
    ADD CONSTRAINT `FK_order_TO_refund_1` FOREIGN KEY (
                                                       `order_id`
        )
        REFERENCES `order` (
                            `order_id`
            );

ALTER TABLE `coupon`
    ADD CONSTRAINT `FK_coupon_type_TO_coupon_1` FOREIGN KEY (
                                                             `coupon_type_id`
        )
        REFERENCES `coupon_type` (
                                  `coupon_type_id`
            );

ALTER TABLE `cart`
    ADD CONSTRAINT `FK_user_TO_cart_1` FOREIGN KEY (
                                                    `user_id`
        )
        REFERENCES `user` (
                           `user_id`
            );

ALTER TABLE `book_img`
    ADD CONSTRAINT `FK_book_TO_book_img_1` FOREIGN KEY (
                                                        `book_id`
        )
        REFERENCES `book` (
                           `book_id`
            );

ALTER TABLE `book_img`
    ADD CONSTRAINT `FK_img_TO_book_img_1` FOREIGN KEY (
                                                       `img_id`
        )
        REFERENCES `img` (
                          `img_id`
            );

ALTER TABLE `payment_detail`
    ADD CONSTRAINT `FK_payment_type_TO_payment_detail_1` FOREIGN KEY (
                                                                      `payment_type_id`
        )
        REFERENCES `payment_type` (
                                   `payment_type_id`
            );

ALTER TABLE `book_tag`
    ADD CONSTRAINT `FK_tag_TO_book_tag_1` FOREIGN KEY (
                                                       `tag_id`
        )
        REFERENCES `tag` (
                          `tag_id`
            );

ALTER TABLE `book_tag`
    ADD CONSTRAINT `FK_book_TO_book_tag_1` FOREIGN KEY (
                                                        `book_id`
        )
        REFERENCES `book` (
                           `book_id`
            );

ALTER TABLE `order`
    ADD CONSTRAINT `FK_order_state_TO_order_1` FOREIGN KEY (
                                                            `order_state_id`
        )
        REFERENCES `order_state` (
                                  `order_state_id`
            );

ALTER TABLE `order`
    ADD CONSTRAINT `FK_delivery_rule_TO_order_1` FOREIGN KEY (
                                                              `delivery_rule_id`
        )
        REFERENCES `delivery_rule` (
                                    `delivery_rule_id`
            );

ALTER TABLE `order`
    ADD CONSTRAINT `FK_user_TO_order_1` FOREIGN KEY (
                                                     `user_id`
        )
        REFERENCES `user` (
                           `user_id`
            );

