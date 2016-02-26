package com.gerardoslnv.homebaxter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


public class grasp_objectFragment extends Fragment implements View.OnClickListener{

    Activity myActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        //set Context
        myActivity = getActivity();
        View view = inflater.inflate(R.layout.fragment_grasp_object, container, false);
        ImageView img = (ImageView) view.findViewById(R.id.robotViewImageView);
        img.setImageResource(R.drawable.baxter_robot);


        return view;
    }

    @Override
    public void onClick(View view){

    }
}
