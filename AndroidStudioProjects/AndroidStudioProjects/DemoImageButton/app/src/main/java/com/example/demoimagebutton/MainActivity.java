package com.example.demoimagebutton;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    Button btnFb, btnTw;
    ImageView imageView;

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
        btnFb = findViewById(R.id.btnFb);
        btnTw = findViewById(R.id.btnTw);
        imageView = findViewById(R.id.imageView);

        btnFb.setOnClickListener(v -> {
            imageView.setImageResource(R.drawable.facebook);
        });
        btnTw.setOnClickListener(v -> {
            imageView.setImageResource(R.drawable.twitter);
        });
    }
}
