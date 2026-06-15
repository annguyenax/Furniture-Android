# Plan: AI Shopping Assistant (Chatbot) cho Furniture App

## Context

App hiện đã có 1 kênh chat 1-1 với shop (`ChatActivity` ↔ `ChatController`, polling 3s, lưu ở bảng `chat_messages`). Đây **không** phải nơi để gắn AI — đó là chat người-người với SUPPORT_ID cố định.

Yêu cầu: xây một **chatbot AI riêng**, dùng **Google Gemini (Flash)**, đóng vai "trợ lý mua sắm" — trả lời câu hỏi sản phẩm dựa trên catalog thật trong DB, **và thực hiện được hành động** (thêm vào giỏ hàng, dẫn tới đặt hàng). Hiển thị dưới dạng **nút nổi (floating bubble)** xuất hiện trên toàn bộ màn hình customer, mở ra 1 bottom sheet chat.

Vì phạm vi rộng nhất (AI có quyền thay đổi dữ liệu của user), điểm mấu chốt của plan là **kiến trúc function-calling an toàn, có giới hạn rõ ràng**, tái dùng tối đa service/API hiện có thay vì viết logic nghiệp vụ mới.

---

## Kiến trúc tổng quan

```
Android (FAB + BottomSheet chat)
   │  POST /api/chatbot/message {message}
   │  GET  /api/chatbot/history
   ▼
ChatbotController (mới, /chatbot/**, authenticated)
   ▼
ChatbotService (orchestration loop)
   │  1. Load lịch sử (ai_chat_messages theo conversation_id, N message gần nhất)
   │  2. Gọi GeminiClient.generateContent(systemPrompt, history, tools)
   │  3. Nếu Gemini trả functionCall → validate an toàn → ChatbotToolExecutor.execute(name, args, userId)
   │     → trả functionResponse về Gemini, lặp lại (tối đa MAX_ITER=5)
   │  4. Nếu Gemini trả text → lưu DB, build ChatbotResponse (text + products + actions)
   ▼
GeminiClient (REST wrapper, gọi generativelanguage.googleapis.com)
ChatbotToolExecutor → tái dùng ProductService / CartService / OrderService / CategoryService / WishlistService...
```

---

## Phần 1 — Backend (Spring Boot)

### 1.1 Config
- Thêm `GEMINI_API_KEY` vào `application.properties` (pattern giống `CLOUDINARY_*`):
  ```
  gemini.api-key=${GEMINI_API_KEY}
  gemini.model=${GEMINI_MODEL:gemini-2.5-flash}
  ```
- Cập nhật `.env.example` / README docs với `GEMINI_API_KEY`.
- `SecurityConfig`: thêm `.requestMatchers("/chatbot/**").authenticated()`.

### 1.2 Entity + Repository mới: lịch sử chat AI
Bảng mới `ai_chat_messages` (Hibernate `ddl-auto=update` sẽ tự tạo, đồng thời thêm CREATE TABLE vào `furniture_db.sql` cho đồng bộ với convention hiện có):
- `message_id`
- `conversation_id` UUID/string, để reset hoặc mở nhiều cuộc hội thoại sau này mà không trộn toàn bộ lịch sử của user vào một luồng dài.
- `user_id` (FK users)
- `role` ENUM('USER','ASSISTANT')
- `content` TEXT
- `metadata_json` TEXT/JSON, chứa `productRefs`, `suggestedActions`, `toolCalls`, lỗi tool nếu có. **Không dùng CSV** vì action/product payload sẽ mở rộng, ví dụ `{type:"CHECKOUT", cartItemIds:[...]}`.
- `created_at`

File: `model/AiChatMessage.java`, `repository/AiChatMessageRepository.java` (giống pattern `ChatMessage`/`ChatMessageRepository`).

Repository nên có:
- `findTop20ByUserIdAndConversationIdOrderByCreatedAtDesc(...)`
- `deleteByUserIdAndConversationId(...)`
- `findLatestConversationIdByUserId(...)` hoặc tạo conversation mới khi user bấm reset.

