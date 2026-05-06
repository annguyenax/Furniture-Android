package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.networkingandroidnew.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import model.WeatherForecastModel;

public class WeatherAdapter extends BaseAdapter {
    private Context context;
    private List<WeatherForecastModel> list;

    public WeatherAdapter(Context context, List<WeatherForecastModel> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.layout_weather_items, null);

        TextView txtTimeForecast = convertView.findViewById(R.id.txtTimeForecast);
        TextView txtTemperatureForecast = convertView.findViewById(R.id.txtTemperatureForecast);
        TextView txtWindForecast = convertView.findViewById(R.id.txtWindForecast);
        ImageView imgForecast = convertView.findViewById(R.id.imgForecast);

        WeatherForecastModel weather = list.get(position);
        txtTimeForecast.setText(weather.getTime());
        txtTemperatureForecast.setText(weather.getTemperature() + " °C");
        txtWindForecast.setText(weather.getWindSpeed() + " Km/h");

        Picasso.get().load("https:" + weather.getIcon()).into(imgForecast);

        return convertView;
    }
}
