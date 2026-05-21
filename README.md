# Furniture Shop — Ứng dụng mua sắm nội thất Android

Đồ án môn học **Lập Trình Android** — Ứng dụng cho phép người dùng tìm kiếm, xem chi tiết và đặt mua sản phẩm nội thất, bao gồm hai phần chính: **Backend API** (Spring Boot) và **Android App** (Java).

---

## Thành viên nhóm

| STT | Họ tên | MSSV | Phân công |
|-----|--------|------|-----------|
| 1   |        |      | Backend: Auth, User, Product, Category, Wishlist API |
| 2   |        |      | Backend: Cart, Order, Review, Address, Admin API |
| 3   |        |      | Android: Home, Search, Product Detail, Wishlist |
| 4   |        |      | Android: Cart, Checkout, Profile, Order, Chat, Admin |

---

## Tổng quan chức năng

### Khách hàng
- Đăng ký / Đăng nhập bằng email + mật khẩu (JWT)
- Xem trang chủ: sản phẩm nổi bật, danh mục, sản phẩm mới
- Tìm kiếm sản phẩm (auto-search), lọc theo danh mục, sắp xếp theo giá
- Xem chi tiết sản phẩm: ảnh slide, biến thể màu/size, đánh giá từ người dùng
- Thêm / xóa sản phẩm yêu thích (Wishlist)
- Giỏ hàng: thêm, cập nhật số lượng, xóa, thanh toán
- Đặt hàng: chọn địa chỉ đã lưu / thêm mới, thanh toán COD hoặc chuyển khoản
- Theo dõi đơn hàng, hủy đơn (khi còn đang xử lý), xem chi tiết
- Đánh giá sản phẩm sau khi đơn hàng được giao (1–5 sao + bình luận + ảnh), mỗi đơn hàng được đánh giá 1 lần
- Yêu cầu hoàn trả đơn hàng đã giao (kèm lý do và ảnh/video minh chứng)
- Quản lý địa chỉ giao hàng (thêm / sửa / xóa)
- Chỉnh sửa thông tin cá nhân
- Chat với hỗ trợ shop

### Admin
- Xem dashboard tổng quan
- Quản lý sản phẩm: thêm, sửa, xóa, ảnh, biến thể
- Quản lý danh mục
- Quản lý đơn hàng: lọc theo trạng thái, tìm kiếm, cập nhật trạng thái
- Quản lý người dùng: tìm kiếm, khóa/mở khóa tài khoản
- Xem và phản hồi chat

---

## Công nghệ sử dụng

### Backend
| Công nghệ | Phiên bản | Mục đích |
|-----------|-----------|----------|
| Java | 17 | Ngôn ngữ lập trình |
| Spring Boot | 3.2.1 | Framework backend |
| Spring Security | 6.x | Xác thực và phân quyền |
| Spring Data JPA | — | ORM, tương tác database |
| MySQL | 8.0+ | Cơ sở dữ liệu |
| JWT (jjwt) | 0.12.3 | Xác thực token |
| Maven | 3.6+ | Quản lý dependencies |

### Android
| Công nghệ | Phiên bản | Mục đích |
|-----------|-----------|----------|
| Java | 17 | Ngôn ngữ lập trình |
| Android SDK | API 24–34 | Nền tảng |
| Retrofit | 2.9.0 | Gọi REST API |
| OkHttp | 4.12.0 | HTTP client + logging |
| Glide | 4.16.0 | Load ảnh |
| Material Design | 3 (1.11.0) | Giao diện |
| MVVM + LiveData | — | Kiến trúc |
| ViewPager2 | — | Swipe giữa tab |
| CircleImageView | 3.1.0 | Ảnh đại diện tròn |
| Lottie | 6.2.0 | Animation |

---

## Cấu trúc project

