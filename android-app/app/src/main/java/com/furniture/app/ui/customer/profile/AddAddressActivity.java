package com.furniture.app.ui.customer.profile;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.furniture.app.R;
import com.furniture.app.data.model.Address;
import com.furniture.app.data.model.ApiResponse;
import com.furniture.app.data.model.Province;
import com.furniture.app.data.remote.RetrofitClient;
import com.furniture.app.data.remote.api.AddressApi;
import com.furniture.app.util.ProvinceService;
import com.furniture.app.util.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddAddressActivity extends AppCompatActivity {

    public static final String EXTRA_ADDRESS = "address";

    private TextInputEditText etRecipientName, etPhone, etAddressLine;
    private AutoCompleteTextView acvProvince, acvDistrict, acvWard;
    private MaterialCheckBox cbDefault;
    private AddressApi addressApi;
    private Address editingAddress;

    private Province selectedProvince;
    private Province.District selectedDistrict;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        SessionManager sessionManager = new SessionManager(this);
        addressApi = RetrofitClient.getInstance(sessionManager.getToken()).create(AddressApi.class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        etRecipientName = findViewById(R.id.et_recipient_name);
        etPhone         = findViewById(R.id.et_phone);
        etAddressLine   = findViewById(R.id.et_address_line);
        acvProvince     = findViewById(R.id.acv_province);
        acvDistrict     = findViewById(R.id.acv_district);
        acvWard         = findViewById(R.id.acv_ward);
        cbDefault       = findViewById(R.id.cb_default);

        editingAddress = (Address) getIntent().getSerializableExtra(EXTRA_ADDRESS);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(editingAddress != null ? "Sửa địa chỉ" : "Thêm địa chỉ mới");
        }

        if (editingAddress == null) {
            String userName = sessionManager.getUserName();
            if (userName != null) etRecipientName.setText(userName);
        }

        loadProvinces();
        findViewById(R.id.btn_save).setOnClickListener(v -> saveAddress());
    }

    // ── Province ─────────────────────────────────────────────────────────────

    private void loadProvinces() {
        ProvinceService.getInstance().getProvinces(new ProvinceService.ResultCallback<List<Province>>() {
            @Override
            public void onSuccess(List<Province> provinces) {
                ArrayAdapter<Province> adapter = new ArrayAdapter<>(
                        AddAddressActivity.this,
                        android.R.layout.simple_dropdown_item_1line,
                        provinces);
                acvProvince.setAdapter(adapter);

                if (editingAddress != null) prefill(editingAddress, provinces);

                acvProvince.setOnItemClickListener((parent, view, position, id) -> {
                    selectedProvince = (Province) parent.getItemAtPosition(position);
                    selectedDistrict = null;
                    acvDistrict.setText("", false);
                    acvDistrict.setEnabled(false);
                    acvWard.setText("", false);
                    acvWard.setEnabled(false);
                    loadDistricts(selectedProvince.getId(), null, null);
                });
            }

            @Override
            public void onError(String message) {
                Toast.makeText(AddAddressActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ── District ─────────────────────────────────────────────────────────────

    private void loadDistricts(String provinceId, String preselectDistrict, String preselectWard) {
        ProvinceService.getInstance().getDistricts(provinceId,
                new ProvinceService.ResultCallback<List<Province.District>>() {
                    @Override
                    public void onSuccess(List<Province.District> districts) {
                        ArrayAdapter<Province.District> adapter = new ArrayAdapter<>(
                                AddAddressActivity.this,
                                android.R.layout.simple_dropdown_item_1line,
                                districts);
                        acvDistrict.setAdapter(adapter);
                        acvDistrict.setEnabled(true);

                        acvDistrict.setOnItemClickListener((parent, view, position, id) -> {
                            selectedDistrict = (Province.District) parent.getItemAtPosition(position);
                            acvWard.setText("", false);
                            acvWard.setEnabled(false);
                            loadWards(selectedDistrict.getId(), null);
                        });

                        if (preselectDistrict != null) {
                            acvDistrict.setText(preselectDistrict, false);
                            for (Province.District d : districts) {
                                if (d.getFullName().equals(preselectDistrict)) {
                                    selectedDistrict = d;
                                    loadWards(d.getId(), preselectWard);
                                    break;
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(String message) {
                        Toast.makeText(AddAddressActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // ── Ward ─────────────────────────────────────────────────────────────────

    private void loadWards(String districtId, String preselectWard) {
        ProvinceService.getInstance().getWards(districtId,
                new ProvinceService.ResultCallback<List<Province.Ward>>() {
                    @Override
                    public void onSuccess(List<Province.Ward> wards) {
                        ArrayAdapter<Province.Ward> adapter = new ArrayAdapter<>(
                                AddAddressActivity.this,
                                android.R.layout.simple_dropdown_item_1line,
                                wards);
                        acvWard.setAdapter(adapter);
                        acvWard.setEnabled(true);

                        if (preselectWard != null) {
                            acvWard.setText(preselectWard, false);
                        }
                    }

                    @Override
                    public void onError(String message) {
                        Toast.makeText(AddAddressActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // ── Prefill (edit mode) ───────────────────────────────────────────────────

    private void prefill(Address a, List<Province> provinces) {
        if (a.getRecipientName() != null) etRecipientName.setText(a.getRecipientName());
        if (a.getPhone() != null)        etPhone.setText(a.getPhone());
        if (a.getAddressLine() != null)  etAddressLine.setText(a.getAddressLine());
        cbDefault.setChecked(Boolean.TRUE.equals(a.getIsDefault()));

        if (a.getCity() == null) return;
        acvProvince.setText(a.getCity(), false);
        for (Province p : provinces) {
            if (p.getFullName().equals(a.getCity())) {
                selectedProvince = p;
                loadDistricts(p.getId(), a.getDistrict(), a.getWard());
                break;
            }
        }
    }

    // ── Save ─────────────────────────────────────────────────────────────────

    private void saveAddress() {
        String name     = getText(etRecipientName);
        String phone    = getText(etPhone);
        String line     = getText(etAddressLine);
        String city     = acvProvince.getText().toString().trim();
        String district = acvDistrict.getText().toString().trim();
        String ward     = acvWard.getText().toString().trim();

        if (name.isEmpty()) {
            etRecipientName.setError("Vui lòng nhập tên người nhận");
            etRecipientName.requestFocus();
            return;
        }
        if (phone.isEmpty() || !phone.matches("^(0|\\+84)[0-9]{9,10}$")) {
            etPhone.setError("Số điện thoại không hợp lệ");
            etPhone.requestFocus();
            return;
        }
        if (line.length() < 5) {
            etAddressLine.setError("Vui lòng nhập địa chỉ cụ thể");
            etAddressLine.requestFocus();
            return;
        }
        if (city.isEmpty() || district.isEmpty() || ward.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn đầy đủ Tỉnh/TP, Quận/Huyện, Phường/Xã",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        AddressApi.AddressRequest request =
                new AddressApi.AddressRequest(name, phone, line, city, district, ward, cbDefault.isChecked());

        if (editingAddress != null) {
            addressApi.updateAddress(editingAddress.getAddressId(), request)
                    .enqueue(saveCallback());
        } else {
            addressApi.createAddress(request).enqueue(saveCallback());
        }
    }

    private Callback<ApiResponse<Address>> saveCallback() {
        return new Callback<ApiResponse<Address>>() {
            @Override
            public void onResponse(Call<ApiResponse<Address>> call, Response<ApiResponse<Address>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(AddAddressActivity.this, "Đã lưu địa chỉ", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    String msg = response.body() != null ? response.body().getMessage() : "Lưu thất bại";
                    Toast.makeText(AddAddressActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onFailure(Call<ApiResponse<Address>> call, Throwable t) {
                Toast.makeText(AddAddressActivity.this, "Lỗi kết nối, thử lại", Toast.LENGTH_SHORT).show();
            }
        };
    }

    private String getText(TextInputEditText et) {
        return et.getText() != null ? et.getText().toString().trim() : "";
    }
}
