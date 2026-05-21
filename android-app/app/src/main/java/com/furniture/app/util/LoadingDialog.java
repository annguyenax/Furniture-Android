package com.furniture.app.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

public class LoadingDialog {

    private final AlertDialog dialog;
    private final TextView tvMessage;

    public LoadingDialog(Context context, String message) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setPadding(64, 48, 64, 48);
        layout.setGravity(Gravity.CENTER_VERTICAL);

        ProgressBar bar = new ProgressBar(context);
        LinearLayout.LayoutParams barParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        barParams.setMarginEnd(40);
        bar.setLayoutParams(barParams);

        tvMessage = new TextView(context);
        tvMessage.setText(message);
        tvMessage.setTextSize(15f);
        tvMessage.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        layout.addView(bar);
        layout.addView(tvMessage);

        dialog = new AlertDialog.Builder(context)
                .setView(layout)
                .setCancelable(false)
                .create();
    }

    public static LoadingDialog show(Context context, String message) {
        LoadingDialog d = new LoadingDialog(context, message);
        d.show();
        return d;
    }

    public void setMessage(String message) {
        if (tvMessage != null) tvMessage.setText(message);
    }

    public void show() {
        if (!dialog.isShowing()) dialog.show();
    }

    public void dismiss() {
        if (dialog.isShowing()) dialog.dismiss();
    }
}
