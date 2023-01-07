package com.example.myapplicationa6230103;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;


public class MainActivity extends AppCompatActivity {
    String str_ip;
    int int_port;
    int stat_code;

    connect_thread lianjie;

    TextView receive;
    TextView textV_state;

    EditText ip;
    EditText port;
    EditText out;

    Socket socket=null;

    Button btn_connect;
    Button btn_disconnect;
    Button btn_send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ip=findViewById(R.id.ip);
        port=findViewById(R.id.port);
        out=findViewById(R.id.out);

        receive=findViewById(R.id.receive);
        textV_state=findViewById(R.id.state);

        btn_connect=findViewById(R.id.connect);
        btn_disconnect=findViewById(R.id.disconnect);
        btn_send=findViewById(R.id.send);

        ip.setText("10.3.2.115");
//        ip.setText("47.96.229.137");
        port.setText("12345");

        btn_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str_ip=ip.getText().toString();
                String str_port=port.getText().toString();
                if("".equals(str_ip)||"".equals(str_port)){
                    Toast.makeText(MainActivity.this,"请输入ip和端口号",Toast.LENGTH_SHORT).show();
                }
                else{
                    textV_state.setText(textV_state.getText()+"ip:"+str_ip+"port"+str_port+"\n");
                    int_port=Integer.valueOf(str_port);
                    lianjie=new connect_thread();
                    lianjie.start();}

                textV_state.setText(textV_state.getText()+"stat_code"+stat_code+"\n");
            }
        });
        //断开
        btn_disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textV_state.setText(textV_state.getText()+"btn_disconnect*");
                try {
                    lianjie.conect_close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        //发送
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //子线程中进行网络操作
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(socket!=null){
                            try {

                                String text=out.getText().toString();
                                lianjie.outputStream.write(text.getBytes());
                            } catch (UnknownHostException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();

                            }catch (IOException e) {
                                e.printStackTrace();
                            }}else{
                            runOnUiThread(new Runnable()//不允许其他线程直接操作组件，用提供的此方法可以
                            {
                                public void run()
                                {
                                    // TODO Auto-generated method stub
                                    Toast.makeText(MainActivity.this,"请先建立连接1",Toast.LENGTH_SHORT).show();
                                    textV_state.setText(textV_state.getText()+"请先建立连接1\n");
                                }
                            });
                        }
                    }
                }).start();
            }
        });
    }
    //子线程中进行网络相关操作
    class connect_thread extends Thread {
        OutputStream outputStream=null;
        InputStream inputStream=null;

        @Override
        public void run() {
            //连接
            try {
                socket=new Socket(str_ip, int_port);
                runOnUiThread(new Runnable()//不允许其他线程直接操作组件，用提供的此方法可以
                {
                    public void run()
                    {
                        Toast.makeText(MainActivity.this,"连接成功1",Toast.LENGTH_SHORT).show();
                        textV_state.setText(textV_state.getText()+"连接成功1\n");
                    }
                });
            } catch (UnknownHostException e) {
                runOnUiThread(new Runnable()//不允许其他线程直接操作组件，用提供的此方法可以
                {
                    public void run()
                    {
                        Toast.makeText(MainActivity.this,"连接失败2",Toast.LENGTH_SHORT).show();
                        textV_state.setText(textV_state.getText()+"连接失败2\n"+e.toString()+"\n");
                    }
                });
                e.printStackTrace();
            }catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable()//不允许其他线程直接操作组件，用提供的此方法可以
                {
                    public void run()
                    {
                        Toast.makeText(MainActivity.this,"连接失败3",Toast.LENGTH_SHORT).show();
                        textV_state.setText(textV_state.getText()+"连接失败3："+e.toString()+"\n");
                    }
                });
            }
            if(socket!=null){
                //获取输出流对象
                try {
                    outputStream=socket.getOutputStream();
                    outputStream.write('a');
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try{
                    while (true)
                    {
                        final byte[] buffer = new byte[1024];//创建接收缓冲区
                        inputStream = socket.getInputStream();
                        final int len = inputStream.read(buffer);//数据读出来，并且返回数据的长度
                        runOnUiThread(new Runnable()//不允许其他线程直接操作组件，用提供的此方法可以
                        {
                            public void run()
                            {
                                receive.append(new String(buffer,0,len)+"\r\n");
                            }
                        });
                    }
                }
                catch (IOException e) {

                }}
        };

        public void conect_close () throws IOException {
            socket.close();
        }
    }}