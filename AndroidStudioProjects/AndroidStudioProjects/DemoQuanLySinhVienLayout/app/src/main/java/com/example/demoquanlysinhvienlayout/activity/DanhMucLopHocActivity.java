package com.example.demoquanlysinhvienlayout.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.demoquanlysinhvienlayout.R;
import com.example.demoquanlysinhvienlayout.adapter.LopHocAdapter;
import com.example.demoquanlysinhvienlayout.dao.LopHocDAO;
import com.example.demoquanlysinhvienlayout.model.LopHoc;
import java.util.List;

public class DanhMucLopHocActivity extends AppCompatActivity {
    private EditText edtTenLop;
    private Button btnLuu, btnThoat;
    private ListView lvLopHoc;
    private LopHocDAO lopHocDAO;
    private List<LopHoc> listLopHoc;
    private LopHocAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danh_muc_lop_hoc);

        initView();
        lopHocDAO = new LopHocDAO(this);
        loadData();

        btnLuu.setOnClickListener(v -> {
            String tenLop = edtTenLop.getText().toString().trim();
            if (tenLop.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên lớp", Toast.LENGTH_SHORT).show();
                return;
            }
            LopHoc lopHoc = new LopHoc(tenLop);
            if (lopHocDAO.insert(lopHoc) > 0) {
                Toast.makeText(this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                edtTenLop.setText("");
                loadData();
            } else {
                Toast.makeText(this, "Thêm thất bại", Toast.LENGTH_SHORT).show();
            }
        });

        btnThoat.setOnClickListener(v -> finish());

        lvLopHoc.setOnItemLongClickListener((parent, view, position, id) -> {
            LopHoc lop = listLopHoc.get(position);
            showDeleteDialog(lop);
            return true;
        });
    }

    private void initView() {
        edtTenLop = findViewById(R.id.edtTenLopHoc);
        btnLuu = findViewById(R.id.btnLuuLopHoc);
        btnThoat = findViewById(R.id.btnThoatLopHoc);
        lvLopHoc = findViewById(R.id.lvdanhsachlophoc);
    }

    private void loadData() {
        listLopHoc = lopHocDAO.getAll();
        adapter = new LopHocAdapter(this, R.layout.layout_danh_muc_lop_hoc_items, listLopHoc);
        lvLopHoc.setAdapter(adapter);
    }

    private void showDeleteDialog(LopHoc lopHoc) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xóa lớp học");
        builder.setMessage("Bạn có chắc chắn muốn xóa lớp " + lopHoc.getTenlophoc() + "?");
        builder.setPositiveButton("Xóa", (dialog, which) -> {
            if (lopHocDAO.delete(lopHoc.getId()) > 0) {
                Toast.makeText(this, "Xóa thành công", Toast.LENGTH_SHORT).show();
                loadData();
            } else {
                Toast.makeText(this, "Xóa thất bại", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}