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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.File;
import java.io.IOException;
import java.util.logging.SocketHandler;


public class grasp_objectFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    Activity myActivity;
    Button btn_receive;
    Button btn_send; //Button equivalent to grab

    private String appPath;
    private String hostname;
    private int port;

    ImageView img;
    File baxter_image;

    int objectIndex;

    ListView listView_itemSelect;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        //set Context
        myActivity = getActivity();
        View view = inflater.inflate(R.layout.fragment_grasp_object, container, false);
        //image stuff
        img = (ImageView) view.findViewById(R.id.robotViewImageView);
        img.setImageResource(R.drawable.baxter_robot);

        //listView stuff
        listView_itemSelect = (ListView) view.findViewById(R.id.listView_itemSelect);
        listView_itemSelect.setOnItemClickListener(this);

        btn_receive = (Button) view.findViewById(R.id.btn_image_receive);
        btn_receive.setOnClickListener(this);

        btn_send = (Button) view.findViewById(R.id.btn_send_object);
        btn_send.setOnClickListener(this);

        appPath = Environment.getExternalStorageDirectory().getAbsolutePath();


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
            case R.id.btn_send_object:
                //send a string object via the socket handler

                new sendObjectResponse().execute();
                break;
        }
    }

    CheckedTextView mCheckedTextView = null;

    //for list view
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if(mCheckedTextView != null){
            //previously assigned, uncheck it
            mCheckedTextView.setChecked(!mCheckedTextView.isChecked());
        }

        mCheckedTextView = ((CheckedTextView) view);
        mCheckedTextView.setChecked(!mCheckedTextView.isChecked());
        objectIndex = position;
    }

    private class sendObjectResponse extends AsyncTask<String, Void, String>{

        socketHandler handle = new socketHandler(appPath, hostname, port);

        @Override
        protected String doInBackground(String... params) {
            try {
                handle.transmitObjectIndex(objectIndex);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class receiveImage extends AsyncTask<String, Void, String>{

        socketHandler handle = new socketHandler(appPath, hostname, port);
        String[] objectIndexes;

        @Override
        protected String doInBackground(String... params) {

            try {
                baxter_image = handle.receiveObjectsPayload(); //as opposed to socketReciever.receiveObjectsPayload -- static method
            } catch (IOException e) {
                e.printStackTrace();
            }
            //preparing elements for listview
            objectIndexes =  new String[handle.getNumObject_Payload()];

            for(int i = 0; i < handle.getNumObject_Payload(); ++i) objectIndexes[i] = "Object " + String.valueOf(i) ;

            return null;
        }

        @Override
        protected void onPostExecute(String result){
            //Set the image
            Bitmap bitmap = BitmapFactory.decodeFile(handle.getFullImagePath());
            img.setImageBitmap(bitmap);

            //Set the listview
            //result holds the number of objects (detected sent by the server)
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(myActivity,
                    android.R.layout.simple_list_item_single_choice, objectIndexes);
            //listView_itemSelect.setItemsCanFocus(true);

            listView_itemSelect.setAdapter(arrayAdapter);


        }
    }
}
