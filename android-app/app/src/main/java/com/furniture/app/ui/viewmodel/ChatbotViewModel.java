package com.furniture.app.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.furniture.app.data.model.ApiResponse;
import com.furniture.app.data.model.ChatbotMessage;
import com.furniture.app.data.repository.ChatbotRepository;

import java.util.ArrayList;
import java.util.List;

public class ChatbotViewModel extends ViewModel {

    private final MutableLiveData<List<ChatbotMessage>> messages = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();

    private ChatbotRepository repository;
    private String conversationId;

    public void init(String token) {
        if (repository == null) {
            repository = new ChatbotRepository(token);
        }
    }

    public LiveData<List<ChatbotMessage>> getMessages() {
        return messages;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void loadHistory() {
        if (repository == null) return;
        LiveData<ApiResponse<List<ChatbotMessage>>> source = repository.getHistory(conversationId);
        Observer<ApiResponse<List<ChatbotMessage>>> observer = new Observer<ApiResponse<List<ChatbotMessage>>>() {
            @Override
            public void onChanged(ApiResponse<List<ChatbotMessage>> response) {
                source.removeObserver(this);
                if (response != null && response.isSuccess() && response.getData() != null) {
                    messages.setValue(response.getData());
                    if (!response.getData().isEmpty()) {
                        conversationId = response.getData().get(0).getConversationId();
                    }
                }
            }
        };
        source.observeForever(observer);
    }

    public void sendMessage(String text) {
        if (repository == null || text == null || text.trim().isEmpty()) return;
        String trimmed = text.trim();
        appendMessage(new ChatbotMessage("USER", trimmed));
        loading.setValue(true);

        LiveData<ApiResponse<ChatbotMessage>> source = repository.sendMessage(trimmed, conversationId);
        Observer<ApiResponse<ChatbotMessage>> observer = new Observer<ApiResponse<ChatbotMessage>>() {
            @Override
            public void onChanged(ApiResponse<ChatbotMessage> response) {
                source.removeObserver(this);
                loading.setValue(false);
                if (response != null && response.isSuccess() && response.getData() != null) {
                    ChatbotMessage botMessage = response.getData();
                    conversationId = botMessage.getConversationId();
                    appendMessage(botMessage);
                } else {
                    error.setValue(response != null ? response.getMessage() : "Không thể gửi tin nhắn");
                    appendMessage(new ChatbotMessage("ASSISTANT", "Mình đang gặp lỗi kết nối. Bạn thử lại giúp mình nhé."));
                }
            }
        };
        source.observeForever(observer);
    }

    public void clearHistory() {
        if (repository == null) return;
        LiveData<ApiResponse<Void>> source = repository.deleteHistory(conversationId);
        Observer<ApiResponse<Void>> observer = new Observer<ApiResponse<Void>>() {
            @Override
            public void onChanged(ApiResponse<Void> response) {
                source.removeObserver(this);
                conversationId = null;
                messages.setValue(new ArrayList<>());
            }
        };
        source.observeForever(observer);
    }

    private void appendMessage(ChatbotMessage message) {
        List<ChatbotMessage> current = messages.getValue();
        if (current == null) current = new ArrayList<>();
        List<ChatbotMessage> updated = new ArrayList<>(current);
        updated.add(message);
        messages.setValue(updated);
    }
}
