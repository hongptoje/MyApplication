package com.example.hong.myapplication;

/**
 * Created by hong on 2018-02-13.
 */

public class Chatting_Room_Item {
    private String room_image;
    private String room_name;
    private String message_preview;
    private String room_friend_id;
    private String room_number;
    private String receive_time;

//    public Chatting_Room_Item(String room_image, String room_name, String message_preview) {
//        this.room_image = room_image;
//        this.room_name = room_name;
//        this.message_preview = message_preview;
//    }


    public String getReceive_time() {
        return receive_time;
    }

    public void setReceive_time(String receive_time) {
        this.receive_time = receive_time;
    }

    public String getRoom_friend_id() {
        return room_friend_id;
    }

    public void setRoom_friend_id(String room_friend_id) {
        this.room_friend_id = room_friend_id;
    }

    public String getRoom_number() {
        return room_number;
    }

    public void setRoom_number(String room_number) {
        this.room_number = room_number;
    }

    public String getRoom_image() {
        return room_image;
    }

    public void setRoom_image(String room_image) {
        this.room_image = room_image;
    }

    public String getRoom_name() {
        return room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }

    public String getMessage_preview() {
        return message_preview;
    }

    public void setMessage_preview(String message_preview) {
        this.message_preview = message_preview;
    }
}
