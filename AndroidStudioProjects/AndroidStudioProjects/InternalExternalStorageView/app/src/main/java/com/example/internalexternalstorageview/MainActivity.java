package com.example.internalexternalstorageview;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Đăng ký sự kiện Click cho các button
        findViewById(R.id.btnwriteIS).setOnClickListener(this);
        findViewById(R.id.btnreadIS).setOnClickListener(this);
        findViewById(R.id.btnwriteFC).setOnClickListener(this);
        findViewById(R.id.btnreadFC).setOnClickListener(this);
        findViewById(R.id.btnwriteES).setOnClickListener(this);
        findViewById(R.id.btnreadES).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnreadIS) {
            String readDataIS = readIS("myfile.txt");
            Toast.makeText(this, "Nội dung file: " + readDataIS, Toast.LENGTH_SHORT).show();
        } else if (id == R.id.btnwriteIS) {
            writeIS("myfile.txt", "Xin chào các bạn!");
            Toast.makeText(this, "Đã ghi vào Internal Storage", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.btnreadFC) {
            String readDataFC = readFC("mycached.cache");
            Toast.makeText(this, "Data cache: " + readDataFC, Toast.LENGTH_SHORT).show();
        } else if (id == R.id.btnwriteFC) {
            writeFC("mycached.cache", "Dữ liệu lưu vào cache");
            Toast.makeText(this, "Đã ghi vào Cache File", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.btnwriteES) {
            writeES("mysdcard.txt", "Dữ liệu lưu vào thẻ nhớ ngoài");
            Toast.makeText(this, "Đã ghi vào External Storage", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.btnreadES) {
            String readDataEX = readES("mysdcard.txt");
            Toast.makeText(this, "Data sdcard: " + readDataEX, Toast.LENGTH_SHORT).show();
        }
    }

    // --- Internal Storage (Bộ nhớ trong) ---
    private void writeIS(String filename, String data) {
        try (FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE)) {
            fos.write(data.getBytes(StandardCharsets.UTF_8));
            fos.close();
            Toast.makeText(this, "Dữ liệu đã được ghi ", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String readIS(String filename) {
        StringBuilder sb = new StringBuilder();
        try (FileInputStream fis = openFileInput(filename);
             BufferedReader reader = new BufferedReader(new InputStreamReader(fis))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    // --- Cache File (Bộ nhớ đệm) ---
    private void writeFC(String filename, String data) {
        try {
            File file = new File(getCacheDir(), filename);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(data.getBytes(StandardCharsets.UTF_8));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String readFC(String filename) {
        StringBuilder sb = new StringBuilder();
        try {
            File file = new File(getCacheDir(), filename);
            try (FileInputStream fis = new FileInputStream(file);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(fis))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    // --- External Storage (Bộ nhớ ngoài - Private) ---
    private void writeES(String filename, String data) {
        try {
            File file = new File(getExternalFilesDir(null), filename);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(data.getBytes(StandardCharsets.UTF_8));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String readES(String filename) {
        StringBuilder sb = new StringBuilder();
        try {
            File file = new File(getExternalFilesDir(null), filename);
            try (FileInputStream fis = new FileInputStream(file);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(fis))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
