package com.furniture.api.config;

import com.furniture.api.model.*;
import com.furniture.api.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
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
    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seedRoles();
        seedDemoAccounts();
        seedCategories();
        seedVendorAndShop();
        seedProducts();
        log.info("Data initialization complete.");
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

    private void seedVendorAndShop() {
        if (shopRepository.count() > 0) return;

        Role customerRole = roleRepository.findByRoleName("CUSTOMER")
            .orElseThrow(() -> new RuntimeException("CUSTOMER role not found"));

        User shopOwner;
        if (!userRepository.existsByEmail("shop@furniture.com")) {
            shopOwner = User.builder()
                .firstName("Minh").lastName("Tuấn")
                .username("shopowner1")
                .email("shop@furniture.com")
                .password(passwordEncoder.encode("123456"))
                .status(User.UserStatus.ACTIVE)
                .authProvider(User.AuthProvider.LOCAL)
                .isVerified(true)
                .roles(Set.of(customerRole))
                .build();
            shopOwner = userRepository.save(shopOwner);
        } else {
            shopOwner = userRepository.findByEmail("shop@furniture.com").orElseThrow();
        }

        Shop shop = Shop.builder()
            .ownerId(shopOwner.getUserId())
            .shopName("Nội Thất Gia Đình")
            .description("Chuyên cung cấp nội thất cao cấp cho mọi không gian sống")
            .logo("https://picsum.photos/seed/shop1/200/200")
            .banner("https://picsum.photos/seed/shopbanner1/800/300")
            .rating(new BigDecimal("4.5"))
            .status(Shop.ShopStatus.ACTIVE)
            .address("123 Nguyễn Văn Linh, Quận 7, TP.HCM")
            .build();
        shopRepository.save(shop);
    }

    private void seedDemoAccounts() {
        Role adminRole = roleRepository.findByRoleName("ADMIN")
            .orElseThrow(() -> new RuntimeException("ADMIN role not found"));
        Role customerRole = roleRepository.findByRoleName("CUSTOMER")
            .orElseThrow(() -> new RuntimeException("CUSTOMER role not found"));

        if (!userRepository.existsByEmail("admin@furniture.com") && !userRepository.existsByUsername("admin_system")) {
            userRepository.save(User.builder()
                .firstName("Admin").lastName("System")
                .username("admin_system")
                .email("admin@furniture.com")
                .password(passwordEncoder.encode("123456"))
                .status(User.UserStatus.ACTIVE)
                .authProvider(User.AuthProvider.LOCAL)
                .isVerified(true)
                .roles(Set.of(adminRole))
                .build());
            log.info("Created admin account: admin@furniture.com");
        }

        if (!userRepository.existsByEmail("customer@furniture.com")) {
            userRepository.save(User.builder()
                .firstName("Khách").lastName("Hàng")
                .username("customer1")
                .email("customer@furniture.com")
                .password(passwordEncoder.encode("123456"))
                .status(User.UserStatus.ACTIVE)
                .authProvider(User.AuthProvider.LOCAL)
                .isVerified(true)
                .roles(Set.of(customerRole))
                .build());
            log.info("Created customer account: customer@furniture.com");
        }
    }

    private void seedProducts() {
        if (productRepository.count() > 0) return;

        Shop shop = shopRepository.findAll().get(0);
        List<Category> categories = categoryRepository.findAll();
        if (categories.isEmpty()) return;

        Category phongKhach = categories.get(0);
        Category phongNgu   = categories.get(1);
        Category phongAn    = categories.get(2);
        Category phongLamViec = categories.get(3);

        // ── Phòng khách ──────────────────────────────────────────────────────────
        createProduct(shop, phongKhach, "Sofa Da Cao Cấp 3 Chỗ",
            "Sofa da thật nhập khẩu Ý, khung gỗ sồi, đệm foam siêu êm. Màu nâu sang trọng.",
            25, 120, new BigDecimal("10"), "200x85x85cm", "8.50",
            new BigDecimal("4.7"), 89,
            List.of(
                variant("Nâu", "Da thật", null, 8990000, 10, 1),
                variant("Đen", "Da thật", null, 8990000, 8, 2),
                variant("Kem", "Da tổng hợp", null, 7490000, 7, 3)
            ));

        createProduct(shop, phongKhach, "Bàn Trà Gỗ Óc Chó",
            "Bàn trà gỗ óc chó nguyên tấm, chân sắt sơn tĩnh điện đen matte. Thiết kế tối giản.",
            30, 80, new BigDecimal("5"), "120x60x45cm", "18.00",
            new BigDecimal("4.5"), 62,
            List.of(
                variant("Tự nhiên", "Gỗ óc chó", null, 3290000, 15, 4),
                variant("Nâu đậm", "Gỗ óc chó", null, 3290000, 15, 5)
            ));

        createProduct(shop, phongKhach, "Kệ Tivi Gỗ Thông 160cm",
            "Kệ tivi phong cách Scandinavian, gỗ thông tự nhiên, nhiều ngăn chứa đồ tiện dụng.",
            20, 45, BigDecimal.ZERO, "160x35x55cm", "22.00",
            new BigDecimal("4.3"), 38,
            List.of(
                variant("Trắng sữa", "Gỗ thông", null, 2490000, 10, 6),
                variant("Nâu nhạt", "Gỗ thông", null, 2490000, 10, 7)
            ));

        // ── Phòng ngủ ─────────────────────────────────────────────────────────────
        createProduct(shop, phongNgu, "Giường Ngủ Gỗ Sồi King Size",
            "Giường gỗ sồi Mỹ nguyên khối, đầu giường bọc nỉ, hộc kéo tiện lợi. Kích thước King.",
            15, 55, new BigDecimal("15"), "180x200cm", "85.00",
            new BigDecimal("4.8"), 41,
            List.of(
                variant("180x200cm", "Gỗ sồi + Nỉ xám", null, 12900000, 8, 8),
                variant("160x200cm", "Gỗ sồi + Nỉ xám", null, 11500000, 7, 9)
            ));

        createProduct(shop, phongNgu, "Tủ Quần Áo 4 Cánh Gương",
            "Tủ quần áo cánh gương toàn thân, bên trong bố trí khoa học với thanh treo và ngăn kéo.",
            12, 28, BigDecimal.ZERO, "200x60x220cm", "110.00",
            new BigDecimal("4.4"), 19,
            List.of(
                variant("Trắng", "MDF phủ Melamine", null, 8500000, 6, 10),
                variant("Walnut", "MDF phủ Melamine", null, 9200000, 6, 11)
            ));

        createProduct(shop, phongNgu, "Đầu Giường Bọc Nỉ Cao Cấp",
            "Đầu giường bọc nỉ nhung mềm, khung gỗ chắc chắn. Phù hợp giường 160 và 180cm.",
            25, 70, new BigDecimal("20"), "180x10x120cm", "15.00",
            new BigDecimal("4.6"), 53,
            List.of(
                variant("Xám đậm", "Nỉ nhung", null, 2190000, 12, 12),
                variant("Xanh navy", "Nỉ nhung", null, 2190000, 13, 13)
            ));

        // ── Phòng ăn ──────────────────────────────────────────────────────────────
        createProduct(shop, phongAn, "Bộ Bàn Ăn 6 Ghế Gỗ Cao Su",
            "Bộ bàn ăn gỗ cao su tự nhiên, mặt bàn dày 4cm, ghế bọc nỉ. Phong cách hiện đại.",
            8, 32, new BigDecimal("8"), "160x90x75cm", "95.00",
            new BigDecimal("4.5"), 27,
            List.of(
                variant("Nâu tự nhiên", "Gỗ cao su", null, 15900000, 4, 14),
                variant("Trắng sữa", "Gỗ cao su sơn trắng", null, 16500000, 4, 15)
            ));

        createProduct(shop, phongAn, "Ghế Ăn Scandinavia Chân Gỗ",
            "Ghế ăn phong cách Bắc Âu, khung gỗ sồi, mặt ghế PP cao cấp. Giá cho 1 ghế.",
            50, 200, BigDecimal.ZERO, "45x50x80cm", "3.50",
            new BigDecimal("4.7"), 175,
            List.of(
                variant("Trắng", "PP + Gỗ sồi", null, 890000, 20, 16),
                variant("Đen", "PP + Gỗ sồi", null, 890000, 15, 17),
                variant("Xám", "PP + Gỗ sồi", null, 890000, 15, 18)
            ));

        // ── Phòng làm việc ────────────────────────────────────────────────────────
        createProduct(shop, phongLamViec, "Bàn Làm Việc Thông Minh 140cm",
            "Bàn làm việc mặt MDF phủ Melamine chống xước, chân thép không gỉ, có thanh quản lý dây cáp.",
            20, 88, new BigDecimal("12"), "140x70x75cm", "25.00",
            new BigDecimal("4.4"), 72,
            List.of(
                variant("Trắng / Chân đen", "MDF + Thép", null, 3490000, 10, 19),
                variant("Walnut / Chân đen", "MDF + Thép", null, 3690000, 10, 20)
            ));

        createProduct(shop, phongLamViec, "Ghế Công Thái Học Ergonomic",
            "Ghế văn phòng ergonomic, tựa lưng lưới thoáng khí, tựa đầu và tựa tay điều chỉnh được.",
            15, 60, new BigDecimal("10"), "65x65x115-125cm", "14.00",
            new BigDecimal("4.9"), 48,
            List.of(
                variant("Đen", "Lưới + Nhựa ABS", null, 4990000, 8, 21),
                variant("Xám", "Lưới + Nhựa ABS", null, 4990000, 7, 22)
            ));
    }

    private void createProduct(Shop shop, Category category, String name, String description,
                               int stock, int sold, BigDecimal discount,
                               String dimensions, String weight,
                               BigDecimal avgRating, int reviewCount,
                               List<ProductVariantSeed> variantSeeds) {
        Product product = Product.builder()
            .productName(name)
            .description(description)
            .shopId(shop.getShopId())
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
