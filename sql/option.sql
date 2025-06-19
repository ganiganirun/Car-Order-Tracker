INSERT INTO options (id,
                     name,
                     description,
                     image,
                     category,
                     price,
                     created_at,
                     updated_at,
                     deleted_at)
VALUES (1, '네비1', '옵션 설명1', '/imageURL.png', 'NAVIGATION', 1000, NOW(), NOW(), NULL),

       (2, '선루프', '옵션 설명2', '/imageURL.png', 'SUNROOF', 4000, NOW(), NOW(), NULL),

       (3, '주차 센서', '옵션 설명3', '/imageURL.png', 'PARKING_SENSOR', 5000, NOW(), NOW(),
        NULL);


INSERT INTO options (name, description, image, category, price, created_at, updated_at)
SELECT CONCAT('옵션-', seq)              AS name,
       CONCAT('설명-', seq)              AS description,
       'https://example.com/image.png' AS image,
       'SUNROOF'                       AS category, -- OptionCategory Enum 중 하나
       FLOOR(1000 + RAND() * 9000)     AS price,
       NOW(),
       NOW()
FROM (SELECT @rownum := @rownum + 1 AS seq
      FROM information_schema.columns a,
           information_schema.columns b,
           (SELECT @rownum := 0) r
      LIMIT 10000) AS tmp;