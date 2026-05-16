package com.furniture.app.data.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ChatRoomItem implements Serializable {

    @SerializedName("chatId")
    private String chatId;

    @SerializedName("userId")
    private Integer userId;

    @SerializedName("userName")
    private String userName;

    @SerializedName("lastMessage")
    private String lastMessage;

    @SerializedName("lastMessageTime")
    private String lastMessageTime;

    @SerializedName("unreadCount")
    private long unreadCount;

    public String getChatId() { return chatId; }
    public Integer getUserId() { return userId; }
    public String getUserName() { return userName; }
    public String getLastMessage() { return lastMessage; }
    public String getLastMessageTime() { return lastMessageTime; }
    public long getUnreadCount() { return unreadCount; }
}
