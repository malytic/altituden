package com.malytic.altituden.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

/**
 * Bound service that provides location tracking in the background.
 */
public class TrackerService extends Service {

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    // Data given to clients
    private int mData = 1337;

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
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

    /**
     * Get all the data.
     * @return cool data
     */
    public int getServiceData() {
        return mData++;
    }
}
