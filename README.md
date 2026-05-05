# Furniture Shop - Ứng dụng mua sắm nội thất Android

Đây là đồ án môn học Lập Trình Android  Ứng dụng cho phép người dùng tìm kiếm, xem chi tiết và đặt mua sản phẩm nội thất, gồm 2 phần chính: **Backend API** (Spring Boot) và **Android App** (Java).

---

## Thành viên nhóm

| STT | Họ tên | MSSV | Phân công |
|-----|--------|------|-----------|
| 1   |        |      | Backend: Auth, User, Product API |
| 2   |        |      | Backend: Cart, Order API |
| 3   |        |      | Android: UI Home, Search, Product Detail |
| 4   |        |      | Android: UI Cart, Checkout, Profile, Order |

---

## Tổng quan chức năng

- Đăng ký / Đăng nhập bằng email + password (JWT)
- Xem danh sách sản phẩm, tìm kiếm theo tên
- Xem chi tiết sản phẩm (hình ảnh, giá, biến thể màu/size)
- Thêm vào giỏ hàng, cập nhật số lượng, xóa sản phẩm
- Đặt hàng với địa chỉ giao hàng và phương thức thanh toán (COD / chuyển khoản)
- Xem lịch sử đơn hàng, hủy đơn hàng
- Chỉnh sửa thông tin cá nhân

---

## Công nghệ sử dụng

### Backend
| Công nghệ | Phiên bản | Mục đích |
|-----------|-----------|----------|
| Java | 17 | Ngôn ngữ lập trình |
| Spring Boot | 3.2.1 | Framework backend |
| Spring Security | 6.x | Xác thực và phân quyền |
| Spring Data JPA | - | ORM, tương tác database |
| MySQL | 8.0+ | Cơ sở dữ liệu |
| JWT (jjwt) | 0.12.3 | Xác thực token |
| Maven | 3.6+ | Quản lý dependencies |

### Android
| Công nghệ | Phiên bản | Mục đích |
|-----------|-----------|----------|
| Java | 17 | Ngôn ngữ lập trình |
| Android SDK | API 24–34 | Nền tảng |
| Retrofit | 2.9.0 | Gọi REST API |
| Glide | 4.16.0 | Load ảnh |
| Material Design | 3 (1.11.0) | Giao diện |
| MVVM | - | Kiến trúc |
| ViewBinding | - | Bind view |
| CircleImageView | 3.1.0 | Ảnh đại diện tròn |

---

## Cấu trúc project

```
Furniture-Android/
├── README.md                          ← file này
├── backend-api/                       ← Spring Boot REST API
│   ├── pom.xml
│   └── src/
│       └── main/
│           ├── java/com/furniture/api/
│           │   ├── FurnitureApiApplication.java
│           │   ├── config/            ← SecurityConfig
│           │   ├── controller/        ← REST Controllers
│           │   ├── dto/               ← Request/Response DTOs
│           │   │   ├── request/
│           │   │   └── response/
│           │   ├── exception/         ← Custom exceptions + GlobalExceptionHandler
│           │   ├── model/             ← JPA Entities (User, Product, Order, ...)
│           │   ├── repository/        ← Spring Data JPA Repositories
│           │   ├── security/          ← JWT filter, UserDetailsService
│           │   └── service/           ← Business logic
│           │       └── impl/
│           └── resources/
│               ├── application.properties
│               └── data.sql           ← Seed data (roles, ảnh mẫu)
│
└── android-app/                       ← Android App
    └── app/src/main/
        ├── java/com/furniture/app/
        │   ├── data/
        │   │   ├── model/             ← Data models (POJO)
        │   │   ├── remote/            ← Retrofit API interfaces + RetrofitClient
        │   │   └── repository/        ← Repositories (gọi API)
        │   ├── ui/
        │   │   ├── adapter/           ← RecyclerView Adapters
        │   │   ├── auth/              ← LoginActivity, RegisterActivity
        │   │   ├── customer/          ← Màn hình chính
        │   │   │   ├── cart/          ← CartFragment
        │   │   │   ├── home/          ← HomeFragment
        │   │   │   ├── order/         ← CheckoutActivity, OrderHistoryActivity
        │   │   │   ├── product/       ← ProductDetailActivity
        │   │   │   ├── profile/       ← ProfileFragment, EditProfileActivity
        │   │   │   └── search/        ← SearchFragment
        │   │   ├── main/              ← MainActivity (splash/entry)
        │   │   └── viewmodel/         ← ViewModels + Factories
        │   └── util/
        │       └── SessionManager.java ← Lưu token vào SharedPreferences
        └── res/
            ├── layout/               ← XML layouts
            ├── drawable/             ← Icons, backgrounds
            └── values/               ← colors, strings, themes
```

