package com.furniture.api.dto.response;

import com.furniture.api.model.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponse {

    private Integer addressId;
    private String recipientName;
    private String phone;
    private String addressLine;
    private String city;
    private String district;
    private String ward;
    private Boolean isDefault;
    private String fullAddress;

    public static AddressResponse fromEntity(Address addr) {
        return AddressResponse.builder()
                .addressId(addr.getAddressId())
                .recipientName(addr.getRecipientName())
                .phone(addr.getPhone())
                .addressLine(addr.getAddressLine())
                .city(addr.getCity())
                .district(addr.getDistrict())
                .ward(addr.getWard())
                .isDefault(addr.getIsDefault())
                .fullAddress(addr.getFullAddress())
                .build();
    }
}
