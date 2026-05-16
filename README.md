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
- Đánh giá sản phẩm sau khi đơn hàng được giao (1–5 sao + bình luận + ảnh)
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
| Vai trò | CUSTOMER, VENDOR, ADMIN, SHIPPER |
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
| GET | `/reviews/product/{id}` | Đánh giá của sản phẩm |
| GET | `/reviews/check/{productId}` | Kiểm tra đã đánh giá chưa 🔒 |

### Wishlist 🔒
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/wishlist` | Danh sách yêu thích |
| POST | `/wishlist/{productId}` | Thêm vào yêu thích |
| DELETE | `/wishlist/{productId}` | Xóa khỏi yêu thích |
| GET | `/wishlist/check/{productId}` | Kiểm tra đã yêu thích chưa |

### User 🔒
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/users/me` | Thông tin cá nhân |
| PUT | `/users/me` | Cập nhật thông tin |

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
