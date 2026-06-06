# Kế hoạch kiểm tra & fix — Furniture Android

> Tài liệu này liệt kê toàn bộ vấn đề phát hiện được sau khi audit source code,
> kèm đề xuất branch tương ứng để test và fix từng nhóm.

---

## Cấu trúc branch đề xuất

```
main
├── fix/cart-exceptions          ← Backend: sai status code khi lỗi giỏ hàng
├── fix/order-address            ← Backend: địa chỉ đơn hàng bị hardcode
├── fix/chat-memory-leak         ← Android: memory leak polling trong ChatActivity
├── fix/cart-refresh-after-order ← Android: giỏ hàng không tự cập nhật sau checkout
├── fix/error-ui-state           ← Android: UI không reset sau khi API lỗi
├── fix/stock-validation         ← Backend: kiểm tra tồn kho chưa đầy đủ
├── feature/email-service        ← Backend: xác minh email, quên mật khẩu
├── feature/pagination           ← Android: phân trang infinite-scroll
└── feature/payment-gateway      ← Backend + Android: tích hợp VNPAY/MOMO
```

---

## NHÓM 1 — BUG CRITICAL (ưu tiên fix trước)

### Branch: `fix/cart-exceptions`

**File:** `backend-api/src/main/java/com/furniture/api/service/impl/CartServiceImpl.java`

**Vấn đề:** Dùng `RuntimeException` thay vì exception tùy chỉnh → API trả về HTTP 500 thay vì 404/400.

| Dòng | Lỗi hiện tại | Cần đổi thành |
|------|-------------|---------------|
| 47 | `throw new RuntimeException("Product not found")` | `throw new ResourceNotFoundException("Product", id)` |
| 75 | `throw new RuntimeException("Product has no price information")` | `throw new BadRequestException(...)` |
| 106, 109 | `RuntimeException` | `ResourceNotFoundException` |
| 126, 129 | `RuntimeException` | `ResourceNotFoundException` |

**Cách test:**
```
POST /api/cart/add  body: { "productId": 99999, "quantity": 1 }
→ Kỳ vọng: 404 Not Found
→ Hiện tại: 500 Internal Server Error
```

---

### Branch: `fix/order-address`

**File:** `backend-api/src/main/java/com/furniture/api/service/impl/OrderServiceImpl.java`

**Vấn đề:** Địa chỉ giao hàng trong đơn hàng bị hardcode.

```java
// Hiện tại (sai):
.city("Vietnam")
.district("District")
.ward("Ward")

// Cần đổi thành: đọc từ địa chỉ người dùng chọn khi checkout
```

**Cách test:**
1. Tạo địa chỉ: TP.HCM, Quận 1, Phường Bến Nghé
2. Đặt đơn hàng với địa chỉ đó
3. Xem chi tiết đơn hàng → mong đợi city = "TP.HCM" (hiện tại = "Vietnam")

---

### Branch: `fix/chat-memory-leak`

**File:** `android-app/app/src/main/java/com/furniture/app/ui/customer/chat/ChatActivity.java`

**Vấn đề:** `pollRunnable` không bị dừng khi Activity bị destroy → tiếp tục gọi API ngầm, leak context.

```java
// Cần thêm trong onDestroy():
@Override
protected void onDestroy() {
    super.onDestroy();
    if (pollHandler != null && pollRunnable != null) {
        pollHandler.removeCallbacks(pollRunnable);
    }
}
```

**Cách test:**
1. Mở Chat → chat vài tin
2. Nhấn Back (exit ChatActivity)
3. Quan sát Logcat → không được còn log polling sau khi thoát

---

### Branch: `fix/cart-refresh-after-order`

**File:** `android-app/app/src/main/java/com/furniture/app/ui/customer/order/CheckoutActivity.java`
và `android-app/app/src/main/java/com/furniture/app/ui/customer/CustomerMainActivity.java`

**Vấn đề:** Sau khi đặt hàng thành công, CartFragment vẫn hiển thị sản phẩm cũ — phải swipe refresh thủ công.

**Fix:** Sau khi `CheckoutActivity` trả về `RESULT_OK`, trigger reload cart trong `CartFragment`.

**Cách test:**
1. Thêm sản phẩm vào giỏ
2. Đặt hàng thành công
3. Quay lại tab Giỏ hàng → phải thấy trống ngay (không cần refresh)

---

### Branch: `fix/error-ui-state`

**Files:** Hầu hết Activity và Fragment trong Android

**Vấn đề:** Khi API lỗi (timeout, 4xx, 5xx), spinner loading tiếp tục quay hoặc nút bị disable, không revert về trạng thái ban đầu.

**Các màn hình cần fix:**
- `ProductDetailActivity` — nút "Thêm vào giỏ" bị disable sau lỗi
- `CheckoutActivity` — nút "Đặt hàng" bị disable sau lỗi
- `EditProfileActivity` — nút "Lưu" không revert
- `AddAddressActivity` — spinner không ẩn khi lỗi

**Cách test:** Tắt BE → thực hiện từng thao tác → kiểm tra UI có về trạng thái ban đầu không.

---

### Branch: `fix/stock-validation`

**File:** `backend-api/src/main/java/com/furniture/api/service/impl/OrderServiceImpl.java`

**Vấn đề:** `product.getStock()` có thể `null` ở line 72-75, gây NPE khi sản phẩm không có variant.

