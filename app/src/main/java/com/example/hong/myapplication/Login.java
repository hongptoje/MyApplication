package com.example.hong.myapplication;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.nfc.Tag;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.kakao.auth.ErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;

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
import java.security.MessageDigest;

public class Login extends AppCompatActivity {
EditText write_id, write_pwd;
String id,pwd,param, login_check, auto_login_id;
Button login_button, join_button;
HttpURLConnection httpURLConnection;
OutputStream outputStream;
InputStream inputStream;
BufferedReader bufferedReader;
Thread thread;
Boolean stop=true;
SessionCallback callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);
        if (com.kakao.auth.Session.getCurrentSession() != null){
            callback.onSessionOpened();

        }

        login_button = (Button)findViewById(R.id.login_button);
        join_button = (Button)findViewById(R.id.join_button);
        write_id = (EditText)findViewById(R.id.write_id);
        write_pwd = (EditText)findViewById(R.id.write_pwd);
        thread = new Thread();
        thread.start();
        //자동로그인
        SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        auto_login_id = auto.getString("input_id",null);
        if (auto_login_id != null){
            Intent intent = new Intent(Login.this, Main.class);
            startActivity(intent);
            finish();
        }else if (auto_login_id == null){
            login_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    id = write_id.getText().toString();
                    pwd = write_pwd.getText().toString();
                    stop = false;
                }
            });
        }

        join_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, join.class);
                startActivity(intent);
            }
        });
    }

    class Thread extends java.lang.Thread{

        String token = FirebaseInstanceId.getInstance().getToken();
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
                        String serverURL = "http://ec2-52-78-13-66.ap-northeast-2.compute.amazonaws.com/login_ok.php?";
//                param = URLEncoder.encode("id","UTF-8")+"="+URLEncoder.encode(id,"UTF-8");
//                param +="&"+URLEncoder.encode("pwd","UTF-8")+"="+URLEncoder.encode(pwd,"UTF-8");
//                param +="&"+URLEncoder.encode("email","UTF-8")+"="+URLEncoder.encode(email,"UTF-8");
                        param = "id=" + id + "&pwd=" + pwd + "&token=" + token;
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
                        outputStream.write(param.getBytes());
                        outputStream.flush();
                        outputStream.close();
                        httpURLConnection.getResponseCode();
                        inputStream = null;
                        bufferedReader = null;
                        login_check = "";

                        inputStream = httpURLConnection.getInputStream();
                        bufferedReader = new BufferedReader(new InputStreamReader(inputStream), 8 * 1024);
                        login_check = bufferedReader.readLine();
//                while ((login_check = bufferedReader.readLine()) != null){
//                    Log.i("login_check", login_check);
//                }
                        Log.i("login_check", "logincheck: " + login_check);

                        if (login_check.equals("login_ok")) {
                            Intent intent = new Intent(Login.this, Main.class);
                            SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor auto_login = auto.edit();
                            auto_login.putString("input_id",id);
                            auto_login.commit();
                            startActivity(intent);
                            finish();
                        } else {
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(Login.this, "아이디, 비밀번호를 확인해주세요", Toast.LENGTH_LONG).show();
                                }
                            }, 0);

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
    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {

            UserManagement.requestMe(new MeResponseCallback() {

                @Override
                public void onFailure(ErrorResult errorResult) {
                    String message = "failed to get user info. msg=" + errorResult;

                    ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                    if (result == ErrorCode.CLIENT_ERROR_CODE) {
                        //에러로 인한 로그인 실패
//                        finish();
                    } else {
                        //redirectMainActivity();
                    }
                }

                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                }

                @Override
                public void onNotSignedUp() {

                }

                @Override
                public void onSuccess(UserProfile userProfile) {
                    //로그인에 성공하면 로그인한 사용자의 일련번호, 닉네임, 이미지url등을 리턴합니다.
                    //사용자 ID는 보안상의 문제로 제공하지 않고 일련번호는 제공합니다.

                    Log.e("UserProfile", userProfile.toString());
                    Log.e("UserProfile", userProfile.getId() + "");
                    String kakao_id = String.valueOf(userProfile.getId());
                    String kakao_nickname = String.valueOf(userProfile.getNickname());
                    String kakao_image = String.valueOf(userProfile.getProfileImagePath());

                    Log.i("kakao_id",""+kakao_id);

                    SharedPreferences kakao_info = getSharedPreferences("kakao_info",Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = kakao_info.edit();
                    editor.putString("kakao_id", kakao_id);
                    editor.putString("kakao_nickname", kakao_nickname);
                    editor.putString("kakao_image", kakao_image);
                    editor.commit();


                    //프래그먼트로 데이터 보내기
//                    setting setting = new setting();
//                    Bundle bundle = new Bundle();
//                    bundle.putString("kakao_id", kakao_id);
//                    bundle.putString("kakao_nickname", kakao_nickname);
//                    bundle.putString("kakao_image", kakao_image);
//                    setting.setArguments(bundle);

                    Log.i("kakao_nickname",""+kakao_nickname);

                    Intent intent = new Intent(Login.this, Main.class);
                    startActivity(intent);
//                    long number = userProfile.getId();


                }
            });

        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            // 세션 연결이 실패했을때
            // 어쩔때 실패되는지는 테스트를 안해보았음 ㅜㅜ

        }
    }
}

