package com.example.demogridviewnangcao;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.List;

public class HinhAnhAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private List<HinhAnh> hinhAnhList;
    public HinhAnhAdapter(Context context, int layout, List<HinhAnh>
            hinhAnhList) {

        this.context = context;
        this.layout = layout;
        this.hinhAnhList = hinhAnhList;
    }
    @Override
    public int getCount() {
        return hinhAnhList.size();
    }
    @Override
    public Object getItem(int i) {
        return null;
    }
    @Override
    public long getItemId(int i) {
        return 0;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view =inflater.inflate(layout,null);
        ImageView imageView = view.findViewById(R.id.imgHinhAnh);
        HinhAnh hinhAnh= hinhAnhList.get(i);
        imageView.setImageResource(hinhAnh.getHinh());
        return view;
    }
}