---

## Hướng dẫn cài đặt và chạy

### Yêu cầu môi trường

| Công cụ | Phiên bản tối thiểu |
|---------|---------------------|
| JDK | 17 |
| Maven | 3.6+ |
| MySQL | 8.0+ |
| Android Studio | Giraffe (2022.3.1) trở lên |
| Android Emulator | API 24+ |

---

### Phần 1: Cài đặt Backend

#### Bước 1 – Tạo database MySQL

Mở MySQL Workbench hoặc dùng command line, chạy:

```sql
CREATE DATABASE furniture_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
```

> Không cần tạo bảng thủ công. Spring Boot sẽ tự tạo toàn bộ bảng khi chạy lần đầu (nhờ `spring.jpa.hibernate.ddl-auto=update`).

#### Bước 2 – Cấu hình kết nối database

Mở file `backend-api/src/main/resources/application.properties`, sửa các dòng sau:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/furniture_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&createDatabaseIfNotExist=true
spring.datasource.username=root          # ← sửa thành username MySQL của bạn
spring.datasource.password=root          # ← sửa thành password MySQL của bạn
```

Các cấu hình khác (Cloudinary, Email, Google OAuth) là tùy chọn – **không cần** để chạy app cơ bản.

#### Bước 3 – Build và chạy backend

Mở terminal, vào thư mục `backend-api`:

```bash
cd backend-api

# Build
mvn clean package -DskipTests

# Chạy
java -jar target/furniture-api-1.0.0.jar
```

Hoặc chạy trực tiếp bằng Maven:

```bash
mvn spring-boot:run
```

**Kết quả thành công:**

```
===========================================
Furniture API is running!
API Base URL: http://localhost:8080/api
===========================================
```

#### Bước 4 – Thêm dữ liệu mẫu (tùy chọn)

File `data.sql` sẽ tự chạy khi khởi động và tạo sẵn:
- 4 vai trò: `CUSTOMER`, `VENDOR`, `ADMIN`, `SHIPPER`
- Ảnh placeholder cho categories và product variants

Để thêm sản phẩm mẫu, có thể dùng Postman gọi API `POST /api/auth/register` để tạo tài khoản, sau đó dùng MySQL Workbench nhập dữ liệu sản phẩm trực tiếp vào bảng `Products` và `Product_Variants`.

---

### Phần 2: Cài đặt Android App

#### Bước 1 – Mở project trong Android Studio

1. Mở **Android Studio**
2. Chọn **Open** → chọn thư mục `android-app`
3. Chờ Gradle sync hoàn tất

#### Bước 2 – Kiểm tra URL backend

Mở file `android-app/app/build.gradle`, kiểm tra:

```groovy
debug {
    buildConfigField "String", "BASE_URL", '"http://10.0.2.2:8080/api/"'
}
```

> `10.0.2.2` là địa chỉ đặc biệt của Android Emulator để trỏ đến `localhost` của máy tính.
> Nếu dùng **thiết bị thật**, đổi thành địa chỉ IP LAN của máy tính (ví dụ: `http://192.168.1.x:8080/api/`).

#### Bước 3 – Chạy app

1. Tạo hoặc khởi động **Android Virtual Device (AVD)** – API 24+
2. Nhấn **Run** (Shift+F10) hoặc nút ▶ trong Android Studio
3. App sẽ build và cài lên emulator

---

## API Endpoints chính

> Base URL: `http://localhost:8080/api`
>
> Các endpoint có 🔒 yêu cầu header: `Authorization: Bearer <token>`

### Auth
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | `/auth/register` | Đăng ký tài khoản mới |
| POST | `/auth/login` | Đăng nhập, nhận JWT token |

### Products
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/products` | Lấy danh sách sản phẩm (phân trang) |
| GET | `/products/{id}` | Chi tiết sản phẩm |
| GET | `/products/search?keyword=...` | Tìm kiếm sản phẩm |
| GET | `/products/featured` | Sản phẩm nổi bật |
| GET | `/categories` | Danh sách danh mục |

### Cart 🔒
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/cart` | Xem giỏ hàng |
| POST | `/cart/add` | Thêm sản phẩm vào giỏ |
| PUT | `/cart/items/{id}` | Cập nhật số lượng |
| DELETE | `/cart/items/{id}` | Xóa khỏi giỏ |
| DELETE | `/cart/clear` | Xóa toàn bộ giỏ |

### Orders 🔒
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | `/orders` | Tạo đơn hàng mới |
| GET | `/orders` | Lịch sử đơn hàng |
| GET | `/orders/{id}` | Chi tiết đơn hàng |
| PUT | `/orders/{id}/cancel` | Hủy đơn hàng |

