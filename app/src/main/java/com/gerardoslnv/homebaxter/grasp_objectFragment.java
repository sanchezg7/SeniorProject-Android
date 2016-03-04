package com.gerardoslnv.homebaxter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;


public class grasp_objectFragment extends Fragment implements View.OnClickListener{

    Activity myActivity;
    Button btn_receive;
    private String appPath;
    private String hostname;
    private int port;
    ImageView img;
    File baxter_image;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        //set Context
        myActivity = getActivity();
        View view = inflater.inflate(R.layout.fragment_grasp_object, container, false);
        img = (ImageView) view.findViewById(R.id.robotViewImageView);
        img.setImageResource(R.drawable.baxter_robot);

        btn_receive = (Button) view.findViewById(R.id.btn_image_receive);
        btn_receive.setOnClickListener(this);
        appPath = Environment.getExternalStorageDirectory().getAbsolutePath();

        //temporary implementation -- refer to bundle next time
        //Bundle bundle = this.getArguments();

        Bundle bundle = this.getArguments();
        hostname = bundle.getString(getResources().getString(R.string.key_ip_address));
        //hostname = "10.224.34.238";
        port = 5000;

        return view;
    }

    @Override
    public void onClick(View view){
        int id = view.getId();

        switch (id){
            case R.id.btn_image_receive:
                //start transaction
                //Toast.makeText(myActivity, "Btn_image_receive", Toast.LENGTH_SHORT).show();
                new receiveImage().execute();
                break;
        }
    }

    private class receiveImage extends AsyncTask<String, Void, String>{

        socketHandler handle = new socketHandler(appPath, hostname, port);

        @Override
        protected String doInBackground(String... params) {

            try {
                baxter_image = handle.dataTransaction(); //as opposed to socketReciever.dataTransaction -- static method
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result){
            //File image = new File(handle.getFullImagePath());
            Bitmap bitmap = BitmapFactory.decodeFile(handle.getFullImagePath());
            img.setImageBitmap(bitmap);
        }
    }
}
