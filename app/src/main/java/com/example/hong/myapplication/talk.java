package com.example.hong.myapplication;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.icu.util.Output;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class talk extends Fragment {
    RecyclerView chatting_room_recycler_view;
    ArrayList<Chatting_Room_Item> chatting_room_list;
    RecyclerView.LayoutManager layoutManager;
    Chatting_Room_Adapter chatting_room_adapter;
    String my_id;
    RoomThread roomThread;
    Chatting_Room_Item chatting_room_item;
    Handler handler;
    String room_name, preview_message, room_image, room_image_url, room_friend_id, room_number, room_list_state_message;
    String TAG = "talk";
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ChildEventListener childEventListener;


    class RoomThread extends java.lang.Thread{
        OutputStream output;
        InputStream input;
        HttpURLConnection httpURLConnection;
        BufferedReader bufferedReader;
        String param;
        String server_get_info;
        String chatting_room_list_info;
        String room_receive_time;
        JSONArray jsonArray;
        JSONObject jsonObject;

        @Override
        public void run() {
            super.run();
                String serverURL = "http://ec2-52-78-13-66.ap-northeast-2.compute.amazonaws.com/chatting_room_list.php?";
                try {
                    Log.i("talk_seq", "1");
                    URL url = new URL(serverURL);
                    param = "my_id=" + my_id;
                    Log.i("param22222", "" + my_id);
                    Log.i("talk_seq", "2");
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setReadTimeout(5000);
                    httpURLConnection.setConnectTimeout(5000);
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.connect();
                    output = httpURLConnection.getOutputStream();
                    output.write(param.getBytes("UTF-8"));
                    output.flush();
                    output.close();
                    httpURLConnection.getResponseCode();
                    Log.i("talk_seq", "3");
                    input = null;
                    bufferedReader = null;
                    server_get_info = "";
                    input = httpURLConnection.getInputStream();
                    bufferedReader = new BufferedReader(new InputStreamReader(input), 8 * 1024);
                    StringBuffer stringBuffer = new StringBuffer();
                    Log.i("talk_seq", "4");
                    while ((server_get_info = bufferedReader.readLine()) != null) {
                        stringBuffer.append(server_get_info);
                    }
                    chatting_room_list_info = stringBuffer.toString();
                    Log.i("chatting_room_info", "" + chatting_room_list_info);
                chatting_room_list.clear();
//                chatting_room_recycler_view.setAdapter(chatting_room_adapter);

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            chatting_room_recycler_view.setAdapter(chatting_room_adapter);
                            try {
                                jsonArray = new JSONArray(chatting_room_list_info);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            jsonObject = null;
                            Log.i("talk_seq", "5");
                            if (jsonArray != null) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    try {
                                        jsonObject = jsonArray.getJSONObject(i);
                                        room_name = jsonObject.getString("name");
                                        room_image = jsonObject.getString("profile");
                                        preview_message = jsonObject.getString("recent_content");
                                        room_list_state_message = jsonObject.getString("state_message");
                                        room_friend_id = jsonObject.getString("id");
                                        room_number = jsonObject.getString("room_number");
                                        room_receive_time = jsonObject.getString("send_time");
                                        room_image_url = "http://ec2-52-78-13-66.ap-northeast-2.compute.amazonaws.com" + room_image;
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    Log.i("chatting_room_info", "name=" + room_name);
                                    Log.i("chatting_room_info", "room_image=" + room_image);
                                    Log.i("chatting_room_info", "state_message" + room_list_state_message);
//                        Log.i("chatting_room_info","preview_message="+preview_message);
                                    chatting_room_item = new Chatting_Room_Item();
                                    chatting_room_item.setRoom_name(room_name);
                                    Log.i("preview_message", "" + preview_message);
                                    if (preview_message.equals("null")) {
                                        chatting_room_item.setMessage_preview("");
                                        Log.i("preview_message", "null인경우");
                                    } else {
                                        chatting_room_item.setMessage_preview(preview_message);
                                        Log.i("preview_message", "null이아닌경우");
                                    }
                                    chatting_room_item.setRoom_image(room_image_url);
                                    chatting_room_item.setRoom_friend_id(room_friend_id);
                                    chatting_room_item.setRoom_number(room_number);
                                    chatting_room_item.setReceive_time(room_receive_time);
                                    chatting_room_list.add(chatting_room_item);

//                        handler.sendEmptyMessage(0);
                                }
                            }
                        }
                    }, 500);
                    Log.i("talk_seq", "6");
                    bufferedReader.close();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d(TAG,"httpConnection");
            }
    }
    @Override
    public void onCreate(@Nullable Bundle savedinstanceState) {
        super.onCreate(savedinstanceState);
        Log.i("talk_seq","talk");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedinstanceState){
       RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.fragment_talk, container, false);
       chatting_room_recycler_view = (RecyclerView)layout.findViewById(R.id.chatting_room_recycler_view);

        SharedPreferences id = getContext().getSharedPreferences("auto", Activity.MODE_PRIVATE);
        my_id = id.getString("input_id","");
        Log.i("my_id", ""+ my_id);
        handler = new Handler();
        roomThread = new RoomThread();
        roomThread.start();
       chatting_room_list = new ArrayList<>();
       chatting_room_adapter = new Chatting_Room_Adapter(getContext(), chatting_room_list);
       layoutManager = new LinearLayoutManager(getContext());
       chatting_room_recycler_view.setLayoutManager(layoutManager);
       chatting_room_adapter.notifyDataSetChanged();
        return  layout;
    }

    public void refresh(){
        android.support.v4.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.detach(this).attach(this).commitAllowingStateLoss();
//        FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
//        transaction.detach(this).attach(this).commitAllowingStateLoss();
    }


}
