package com.furniture.app.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.furniture.app.R;

import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.VH> {

    private final List<String> imageUrls;

    public BannerAdapter(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_banner, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Glide.with(h.image.getContext())
                .load(imageUrls.get(position))
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .placeholder(R.color.primary)
                .into(h.image);
    }

    @Override public int getItemCount() { return imageUrls.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView image;
        VH(View v) {
            super(v);
            image = v.findViewById(R.id.iv_banner);
        }
    }
}