```
Furniture-Android/
├── README.md
├── backend-api/                        ← Spring Boot REST API
│   └── src/main/java/com/furniture/api/
│       ├── controller/                 ← REST Controllers
│       │   ├── AuthController.java
│       │   ├── ProductController.java
│       │   ├── CategoryController.java
│       │   ├── CartController.java
│       │   ├── OrderController.java
│       │   ├── AddressController.java
│       │   ├── ReviewController.java
│       │   ├── WishlistController.java
│       │   ├── ChatController.java
│       │   ├── UserController.java
│       │   └── AdminController.java
│       ├── model/                      ← JPA Entities
│       ├── repository/                 ← Spring Data JPA Repositories
│       ├── service/                    ← Business logic
│       ├── security/                   ← JWT filter, UserDetailsService
│       └── config/                     ← SecurityConfig
│
└── android-app/app/src/main/
    ├── java/com/furniture/app/
    │   ├── data/
    │   │   ├── model/                  ← POJOs (Product, Order, WishlistItem, ...)
    │   │   ├── remote/api/             ← Retrofit interfaces
    │   │   └── repository/             ← Data layer
    │   ├── ui/
    │   │   ├── adapter/                ← RecyclerView Adapters
    │   │   ├── auth/                   ← Login, Register
    │   │   ├── customer/
    │   │   │   ├── home/               ← HomeFragment
    │   │   │   ├── search/             ← SearchFragment
    │   │   │   ├── cart/               ← CartFragment
    │   │   │   ├── product/            ← ProductDetailActivity, CategoryProductsActivity
    │   │   │   ├── order/              ← Checkout, OrderHistory, OrderDetail, WriteReview
    │   │   │   ├── profile/            ← ProfileFragment, EditProfile, Address, Wishlist
    │   │   │   ├── shop/               ← ShopDetailActivity
    │   │   │   └── chat/               ← ChatActivity
    │   │   ├── admin/                  ← Admin screens
    │   │   └── viewmodel/              ← ViewModels + Factories
    │   └── util/
    │       └── SessionManager.java     ← Lưu token vào SharedPreferences
    └── res/
        ├── layout/                     ← XML layouts
        ├── drawable/                   ← Icons, backgrounds
        └── values/                     ← colors, strings, themes
```

---

## Hướng dẫn cài đặt và chạy

### Yêu cầu môi trường

| Công cụ | Phiên bản tối thiểu |
|---------|---------------------|
| JDK | 17 |
| Maven | 3.6+ |
| MySQL | 8.0+ |
| Android Studio | Hedgehog (2023.1.1) trở lên |
| Android Emulator / Thiết bị thật | API 24+ |

---

### Phần 1: Cài đặt Backend

**Bước 1 — Tạo database MySQL**

```sql
CREATE DATABASE furniture_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
```

> Spring Boot tự tạo bảng khi chạy lần đầu (`ddl-auto=update`). Không cần tạo thủ công.

**Bước 2 — Cấu hình kết nối**

Mở `backend-api/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/furniture_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root      # ← đổi thành username MySQL của bạn
spring.datasource.password=root      # ← đổi thành password MySQL của bạn
```

**Bước 3 — Chạy backend**

```bash
cd backend-api
mvn spring-boot:run
```

Kết quả thành công:
```
Furniture API is running!
API Base URL: http://localhost:8080/api
```

**Bước 4 — Dữ liệu mẫu (tự động)**

`DataInitializer` tự tạo khi khởi động lần đầu:

| Loại | Nội dung |
|------|----------|
| Vai trò | CUSTOMER, ADMIN |
| Danh mục | Phòng khách, Phòng ngủ, Phòng ăn, Phòng làm việc, Ngoài trời, Trang trí |
| Cửa hàng | "Nội Thất Gia Đình" |
| Sản phẩm | 10 sản phẩm nội thất với ảnh và biến thể |

**Tài khoản demo** (mật khẩu: `123456`):

| Email | Vai trò |
|-------|---------|
| `customer@furniture.com` | CUSTOMER |
| `admin@furniture.com` | ADMIN |

---

### Phần 2: Cài đặt Android App

**Bước 1** — Mở `android-app` trong Android Studio, chờ Gradle sync.

**Bước 2** — Kiểm tra URL backend trong `android-app/app/build.gradle`:

```groovy
debug {
    buildConfigField "String", "BASE_URL", '"http://10.0.2.2:8080/api/"'
}
```

> `10.0.2.2` = `localhost` của máy tính khi dùng emulator Android.  
> Nếu dùng **thiết bị thật**: đổi thành IP LAN của máy tính, ví dụ `http://192.168.1.x:8080/api/`.

**Bước 3** — Tạo AVD API 24+ và nhấn Run (Shift+F10).

---

## API Endpoints

> Base URL: `http://localhost:8080/api` — Các endpoint 🔒 yêu cầu `Authorization: Bearer <token>`

### Auth
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | `/auth/register` | Đăng ký |
| POST | `/auth/login` | Đăng nhập, nhận JWT |

