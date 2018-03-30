package com.example.hong.myapplication;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.StringTokenizer;

public class chatting extends AppCompatActivity {
    String TAG = "chattingActivity";
    RecyclerView recyclerView;
    ArrayList<chatting_item> chatting_list;
    chatting_item chatting_item;
    Chatting_Adapter adaptor;
    EditText send_message;
    String meetNow_state = "채팅";
    ImageView send_message_button;
    ImageView meet_now_button;
    TextView chatting_info;
    String name;
    Handler handler;
    ClientThread clientThread;
    ReceiveThread receiveThread;
    SendThread sendThread;
    Socket socket;
    String ip = "52.78.13.66";
    int port = 5001;
    String  get_image, get_message;
    RecyclerView.LayoutManager layoutManager;
    String chatMessage;
    String get_id;//로그인한 유저의 아이디
    String friend_get_id;//채팅하려는 친구의 아이디
    String get_room_number;//채팅방 선택시 가져오는 방번호
    String get_name;
    String receive_id;//소켓서버에서 받아온 메시지를 보낸사람의 아이디
    String save_id;
    String server_get_name, server_get_profile;
    String receive_message;
    String server_get_room_number;//서버에서 넘겨받아오는 방번호 -> DB에 저장할 방번호
    ImageButton chatting_back_button;
    String load_content = "load"; //채팅시작시 저장된 채팅내용을 불러오기위함
    String load_writer;
    int position_type;
    int message_again=0;
    AlertDialog.Builder dialog;
    httpConnection httpConnection = new httpConnection();
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

     double comeHereLatitude;
    double comeHereLongitude;


    private FusedLocationProviderClient mFusedLocationClient;
    private boolean mLocationPermissionGranted;
    private Double latitude;
    private Double longitude;
    private Double receiveLatitude;
    private Double receiveLongitude;
    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION; //GPS access : GPS신호를 통해 위치 정보를 받기위한 권한설정
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION; //Cell_ID/WiFi access : WiFi또는 통신사의 기지국 정보를 통해 위치 정보를 받기위한 권한설정
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    public String getGet_id() {
        return get_id;
    }

    public void setGet_id(String get_id) {
        this.get_id = get_id;
    }

    public String getReceive_id() {
        return receive_id;
    }

    public void setReceive_id(String receive_id) {
        this.receive_id = receive_id;
    }

    public Double getReceiveLatitude() {
        return receiveLatitude;
    }

    public void setReceiveLatitude(Double receiveLatitude) {
        this.receiveLatitude = receiveLatitude;
    }

    public Double getReceiveLongitude() {
        return receiveLongitude;
    }

    public void setReceiveLongitude(Double receiveLongitude) {
        this.receiveLongitude = receiveLongitude;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);
        recyclerView = (RecyclerView)findViewById(R.id.chatting_recycler_view);
        send_message = (EditText)findViewById(R.id.send_message);//채팅 메시지 입력창
        send_message_button = (ImageView)findViewById(R.id.send_message_button);//메시지 전송 버튼
        meet_now_button = (ImageView)findViewById(R.id.meet_now_button); //만나기 요청 버튼
        chatting_back_button = (ImageButton) findViewById(R.id.chatting_back_button);//뒤로가기버튼
        chatting_info = (TextView)findViewById(R.id.chatting_info);
        SharedPreferences get_info = getSharedPreferences("friend_info", Activity.MODE_PRIVATE);
        SharedPreferences id = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        get_id = id.getString("input_id","");
        friend_get_id = get_info.getString("id","");
        get_image = get_info.getString("image","");
        get_name = get_info.getString("name", "");
        get_room_number = get_info.getString("room_number","");
        Intent intent2 = getIntent();
        name = intent2.getStringExtra("friend_info");
        comeHereLatitude = intent2.getDoubleExtra("comeHereLatitude",0);
        comeHereLongitude = intent2.getDoubleExtra("comeHereLatitude",0);
        Log.d(TAG, "comeHereLatLng: "+comeHereLatitude+", "+comeHereLongitude);

        chatting_info.setText(get_name);
        Log.i("chatting_info: ", "아이디= "+friend_get_id + "닉네임= "+ get_name + "이미지= "+get_image + "방번호= "+get_room_number);
        clientThread = new ClientThread(ip, port);
        clientThread.start();
        chatting_list = new ArrayList<>();
        adaptor = new Chatting_Adapter(chatting.this, chatting_list);
        layoutManager = new LinearLayoutManager(chatting.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adaptor);
        dialog = new AlertDialog.Builder(chatting.this);
        httpConnection.start();
        getLocationPermission();
        getDeviceLocation();



