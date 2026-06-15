package com.furniture.app.data.model;

import com.google.gson.annotations.SerializedName;

public class ChatbotSuggestedAction {

    @SerializedName("type")
    private String type;

    @SerializedName("label")
    private String label;

    @SerializedName("productId")
    private Integer productId;

    public String getType() { return type; }
    public String getLabel() { return label; }
    public Integer getProductId() { return productId; }
}
