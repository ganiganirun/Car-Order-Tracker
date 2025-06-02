INSERT INTO models (id,
                    name,
                    color,
                    description,
                    image,
                    category,
                    seat_count,
                    price,
                    created_at,
                    updated_at,
                    deleted_at)
VALUES (1, '모델명1', 'RED', '모델 설명1', '/imageURL.png', 'SAFETY', '5', 18000000, NOW(), NOW(), NULL),

       (2, '모델명2', 'RED', '모델 설명2', '/imageURL.png', 'SAFETY', '7', 40000000, NOW(),
        NOW(),
        NULL),

       (3, '모델명3', 'RED', '모델 설명3', '/imageURL.png', 'EASE', '9', 46000000, NOW(), NOW(),
        NULL);
