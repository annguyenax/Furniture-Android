package com.example.demolistviewnangcao;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CityAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private List<City> cityList;
    public CityAdapter(Context context, int layout, List<City> cityList) {
        this.context = context;
        this.layout = layout;
        this.cityList = cityList;
    }
    @Override
    public int getCount() {
        return cityList.size();
    }
    @Override
    public Object getItem(int i) {
        return cityList.get(i);
    }
    @Override
    public long getItemId(int i) {
        return i;
    }
    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup)
    {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate( layout,null);
        //anh xạ
        TextView txtTen = convertView.findViewById(R.id.txtTen);
        TextView txtlin= convertView.findViewById(R.id.txtlink);
        ImageView imgHinh = convertView.findViewById(R.id.imgHinh);
        City city = cityList.get(position);
        txtTen.setText(city.getNameCity());
        txtlin.setText(city.getLinkWiki());
        imgHinh.setImageResource(city.getHinh());
        return convertView;
    }
}
