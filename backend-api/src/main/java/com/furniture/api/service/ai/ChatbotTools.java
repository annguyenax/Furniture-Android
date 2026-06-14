package com.furniture.api.service.ai;

import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class ChatbotTools {

    public List<Map<String, Object>> geminiTools() {
        return List.of(Map.of("functionDeclarations", List.of(
                function("search_products", "Tìm sản phẩm nội thất trong catalog thật theo từ khóa, phòng/danh mục và khoảng giá.",
                        object(Map.of(
                                "keyword", string("Từ khóa sản phẩm, ví dụ sofa, bàn ăn, tủ áo"),
                                "categoryName", string("Tên phòng/danh mục, ví dụ Phòng khách, Phòng ngủ"),
                                "minPrice", number("Giá thấp nhất"),
                                "maxPrice", number("Giá cao nhất"),
                                "sortBy", string("Sắp xếp: price_asc, price_desc, rating_desc")
                        ))),
                function("get_product_detail", "Lấy thông tin chi tiết một sản phẩm theo productId.",
                        object(Map.of("productId", number("ID sản phẩm")))),
                function("list_categories", "Liệt kê các phòng/danh mục đang có trong app.",
                        object(Map.of())),
                function("add_to_cart", "Thêm sản phẩm vào giỏ khi khách xác nhận muốn mua. Nếu có nhiều phân loại mà khách chưa chọn rõ, hệ thống sẽ yêu cầu chọn phân loại thay vì tự thêm.",
                        object(Map.of(
                                "productId", number("ID sản phẩm cần thêm vào giỏ hàng"),
                                "variantId", number("ID phân loại màu/kích thước cụ thể, bỏ trống nếu khách chưa chọn"),
                                "quantity", number("Số lượng muốn thêm, mặc định là 1, tối đa 10")
                        ))),
                function("get_cart", "Xem giỏ hàng hiện tại của khách.",
                        object(Map.of())),
                function("remove_cart_item", "Xóa một dòng sản phẩm khỏi giỏ hàng khi khách yêu cầu rõ ràng.",
                        object(Map.of("cartItemId", number("ID dòng sản phẩm trong giỏ hàng cần xóa")))),
                function("get_my_orders", "Xem các đơn hàng của khách hiện tại. Có thể lọc theo trạng thái.",
                        object(Map.of(
                                "status", string("Trạng thái đơn: PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED hoặc tiếng Việt tương ứng"),
                                "limit", number("Số đơn muốn xem, mặc định 5, tối đa 10")
                        ))),
                function("get_order_detail", "Xem chi tiết một đơn hàng của chính khách hiện tại.",
                        object(Map.of("orderId", number("ID đơn hàng cần xem")))),
                function("get_wishlist", "Xem danh sách sản phẩm yêu thích của khách.",
                        object(Map.of())),
                function("add_to_wishlist", "Thêm sản phẩm vào danh sách yêu thích khi khách yêu cầu rõ ràng.",
                        object(Map.of("productId", number("ID sản phẩm cần thêm vào yêu thích")))),
                function("get_shipping_addresses", "Xem danh sách địa chỉ giao hàng của khách hiện tại.",
                        object(Map.of())),
                function("get_default_shipping_address", "Xem địa chỉ giao hàng mặc định của khách hiện tại.",
                        object(Map.of()))
        )));
    }

    private Map<String, Object> function(String name, String description, Map<String, Object> parameters) {
        return Map.of(
                "name", name,
                "description", description,
                "parameters", parameters
        );
    }

    private Map<String, Object> object(Map<String, Object> properties) {
        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("type", "OBJECT");
        schema.put("properties", properties);
        return schema;
    }

    private Map<String, Object> string(String description) {
        return Map.of("type", "STRING", "description", description);
    }

    private Map<String, Object> number(String description) {
        return Map.of("type", "NUMBER", "description", description);
    }
}
