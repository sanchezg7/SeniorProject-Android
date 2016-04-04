package com.gerardoslnv.homebaxter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener{

    String ipAddress;
    FragmentManager fragManager;
    SharedPreferences.Editor mSPEditor;
    SharedPreferences mSp;

    public Fragment currentFrag = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //fab.setOnClickListener(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //GS Added
        fragManager = getFragmentManager();
        currentFrag = new hello_Fragment();
        fragManager.beginTransaction().replace(R.id.main_content, currentFrag).commit();
        fragManager.executePendingTransactions();

        //to recall the ip address
        mSp = getSharedPreferences(getResources().getString(R.string.mainActivity_SP), MODE_PRIVATE);
    }


    @Override
    public void onClick(View view){

        return;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.action_about:
                openAboutDialog();
            default:
                return super.onOptionsItemSelected(item);

        }


    }

    void openAboutDialog(){
        about_Fragment mAboutFragment = new about_Fragment();
        mAboutFragment.show(getSupportFragmentManager().beginTransaction(), "about_menu");
    }

    public <nxt> boolean switchFragments(nxt nextFrag, int containerId)
    {
        FragmentManager mFragmentManager = this.getFragmentManager();
        FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
        Fragment mFragment = null;
        //currentFrag = (Fragment) nextFrag;
        this.setCurrentFrag((Fragment) nextFrag);

        mFragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        mFragmentTransaction.replace(containerId, currentFrag, "switched Fragments Function");
        finishTransaction(mFragmentTransaction, containerId);
        return true;
    }

    public void setCurrentFrag(Fragment fragment){
        this.currentFrag = fragment;
        return;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        int containerId = R.id.main_content;

        FragmentTransaction fragmentTransaction = fragManager.beginTransaction();
        Fragment myFragment = null;

        switch (id){
            case R.id.nav_home:
                if(!(currentFrag instanceof hello_Fragment)){
                    myFragment = new hello_Fragment();
                    switchFragments(myFragment, containerId);
                    break;
                }
                break;
            case R.id.nav_grasp_object:
                if(currentFrag instanceof hello_Fragment)
                {
                    myFragment = new graspObject_Fragment();

                    //save the ip address from the hello screen and MOVE ON
                    ipAddress = ((EditText) currentFrag.getView().findViewById(R.id.ipAddress_ET)).getText().toString();

                    //Save SharedPreferences for ip address
                    mSPEditor = mSp.edit(); //returns an instance of SharedPreferences.Editor object
                    mSPEditor.putString(this.getResources().getString(R.string.key_ip_address), ipAddress);
                    mSPEditor.commit();     //finalize the changes

                    Bundle bundle = new Bundle();
                    bundle.putString(getResources().getString(R.string.key_ip_address), ipAddress); //pass ip address to other fragments

                    myFragment.setArguments(bundle);
                    switchFragments(myFragment, containerId);
                }
                break;
            default:
                Toast.makeText(MainActivity.this, "Error Selecting Menu Item", Toast.LENGTH_SHORT).show();
                break;

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    private void finishTransaction(FragmentTransaction fragmentTransaction, int containerId) {
        fragmentTransaction.commit();
        fragManager.executePendingTransactions();

        return;
    }
}
