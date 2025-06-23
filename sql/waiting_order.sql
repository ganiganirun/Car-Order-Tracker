-- insert_waiting_orders.sql
USE osid;

SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE waiting_orders;
SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO waiting_orders (order_id, waiting_status, created_at, updated_at)
SELECT o.id, 'WAITING', NOW(), NOW()
FROM orders o
WHERE o.id BETWEEN 1 AND 50;