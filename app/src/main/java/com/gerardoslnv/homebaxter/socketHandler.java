package com.gerardoslnv.homebaxter;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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

    public void transmitObjectIndex(int objectIndex) throws IOException{

        Socket stringSocket = startConnection(hostname, port+2);

        return;
    }

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


    //should only be run separate from the UI thread

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