```java
// Hiện tại (nguy hiểm):
if (product.getStock() < quantity)  // NPE nếu stock = null

// Cần:
if (product.getStock() == null || product.getStock() < quantity)
```

**Cách test:**
1. Tạo sản phẩm không có `stock` trong DB
2. Thêm vào giỏ và đặt hàng
3. Kỳ vọng: lỗi rõ ràng "Hết hàng" (không phải NPE 500)

---

## NHÓM 2 — TÍNH NĂNG CHƯA HOÀN CHỈNH

### Branch: `feature/email-service`

**Files:**
- `backend-api/src/main/java/com/furniture/api/service/impl/AuthServiceImpl.java`
- Cần tạo mới: `EmailService.java`, `EmailServiceImpl.java`

**Vấn đề:**
1. `isVerified` luôn = `false` khi đăng ký, không có email xác minh
2. `forgot-password` / `reset-password` endpoint tồn tại nhưng không gửi email

**Cần implement:**
- Gửi email xác minh sau đăng ký
- Gửi email link reset mật khẩu với token hết hạn sau 15 phút
- Validate token trước khi cho phép đổi mật khẩu

**Cách test:**
```
POST /api/auth/forgot-password  { "email": "test@test.com" }
→ Kỳ vọng: email được gửi với link reset
→ Hiện tại: không làm gì
```

---

### Branch: `feature/pagination`

**Files:**
- `android-app/app/src/main/java/com/furniture/app/ui/customer/home/HomeFragment.java`
- Các Adapter: `ProductAdapter.java`, `OrderAdapter.java`

**Vấn đề:** Danh sách sản phẩm chỉ load trang đầu (~20 items), không có infinite-scroll.

**Cần implement:**
- `RecyclerView.OnScrollListener` để detect khi scroll đến cuối
- Thêm `currentPage` tracking trong Repository
- Append (không replace) kết quả trang mới vào adapter

---

### Branch: `feature/payment-gateway`

**Vấn đề:** VNPAY và MOMO được chọn trong UI nhưng không có tích hợp thật.

**Scope lớn — cần:**

Backend:
- Tích hợp VNPAY SDK hoặc REST API
- Endpoint tạo payment URL
- Webhook callback từ cổng thanh toán
- Cập nhật trạng thái đơn hàng sau thanh toán

Android:
- Mở WebView hoặc deep link đến cổng thanh toán
- Xử lý `PaymentCallbackActivity` (đã có deep link `furnitureapp://payment`)
- Hiển thị kết quả thanh toán

---

## NHÓM 3 — VẤN ĐỀ TRUNG BÌNH (fix sau)

| # | Mô tả | File | Branch đề xuất |
|---|-------|------|----------------|
| 1 | Shop ID hardcode = 1 trong ChatController | `ChatController.java:27` | `fix/chat-shop-id` |
| 2 | Google login luôn signOut trước khi sign in | `LoginActivity.java:115` | `fix/google-login-ux` |
| 3 | Không validate địa chỉ trước khi checkout | `CheckoutActivity.java` | `fix/checkout-validation` |
| 4 | Review không kiểm tra order thuộc về user | `ReturnRequestController.java:80` | `fix/review-ownership` |
| 5 | Ảnh không có placeholder khi load thất bại | Các Adapter | `fix/image-placeholders` |

---

## NHÓM 4 — KIỂM TRA THEO LUỒNG (manual test)

Tạo nhánh `test/<tên-luồng>` để test từng chức năng:

```
test/auth-flow          ← Đăng ký → đăng nhập → đổi mật khẩu → đăng xuất
test/product-flow       ← Tìm kiếm → xem chi tiết → chọn variant → thêm giỏ
test/order-flow         ← Thêm giỏ → checkout → theo dõi đơn → hủy đơn
test/review-flow        ← Mua hàng → viết review → admin duyệt
test/return-flow        ← Đặt hàng → yêu cầu hoàn trả → admin xử lý
test/chat-flow          ← Mở chat → gửi tin → gửi ảnh → admin trả lời
test/admin-flow         ← Đăng nhập admin → quản lý đơn → duyệt return → thống kê
test/address-flow       ← Thêm địa chỉ → đặt mặc định → xóa → checkout với địa chỉ mới
```

---

## Thứ tự ưu tiên fix

```
1. fix/cart-exceptions          (30 phút)
2. fix/chat-memory-leak         (30 phút)
3. fix/order-address            (1 giờ)
4. fix/cart-refresh-after-order (1 giờ)
5. fix/error-ui-state           (2-3 giờ, nhiều file)
6. fix/stock-validation         (30 phút)
7. feature/email-service        (4-6 giờ)
8. feature/pagination           (3-4 giờ)
9. feature/payment-gateway      (1-2 ngày)
```

---

## Checklist tạo branch

```bash
# Fix branches
git checkout -b fix/cart-exceptions
git checkout -b fix/order-address
git checkout -b fix/chat-memory-leak
git checkout -b fix/cart-refresh-after-order
git checkout -b fix/error-ui-state
git checkout -b fix/stock-validation

# Feature branches
git checkout -b feature/email-service
git checkout -b feature/pagination
git checkout -b feature/payment-gateway

# Test branches
git checkout -b test/auth-flow
git checkout -b test/order-flow
git checkout -b test/admin-flow
```
