# Kế hoạch dọn dẹp & tối ưu Database — Furniture App

> Tài liệu này chỉ là **bản preview/đề xuất** dựa trên việc rà soát code (`backend-api`) + dump `furniture_db.sql`.
> **Chưa có thay đổi nào được thực hiện** — bạn xem qua rồi chọn phần muốn làm.

## 1. Bối cảnh phát hiện được

Khi rà soát, thấy app hiện tại:
- Chỉ vận hành **1 shop duy nhất** (bảng `shops` chỉ có 1 dòng, `shop_id = 1`, được seed cứng và dùng làm default ở khắp nơi)
- Chỉ thực sự dùng **2 role**: `CUSTOMER`, `ADMIN`

Nhưng schema DB vẫn còn dáng dấp kiến trúc **multi-vendor** (kiểu Shopee/Lazada — nhiều shop, có shipper riêng, sub-order theo từng shop...) được dựng sẵn từ giai đoạn đầu nhưng chưa bao giờ triển khai đầy đủ → đây chính là phần "thừa, chưa tối ưu" mà bạn cảm nhận được.

---

## 2. Role thừa trong DB (dữ liệu mồ côi)

- Bảng `roles` hiện có **5 dòng**: `ADMIN(id=3)`, `CUSTOMER(id=1)`, `SELLER(id=2)`, `SHIPPER(id=4)`, `VENDOR(id=60)`
- Code (`Role.java`, `DataInitializer.seedRoles()`) chỉ định nghĩa & seed **2 role**: `ADMIN`, `CUSTOMER`
- Bảng `user_roles` chỉ map tới `role_id` = 1 và 3 → `SELLER`, `SHIPPER`, `VENDOR` là **rác mồ côi**, không user nào được gán

**Đề xuất:** xóa 3 dòng role thừa (`SELLER`, `SHIPPER`, `VENDOR`) khỏi bảng `roles`.

---

## 3. Entity/bảng thuộc kiến trúc multi-vendor không còn phù hợp

### 3.1 Hoàn toàn không được dùng — an toàn để xóa

| Entity / Bảng | Bằng chứng |
|---|---|
| `Shipper` / `shippers` | 0 dòng dữ liệu. `ShipperRepository` chỉ được khai báo, **không** service/controller nào gọi |
| `Shipment` / `shipments` | 0 dòng dữ liệu. `ShipmentRepository` **không hề được reference** ở bất kỳ đâu ngoài chính file repository |
| `ShopReview` / `shop_reviews` | 0 dòng dữ liệu. `ShopReviewRepository` không được dùng ở bất kỳ đâu |

→ Đây đúng nghĩa là code/scaffolding để lại từ lúc thiết kế multi-vendor, **chưa từng được triển khai tính năng**.

**Đề xuất:** xóa hẳn 3 entity (`Shipper.java`, `Shipment.java`, `ShopReview.java`) + 3 repository tương ứng + DROP 3 bảng.

### 3.2 Đang được dùng, nhưng dư thừa vì chỉ có 1 shop

- **`shops`**: chỉ có đúng 1 dòng (`shop_id=1`, "Furniture Store Vietnam"), seed cứng trong `DataInitializer`, và `AdminController` còn fallback cứng `shopId = request.getShopId() != null ? ... : 1`
- **`sub_orders`**: mỗi `Order` luôn tạo **đúng 1** `SubOrder` (xác nhận từ dữ liệu thật: toàn bộ 45 dòng trong `sub_orders` đều có `shop_id = 1`) → bảng này hiện chỉ là **một lớp bọc 1-1 không cần thiết** quanh `orders`, không hề "group nhiều shop" như mục đích thiết kế ban đầu

Việc giữ `Shop` + `SubOrder` khiến luồng đặt hàng phải đi qua thêm một tầng gián tiếp (group theo shop → tạo SubOrder → OrderItem trỏ qua subOrderId → ReturnRequest lại phải tra ngược qua SubOrder...) chỉ để luôn cho ra kết quả "1 group duy nhất".

**Đề xuất (2 hướng, tùy mức độ sẵn sàng refactor lớn):**
- **(A) Tối thiểu** — giữ nguyên `Shop`/`SubOrder` để tránh đụng vào luồng Order/Cart/Product/ReturnRequest (rủi ro cao), chỉ dọn phần chết ở mục 3.1
- **(B) Triệt để** — gộp `SubOrder` vào `Order` (đưa `status`, `shipping_fee` lên thẳng `Order`, `OrderItem` trỏ thẳng `order_id`), xóa bảng `shops`, bỏ cột `shop_id` khỏi `products`/`cart_items`/`order_items`, xóa `Shop` entity, `ShopDetailActivity` bên Android... → **đây là refactor kiến trúc lớn**, cần kế hoạch & test riêng, không nên làm chung đợt với phần dọn rác an toàn ở trên.

---

## 4. Bảng `Addresses` — không thừa về cấu trúc, nhưng có BUG gây phình bảng

Cấu trúc bảng (`address_id, user_id, recipient_name, phone, address_line, city, district, ward, is_default,...`) hợp lý cho 1 app thương mại điện tử, không trùng lặp với entity nào khác (khác với `Shop.address` — đó là địa chỉ cửa hàng, một khái niệm khác).

**Tuy nhiên phát hiện bug ở `OrderServiceImpl.createOrFindAddress()` (dòng ~332-346):**

