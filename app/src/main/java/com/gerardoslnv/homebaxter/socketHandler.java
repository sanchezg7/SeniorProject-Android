package com.gerardoslnv.homebaxter;

import android.graphics.Bitmap;
import android.provider.ContactsContract;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * Created by rawrdoe on 2/29/16.
 */
public class socketHandler {

    private String recvImagePathName = null;
    private String path = null;
    private String fullFilePath = null; //consider moving this to value/strings.xml

    private String hostname = null;
    private int port;
    private int numObject_Payload;

    socketHandler(String path, String hostname, int port){

        recvImagePathName = "baxter_image.jpg";
        this.path = path; //scalable to different devices
        this.hostname = hostname;
        this.port = port;
        /*
        Recall: thisPath = Environment.getExternalStorageDirectory().getAbsolutePath();
         */
        fullFilePath = this.path + "/Home_Baxter/";
        return;
    }

    public String getFullImagePath(){ return fullFilePath + recvImagePathName;}
    public int getNumObject_Payload(){return numObject_Payload;}


    public File receiveObjectsPayload() throws IOException {
        File file;

        Socket stringSocket = startConnection(hostname, port+1);
        numObject_Payload = receiveNumObjects(stringSocket);
        file = prepareFile(); //prepare file, then recieve
        Socket imageSocket = startConnection(hostname, port);
        file = receiveImage(imageSocket, file);

        return file;
    }

    public Bitmap liveFeed() throws IOException{
        Bitmap bitmap = null;
        return bitmap;
    }
    //continue here 12 - Apr - 2016


    private Socket startConnection(String hostname, int port) throws IOException{
        return new Socket(hostname, port); //throws UHE and IOE exceptions;
    }

    private File prepareFile() throws IOException {
        File file = new File(fullFilePath + recvImagePathName);
        File parentPath = new File(fullFilePath);
        parentPath.mkdirs(); //assures the paths exist for writing
        //file.createNewFile(); //THROWS IOException

        return file;
    }

    /******************************************************
    *Sending Method
    ******************************************************/
    public void transmitObjectIndex(int objectIndex) throws IOException{

        Socket stringSocket = startConnection(hostname, port+2);
        //used BufferedWriter rather than DataOutputWriter due to garbage being sent through the socket
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stringSocket.getOutputStream()));
        String payLoad = String.valueOf(objectIndex);
        writer.write(payLoad, 0, payLoad.length());

        writer.flush();
        writer.close();
        stringSocket.close();

        return;
    }


    /****************************************************
     * Receiving methods
     ****************************************************/


    private int receiveNumObjects(Socket mySoc) throws IOException{
        BufferedReader stringInput = new BufferedReader(new InputStreamReader(mySoc.getInputStream()));

        int temp = Integer.parseInt(stringInput.readLine());
        mySoc.close();
        return temp;
    }


    private File receiveImage(Socket mySoc, File file) throws IOException {

        DataInputStream din = new DataInputStream(mySoc.getInputStream());

        DataOutputStream dout = new DataOutputStream(new FileOutputStream(file));
        byte[] buffer = new byte[1024];
        //Transmission Sequence (number of objects; image of objects)

        int count = 0; //counts the number of bytes read

        while((count = din.read(buffer)) > 0)
        {
            dout.write(buffer, 0, count);
            dout.flush(); //force a write
        }

        dout.close(); //finally close
        mySoc.close();

        return file;
    }

}
