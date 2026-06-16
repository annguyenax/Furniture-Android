package com.furniture.api.config;

import com.furniture.api.model.*;
import com.furniture.api.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ReturnRequestRepository returnRequestRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        fixDatabaseSchema();
        seedRoles();
        seedDemoAccounts();
        seedCategories();
        seedProducts();
        seedReturnDemoData();
        log.info("Data initialization complete.");
    }

    private void fixDatabaseSchema() {
        try {
            log.info("Fixing database schema: Converting ENUM to VARCHAR for payment_method...");
            jdbcTemplate.execute("ALTER TABLE orders MODIFY payment_method VARCHAR(20) NOT NULL");
            jdbcTemplate.execute("ALTER TABLE payments MODIFY payment_method VARCHAR(20) NOT NULL");
            log.info("Database schema fix applied successfully.");
        } catch (Exception e) {
            log.warn("Database schema fix skipped or already applied: {}", e.getMessage());
        }
    }

    private void seedRoles() {
        for (String name : List.of("CUSTOMER", "ADMIN")) {
            if (!roleRepository.existsByRoleName(name)) {
                roleRepository.save(new Role(name));
            }
        }
    }

    private void seedCategories() {
        if (categoryRepository.count() > 0) return;

        List<Category> categories = List.of(
            Category.builder().categoryName("Phòng khách").description("Sofa, bàn trà, kệ tivi")
                .image("https://picsum.photos/seed/cat1/400/300").build(),
            Category.builder().categoryName("Phòng ngủ").description("Giường, tủ quần áo, bàn phấn")
                .image("https://picsum.photos/seed/cat2/400/300").build(),
            Category.builder().categoryName("Phòng ăn").description("Bàn ăn, ghế ăn, tủ bếp")
                .image("https://picsum.photos/seed/cat3/400/300").build(),
            Category.builder().categoryName("Phòng làm việc").description("Bàn làm việc, ghế văn phòng, kệ sách")
                .image("https://picsum.photos/seed/cat4/400/300").build(),
            Category.builder().categoryName("Ngoài trời").description("Bàn ghế sân vườn, ghế xích đu")
                .image("https://picsum.photos/seed/cat5/400/300").build(),
            Category.builder().categoryName("Trang trí").description("Đèn, thảm, gương, tranh")
                .image("https://picsum.photos/seed/cat6/400/300").build()
        );
        categoryRepository.saveAll(categories);
    }

    private void seedDemoAccounts() {
        Role adminRole = roleRepository.findByRoleName("ADMIN")
            .orElseThrow(() -> new RuntimeException("ADMIN role not found"));
        Role customerRole = roleRepository.findByRoleName("CUSTOMER")
            .orElseThrow(() -> new RuntimeException("CUSTOMER role not found"));

        if (!userRepository.existsByEmail("admin@fur.vn") && !userRepository.existsByUsername("admin")) {
            userRepository.save(User.builder()
                .firstName("Quản trị").lastName("Hệ thống")
                .username("admin")
                .email("admin@fur.vn")
                .password(passwordEncoder.encode("123456"))
                .status(User.UserStatus.ACTIVE)
                .authProvider(User.AuthProvider.LOCAL)
                .isVerified(true)
                .roles(Set.of(adminRole))
                .build());
            log.info("Created admin account: admin@fur.vn");
        }

        if (!userRepository.existsByEmail("customer@fur.vn")) {
            userRepository.save(User.builder()
                .firstName("Khách").lastName("Hàng")
                .username("customer")
                .email("customer@fur.vn")
                .password(passwordEncoder.encode("123456"))
                .status(User.UserStatus.ACTIVE)
                .authProvider(User.AuthProvider.LOCAL)
                .isVerified(true)
                .roles(Set.of(customerRole))
                .build());
            log.info("Created customer account: customer@fur.vn");
        }
    }

    private void seedProducts() {
        if (productRepository.count() > 0) return;

        List<Category> categories = categoryRepository.findAll();
        if (categories.isEmpty()) return;

        Category phongKhach = categories.get(0);
        Category phongNgu   = categories.get(1);
        Category phongAn    = categories.get(2);
        Category phongLamViec = categories.get(3);

        // ── Phòng khách ──────────────────────────────────────────────────────────
        createProduct(phongKhach, "Sofa Da Cao Cấp 3 Chỗ",
            "Sofa da thật nhập khẩu Ý, khung gỗ sồi, đệm foam siêu êm. Màu nâu sang trọng.",
            25, 120, new BigDecimal("10"), "200x85x85cm", "8.50",
            new BigDecimal("4.7"), 89,
            List.of(
                variant("Nâu", "Da thật", null, 8990000, 10, 1),
                variant("Đen", "Da thật", null, 8990000, 8, 2),
                variant("Kem", "Da tổng hợp", null, 7490000, 7, 3)
            ));

        createProduct(phongKhach, "Bàn Trà Gỗ Óc Chó",
            "Bàn trà gỗ óc chó nguyên tấm, chân sắt sơn tĩnh điện đen matte. Thiết kế tối giản.",
            30, 80, new BigDecimal("5"), "120x60x45cm", "18.00",
            new BigDecimal("4.5"), 62,
            List.of(
                variant("Tự nhiên", "Gỗ óc chó", null, 3290000, 15, 4),
                variant("Nâu đậm", "Gỗ óc chó", null, 3290000, 15, 5)
            ));

        createProduct(phongKhach, "Kệ Tivi Gỗ Thông 160cm",
            "Kệ tivi phong cách Scandinavian, gỗ thông tự nhiên, nhiều ngăn chứa đồ tiện dụng.",
            20, 45, BigDecimal.ZERO, "160x35x55cm", "22.00",
            new BigDecimal("4.3"), 38,
            List.of(
                variant("Trắng sữa", "Gỗ thông", null, 2490000, 10, 6),
                variant("Nâu nhạt", "Gỗ thông", null, 2490000, 10, 7)
            ));

        // ── Phòng ngủ ─────────────────────────────────────────────────────────────
        createProduct(phongNgu, "Giường Ngủ Gỗ Sồi King Size",
            "Giường gỗ sồi Mỹ nguyên khối, đầu giường bọc nỉ, hộc kéo tiện lợi. Kích thước King.",
            15, 55, new BigDecimal("15"), "180x200cm", "85.00",
            new BigDecimal("4.8"), 41,
            List.of(
                variant("180x200cm", "Gỗ sồi + Nỉ xám", null, 12900000, 8, 8),
                variant("160x200cm", "Gỗ sồi + Nỉ xám", null, 11500000, 7, 9)
            ));

        createProduct(phongNgu, "Tủ Quần Áo 4 Cánh Gương",
            "Tủ quần áo cánh gương toàn thân, bên trong bố trí khoa học với thanh treo và ngăn kéo.",
            12, 28, BigDecimal.ZERO, "200x60x220cm", "110.00",
            new BigDecimal("4.4"), 19,
            List.of(
                variant("Trắng", "MDF phủ Melamine", null, 8500000, 6, 10),
                variant("Walnut", "MDF phủ Melamine", null, 9200000, 6, 11)
            ));

        createProduct(phongNgu, "Đầu Giường Bọc Nỉ Cao Cấp",
            "Đầu giường bọc nỉ nhung mềm, khung gỗ chắc chắn. Phù hợp giường 160 và 180cm.",
            25, 70, new BigDecimal("20"), "180x10x120cm", "15.00",
            new BigDecimal("4.6"), 53,
            List.of(
                variant("Xám đậm", "Nỉ nhung", null, 2190000, 12, 12),
                variant("Xanh navy", "Nỉ nhung", null, 2190000, 13, 13)
            ));

        // ── Phòng ăn ──────────────────────────────────────────────────────────────
        createProduct(phongAn, "Bộ Bàn Ăn 6 Ghế Gỗ Cao Su",
            "Bộ bàn ăn gỗ cao su tự nhiên, mặt bàn dày 4cm, ghế bọc nỉ. Phong cách hiện đại.",
            8, 32, new BigDecimal("8"), "160x90x75cm", "95.00",
            new BigDecimal("4.5"), 27,
            List.of(
                variant("Nâu tự nhiên", "Gỗ cao su", null, 15900000, 4, 14),
                variant("Trắng sữa", "Gỗ cao su sơn trắng", null, 16500000, 4, 15)
            ));

        createProduct(phongAn, "Ghế Ăn Scandinavia Chân Gỗ",
            "Ghế ăn phong cách Bắc Âu, khung gỗ sồi, mặt ghế PP cao cấp. Giá cho 1 ghế.",
            50, 200, BigDecimal.ZERO, "45x50x80cm", "3.50",
            new BigDecimal("4.7"), 175,
            List.of(
                variant("Trắng", "PP + Gỗ sồi", null, 890000, 20, 16),
                variant("Đen", "PP + Gỗ sồi", null, 890000, 15, 17),
                variant("Xám", "PP + Gỗ sồi", null, 890000, 15, 18)
            ));

        // ── Phòng làm việc ────────────────────────────────────────────────────────
        createProduct(phongLamViec, "Bàn Làm Việc Thông Minh 140cm",
            "Bàn làm việc mặt MDF phủ Melamine chống xước, chân thép không gỉ, có thanh quản lý dây cáp.",
            20, 88, new BigDecimal("12"), "140x70x75cm", "25.00",
            new BigDecimal("4.4"), 72,
            List.of(
                variant("Trắng / Chân đen", "MDF + Thép", null, 3490000, 10, 19),
                variant("Walnut / Chân đen", "MDF + Thép", null, 3690000, 10, 20)
            ));

        createProduct(phongLamViec, "Ghế Công Thái Học Ergonomic",
            "Ghế văn phòng ergonomic, tựa lưng lưới thoáng khí, tựa đầu và tựa tay điều chỉnh được.",
            15, 60, new BigDecimal("10"), "65x65x115-125cm", "14.00",
            new BigDecimal("4.9"), 48,
            List.of(
                variant("Đen", "Lưới + Nhựa ABS", null, 4990000, 8, 21),
                variant("Xám", "Lưới + Nhựa ABS", null, 4990000, 7, 22)
            ));
    }

    /**
     * Seeds a few DELIVERED orders + return requests (PENDING/APPROVED/REJECTED)
     * for the demo customer account, so the admin/customer return-management
     * screens have realistic data to test against.
     */
    private void seedReturnDemoData() {
        Optional<User> customerOpt = userRepository.findByEmail("customer@fur.vn");
        if (customerOpt.isEmpty()) return;
        Integer customerId = customerOpt.get().getUserId();
        if (orderRepository.countByUserId(customerId) > 0) return;

        OrderItem item1 = createDeliveredOrder(customerId, 1, 1, new BigDecimal("8990000"));  // Sofa Da Cao Cấp 3 Chỗ - Nâu
        OrderItem item2 = createDeliveredOrder(customerId, 8, 21, new BigDecimal("4990000")); // Ghế Công Thái Học Ergonomic - Đen
        OrderItem item3 = createDeliveredOrder(customerId, 2, 4, new BigDecimal("3290000"));  // Bàn Trà Gỗ Óc Chó - Tự nhiên
        OrderItem item4 = createDeliveredOrder(customerId, 3, 6, new BigDecimal("2490000"));  // Kệ Tivi Gỗ Thông 160cm - Trắng sữa

        seedReturnRequests(customerId, item1, item2, item3, item4);
    }

    private OrderItem createDeliveredOrder(Integer userId, Integer productId, Integer variantId, BigDecimal price) {
        Order order = orderRepository.save(Order.builder()
            .userId(userId)
            .recipientName("Khách Hàng")
            .recipientPhone("0900000000")
            .shippingAddressText("123 Đường Lê Lợi, Phường Bến Nghé, Quận 1, TP. Hồ Chí Minh")
            .totalPrice(price)
            .shippingFee(BigDecimal.ZERO)
            .paymentMethod(Order.PaymentMethod.COD)
            .status(Order.OrderStatus.DELIVERED)
            .paymentStatus(Order.PaymentStatus.PAID)
            .build());

        return orderItemRepository.save(OrderItem.builder()
            .orderId(order.getOrderId())
            .productId(productId)
            .variantId(variantId)
            .quantity(1)
            .price(price)
            .discount(BigDecimal.ZERO)
            .total(price)
            .build());
    }

    private void seedReturnRequests(Integer userId, OrderItem item1, OrderItem item2, OrderItem item3, OrderItem item4) {
        // PENDING - chờ admin xử lý, có ảnh minh chứng
        returnRequestRepository.save(ReturnRequest.builder()
            .orderId(item1.getOrderId())
            .orderItemId(item1.getOrderItemId())
            .userId(userId)
            .reason("Sofa bị lệch một bên chân, ngồi không vững. Mong shop kiểm tra và hỗ trợ đổi sản phẩm mới.")
            .evidenceUrl("https://picsum.photos/seed/return1/600/400")
            .evidenceType(ReturnRequest.EvidenceType.IMAGE)
            .status(ReturnRequest.ReturnStatus.PENDING)
            .build());

        // PENDING - chờ admin xử lý, không có ảnh minh chứng
        returnRequestRepository.save(ReturnRequest.builder()
            .orderId(item2.getOrderId())
            .orderItemId(item2.getOrderItemId())
            .userId(userId)
            .reason("Tựa lưng ghế bị kêu cọt kẹt khi ngả người, có thể do lỗi linh kiện bên trong.")
            .status(ReturnRequest.ReturnStatus.PENDING)
            .build());

        // APPROVED - hoàn trả toàn bộ đơn hàng, đã được xác nhận
        Order order3 = orderRepository.findById(item3.getOrderId()).orElse(null);
        if (order3 != null) {
            order3.setPaymentStatus(Order.PaymentStatus.REFUNDED);
            orderRepository.save(order3);
        }
        returnRequestRepository.save(ReturnRequest.builder()
            .orderId(item3.getOrderId())
            .orderItemId(null)
            .userId(userId)
            .reason("Đặt nhầm màu, muốn hoàn trả toàn bộ đơn để đổi sang màu khác.")
            .evidenceUrl("https://picsum.photos/seed/return3/600/400")
            .evidenceType(ReturnRequest.EvidenceType.IMAGE)
            .status(ReturnRequest.ReturnStatus.APPROVED)
            .build());

        // REJECTED - đã từ chối, có ghi chú của admin
        returnRequestRepository.save(ReturnRequest.builder()
            .orderId(item4.getOrderId())
            .orderItemId(item4.getOrderItemId())
            .userId(userId)
            .reason("Kệ tivi bị nứt nhẹ ở góc cạnh, nghi do va đập trong quá trình vận chuyển.")
            .status(ReturnRequest.ReturnStatus.REJECTED)
            .adminNote("Đã kiểm tra hình ảnh, vết nứt không ảnh hưởng đến chất lượng sử dụng và không thuộc diện lỗi do nhà sản xuất nên không đủ điều kiện đổi trả.")
            .build());
    }

    private void createProduct(Category category, String name, String description,
                               int stock, int sold, BigDecimal discount,
                               String dimensions, String weight,
                               BigDecimal avgRating, int reviewCount,
                               List<ProductVariantSeed> variantSeeds) {
        Product product = Product.builder()
            .productName(name)
            .description(description)
            .categoryId(category.getCategoryId())
            .stock(stock)
            .sold(sold)
            .discount(discount)
            .dimensions(dimensions)
            .weight(new BigDecimal(weight))
            .status(Product.ProductStatus.ACTIVE)
            .averageRating(avgRating)
            .reviewCount(reviewCount)
            .build();
        product = productRepository.save(product);

        for (ProductVariantSeed s : variantSeeds) {
            productVariantRepository.save(ProductVariant.builder()
                .productId(product.getProductId())
                .color(s.color)
                .material(s.material)
                .price(new BigDecimal(s.price))
                .stock(s.stock)
                .imageUrl("https://picsum.photos/seed/variant" + s.seed + "/500/500")
                .build());
        }
    }

    private record ProductVariantSeed(String color, String material, String size,
                                      long price, int stock, int seed) {}

    private ProductVariantSeed variant(String color, String material, String size,
                                       long price, int stock, int seed) {
        return new ProductVariantSeed(color, material, size, price, stock, seed);
    }
}
