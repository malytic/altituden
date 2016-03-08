package com.malytic.altituden;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
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
import android.view.MenuItem;
import android.view.View;

import com.malytic.altituden.fragments.*;
import com.malytic.altituden.utils.PathData;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RouteFragment routeFragment;
    private TrackerFragment trackerFragment;
    private AltitudeFragment altitudeFragment;
    private ProfileFragment profileFragment;
    private boolean firstRun;

    public static PathData pathData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set firstRun true if permissions is not already granted
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

        //Show dialog about permissions if first time app runs
        if(firstRun)
            askPermission();

        initiateFragments();
    }

    /**
     * Checks if the app has the correct permissions
     * Otherwise ask for them
     * Code from:
     *      http://developer.android.com/training/permissions/requesting.html
     */
    public void askPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 5);
        }
    }

    /**
     * Callback from permission ask
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 5: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                } else {
                    // permission denied, boo!
                    //Show same informationdialog as if firstRun
                    showInformationDialog();
                }
            }
        }
    }

    /**
     * Shows a dialog about the permissions
     */
    public void showInformationDialog(){
        new AlertDialog.Builder(this)
                .setMessage(("You denied Altituden permission. You have to grant permission in order for Altituden to run.\n\n\n" +
                        "If you have selected deny always you will have to change this in you phones settings"))
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
     * Makes only formfragment display after location whitch of
     */
    @Override
    public void onResume(){
        super.onResume();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            altitudeFragment = new AltitudeFragment();
            profileFragment = new ProfileFragment();

            transaction.replace(R.id.frame, altitudeFragment);
            transaction.hide(altitudeFragment);
            transaction.add(R.id.frame, profileFragment);

            transaction.commit();
        }
    }

    /**
     * Creates new fragments and decides which
     * fragment to show as startscreen
     */
    public void initiateFragments(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        trackerFragment = new TrackerFragment();
        transaction.add(R.id.frame, trackerFragment);
        transaction.hide(trackerFragment);

        altitudeFragment = new AltitudeFragment();
        transaction.add(R.id.frame, altitudeFragment);
        transaction.hide(altitudeFragment);

        profileFragment = new ProfileFragment();
        transaction.add(R.id.frame, profileFragment);

        //If not first time app runs a map should be the first screen
        //Create map, set it as current fragment and hide profileFragment
        if(!firstRun){
            transaction.hide(profileFragment);
            routeFragment = new RouteFragment();
            transaction.add(R.id.frame, routeFragment);
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
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
*/
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
        int id = item.getItemId();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        //A fragment with the tag "crash" is present if the
        //map has crashed some time. Hide it if it exists
        if (getSupportFragmentManager().findFragmentByTag("crash") != null) {
            transaction.hide(getSupportFragmentManager().findFragmentByTag("crash"));
        }

        if (id == R.id.nav_route) {
            //Hide all other fragments
            transaction.hide(trackerFragment);
            transaction.hide(altitudeFragment);
            transaction.hide(profileFragment);

            if (firstRun) {
                routeFragment = new RouteFragment();
                transaction.add(R.id.frame, routeFragment);
                firstRun = false;
            } else if (routeFragment.isHidden()) {
                transaction.show(routeFragment);
            }
        }

        else if (id == R.id.nav_tracker) {
            //Hide all other fragments
            transaction.hide(altitudeFragment);
            transaction.hide(profileFragment);
            if (routeFragment != null) transaction.hide(routeFragment);

            if (trackerFragment.isHidden()) {
                transaction.show(trackerFragment);
            }
        }

        else if (id == R.id.nav_altitude) {
            //Hide all other fragments
            transaction.hide(trackerFragment);
            transaction.hide(profileFragment);
            if (routeFragment != null) transaction.hide(routeFragment);

            if (altitudeFragment.isHidden()) {
                transaction.show(altitudeFragment);
                altitudeFragment.onResume();
            }
        }

        else if (id == R.id.nav_profile) {
            //Hide all other fragments
            transaction.hide(trackerFragment);
            transaction.hide(altitudeFragment);
            if (routeFragment != null) transaction.hide(routeFragment);

            if (profileFragment.isHidden()) {
                transaction.show(profileFragment);
            }
        }

        transaction.commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void fragmentClick(View view) {
        profileFragment.onClick(view);
    }
}
