package com.furniture.app.data.model;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;

public class WishlistItem {
    @SerializedName("wishlistId") private Integer wishlistId;
    @SerializedName("productId") private Integer productId;
    @SerializedName("productName") private String productName;
    @SerializedName("productImage") private String productImage;
    @SerializedName("price") private BigDecimal price;
    @SerializedName("discount") private BigDecimal discount;

    public Integer getWishlistId() { return wishlistId; }
    public Integer getProductId() { return productId; }
    public String getProductName() { return productName; }
    public String getProductImage() { return productImage; }
    public BigDecimal getPrice() { return price; }
    public BigDecimal getDiscount() { return discount; }
}
