package com.furniture.api.controller;

import com.furniture.api.dto.response.AddressResponse;
import com.furniture.api.dto.response.ApiResponse;
import com.furniture.api.model.Address;
import com.furniture.api.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressRepository addressRepository;

    private Integer getCurrentUserId(Authentication auth) {
        return Integer.parseInt(auth.getName());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getAddresses(Authentication auth) {
        List<AddressResponse> list = addressRepository.findByUserId(getCurrentUserId(auth))
                .stream().map(AddressResponse::fromEntity).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @PostMapping
    @Transactional
    public ResponseEntity<ApiResponse<AddressResponse>> createAddress(
            @RequestBody AddressRequest request, Authentication auth) {

        if (request.getCity() == null || request.getCity().isBlank() ||
            request.getDistrict() == null || request.getDistrict().isBlank() ||
            request.getWard() == null || request.getWard().isBlank()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Vui lòng điền đầy đủ tỉnh/thành, quận/huyện, phường/xã"));
        }

        Integer userId = getCurrentUserId(auth);

        boolean firstAddress = addressRepository.countByUserId(userId) == 0;
        boolean setDefault = request.getIsDefault() != null && request.getIsDefault() || firstAddress;

        if (setDefault) {
            addressRepository.resetDefaultAddressForUser(userId);
        }

        Address address = Address.builder()
                .userId(userId)
                .recipientName(request.getRecipientName())
                .phone(request.getPhone())
                .addressLine(request.getAddressLine())
                .city(request.getCity().trim())
                .district(request.getDistrict().trim())
                .ward(request.getWard().trim())
                .isDefault(setDefault)
                .build();

        address = addressRepository.save(address);
        return ResponseEntity.ok(ApiResponse.success("Đã thêm địa chỉ", AddressResponse.fromEntity(address)));
    }

    @PutMapping("/{addressId}")
    @Transactional
    public ResponseEntity<ApiResponse<AddressResponse>> updateAddress(
            @PathVariable Integer addressId,
            @RequestBody AddressRequest request,
            Authentication auth) {

        Integer userId = getCurrentUserId(auth);
        Address address = addressRepository.findById(addressId)
                .filter(a -> a.getUserId().equals(userId))
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ"));

        if (Boolean.TRUE.equals(request.getIsDefault())) {
            addressRepository.resetDefaultAddressForUser(userId);
        }

        if (request.getRecipientName() != null) address.setRecipientName(request.getRecipientName());
        if (request.getPhone() != null) address.setPhone(request.getPhone());
        if (request.getAddressLine() != null) address.setAddressLine(request.getAddressLine());
        if (request.getCity() != null) address.setCity(request.getCity().trim());
        if (request.getDistrict() != null) address.setDistrict(request.getDistrict().trim());
        if (request.getWard() != null) address.setWard(request.getWard().trim());
        if (request.getIsDefault() != null) address.setIsDefault(request.getIsDefault());

        address = addressRepository.save(address);
        return ResponseEntity.ok(ApiResponse.success("Đã cập nhật địa chỉ", AddressResponse.fromEntity(address)));
    }

    @DeleteMapping("/{addressId}")
    @Transactional
    public ResponseEntity<ApiResponse<Void>> deleteAddress(
            @PathVariable Integer addressId, Authentication auth) {

        Integer userId = getCurrentUserId(auth);
        Address address = addressRepository.findById(addressId)
                .filter(a -> a.getUserId().equals(userId))
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ"));

        try {
            addressRepository.delete(address);
            addressRepository.flush();
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Không thể xóa địa chỉ đang được dùng trong đơn hàng"));
        }
        return ResponseEntity.ok(ApiResponse.success("Đã xóa địa chỉ", null));
    }

    @PutMapping("/{addressId}/default")
    @Transactional
    public ResponseEntity<ApiResponse<AddressResponse>> setDefault(
            @PathVariable Integer addressId, Authentication auth) {

        Integer userId = getCurrentUserId(auth);
        Address address = addressRepository.findById(addressId)
                .filter(a -> a.getUserId().equals(userId))
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ"));

        addressRepository.resetDefaultAddressForUser(userId);
        address.setIsDefault(true);
        address = addressRepository.save(address);
        return ResponseEntity.ok(ApiResponse.success("Đã đặt mặc định", AddressResponse.fromEntity(address)));
    }

    // ─── Request DTO ──────────────────────────────────────────────────────────

    public static class AddressRequest {
        private String recipientName;
        private String phone;
        private String addressLine;
        private String city;
        private String district;
        private String ward;
        private Boolean isDefault;

        public String getRecipientName() { return recipientName; }
        public String getPhone() { return phone; }
        public String getAddressLine() { return addressLine; }
        public String getCity() { return city; }
        public String getDistrict() { return district; }
        public String getWard() { return ward; }
        public Boolean getIsDefault() { return isDefault; }
    }
}
