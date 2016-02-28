package com.malytic.altituden.services;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Unbound service that provides location tracking in the background.
 */
public class TrackerService extends Service implements LocationListener {

    private static final String TAG = TrackerService.class.getSimpleName();
    private static final long FASTEST_UPDATE_INTERVAL = 2000;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(FASTEST_UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiHandler())
                .addOnConnectionFailedListener(new GoogleApiHandler())
                .addApi(LocationServices.API).build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        if (intent == null) {
            Log.d(TAG, "Service restarted");
        } else {
            // create new row in database here
        }
        mGoogleApiClient.connect();
        return START_STICKY;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged: " + location);
        // save new location in database
    }

    @Override
    public void onDestroy(){
        Log.d(TAG, "onDestroy");
        if (mGoogleApiClient.isConnected()) {
            cancelLocationUpdates();
            mGoogleApiClient.disconnect();
        }
        super.onDestroy();
    }

    /**
     * Start location tracking, restart if tracking is already running.
     */
    public static void start(Context context) {
        context.startService(new Intent(context, TrackerService.class));
    }

    /**
     * Stop location tracking.
     */
    public static void stop(Context context) {
        context.stopService(new Intent(context, TrackerService.class));
    }

    /**
     * Check if location tracking is running.
     * @return true if running, false if not
     */
    public static boolean isRunning(Context context) {
        ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (TrackerService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void requestLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        Log.d(TAG, "Location updates requested");
    }

    private void cancelLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        Log.d(TAG, "Location updates cancelled");
    }

    // Defines connection callbacks for the GoogleApiClient
    private class GoogleApiHandler implements ConnectionCallbacks, OnConnectionFailedListener {

        @Override
        public void onConnected(Bundle bundle) {
            requestLocationUpdates();
        }

        @Override
        public void onConnectionSuspended(int i) {
            Log.w(TAG, "GoogleApiClient connection suspended: "+ i);
        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            if (connectionResult.hasResolution()) {
                try {
                    // Start an Activity that tries to resolve the error
                    connectionResult.startResolutionForResult(null, 9000);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                    stopSelf();
                }
            } else {
                Log.e(TAG, "GoogleApiClient connection failed: "+ connectionResult.getErrorCode());
                stopSelf();
            }
        }
    }
}
