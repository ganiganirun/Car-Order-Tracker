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