        //뒤로가기 버튼 클릭
        chatting_back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(chatting.this, friend.class);
                startActivity(intent);
            }
        });

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0){
                    /**get_id는 로그인한 아이디, receive_id는 메시지를 보낸사람의 아이디
                     * 로그인한 사람과 메시지를 보낸 사람이 같은 경우에는 오른쪽에 메시지만 표시
                     * 다른 경우에는 왼쪽에 프로필사진, 닉네임, 메시지 표시
                     * 아이디가 일치하는 경우 posotion_type = 1
                     * */
                    SharedPreferences saved_id2 = getSharedPreferences("saved_id", Activity.MODE_PRIVATE);
                    save_id = saved_id2.getString("get_saved_id", "");
                    Log.i("저장된 아이디불러오기", ""+save_id);
                    Log.i("send_adapter_info", getGet_id()+", "+getReceive_id());
                    if (getGet_id().equals(getReceive_id())){
                        position_type = 1;
                    }else{
                        position_type = 0;
                        if (save_id == null || save_id == "" || !save_id.equals(getReceive_id())){
                            message_again = 0;
                        }else if (save_id.equals(getReceive_id())){
                            message_again = 1;
                        }
                    }

                        chatting_list.add(new chatting_item(receive_message, server_get_name, "http://ec2-52-78-13-66.ap-northeast-2.compute.amazonaws.com"+ server_get_profile, position_type, message_again));
                        adaptor.notifyDataSetChanged();
                        send_message.setText("");
                    SharedPreferences saved_id = getSharedPreferences("saved_id", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor save_id_editor = saved_id.edit();
                    save_id_editor.putString("get_saved_id", getReceive_id());
                    Log.i("저장한 아이디", ""+getReceive_id());
                    save_id_editor.commit();
                    }
            }
        };


        //메시지 전송 버튼
        send_message_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendThread = new SendThread(socket);
                sendThread.start();
                //firebase realtime database에 값을 저장한다. 그러면 Main Activity에서 ChildEventListener를 통해 방정보를 갱신해준다.
                databaseReference.child("message").push().setValue("listRefresh");
                Log.i("chatting_info_onclick","이름 :"+ name+ "메시지 : " + get_message + "이미지 : " +get_image);
            }
        });

        //만나기 요청
        meet_now_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "getDeviceLocation: Latitude: "+latitude+" Longitude: "+longitude);
            dialog.setTitle("만나기 요청을 하시겠습니까?");
            dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    meetNow_state = "만남요청";
                    sendThread = new SendThread(socket);
                    sendThread.start();
                    databaseReference.child("message").push().setValue("meetNow");
                }
            });
            dialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });dialog.show();
            }
        });

        if (comeHereLatitude !=0 && comeHereLongitude != 0){
            meetNow_state = "여기로와";
            sendThread = new SendThread(socket);
            sendThread.start();
        }
    }


    //소켓  내부 클래스
    class ClientThread extends Thread{
        String ip;
        int port;
        DataOutputStream out;
        String room_setting;
        public ClientThread(String ip, int port) {
            this.ip = ip;
            this.port = port;
        }

        @Override
        public void run() {
            super.run();
            try {
                socket = new Socket(ip, port);
                try {
                    out = new DataOutputStream(socket.getOutputStream());
                    room_setting = get_id+"/"+friend_get_id+"/"+get_room_number;
                    out.writeUTF(room_setting);
                }catch (IOException e){
                    e.printStackTrace();
                }
                receiveThread = new ReceiveThread(socket);
                receiveThread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //받는 부분 내부 클래스
    class ReceiveThread extends Thread{
        Socket socket;
        DataInputStream dataInputStream;
        AlertDialog.Builder receiveDialog;

        public ReceiveThread(Socket socket) {
            this.socket = socket;
            try {
                dataInputStream = new DataInputStream(socket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            super.run();
            Log.d(TAG,"receiveThread");
            try {
            while(dataInputStream != null){
                    chatMessage = dataInputStream.readUTF();

                JSONObject jsonObject = new JSONObject(chatMessage);
                receive_id = jsonObject.getString("id");
                server_get_room_number = jsonObject.getString("room_number");
                receive_message = jsonObject.getString("send_message");
                receiveLatitude = Double.valueOf(jsonObject.getString("get_latitude"));
                receiveLongitude = Double.valueOf(jsonObject.getString("get_longitude"));
                receiveDialog = new AlertDialog.Builder(chatting.this);
                if (!receive_id.equals(get_id)){
                    setReceiveLatitude(receiveLatitude);
                    setReceiveLongitude(receiveLongitude);
                }
                Log.d(TAG,""+receive_id);
                Log.d(TAG,""+receive_message);
                Log.d(TAG,""+receiveLongitude);
                Log.d(TAG,""+receiveLatitude);

                    //받은 메시지가 #지금만나이고 보낸사람과 로그인한 사람이 다른 경우 요청 다이얼로그 띄움
                    if (receive_message.equals("#지금만나")&&!receive_id.equals(get_id)){
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                receiveDialog.setTitle("만남 요청을 수락하시겠습니까?");
                                receiveDialog.setPositiveButton("수락", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        meetNow_state = "만남수락";
                                        sendThread = new SendThread(socket);
                                        sendThread.start();
                                    }
                                });
                                receiveDialog.setNegativeButton("거절", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        meetNow_state = "만남거절";
                                        sendThread = new SendThread(socket);
                                        sendThread.start();
                                    }
                                });
                                receiveDialog.show();
                            }
                        },0);
                    }else if (receive_message.equals("#수락")){
                        //받는 메시지가 #수락인경우 지도를 내 좌표를 상대방에게 보내고 지도에 위치를 띄움
                        Log.d(TAG, "onClick: meetOk: getDeviceLocation: latitude: "+getReceiveLatitude()+" longitude: "+getReceiveLongitude());
                        Intent intent = new Intent(chatting.this, MapsActivity.class);
                        intent.putExtra("receiveLatitude",getReceiveLatitude());
                        intent.putExtra("receiveLongitude",getReceiveLongitude());
                        intent.putExtra("friendName", get_name);
                        startActivity(intent);
                    }

                httpConnection httpConnection = new httpConnection();
                httpConnection.setDaemon(true);
                httpConnection.start();

                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //보내는 부분 내무 클래스
    class SendThread extends Thread{
        Socket socket;
        DataOutputStream dataOutputStream;
        String send;

        public SendThread(Socket socket) {
            this.socket = socket;
            try {
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            super.run();
            switch (meetNow_state){
                case "채팅":
//                    send = get_id+"/"+get_room_number+"/"+send_message.getText().toString();
                    send = get_id+"/"+get_room_number+"/"+send_message.getText().toString()+"/"+37.555950+"/"+127.02178;
                    meetNow_state = "채팅";
                    break;
                case "만남요청":
//                    send = get_id+"/"+get_room_number+"/"+"#지금만나";
                    send = get_id+"/"+get_room_number+"/"+"#지금만나"+"/"+latitude+"/"+longitude;
                    meetNow_state = "채팅";
                    break;
                case "만남수락":
//                    send = get_id+"/"+get_room_number+"/"+"#수락";
                    send = get_id+"/"+get_room_number+"/"+"#수락"+"/"+latitude+"/"+longitude;
                    meetNow_state = "채팅";
                    break;
                case "만남거절":
//                    send = get_id+"/"+get_room_number+"/"+"#거절";
                    send = get_id+"/"+get_room_number+"/"+"#거절"+"/"+37.555950+"/"+127.02178;
                    meetNow_state = "채팅";
                    break;
                case "여기로와":
                    send =get_id+"/"+get_room_number+"/"+"#여기로와"+"/"+comeHereLatitude+"/"+comeHereLongitude;

            }

            try {
                dataOutputStream.writeUTF(send);
                Log.i("sendMessage", ""+ dataOutputStream);
                Log.i("sendMessage", ""+send);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //httpConnection
    class httpConnection extends Thread{
        OutputStream output;
        InputStream input;
        String param;
        HttpURLConnection httpURLConnection;
        BufferedReader bufferedReader;
        String server_get_info;
        String chatting_content_info;
        JSONArray jsonArray;
        JSONObject jsonObject;

        @Override
        public void run() {
            super.run();
            String serverURL = "http://ec2-52-78-13-66.ap-northeast-2.compute.amazonaws.com/chatting.php?";
            try {
                URL url = new URL(serverURL);
                param = "id="+receive_id+"&room_number="+get_room_number+"&chatting_content="+receive_message+"&load_content="+load_content+"&my_id="+get_id+"&friend_id="+friend_get_id;
                Log.d("chatting_param",""+param);
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

                input = null;
                bufferedReader = null;
                server_get_info = "";
                input = httpURLConnection.getInputStream();
                    bufferedReader = new BufferedReader(new InputStreamReader(input), 8 * 1024);
                    StringBuffer stringBuffer = new StringBuffer();
                while ((server_get_info = bufferedReader.readLine()) != null){
                    stringBuffer.append(server_get_info);
                }
                chatting_content_info = stringBuffer.toString();
                    Log.i("server_get_info", "" + chatting_content_info);

                    if (load_content.equals("load")) {
                        jsonArray = new JSONArray(chatting_content_info);
                        jsonObject=null;
                        if (jsonArray != null){
                            for (int i=0; i<jsonArray.length(); i++){
                                jsonObject = jsonArray.getJSONObject(i);
                                load_writer = jsonObject.getString("writer");
//                                load_contents = jsonObject.getString("content");
                                setReceive_id(load_writer);
                                receive_message = jsonObject.getString("content");
                                server_get_name = jsonObject.getString("server_get_name");
                                server_get_profile = jsonObject.getString("server_get_profile");
                                Log.i("load_content", "기존 채팅내용 불러오기----------------------------------------------------------");
                                Log.i("load_content", "작성자 ID: " + load_writer);
//                                Log.i("load_content", "채팅 내용: " + load_contents);
                                Log.i("load_content", "채팅 내용: " + receive_message);
                                Log.i("load_content", "작성자 이름: " + server_get_name);
                                Log.i("load_content", "프로필사진: " + server_get_profile);
                                SharedPreferences saved_id2 = getSharedPreferences("saved_id", Activity.MODE_PRIVATE);
                                save_id = saved_id2.getString("get_saved_id", "");
                                if (getGet_id().equals(getReceive_id())){
                                    position_type = 1;
                                }else{
                                    position_type = 0;
                                    if (save_id == null || save_id == "" || !save_id.equals(getReceive_id())){
                                        message_again = 0;
                                    }else if (save_id.equals(getReceive_id())){
                                        message_again = 1;
                                    }
                                }
                                chatting_item = new chatting_item(receive_message, server_get_name, "http://ec2-52-78-13-66.ap-northeast-2.compute.amazonaws.com"+server_get_profile, position_type, message_again);
                                chatting_list.add(chatting_item);
                                SharedPreferences saved_id = getSharedPreferences("saved_id", Activity.MODE_PRIVATE);
                                SharedPreferences.Editor save_id_editor = saved_id.edit();
                                save_id_editor.putString("get_saved_id", getReceive_id());
                                save_id_editor.commit();
                            }
                        }
                    } else {
                        jsonObject = new JSONObject(chatting_content_info);
                        server_get_name = jsonObject.getString("server_get_name");
                        server_get_profile = jsonObject.getString("server_get_profile");
                        handler.sendEmptyMessage(0);
                        Log.i("new_content", "새로운 채팅 시작");
                    }
                    bufferedReader.close();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            load_content= "unLoad";
        }
    }

    public void getLocationPermission() {
        Log.d(TAG, "getLocationPermission");
        String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION};

        //FINELOCATION 권한이 있는지 체크함, 권한이 있는경우 PackageManager.PERMISSION_GRANTED를 반환한다.
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //COURSE_LOCATION 권한이 있는지 체크함, 권한이 있는경우  PackageManager.PERMISSION_GRANTED를 반환한다.
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
            } else {
                //권한이 없는 경우 권한 요청
                ActivityCompat.requestPermissions(this, permission, LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else {
            ActivityCompat.requestPermissions(this, permission, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    //권한 요청시 응답처리
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "permissionRequest: called");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation");
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermissionGranted) {
                Task location = mFusedLocationClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete");
                            Location currentLocation = (Location) task.getResult();
                            if (currentLocation == null){
                            latitude = 37.509773;
                            longitude = 127.07572;
                            }else {
                                latitude = currentLocation.getLatitude();
                                longitude = currentLocation.getLongitude();
                                Log.d(TAG, "getDeviceLocation: latitude: " + latitude + " longitude: " + longitude);
                            }
                        } else {
                            Log.d(TAG, "current location null");
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "SecurityException");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
        System.gc();
        SharedPreferences friend_info = getSharedPreferences("friend_info",Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = friend_info.edit();
        editor.remove("room_number").commit();
        SharedPreferences saved_id = getSharedPreferences("saved_id", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor2 = saved_id.edit();
        editor2.remove("get_saved_id").commit();
    }
}