### 1.3 GeminiClient — `service/ai/GeminiClient.java`
- Dùng `RestClient` (Spring 6, có sẵn, không cần thêm dependency).
- Method: `GeminiTurnResult generate(String systemPrompt, List<GeminiContent> history, List<ToolDeclaration> tools)`
- Map request/response JSON của Gemini `generateContent` API: `systemInstruction`, `contents[]` (role `user`/`model`/`function`), `tools[].functionDeclarations[]` (JSON schema params), parse `functionCall` / text parts từ response.

### 1.4 Tool definitions — `service/ai/ChatbotTools.java`
Khai báo function schema (tên, description, JSON schema params) cho Gemini. Mỗi tool map 1:1 với service method đã có sẵn — **không viết lại business logic**:

| Tool | Map tới | Rủi ro |
|---|---|---|
| `search_products(keyword?, categoryName?, minPrice?, maxPrice?, sortBy?)` | `ProductService.searchProducts` / `getProductsByCategory` / `getAllProducts` (lọc giá thêm ở tầng tool nếu cần — catalog chỉ ~23 sp) | đọc |
| `get_product_detail(productId)` | `ProductService.getProductById` | đọc |
| `list_categories()` | `CategoryService` | đọc |
| `get_cart()` | `CartService.getCart` | đọc |
| `add_to_cart(productId, variantId?, quantity)` | `CartService.addToCart` | viết, nhưng dễ hoàn tác |
| `remove_cart_item(cartItemId)` | `CartService.removeCartItem` | viết, dễ hoàn tác |
| `get_my_orders(status?, limit?)` | `OrderService.getUserOrders` / `getUserOrdersByStatus` | đọc |
| `get_order_detail(orderId)` | `OrderService.getOrderById` (đã check ownership theo userId) | đọc |
| `get_wishlist()` / `add_to_wishlist(productId)` | Wishlist endpoints hiện có | đọc / viết nhẹ |

### 1.4.1 Safety rules cho write tools
Tool ghi dữ liệu (`add_to_cart`, `remove_cart_item`, `add_to_wishlist`) chỉ được execute khi thỏa đủ điều kiện:
- User xác nhận rõ hành động, ví dụ "thêm vào giỏ", "mua cái này", "xóa khỏi giỏ". Nếu user chỉ hỏi/gợi ý chung thì chatbot chỉ trả lời và render action chip để user bấm.
- `productId` phải đến từ tool `search_products`/`get_product_detail`, không tin productId do model tự bịa.
- Nếu sản phẩm có nhiều variant mà user chưa chọn rõ, chatbot phải hỏi lại hoặc trả action chọn variant, không tự chọn bừa.
- `quantity` phải nằm trong giới hạn hợp lý, ví dụ `1..10`, và không vượt `stock`.
- `remove_cart_item` chỉ được xóa item thuộc cart của user hiện tại.

Nếu Gemini gọi write tool khi chưa đủ điều kiện, `ChatbotToolExecutor` trả `needs_confirmation` hoặc `needs_variant_selection` thay vì thực thi.

**Quyết định thiết kế quan trọng — KHÔNG expose `create_order` như một tool gọi trực tiếp.** Lý do: đặt hàng cần chọn địa chỉ giao, phương thức thanh toán, mã giảm giá, phí ship — đây là quyết định tài chính, nên luôn cần con người xác nhận trên `CheckoutActivity` thật (đã có UI đầy đủ). Thay vào đó:
- Chatbot dùng `add_to_cart` để thêm sản phẩm khách đồng ý mua vào giỏ.
- Sau khi thêm, trả về `suggestedActions: [{type: "CHECKOUT"}]` → Android render nút "Tiến hành thanh toán" → mở `CheckoutActivity` (giỏ hàng đã có sẵn item).

Đây vẫn thoả "AI thực hiện hành động" (giỏ hàng thay đổi thật) nhưng giữ bước review cuối cùng cho hành động tài chính — best practice cho agent có quyền viết dữ liệu.

