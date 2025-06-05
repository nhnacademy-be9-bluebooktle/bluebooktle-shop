-- ===============================================
-- 1) 샘플 책 1권 추가
-- ===============================================
INSERT INTO book (title,
                  description,
                  isbn,
                  publish_date,
                  created_at)
VALUES ('코딩 대모험',
        '프로그래밍 입문자를 위한 상상 가득 코딩 여행기',
        '9788912345678',
        '2025-05-01',
        CURRENT_TIMESTAMP);

-- 2) 샘플 이미지 추가 (Img 테이블)
INSERT INTO img (img_url, created_at)
VALUES ('https://image.aladin.co.kr/product/13537/89/cover200/k762532810_1.jpg',
        CURRENT_TIMESTAMP);

-- MySQL 기준: 방금 넣은 img의 자동증가 PK를 변수에 보관
SET @imgId = LAST_INSERT_ID();

-- 3) book_img 에 썸네일로 연결
-- (book_id = 1, img_id = @imgId, is_thumbnail = TRUE)
INSERT INTO book_img (book_id, img_id, is_thumbnail)
VALUES (1, @imgId, TRUE);

-- 4) 도서 판매 정보(book_sale_info) 삽입
INSERT INTO book_sale_info (book_id,
                            price,
                            sale_price,
                            stock,
                            is_packable,
                            sale_percentage,
                            state,
                            created_at)
VALUES (1, -- book_id = 1
        20000.00,
        18000.00,
        100,
        TRUE,
        10.00, -- sale_percentage = 10%
        'AVAILABLE',
        CURRENT_TIMESTAMP);

-- 5) 저자 삽입 및 book_author 매핑
INSERT INTO author (name, created_at)
VALUES ('홍길동', CURRENT_TIMESTAMP);

-- 방금 추가된 author의 PK를 변수에 담기
SET @authorId = LAST_INSERT_ID();

-- book_author 중간 테이블에 매핑 (book_id=1, author_id=@authorId)
INSERT INTO book_author (book_id, author_id)
VALUES (1, @authorId);


-- 6) 출판사(publisher) 삽입 + book_publisher 매핑
INSERT INTO publisher (name, created_at)
VALUES ('에이콘출판사', CURRENT_TIMESTAMP);
SET @pub1 = LAST_INSERT_ID();

INSERT INTO publisher (name, created_at)
VALUES ('인사이트', CURRENT_TIMESTAMP);
SET @pub2 = LAST_INSERT_ID();

INSERT INTO publisher (name, created_at)
VALUES ('길벗', CURRENT_TIMESTAMP);
SET @pub3 = LAST_INSERT_ID();

-- '코딩 대모험'(book_id=1) → '에이콘출판사' 연결
INSERT INTO book_publisher (book_id, publisher_id)
VALUES (1, @pub1);

