package com.furniture.app.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.furniture.app.R;
import com.furniture.app.data.model.ReviewModel;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private final List<ReviewModel> reviews;

    public ReviewAdapter(List<ReviewModel> reviews) {
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReviewModel r = reviews.get(position);

        holder.tvName.setText(r.getUserName() != null ? r.getUserName() : "Người dùng");

        if (r.getRating() != null) {
            holder.rbRating.setRating(r.getRating());
        }

        if (r.getComment() != null && !r.getComment().isEmpty()) {
            holder.tvComment.setText(r.getComment());
            holder.tvComment.setVisibility(View.VISIBLE);
        } else {
            holder.tvComment.setVisibility(View.GONE);
        }

        if (r.getCreatedAt() != null && r.getCreatedAt().length() >= 10) {
            holder.tvDate.setText(r.getCreatedAt().substring(0, 10));
        } else {
            holder.tvDate.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvComment, tvDate;
        RatingBar rbRating;

        ViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.tv_reviewer_name);
            rbRating = view.findViewById(R.id.rb_review_rating);
            tvComment = view.findViewById(R.id.tv_review_comment);
            tvDate = view.findViewById(R.id.tv_review_date);
        }
    }
}
