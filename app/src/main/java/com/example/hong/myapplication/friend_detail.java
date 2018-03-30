package com.example.hong.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;

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

import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class friend_detail extends AppCompatActivity {
    ImageView image_blur, image_main;
    TextView name, state_message;
    Button start_chatting_button;
    String detail_name, detail_state_message, friend_name, detail_image, my_id, detail_id;
    int position=1;
    FriendDetailThread friendDetailThread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_detail);
        image_blur = (ImageView)findViewById(R.id.image_blur);
        image_main = (ImageView)findViewById(R.id.image_main);
        name = (TextView)findViewById(R.id.name);
        state_message = (TextView)findViewById(R.id.state_message);
        start_chatting_button = (Button)findViewById(R.id.start_chatting_button);


        //내 아이디 가져오기
        SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        my_id = auto.getString("input_id", null);
        //저장된 친구 아이템값 가져오기
        SharedPreferences selected_friend_info = getSharedPreferences("friend_info", Activity.MODE_PRIVATE);
        detail_name = selected_friend_info.getString("name","");
        detail_state_message = selected_friend_info.getString("state_message", "");
        detail_image = selected_friend_info.getString("image", "");
        detail_id = selected_friend_info.getString("id","");
        Log.i("friend_info", ""+friend_name);
        //방생성할 때 넘길 값
        Log.i("방 생성할 때 넘길 값", "내 아이디정보: " + my_id + " 친구 아이디정보: " + detail_id);




        //아이템 값 셋팅

        Log.i("friend_info", "name=" +detail_name);
        Log.i("friend_info", "state_message=" +detail_state_message);
        Log.i("friend_info", "image=" +detail_image);

        //뷰셋팅
        name.setText(detail_name);
        state_message.setText(detail_state_message);
        Glide.with(friend_detail.this).load(detail_image).apply(bitmapTransform(new CircleCrop())).into(image_main);
        Glide.with(friend_detail.this).load(detail_image).apply(bitmapTransform(new BlurTransformation())).into(image_blur);

        start_chatting_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendDetailThread = new FriendDetailThread();
                friendDetailThread.start();
                Intent intent = new Intent(friend_detail.this, chatting.class);
                intent.putExtra("friend_info",detail_name);
                startActivity(intent);
            }
        });

    }

    class FriendDetailThread extends Thread{
        OutputStream outputStream;
        InputStream inputStream;
        HttpURLConnection httpURLConnection;
        BufferedReader bufferedReader;
        String serverURL;
        String param;
        String created_room_number;
        @Override
        public void run() {
            super.run();
            serverURL = "http://ec2-52-78-13-66.ap-northeast-2.compute.amazonaws.com/create_chatting_room.php?";

            try {
                Log.i("1","1");
                URL url = new URL(serverURL);
                param ="my_id="+my_id+"&friend_id="+detail_id ;
                Log.i("방만들 아이디 체크", my_id+detail_id);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.connect();
                outputStream = httpURLConnection.getOutputStream();
                outputStream.write(param.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();
                httpURLConnection.getResponseCode();

                inputStream = null;
                bufferedReader = null;
                created_room_number = "";
                inputStream = httpURLConnection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream), 8 * 1024);
                created_room_number = bufferedReader.readLine();
                Log.i("생성된 방의 번호: ",""+created_room_number);
                SharedPreferences room_info = getSharedPreferences("friend_info", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = room_info.edit();
                editor.putString("room_number", created_room_number);
                editor.commit();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