### 1.5 ChatbotToolExecutor — `service/ai/ChatbotToolExecutor.java`
- `Object execute(String toolName, Map<String,Object> args, Integer userId)` — switch theo tên tool, gọi service tương ứng, trả Map/DTO sẽ được serialize làm `functionResponse`.
- Tool nào trả về product list/detail → tool executor cũng append vào "collected products" của lượt chat (để build `ChatbotResponse.products` cho UI render card).
- Tool `add_to_cart`/`remove_cart_item` thành công → append suggested action `VIEW_CART`/`CHECKOUT`.
- Tool response phải là DTO tối giản, **không trả nguyên entity JPA** cho Gemini:
  - Product: `productId`, `name`, `category`, `price`, `discount`, `finalPrice`, `stock`, `rating`, `shortDescription`, `variants[{variantId,color,size,price,stock,imageUrl}]`.
  - Cart: `cartItemId`, `productId`, `variantId`, `name`, `quantity`, `price`, `imageUrl`.
  - Order: chỉ trả thông tin cần thiết để user tra cứu; hạn chế PII như địa chỉ đầy đủ/số điện thoại nếu không thật sự cần.
- Search nên normalize tiếng Việt có dấu/không dấu ở tầng tool: hỗ trợ `sofa/sô pha`, `phong khach/phòng khách`, `tu ao/tủ áo`, v.v. Có thể thêm helper remove dấu + lowercase trước khi lọc name/category/description.

### 1.6 ChatbotService — `service/ai/ChatbotService.java`
- System prompt (tiếng Việt): vai trò trợ lý mua sắm nội thất, luôn trả lời ngắn gọn, chỉ dùng dữ liệu từ tool (không bịa sản phẩm/giá/tồn kho), không tự ý gọi write tool nếu user chưa xác nhận rõ, khi thiếu variant/quantity thì hỏi lại.
- Loop tool-calling, `MAX_TOOL_ITERATIONS = 5` để chặn vòng lặp/chi phí runaway.
- Giới hạn lịch sử gửi cho Gemini: 20 message gần nhất (đủ ngữ cảnh, giảm token cost).
- Lưu user message + assistant reply vào `ai_chat_messages` sau khi có câu trả lời cuối.
- Validate input trước khi gọi Gemini:
  - `message` không rỗng, trim khoảng trắng.
  - giới hạn độ dài, ví dụ 1000-2000 ký tự.
  - rate limit theo user/IP để chống spam tốn token, ví dụ 10 request/phút/user.
- Timeout/quota fallback: nếu Gemini lỗi, trả message thân thiện và không mất lịch sử user.

### 1.7 DTOs + Controller
- `dto/response/ChatbotMessageResponse`: `role, content, products[], suggestedActions[], createdAt`.
- `dto/request/ChatbotRequest`: `{ message: String }`.
- `controller/ChatbotController`:
  - `POST /chatbot/message` → `ChatbotService.chat(userId, message)`
  - `GET /chatbot/history` → lịch sử (phục vụ mở lại bottom sheet, load tin cũ)
  - `DELETE /chatbot/history` → reset conversation hiện tại hoặc tạo `conversation_id` mới

Lưu ý route: backend đang có `server.servlet.context-path=/api`, nên controller vẫn `@RequestMapping("/chatbot")`, SecurityConfig match `"/chatbot/**"`, còn Android/Postman gọi URL đầy đủ `/api/chatbot/...`.

---

## Phần 2 — Android

### 2.1 Data layer (theo pattern hiện có)
- `data/remote/api/ChatbotApi.java`: `sendMessage(ChatbotRequest)`, `getHistory()`.
- `data/model/ChatbotMessage.java`, `ChatbotSuggestedAction.java` (POJO khớp DTO backend).
- `data/repository/ChatbotRepository.java` — Retrofit callback → LiveData, đúng pattern các repository khác.
- `ui/viewmodel/ChatbotViewModel.java` — self-removing `observeForever` (theo quy ước CLAUDE.md, KHÔNG dùng MediatorLiveData anti-pattern).

