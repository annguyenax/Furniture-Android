package com.furniture.api.repository;

import com.furniture.api.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    Page<Product> findByShopId(Integer shopId, Pageable pageable);

    Page<Product> findByCategoryId(Integer categoryId, Pageable pageable);

    Page<Product> findByStatus(Product.ProductStatus status, Pageable pageable);

    // JOIN FETCH để tránh N+1 khi lấy danh sách sản phẩm
    @Query(value = "SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.variants LEFT JOIN FETCH p.category WHERE p.status = :status",
           countQuery = "SELECT COUNT(p) FROM Product p WHERE p.status = :status")
    Page<Product> findByStatusFetch(@Param("status") Product.ProductStatus status, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.productName LIKE %:keyword% OR p.description LIKE %:keyword%")
    Page<Product> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query(value = "SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.variants LEFT JOIN FETCH p.category WHERE p.status = 'ACTIVE' AND (p.productName LIKE %:keyword% OR p.description LIKE %:keyword%)",
           countQuery = "SELECT COUNT(p) FROM Product p WHERE p.status = 'ACTIVE' AND (p.productName LIKE %:keyword% OR p.description LIKE %:keyword%)")
    Page<Product> searchByKeywordFetch(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.variants LEFT JOIN FETCH p.category WHERE p.status = 'ACTIVE'")
    List<Product> findAllActiveFetch(Pageable pageable);

    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.variants LEFT JOIN FETCH p.category WHERE p.status = 'ACTIVE' ORDER BY p.sold DESC")
    List<Product> findFeaturedProducts(Pageable pageable);

    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.variants LEFT JOIN FETCH p.category WHERE p.status = 'ACTIVE' ORDER BY p.createdAt DESC")
    List<Product> findNewArrivals(Pageable pageable);

    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.variants LEFT JOIN FETCH p.category WHERE p.status = 'ACTIVE' AND p.discount > 0 ORDER BY p.discount DESC")
    List<Product> findBestDeals(Pageable pageable);

    List<Product> findByCategoryIdAndStatusAndProductIdNot(Integer categoryId, Product.ProductStatus status, Integer productId, Pageable pageable);

    Long countByShopId(Integer shopId);

    boolean existsByCategoryId(Integer categoryId);
}
