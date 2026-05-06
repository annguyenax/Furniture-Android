package com.example.demolistviewnangcao;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView lvCity;
    ArrayList<City> cityArrayList = new ArrayList<>();
    CityAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        lvCity = findViewById(R.id.lvCity);
        cityArrayList.add(new City("New York",R.drawable.newyork,"https://vi.wikipedia.org/wiki/Th%C3%A0nh_ph%E1%BB%91_New_York"));
        cityArrayList.add(new City("Paris",R.drawable.paris,"https://vi.wikipedia.org/wiki/Paris"));
        cityArrayList.add(new City("Rome",R.drawable.rome,"https://vi.wikipedia.org/wiki/Roma"));
        adapter = new
                CityAdapter(this,R.layout.dong_thanh_pho,cityArrayList);
        lvCity.setAdapter(adapter);
        lvCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,
                                    int i, long l) {
                City city = cityArrayList.get(i);
                String url = city.getLinkWiki();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });
    }
}
