package com.wallpo.android.getset;

public class Chats {
    String message, reciver, sender, share;
    Long time;

    public Chats() {
    }

    public Chats(String reciver, String sender) {
        this.reciver = reciver;
        this.sender = sender;
    }

    public Chats(String message, String reciver, String sender, String share, Long time) {
        this.message = message;
        this.reciver = reciver;
        this.sender = sender;
        this.share = share;
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReciver() {
        return reciver;
    }

    public void setReciver(String reciver) {
        this.reciver = reciver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getShare() {
        return share;
    }

    public void setShare(String share) {
        this.share = share;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Chats{" +
                "message='" + message + '\'' +
                ", reciver='" + reciver + '\'' +
                ", sender='" + sender + '\'' +
                ", share='" + share + '\'' +
                ", time=" + time +
                '}';
    }
}
