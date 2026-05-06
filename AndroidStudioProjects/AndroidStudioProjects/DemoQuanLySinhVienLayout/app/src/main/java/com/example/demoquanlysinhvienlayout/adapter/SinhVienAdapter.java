package com.example.demoquanlysinhvienlayout.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.demoquanlysinhvienlayout.R;
import com.example.demoquanlysinhvienlayout.model.SinhVien;
import com.example.demoquanlysinhvienlayout.helper.DateTimeHelper;
import java.util.List;

public class SinhVienAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private List<SinhVien> list;

    public SinhVienAdapter(Context context, int layout, List<SinhVien> list) {
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
        return 0; // Not using long ID
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout, null);
            holder = new ViewHolder();
            holder.txtId = convertView.findViewById(R.id.tvID);
            holder.txtHoTen = convertView.findViewById(R.id.tvHoten);
            holder.txtNgaySinh = convertView.findViewById(R.id.tvNgaysinh);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        SinhVien sv = list.get(position);
        holder.txtId.setText(sv.getId());
        holder.txtHoTen.setText(sv.getHoten());
        holder.txtNgaySinh.setText(DateTimeHelper.toString(sv.getNgaysinh()));

        return convertView;
    }

    private class ViewHolder {
        TextView txtId, txtHoTen, txtNgaySinh;
    }
}