# Furniture Android App — CLAUDE.md

## Tổng quan dự án

Ứng dụng thương mại điện tử bán nội thất cho Android. Gồm 2 module:
- **android-app/** — Android client (Java, MVVM)
- **backend-api/** — Spring Boot REST API

---

## Môi trường & Cách chạy

### Backend (Spring Boot)
```bash
cd backend-api
mvn spring-boot:run
# hoặc chạy từ IntelliJ / VS Code
# API chạy tại http://localhost:8080/api
```

Yêu cầu biến môi trường (file `.env` hoặc system env):
```
DB_USERNAME=...
DB_PASSWORD=...
JWT_SECRET=...
CLOUDINARY_CLOUD_NAME=...
CLOUDINARY_API_KEY=...
CLOUDINARY_API_SECRET=...
MAIL_USERNAME=...
MAIL_PASSWORD=...
GOOGLE_CLIENT_ID=...
GOOGLE_CLIENT_SECRET=...
```

### Android App

**Emulator:** chạy thẳng, không cần cấu hình thêm (mặc định `10.0.2.2:8080`).

**Thiết bị thật:** thêm vào `android-app/local.properties`:
```
base.url=http://<IP_LAN_MÁY_TÍNH>:8080/api/
```

Mở firewall port 8080:
```powershell
netsh advfirewall firewall add rule name="Spring Boot 8080" dir=in action=allow protocol=TCP localport=8080
```

Build & chạy từ Android Studio hoặc:
```powershell
cd android-app
.\gradlew installDebug
```

---

## Kiến trúc Android

### Pattern: MVVM + Repository

```
UI (Activity/Fragment)
    └── ViewModel (LiveData)
        └── Repository (Retrofit callbacks → LiveData)
            └── API Interface (Retrofit)
                └── RetrofitClient (OkHttp singleton)
```

### Cấu trúc thư mục

```
app/src/main/java/com/furniture/app/
├── data/
│   ├── model/              # 25 POJO models + request DTOs
│   ├── remote/
│   │   ├── api/            # 13 Retrofit API interfaces
│   │   ├── interceptor/    # AuthInterceptor (Bearer token + 401 handler)
│   │   └── RetrofitClient.java   # OkHttp singleton, BASE_URL từ BuildConfig
│   └── repository/         # AuthRepository, CartRepository, OrderRepository, ProductRepository
├── ui/
│   ├── adapter/            # 11 RecyclerView adapters
│   ├── admin/              # 10 Admin activities
│   ├── auth/               # LoginActivity, RegisterActivity
│   ├── customer/           # CustomerMainActivity + fragments + sub-activities
│   ├── main/               # MainActivity (splash/routing)
│   └── viewmodel/          # AuthViewModel, CartViewModel, OrderViewModel, ProductViewModel
├── util/
│   ├── SessionManager.java # JWT token storage (EncryptedSharedPreferences)
│   ├── InputValidator.java # Form validation helpers
│   ├── LoadingDialog.java  # Reusable loading dialog
│   └── ProvinceService.java
└── FurnitureApplication.java   # Global init: auto-logout on 401
```

---

## Luồng xác thực

1. App khởi động → `MainActivity` kiểm tra `SessionManager.isLoggedIn()`
2. Nếu đã đăng nhập → route đến `AdminMainActivity` (role=ADMIN) hoặc `CustomerMainActivity`
3. Mỗi API request → `AuthInterceptor` tự động thêm `Authorization: Bearer <token>`
4. Nếu server trả 401 → `FurnitureApplication.setupAuthHandler()` tự logout và chuyển về `LoginActivity`
5. Token lưu mã hóa bằng `EncryptedSharedPreferences` (AES256-GCM)

---

## ViewModels — quan trọng

Tất cả ViewModels dùng pattern **self-removing observeForever**:

```java
LiveData<T> source = repository.getData();
source.observeForever(new Observer<T>() {
    @Override public void onChanged(T data) {
        // xử lý data
        source.removeObserver(this);  // tự xóa sau khi nhận
    }
});
```

**Không dùng** `MediatorLiveData + observeForever(r -> {})` — đây là anti-pattern gây memory leak.

---

## Màn hình & Tính năng

### Customer
| Màn hình | Mô tả |
|---|---|
| HomeFragment | Banner, danh mục, sản phẩm nổi bật |
| SearchFragment | Tìm kiếm full-text |
| CartFragment | Giỏ hàng, cập nhật số lượng, xóa |
| ProfileFragment | Thông tin cá nhân, đơn hàng, địa chỉ |
| ProductDetailActivity | Ảnh, variants, đánh giá, thêm giỏ/wishlist |
| CheckoutActivity | Chọn địa chỉ, phương thức thanh toán |
| OrderHistoryActivity | Lịch sử đơn hàng theo trạng thái |
| ChatActivity | Chat 1-1 với shop |
| WishlistActivity | Danh sách yêu thích |
| ReturnRequestActivity | Yêu cầu trả hàng |

### Admin
| Màn hình | Mô tả |
|---|---|
| AdminMainActivity | Dashboard menu |
| AdminProductListActivity | CRUD sản phẩm + variants |
| AdminOrderListActivity | Quản lý đơn hàng, cập nhật trạng thái |
| AdminUserListActivity | Quản lý user, ban/unban |
| AdminCategoryListActivity | CRUD danh mục |
| AdminReviewListActivity | Xóa review vi phạm |
| AdminReturnListActivity | Duyệt/từ chối trả hàng |
| AdminStatsActivity | Thống kê doanh thu, top sản phẩm |
| AdminChatListActivity | Danh sách phòng chat |

---

## API Endpoints chính

Base URL: `http://<host>:8080/api/`

| Module | Endpoint |
|---|---|
| Auth | `POST /auth/login`, `/auth/register`, `/auth/google` |
| Products | `GET /products`, `/products/{id}`, `/products/search`, `/products/featured` |
| Cart | `GET /cart`, `POST /cart/add`, `PUT /cart/items/{id}`, `DELETE /cart/items/{id}` |
| Orders | `GET /orders`, `POST /orders`, `POST /orders/{id}/cancel` |
| Address | `GET /addresses`, `POST /addresses`, `PUT /addresses/{id}/default` |
| Reviews | `POST /reviews`, `GET /reviews/product/{productId}` |
| Wishlist | `GET /wishlist`, `POST /wishlist/{productId}`, `DELETE /wishlist/{productId}` |
| Chat | `POST /chat/send`, `GET /chat/messages/{chatId}` |
| Returns | `POST /returns`, `GET /returns/my` |
| Admin | `GET /admin/stats`, `/admin/orders`, `/admin/users`, `/admin/products` |

---

## Dependencies chính

```gradle
Retrofit 2.9.0          // HTTP client
OkHttp 4.12.0           // HTTP + logging interceptor
Glide 4.16.0            // Image loading
Room 2.6.1              // Local DB (prepared, chưa dùng)
Lottie 6.2.0            // Animations
MPAndroidChart 3.1.0    // Charts (admin stats)
security-crypto 1.1.0   // EncryptedSharedPreferences
Navigation 2.7.6        // Fragment navigation
```

---

## Vấn đề đã biết / Roadmap

- [ ] Room DB: implement offline cache cho products và orders
- [ ] Pagination: infinite scroll cho danh sách sản phẩm (API đã hỗ trợ `PageResponse`)
- [ ] WebSocket: thay polling bằng real-time cho chat
- [ ] Push notifications: chưa implement
- [ ] Dark mode: chưa hỗ trợ
- [ ] Unit tests: chưa có

---

## Lưu ý khi phát triển

- `local.properties` không được commit (đã trong `.gitignore`) — chứa `sdk.dir` và `base.url`
- `BASE_URL` được inject lúc build qua `BuildConfig.BASE_URL` từ `build.gradle`
- Mọi validate form dùng `InputValidator` utility thay vì viết inline
- Token tự động reset khi call `RetrofitClient.resetInstance()` sau logout
