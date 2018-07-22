package com.ksapps.shelfshare;

import java.util.Date;

/**
 * Created by Kshitij on 15-03-2018.
 */

public class ChatMessage {
    private String messageText;
    private String messageSender;
    private String messageReceiver;
    private long messageTime;

    public ChatMessage(String messageText, String messageSender, String messageReceiver) {
        this.messageText = messageText;
        this.messageSender = messageSender;
        this.messageReceiver = messageReceiver;


        // Initialize to current time
        messageTime = new Date().getTime();
    }

    public ChatMessage(){

    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageSender() {
        return messageSender;
    }

    public void setMessageSender(String messageSender) {
        this.messageSender = messageSender;
    }

    public String getMessageReceiver() {return messageReceiver;}

    public void setMessageReceiver(String messageReceiver) {this.messageReceiver = messageReceiver;}

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }
}
