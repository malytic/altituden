package com.malytic.altituden.services;

import android.app.Service;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Binder;
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
 * Bound service that provides location tracking in the background.
 */
public class TrackerService extends Service implements LocationListener {

    private static final String TAG = TrackerService.class.getSimpleName();
    private static final long UPDATE_INTERVAL = 2000; // Minimum time between location updates
    private final IBinder mBinder = new LocalBinder(); // Binder given to clients of the service

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private boolean mIsTracking = false;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(UPDATE_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiHandler())
                .addOnConnectionFailedListener(new GoogleApiHandler())
                .addApi(LocationServices.API).build();
        if (mIsTracking) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return mBinder;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged: " + location);
        // save in database
    }

    @Override
    public void onDestroy(){
        Log.d(TAG, "onDestroy");
        stopTracking();
        super.onDestroy();
    }

    /**
     * Check if location tracking is activated.
     * @return true if activated, false if not
     */
    public boolean isTracking() {
        return mIsTracking;
    }

    /**
     * Start location tracking.
     */
    public void startTracking() {
        Log.d(TAG, "startTracking called");
        mIsTracking = true;
        if (mGoogleApiClient.isConnected()) {
            requestLocationUpdates();
            // create new row in database
        } else {
            mGoogleApiClient.connect();
        }
    }

    /**
     * Stop location tracking.
     */
    public void stopTracking() {
        Log.d(TAG, "stopTracking called");
        mIsTracking = false;
        if (mGoogleApiClient.isConnected()) {
            cancelLocationUpdates();
            mGoogleApiClient.disconnect();
        }
    }

    private void requestLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        Log.d(TAG, "Location updates requested");
    }

    private void cancelLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        Log.d(TAG, "Location updates cancelled");
    }

    /**
     * Class used for the client Binder.
     */
    public class LocalBinder extends Binder {

        /**
         * Get this instance of TrackerService so
         * that clients can call public methods.
         * @return this instance of TrackerService
         */
        public TrackerService getService() {
            return TrackerService.this;
        }
    }

    // Defines connection callbacks for the GoogleApiClient
    private class GoogleApiHandler implements ConnectionCallbacks, OnConnectionFailedListener {

        @Override
        public void onConnected(Bundle bundle) {
            Log.d(TAG, "GoogleApiClient connected");
            if (mIsTracking) {
                requestLocationUpdates();
            }
        }

        @Override
        public void onConnectionSuspended(int i) {
            Log.d(TAG, "GoogleApiClient connection suspended: "+ i);
        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            mIsTracking = false;
            if (connectionResult.hasResolution()) {
                try {
                    // Start an Activity that tries to resolve the error
                    connectionResult.startResolutionForResult(null, 9000);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e(TAG, "GoogleApiClient connection failed: "+ connectionResult.getErrorCode());
            }
        }
    }
}
