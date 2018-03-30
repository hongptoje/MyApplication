package com.example.hong.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

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

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class friend_add extends AppCompatActivity {
    ImageButton back_button;
    Button friend_search_button, friend_add_button;
    EditText friend_search;
    ImageView friend_image;
    TextView friend_name, friend_state_message, friend_no, friend_no2;
    LinearLayout friend_ok_layout;
    String friend_id, get_friend_name, get_friend_state_message, get_friend_image;
    Boolean stop = true;
    OutputStream outputStream;
    InputStream inputStream;
    BufferedReader bufferedReader;
    HttpURLConnection httpURLConnection;
    Handler handler;
    Thread thread;
    String param, profile_info, id;
    AlertDialog.Builder dialog;
    String add_ok = "no";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_add);
        back_button = (ImageButton)findViewById(R.id.back_button);
        friend_search_button = (Button)findViewById(R.id.friend_search_button);
        friend_add_button = (Button)findViewById(R.id.friend_add_button);
        friend_search = (EditText)findViewById(R.id.friend_search);
        friend_image = (ImageView)findViewById(R.id.friend_image);
        friend_name = (TextView)findViewById(R.id.friend_name);
        friend_state_message = (TextView)findViewById(R.id.friend_state_message);
        friend_no = (TextView)findViewById(R.id.friend_no);
        friend_no2 = (TextView)findViewById(R.id.friend_no2);
        friend_ok_layout = (LinearLayout)findViewById(R.id.friend_ok_layout);
        dialog = new AlertDialog.Builder(friend_add.this);
        handler = new Handler(Looper.getMainLooper());
        thread = new Thread();
        thread.start();

        //뒤로가기
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(friend_add.this, Main.class);
                startActivity(intent);
                finish();
            }
        });

        //아이디로 친구 찾기
        friend_search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friend_id = friend_search.getText().toString();
                stop = false;
            }
        });

        //친구 추가하기
        friend_add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               dialog.setTitle("추가하시겠습니까?");
               dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                        add_ok = "ok";
                        stop = false;
                   }
               });
               dialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                  dialog.dismiss();
                   }
               });
               dialog.show();
            }
        });
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
                            String serverURL = "http://ec2-52-78-13-66.ap-northeast-2.compute.amazonaws.com/friend_add.php?";
                            SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
                            id = auto.getString("input_id", "");
                            param = "id=" + id + "&friend_id=" + friend_id + "&add_ok=" + add_ok ;
                            Log.i("id= ",""+id);
                            Log.i("friend_id= ",""+friend_id);
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
                            profile_info = bufferedReader.readLine();
                            Log.d("seq", "2");
                            Log.d("friend_ok", ""+ profile_info);
                            if (profile_info.equals("friend_empty")){
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        friend_ok_layout.setVisibility(View.INVISIBLE);
                                        friend_no.setVisibility(View.VISIBLE);
                                        friend_no2.setVisibility(View.VISIBLE);
                                    }
                                },0 );
                            }else if (profile_info.equals("add_no")){
                              handler.postDelayed(new Runnable() {
                                  @Override
                                  public void run() {
                                   Toast.makeText(friend_add.this,"이미 추가되었습니다.", Toast.LENGTH_LONG).show();
                                  }
                              },0);
                            } else if (profile_info.equals("add_ok")){
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(friend_add.this,"친구가 추가되었습니다.",  Toast.LENGTH_LONG).show();
                                    }
                                },0);
                            }
                            else {
                                JSONObject jsonObject = new JSONObject(profile_info);
                                Log.d("seq", "3");
                                get_friend_name = jsonObject.getString("name");
                                get_friend_state_message = jsonObject.getString("state_message");
                                get_friend_image = jsonObject.getString("friend_image");
                                final URL friend_image_url = new URL("http://ec2-52-78-13-66.ap-northeast-2.compute.amazonaws.com"+get_friend_image);

                                Log.d("message", "name" + get_friend_name);
                                Log.d("message", "state_message" + get_friend_state_message);


                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        friend_ok_layout.setVisibility(View.VISIBLE);
                                        friend_no.setVisibility(View.INVISIBLE);
                                        friend_no2.setVisibility(View.INVISIBLE);
                                        friend_name.setText(get_friend_name);
                                        friend_state_message.setText(get_friend_state_message);
                                        Glide.with(friend_add.this).load(friend_image_url).apply(bitmapTransform(new CircleCrop())).into(friend_image);
                                    }
                                }, 0);
                                bufferedReader.close();
                            }

                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (ProtocolException e) {
                            e.printStackTrace();
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//                    }
                    stop = true;
                    add_ok = "no";
                }
            }
        }
    }
}
