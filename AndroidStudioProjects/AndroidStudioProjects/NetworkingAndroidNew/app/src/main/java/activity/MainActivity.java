package activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.networkingandroidnew.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import adapter.WeatherAdapter;
import model.WeatherForecastModel;

public class MainActivity extends AppCompatActivity {
    TextView txtCityName, txtTemperature, txtCityTemperature;
    EditText editTextTextPersonName;
    ImageView imgSearch, imgSeasonForecast;
    ListView lvWeatherForecast;
    List<WeatherForecastModel> weatherForecastModelList = new ArrayList<>();
    WeatherAdapter weatherAdapter;

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

        initViews();

        weatherAdapter = new WeatherAdapter(this, weatherForecastModelList);
        lvWeatherForecast.setAdapter(weatherAdapter);

        // Mặc định lấy dữ liệu của Saigon
        getWeatherData("Saigon");

        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = editTextTextPersonName.getText().toString().trim();
                if (city.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter city name", Toast.LENGTH_SHORT).show();
                } else {
                    getWeatherData(city);
                }
            }
        });
    }

    private void initViews() {
        txtCityName = findViewById(R.id.txtCityName);
        txtTemperature = findViewById(R.id.txtTemperature);
        txtCityTemperature = findViewById(R.id.txtCityTemperature);
        editTextTextPersonName = findViewById(R.id.editTextTextPersonName);
        imgSearch = findViewById(R.id.imgSearch);
        imgSeasonForecast = findViewById(R.id.imgSeasonForecast);
        lvWeatherForecast = findViewById(R.id.lvWeatherForecast);
    }

    private void getWeatherData(String cityName) {
        String url = "https://api.weatherapi.com/v1/forecast.json?key=fc8f4188ad3f48d8a10132707221212&q=" + cityName + "&days=1&aqi=no&alerts=no";
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        weatherForecastModelList.clear();
                        try {
                            // Parse thông tin thời tiết hiện tại
                            JSONObject location = response.getJSONObject("location");
                            String name = location.getString("name");
                            txtCityName.setText(name);

                            JSONObject current = response.getJSONObject("current");
                            String temp = current.getString("temp_c");
                            txtTemperature.setText(temp + "°C");
                            txtCityTemperature.setText(temp + "°C");

                            JSONObject condition = current.getJSONObject("condition");
                            String iconUrl = "https:" + condition.getString("icon");
                            Picasso.get().load(iconUrl).into(imgSeasonForecast);

                            // Parse dự báo theo giờ
                            JSONObject forecast = response.getJSONObject("forecast");
                            JSONArray forecastday = forecast.getJSONArray("forecastday");
                            JSONObject day0 = forecastday.getJSONObject(0);
                            JSONArray hourArray = day0.getJSONArray("hour");

                            for (int i = 0; i < hourArray.length(); i++) {
                                JSONObject hourObj = hourArray.getJSONObject(i);
                                String time = hourObj.getString("time");
                                String hourTemp = hourObj.getString("temp_c");
                                String windSpeed = hourObj.getString("wind_kph");
                                JSONObject hourCondition = hourObj.getJSONObject("condition");
                                String icon = hourCondition.getString("icon");

                                weatherForecastModelList.add(new WeatherForecastModel(time, hourTemp, "", icon, windSpeed));
                            }
                            weatherAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }
}
