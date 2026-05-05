package com.furniture.app.data.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Product variant model
 */
public class ProductVariant implements Serializable {

    @SerializedName("variantId")
    private int variantId;

    @SerializedName("size")
    private String size;

    @SerializedName("color")
    private String color;

    @SerializedName("material")
    private String material;

    @SerializedName("price")
    private BigDecimal price;

    @SerializedName("stock")
    private int stock;

    @SerializedName("imageUrl")
    private String imageUrl;

    // Getters and Setters
    public int getVariantId() {
        return variantId;
    }

    public void setVariantId(int variantId) {
        this.variantId = variantId;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getVariantInfo() {
        StringBuilder sb = new StringBuilder();
        if (color != null && !color.isEmpty()) {
            sb.append(color);
        }
        if (size != null && !size.isEmpty()) {
            if (sb.length() > 0) sb.append(" - ");
            sb.append(size);
        }
        if (material != null && !material.isEmpty()) {
            if (sb.length() > 0) sb.append(" - ");
            sb.append(material);
        }
        return sb.toString();
    }
}
