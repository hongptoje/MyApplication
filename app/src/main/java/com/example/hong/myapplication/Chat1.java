package com.example.hong.myapplication;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class Chat1 extends AppCompatActivity {
    Button btnConnected, btnSend;
    EditText editIp, editPort, send_message;
    TextView txtmessage;
    Handler msgHandler;
    SocketClient client;
    ReceiveThread receive;
    SendThread send;
    Socket socket;
    //    LinkedList<SocketClient> threadlist;
    Context context;
    String mac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat1);

        context = this;
        btnConnected = (Button)findViewById(R.id.btnConnected);
        btnSend = (Button)findViewById(R.id.btnSend);
        editIp = (EditText)findViewById(R.id.editIp);
        editPort = (EditText)findViewById(R.id.editPort);
        send_message = (EditText)findViewById(R.id.send_message);
        txtmessage = (TextView)findViewById(R.id.txtmesage);

        msgHandler = new Handler(){
            //백그라운드 스레드에서 받은 메시지를 처리
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1111){
                    //채팅서버로부터 수신한 메시지를 텍스트뷰에 추가
                    txtmessage.append(msg.obj.toString()+"\n");
                }
            }
        };
        //서버에 접속하는 버튼
        btnConnected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client = new SocketClient(editIp.getText().toString(), editPort.getText().toString());
                client.start();
            }
        });

        //서버에 전송하는 버튼
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = send_message.getText().toString();
                if (message != null && message.equals("")){
                    send = new SendThread(socket);
                    send.start();
                    send_message.setText("");
                }
            }
        });
    }

    //내부클래스
    class SocketClient extends Thread{
        boolean threadAlive;
        String ip;
        String port;

        OutputStream outputStream = null;
        BufferedReader br = null;
        DataOutputStream output = null;
        public SocketClient(String ip, String port){
            threadAlive=true;
            this.ip = ip;
            this.port = port;
        }

        public void run(){
            try {
                //채팅서버에 접속
                socket = new Socket(ip, Integer.parseInt(port));
                //서버에 메시지를 전달하기 위한 스트림 생성
                output = new DataOutputStream(socket.getOutputStream());
                //메시지 수신용 스레드 생성
                receive = new ReceiveThread(socket);
                receive.start();
                //와이파이 정보 관리자 객체로부터 폰의  mac address를 가져와서 채팅서버에 전달
                WifiManager mng = (WifiManager)context.getSystemService(WIFI_SERVICE);
                WifiInfo info = mng.getConnectionInfo();
                mac = info.getMacAddress();
                output.writeUTF(mac);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    } //소켓클라이언트 끝
    //내부틀래스
    class ReceiveThread extends Thread{
        Socket socket = null;
        DataInputStream input = null;
        public ReceiveThread(Socket socket){
            this.socket = socket;
            try {
                //채팅서버로부터 메시지를 받기위한 인풋 스트림
                input = new DataInputStream(socket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run(){
            try {
            while(input != null){
                //채팅 서버로부터 받은 메시지
                    String msg = input.readUTF();
                    if (msg != null){
                        //핸드러에게 전달할 메시지 객체
                        Message hdmsg = msgHandler.obtainMessage();
                        hdmsg.what = 1111;  //메시지의 식별자
                        hdmsg.obj = msg; //메시지의 본문
                        //핸드러에게 메시지 전달(화면 변경 요청)
                        msgHandler.sendMessage(hdmsg);
                    }
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class  SendThread extends Thread{
        Socket socket;
        String sendmsg = send_message.getText().toString();
        DataOutputStream output;
        public SendThread(Socket socket){
            this.socket = socket;
            try {
                //채팅서버로 메시지를 보내기 위한 스트림 생성
                output = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        public void run(){
            try {
            if (output != null){
                if (sendmsg != null){
                        output.writeUTF(mac + ":"+sendmsg);
                    }
                    }
                }catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