### Products
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/products` | Danh sách sản phẩm (phân trang, sắp xếp) |
| GET | `/products/{id}` | Chi tiết sản phẩm |
| GET | `/products/search?keyword=` | Tìm kiếm |
| GET | `/products/category/{id}` | Sản phẩm theo danh mục |
| GET | `/products/featured` | Sản phẩm nổi bật |
| GET | `/products/new-arrivals` | Sản phẩm mới |
| GET | `/categories` | Danh sách danh mục |

### Cart 🔒
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/cart` | Xem giỏ hàng |
| POST | `/cart/add` | Thêm sản phẩm |
| PUT | `/cart/items/{id}` | Cập nhật số lượng |
| DELETE | `/cart/items/{id}` | Xóa khỏi giỏ |
| DELETE | `/cart/clear` | Xóa toàn bộ |

### Orders 🔒
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | `/orders` | Tạo đơn hàng |
| GET | `/orders` | Lịch sử đơn hàng |
| GET | `/orders/{id}` | Chi tiết đơn hàng |
| PUT | `/orders/{id}/cancel` | Hủy đơn hàng |

### Addresses 🔒
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/addresses` | Danh sách địa chỉ |
| POST | `/addresses` | Thêm địa chỉ |
| PUT | `/addresses/{id}` | Sửa địa chỉ |
| DELETE | `/addresses/{id}` | Xóa địa chỉ |

### Reviews 🔒
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | `/reviews` | Gửi đánh giá |
| POST | `/reviews/upload-image` | Upload anh danh gia len Cloudinary |
| GET | `/reviews/product/{id}` | Đánh giá của sản phẩm |
| GET | `/reviews/check/{productId}` | Kiểm tra đã đánh giá chưa 🔒 |

### Wishlist 🔒
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/wishlist` | Danh sách yêu thích |
| POST | `/wishlist/{productId}` | Thêm vào yêu thích |
| DELETE | `/wishlist/{productId}` | Xóa khỏi yêu thích |
| GET | `/wishlist/check/{productId}` | Kiểm tra đã yêu thích chưa |

### Chat 🔒
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | `/chat/send` | Gửi tin nhắn text |
| POST | `/chat/send-image` | Gửi hình ảnh chat bằng multipart file |
| GET | `/chat/messages/{chatId}` | Lấy nội dung phòng chat |
| GET | `/chat/rooms` | Danh sách phòng chat |

### Returns / Refund Requests
| Method | Endpoint | Mo ta |
|--------|----------|-------|
| POST | `/returns` | Customer tao yeu cau hoan tra bang multipart: `orderId`, `orderItemId`, `reason`, `file` |
| GET | `/returns/my` | Customer xem cac yeu cau hoan tra cua minh |
| GET | `/admin/returns` | Admin xem danh sach yeu cau hoan tra, co the loc `status` |
| PUT | `/admin/returns/{id}/status` | Admin cap nhat `APPROVED` / `REJECTED`, tuy chon `adminNote` |

### User 🔒
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/users/me` | Thông tin cá nhân |
| PUT | `/users/me` | Cập nhật thông tin |
| POST | `/users/me/avatar` | Upload ảnh đại diện lên Cloudinary |

### Media Upload
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | `/media/upload-image` | Admin upload ảnh dùng chung, query `folder`, multipart `file` |

### Admin 🔒 (ROLE_ADMIN)
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/admin/orders` | Tất cả đơn hàng (lọc + tìm kiếm) |
| PUT | `/admin/orders/{id}/status` | Cập nhật trạng thái đơn |
| GET | `/admin/products` | Quản lý sản phẩm |
| POST | `/admin/products` | Thêm sản phẩm |
| PUT | `/admin/products/{id}` | Sửa sản phẩm |
| DELETE | `/admin/products/{id}` | Xóa sản phẩm |
| GET | `/admin/categories` | Quản lý danh mục |
| GET | `/admin/users` | Quản lý người dùng |
| PUT | `/admin/users/{id}/status` | Khóa/mở khóa tài khoản |
| GET | `/admin/reviews` | Quản lý đánh giá của user |
| DELETE | `/admin/reviews/{id}` | Xóa đánh giá không phù hợp |
| GET | `/admin/stats?period=day|month|year` | Thống kê doanh thu theo ngày/tháng/năm, sản phẩm, danh mục |

---

## Sơ đồ quan hệ database (các bảng chính)

