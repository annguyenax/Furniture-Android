package com.furniture.app.ui.customer.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.furniture.app.R;
import com.furniture.app.data.model.Address;
import com.furniture.app.data.model.ApiResponse;
import com.furniture.app.data.remote.RetrofitClient;
import com.furniture.app.data.remote.api.AddressApi;
import com.furniture.app.util.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressListActivity extends AppCompatActivity {

    public static final int REQUEST_ADD_EDIT = 1001;

    private RecyclerView rvAddresses;
    private TextView tvEmpty;
    private AddressAdapter adapter;
    private final List<Address> addresses = new ArrayList<>();
    private AddressApi addressApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_list);

        SessionManager sessionManager = new SessionManager(this);
        addressApi = RetrofitClient.getInstance(sessionManager.getToken()).create(AddressApi.class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        rvAddresses = findViewById(R.id.rv_addresses);
        tvEmpty = findViewById(R.id.tv_empty);

        adapter = new AddressAdapter(addresses,
                address -> openAddEdit(address),
                address -> confirmDelete(address),
                address -> setDefault(address));

        rvAddresses.setLayoutManager(new LinearLayoutManager(this));
        rvAddresses.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab_add);
        fab.setOnClickListener(v -> openAddEdit(null));

        loadAddresses();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_EDIT && resultCode == RESULT_OK) {
            loadAddresses();
        }
    }

    private void openAddEdit(Address address) {
        Intent intent = new Intent(this, AddAddressActivity.class);
        if (address != null) {
            intent.putExtra(AddAddressActivity.EXTRA_ADDRESS, address);
        }
        startActivityForResult(intent, REQUEST_ADD_EDIT);
    }

    private void loadAddresses() {
        addressApi.getAddresses().enqueue(new Callback<ApiResponse<List<Address>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Address>>> call,
                                   Response<ApiResponse<List<Address>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    addresses.clear();
                    addresses.addAll(response.body().getData());
                    adapter.notifyDataSetChanged();
                    tvEmpty.setVisibility(addresses.isEmpty() ? View.VISIBLE : View.GONE);
                    rvAddresses.setVisibility(addresses.isEmpty() ? View.GONE : View.VISIBLE);
                }
            }
            @Override public void onFailure(Call<ApiResponse<List<Address>>> call, Throwable t) {
                Toast.makeText(AddressListActivity.this, "Lỗi tải địa chỉ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmDelete(Address address) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa địa chỉ")
                .setMessage("Bạn có chắc muốn xóa địa chỉ này?")
                .setPositiveButton("Xóa", (d, w) -> deleteAddress(address))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteAddress(Address address) {
        addressApi.deleteAddress(address.getAddressId()).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(AddressListActivity.this, "Đã xóa địa chỉ", Toast.LENGTH_SHORT).show();
                    loadAddresses();
                } else {
                    String msg = response.body() != null ? response.body().getMessage() : "Không thể xóa địa chỉ này";
                    Toast.makeText(AddressListActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            }
            @Override public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Toast.makeText(AddressListActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setDefault(Address address) {
        addressApi.setDefault(address.getAddressId()).enqueue(new Callback<ApiResponse<Address>>() {
            @Override
            public void onResponse(Call<ApiResponse<Address>> call, Response<ApiResponse<Address>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddressListActivity.this, "Đã đặt địa chỉ mặc định", Toast.LENGTH_SHORT).show();
                    loadAddresses();
                }
            }
            @Override public void onFailure(Call<ApiResponse<Address>> call, Throwable t) {}
        });
    }

    // ─── Adapter ──────────────────────────────────────────────────────────────

    interface AddressAction { void run(Address address); }

    static class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.VH> {
        private final List<Address> list;
        private final AddressAction onEdit, onDelete, onSetDefault;

        AddressAdapter(List<Address> list, AddressAction onEdit, AddressAction onDelete, AddressAction onSetDefault) {
            this.list = list;
            this.onEdit = onEdit;
            this.onDelete = onDelete;
            this.onSetDefault = onSetDefault;
        }

        @NonNull @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int position) {
            Address a = list.get(position);
            h.tvName.setText(a.getRecipientName() != null ? a.getRecipientName() : "");
            h.tvPhone.setText(a.getPhone() != null ? "📞 " + a.getPhone() : "");

            String full = a.getFullAddress();
            if (full == null || full.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                if (a.getAddressLine() != null) sb.append(a.getAddressLine()).append(", ");
                if (a.getWard() != null) sb.append(a.getWard()).append(", ");
                if (a.getDistrict() != null) sb.append(a.getDistrict()).append(", ");
                if (a.getCity() != null) sb.append(a.getCity());
                full = sb.toString();
            }
            h.tvAddress.setText(full);

            boolean isDefault = Boolean.TRUE.equals(a.getIsDefault());
            h.tvDefaultBadge.setVisibility(isDefault ? View.VISIBLE : View.GONE);
            h.btnSetDefault.setVisibility(isDefault ? View.GONE : View.VISIBLE);

            h.btnEdit.setOnClickListener(v -> onEdit.run(a));
            h.btnDelete.setOnClickListener(v -> onDelete.run(a));
            h.btnSetDefault.setOnClickListener(v -> onSetDefault.run(a));
        }

        @Override public int getItemCount() { return list.size(); }

        static class VH extends RecyclerView.ViewHolder {
            TextView tvName, tvPhone, tvAddress, tvDefaultBadge;
            ImageButton btnEdit, btnDelete;
            MaterialButton btnSetDefault;

            VH(@NonNull View v) {
                super(v);
                tvName = v.findViewById(R.id.tv_recipient_name);
                tvPhone = v.findViewById(R.id.tv_phone);
                tvAddress = v.findViewById(R.id.tv_full_address);
                tvDefaultBadge = v.findViewById(R.id.tv_default_badge);
                btnEdit = v.findViewById(R.id.btn_edit);
                btnDelete = v.findViewById(R.id.btn_delete);
                btnSetDefault = v.findViewById(R.id.btn_set_default);
            }
        }
    }
}
