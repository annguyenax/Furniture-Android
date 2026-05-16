package com.furniture.app.data.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ChatMessage implements Serializable {

    @SerializedName("messageId")
    private Integer messageId;

    @SerializedName("chatId")
    private String chatId;

    @SerializedName("senderId")
    private Integer senderId;

    @SerializedName("senderType")
    private String senderType;

    @SerializedName("message")
    private String message;

    @SerializedName("isRead")
    private Boolean isRead;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("mine")
    private boolean mine;

    public Integer getMessageId() { return messageId; }
    public String getChatId() { return chatId; }
    public Integer getSenderId() { return senderId; }
    public String getSenderType() { return senderType; }
    public String getMessage() { return message; }
    public Boolean getIsRead() { return isRead; }
    public String getCreatedAt() { return createdAt; }
    public boolean isMine() { return mine; }
}
