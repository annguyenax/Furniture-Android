package com.furniture.app.ui.customer.profile;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.furniture.app.R;
import com.furniture.app.util.SessionManager;
import com.google.android.material.button.MaterialButton;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etFirstName, etLastName, etPhone, etAddress;
    private MaterialButton btnSave;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        sessionManager = new SessionManager(this);

        initViews();
        setupToolbar();
        loadUserData();
        setupListeners();
    }

    private void initViews() {
        etFirstName = findViewById(R.id.et_first_name);
        etLastName = findViewById(R.id.et_last_name);
        etPhone = findViewById(R.id.et_phone);
        etAddress = findViewById(R.id.et_address);
        btnSave = findViewById(R.id.btn_save);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle("Chỉnh sửa hồ sơ");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void loadUserData() {
        etFirstName.setText(sessionManager.getFirstName());
        etLastName.setText(sessionManager.getLastName());
        etPhone.setText(sessionManager.getPhone());
        etAddress.setText(sessionManager.getUserAddress());
    }

    private void setupListeners() {
        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void saveProfile() {
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        if (firstName.isEmpty() || lastName.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập họ và tên", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save to session
        sessionManager.saveShippingInfo(firstName + " " + lastName, phone, address);

        Toast.makeText(this, "Đã lưu thông tin", Toast.LENGTH_SHORT).show();
        finish();
    }
}
