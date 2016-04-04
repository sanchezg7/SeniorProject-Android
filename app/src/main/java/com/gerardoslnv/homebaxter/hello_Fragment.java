package com.gerardoslnv.homebaxter;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;


public class hello_Fragment extends Fragment implements View.OnClickListener, TextWatcher{

    Activity myActivity;
    EditText ipAddress_ET; //edit text
    String ipAddress;
    View view;
    SharedPreferences mSp;
    Button btn_actionGraspObject;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        myActivity = getActivity();
        view = inflater.inflate(R.layout.fragment_hello_, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        //Add ipAddress Edit Text
        ipAddress_ET = (EditText) view.findViewById(R.id.ipAddress_ET);
        ipAddress_ET.addTextChangedListener(this); //TextWatcher here
        btn_actionGraspObject = (Button) view.findViewById(R.id.btn_actionGraspObject);
        btn_actionGraspObject.setEnabled(false);
        btn_actionGraspObject.setOnClickListener(this);

        //SharedPreferences for ip address
        //retrieve the previous ip if it exist
        mSp = myActivity.getSharedPreferences(getResources().getString(R.string.mainActivity_SP), Context.MODE_PRIVATE);
        String prevIP = mSp.getString(myActivity.getResources().getString(R.string.key_ip_address), ""); //return empty string if not found
        ipAddress_ET.setText(prevIP);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();



        switch (id){
            case R.id.btn_actionGraspObject:
                if (myActivity instanceof MainActivity)
                {
                    graspObject_Fragment graspObject_fragment = new graspObject_Fragment();
                    ipAddress = ipAddress_ET.getText().toString();

                    Bundle bundle = new Bundle();
                    bundle.putString(getResources().getString(R.string.key_ip_address), ipAddress); //pass ip address to other fragments
                    graspObject_fragment.setArguments(bundle);
                    ((MainActivity) myActivity).switchFragments(graspObject_fragment, R.id.main_content);
                }
                break;
        }
    }

    //TEXT WATCHER INTERFACE
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        //Will be called after ANY text has been input into the field
        if(s.length() > 2)
        {
            btn_actionGraspObject.setEnabled(true);
        }else{
            btn_actionGraspObject.setEnabled(false);
        }
    }

    //Socket Reception Task for a string **********
    private class socketComm extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            Socket s;
            String payLoad = null;
            String hostname = params[0]; //ipAddress;

            try{
                s = new Socket(hostname, 5000);
                BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
                payLoad = input.readLine();

            } catch (UnknownHostException e){
                e.printStackTrace();
            } catch (IOException e){
                e.printStackTrace();
            }
            return payLoad;
        }

        @Override
        protected void onPostExecute(String result){
//            TextView txt = (TextView) view.findViewById(R.id.payLoad);
//            txt.setText(result);
        }
    }


}
