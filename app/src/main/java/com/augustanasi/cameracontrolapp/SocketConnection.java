package com.augustanasi.cameracontrolapp;

/**
 * Created by viola on 2/7/2017.
 */
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class SocketConnection {

    private Socket socket;
    private OutputStreamWriter os;
    private String filePath;
    private FileOutputStream fos;
    private BufferedOutputStream bos;
    private BufferedInputStream bis;
    private DataInputStream dis;
    private int count;
    private File imageFile;


    public SocketConnection(int portNum, String ip,String fp) throws Exception{
        Log.d("SocketConnection", "Here");
        socket = new Socket(ip, portNum);
        Log.d("SocketConnection","Socket Created");
        os = new OutputStreamWriter(socket.getOutputStream());
        filePath = fp;
        //fos = new FileOutputStream(filePath);
        bos = new BufferedOutputStream(fos);
        bis = new BufferedInputStream(socket.getInputStream());
        dis = new DataInputStream(bis);
        count = 0;
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "AugustanaRobotImgs");
        imageFile = new File(storageDir,"image.png");
        fos = new FileOutputStream(imageFile,false);
    }

  /*  public void sendMsg() throws IOException {
        Log.d("SOCKET", "SENDING MESSAGE");
        os.write("Message from other app \n");
        os.flush();
        Log.d("SOCKET","Message Sent");
        Log.d("Socket","Wait For Size");
        String str = dis.readInt();
        Log.d("Socket",str);
        int size = br.read();
        Log.d("Socket","Size: "+size);
    }*/
    public Bitmap requestImg() throws IOException{
        Log.d("Socket", "Send pic");
        os.write("pic"+"\n");
        os.flush();
        return loadIntoMem();
    }
    public void stopTransfer() throws IOException{
        os.write("stop"+"\n");
        os.flush();
    }

    private Bitmap loadIntoMem() throws IOException{
        Log.d("Socket", "In LOADINTOMEM");
        int fileLength = dis.readInt();
        Log.d("Socket","File Length = "+fileLength);
        byte[] fileByArray = new byte[fileLength];
        dis.readFully(fileByArray);
        Log.d("Socket","Read File IN!!!!!!");
        Log.d("Socket","Start to write to file");
        fos.write(fileByArray);
        Log.d("Socket","File writen Complete");
        Log.d("File","Image File Size = "+imageFile.length());
        //int bytesRead = is.read(fileByArray,0,fileByArray.length);
        //int current = bytesRead;
//        try{
//            /*do{
//                bytesRead = is.read(fileByArray,current,(fileByArray.length-current));
//                if(bytesRead>=0){
//                    current = current + bytesRead;
//                }
//            }while(bytesRead > -1);
//
//            bos.write(fileByArray,0,current);
//            bos.flush();
//            Log.d("Socket","File Downloaded");*/
//            while((bytesRead = is.read(fileByArray,0,fileByArray.length))!= -1){
//                bos.write(fileByArray,0,bytesRead);
//                Log.d("Socket", "Loading to memory");
//            }
//            Log.d("Socket","Image Loaded");
//        }catch (IOException e){
//            e.printStackTrace();
//        }
        return BitmapFactory.decodeFile(filePath);
    }
    public void closeSocket(){
        try{
            Log.d("Socket","Socket Closed");
            socket.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
