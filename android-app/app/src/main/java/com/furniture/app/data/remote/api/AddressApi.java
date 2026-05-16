package com.furniture.app.data.remote.api;

import com.furniture.app.data.model.Address;
import com.furniture.app.data.model.ApiResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface AddressApi {

    @GET("addresses")
    Call<ApiResponse<List<Address>>> getAddresses();

    @POST("addresses")
    Call<ApiResponse<Address>> createAddress(@Body AddressRequest request);

    @PUT("addresses/{id}")
    Call<ApiResponse<Address>> updateAddress(@Path("id") int id, @Body AddressRequest request);

    @DELETE("addresses/{id}")
    Call<ApiResponse<Void>> deleteAddress(@Path("id") int id);

    @PUT("addresses/{id}/default")
    Call<ApiResponse<Address>> setDefault(@Path("id") int id);

    class AddressRequest {
        private String recipientName;
        private String phone;
        private String addressLine;
        private String city;
        private String district;
        private String ward;
        private Boolean isDefault;

        public AddressRequest(String recipientName, String phone, String addressLine,
                              String city, String district, String ward, Boolean isDefault) {
            this.recipientName = recipientName;
            this.phone = phone;
            this.addressLine = addressLine;
            this.city = city;
            this.district = district;
            this.ward = ward;
            this.isDefault = isDefault;
        }

        public String getRecipientName() { return recipientName; }
        public String getPhone() { return phone; }
        public String getAddressLine() { return addressLine; }
        public String getCity() { return city; }
        public String getDistrict() { return district; }
        public String getWard() { return ward; }
        public Boolean getIsDefault() { return isDefault; }
    }
}