```java
private Address createOrFindAddress(Integer userId, CreateOrderRequest request) {
    // Create a new address for this order
    Address address = Address.builder()
            .userId(userId)
            .recipientName(request.getRecipientName())
            .phone(request.getRecipientPhone())
            .addressLine(request.getShippingAddress())
            .city("Vietnam")        // ← hard-code, đè lên tỉnh/thành thật
            .district("District")   // ← hard-code, đè lên quận/huyện thật
            .ward("Ward")           // ← hard-code, đè lên phường/xã thật
            .isDefault(false)
            .build();
    return addressRepository.save(address);   // ← LUÔN tạo mới, không tìm/tái sử dụng
}
```

- Tên hàm là `createOrFind` nhưng thực chất **luôn luôn tạo `Address` mới** mỗi lần đặt hàng — không tìm và tái sử dụng địa chỉ đã lưu
- `city`/`district`/`ward` bị **hard-code** thành chuỗi literal `"Vietnam"/"District"/"Ward"`, còn dữ liệu tỉnh/huyện/xã thật mà client gửi lên bị gộp hết vào `addressLine`

**Hệ quả nhìn thấy trong dữ liệu thật:** bảng `addresses` hiện có 53 dòng, nhưng phần lớn là **bản sao gần giống hệt nhau của cùng một user** — ví dụ `user_id = 6` chiếm khoảng 30/53 dòng, nhiều dòng có `city = "Vietnam"`, `district = "District"`, `ward = "Ward"` (dữ liệu rác do bug trên gây ra), mỗi lần khách bấm đặt hàng lại sinh thêm 1-2 dòng mới dù thông tin giống hệt lần trước.

**Đề xuất:**
1. Sửa luồng tạo đơn hàng: cho client gửi `addressId` (của địa chỉ đã lưu trong `AddressActivity`) thay vì gửi lại text mỗi lần → backend dùng `addressRepository.findById(addressId)`. Cần thêm field `addressId` vào `CreateOrderRequest` và sửa `CheckoutActivity` phía Android để gửi `addressId` đã chọn.
2. Nếu vẫn muốn giữ flow nhập tay khi checkout, ít nhất nên tìm địa chỉ trùng (theo `recipientName + phone + addressLine + city + district + ward`) trước khi tạo dòng mới, và **không hard-code** city/district/ward.
3. Sau khi sửa bug, dọn dữ liệu rác hiện có (xóa các dòng trùng lặp / có `city="Vietnam"`, `district="District"`, `ward="Ward"`).

---

## 5. Bảng tổng hợp đề xuất

| # | Thành phần | Loại | Trạng thái hiện tại | Đề xuất | Rủi ro |
|---|---|---|---|---|---|
| 1 | Role `SELLER`, `SHIPPER`, `VENDOR` (id 2, 4, 60) | Dữ liệu | Mồ côi, không user nào được gán | `DELETE` khỏi bảng `roles` | Thấp |
| 2 | `Shipper` entity + bảng `shippers` + `ShipperRepository` | Code + Schema | 0 dữ liệu, không nơi nào dùng | Xóa hoàn toàn | Thấp |
| 3 | `Shipment` entity + bảng `shipments` + `ShipmentRepository` | Code + Schema | 0 dữ liệu, repo không ai gọi | Xóa hoàn toàn | Thấp |
| 4 | `ShopReview` entity + bảng `shop_reviews` + `ShopReviewRepository` | Code + Schema | 0 dữ liệu, repo không ai gọi | Xóa hoàn toàn | Thấp |
| 5 | `Shop` + `SubOrder` (tầng multi-vendor) | Code + Schema | Đang dùng, nhưng luôn ra đúng 1 group | Bàn riêng — gộp vào `Order` (refactor lớn) | **Cao** |
| 6 | Bug `createOrFindAddress` luôn tạo `Address` mới + hard-code city/district/ward | Code | Đang gây phình bảng `addresses` mỗi lần đặt hàng | Sửa để tái sử dụng theo `addressId` | Trung bình (đụng API + Android) |
| 7 | Dữ liệu rác trong `addresses` (~30 dòng trùng của `user_id=6`, giá trị placeholder) | Dữ liệu | Đã tồn tại trong DB | Dọn sau khi sửa xong bug #6 | Cần backup trước khi xóa |

---

## 6. Thứ tự thực hiện đề xuất

1. **Xóa role rác** (#1) — 1 câu `DELETE`, an toàn tuyệt đối
2. **Xóa Shipper / Shipment / ShopReview** (#2, #3, #4) — xóa entity + repository trong code, sau đó `DROP TABLE` (lưu ý: `spring.jpa.hibernate.ddl-auto=update` hiện tại sẽ **không tự xóa** bảng khi xóa entity, cần `DROP TABLE` thủ công)
3. **Sửa bug địa chỉ** (#6), deploy, rồi mới **dọn dữ liệu rác** (#7) — tránh vừa sửa vừa có dữ liệu mới sinh ra theo kiểu cũ
4. **Bàn riêng** về việc gộp `Shop`/`SubOrder` (#5) vì đây là thay đổi kiến trúc lớn, ảnh hưởng `Order`, `Cart`, `Product`, `ReturnRequest` ở cả backend lẫn Android (bao gồm `ShopDetailActivity`)

---

Bạn muốn bắt đầu từ phần nào? Mình đề xuất làm theo thứ tự #1 → #2 → #6/#7 trước (an toàn, gọn DB ngay), còn #5 (gộp Shop/SubOrder) nên để riêng một đợt vì là refactor lớn.
