# Đề xuất dọn database cho shop một cửa hàng

Tài liệu này chỉ là proposal để review trước. Chưa xóa bảng, chưa sửa schema, chưa chạy SQL.

## Bối cảnh hiện tại

Project hiện tại là một shop nội thất duy nhất, không phải marketplace nhiều shop/vendor. App cũng chỉ cần 2 role:

- `CUSTOMER`
- `ADMIN`

Trong database hiện đang còn nhiều dấu vết của mô hình multi-vendor:

- Role thừa: `SELLER`, `SHIPPER`, `VENDOR`
- Bảng shop/vendor: `shops`, `shop_reviews`
- Bảng chia đơn theo shop: `sub_orders`
- Bảng giao hàng theo shipper riêng: `shippers`, `shipments`
- Coupon đang gắn theo shop: `coupons.shop_id`
- Nhiều bảng/model vẫn dùng `shop_id`

## Kết luận nhanh

Nên dọn theo 2 pha:

1. Dọn an toàn trước: xóa role thừa, khóa hệ thống về một shop duy nhất, sửa logic địa chỉ khi checkout để không tạo rác.
2. Refactor schema sau: bỏ hẳn các bảng/field multi-shop sau khi đã sửa backend và Android không còn phụ thuộc vào `shop_id`, `sub_orders`.

Không nên drop bảng ngay trong một lần, vì hiện tại order flow đang phụ thuộc khá sâu vào `sub_orders` và `shop_id`.

## Role

### Hiện trạng

Trong `furniture_db.sql`, bảng `roles` đang có:

- `CUSTOMER`
- `SELLER`
- `ADMIN`
- `SHIPPER`
- `VENDOR`

Backend hiện tại đã seed đúng 2 role trong `DataInitializer`:

- `CUSTOMER`
- `ADMIN`

### Đề xuất

Giữ bảng `roles` và `user_roles` ở pha này, vì Spring Security hiện đang map user role bằng quan hệ nhiều-nhiều trong `User.roles`.

Chỉ dọn data thừa:

- Xóa mapping role không còn dùng trong `user_roles`
- Xóa role không thuộc `CUSTOMER`, `ADMIN`

SQL review trước:

```sql
DELETE ur
FROM user_roles ur
JOIN roles r ON ur.role_id = r.role_id
WHERE r.role_name NOT IN ('CUSTOMER', 'ADMIN');

DELETE FROM roles
WHERE role_name NOT IN ('CUSTOMER', 'ADMIN');
```

### Có nên bỏ bảng `roles/user_roles` luôn không?

Chưa nên làm ngay. Nếu muốn tối giản mạnh hơn, có thể đổi thành `users.role ENUM('CUSTOMER','ADMIN')`, nhưng sẽ phải refactor:

- `User.roles`
- `Role`, `RoleRepository`
- `AuthServiceImpl`
- `CustomUserDetailsService`
- `JwtTokenProvider`
- `DataInitializer`
- Response user/auth trả roles

Vì scope hiện tại là dọn DB cho shop một cửa hàng, giữ `roles/user_roles` nhưng chỉ còn 2 role là phương án ít rủi ro hơn.

## Single shop

### Hiện trạng

Code đang giả định shop mặc định là `1` ở nhiều chỗ:

- `AdminController`: tạo sản phẩm dùng `shopId` từ request hoặc mặc định `1`
- `ChatController`: hard-code `SHOP_ID = 1`
- Android chat/home/profile cũng truyền `shop_id = 1`

Nhưng schema vẫn thiết kế như marketplace:

- `products.shop_id`
- `cart_items.shop_id`
- `coupons.shop_id`
- `sub_orders.shop_id`
- `shops`
- `shop_reviews`

### Đề xuất pha 1

Giữ tạm `shops` với đúng 1 record để tránh vỡ foreign key.

Dọn data:

- Chỉ giữ shop chính `shop_id = 1`
- Đảm bảo toàn bộ `products`, `cart_items`, `sub_orders`, `coupons` nếu có đều trỏ về `shop_id = 1`
- Không mở API/UI tạo shop mới
- Không dùng role seller/vendor nữa

SQL kiểm tra trước:

```sql
SELECT role_id, role_name FROM roles;

SELECT shop_id, shop_name, owner_id, status
FROM shops;

SELECT shop_id, COUNT(*) AS total_products
FROM products
GROUP BY shop_id;

SELECT shop_id, COUNT(*) AS total_cart_items
FROM cart_items
GROUP BY shop_id;

SELECT shop_id, COUNT(*) AS total_sub_orders
FROM sub_orders
GROUP BY shop_id;
```

### Đề xuất pha 2

Sau khi sửa code, có thể bỏ hẳn mô hình shop/vendor:

- Drop `shop_reviews`
- Drop `shops`
- Xóa `shop_id` khỏi `products`
- Xóa `shop_id` khỏi `cart_items`
- Xóa `shop_id` khỏi `coupons` nếu coupon là global toàn shop
- Bỏ endpoint `GET /products/shop/{shopId}`
- Bỏ model Android `Shop` và màn `ShopDetailActivity` nếu không còn dùng

