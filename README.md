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
- Tìm kiếm sản phẩm (auto-search, hỗ trợ tiếng Việt có/không dấu), lọc theo danh mục, sắp xếp theo giá
- Xem chi tiết sản phẩm: ảnh slide, biến thể màu/size/chất liệu, đánh giá từ người dùng
- Thêm / xóa sản phẩm yêu thích (Wishlist)
- Giỏ hàng: chọn từng sản phẩm, cập nhật số lượng, xóa, thanh toán
- Đặt hàng: chọn địa chỉ đã lưu / thêm mới, thanh toán COD hoặc chuyển khoản
- Theo dõi đơn hàng, hủy đơn (khi còn đang xử lý), xem chi tiết
- Đánh giá sản phẩm sau khi đơn hàng được giao (1–5 sao + bình luận + ảnh)
- Yêu cầu hoàn trả đơn hàng đã giao (kèm lý do và ảnh minh chứng)
- Quản lý địa chỉ giao hàng (thêm / sửa / xóa)
- Chỉnh sửa thông tin cá nhân, đổi ảnh đại diện
- Chat với hỗ trợ shop

### Admin
- Dashboard tổng quan: doanh thu ngày/tháng/năm, xuất CSV
- Quản lý sản phẩm: thêm, sửa, xóa, upload ảnh, quản lý biến thể
- Quản lý danh mục: thêm, sửa, xóa, upload ảnh
- Quản lý đơn hàng: lọc theo trạng thái, tìm kiếm, cập nhật trạng thái
- Quản lý yêu cầu hoàn trả: xem ảnh minh chứng, duyệt / từ chối
- Quản lý người dùng: tìm kiếm, khóa/mở khóa tài khoản
- Quản lý đánh giá: xem, xóa đánh giá không phù hợp
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
| Cloudinary | — | Lưu trữ ảnh đại diện, sản phẩm, đánh giá, chat |
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
| CircleImageView | 3.1.0 | Ảnh đại diện tròn |
| Lottie | 6.2.0 | Animation |

---

## Cấu trúc project

```
Furniture-Android/
├── backend-api/                        ← Spring Boot REST API
│   └── src/main/java/com/furniture/api/
│       ├── controller/                 ← REST Controllers
│       ├── model/                      ← JPA Entities
│       ├── repository/                 ← Spring Data JPA Repositories
│       ├── service/                    ← Business logic
│       ├── security/                   ← JWT filter, UserDetailsService
│       └── config/                     ← SecurityConfig, CloudinaryConfig
│
└── android-app/app/src/main/
    ├── java/com/furniture/app/
    │   ├── data/
    │   │   ├── model/                  ← POJOs (Product, Order, ...)
    │   │   ├── remote/api/             ← Retrofit interfaces
    │   │   └── repository/             ← Data layer
    │   ├── ui/
    │   │   ├── adapter/                ← RecyclerView Adapters
    │   │   ├── auth/                   ← Login, Register
    │   │   ├── customer/               ← Home, Search, Cart, Order, Profile, Chat
    │   │   ├── admin/                  ← Admin screens
    │   │   └── viewmodel/              ← ViewModels + Factories
    │   └── util/
    │       ├── SessionManager.java     ← Lưu token vào SharedPreferences
    │       └── LoadingDialog.java      ← Dialog chờ có mô tả
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

**Bước 3 — Cấu hình Cloudinary** (upload ảnh)

Tạo file `backend-api/.env` hoặc set environment variables:

```
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret
```

**Bước 4 — Chạy backend**

```bash
cd backend-api
mvn spring-boot:run
```

Kết quả thành công:
```
Furniture API is running!
API Base URL: http://localhost:8080/api
```

**Dữ liệu mẫu (tự động khởi tạo)**

| Loại | Nội dung |
|------|----------|
| Vai trò | CUSTOMER, ADMIN |
| Danh mục | Phòng khách, Phòng ngủ, Phòng ăn, Phòng làm việc, Ngoài trời, Trang trí |
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
| GET | `/products/search?keyword=` | Tìm kiếm (hỗ trợ không dấu) |
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

### Returns 🔒
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | `/returns` | Tạo yêu cầu hoàn trả (multipart: `orderId`, `reason`, `file`) |
| GET | `/returns/my` | Xem các yêu cầu hoàn trả của mình |

### Reviews 🔒
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | `/reviews` | Gửi đánh giá |
| POST | `/reviews/upload-image` | Upload ảnh đánh giá |
| GET | `/reviews/product/{id}` | Đánh giá của sản phẩm |
| GET | `/reviews/check/{productId}` | Kiểm tra đã đánh giá chưa |

### Wishlist 🔒
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/wishlist` | Danh sách yêu thích |
| POST | `/wishlist/{productId}` | Thêm vào yêu thích |
| DELETE | `/wishlist/{productId}` | Xóa khỏi yêu thích |
| GET | `/wishlist/check/{productId}` | Kiểm tra đã yêu thích chưa |

