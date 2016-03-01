package com.gerardoslnv.homebaxter;

import android.app.Activity;
import android.content.Context;
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
public class hello_Fragment extends Fragment implements View.OnClickListener {

    Activity myActivity;
    EditText ipAddress_ET; //edit text
    String ipAddress;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        myActivity = getActivity();
        view = inflater.inflate(R.layout.fragment_hello_, container, false);

        //Add ipAddress Edit Text
        ipAddress_ET = (EditText) view.findViewById(R.id.ipAddress_ET);
//        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
//        fab.setOnClickListener(this);
        Button btn_stringReceive = (Button) view.findViewById(R.id.btn_string_recieve);
        btn_stringReceive.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.btn_string_recieve:
                ipAddress = ipAddress_ET.getText().toString();
                Snackbar.make(view, "Socket, ipAddress: " + ipAddress, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                Toast.makeText(myActivity, ipAddress, Toast.LENGTH_SHORT).show();
                new socketComm().execute(ipAddress); //GS Added
                break;
        }
    }

    //Socket Reception Task **********
    private class socketComm extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            Socket s;
            String payLoad = null;
            String hostname = params[0]; //ipAddress;

            try{
                s = new Socket(hostname, 1024);
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
            TextView txt = (TextView) view.findViewById(R.id.payLoad);
            txt.setText(result);
        }
    }


}