```
Users ────────── has many ──→ Orders ──→ OrderItems ──→ Products
Users ────────── has one  ──→ Cart ────→ CartItems ───→ Products
Users ────────── has many ──→ Addresses
Users ────────── has many ──→ Wishlists ─→ Products
Products ──────── belongs to ─→ Categories
Products ──────── has many ──→ ProductVariants
Products ──────── has many ──→ ProductReviews ←── Users
Orders ──────────── has one ──→ Address (snapshot)
```

---

## Luồng xác thực JWT

```
Android App ──POST /auth/login──→ Backend
                  ←── accessToken (JWT) ───
App lưu token vào SharedPreferences (SessionManager)
Mọi request sau gửi kèm: Authorization: Bearer <token>
Backend xác minh qua JwtAuthenticationFilter
Khi token hết hạn → app tự chuyển về màn hình đăng nhập
```

---

## Các lỗi hay gặp

| Lỗi | Nguyên nhân | Cách sửa |
|-----|-------------|----------|
| `Connection refused` | Backend chưa chạy | Khởi động `mvn spring-boot:run` |
| `Unable to resolve host 10.0.2.2` | Emulator không kết nối được localhost | Kiểm tra backend đang chạy, đúng port 8080 |
| `Access denied for user 'root'` | Sai password MySQL | Sửa `application.properties` |
| `Table doesn't exist` | JPA chưa tạo bảng | Chạy lại backend, kiểm tra `ddl-auto=update` |
| App hiện 401 Unauthorized | Token hết hạn | Đăng xuất và đăng nhập lại |
| `No static resource reviews` | Backend cũ đang chạy | Kill process port 8080, restart backend |
| Không xóa được địa chỉ | FK constraint (địa chỉ đang dùng trong đơn) | Backend trả về thông báo lỗi rõ ràng |

---

## Trạng thái hoàn thiện

| Chức năng | Backend | Android |
|-----------|:-------:|:-------:|
| Đăng ký / Đăng nhập | ✅ | ✅ |
| Trang chủ (Home) | ✅ | ✅ |
| Tìm kiếm + lọc + sắp xếp | ✅ | ✅ |
| Chi tiết sản phẩm + đánh giá | ✅ | ✅ |
| Giỏ hàng | ✅ | ✅ |
| Đặt hàng + chọn địa chỉ | ✅ | ✅ |
| Lịch sử đơn hàng | ✅ | ✅ |
| Chi tiết đơn hàng + hủy đơn | ✅ | ✅ |
| Đánh giá sản phẩm (sau giao) | ✅ | ✅ |
| Danh sách yêu thích (Wishlist) | ✅ | ✅ |
| Quản lý địa chỉ | ✅ | ✅ |
| Chỉnh sửa profile | ✅ | ✅ |
| Chat hỗ trợ | ✅ | ✅ |
| Admin: quản lý sản phẩm | ✅ | ✅ |
| Admin: quản lý danh mục | ✅ | ✅ |
| Admin: quản lý đơn hàng + filter | ✅ | ✅ |
| Admin: quản lý người dùng | ✅ | ✅ |
| Admin: chat với khách | ✅ | ✅ |
| Admin: quản lý đánh giá user | ✅ | ✅ |
| Admin: biểu đồ doanh thu ngày/tháng/năm | ✅ | ✅ |

---

## Tiến độ phát triển

### Phase 1 — Guest mode & Auth guards ✅

| Hạng mục | Trạng thái |
|----------|:----------:|
| App khởi động vào Home (không ép đăng nhập) | ✅ |
| Splash → CustomerMainActivity (khách) hoặc AdminMainActivity (admin) | ✅ |
| ProfileFragment: hiển thị nút Đăng nhập / Đăng ký khi chưa login | ✅ |
| ProfileFragment: hiển thị thông tin user khi đã login | ✅ |
| Cart tab: hiển thị giao diện khách khi chưa login | ✅ |
| Auth guard: Đơn hàng, Wishlist, Địa chỉ, Chat, Edit Profile → yêu cầu đăng nhập | ✅ |
| Auth guard: Thêm vào giỏ hàng, Mua ngay, Yêu thích → yêu cầu đăng nhập | ✅ |
| Nút Back trên các Activity con | ✅ |
| Xử lý lỗi mạng ở màn Home: thông báo + nút Thử lại | ✅ |
| Build `assembleDebug` thành công | ✅ |

### Phase 3 — Product Detail, Variant, Wishlist ✅

