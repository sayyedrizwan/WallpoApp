package com.wallpo.android.getset;

public class ChatUsers {

    String chatuser, status;
    long time;

    public ChatUsers() {
    }

    public ChatUsers(String chatuser, String status, long time) {
        this.chatuser = chatuser;
        this.status = status;
        this.time = time;
    }

    public String getChatuser() {
        return chatuser;
    }

    public void setChatuser(String chatuser) {
        this.chatuser = chatuser;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
