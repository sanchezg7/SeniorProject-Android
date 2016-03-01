package com.gerardoslnv.homebaxter;

import android.os.Environment;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by rawrdoe on 2/29/16.
 */
public class socketReceiver {

    private String recvImage = null;
    private String path = null;
    private String fullFilePath = null; //consider moving this to value/strings.xml

    private String hostname = null;
    private int port;

    socketReceiver(String path, String hostname, int port){

        recvImage = "baxter_image.jpg";
        this.path = path; //scalable to different devices
        this.hostname = hostname;
        this.port = port;
        /*
        Recall: thisPath = Environment.getExternalStorageDirectory().getAbsolutePath();

         */
        fullFilePath = this.path + "/Home_Baxter/";
        return;
    }

    Socket start_connection() throws UnknownHostException, IOException{
        return new Socket(hostname, port); //throws UHE and IOE exceptions;
    }

    void transmission(Socket mySoc) throws IOException {
        File file = new File("test_image.jpg");
        file.createNewFile(); //THROWS IOException

        DataInputStream din = new DataInputStream(mySoc.getInputStream());
        DataOutputStream dout = new DataOutputStream(new FileOutputStream(file));
        byte[] buffer = new byte[8192];
        int count = 0; //counts the number of bytes read

        while((count = din.read(buffer)) > 0)
        {
            dout.write(buffer, 0, count);
            dout.flush();
        }
        dout.close();
    }






}
