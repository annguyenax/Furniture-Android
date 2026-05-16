package com.furniture.app.data.model;

import com.google.gson.annotations.SerializedName;

public class ReviewModel {

    @SerializedName("reviewId") private Integer reviewId;
    @SerializedName("productId") private Integer productId;
    @SerializedName("userId") private Integer userId;
    @SerializedName("userName") private String userName;
    @SerializedName("rating") private Integer rating;
    @SerializedName("comment") private String comment;
    @SerializedName("images") private String images;
    @SerializedName("isVerified") private Boolean isVerified;
    @SerializedName("createdAt") private String createdAt;

    public Integer getReviewId() { return reviewId; }
    public Integer getProductId() { return productId; }
    public Integer getUserId() { return userId; }
    public String getUserName() { return userName; }
    public Integer getRating() { return rating; }
    public String getComment() { return comment; }
    public String getImages() { return images; }
    public Boolean getIsVerified() { return isVerified; }
    public String getCreatedAt() { return createdAt; }
}