## Sub orders

### Hiện trạng

`sub_orders` hiện dùng để chia một đơn chính thành nhiều đơn con theo shop. Với project một shop, bảng này không còn ý nghĩa nghiệp vụ.

Nhưng hiện tại `order_items` đang trỏ tới `sub_orders` bằng `sub_order_id`, không trỏ trực tiếp tới `orders`.

Luồng order hiện tại:

```text
orders
  -> sub_orders
      -> order_items
```

Với một shop, nên đổi thành:

```text
orders
  -> order_items
```

### Đề xuất

Không drop `sub_orders` ngay. Đây là refactor lớn.

Khi làm pha 2:

- Thêm `order_items.order_id`
- Migrate data từ `sub_orders.order_id` sang `order_items.order_id`
- Sửa `OrderItem` model
- Sửa `OrderItemRepository`
- Sửa `OrderServiceImpl.createOrder`
- Sửa `OrderServiceImpl.getOrderItems`
- Sửa `cancelOrder`, `confirmReceived`
- Sửa return request đang validate qua `SubOrder`
- Sau khi app chạy ổn mới drop `sub_orders`

SQL migrate minh họa:

```sql
ALTER TABLE order_items ADD COLUMN order_id INT NULL;

UPDATE order_items oi
JOIN sub_orders so ON oi.sub_order_id = so.sub_order_id
SET oi.order_id = so.order_id;

ALTER TABLE order_items MODIFY order_id INT NOT NULL;
ALTER TABLE order_items ADD CONSTRAINT fk_order_items_orders
FOREIGN KEY (order_id) REFERENCES orders(order_id);
```

Sau khi backend không còn dùng `sub_order_id` mới tính chuyện drop column/table.

## Shippers và shipments

### Hiện trạng

Có bảng:

- `shippers`
- `shipments`

Nhưng app hiện tại không có role shipper thật, không có flow shipper nhận đơn/giao đơn độc lập. Admin chỉ đổi trạng thái đơn.

### Đề xuất

Nếu project chỉ là shop đơn giản:

- Bỏ role `SHIPPER`
- Bỏ module shipper
- Drop `shipments`
- Drop `shippers`

Lưu ý: `shipments` đang FK tới `sub_orders`, nên nên dọn cùng pha refactor `sub_orders`.

## Coupons

### Hiện trạng

Có bảng:

- `coupons`
- `user_coupons`

Trong dump hiện tại hai bảng này chưa có data. Checkout Android hiện tại chưa thấy flow chọn mã giảm giá.

### Đề xuất

Có 2 lựa chọn:

1. Nếu chưa cần coupon: drop `user_coupons`, `coupons`.
2. Nếu sẽ làm coupon sau: giữ `coupons`, nhưng bỏ `shop_id`, vì shop chỉ có một.

Khuyến nghị hiện tại: nếu deadline cần app gọn, bỏ tạm coupon để giảm schema thừa. Sau này cần voucher thì thêm lại theo thiết kế global.

## Shop reviews

### Hiện trạng

`shop_reviews` là đánh giá cho từng shop trong marketplace. App hiện tại đã có `product_reviews`, phù hợp hơn với shop nội thất một cửa hàng.

### Đề xuất

Drop `shop_reviews`.

Giữ `product_reviews`, vì đánh giá sản phẩm vẫn cần và đã được app dùng.

## Addresses

### Hiện trạng bảng

Bảng `addresses` hiện có các cột chính:

- `address_id`
- `user_id`
- `recipient_name`
- `phone`
- `address_line`
- `ward`
- `district`
- `city`
- `is_default`
- `created_at`
- `updated_at`

Schema này ổn cho sổ địa chỉ của user.

### Vấn đề đang có

Không nên xóa bảng `addresses`, nhưng logic checkout hiện tại đang làm bảng này bị rác.

Trong `OrderServiceImpl.createOrFindAddress`, mỗi lần tạo order backend lại tạo một row address mới:

```text
addressLine = request.shippingAddress
city = "Vietnam"
district = "District"
ward = "Ward"
isDefault = false
```

Dump hiện tại cho thấy rất nhiều row bị lặp kiểu:

- `Vietnam`
- `District`
- `Ward`
- address line chứa full address đã ghép sẵn

Nguyên nhân: Android chọn địa chỉ đã lưu, nhưng khi checkout chỉ gửi `shippingAddress` dạng text. Backend không nhận `addressId`, nên nó tạo địa chỉ mới cho mỗi đơn.

### Đề xuất sửa logic địa chỉ

Giữ `addresses` làm sổ địa chỉ user, nhưng checkout phải dùng `addressId`.

Backend:

- Thêm `addressId` vào `CreateOrderRequest`
- Khi tạo order, validate address đó thuộc user hiện tại
- Không tạo row address mới nếu user đã chọn address có sẵn
- Nếu muốn lưu lịch sử địa chỉ ổn định, thêm snapshot vào `orders`

Android:

- `CheckoutActivity` đang có `selectedAddress.getAddressId()`
- Gửi `addressId` lên API thay vì chỉ gửi `fullAddress`

Khuyến nghị tốt nhất cho lịch sử đơn:

- `addresses`: sổ địa chỉ hiện tại của user
- `orders`: lưu snapshot địa chỉ lúc đặt hàng

Ví dụ thêm vào `orders`:

```sql
ALTER TABLE orders
ADD COLUMN recipient_name varchar(100) NULL,
ADD COLUMN recipient_phone varchar(15) NULL,
ADD COLUMN shipping_address_text varchar(500) NULL;
```

Khi tạo đơn:

- Lấy address theo `addressId`
- Copy `recipient_name`, `phone`, `fullAddress` vào order snapshot
- Có thể vẫn lưu `shipping_address_id` để biết user chọn address nào

Lý do cần snapshot: nếu khách sửa/xóa địa chỉ sau này, đơn cũ vẫn phải hiển thị đúng địa chỉ lúc đã đặt.

### Dọn data địa chỉ

Chỉ dọn sau khi đã migrate order snapshot hoặc chắc chắn order vẫn có địa chỉ hợp lệ.

SQL kiểm tra địa chỉ rác:

```sql
SELECT city, district, ward, COUNT(*) AS total
FROM addresses
GROUP BY city, district, ward
ORDER BY total DESC;

SELECT user_id, recipient_name, phone, address_line, city, district, ward, COUNT(*) AS total
FROM addresses
GROUP BY user_id, recipient_name, phone, address_line, city, district, ward
HAVING COUNT(*) > 1
ORDER BY total DESC;

SELECT a.address_id, a.user_id, a.address_line, a.city, a.district, a.ward
FROM addresses a
LEFT JOIN orders o ON o.shipping_address_id = a.address_id
WHERE o.order_id IS NULL
  AND a.city = 'Vietnam'
  AND a.district = 'District'
  AND a.ward = 'Ward';
```

Nếu muốn xóa các địa chỉ checkout-rác không còn order tham chiếu:

```sql
DELETE a
FROM addresses a
LEFT JOIN orders o ON o.shipping_address_id = a.address_id
WHERE o.order_id IS NULL
  AND a.city = 'Vietnam'
  AND a.district = 'District'
  AND a.ward = 'Ward'
  AND a.is_default = 0;
```

Không nên xóa address đang được `orders.shipping_address_id` tham chiếu trước khi có snapshot.

## Bảng nên giữ

Các bảng cốt lõi nên giữ:

- `users`
- `roles`
- `user_roles`
- `addresses`
- `categories`
- `products`
- `product_variants`
- `carts`
- `cart_items`
- `orders`
- `order_items`
- `payments`
- `product_reviews`
- `return_requests`
- `wishlists`
- `chat_messages`
- `notifications`

Ghi chú:

- `roles/user_roles` giữ nhưng chỉ còn `CUSTOMER`, `ADMIN`
- `payments` giữ nếu app còn hiển thị/tracking trạng thái thanh toán
- `return_requests` giữ vì app có hoàn hàng
- `chat_messages` giữ vì app có chat với shop/admin

## Bảng nên bỏ hoặc refactor

| Bảng / field | Đề xuất | Lý do |
| --- | --- | --- |
| `roles`: `SELLER`, `SHIPPER`, `VENDOR` | Xóa data | App chỉ cần `CUSTOMER`, `ADMIN` |
| `shops` | Refactor rồi drop | Project là một shop, không cần entity shop riêng |
| `shop_reviews` | Drop | Đánh giá shop không cần, giữ `product_reviews` |
| `sub_orders` | Refactor rồi drop | Chỉ cần `orders -> order_items` |
| `shippers` | Drop | Không có role/flow shipper |
| `shipments` | Drop | Admin đổi trạng thái đơn là đủ |
| `coupons`, `user_coupons` | Drop hoặc giữ global | Hiện chưa có flow coupon; nếu giữ thì bỏ `shop_id` |
| `products.shop_id` | Refactor rồi xóa | Không còn multi-shop |
| `cart_items.shop_id` | Refactor rồi xóa | Không cần group cart/order theo shop |
| `coupons.shop_id` | Xóa nếu giữ coupon | Coupon global cho toàn shop |

## Thứ tự thực hiện khuyến nghị

1. Backup DB.
2. Chạy các query kiểm tra role/shop/address rác.
3. Sửa checkout dùng `addressId`, không tạo address mới mỗi đơn.
4. Thêm snapshot địa chỉ vào `orders`.
5. Dọn role thừa chỉ còn `CUSTOMER`, `ADMIN`.
6. Dọn address rác không còn order tham chiếu.
7. Refactor order flow bỏ `sub_orders`.
8. Refactor bỏ `shop_id` khỏi products/cart/coupon.
9. Drop các bảng multi-vendor/giao hàng không dùng.

## Việc nên làm đầu tiên

Ưu tiên nhất nên làm trước:

1. Sửa `CreateOrderRequest` nhận `addressId`.
2. Sửa `OrderServiceImpl` không tạo địa chỉ mới khi checkout.
3. Dọn role thừa trong `roles`.

Ba việc này giảm rác DB ngay, ít ảnh hưởng UI, và không cần đập schema lớn ngay lập tức.
