package com.gerardoslnv.homebaxter;

import android.os.AsyncTask;
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
public class socketHandler {

    private String recvImagePathName = null;
    private String path = null;
    private String fullFilePath = null; //consider moving this to value/strings.xml

    private String hostname = null;
    private int port;

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


    public File dataTransaction() throws IOException {
        File file;

        file = prepareFile(); //prepare file, then recieve
        Socket socket = startConnection();
        file = receiveData(socket, file);

        return file;
    }

    private Socket startConnection() throws UnknownHostException, IOException{
        return new Socket(hostname, port); //throws UHE and IOE exceptions;
    }

    private File prepareFile() throws IOException {
        File file = new File(fullFilePath + recvImagePathName);
        File parentPath = new File(fullFilePath);
        parentPath.mkdirs(); //assures the paths exist for writing
        //file.createNewFile(); //THROWS IOException

        return file;
    }

    //consider making this an asynchtask
    private File receiveData(Socket mySoc, File file) throws IOException {

        DataInputStream din = new DataInputStream(mySoc.getInputStream());
        DataOutputStream dout = new DataOutputStream(new FileOutputStream(file));
        byte[] buffer = new byte[8192];
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





//    //Socket Reception Task **********
//    private class socketComm extends AsyncTask<String, Void, String> {
//
//        @Override
//        protected String doInBackground(String... params) {
//            Socket s;
//            String payLoad = null;
//            String hostname = params[0]; //ipAddress;
//
//            try{
//                s = new Socket(hostname, 1024);
//                BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
//                payLoad = input.readLine();
//
//            } catch (UnknownHostException e){
//                e.printStackTrace();
//            } catch (IOException e){
//                e.printStackTrace();
//            }
//
//            return payLoad;
//        }
//
//        @Override
//        protected void onPostExecute(String result){
//            TextView txt = (TextView) view.findViewById(R.id.payLoad);
//            txt.setText(result);
//        }
//    }






}
