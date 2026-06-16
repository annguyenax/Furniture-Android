package com.furniture.app.ui.customer.aichat;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.furniture.app.R;
import com.furniture.app.data.model.ChatbotSuggestedAction;
import com.furniture.app.ui.auth.LoginActivity;
import com.furniture.app.ui.customer.CustomerMainActivity;
import com.furniture.app.ui.customer.product.ProductDetailActivity;
import com.furniture.app.ui.viewmodel.ChatbotViewModel;
import com.furniture.app.util.SessionManager;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class AiChatBottomSheetFragment extends BottomSheetDialogFragment {

    private ChatbotViewModel viewModel;
    private AiChatAdapter adapter;
    private RecyclerView rvMessages;
    private EditText etMessage;
    private ImageButton btnSend;
    private TextView tvStatus;

    @NonNull
    @Override
    public Dialog onCreateDialog(@NonNull Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.setOnShowListener(d -> {
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) d;
            View bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.85f);
                ViewGroup.LayoutParams params = bottomSheet.getLayoutParams();
                params.height = height;
                bottomSheet.setLayoutParams(params);

                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_ai_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SessionManager sessionManager = new SessionManager(requireContext());
        String token = sessionManager.getToken();

        if (token == null || token.isEmpty()) {
            view.findViewById(R.id.layout_ai_guest).setVisibility(View.VISIBLE);
            view.findViewById(R.id.quick_chip_scroll).setVisibility(View.GONE);
            view.findViewById(R.id.layout_ai_input_bar).setVisibility(View.GONE);
            view.findViewById(R.id.btn_ai_login).setOnClickListener(v -> {
                dismiss();
                Intent intent = new Intent(requireContext(), LoginActivity.class);
                intent.putExtra(LoginActivity.EXTRA_RETURN_AFTER_LOGIN, true);
                startActivity(intent);
            });
            return;
        }

        rvMessages = view.findViewById(R.id.rv_ai_messages);
        etMessage = view.findViewById(R.id.et_ai_message);
        btnSend = view.findViewById(R.id.btn_ai_send);
        tvStatus = view.findViewById(R.id.tv_ai_status);
        ImageButton btnClear = view.findViewById(R.id.btn_clear_ai_chat);

        adapter = new AiChatAdapter(this::handleSuggestedAction);
        rvMessages.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvMessages.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(ChatbotViewModel.class);
        viewModel.init(token);

        View welcomeLayout = view.findViewById(R.id.layout_ai_welcome);
        viewModel.getMessages().observe(getViewLifecycleOwner(), messages -> {
            adapter.setMessages(messages);
            boolean hasMessages = messages != null && !messages.isEmpty();
            welcomeLayout.setVisibility(hasMessages ? View.GONE : View.VISIBLE);
            if (hasMessages) {
                rvMessages.scrollToPosition(messages.size() - 1);
            }
        });

        viewModel.getLoading().observe(getViewLifecycleOwner(), isLoading -> {
            boolean loading = Boolean.TRUE.equals(isLoading);
            tvStatus.setText(loading ? "Đang trả lời..." : "Hỏi nhanh về sản phẩm, phòng và giá");
            btnSend.setEnabled(!loading);
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        btnSend.setOnClickListener(v -> sendMessage(etMessage.getText().toString()));

        view.findViewById(R.id.chip_sofa).setOnClickListener(v -> sendMessage("Tìm sofa phòng khách"));
        view.findViewById(R.id.chip_under_5m).setOnClickListener(v -> sendMessage("Sản phẩm dưới 5 triệu"));
        view.findViewById(R.id.chip_bedroom).setOnClickListener(v -> sendMessage("Gợi ý nội thất cho phòng ngủ"));

        btnClear.setOnClickListener(v -> viewModel.clearHistory());

        viewModel.loadHistory();
    }

    private void sendMessage(String text) {
        if (text == null || text.trim().isEmpty()) return;
        viewModel.sendMessage(text);
        etMessage.setText("");
    }

    private void handleSuggestedAction(ChatbotSuggestedAction action) {
        if (action == null || action.getType() == null) return;

        if ("VIEW_CART".equals(action.getType())) {
            dismiss();
            if (getActivity() instanceof CustomerMainActivity) {
                ((CustomerMainActivity) getActivity()).navigateToTab(2);
            }
        } else if ("CHECKOUT".equals(action.getType())) {
            dismiss();
            if (getActivity() instanceof CustomerMainActivity) {
                ((CustomerMainActivity) getActivity()).navigateToTab(2);
                Toast.makeText(requireContext(), "Chọn sản phẩm trong giỏ rồi bấm thanh toán nhé", Toast.LENGTH_SHORT).show();
            }
        } else if ("VIEW_PRODUCT".equals(action.getType()) && action.getProductId() != null) {
            Intent intent = new Intent(requireContext(), ProductDetailActivity.class);
            intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT_ID, action.getProductId());
            startActivity(intent);
        }
    }
}
