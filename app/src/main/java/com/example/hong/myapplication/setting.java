package com.example.hong.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.kakao.auth.authorization.AuthorizationResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
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
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;
import static android.content.Intent.getIntent;
import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class setting extends Fragment {
    ImageView profile_image_setting;
    Button profile_change_button, logout_button, profile_ok_button;
    EditText profile_name, profile_message;
    String image_url;
    String image_name, param, id, name, profile_info, state_message, profile_image, get_name, get_state_message, get_image;
    String kakao_id, kakao_nickname, kakao_image;
    String url;
    String profile_update = "no";
    AlertDialog.Builder dialog;
    Thread thread;
    Boolean stop=true;
    OutputStream outputStream;
    InputStream inputStream;
    BufferedReader bufferedReader;
    HttpURLConnection httpURLConnection;
    Handler handler;
    TextView text_name, text_state_message;
    SharedPreferences kakao_info;
    String TAG="Setting";
    public RequestManager mGlideRequestManager;


    private static final int PICK_FROM_CAMERA= 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_IMAGE = 2;
    private Uri image_uri;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
       RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.fragment_setting, container, false);
        profile_image_setting = (ImageView)layout.findViewById(R.id.profile_image_setting);
        profile_name = (EditText)layout.findViewById(R.id.profile_name);
        profile_message = (EditText)layout.findViewById(R.id.profile_message);
        profile_change_button = (Button)layout.findViewById(R.id.profile_change_button);
        profile_ok_button = (Button)layout.findViewById(R.id.profile_ok_button);
        logout_button = (Button)layout.findViewById(R.id.logout_button);
        text_name = (TextView)layout.findViewById(R.id.text_name);
        text_state_message = (TextView)layout.findViewById(R.id.text_state_message);
        dialog = new AlertDialog.Builder(getContext());

        //카카오톡으로 로그인한 경우 저장된 유저 정보를 가져옴
        kakao_info = getContext().getSharedPreferences("kakao_info", Activity.MODE_PRIVATE);

        //로그인한 유저의 아이디값을 가지고옴
        SharedPreferences auto = getContext().getSharedPreferences("auto", Activity.MODE_PRIVATE);
        id = auto.getString("input_id", "");

//        handler = new Handler(Looper.getMainLooper());
        handler = new Handler();
        thread = new Thread();
        Log.i("profile_update",""+profile_update);
        thread.start();
        mGlideRequestManager = Glide.with(this);

        //프로필 수정
        profile_change_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profile_change_button.setVisibility(View.INVISIBLE);
                profile_ok_button.setVisibility(View.VISIBLE);
                text_name.setVisibility(View.INVISIBLE);
                text_state_message.setVisibility(View.INVISIBLE);
                profile_name.setVisibility(View.VISIBLE);
                profile_message.setVisibility(View.VISIBLE);

            }
        });

        //프로필 수정 확인
        profile_ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = profile_name.getText().toString();
                state_message = profile_message.getText().toString();
                profile_ok_button.setVisibility(View.INVISIBLE);
                profile_change_button.setVisibility(View.VISIBLE);
                text_name.setVisibility(View.VISIBLE);
                text_state_message.setVisibility(View.VISIBLE);
                profile_name.setVisibility(View.INVISIBLE);
                profile_message.setVisibility(View.INVISIBLE);
                profile_update = "yes";
                Log.i("profile_update2222",""+profile_update);
                stop = false;
            }
        });

        //로그아웃
        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences auto = getContext().getSharedPreferences("auto", Activity.MODE_PRIVATE);
                SharedPreferences.Editor logout = auto.edit();
                SharedPreferences.Editor kakao_del = kakao_info.edit();
                kakao_del.clear();
                logout.clear();
                kakao_del.commit();
                logout.commit();
                onClickLogout();
                Intent intent = new Intent(getContext(), Login.class);
                startActivity(intent);
                getActivity().finish();
            }
        });


        //프로필 이미지 설정하기
        profile_image_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setTitle("프로필사진 가져오기");
                dialog.setPositiveButton("사진 촬영", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        take_photo();
                    }
                });
                dialog.setNegativeButton("앨범 에서", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        take_album();
                    }
                });

                dialog.show();


                //                Intent intent = new Intent(Intent.ACTION_PICK);
