START TRANSACTION;

-- 1) id=1 키 ASSIGNED 처리 및 마스터A 생성
UPDATE licensekeys
SET license_status = 'ASSIGNED',
    owner_id       = 1,
    assigned_at    = NOW()
WHERE id = 1
  AND license_status = 'AVAILABLE';

INSERT INTO masters (business_number,
                     name,
                     phone_number,
                     email,
                     password,
                     address,
                     product_key,
                     role,
                     is_deleted,
                     created_at,
                     updated_at)
SELECT '123-45-67890',        -- business_number
       '마스터A',                -- name
       '010-1234-5678',       -- phone_number
       'masterA@example.com', -- email
       '$2b$10$zlKr2tiDpfkNgifvCglcg.c01kXBOJre3Th3v5jqyj2sbPzi66Rfu',
       '서울시 강남구 역삼동 1-1',     -- address
       lk.product_key,        -- 방금 ASSIGNED 한 키
       'MASTER',
       FALSE,
       NOW(),
       NOW()
FROM licensekeys lk
WHERE lk.id = 1;

-- 2) id=2 키 ASSIGNED 처리 및 마스터B 생성
UPDATE licensekeys
SET license_status = 'ASSIGNED',
    owner_id       = 2,
    assigned_at    = NOW()
WHERE id = 2
  AND license_status = 'AVAILABLE';

INSERT INTO masters (business_number,
                     name,
                     phone_number,
                     email,
                     password,
                     address,
                     product_key,
                     role,
                     is_deleted,
                     created_at,
                     updated_at)
SELECT '123-45-67890', -- 동일한 사업자 번호
       '마스터B',
       '010-2345-6789',
       'masterB@example.com',
       '$2b$10$zlKr2tiDpfkNgifvCglcg.c01kXBOJre3Th3v5jqyj2sbPzi66Rfu',
       '서울시 강남구 역삼동 2-2',
       lk.product_key,
       'MASTER',
       FALSE,
       NOW(),
       NOW()
FROM licensekeys lk
WHERE lk.id = 2;

-- 3) id=3 키 ASSIGNED 처리 및 마스터C 생성
UPDATE licensekeys
SET license_status = 'ASSIGNED',
    owner_id       = 3,
    assigned_at    = NOW()
WHERE id = 3
  AND license_status = 'AVAILABLE';

INSERT INTO masters (business_number,
                     name,
                     phone_number,
                     email,
                     password,
                     address,
                     product_key,
                     role,
                     is_deleted,
                     created_at,
                     updated_at)
SELECT '987-65-43210', -- 다른 사업자 번호
       '마스터C',
       '010-3456-7890',
       'masterC@example.com',
       '$2b$10$zlKr2tiDpfkNgifvCglcg.c01kXBOJre3Th3v5jqyj2sbPzi66Rfu',
       '서울시 강남구 역삼동 3-3',
       lk.product_key,
       'MASTER',
       FALSE,
       NOW(),
       NOW()
FROM licensekeys lk
WHERE lk.id = 3;

COMMIT;



# INSERT INTO masters (id,
#                      business_number,
#                      name,
#                      phone_number,
#                      email,
#                      password,
#                      address,
#                      product_key,
#                      role,
#                      is_deleted,
#                      deleted_at,
#                      created_at,
#                      updated_at)
# VALUES (1,
#         '123-45-67890', -- 두 계정이 같은 사업자 번호
#         '마스터A',
#         '010-1234-5678',
#         'masterA@example.com',
#         '$2b$10$zlKr2tiDpfkNgifvCglcg.c01kXBOJre3Th3v5jqyj2sbPzi66Rfu',
#         '서울시 강남구 역삼동 1-1',
#         'LIC-20250101',
#         'MASTER',
#         FALSE,
#         NULL,
#         NOW(),
#         NOW()),
#        (2,
#         '123-45-67890', -- 같은 사업자 번호
#         '마스터B',
#         '010-2345-6789',
#         'masterB@example.com',
#         '$2b$10$zlKr2tiDpfkNgifvCglcg.c01kXBOJre3Th3v5jqyj2sbPzi66Rfu',
#         '서울시 강남구 역삼동 2-2',
#         'LIC-20250102',
#         'MASTER',
#         FALSE,
#         NULL,
#         NOW(),
#         NOW()),
#        (3,
#         '987-65-43210', -- 다른 사업자 번호
#         '마스터C',
#         '010-3456-7890',
#         'masterC@example.com',
#         '$2b$10$zlKr2tiDpfkNgifvCglcg.c01kXBOJre3Th3v5jqyj2sbPzi66Rfu',
#         '서울시 강남구 역삼동 3-3',
#         'LIC-20250103',
#         'MASTER',
#         FALSE,
#         NULL,
#         NOW(),
#         NOW());
