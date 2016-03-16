package com.gerardoslnv.homebaxter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;



public class grasp_objectFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {

    private SwipeRefreshLayout mSwipeRefresh;


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

        //Swipe Refresh Interface
        mSwipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer_fragment_graspObject);
        mSwipeRefresh.setOnRefreshListener(this);

        mSwipeRefresh.setColorSchemeResources(R.color.blue, R.color.green, R.color.orange, R.color.red);

//        btn_receive = (Button) view.findViewById(R.id.btn_image_receive);
//        btn_receive.setOnClickListener(this);

        appPath = Environment.getExternalStorageDirectory().getAbsolutePath();

        Bundle bundle = this.getArguments();
        hostname = bundle.getString(getResources().getString(R.string.key_ip_address));
        port = 5000;

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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

    CheckedTextView mCheckedTextView = null;



    //listens to list view elements
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if(mCheckedTextView != null)
        {
            //uncheck the old object
            mCheckedTextView.setChecked(false);
        }

        //set the new item checked and make the user confirm their selection with through the snackbar
        mCheckedTextView = ((CheckedTextView) view);
        mCheckedTextView.setChecked(true);
        objectIndex = position;
        Snackbar.make(myActivity.findViewById(R.id.frg_grasp_object), "Object " + position + " selected", Snackbar.LENGTH_LONG)
                .setAction("Confirm selection", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new sendObjectResponse().execute();
                    }
                }).setActionTextColor(myActivity.getResources().getColor(R.color.colorConfirm)).show();
    }

    @Override
    public void onRefresh() {
        Toast.makeText(myActivity, "Refresh requested", Toast.LENGTH_SHORT).show();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            mSwipeRefresh.setEnabled(false);
        }
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

//            try {
//                baxter_image = handle.receiveObjectsPayload(); //as opposed to socketReciever.receiveObjectsPayload -- static method
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            //preparing elements for listview

            objectIndexes =  new String[3]; //handle.getNumObject_Payload()];
//            for(int i = 0; i < handle.getNumObject_Payload(); ++i) objectIndexes[i] = "Object " + String.valueOf(i) ;
            for(int i = 0; i < 3; ++i) objectIndexes[i] = "Object " + String.valueOf(i) ;

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