//                intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
//                intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(intent, requestCode);
            }
        });

        return  layout;
    }

    //카카오톡 로그아웃
    private void onClickLogout() {
        UserManagement.requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {

            }
        });
    }

    //카메라로 촬영해서 가져오기
    public void take_photo(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        url = String.valueOf(System.currentTimeMillis());
        File storageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/pathvalue/"+ url);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            Log.d("log",""+ ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA));
            image_uri = FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".fileprovider",storageDir);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        }else{

            intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri.fromFile(storageDir));
        }
        startActivityForResult(intent, PICK_FROM_CAMERA);

    }

    //갤러리에서 이미지 가져오기
    public void take_album(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    //이미지 크롭
    public void crop_image(){
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(image_uri,"image/*");
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_FROM_IMAGE);
//    startActivity(intent);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode!=RESULT_OK)
            return;
        switch (requestCode){
            case PICK_FROM_ALBUM:{
                image_uri = data.getData();

            }
            case PICK_FROM_CAMERA:{
                crop_image();
                break;
            }
            case CROP_FROM_IMAGE:{
                if (resultCode != RESULT_OK){
                    return;
                }

                final Bundle extras = data.getExtras();

                if (extras != null){
                    Bitmap photo = extras.getParcelable("data");
                    //비트맵 이미지 리사이징
                    Bitmap photo_resize = Bitmap.createScaledBitmap(photo, 600,600, true);
                    Glide.with(getContext()).load(photo_resize).apply(bitmapTransform(new CircleCrop())).into(profile_image_setting);
//                    profile_image_setting.setImageBitmap(photo_resize);
                    image_name = String.valueOf(System.currentTimeMillis());
                    //비트맵 to base64
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    photo_resize.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    try {
                        profile_image = URLEncoder.encode(Base64.encodeToString(byteArrayOutputStream.toByteArray(),Base64.DEFAULT),"UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    Log.i("base64",""+ profile_image);
                    Log.i("base64",""+ image_name);
//                    absolutePath = filePath;
                    break;
                }
            }
        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }


    class Thread extends java.lang.Thread{

        public void run() {
//                    for (int i=0; i<2; i++) {
                        try {
                            kakao_id = kakao_info.getString("kakao_id","");
                            kakao_nickname = kakao_info.getString("kakao_nickname","");
                            kakao_image = kakao_info.getString("kakao_image","");
                            Log.i("kakao_info", "" + kakao_id);
                            Log.i("kakao_info",""+kakao_nickname);
                            Log.i("kakao_info",""+kakao_image);
                            String serverURL = "http://ec2-52-78-13-66.ap-northeast-2.compute.amazonaws.com/profile_ok.php?";
                            Log.d("current_id", "" + id);
                            Log.i("intoPhoto",""+ profile_image);
                            if (kakao_id==null || kakao_id =="") {
                                param = "id=" + id + "&name=" + name + "&state_message=" + state_message + "&profile_image=" + profile_image + "&image_name=" + image_name + "&profile_update=" + profile_update;
                            }else{
                                param = "id=" + kakao_id + "&name=" + kakao_nickname + "&state_message=" + state_message + "&profile_image=" + "kakao_image"  + "&profile_update=" + profile_update;
                            }

                            Log.i("param_info",""+param);
//                            param = "id=" + id + "&name=" + name + "&state_message=" + state_message + "&image_name=" + image_name +"&profile_update=" + profile_update;
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
                            Log.d("seq", ""+ profile_info);
                            JSONObject jsonObject = new JSONObject(profile_info);
                            Log.d("seq", "3");
                            get_name = jsonObject.getString("name");
                            get_state_message = jsonObject.getString("state_message");
                            get_image = jsonObject.getString("image");
                            Log.d("message", "name" + get_name);
                            Log.d("message", "state_message" + get_state_message);

                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                            if (!get_image.equals("no_image")){
                                Log.d(TAG, "get_image: no_image");
                                if (kakao_id==null || kakao_id =="") {
                                    image_url = "http://ec2-52-78-13-66.ap-northeast-2.compute.amazonaws.com" + get_image;
                                }else{
                                    Log.d(TAG, "kakao_image");
                                    image_url = kakao_image;
                                }
                                Log.i("kakao_image",""+image_url);
                                Log.d(TAG,"image_url: "+image_url);
//                                Glide.with(setting.this).load(image_url).apply(bitmapTransform(new CircleCrop())).into(profile_image_setting);
                                mGlideRequestManager.load(image_url).apply(bitmapTransform(new CircleCrop())).into(profile_image_setting);
                            }else {
                                        Bitmap size = BitmapFactory.decodeResource(getResources(), R.drawable.profile_image_default);
                                        size = Bitmap.createScaledBitmap(size, 600,600,true);
                                        profile_image_setting.setImageBitmap(size);
                            }
                            profile_name.setText(get_name);
                                    profile_message.setText(get_state_message);
                                    text_name.setText(get_name);
                                    text_state_message.setText(get_state_message);
                                }
                            },500);

                            bufferedReader.close();

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
        Log.d(TAG, "httpConnection");
        }

    }
}
