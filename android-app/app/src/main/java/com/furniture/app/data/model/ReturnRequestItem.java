package com.furniture.app.data.model;

import com.google.gson.annotations.SerializedName;

public class ReturnRequestItem {

    @SerializedName("returnId") private Integer returnId;
    @SerializedName("orderId") private Integer orderId;
    @SerializedName("orderCode") private String orderCode;
    @SerializedName("orderItemId") private Integer orderItemId;
    @SerializedName("userId") private Integer userId;
    @SerializedName("userName") private String userName;
    @SerializedName("userEmail") private String userEmail;
    @SerializedName("productName") private String productName;
    @SerializedName("reason") private String reason;
    @SerializedName("evidenceUrl") private String evidenceUrl;
    @SerializedName("evidenceType") private String evidenceType;
    @SerializedName("status") private String status;
    @SerializedName("adminNote") private String adminNote;
    @SerializedName("createdAt") private String createdAt;
    @SerializedName("updatedAt") private String updatedAt;

    public Integer getReturnId() { return returnId; }
    public Integer getOrderId() { return orderId; }
    public String getOrderCode() { return orderCode; }
    public Integer getOrderItemId() { return orderItemId; }
    public Integer getUserId() { return userId; }
    public String getUserName() { return userName; }
    public String getUserEmail() { return userEmail; }
    public String getProductName() { return productName; }
    public String getReason() { return reason; }
    public String getEvidenceUrl() { return evidenceUrl; }
    public String getEvidenceType() { return evidenceType; }
    public String getStatus() { return status; }
    public String getAdminNote() { return adminNote; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }

    public String getStatusDisplay() {
        if ("APPROVED".equals(status)) return "Da xac nhan";
        if ("REJECTED".equals(status)) return "Da tu choi";
        return "Dang cho xu ly";
    }
}