### User 🔒
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/users/me` | Thông tin cá nhân |
| PUT | `/users/me` | Cập nhật thông tin |

---

## Ví dụ request/response

### Đăng ký

**Request:**
```json
POST /api/auth/register
{
  "firstName": "Nguyen",
  "lastName": "Van A",
  "username": "nguyenvana",
  "email": "nguyenvana@gmail.com",
  "phone": "0901234567",
  "password": "123456"
}
```

**Response:**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "...",
    "userId": 1,
    "username": "nguyenvana",
    "email": "nguyenvana@gmail.com"
  }
}
```

### Thêm vào giỏ hàng

**Request:**
```json
POST /api/cart/add
Authorization: Bearer <token>
{
  "productId": 1,
  "variantId": 2,
  "quantity": 1
}
```

---

## Sơ đồ database (các bảng chính)

```
Users ─────────── has many ──→ Orders
Users ─────────── has one  ──→ Cart ──→ CartItems ──→ Products
Products ──────── belongs to ─→ Categories
Products ──────── has many ──→ ProductVariants
Orders ─────────── has many ──→ SubOrders ──→ OrderItems
Orders ─────────── has one ──→ Address
```

**Các bảng trong database sau khi chạy:**
- `Users`, `Roles`, `User_Roles`
- `Categories`, `Products`, `Product_Variants`, `Product_Reviews`
- `Shops`, `Shop_Reviews`
- `Carts`, `Cart_Items`
- `Orders`, `Sub_Orders`, `Order_Items`
- `Addresses`, `Payments`
- `Wishlists`, `Notifications`, `Chat_Messages`
- `Coupons`, `User_Coupons`, `Shippers`, `Shipments`

---

## Lưu ý khi phát triển

### Phân chia công việc trong code

- **Backend**: code trong `backend-api/src/main/java/com/furniture/api/`
- **Android**: code trong `android-app/app/src/main/java/com/furniture/app/`
- **Khi thêm API mới**: tạo Controller → Service interface → ServiceImpl → Repository (nếu cần)
- **Khi thêm màn hình mới**: tạo layout XML → Activity/Fragment → ViewModel → Repository

### Luồng xác thực JWT

```
Android App ──POST /auth/login──→ Backend
                  ←─── JWT Token ───
App lưu token vào SharedPreferences (SessionManager)
Mọi request sau đó gửi kèm: Authorization: Bearer <token>
Backend đọc token qua JwtAuthenticationFilter
```

### Chạy song song (cần mở 2 cửa sổ)

```
Cửa sổ 1: cd backend-api && java -jar target/furniture-api-1.0.0.jar
Cửa sổ 2: Mở Android Studio → Run app trên emulator
```

---

## Các lỗi hay gặp

| Lỗi | Nguyên nhân | Cách sửa |
|-----|-------------|----------|
| `Connection refused` trên app | Backend chưa chạy | Chạy backend trước |
| `Unable to resolve host "10.0.2.2"` | Thiếu INTERNET permission | Đã có trong Manifest, kiểm tra emulator |
| `Access denied for user 'root'` | Sai password MySQL | Sửa lại trong `application.properties` |
| `Table doesn't exist` | JPA chưa tạo bảng | Chạy lại backend, kiểm tra `ddl-auto=update` |
| Build fail `LazyInitializationException` | Thiếu `@Transactional` | Đã fix, rebuild lại |
| App hiện "Giỏ hàng trống" sau khi thêm | Lỗi lazy loading (đã fix) | Pull code mới nhất |

---

## Trạng thái hoàn thiện

| Chức năng | Backend | Android |
|-----------|---------|---------|
| Đăng ký / Đăng nhập | ✅ Xong | ✅ Xong |
| Xem danh sách sản phẩm | ✅ Xong | ✅ Xong |
| Tìm kiếm sản phẩm | ✅ Xong | ✅ Xong |
| Xem chi tiết sản phẩm | ✅ Xong | ✅ Xong |
| Giỏ hàng | ✅ Xong | ✅ Xong |
| Đặt hàng | ✅ Xong | ✅ Xong |
| Lịch sử đơn hàng | ✅ Xong | ✅ Xong |
| Chỉnh sửa profile | ✅ Xong | ✅ Xong |
| Danh mục sản phẩm | ✅ Xong | ⚙️ Đang làm |
| Wishlist | ⚙️ Đang làm | ⚙️ Đang làm |
| Chat | ⚙️ Đang làm | ⚙️ Đang làm |
| Thông báo | ⚙️ Đang làm | ⚙️ Đang làm |
