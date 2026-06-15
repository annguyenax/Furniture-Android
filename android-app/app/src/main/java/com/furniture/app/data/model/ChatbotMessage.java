package com.furniture.app.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ChatbotMessage {

    @SerializedName("conversationId")
    private String conversationId;

    @SerializedName("role")
    private String role;

    @SerializedName("content")
    private String content;

    @SerializedName("products")
    private List<ChatbotProduct> products;

    @SerializedName("suggestedActions")
    private List<ChatbotSuggestedAction> suggestedActions;

    @SerializedName("createdAt")
    private String createdAt;

    public ChatbotMessage() {
    }

    public ChatbotMessage(String role, String content) {
        this.role = role;
        this.content = content;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<ChatbotProduct> getProducts() {
        return products;
    }

    public void setProducts(List<ChatbotProduct> products) {
        this.products = products;
    }

    public List<ChatbotSuggestedAction> getSuggestedActions() {
        return suggestedActions;
    }

    public void setSuggestedActions(List<ChatbotSuggestedAction> suggestedActions) {
        this.suggestedActions = suggestedActions;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isUser() {
        return "USER".equalsIgnoreCase(role);
    }

    public boolean isAssistant() {
        return "ASSISTANT".equalsIgnoreCase(role);
    }
}