| Hạng mục | Trạng thái |
|----------|:----------:|
| Chọn variant → ảnh slider đổi theo variant tương ứng | ✅ |
| Chọn variant → giá, tồn kho cập nhật đúng | ✅ |
| Chọn variant → số lượng reset về 1 | ✅ |
| Hiển thị "X sản phẩm còn lại" / "Hết hàng" rõ ràng | ✅ |
| Không cho thêm giỏ / mua ngay khi hết hàng | ✅ |
| Wishlist: thêm nút "Mua ngay" trên mỗi sản phẩm | ✅ |
| Build `assembleDebug` thành công | ✅ |

### Phase 2 — Home, Search, Banner, Header ✅

| Hạng mục | Trạng thái |
|----------|:----------:|
| Banner tự động chạy ở Home (4 ảnh Unsplash, 3.5s/slide) | ✅ |
| Header Home: xóa icon giỏ hàng, thêm icon Chat + Thông báo | ✅ |
| Auth guard: Chat/Thông báo trên header → yêu cầu đăng nhập | ✅ |
| Bỏ nút "Xem tất cả" ở khu vực danh mục | ✅ |
| Search: danh mục filter động từ API (không hardcode) | ✅ |
| Search: gợi ý tên sản phẩm khi gõ (≥2 ký tự) | ✅ |
| Search: đổi "Liên quan" → "Mặc định" | ✅ |
| Build `assembleDebug` thành công | ✅ |

### Phase 4 — Cart selection, Checkout flow, Order detail ✅

| Hạng mục | Trạng thái |
|----------|:----------:|
| Cart: checkbox chọn từng sản phẩm (`cb_select` trên `item_cart.xml`) | ✅ |
| Cart: hàng "Chọn tất cả" (`layout_select_all`, `cb_select_all`) | ✅ |
| Cart: tổng tiền chỉ tính các sản phẩm đã tick | ✅ |
| Cart: hiển thị số lượng đã chọn ("Đã chọn X/Y") | ✅ |
| Cart: nút Đặt hàng bị tắt khi không có sản phẩm nào được tick | ✅ |
| Cart: chỉ truyền sản phẩm đã tick sang CheckoutActivity (EXTRA_CART_ITEMS) | ✅ |
| Checkout: sắp xếp nhận EXTRA_CART_ITEMS, hiển thị đúng sản phẩm được chọn | ✅ |
| Checkout: sau đặt hàng thành công → xóa các sản phẩm đã mua khỏi giỏ | ✅ |
| Checkout: sau đặt hàng thành công → chuyển thẳng sang OrderDetailActivity | ✅ |
| Order detail: ẩn hàng "Phí vận chuyển" | ✅ |
| Order detail: nút Đánh giá chỉ hiện khi đơn DELIVERED | ✅ |
| Order detail: không cho đánh giá lại sản phẩm đã đánh giá (checkReviewedStatus) | ✅ |
| Build `assembleDebug` thành công | ✅ |
### Phase 5 — Address, Profile, Auth Customer ✅

| Hạng mục | Trạng thái |
|----------|:----------:|
| Address: validate tên người nhận, số điện thoại Việt Nam, địa chỉ cụ thể | ✅ |
| Address: giữ ProvinceService dùng `provinces.open-api.vn` miễn phí, có xử lý lỗi mạng | ✅ |
| Profile: validate họ, tên, số điện thoại trước khi lưu | ✅ |
| Profile: thêm nút Đổi mật khẩu trong màn chỉnh sửa hồ sơ | ✅ |
| Đổi mật khẩu: gọi API `POST /users/me/change-password` | ✅ |
| Đổi mật khẩu xong: clear session và chuyển về LoginActivity | ✅ |
| Avatar: thêm thông báo TODO, chờ cấu hình upload cloud ở phase upload media | ✅ |
| Register: validate họ/tên, username, email, phone, password rõ hơn | ✅ |
| Login/Register: thêm nút Google/Facebook UI, không fake OAuth khi backend chưa cấu hình | ✅ |
| Forgot password: hiển thị TODO cấu hình email thay vì fake thành công | ✅ |
| Build `assembleDebug` thành công | ✅ |

### Phase 6 — Chat gửi hình ảnh ✅

