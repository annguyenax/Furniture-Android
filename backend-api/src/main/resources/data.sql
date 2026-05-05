-- ============================================================
-- Seed data for Furniture App development
-- Chạy lại an toàn nhờ INSERT IGNORE và UPDATE với điều kiện
-- ============================================================

-- Roles
INSERT IGNORE INTO Roles (role_id, role_name) VALUES
(1, 'CUSTOMER'),
(2, 'VENDOR'),
(3, 'ADMIN'),
(4, 'SHIPPER');

-- ============================================================
-- Thêm ảnh cho category nếu chưa có
-- ============================================================
UPDATE Categories
SET image = CONCAT('https://picsum.photos/seed/cat', category_id, '/400/300')
WHERE image IS NULL OR image = '';

-- ============================================================
-- Thêm ảnh cho product_variants nếu chưa có (dùng picsum.photos)
-- ============================================================
UPDATE Product_Variants
SET image_url = CONCAT('https://picsum.photos/seed/variant', variant_id, '/500/500')
WHERE image_url IS NULL OR image_url = '';
