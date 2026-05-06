package com.example.demoquanlysinhvienlayout.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.example.demoquanlysinhvienlayout.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnDanhMucLopHoc).setOnClickListener(this);
        findViewById(R.id.btnQuanLySinhVien).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnDanhMucLopHoc) {
            startActivity(new Intent(this, DanhMucLopHocActivity.class));
        } else if (id == R.id.btnQuanLySinhVien) {
            startActivity(new Intent(this, QuanLySinhVienActivity.class));
        }
    }
}