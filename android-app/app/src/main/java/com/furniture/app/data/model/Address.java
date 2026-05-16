package com.furniture.app.data.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Address implements Serializable {

    @SerializedName("addressId")
    private Integer addressId;

    @SerializedName("recipientName")
    private String recipientName;

    @SerializedName("phone")
    private String phone;

    @SerializedName("addressLine")
    private String addressLine;

    @SerializedName("city")
    private String city;

    @SerializedName("district")
    private String district;

    @SerializedName("ward")
    private String ward;

    @SerializedName("isDefault")
    private Boolean isDefault;

    @SerializedName("fullAddress")
    private String fullAddress;

    public Integer getAddressId() { return addressId; }
    public String getRecipientName() { return recipientName; }
    public String getPhone() { return phone; }
    public String getAddressLine() { return addressLine; }
    public String getCity() { return city; }
    public String getDistrict() { return district; }
    public String getWard() { return ward; }
    public Boolean getIsDefault() { return isDefault; }
    public String getFullAddress() { return fullAddress; }

    public void setAddressId(Integer addressId) { this.addressId = addressId; }
    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setAddressLine(String addressLine) { this.addressLine = addressLine; }
    public void setCity(String city) { this.city = city; }
    public void setDistrict(String district) { this.district = district; }
    public void setWard(String ward) { this.ward = ward; }
    public void setIsDefault(Boolean isDefault) { this.isDefault = isDefault; }
}
