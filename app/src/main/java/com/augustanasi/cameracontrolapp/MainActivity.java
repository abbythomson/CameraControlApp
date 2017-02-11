package com.augustanasi.cameracontrolapp;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    Button connect;
    Button start;
    String ipString = "10.100.9.174";
    final int port = 5678;
    String filePath;
    ImageView imageView;
    SocketConnection socketConnection;
    boolean stop;
    Button stopBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        imageView = (ImageView)findViewById(R.id.imageView);
        imageView.setRotation(90);

        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"AugustanaRobotImgs");
        if(!storageDir.exists()){
            storageDir.mkdir();
        }
        filePath = storageDir.getPath()+"/image.png";

        connect = (Button)findViewById(R.id.connectBtn);
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Connect","HERE");
                try{
                    connectToSocket();
                }catch (Exception e){
                    e.printStackTrace();
                }


            }
        });

        start = (Button) findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        while(!stop){
                            Log.d("START","Running");
                            try {
                                Log.d("Socket","Requesting Image");
                                final  Bitmap map = socketConnection.requestImg();
                                Log.d("Socket","Image recieve");
                                Log.d("ImageView","BitMap byte Count ="+map.getByteCount());

                                Log.d("Image View","Set Image View");
                                MainActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.d("ImageView","Change Image View");
                                        imageView.setImageBitmap(map);
                                    }
                                });

                                //socketConnection.sendMsg();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

            }
        });

        stopBtn = (Button)findViewById(R.id.stop);
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Socket","Stop Transfer");
                stop = true;
                try{
                    Log.d("Socket","Called SocketConnection.stopTranfer");
                    socketConnection.stopTransfer();
                } catch(IOException e ){
                    e.printStackTrace();
                }
                socketConnection.closeSocket();
            }
        });
    }

    public void connectToSocket() throws Exception{
        socketConnection = new SocketConnection(port,ipString,filePath);
        //socketConnection.sendMsg();
        //ImageThread imgThread = new ImageThread(socketConnection,imageView);
        connect.setClickable(false);
        //imgThread.start();
    }
    /*private void loadImages(InputStream is, Socket server){

        try{
            FileOutputStream fos = new FileOutputStream(filePath);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(server.getOutputStream());
            ImageThread imgThread = new ImageThread(fos,outputStreamWriter,bos,is,filePath,imageView);
            imgThread.start();
            bos.close();
            server.close();
        }catch(IOException e){

        }

    }*/
}

class ImageThread extends Thread{
    private SocketConnection connection;
    private Bitmap bmp;
    private ImageView imgView;

    public ImageThread(SocketConnection c, ImageView view){
       connection = c;
        imgView = view;
    }
    public void run(){
        try{
            while(true){
                bmp = connection.requestImg();
                imgView.setImageBitmap(bmp);
            }

        }catch (IOException e){

        }
        }

}