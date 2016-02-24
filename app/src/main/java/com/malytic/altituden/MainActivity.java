package com.malytic.altituden;

import android.Manifest;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.malytic.altituden.classes.PathData;
import com.malytic.altituden.fragments.FormFragment;
import com.malytic.altituden.fragments.GraphFragment;
import com.malytic.altituden.fragments.MapsFragment;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private MapsFragment mapsFragment;
    private GraphFragment graphFragment;
    private FormFragment formFragment;
    private boolean firstRun;
    private boolean permissionsGranted = false;
    public static PathData pathData;
    int weight;
    int age;
    int gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        weight = preferences.getInt("weight", -1);
        age = preferences.getInt("age", -1);
        gender = preferences.getInt("gender",-1);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            firstRun = true;
        }

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        pathData = new PathData();

        if(firstRun || (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED))
            showInformationDialog();

        initiateFragments();
    }

    /**
     * Checks if the app has the correct permissions
     * Otherwise ask for them
     */
    public void askPermission(){
        /*---------------------------------
        Ask permission to use GPS
        */

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {


            // Should we show an explanation?
            // if (ActivityCompat.shouldShowRequestPermissionRationale(
           //          this, Manifest.permission.ACCESS_FINE_LOCATION)) {


            // Show an expanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.

            //} else {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 5);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
            //}
        }
    }

    /**
     * Callback from permission ask
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 5: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    permissionsGranted = true;
                    // permission was granted, yay!

                } else {
                    // permission denied, boo!
                    showInformationDialog();
                }
                return;
            }
        }
    }

    /**
     * Shows a dialog about the permissions
     */
    public void showInformationDialog(){
        new AlertDialog.Builder(this)
                .setMessage(("You denied Altituden permission. Grant permission or quit app?" +
                        "(If you have selected deny always you will have to change this in you phones settings)"))
                .setCancelable(false)
                .setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        askPermission();
                    }
                })
                .setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                }).show();
    }

    /**
     * Creates new fragments and decides which
     * fragment to show as startscreen
     */
    public void initiateFragments(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        graphFragment = new GraphFragment();
        transaction.add(R.id.frame, graphFragment);
        transaction.hide(graphFragment);

        formFragment = new FormFragment();
        transaction.add(R.id.frame, formFragment);

        if(!firstRun){
            transaction.hide(formFragment);
            mapsFragment = new MapsFragment();
            transaction.add(R.id.frame, mapsFragment);
        }
        transaction.commit();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            new AlertDialog.Builder(this)
                    .setMessage("Are you sure you want to close Altituden?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    })
                    .setNegativeButton("No", null).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        int id = item.getItemId();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (id == R.id.nav_maps) {
            transaction.hide(graphFragment);
            transaction.hide(formFragment);

            if(firstRun){
                mapsFragment = new MapsFragment();
                transaction.add(R.id.frame, mapsFragment);
                firstRun = false;
            }else if (mapsFragment.isHidden()) {
                transaction.show(mapsFragment);
            }
        }

        else if (id == R.id.nav_graph) {
            transaction.hide(formFragment);
            if(mapsFragment != null)
                transaction.hide(mapsFragment);

            if(graphFragment.isHidden()){
                transaction.show(graphFragment);
                graphFragment.onResume();
            }
        }

        else if (id == R.id.nav_form) {
            transaction.hide(graphFragment);
            transaction.hide(mapsFragment);

            if(formFragment.isHidden()){
                transaction.show(formFragment);
            }
        }

        transaction.commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void fragmentClick(View view) {
        formFragment.onClick(view);
    }
}