| Hạng mục | Trạng thái |
|----------|:----------:|
| Backend: mở rộng `ChatMessage` với `messageType`, `mediaUrl`, `mediaPublicId` | ✅ |
| Backend: thêm API `POST /chat/send-image` nhận multipart image | ✅ |
| Backend: upload ảnh qua Cloudinary, đọc cấu hình từ env `CLOUDINARY_*` | ✅ |
| Backend: nếu Cloudinary chưa cấu hình thì trả lỗi rõ ràng, không hardcode secret | ✅ |
| Android: thêm nút gửi hình ảnh trong màn chat | ✅ |
| Android: chọn ảnh từ thư viện bằng `GetContent` | ✅ |
| Android: chụp ảnh bằng camera qua `FileProvider` | ✅ |
| Android: gửi ảnh lên API bằng multipart Retrofit | ✅ |
| Android: bubble chat hiển thị ảnh bằng Glide | ✅ |
| Cấu hình: thêm `res/xml/file_paths.xml` cho ảnh camera tạm | ✅ |
| `mvn test` backend thành công | ✅ |
| Build `assembleDebug` thành công | ✅ |

### Phase 7 - Hoan tra san pham
**Trang thai:** Da hoan thanh

**Da lam:**
- Customer tao yeu cau hoan tra cho don hang da giao thanh cong.
- Form hoan tra co ly do va file minh chung anh/video.
- Admin co man hinh quan ly yeu cau hoan tra, xac nhan hoac tu choi.

**Backend thay doi:**
- Them entity `ReturnRequest` va repository `ReturnRequestRepository`.
- Mo rong `CloudinaryService` de upload minh chung hoan tra vao folder `furniture/returns`.
- Them `ReturnRequestController` cho customer/admin.

**API thay doi:**
- `POST /returns`
- `GET /returns/my`
- `GET /admin/returns`
- `PUT /admin/returns/{id}/status`

**Android thay doi:**
- Them `ReturnRequestActivity` cho customer.
- Them nut `Yeu cau hoan tra` trong `OrderDetailActivity` khi don hang `DELIVERED`.
- Them `AdminReturnListActivity` va card `Hoan tra` trong admin dashboard.

**Kiem thu:**
- `mvn test`: PASS
- `.\gradlew.bat assembleDebug`: PASS

**TODO con lai:**
- Co the bo sung ly do tu choi chi tiet va preview media minh chung trong admin.

### Phase 8 - Admin dashboard, product, category, user, review
**Trang thai:** Da hoan thanh

**Da lam:**
- Admin thong ke co nut xuat file CSV tu du lieu doanh thu hien tai.
- Quan ly san pham co thanh tim kiem va bo loc trang thai.
- Quan ly danh muc co thanh tim kiem.
- Them/sua danh muc cho phep nhap URL hinh anh danh muc.
- Quan ly nguoi dung da co badge trang thai nam sat ten tai khoan.
- Admin dashboard co them trang ho so admin, dung man hinh chinh sua thong tin/doi mat khau hien co.
- Quan ly danh gia hien thi email nguoi danh gia, trang thai verified va anh danh gia neu review co `images`.

**Backend thay doi:**
- Mo rong DTO `GET /admin/reviews` de tra them `userEmail`, `images`, `isVerified`.

**API thay doi:**
- `GET /admin/reviews`: bo sung field `userEmail`, `images`, `isVerified`.

**Android thay doi:**
- `AdminStatsActivity`: xuat CSV bang `ACTION_CREATE_DOCUMENT`.
- `activity_admin_stats.xml`: them nut `Xuat file CSV`.
- `AdminProductListActivity`: them filter client-side theo ten/danh muc/trang thai.
- `activity_admin_product_list.xml`: them input tim kiem va spinner loc trang thai.
- `AdminCategoryListActivity`: them tim kiem, form them/sua co truong URL hinh anh.
- `activity_admin_category_list.xml`: them input tim kiem danh muc.
- `AdminMainActivity`: them card `Ho so admin`.
- `AdminReviewListActivity`: hien thi email, anh danh gia va verified badge.

**Kiem thu:**
- `mvn test`: PASS
- `.\gradlew.bat assembleDebug`: PASS

**TODO con lai:**
- Admin review co the bo sung thong tin don hang lien quan neu sau nay backend luu `orderId`/`orderItemId` trong review.

### Phase 9 - Upload Cloud va database hinh anh hop ly
**Trang thai:** Da hoan thanh

