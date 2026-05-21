package com.furniture.app.ui.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.furniture.app.R;
import com.furniture.app.data.model.ReviewModel;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private List<ReviewModel> reviews;

    // Avatar background colors (cycle through)
    private static final int[] AVATAR_COLORS = {
            0xFF8B6914, 0xFF2E7D32, 0xFF1565C0, 0xFF6A1B9A,
            0xFFBF360C, 0xFF00838F, 0xFF558B2F, 0xFF37474F
    };

    public ReviewAdapter(List<ReviewModel> reviews) {
        this.reviews = reviews;
    }

    public void setReviews(List<ReviewModel> reviews) {
        this.reviews = reviews;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        ReviewModel r = reviews.get(position);
        Context ctx = h.itemView.getContext();

        // ── Avatar ───────────────────────────────────────────────────────────
        String name = r.getUserName() != null ? r.getUserName() : "?";
        String initial = name.length() > 0
                ? String.valueOf(Character.toUpperCase(name.charAt(0))) : "?";
        int color = AVATAR_COLORS[position % AVATAR_COLORS.length];
        h.tvAvatar.setText(initial);
        h.tvAvatar.setBackgroundColor(color);
        // Make circle via clipToOutline on a shape background
        h.tvAvatar.setBackground(makeCircleDrawable(color));

        // ── Name ─────────────────────────────────────────────────────────────
        h.tvName.setText(name);

        // ── Rating ───────────────────────────────────────────────────────────
        if (r.getRating() != null) h.rbRating.setRating(r.getRating());

        // ── Date ─────────────────────────────────────────────────────────────
        if (r.getCreatedAt() != null && r.getCreatedAt().length() >= 10) {
            h.tvDate.setText(r.getCreatedAt().substring(0, 10));
        } else {
            h.tvDate.setText("");
        }

        // ── Comment + expand ─────────────────────────────────────────────────
        String comment = r.getComment();
        if (comment != null && !comment.isEmpty()) {
            h.tvComment.setVisibility(View.VISIBLE);
            h.tvComment.setText(comment);
            h.tvComment.setMaxLines(3);

            // Check if text is long enough to need expand
            h.tvComment.post(() -> {
                if (h.tvComment.getLineCount() > 3) {
                    h.tvExpand.setVisibility(View.VISIBLE);
                    h.tvExpand.setText("Xem thêm");
                } else {
                    h.tvExpand.setVisibility(View.GONE);
                }
            });

            h.tvExpand.setOnClickListener(v -> {
                if (h.tvComment.getMaxLines() == 3) {
                    h.tvComment.setMaxLines(Integer.MAX_VALUE);
                    h.tvExpand.setText("Thu gọn");
                } else {
                    h.tvComment.setMaxLines(3);
                    h.tvExpand.setText("Xem thêm");
                }
            });
        } else {
            h.tvComment.setVisibility(View.GONE);
            h.tvExpand.setVisibility(View.GONE);
        }

        // ── Images ───────────────────────────────────────────────────────────
        String imagesStr = r.getImages();
        if (imagesStr != null && !imagesStr.trim().isEmpty()) {
            List<String> urls = Arrays.stream(imagesStr.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());

            if (!urls.isEmpty()) {
                h.hsvImages.setVisibility(View.VISIBLE);
                h.llImages.removeAllViews();

                int sizePx = dp(ctx, 80);
                int radiusPx = dp(ctx, 8);
                int marginPx = dp(ctx, 6);

                for (int i = 0; i < urls.size(); i++) {
                    final String url = urls.get(i);
                    final List<String> allUrls = urls;
                    final int clickedIndex = i;

                    ImageView iv = new ImageView(ctx);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(sizePx, sizePx);
                    lp.setMarginEnd(marginPx);
                    iv.setLayoutParams(lp);
                    iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    iv.setClipToOutline(true);

                    Glide.with(ctx)
                            .load(url)
                            .transform(new RoundedCorners(radiusPx))
                            .placeholder(R.drawable.placeholder_product)
                            .error(R.drawable.placeholder_product)
                            .into(iv);

                    iv.setOnClickListener(v -> showFullScreenImage(ctx, allUrls, clickedIndex));
                    h.llImages.addView(iv);
                }
            } else {
                h.hsvImages.setVisibility(View.GONE);
            }
        } else {
            h.hsvImages.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return reviews != null ? reviews.size() : 0;
    }

    // ── Full-screen image viewer ──────────────────────────────────────────────

    private void showFullScreenImage(Context ctx, List<String> urls, int startIndex) {
        Dialog dialog = new Dialog(ctx, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        }

        LinearLayout root = new LinearLayout(ctx);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.BLACK);

        // Close button
        TextView btnClose = new TextView(ctx);
        btnClose.setText("✕");
        btnClose.setTextColor(Color.WHITE);
        btnClose.setTextSize(20);
        btnClose.setPadding(dp(ctx, 16), dp(ctx, 16), dp(ctx, 16), dp(ctx, 8));
        btnClose.setOnClickListener(v -> dialog.dismiss());

        // Main image view
        ImageView ivFull = new ImageView(ctx);
        LinearLayout.LayoutParams ivParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f);
        ivFull.setLayoutParams(ivParams);
        ivFull.setScaleType(ImageView.ScaleType.FIT_CENTER);

        // Thumbnail strip (if multiple images)
        final int[] currentIndex = {startIndex};

        root.addView(btnClose);
        root.addView(ivFull);

        if (urls.size() > 1) {
            android.widget.HorizontalScrollView hsv = new android.widget.HorizontalScrollView(ctx);
            LinearLayout llStrip = new LinearLayout(ctx);
            llStrip.setOrientation(LinearLayout.HORIZONTAL);
            llStrip.setPadding(dp(ctx, 8), dp(ctx, 8), dp(ctx, 8), dp(ctx, 8));
            hsv.addView(llStrip);
            root.addView(hsv);

            int thumbSize = dp(ctx, 60);
            int margin = dp(ctx, 4);

            for (int i = 0; i < urls.size(); i++) {
                final int idx = i;
                ImageView thumb = new ImageView(ctx);
                LinearLayout.LayoutParams tp = new LinearLayout.LayoutParams(thumbSize, thumbSize);
                tp.setMarginEnd(margin);
                thumb.setLayoutParams(tp);
                thumb.setScaleType(ImageView.ScaleType.CENTER_CROP);
                thumb.setAlpha(i == startIndex ? 1f : 0.5f);

                Glide.with(ctx).load(urls.get(i))
                        .transform(new RoundedCorners(dp(ctx, 4)))
                        .into(thumb);

                thumb.setOnClickListener(v -> {
                    currentIndex[0] = idx;
                    Glide.with(ctx).load(urls.get(idx)).into(ivFull);
                    for (int j = 0; j < llStrip.getChildCount(); j++) {
                        llStrip.getChildAt(j).setAlpha(j == idx ? 1f : 0.5f);
                    }
                });
                llStrip.addView(thumb);
            }
        }

        Glide.with(ctx).load(urls.get(startIndex)).into(ivFull);

        dialog.setContentView(root);
        dialog.show();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private android.graphics.drawable.GradientDrawable makeCircleDrawable(int color) {
        android.graphics.drawable.GradientDrawable gd = new android.graphics.drawable.GradientDrawable();
        gd.setShape(android.graphics.drawable.GradientDrawable.OVAL);
        gd.setColor(color);
        return gd;
    }

    private static int dp(Context ctx, int dp) {
        return (int) (dp * ctx.getResources().getDisplayMetrics().density);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvAvatar, tvName, tvDate, tvComment, tvExpand;
        RatingBar rbRating;
        android.widget.HorizontalScrollView hsvImages;
        LinearLayout llImages;

        ViewHolder(View view) {
            super(view);
            tvAvatar   = view.findViewById(R.id.tv_avatar);
            tvName     = view.findViewById(R.id.tv_reviewer_name);
            rbRating   = view.findViewById(R.id.rb_review_rating);
            tvDate     = view.findViewById(R.id.tv_review_date);
            tvComment  = view.findViewById(R.id.tv_review_comment);
            tvExpand   = view.findViewById(R.id.tv_expand);
            hsvImages  = view.findViewById(R.id.hsv_images);
            llImages   = view.findViewById(R.id.ll_review_images);
        }
    }
}
