package com.furniture.api.dto.request;

import lombok.Data;

@Data
public class ReviewRequest {
    private Integer productId;
    private Integer orderId;
    private Integer rating;
    private String comment;
    private String images;
}