**Da lam:**
- Chuan hoa upload anh qua `CloudinaryService.uploadImage(file, folder)`.
- Hoan thien upload avatar user, luu URL vao `Users.profilePicture`.
- Them API upload anh dung chung cho admin: `/media/upload-image`.
- Admin category co the chon anh tu may, upload len Cloudinary va tu dong dien URL vao form them/sua.
- Admin product co the chon anh tu may, upload len Cloudinary va luu vao variant mac dinh cua san pham.
- Admin product edit co the them/sua/xoa nhieu variant, moi variant co size/color/material/price/stock/imageUrl rieng.
- Man hinh profile customer/admin co the doi anh dai dien bang anh tu may.
- Review customer upload anh len Cloudinary truoc khi gui danh gia, DB luu URL cloud thay vi `content://` local URI.

**Backend thay doi:**
- `CloudinaryService`: them ham upload image dung chung theo folder.
- `UserServiceImpl.uploadAvatar`: upload avatar len Cloudinary va cap nhat user.
- Them `MediaController` cho upload image admin.
- `AdminController`: create/update product nhan `imageUrl` va ghi vao `ProductVariant.imageUrl`.
- `AdminController`: them API create/update/delete variant cho san pham.
- `ReviewController`: them API upload anh review vao folder `furniture/reviews`.
- `ProductReview.images`: dung kieu `TEXT` de luu nhieu URL anh.

**API thay doi:**
- `POST /users/me/avatar`
- `POST /media/upload-image?folder=categories`
- `POST /media/upload-image?folder=products`
- `POST /admin/products`: bo sung `imageUrl`
- `PUT /admin/products/{id}`: bo sung `imageUrl`
- `POST /admin/products/{productId}/variants`
- `PUT /admin/variants/{variantId}`
- `DELETE /admin/variants/{variantId}`
- `POST /reviews/upload-image`

**Android thay doi:**
- `UserApi`: them multipart `uploadAvatar`.
- Them `MediaApi`.
- `EditProfileActivity`: chon anh, upload avatar, hien thi avatar bang Glide.
- `AdminCategoryListActivity`: chon anh tu may, upload Cloudinary, gan URL vao category image.
- `AdminProductEditActivity`: chon anh tu may, upload Cloudinary, gan URL vao anh variant mac dinh.
- `AdminProductEditActivity`: quan ly danh sach variant trong man sua san pham, co upload anh rieng cho variant.
- `AdminProductApi`: request create/update product co them `imageUrl`.
- `ReviewApi`: them multipart `uploadReviewImage`.
- `WriteReviewActivity`: upload tung anh da chon len Cloudinary, gom URL roi moi tao review.

**Kiem thu:**
- `mvn test`: PASS
- `.\gradlew.bat assembleDebug`: PASS

**TODO con lai:**
- Can cau hinh env Cloudinary that: `CLOUDINARY_CLOUD_NAME`, `CLOUDINARY_API_KEY`, `CLOUDINARY_API_SECRET`.
- Neu muon ho tro video/reuse media nang cao, co the tach them bang `Media` dung chung cho product/review/return/chat.

### Phase 10 - Tong ra soat va polish
**Trang thai:** Da hoan thanh

**Da lam:**
- Ra soat README bo sung endpoint moi cho chat image, return request, avatar, media upload, admin reviews/stats va review image upload.
- Ra soat role: project chi con `CUSTOMER`, `ADMIN`; khong con role du `VENDOR`, `SHIPPER`, `SELLER`, `MODERATOR`, `STAFF` trong source/README.
- Ra soat upload cloud: anh avatar/category/product/variant/chat/review/return deu di qua Cloudinary hoac endpoint upload phu hop.
- Ra soat secret: Cloudinary doc tu env, khong commit secret that vao source.
- Ghi ro TODO con lai: OAuth Google/Facebook, forgot/reset password bang email that, cau hinh Cloudinary env khi deploy.

**Backend thay doi:**
- Khong them API moi trong Phase 10, chi ra soat va xac nhan cac API phase truoc.

**API thay doi:**
- Khong thay doi them.

**Android thay doi:**
- Khong thay doi them ngoai polish review upload da hoan thien o Phase 9.

**Kiem thu:**
- `mvn test`: PASS
- `.\gradlew.bat assembleDebug`: PASS

**TODO con lai:**
- Cau hinh Cloudinary env that tren may/deploy server.
- Neu can social login that: hoan thien Google/Facebook OAuth backend, khong fake login.
- Neu can quen mat khau that: cau hinh email service va reset token.
