package com.example.hong.myapplication;

import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.bumptech.glide.RequestBuilder;

import java.net.URL;

/**
 * Created by hong on 2018-01-30.
 */

public class item {
    private String friend_name;
    private String friend_state_message;
    private String friend_image;
    private String friend_id;

    public String getFriend_id() {
        return friend_id;
    }

    public void setFriend_id(String friend_id) {
        this.friend_id = friend_id;
    }
    //    public item(String friend_name, String friend_state_message, URL friend_image){
//        this.friend_name = friend_name;
//        this.friend_state_message = friend_state_message;
//        this.friend_image = friend_image;
//    }

    public String getFriend_name() {
        return friend_name;
    }

    public void setFriend_name(String friend_name) {
        this.friend_name = friend_name;
    }

    public String getFriend_state_message() {
        return friend_state_message;
    }

    public void setFriend_state_message(String friend_state_message) {
        this.friend_state_message = friend_state_message;
    }

    public String getFriend_image() {
        return friend_image;
    }

    public void setFriend_image(String friend_image) {
        this.friend_image = friend_image;
    }
}
