package com.furniture.app.data.model;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

public class ChatbotVariant {

    @SerializedName("variantId")
    private Integer variantId;

    @SerializedName("color")
    private String color;

    @SerializedName("size")
    private String size;

    @SerializedName("price")
    private BigDecimal price;

    @SerializedName("stock")
    private Integer stock;

    @SerializedName("imageUrl")
    private String imageUrl;

    public Integer getVariantId() { return variantId; }
    public String getColor() { return color; }
    public String getSize() { return size; }
    public BigDecimal getPrice() { return price; }
    public Integer getStock() { return stock; }
    public String getImageUrl() { return imageUrl; }
}