### 2.2 UI: Floating bubble + Bottom Sheet
- `activity_customer_main.xml`: bọc nội dung hiện tại (`LinearLayout` chứa `ViewPager2` + `BottomNavigationView`) trong `FrameLayout` (hoặc `CoordinatorLayout`), thêm `com.google.android.material.floatingactionbutton.FloatingActionButton` (id `fab_ai_chat`) neo `bottom|end`, margin để không che `BottomNavigationView`.
- `CustomerMainActivity.java`: `fab.setOnClickListener` → mở `AiChatBottomSheetFragment` (BottomSheetDialogFragment, `expand` state).
- `ui/customer/aichat/AiChatBottomSheetFragment.java`:
  - RecyclerView messages + EditText + nút gửi (tái dùng style từ `activity_chat.xml`/`item_chat_sender`/`item_chat_receiver`).
  - Load `getHistory()` khi mở; gọi `sendMessage()` khi user nhập.
  - Loading indicator khi đợi Gemini trả lời (có thể mất 1-3s).
  - Quick chips gợi ý dưới ô nhập khi mới mở: "Tìm sofa phòng khách", "Sản phẩm dưới 5 triệu", "Xem giỏ hàng", "Gợi ý theo phòng".
- `ui/customer/aichat/AiChatAdapter.java` — multi view-type:
  - `TEXT_USER` / `TEXT_BOT` (tái dùng layout chat hiện có)
  - `PRODUCT_CARD` (layout mới `item_chat_product_card.xml` — ảnh, tên, giá, tap → `ProductDetailActivity`)
  - `ACTION_CHIPS` (layout mới `item_chat_actions.xml` — chip "Xem giỏ hàng" / "Thanh toán" / "Xem sản phẩm" điều hướng tới `CartFragment`/`CheckoutActivity`/`ProductDetailActivity`)

---

## Phased rollout

1. **Phase 1 — Backend đọc (RAG cơ bản)**: `ai_chat_messages` entity, `GeminiClient`, tools đọc-only (`search_products`, `get_product_detail`, `list_categories`), `ChatbotService` + `ChatbotController`. Test bằng Postman trước.
2. **Phase 2 — Android UI cơ bản**: FAB + bottom sheet + chat text thuần (chưa render product card/action), gọi được Phase 1.
3. **Phase 3 — Rich response**: `products[]` → `PRODUCT_CARD`, tap mở `ProductDetailActivity`.
4. **Phase 4 — Hành động giỏ hàng an toàn**: tools `add_to_cart`/`remove_cart_item`/`get_cart`, confirm trước write tool, variant selection nếu thiếu, `suggestedActions` → `ACTION_CHIPS` (Xem giỏ / Thanh toán).
5. **Phase 5 — Tra cứu đơn hàng cá nhân**: `get_my_orders`, `get_order_detail`, `get_wishlist`.
6. **Phase 6 — Polish + hardening**: tinh chỉnh system prompt, xử lý lỗi Gemini timeout/quota, giới hạn rate per user (chống spam tốn token), input length limit, logging tool calls, loading/error UI.

---

## Verification

- Backend: gọi `POST /api/chatbot/message` qua Postman với JWT thật, kiểm tra: (a) câu hỏi "có sofa nào dưới 5 triệu không" → đúng sản phẩm từ DB; (b) "thêm sofa vải gỗ sồi vào giỏ" → kiểm tra bảng `cart_items` có record mới + response có `suggestedActions`.
- Backend safety: câu "sofa này đẹp không" không được tự thêm giỏ; câu "thêm sofa vào giỏ" nhưng có nhiều variant phải hỏi lại variant; câu "thêm 999 cái" phải bị chặn bởi quantity/stock validation.
- Android: build & chạy emulator, mở FAB từ Home/Search/Cart/Profile đều thấy bubble; chat hỏi sản phẩm → card hiện đúng ảnh/giá; bấm "Thêm vào giỏ" qua chatbot rồi mở tab Cart → item xuất hiện thật.
- Kiểm tra giới hạn: gửi câu hỏi yêu cầu lặp tool nhiều lần → đảm bảo dừng ở `MAX_TOOL_ITERATIONS` và vẫn trả lời được (graceful fallback message).
