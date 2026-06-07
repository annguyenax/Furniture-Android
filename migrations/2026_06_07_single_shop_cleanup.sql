-- Single-shop cleanup migration.
-- Run after backing up furniture_db.

USE furniture_db;

DROP PROCEDURE IF EXISTS drop_fk_for_column;
DROP PROCEDURE IF EXISTS add_column_if_missing;
DROP PROCEDURE IF EXISTS drop_column_if_exists;
DROP PROCEDURE IF EXISTS add_fk_if_missing;
DROP PROCEDURE IF EXISTS fail_if_order_items_missing_order;

DELIMITER //

CREATE PROCEDURE drop_fk_for_column(IN table_name_in VARCHAR(64), IN column_name_in VARCHAR(64))
BEGIN
    DECLARE fk_name VARCHAR(64);

    SELECT kcu.CONSTRAINT_NAME
      INTO fk_name
      FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE kcu
     WHERE kcu.TABLE_SCHEMA = DATABASE()
       AND kcu.TABLE_NAME = table_name_in
       AND kcu.COLUMN_NAME = column_name_in
       AND kcu.REFERENCED_TABLE_NAME IS NOT NULL
     LIMIT 1;

    IF fk_name IS NOT NULL THEN
        SET @sql_text = CONCAT('ALTER TABLE `', table_name_in, '` DROP FOREIGN KEY `', fk_name, '`');
        PREPARE stmt FROM @sql_text;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END//

CREATE PROCEDURE add_column_if_missing(
    IN table_name_in VARCHAR(64),
    IN column_name_in VARCHAR(64),
    IN column_def_in TEXT
)
BEGIN
    IF NOT EXISTS (
        SELECT 1
          FROM INFORMATION_SCHEMA.COLUMNS
         WHERE TABLE_SCHEMA = DATABASE()
           AND TABLE_NAME = table_name_in
           AND COLUMN_NAME = column_name_in
    ) THEN
        SET @sql_text = CONCAT('ALTER TABLE `', table_name_in, '` ADD COLUMN `', column_name_in, '` ', column_def_in);
        PREPARE stmt FROM @sql_text;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END//

CREATE PROCEDURE drop_column_if_exists(IN table_name_in VARCHAR(64), IN column_name_in VARCHAR(64))
BEGIN
    IF EXISTS (
        SELECT 1
          FROM INFORMATION_SCHEMA.COLUMNS
         WHERE TABLE_SCHEMA = DATABASE()
           AND TABLE_NAME = table_name_in
           AND COLUMN_NAME = column_name_in
    ) THEN
        SET @sql_text = CONCAT('ALTER TABLE `', table_name_in, '` DROP COLUMN `', column_name_in, '`');
        PREPARE stmt FROM @sql_text;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END//

CREATE PROCEDURE add_fk_if_missing(
    IN table_name_in VARCHAR(64),
    IN fk_name_in VARCHAR(64),
    IN column_name_in VARCHAR(64),
    IN referenced_table_in VARCHAR(64),
    IN referenced_column_in VARCHAR(64)
)
BEGIN
    IF NOT EXISTS (
        SELECT 1
          FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS
         WHERE TABLE_SCHEMA = DATABASE()
           AND TABLE_NAME = table_name_in
           AND CONSTRAINT_NAME = fk_name_in
    ) THEN
        SET @sql_text = CONCAT(
            'ALTER TABLE `', table_name_in, '` ADD CONSTRAINT `', fk_name_in,
            '` FOREIGN KEY (`', column_name_in, '`) REFERENCES `',
            referenced_table_in, '` (`', referenced_column_in, '`)'
        );
        PREPARE stmt FROM @sql_text;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END//

CREATE PROCEDURE fail_if_order_items_missing_order()
BEGIN
    IF EXISTS (
        SELECT 1
          FROM order_items
         WHERE order_id IS NULL
         LIMIT 1
    ) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Cannot migrate order_items: some rows have no matching sub_orders.order_id';
    END IF;
END//

DELIMITER ;

START TRANSACTION;

DELETE ur
  FROM user_roles ur
  JOIN roles r ON ur.role_id = r.role_id
 WHERE r.role_name NOT IN ('CUSTOMER', 'ADMIN');

DELETE FROM roles
 WHERE role_name NOT IN ('CUSTOMER', 'ADMIN');

CALL add_column_if_missing('orders', 'recipient_name', 'VARCHAR(100) NULL');
CALL add_column_if_missing('orders', 'recipient_phone', 'VARCHAR(15) NULL');
CALL add_column_if_missing('orders', 'shipping_address_text', 'VARCHAR(500) NULL');

UPDATE orders o
  JOIN addresses a ON a.address_id = o.shipping_address_id
   SET o.recipient_name = COALESCE(o.recipient_name, a.recipient_name),
       o.recipient_phone = COALESCE(o.recipient_phone, a.phone),
       o.shipping_address_text = COALESCE(
           o.shipping_address_text,
           TRIM(BOTH ', ' FROM CONCAT_WS(', ', a.address_line, a.ward, a.district, a.city))
       );

ALTER TABLE orders MODIFY shipping_address_id INT NULL;
ALTER TABLE orders MODIFY payment_method ENUM('COD','BANK_TRANSFER') NOT NULL;

ALTER TABLE payments MODIFY payment_method ENUM('COD','BANK_TRANSFER') NOT NULL;

CALL add_column_if_missing('order_items', 'order_id', 'INT NULL');

UPDATE order_items oi
  JOIN sub_orders so ON so.sub_order_id = oi.sub_order_id
   SET oi.order_id = so.order_id
 WHERE oi.order_id IS NULL;

CALL fail_if_order_items_missing_order();

ALTER TABLE order_items MODIFY order_id INT NOT NULL;
CALL drop_fk_for_column('order_items', 'sub_order_id');
CALL drop_column_if_exists('order_items', 'sub_order_id');
CALL add_fk_if_missing('order_items', 'fk_order_items_orders', 'order_id', 'orders', 'order_id');

CALL drop_fk_for_column('products', 'shop_id');
CALL drop_column_if_exists('products', 'shop_id');

CALL drop_fk_for_column('cart_items', 'shop_id');
CALL drop_column_if_exists('cart_items', 'shop_id');

DROP TABLE IF EXISTS user_coupons;
DROP TABLE IF EXISTS coupons;
DROP TABLE IF EXISTS shipments;
DROP TABLE IF EXISTS shippers;
DROP TABLE IF EXISTS shop_reviews;
DROP TABLE IF EXISTS sub_orders;
DROP TABLE IF EXISTS shops;

COMMIT;

DROP PROCEDURE IF EXISTS drop_fk_for_column;
DROP PROCEDURE IF EXISTS add_column_if_missing;
DROP PROCEDURE IF EXISTS drop_column_if_exists;
DROP PROCEDURE IF EXISTS add_fk_if_missing;
DROP PROCEDURE IF EXISTS fail_if_order_items_missing_order;
