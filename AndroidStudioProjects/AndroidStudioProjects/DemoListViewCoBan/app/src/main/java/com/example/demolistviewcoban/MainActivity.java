package com.example.demolistviewcoban;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> arrayMonhoc;
    ArrayAdapter adapter;
    ListView lsView;
    Button btnThem, btnCapNhat;
    EditText edtText;
    int pos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        lsView=findViewById(R.id.listView);
        btnThem=findViewById(R.id.btnThem);
        btnCapNhat=findViewById(R.id.btnCapNhat);
        edtText=findViewById(R.id.edText);
        arrayMonhoc=new ArrayList<>();
        arrayMonhoc.add("Android");
        arrayMonhoc.add("Java");
        arrayMonhoc.add("PHP");
        arrayMonhoc.add("Hadoop");
        arrayMonhoc.add("Sap");
        arrayMonhoc.add("Python");
        arrayMonhoc.add("Ajax");
        arrayMonhoc.add("C++");
        arrayMonhoc.add("Ruby");
        arrayMonhoc.add("Rails");
        arrayMonhoc.add("JAV");
        adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, arrayMonhoc);
        lsView.setAdapter(adapter);
        btnCapNhat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pos != -1) {
                    arrayMonhoc.set(pos,edtText.getText().toString());
                    adapter.notifyDataSetChanged();
                }
                pos =-1;
                edtText.setText("");
            }
        });

        btnThem.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                arrayMonhoc.add(edtText.getText().toString());
                adapter.notifyDataSetChanged();
                edtText.setText("");
            }
        });
        lsView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                edtText.setText(arrayMonhoc.get(i));
                pos=i;

            }

        });
        lsView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

            Toast.makeText(MainActivity.this, "Xóa", Toast.LENGTH_SHORT).show();
            arrayMonhoc.remove(i);
            adapter.notifyDataSetChanged();
            return true;
            }

        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


}