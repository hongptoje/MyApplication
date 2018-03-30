package com.example.hong.myapplication;

/**
 * Created by hong on 2018-02-04.
 */

public class chatting_item {
    private String chatting_message;
    private String chatting_name;
    private String chatting_image;
    private int position_type;
    private int message_again;

    public chatting_item(String chatting_message, String chatting_name, String chatting_image, int position_type, int message_again) {
        this.chatting_message = chatting_message;
        this.chatting_name = chatting_name;
        this.chatting_image = chatting_image;
        this.position_type = position_type;
        this.message_again = message_again;
    }

    public String getChatting_message() {
        return chatting_message;
    }

    public void setChatting_message(String chatting_message) {
        this.chatting_message = chatting_message;
    }

    public String getChatting_name() {
        return chatting_name;
    }

    public void setChatting_name(String chatting_name) {
        this.chatting_name = chatting_name;
    }

    public String getChatting_image() {
        return chatting_image;
    }

    public void setChatting_image(String chatting_image) {
        this.chatting_image = chatting_image;
    }
    public int getPosition_type() {
        return position_type;
    }

    public void setPosition_type(int position_type) {
        this.position_type = position_type;
    }

    public int getMessage_again() {
        return message_again;
    }

    public void setMessage_again(int message_again) {
        this.message_again = message_again;
    }
}