### Addresses 🔒
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/addresses` | Danh sách địa chỉ |
| POST | `/addresses` | Thêm địa chỉ |
| PUT | `/addresses/{id}` | Sửa địa chỉ |
| DELETE | `/addresses/{id}` | Xóa địa chỉ |

### User 🔒
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/users/me` | Thông tin cá nhân |
| PUT | `/users/me` | Cập nhật thông tin |
| POST | `/users/me/avatar` | Upload ảnh đại diện |

### Chat 🔒
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | `/chat/send` | Gửi tin nhắn text |
| POST | `/chat/send-image` | Gửi hình ảnh |
| GET | `/chat/messages/{chatId}` | Lấy nội dung phòng chat |
| GET | `/chat/rooms` | Danh sách phòng chat |

### Media 🔒
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | `/media/upload-image?folder=` | Upload ảnh dùng chung (Admin) |

### Admin 🔒 (ROLE_ADMIN)
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/admin/orders` | Tất cả đơn hàng (lọc + tìm kiếm) |
| PUT | `/admin/orders/{id}/status` | Cập nhật trạng thái đơn |
| GET | `/admin/products` | Danh sách sản phẩm |
| POST | `/admin/products` | Thêm sản phẩm |
| PUT | `/admin/products/{id}` | Sửa sản phẩm |
| DELETE | `/admin/products/{id}` | Xóa sản phẩm |
| POST | `/admin/products/{id}/variants` | Thêm biến thể |
| PUT | `/admin/variants/{id}` | Sửa biến thể |
| DELETE | `/admin/variants/{id}` | Xóa biến thể |
| GET | `/admin/categories` | Danh sách danh mục |
| POST | `/admin/categories` | Thêm danh mục |
| PUT | `/admin/categories/{id}` | Sửa danh mục |
| DELETE | `/admin/categories/{id}` | Xóa danh mục |
| GET | `/admin/users` | Danh sách người dùng |
| PUT | `/admin/users/{id}/status` | Khóa/mở khóa tài khoản |
| GET | `/admin/reviews` | Danh sách đánh giá |
| DELETE | `/admin/reviews/{id}` | Xóa đánh giá |
| GET | `/admin/returns` | Danh sách yêu cầu hoàn trả |
| PUT | `/admin/returns/{id}/status` | Duyệt / từ chối hoàn trả |
| GET | `/admin/stats?period=day\|month\|year` | Thống kê doanh thu |

---

## Sơ đồ quan hệ database

```
Users ──────── has many ──→ Orders ──→ SubOrders ──→ OrderItems ──→ ProductVariants
Users ──────── has one  ──→ Cart ───→ CartItems ──→ ProductVariants
Users ──────── has many ──→ Addresses
Users ──────── has many ──→ Wishlists ──→ Products
Users ──────── has many ──→ ReturnRequests ──→ Orders
Products ────── belongs to ─→ Categories
Products ────── has many ──→ ProductVariants
Products ────── has many ──→ ProductReviews ←── Users
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
| `Unable to resolve host 10.0.2.2` | Emulator không kết nối được localhost | Kiểm tra backend đang chạy đúng port 8080 |
| `Access denied for user 'root'` | Sai password MySQL | Sửa `application.properties` |
| `Table doesn't exist` | JPA chưa tạo bảng | Chạy lại backend, kiểm tra `ddl-auto=update` |
| App hiện 401 Unauthorized | Token hết hạn | Đăng xuất và đăng nhập lại |
| Upload ảnh thất bại | Cloudinary chưa cấu hình | Kiểm tra biến môi trường `CLOUDINARY_*` |
| Không xóa được danh mục | Đang có sản phẩm thuộc danh mục | Chuyển hoặc xóa sản phẩm trước |

---

## Trạng thái hoàn thiện

| Chức năng | Backend | Android |
|-----------|:-------:|:-------:|
| Đăng ký / Đăng nhập | ✅ | ✅ |
| Trang chủ | ✅ | ✅ |
| Tìm kiếm + lọc + sắp xếp (hỗ trợ không dấu) | ✅ | ✅ |
| Chi tiết sản phẩm + biến thể | ✅ | ✅ |
| Giỏ hàng | ✅ | ✅ |
| Đặt hàng + quản lý địa chỉ | ✅ | ✅ |
| Lịch sử & chi tiết đơn hàng | ✅ | ✅ |
| Hủy đơn hàng | ✅ | ✅ |
| Đánh giá sản phẩm (kèm ảnh) | ✅ | ✅ |
| Yêu cầu hoàn trả + duyệt hoàn | ✅ | ✅ |
| Danh sách yêu thích | ✅ | ✅ |
| Chỉnh sửa profile + ảnh đại diện | ✅ | ✅ |
| Chat hỗ trợ (text + ảnh) | ✅ | ✅ |
| Admin: quản lý sản phẩm + biến thể | ✅ | ✅ |
| Admin: quản lý danh mục | ✅ | ✅ |
| Admin: quản lý đơn hàng | ✅ | ✅ |
| Admin: quản lý hoàn trả | ✅ | ✅ |
| Admin: quản lý người dùng | ✅ | ✅ |
| Admin: quản lý đánh giá | ✅ | ✅ |
| Admin: thống kê doanh thu + xuất CSV | ✅ | ✅ |
