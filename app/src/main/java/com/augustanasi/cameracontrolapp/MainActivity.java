package com.augustanasi.cameracontrolapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.renderscript.ScriptGroup;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    Button connect;
    EditText ipNum;
    String ipString;
    String filePath;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final int port = 5678;

        imageView = (ImageView)findViewById(R.id.imageView);

        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"AppPics");
        filePath = storageDir.getPath()+"/image.png";

        File temp = new File(storageDir,"image.png");

        Log.d("FilePath","File Path: "+filePath);

        Log.d("Location","Exists: "+storageDir.exists());

        Log.d("File", temp.getName()+" "+temp.toString());

        Bitmap bmp = BitmapFactory.decodeFile(filePath);
        //imageView.setImageBitmap(bmp);


        connect = (Button)findViewById(R.id.connectBtn);
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* try{
                    Socket server = new Socket(ipString,port);
                    InputStream is = server.getInputStream();
                    OutputStreamWriter osWriter = new OutputStreamWriter(server.getOutputStream());
                    osWriter.write("size\n");
                    int fileSize = is.read();
                    Log.d("READ IN","File Size = "+fileSize);
                    byte[] fileBytes = new byte[fileSize];
                    loadImages(is,fileSize,server);

                }catch (IOException e){

                }*/

            }
        });
        ipNum =  (EditText)findViewById(R.id.IPNum);

        ipNum.addTextChangedListener(addressTW);
    }

    private void loadImages(InputStream is, int fileSize, Socket server){

        Thread imageThread = new Thread();

        try{
            FileOutputStream fos = new FileOutputStream(filePath);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            ImageThread imgThread = new ImageThread(fos,bos,is,fileSize,filePath,imageView);
            imgThread.start();
            bos.close();
            server.close();
        }catch(IOException e){

        }

    }

    private TextWatcher addressTW = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            ipString = s.toString();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}

class ImageThread extends Thread{
    private FileOutputStream fos;
    private BufferedOutputStream bos;
    private InputStream is;
    private byte[] fileData;
    private ImageView imgView;
    private String path;
    private Bitmap bmp;

    public ImageThread(FileOutputStream fileOutputStream, BufferedOutputStream bufferedOutputStream, InputStream stream, int numByes , String filePath, ImageView view){
        fos = fileOutputStream;
        bos = bufferedOutputStream;
        is = stream;
        fileData = new byte[numByes];
        imgView = view;
        path = filePath;
        bmp = BitmapFactory.decodeFile(path);
    }
    public void run(){
        try{
            while(true){
                int bytesRead = is.read(fileData,0,fileData.length);
                bos.write(fileData,0,bytesRead);
                imgView.setImageBitmap(bmp);
            }

        }catch (IOException e){

        }
        }

}