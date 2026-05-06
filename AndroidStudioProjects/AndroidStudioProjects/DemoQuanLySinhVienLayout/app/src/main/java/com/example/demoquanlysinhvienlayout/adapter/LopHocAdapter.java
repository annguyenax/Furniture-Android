package com.example.demoquanlysinhvienlayout.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.demoquanlysinhvienlayout.R;
import com.example.demoquanlysinhvienlayout.model.LopHoc;
import java.util.List;

public class LopHocAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private List<LopHoc> list;

    public LopHocAdapter(Context context, int layout, List<LopHoc> list) {
        this.context = context;
        this.layout = layout;
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
        return list.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout, null);
            holder = new ViewHolder();
            holder.txtId = convertView.findViewById(R.id.txtIdLopHoc);
            holder.txtTen = convertView.findViewById(R.id.txtTenLopHoc);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        LopHoc lopHoc = list.get(position);
        holder.txtId.setText(String.valueOf(lopHoc.getId()));
        holder.txtTen.setText(lopHoc.getTenlophoc());

        return convertView;
    }

    private class ViewHolder {
        TextView txtId, txtTen;
    }
}