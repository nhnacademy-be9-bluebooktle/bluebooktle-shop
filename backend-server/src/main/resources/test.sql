-- 도서 더미 데이터
-- 샘플 책 1권 추가
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

-- img 테이블에 이미지 URL 등록
INSERT INTO img (img_url,
                 created_at -- BaseEntity 에 매핑된 생성일자 컬럼
)
VALUES ('https://image.aladin.co.kr/product/13537/89/cover200/k762532810_1.jpg',
        CURRENT_TIMESTAMP);

-- 방금 등록된 img_id 가져오기 (MySQL 기준)
SET @imgId = LAST_INSERT_ID();

-- book_img 에 썸네일로 맵핑 (book_id = 1 로 가정)
INSERT INTO book_img (book_id,
                      img_id,
                      is_thumbnail)
VALUES (1,
        @imgId,
        TRUE);

-- 도서 판매 정보
INSERT INTO book_sale_info (book_id,
                            price,
                            sale_price,
                            stock,
                            is_packable,
                            sale_percentage,
                            state,
                            created_at)
VALUES (1, -- 방금 만든 책(book_id=1)에 대응
        20000.00, -- 정가
        18000.00, -- 할인된 가격
        100, -- 재고 수량
        TRUE, -- 포장 여부
        10.00, -- 할인율(퍼센트)
        'AVAILABLE', -- 판매 상태 (BookSaleInfoState.AVAILABLE)
        CURRENT_TIMESTAMP);

-- author 테이블에 저자 추가
INSERT INTO author (name, created_at)
VALUES ('홍길동', CURRENT_TIMESTAMP);

-- 방금 추가된 author_id 꺼내오기
SET @authorId = LAST_INSERT_ID();

-- book_author (책-저자 중간) 테이블에 맵핑
INSERT INTO book_author (book_id, author_id)
VALUES (1, @authorId);


-- user_id=1 사용자가 book_id=2 번 도서를 좋아요에 추가
INSERT INTO book_likes (book_id, user_id)
VALUES (1, 1);

-- 샘플 책 1권 추가 (book_id=2 예상)
INSERT INTO book (title,
                  description,
                  isbn,
                  publish_date,
                  created_at)
VALUES ('자바의 정석',
        '자바의 기초부터 실무 활용까지! 입문자를 위한 필독서',
        '9788970501234',
        '2025-05-15',
        CURRENT_TIMESTAMP);

-- img 테이블에 이미지 URL 등록
INSERT INTO img (img_url,
                 created_at)
VALUES ('https://image.aladin.co.kr/product/22563/24/cover200/k742831911_1.jpg',
        CURRENT_TIMESTAMP);

-- 방금 등록된 img_id 가져오기
SET @imgId = LAST_INSERT_ID();

-- book_img 에 썸네일로 맵핑 (book_id=2 가정)
INSERT INTO book_img (book_id,
                      img_id,
                      is_thumbnail)
VALUES (2,
        @imgId,
        TRUE);

-- 도서 판매 정보
INSERT INTO book_sale_info (book_id,
                            price,
                            sale_price,
                            stock,
                            is_packable,
                            sale_percentage,
                            state,
                            created_at)
VALUES (2,
        32000.00,
        28800.00,
        50,
        TRUE,
        10.00,
        'AVAILABLE',
        CURRENT_TIMESTAMP);

-- author 테이블에 저자 추가
INSERT INTO author (name, created_at)
VALUES ('남궁성', CURRENT_TIMESTAMP);

-- 방금 추가된 author_id 가져오기
SET @authorId = LAST_INSERT_ID();

-- book_author 테이블에 맵핑
INSERT INTO book_author (book_id, author_id)
VALUES (2, @authorId);

-- user_id=1 사용자가 book_id=2 도서를 좋아요에 추가
INSERT INTO book_likes (book_id, user_id)
VALUES (2, 1);



