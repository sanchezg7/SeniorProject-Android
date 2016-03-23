package com.gerardoslnv.homebaxter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.zip.Inflater;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link hello_Fragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link hello_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class hello_Fragment extends Fragment {

    Activity myActivity;
    EditText ipAddress_ET; //edit text
    String ipAddress;
    View view;
    SharedPreferences mSp;


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


        //SharedPreferences for ip address
        //retrieve the previous ip if it exist
        mSp = myActivity.getSharedPreferences(getResources().getString(R.string.mainActivity_SP), Context.MODE_PRIVATE);
        String prevIP = mSp.getString(myActivity.getResources().getString(R.string.key_ip_address), ""); //return empty string if not found
        ipAddress_ET.setText(prevIP);
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
