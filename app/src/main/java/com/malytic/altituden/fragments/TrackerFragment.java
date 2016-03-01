package com.malytic.altituden.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.malytic.altituden.R;
import com.malytic.altituden.events.TrackerEvent;
import com.malytic.altituden.services.TrackerService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class TrackerFragment extends Fragment {

    private Button startStopButton;
    private TextView trackerStatus;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        View view = inflater.inflate(R.layout.fragment_tracker, container, false);

        startStopButton = (Button)view.findViewById(R.id.tracker_start_stop_button);
        trackerStatus = (TextView)view.findViewById(R.id.tracker_status_field);

        startStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TrackerService.isRunning(getContext())) {
                    TrackerService.stop(getContext());
                } else {
                    TrackerService.start(getContext());
                }
                setTrackingWaiting();
            }
        });
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            EventBus.getDefault().unregister(this);
        } else {
            EventBus.getDefault().register(this);
            if (TrackerService.isRunning(getContext())) {
                setTrackingActive();
            } else {
                setTrackingInactive();
            }
        }
    }

    @Subscribe
    public void onTrackerEvent(TrackerEvent event){
        switch (event.status) {
            case TrackerEvent.STARTED:
                setTrackingActive();
                break;
            case TrackerEvent.STOPPED:
                setTrackingInactive();
                break;
        }
    }

    private void setTrackingActive() {
        startStopButton.setEnabled(true);
        startStopButton.setText(R.string.tracker_button_stop);
        trackerStatus.setText(R.string.tracker_status_active);
    }

    private void setTrackingInactive() {
        startStopButton.setEnabled(true);
        startStopButton.setText(R.string.tracker_button_start);
        trackerStatus.setText(R.string.tracker_status_inactive);
    }

    private void setTrackingWaiting() {
        startStopButton.setEnabled(false);
        trackerStatus.setText(R.string.tracker_status_waiting);
    }
}
