package com.example.hong.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class friend extends Fragment {
    RecyclerView recyclerView;
    ArrayList<item> friend_list;
    RecyclerView.LayoutManager layoutManager;
    adapter adapter;
    ImageView friend_add_button;
    String get_friend_name, get_friend_image, get_friend_state_message, id, param, profile_info, get_friend_id;
    Thread thread;
    Boolean stop = false;
    HttpURLConnection httpURLConnection;
    InputStream inputStream;
    OutputStream outputStream;
    BufferedReader bufferedReader;
    Handler handler;
    String friend_image_url;
    JSONArray jsonArray;
    JSONObject jsonObject;
    item item;
    String TAG = "friend";


    class Thread extends java.lang.Thread{

        public void run() {
                    try {

                        Log.i("start_thread", "start_thread");
                        Log.i("stop= ", ""+stop);
                        String serverURL = "http://ec2-52-78-13-66.ap-northeast-2.compute.amazonaws.com/friend_list.php?";
                        SharedPreferences auto = getContext().getSharedPreferences("auto", Activity.MODE_PRIVATE);
                        id = auto.getString("input_id", "");
                        param = "id=" + id;
                        Log.i("id= ",""+id);
                        URL url = new URL(serverURL);
                        //http connection 구하기
                        httpURLConnection = (HttpURLConnection) url.openConnection();
                        //읽기 타임 아웃 설정
                        httpURLConnection.setReadTimeout(5000);
                        //연결 타임 아웃 설정
                        httpURLConnection.setConnectTimeout(5000);
                        //요청 방식 설정
                        httpURLConnection.setRequestMethod("POST");
                        //읽기모드를 지정
                        httpURLConnection.setDoInput(true);
                        //쓰기모드를 지정(POST로 보내는경우에 설정하면됨 default 값 false)
                        httpURLConnection.setDoOutput(true);
                        httpURLConnection.connect();
//                            //데이터 작성
                        outputStream = httpURLConnection.getOutputStream();
                        outputStream.write(param.getBytes("UTF-8"));
                        outputStream.flush();
                        outputStream.close();

                        httpURLConnection.getResponseCode();

                        inputStream = null;
                        bufferedReader = null;
                        profile_info = "";
                        inputStream = httpURLConnection.getInputStream();
                        bufferedReader = new BufferedReader(new InputStreamReader(inputStream), 8 * 1024);
                        Log.d("seq", "1");
                        StringBuffer stringBuffer = new StringBuffer();
                        while ((profile_info = bufferedReader.readLine()) != null){
                            stringBuffer.append(profile_info);
                        }

                        final String test_message = stringBuffer.toString();
                        Log.i("test_message", ""+test_message);
                        Log.d("seq", "2");


//                        friend_list.add(new item("aa", "asdf", friend_image_url));
                        friend_list.clear();

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                recyclerView.setAdapter(adapter);

                                try {
                                    jsonArray = new JSONArray(test_message);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                jsonObject = null;
                                if (jsonArray != null) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        try {
                                            jsonObject = jsonArray.getJSONObject(i);
                                            get_friend_name = jsonObject.getString("name");
                                            get_friend_state_message = jsonObject.getString("state_message");
                                            get_friend_image = jsonObject.getString("profile");
                                            get_friend_id = jsonObject.getString("id");
//                                            friend_image_url = new URL("http://ec2-52-78-13-66.ap-northeast-2.compute.amazonaws.com" + get_friend_image);
                                            friend_image_url ="http://ec2-52-78-13-66.ap-northeast-2.compute.amazonaws.com" + get_friend_image;
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        item = new item();
                                        item.setFriend_name(get_friend_name);
                                        item.setFriend_state_message(get_friend_state_message);
                                        item.setFriend_image(friend_image_url);
                                        item.setFriend_id(get_friend_id);

                                        friend_list.add(item);

//                            friend_list.add( new item(get_friend_name, get_friend_state_message, friend_image_url));

                                        Log.i("test_name", "" + get_friend_name);
                                        Log.i("test_state_message", "" + get_friend_state_message);
                                        Log.i("test_image", "" + get_friend_image);
                                    }
                                }
                            }
                        },500);

                            Log.d("seq", "3");
                            Log.d("message", "name" + get_friend_name);
                            Log.d("message", "state_message" + get_friend_state_message);
                            Log.i("message","id"+get_friend_id);

                            bufferedReader.close();


                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (ProtocolException e) {
                        e.printStackTrace();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG,"httpConnection");

                }
            }

   @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedinstanceState){
       RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.fragment_friend, container, false);
       recyclerView = (RecyclerView)layout.findViewById(R.id.recycler_view);
       friend_add_button = (ImageView)layout.findViewById(R.id.friend_add_button);
       handler = new Handler();
       thread = new Thread();
       thread.start();

       friend_add_button.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(getContext(), friend_add.class);
               startActivity(intent);
           }
       });
       friend_list = new ArrayList<item>();
       adapter = new adapter(getContext(),friend_list);
       layoutManager = new LinearLayoutManager(getContext());
       recyclerView.setLayoutManager(layoutManager);
       return  layout;
   }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        thread = new Thread();
//        thread.start();
    }
}
