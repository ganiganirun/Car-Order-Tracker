-- insert_orders.sql
USE osid;
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE order_option;
TRUNCATE TABLE orders;
SET FOREIGN_KEY_CHECKS = 1;

-- 50개의 샘플 주문 데이터 삽입 (ID: 1 ~ 50)
-- 주문당 옵션 3개 (option_id 1, 2, 3) 연결
DELIMITER $$

DROP PROCEDURE IF EXISTS insert_sample_orders $$
CREATE PROCEDURE insert_sample_orders()
BEGIN
    DECLARE i INT DEFAULT 1;

    WHILE i <= 50 DO
        INSERT INTO orders (id,
                            address,
                            merchant_uid,
                            body_number,
                            total_price,
                            order_status,
                            expected_delivery_date,
                            actual_delivery_date,
                            user_id,
                            dealer_id,
                            model_id,
                            created_at,
                            updated_at)
        VALUES (i,
                CONCAT('서울시 강남구 역삼동 ', i, '-1'),
                CONCAT('order_', UUID()),
                CONCAT('car_', UUID()),
                18000,
                'ORDERED',
                NULL,
                NULL,
                1,
                1,
                1,
                NOW(),
                NOW());

INSERT INTO order_option (order_id, option_id) VALUES
                                                   (i, 1),
                                                   (i, 2),
                                                   (i, 3);

SET i = i + 1;
END WHILE;
END $$
DELIMITER ;

CALL insert_sample_orders();
DROP PROCEDURE insert_sample_orders;


-- -- insert_orders.sql
-- -- 1) 기존 주문 및 주문-옵션 데이터 초기화
-- USE osid;
-- SET FOREIGN_KEY_CHECKS = 0;
-- TRUNCATE TABLE order_option;
-- TRUNCATE TABLE orders;
-- SET FOREIGN_KEY_CHECKS = 1;
--
-- -- 2) 샘플 주문 데이터 삽입 (주문 ID = 1, User ID = 1, Dealer ID = 1, Model ID = 1)
-- INSERT INTO orders (id,
--                     address,
--                     merchant_uid,
--                     body_number,
--                     total_price,
--                     order_status,
--                     expected_delivery_date,
--                     actual_delivery_date,
--                     user_id,
--                     dealer_id,
--                     model_id,
--                     created_at,
--                     updated_at)
-- VALUES (1,
--         '서울시 강남구 역삼동 1-1', -- address
--         CONCAT('order_', REPLACE(UUID(), '-', '')), -- merchant_uid
--         CONCAT('car_', REPLACE(UUID(), '-', '')), -- body_number
--         18000, -- total_price (모델 10000 + 옵션 1000+4000+5000)
--         'ORDERED', -- order_status
--         NULL,
--         NULL,
--         1, -- user_id = 1
--         1, -- dealer_id = 1
--         1, -- model_id = 1
--         NOW(),
--         NOW());
--
-- -- 3) 주문-옵션 매핑 데이터 삽입 (Order 1 ↔ Options 1,2,3)
-- INSERT INTO order_option (order_id,
--                           option_id)
-- VALUES (1, 1),
--        (1, 2),
--        (1, 3);
