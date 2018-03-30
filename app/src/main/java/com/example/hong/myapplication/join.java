package com.example.hong.myapplication;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;

public class join extends AppCompatActivity {
    Button join_ok_button;
    ImageButton back_button;
    EditText join_id, join_pwd, join_pwd_check, join_email;
    String id, pwd, pwd_check, email, check, auto_login_id;
    HttpURLConnection httpURLConnection;
    OutputStream outputStream;
    InputStream inputStream;
    String param;
    Thread thread;
    ImageView checkbox_id, checkbox_id2, checkbox_pwd, checkbox_pwd2, checkbox_email, checkbox_email2;
    boolean stop = true;
    BufferedReader bufferedReader;
    AlertDialog.Builder dialog;
    Handler handler;
    Boolean id_check_ok = true;
    Boolean check_on;
    Boolean no_info = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        join_ok_button = (Button)findViewById(R.id.join_ok_button);
        back_button = (ImageButton) findViewById(R.id.back_button);
        join_id = (EditText) findViewById(R.id.join_id);
        join_pwd = (EditText)findViewById(R.id.join_pwd);
        join_pwd_check = (EditText)findViewById(R.id.join_pwd_check);
        join_email = (EditText)findViewById(R.id.join_email);
        checkbox_id = (ImageView)findViewById(R.id.checkbox_id);
        checkbox_id2 = (ImageView)findViewById(R.id.checkbox_id2);
        checkbox_pwd = (ImageView)findViewById(R.id.checkbox_pwd);
        checkbox_pwd2 = (ImageView)findViewById(R.id.checkbox_pwd2);
        dialog = new AlertDialog.Builder(join.this);
        handler = new Handler(Looper.getMainLooper());
        thread = new Thread();
        thread.start();

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(join.this, Login.class);
                startActivity(intent);
                finish();
            }
        });


        join_ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id = join_id.getText().toString();
                pwd = join_pwd.getText().toString();
                pwd_check = join_pwd_check.getText().toString();
                email= join_email.getText().toString();


                if (!pwd.equals(pwd_check)){
                    dialog.setTitle("비밀번호가 일치하지 않습니다.");
                    dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    dialog.show();
                }else if (id.equals("") || pwd.equals("") || pwd_check.equals("") || email.equals("")){
                    dialog.setTitle("정보를 모두 입력해주세요");
                    dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    dialog.show();
                } else {
                    stop = false;
                    id_check_ok = false;
                }
            }
        });

//        join_id.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                id = join_id.getText().toString();
//
//                id_check_ok = true;
//                if (check_on = true){
//                    checkbox_id.setVisibility(View.GONE);
//                    checkbox_id2.setVisibility(View.VISIBLE);
//                }else{
//                    checkbox_id.setVisibility(View.VISIBLE);
//                    checkbox_id2.setVisibility(View.GONE);
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                stop = false;
//            }
//        });


        join_pwd_check.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            pwd = join_pwd.getText().toString();
            pwd_check = join_pwd_check.getText().toString();
            if (pwd.equals(pwd_check)){
                checkbox_pwd.setVisibility(View.VISIBLE);
                checkbox_pwd2.setVisibility(View.GONE);
            }else{
                checkbox_pwd.setVisibility(View.GONE);
                checkbox_pwd2.setVisibility(View.VISIBLE);
            }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }

    class Thread extends java.lang.Thread{

        public void run(){
            while (true) {
                try {
                    thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                while (stop != true) {

                    try {
                        String serverURL = "http://ec2-52-78-13-66.ap-northeast-2.compute.amazonaws.com/insert.php?";
                        Log.i("check", "thread id =" + id);
//                param = URLEncoder.encode("id","UTF-8")+"="+URLEncoder.encode(id,"UTF-8");
//                param +="&"+URLEncoder.encode("pwd","UTF-8")+"="+URLEncoder.encode(pwd,"UTF-8");
//                param +="&"+URLEncoder.encode("email","UTF-8")+"="+URLEncoder.encode(email,"UTF-8");
                        param = "id=" + id + "&pwd=" + pwd + "&email=" + email;
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
                        outputStream = httpURLConnection.getOutputStream();
                        outputStream.write(param.getBytes("UTF-8"));
                        outputStream.flush();
                        outputStream.close();
                        httpURLConnection.getResponseCode();
                        inputStream = null;
                        bufferedReader = null;
                        check = "";

                        inputStream = httpURLConnection.getInputStream();
                        bufferedReader = new BufferedReader(new InputStreamReader(inputStream), 8 * 1024);
                        check = bufferedReader.readLine();
                        Log.i("아이디 체크2", "아이디체크2: "+ check);
                        Log.i("id_check", "check_on"+ check_on + "id_check_ok" + id_check_ok);

                            if (check.equals("used_id")) {
                                dialog.setTitle("이미 사용중인 아이디입니다.");
                                dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialog.show();
                                    }
                                }, 0);
                            } else if (check.equals("join_ok")) {
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(join.this, "회원가입이 완료되었습니다.", Toast.LENGTH_LONG).show();
                                    }
                                },0);

                                Intent intent = new Intent(join.this, Login.class);
                                startActivity(intent);
                            }
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

                    stop = true;
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
