package com.example.hong.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

public class test_image extends AppCompatActivity {
    ImageView test_image;
    Button test_button;
    Uri uri;
    Bitmap bitmap;
    String image_name, profile_image, profile_info, get_image;
    Thread thread;
    Boolean stop = true;
    String param;
    HttpURLConnection httpURLConnection;
    OutputStream outputStream;
    InputStream inputStream;
    BufferedReader bufferedReader;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_image);
        test_image = (ImageView) findViewById(R.id.test_image);
        test_button = (Button) findViewById(R.id.test_button);
        thread = new Thread();
        thread.start();
        handler = new Handler();

        test_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1111);
            }
        });





        test_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop = false;
            }
        });
}

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode!=RESULT_OK)
            return;
        if (requestCode ==1111){
            uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                image_name = String.valueOf(System.currentTimeMillis());
                Glide.with(com.example.hong.myapplication.test_image.this).load(bitmap).into(test_image);
                //비트맵 to base64
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                profile_image = URLEncoder.encode(Base64.encodeToString(byteArrayOutputStream.toByteArray(),Base64.DEFAULT), "UTF-8");
//                byte[] b_image = byteArrayOutputStream.toByteArray();
//                profile_image = Base64.encodeToString(b_image, Base64.DEFAULT);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    class Thread extends java.lang.Thread{

        public void run() {
            while (true) {
                try {
                    thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                while (stop != true) {
                        try {
                            Log.i("start_thread", "start_thread");
                            String serverURL = "http://ec2-13-125-130-93.ap-northeast-2.compute.amazonaws.com/test_image.php?";
                            param = "profile_image="+profile_image +"&image_name="+ image_name;
                            Log.i("imagename",""+image_name);
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

//                            inputStream = null;
//                            bufferedReader = null;
//                            profile_info = "";
//                            inputStream = httpURLConnection.getInputStream();
//                            bufferedReader = new BufferedReader(new InputStreamReader(inputStream), 8 * 1024);
//                            Log.d("seq", "1");
//                            profile_info = bufferedReader.readLine();
//                            Log.d("seq", "2");
//                            Log.d("seq", ""+ profile_info);
//                            JSONObject jsonObject = new JSONObject(profile_info);
//                            Log.d("seq", "3");
//                            get_image = jsonObject.getString("image");
//
//                            if (!get_image.equals("no_image")){
//                                final URL image_url = new URL("http://ec2-13-125-130-93.ap-northeast-2.compute.amazonaws.com"+get_image);
//                                handler.postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Glide.with(com.example.hong.myapplication.test_image.this).load(image_url).into(test_image);
//                                    }
//                                },0);
//                            }
//                            bufferedReader.close();

                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (ProtocolException e) {
                            e.printStackTrace();
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    stop = true;
                }
            }
        }



}

