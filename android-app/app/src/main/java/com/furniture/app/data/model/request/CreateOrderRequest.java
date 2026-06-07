package com.furniture.app.data.model.request;

import java.util.List;

public class CreateOrderRequest {
    private Integer addressId;
    private String paymentMethod;
    private String note;
    private Boolean fromCart;
    private List<OrderItemRequest> items;

    public CreateOrderRequest() {}

    public CreateOrderRequest(Integer addressId, String paymentMethod,
                              String note, Boolean fromCart, List<OrderItemRequest> items) {
        this.addressId = addressId;
        this.paymentMethod = paymentMethod;
        this.note = note;
        this.fromCart = fromCart;
        this.items = items;
    }

    public Integer getAddressId() { return addressId; }
    public void setAddressId(Integer addressId) { this.addressId = addressId; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public Boolean getFromCart() { return fromCart; }
    public void setFromCart(Boolean fromCart) { this.fromCart = fromCart; }

    public List<OrderItemRequest> getItems() { return items; }
    public void setItems(List<OrderItemRequest> items) { this.items = items; }

    public static class OrderItemRequest {
        private Integer productId;
        private Integer variantId;
        private Integer quantity;

        public OrderItemRequest() {}

        public OrderItemRequest(Integer productId, Integer variantId, Integer quantity) {
            this.productId = productId;
            this.variantId = variantId;
            this.quantity = quantity;
        }

        public Integer getProductId() { return productId; }
        public void setProductId(Integer productId) { this.productId = productId; }

        public Integer getVariantId() { return variantId; }
        public void setVariantId(Integer variantId) { this.variantId = variantId; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }
}
