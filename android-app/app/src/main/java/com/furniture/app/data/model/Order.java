package com.furniture.app.data.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class Order implements Serializable {
    private Integer orderId;
    private String orderCode;
    private Integer userId;
    private String recipientName;

    @SerializedName("recipientPhone")
    private String phone;

    @SerializedName("shippingAddress")
    private String address;

    private String note;
    private String paymentMethod;
    private String paymentStatus;

    @SerializedName("orderStatus")
    private String status;

    @SerializedName("returnStatus")
    private String returnStatus;

    private BigDecimal subtotal;
    private BigDecimal shippingFee;
    private BigDecimal totalAmount;
    private List<OrderItem> items;
    private String createdAt;

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(BigDecimal shippingFee) {
        this.shippingFee = shippingFee;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getReturnStatus() {
        return returnStatus;
    }

    public void setReturnStatus(String returnStatus) {
        this.returnStatus = returnStatus;
    }

    public String getStatusDisplay() {
        if ("APPROVED".equals(returnStatus)) return "Đã hoàn hàng";
        if ("PENDING".equals(returnStatus)) return "Chờ duyệt hoàn hàng";
        if (status == null) return "Không xác định";
        switch (status) {
            case "PENDING": return "Chờ xác nhận";
            case "PROCESSING": return "Đang xử lý";
            case "SHIPPED": return "Đang giao hàng";
            case "DELIVERED": return "Đã giao hàng";
            case "CANCELLED": return "Đã hủy";
            default: return status;
        }
    }
}
