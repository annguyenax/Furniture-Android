package com.furniture.app.data.model;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.List;

public class ChatbotProduct {

    @SerializedName("productId")
    private Integer productId;

    @SerializedName("name")
    private String name;

    @SerializedName("category")
    private String category;

    @SerializedName("price")
    private BigDecimal price;

    @SerializedName("discount")
    private BigDecimal discount;

    @SerializedName("finalPrice")
    private BigDecimal finalPrice;

    @SerializedName("stock")
    private Integer stock;

    @SerializedName("rating")
    private BigDecimal rating;

    @SerializedName("shortDescription")
    private String shortDescription;

    @SerializedName("imageUrl")
    private String imageUrl;

    @SerializedName("variants")
    private List<ChatbotVariant> variants;

    public Integer getProductId() { return productId; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public BigDecimal getPrice() { return price; }
    public BigDecimal getDiscount() { return discount; }
    public BigDecimal getFinalPrice() { return finalPrice; }
    public Integer getStock() { return stock; }
    public BigDecimal getRating() { return rating; }
    public String getShortDescription() { return shortDescription; }
    public String getImageUrl() { return imageUrl; }
    public List<ChatbotVariant> getVariants() { return variants; }
}
