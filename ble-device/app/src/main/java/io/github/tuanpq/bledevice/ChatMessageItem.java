package io.github.tuanpq.bledevice;

public class ChatMessageItem {

    private final Boolean mIsMe;
    private final String mMessage;

    public ChatMessageItem(Boolean isMe, String message) {
        this.mIsMe = isMe;
        this.mMessage = message;
    }

    public Boolean isMe() {
        return mIsMe;
    }

    public String getMessage() {
        return mMessage;
    }

}