package com.example.demoquanlysinhvienlayout.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.demoquanlysinhvienlayout.R;
import com.example.demoquanlysinhvienlayout.adapter.SinhVienAdapter;
import com.example.demoquanlysinhvienlayout.dao.LopHocDAO;
import com.example.demoquanlysinhvienlayout.dao.SinhVienDAO;
import com.example.demoquanlysinhvienlayout.helper.DateTimeHelper;
import com.example.demoquanlysinhvienlayout.model.LopHoc;
import com.example.demoquanlysinhvienlayout.model.SinhVien;
import java.util.Date;
import java.util.List;

public class QuanLySinhVienActivity extends AppCompatActivity {
    private Spinner spLopHoc;
    private EditText edtMaSV, edtHoten, edtNgaySinh;
    private Button btnLuu, btnThoat;
    private ListView lvSinhVien;
    
    private LopHocDAO lopHocDAO;
    private SinhVienDAO sinhVienDAO;
    
    private List<LopHoc> listLopHoc;
    private List<SinhVien> listSinhVien;
    private SinhVienAdapter adapter;
    private int selectedLopId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quan_ly_sinhvien);

        initView();
        lopHocDAO = new LopHocDAO(this);
        sinhVienDAO = new SinhVienDAO(this);
        
        loadSpinner();

        spLopHoc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedLopId = listLopHoc.get(position).getId();
                loadDataByLop(selectedLopId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedLopId = -1;
            }
        });

        btnLuu.setOnClickListener(v -> {
            saveSinhVien();
        });

        btnThoat.setOnClickListener(v -> finish());
    }

    private void initView() {
        spLopHoc = findViewById(R.id.spLopHoc);
        edtMaSV = findViewById(R.id.edtMaSV);
        edtHoten = findViewById(R.id.edtHotenSV);
        edtNgaySinh = findViewById(R.id.edtNgaySinhSV);
        btnLuu = findViewById(R.id.btnLuuSinhVien);
        btnThoat = findViewById(R.id.btnThoatSinhVien);
        lvSinhVien = findViewById(R.id.lvDanhsachSinhvien);
    }

    private void loadSpinner() {
        listLopHoc = lopHocDAO.getAll();
        if (listLopHoc.isEmpty()) {
            Toast.makeText(this, "Vui lòng thêm lớp học trước", Toast.LENGTH_LONG).show();
            return;
        }
        ArrayAdapter<LopHoc> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listLopHoc);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spLopHoc.setAdapter(spinnerAdapter);
    }

    private void loadDataByLop(int lopId) {
        listSinhVien = sinhVienDAO.getByLop(lopId);
        adapter = new SinhVienAdapter(this, R.layout.layout_sinhvien_items, listSinhVien);
        lvSinhVien.setAdapter(adapter);
    }

    private void saveSinhVien() {
        if (selectedLopId == -1) {
            Toast.makeText(this, "Chưa chọn lớp học", Toast.LENGTH_SHORT).show();
            return;
        }

        String maSV = edtMaSV.getText().toString().trim();
        String hoTen = edtHoten.getText().toString().trim();
        String ngaySinhStr = edtNgaySinh.getText().toString().trim();

        if (maSV.isEmpty() || hoTen.isEmpty() || ngaySinhStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Date ngaySinh = DateTimeHelper.toDate(ngaySinhStr);
            SinhVien sv = new SinhVien(maSV, hoTen, ngaySinh, selectedLopId);
            
            if (sinhVienDAO.insert(sv) > 0) {
                Toast.makeText(this, "Thêm sinh viên thành công", Toast.LENGTH_SHORT).show();
                edtMaSV.setText("");
                edtHoten.setText("");
                edtNgaySinh.setText("");
                loadDataByLop(selectedLopId);
            } else {
                Toast.makeText(this, "Thêm sinh viên thất bại (Mã SV có thể bị trùng)", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Định dạng ngày sinh sai (dd/MM/yyyy)", Toast.LENGTH_SHORT).show();
        }
    }
}