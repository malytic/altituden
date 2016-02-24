package com.malytic.altituden;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

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
    public static PathData pathData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        /*---------------------------------
        Ask permission to use GPS
        */
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 5);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        pathData = new PathData();
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

        if (id == R.id.nav_blank) {
            Fragment tmp = getSupportFragmentManager().findFragmentById(R.id.frame);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().addToBackStack("Graph");

            if(tmp != null)
                if(tmp != graphFragment)
                    transaction.hide(tmp);

            if(graphFragment == null) {
                graphFragment = new GraphFragment();
                transaction.add(R.id.frame, graphFragment);
            }else if(graphFragment.isHidden()){
                transaction.show(graphFragment);
                graphFragment.onResume();
            }
            transaction.commit();
        }

        if (id == R.id.nav_slideshow) {
            Fragment tmp = getSupportFragmentManager().findFragmentById(R.id.frame);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().addToBackStack("Map");

            if(tmp != null)
                if(tmp != mapsFragment)
                    transaction.hide(tmp);

            if(mapsFragment == null) {
                mapsFragment = new MapsFragment();
                transaction.add(R.id.frame, mapsFragment);
            }else if(mapsFragment.isHidden()){
                transaction.show(mapsFragment);
            }
            transaction.commit();
        }
        if (id == R.id.nav_form) {
            Fragment tmp = getSupportFragmentManager().findFragmentById(R.id.frame);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().addToBackStack("Form");

            if(tmp != null)
                if(tmp != formFragment)
                    transaction.hide(tmp);

            if(formFragment == null) {
                formFragment = new FormFragment();
                transaction.add(R.id.frame, formFragment);
            }else if(formFragment.isHidden()){
                transaction.show(formFragment);
            }

            transaction.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
